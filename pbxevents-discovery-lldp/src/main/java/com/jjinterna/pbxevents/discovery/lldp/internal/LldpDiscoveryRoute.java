package com.jjinterna.pbxevents.discovery.lldp.internal;

import java.io.IOException;
import java.util.List;

import org.apache.camel.builder.RouteBuilder;
import org.snmp4j.CommunityTarget;
import org.snmp4j.Snmp;
import org.snmp4j.TransportMapping;
import org.snmp4j.smi.Address;
import org.snmp4j.smi.GenericAddress;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.transport.DefaultUdpTransportMapping;
import org.snmp4j.util.DefaultPDUFactory;
import org.snmp4j.util.TreeEvent;
import org.snmp4j.util.TreeUtils;

import com.jjinterna.pbxevents.model.Phone;

public class LldpDiscoveryRoute extends RouteBuilder {

	private Integer port;
	private String snmpCommunity;
	private Integer snmpVersion;
	private Integer retries;
	private Integer timeout;

	public static OID lldpRemEntry = new OID("1.0.8802.1.1.2.1.4.1.1");
	public static OID lldpRemChassisId = new OID("1.0.8802.1.1.2.1.4.1.1.5");
	public static OID lldpRemSysName = new OID("1.0.8802.1.1.2.1.4.1.1.9");
	public static OID lldpRemSysDesc = new OID("1.0.8802.1.1.2.1.4.1.1.10");
	public static OID lldpRemSysCapSupported = new OID("1.0.8802.1.1.2.1.4.1.1.11");
	public static OID lldpRemSysCapEnabled = new OID("1.0.8802.1.1.2.1.4.1.1.12");
	
	public static int capRepeater = 2, capBridge = 4, capWlanAccessPoint = 8, capRouter = 16,
			capTelephone = 32, capDocsisCableDevice = 64, capStationOnly = 128;

	@Override
	public void configure() throws Exception {

		from("direct:start")
			.bean(this, "discover")
			.setHeader("PBXEvent", constant("Phone"))
			.choice().when(body().isNotNull()).to("direct:publish").stop();
	}

	public Phone discover(String host) throws IOException {
		Address targetAddress = GenericAddress.parse("udp:" + host + "/" + port);
		TransportMapping transport = new DefaultUdpTransportMapping();
		Snmp snmp = new Snmp(transport);
		transport.listen();

		// setting up target
		CommunityTarget target = new CommunityTarget();
		target.setCommunity(new OctetString(snmpCommunity));
		target.setAddress(targetAddress);
		target.setRetries(retries);
		target.setTimeout(timeout);
		target.setVersion(snmpVersion);

		Phone phone = new Phone();
		phone.setAgentAddress(host);
		boolean isTelephone = false;		
		TreeUtils treeUtils = new TreeUtils(snmp, new DefaultPDUFactory());
		List<TreeEvent> events = treeUtils.getSubtree(target, lldpRemEntry);
		if (events == null || events.size() == 0) {
			log.info("No result returned.");
		} else {
			// Get snmpwalk result.
			for (TreeEvent event : events) {
				if (event == null) {
					continue;
				}
				if (event.isError()) {
					log.error(host + ": " + event.getErrorMessage());
				} else {
					VariableBinding[] varBindings = event.getVariableBindings();					
					if (varBindings != null && varBindings.length > 0) {
						for (VariableBinding varBinding : varBindings) {
							OID oid = varBinding.getOid();
							log.debug(host + ": " + oid.toDottedString() + " = " + varBinding.getVariable().toString());
							if (varBinding.getOid().startsWith(lldpRemChassisId)) {
								String value = varBinding.getVariable().toString().substring(3);
								OctetString os = OctetString.fromHexString(value);
								Address snmp4jIpAddress = new org.snmp4j.smi.IpAddress(os.getValue());
								phone.setPhoneAddress(snmp4jIpAddress.toString());
							} else if (oid.startsWith(lldpRemSysDesc)) {
								phone.setSysDesc(varBinding.toValueString());								
							} else if (oid.startsWith(lldpRemSysName)) {
								phone.setSysName(varBinding.toValueString());
							} else if (oid.startsWith(lldpRemSysCapEnabled)) {
								int caps = varBinding.toValueString().charAt(0);
								if ((caps & capTelephone) > 0) {
									isTelephone = true;
								}
							}
						}
					}
				}
			}
		}

		snmp.close();
		return isTelephone ? phone : null;
	}
}
