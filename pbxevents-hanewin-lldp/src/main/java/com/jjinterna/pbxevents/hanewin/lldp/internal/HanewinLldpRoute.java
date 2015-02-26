package com.jjinterna.pbxevents.hanewin.lldp.internal;

import java.io.IOException;
import java.net.InetAddress;
import java.util.List;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

import com.jjinterna.pbxevents.model.PhoneDetected;

public class HanewinLldpRoute extends RouteBuilder {

	private String camelContextId;
	private String oidStr;
	private String community;
	private Integer snmpVersion;
	private String port;
	private Long timeout;
	private Integer retries;

	private static final Logger log = LoggerFactory
			.getLogger(HanewinLldpRoute.class);

	@Override
	public void configure() throws Exception {
		fromF("activemq:queue:%s?username=karaf&password=karaf", camelContextId)
				.process(new Processor() {
					@Override
					public void process(Exchange exchange) throws Exception {
						String stationAddress = (String) exchange.getIn()
								.getBody();
						detect(stationAddress);
					}
				});
	}

	public PhoneDetected detect(String stationAddress) throws IOException {
		Address targetAddress = GenericAddress.parse("udp:" + stationAddress
				+ "/" + port);
		TransportMapping transport = new DefaultUdpTransportMapping();
		Snmp snmp = new Snmp(transport);
		transport.listen();

		// setting up target
		CommunityTarget target = new CommunityTarget();
		target.setCommunity(new OctetString(community));
		target.setAddress(targetAddress);
		target.setRetries(retries);
		target.setTimeout(timeout);
		target.setVersion(snmpVersion);

		OID oid = new OID(oidStr);

		TreeUtils treeUtils = new TreeUtils(snmp, new DefaultPDUFactory());
		List<TreeEvent> events = treeUtils.getSubtree(target, oid);
		if (events == null || events.size() == 0) {
			log.info("No result returned.");
		} else {
			// Get snmpwalk result.
			for (TreeEvent event : events) {
				if (event == null) {
					continue;
				}
				if (event.isError()) {
					log.error(stationAddress + ": " + event.getErrorMessage());
				} else {
					VariableBinding[] varBindings = event.getVariableBindings();
					if (varBindings != null && varBindings.length > 0) {
						for (VariableBinding varBinding : varBindings) {
							String field = varBinding.getOid().toDottedString();
							log.info(field + " = " + varBinding.getVariable().toString());
							if (field.equals(oidStr + ".5.628900.1.1")) {
								String value = varBinding.getVariable().toString().substring(3);
								OctetString os = OctetString.fromHexString(value);
								org.snmp4j.smi.Address snmp4jIpAddress = new org.snmp4j.smi.IpAddress(os.getValue());
								log.info(snmp4jIpAddress.toString());
							}
						}
					}
				}
			}
		}

		snmp.close();
		return null;
	}

}
