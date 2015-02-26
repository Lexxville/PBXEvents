package com.jjinterna.pbxevents.hanewin.lldp.internal;

import java.io.IOException;
import java.util.List;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
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

import com.jjinterna.pbxevents.model.PhoneDetected;

public class HanewinLldpRoute extends RouteBuilder {

	private String camelContextId;
	private String oidStr;
	private String community;
	private Integer snmpVersion;
	private String port;
	private Long timeout;
	private Integer retries;

	@Override
	public void configure() throws Exception {
		fromF("activemq:queue:%s?username=karaf&password=karaf", camelContextId)
		.process(new Processor() {
			@Override
			public void process(Exchange exchange) throws Exception {
				String stationAddress = (String) exchange.getIn().getBody();
				detect(stationAddress);
			}
		});
	}

	public PhoneDetected detect(String stationAddress) throws IOException {
		Address targetAddress = GenericAddress.parse("udp:"+ stationAddress + "/" + port);
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
	    if(events == null || events.size() == 0){
	      System.out.println("No result returned.");
	    } else {
	    	// Get snmpwalk result.
	    	for (TreeEvent event : events) {
	    		if(event != null){
	    			if (event.isError()) {
	    				System.err.println(timeout);
	    				System.err.println("oid [" + oid + "] " + event.getErrorMessage());
	    			}

	    			VariableBinding[] varBindings = event.getVariableBindings();
	    			if(varBindings == null || varBindings.length == 0){
	    				System.out.println("No result returned.");
	    			} else {
	    			for (VariableBinding varBinding : varBindings) {
	    				System.out.println(varBinding.getVariable().toString());
	    			}
	    			}
	    		}
	    	}	    
	    }

	    snmp.close();
	    return null;
	}
	
}

