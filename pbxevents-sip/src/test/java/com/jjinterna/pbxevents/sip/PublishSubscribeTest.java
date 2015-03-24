package com.jjinterna.pbxevents.sip;

import javax.sip.SipFactory;
import javax.sip.address.AddressFactory;
import javax.sip.address.SipURI;
import javax.sip.header.EventHeader;
import javax.sip.header.FromHeader;
import javax.sip.header.HeaderFactory;
import javax.sip.header.ToHeader;
import javax.sip.message.Request;

import org.apache.camel.EndpointInject;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.impl.JndiRegistry;
import org.apache.camel.impl.PropertyPlaceholderDelegateRegistry;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

public class PublishSubscribeTest extends CamelTestSupport {
    
    @EndpointInject(uri = "mock:neverland")
    protected MockEndpoint unreachableEndpoint;

    @EndpointInject(uri = "mock:notification")
    protected MockEndpoint resultEndpoint;

    @Produce(uri = "direct:start")
    protected ProducerTemplate producerTemplate;
    
    @Test
    public void testPresenceAgentBasedPubSub() throws Exception {
        unreachableEndpoint.expectedMessageCount(0);
        resultEndpoint.expectedMinimumMessageCount(1);
        
        producerTemplate.sendBodyAndHeader(
            "sip://agent@localhost:5252?stackName=client&eventHeaderName=evtHdrName&eventId=evtid&fromUser=user2&fromHost=localhost&fromPort=3534", 
            "EVENT_A",
            "REQUEST_METHOD", Request.PUBLISH);         

        assertMockEndpointsSatisfied();
    }
    
    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {  
                // Create PresenceAgent
                SipFactory sipFactory = SipFactory.getInstance();            	            	
                
                JndiRegistry registry = (JndiRegistry) ((PropertyPlaceholderDelegateRegistry)context.getRegistry()).getRegistry();

				AddressFactory addressFactory = sipFactory.createAddressFactory();
				HeaderFactory headerFactory = sipFactory.createHeaderFactory();

				SipURI toAddress = addressFactory.createSipURI("100", "192.168.122.185");
            	toAddress.setPort(5060);
            	ToHeader to = headerFactory.createToHeader(addressFactory.createAddress(toAddress), null);

            	SipURI fromAddress = addressFactory.createSipURI("100", "192.168.122.1");
            	fromAddress.setPort(5060);
            	FromHeader from = headerFactory.createFromHeader(addressFactory.createAddress(fromAddress), null);
            	
            	EventHeader eventHeader = headerFactory.createEventHeader("presence");

            	//SimpleRegistry registry = new SimpleRegistry();            	
            	registry.bind("myTo", to);
            	registry.bind("myFrom", from);            	

                from("sip://90001@192.168.122.1:5060?transport=udp&fromHeader=#myFrom&toUser=100&toHost=192.168.122.185&toPort=5060&stackName=Subscriber&eventHeaderName=presence&eventId=evtid&contentType=application&contentSubType=uri&automaticDialogSupport=off")
                   .to("log:ReceivedEvent")
                   .to("mock:notification");
            }
        };
    }

} 
