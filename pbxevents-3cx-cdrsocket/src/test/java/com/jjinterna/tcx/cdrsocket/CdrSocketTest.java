package com.jjinterna.tcx.cdrsocket;

import java.util.List;

import org.apache.camel.builder.AdviceWithRouteBuilder;
import org.apache.camel.component.mock.MockComponent;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.model.ModelCamelContext;
import org.apache.camel.model.RouteDefinition;
import org.apache.camel.scr.internal.ScrHelper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.jjinterna.pbxevents.tcx.cdrsocket.CdrSocket;

@RunWith(JUnit4.class)
public class CdrSocketTest {

	CdrSocket integration;
    ModelCamelContext context;
    
	@Before
	public void setUp() throws Exception {

		// Set property prefix for unit testing
		System.setProperty(CdrSocket.PROPERTY_PREFIX, "unit");

		// Prepare the integration
		integration = new CdrSocket();
		integration.prepare(null, ScrHelper.getScrProperties(integration.getClass().getName()));
		context = integration.getContext();

		// Disable JMX for test
		context.disableJMX();

		// Fake a component for test
		context.addComponent("amq", new MockComponent());

	}

	@After
	public void tearDown() throws Exception {
		integration.stop();
	}

    @Test
    public void testRoutes() throws Exception {
        // Adjust routes
        List<RouteDefinition> routes = context.getRouteDefinitions();
 
        routes.get(0).adviceWith(context, new AdviceWithRouteBuilder() {
            @Override
            public void configure() throws Exception {
                // Replace "from" endpoint with direct:start
                replaceFromWith("direct:start");
                // Mock and skip result endpoint
                mockEndpoints("direct:publish");
            }
        });
 
        MockEndpoint resultEndpoint = context.getEndpoint("mock:direct:publish", MockEndpoint.class);
        // resultEndpoint.expectedMessageCount(1); // If you want to just check the number of messages
        resultEndpoint.expectedBodiesReceived("hello"); // If you want to check the contents
 
        // Start the integration
        integration.run();
 
        // Send the test message
        context.createProducerTemplate().sendBody("direct:start", getClass().getResource("/l_001.txt"));
 
        resultEndpoint.assertIsSatisfied();
    }
}
