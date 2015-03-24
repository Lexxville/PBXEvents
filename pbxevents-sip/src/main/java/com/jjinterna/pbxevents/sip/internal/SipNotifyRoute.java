package com.jjinterna.pbxevents.sip.internal;

import javax.sip.SipFactory;
import javax.sip.address.AddressFactory;
import javax.sip.address.SipURI;
import javax.sip.header.HeaderFactory;
import javax.sip.header.ToHeader;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.SimpleRegistry;

public class SipNotifyRoute extends RouteBuilder {

	private String fromUser;
	private String fromHost;
	private Integer fromPort;

	private String toUser;
	private String toHost;
	private Integer toPort;

	private SimpleRegistry registry;
	
	public SipNotifyRoute(SimpleRegistry registry) {
		this.registry = registry;
	}

	@Override
	public void configure() throws Exception {
        SipFactory sipFactory = SipFactory.getInstance();            	            	
		AddressFactory addressFactory = sipFactory.createAddressFactory();
		HeaderFactory headerFactory = sipFactory.createHeaderFactory();

		SipURI toAddress = addressFactory.createSipURI("100", "192.168.122.185");
    	toAddress.setPort(5060);

    	ToHeader to = headerFactory.createToHeader(addressFactory.createAddress(toAddress), null);
    	registry.put("myto", to);

        from("sip://90001@192.168.122.1:5060?transport=udp&toHeader=#myto&stackName=Subscriber&eventHeaderName=presence&eventId=evtid&contentType=application&contentSubType=uri&automaticDialogSupport=off")
            .to("log:ReceivedEvent");		
		
//		from("sip:{{fromUser}}@{{fromHost}}:{{fromPort}}?toUser={{toUser}}&toHost={{toHost}}&toPort={{toPort}}&eventHeaderName=evtHdrName&eventId=evtid")
//		.to("log:sip?showHeaders=true");

	}

}
