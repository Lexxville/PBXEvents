package com.jjinterna.tcx.cdrsocket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import org.apache.camel.CamelExecutionException;
import org.apache.camel.builder.AdviceWithRouteBuilder;
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
    MockEndpoint resultEndpoint;
    
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

	}

	@After
	public void tearDown() throws Exception {
		integration.stop();
	}

	private void runtest(String name) throws Exception {
        // Adjust routes
        List<RouteDefinition> routes = context.getRouteDefinitions();
 
        routes.get(0).adviceWith(context, new AdviceWithRouteBuilder() {
            @Override
            public void configure() throws Exception {
                // Replace "from" endpoint with direct:start
                replaceFromWith("direct:start");
                // Mock and skip result endpoint
                mockEndpointsAndSkip("direct:publish");
            }
        });
 
        resultEndpoint = context.getEndpoint("mock:direct:publish", MockEndpoint.class);
        
        // Start the integration
        integration.run();
 
        // Send the test message
        BufferedReader in = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream(name)));
        String line = null;
        while ((line = in.readLine()) != null) {
            context.createProducerTemplate().sendBody("direct:start", line);        	
        }
	}
	
    @Test
    public void l_001() throws Exception  {

		runtest("/l_001.txt");

		resultEndpoint.allMessages().body().isInstanceOf(com.jjinterna.pbxevents.model.tcx.CallStop.class);
        resultEndpoint.assertIsSatisfied();
    }

    @Test
    public void l_002() throws Exception {

    	runtest("/l_002.txt");

    	//resultEndpoint.expectedMessageCount(1);
    	//resultEndpoint.setAssertPeriod(2000);
		resultEndpoint.allMessages().body().isInstanceOf(com.jjinterna.pbxevents.model.tcx.CallStop.class);
        resultEndpoint.assertIsSatisfied();
    }

    @Test
    public void l_003() throws Exception {

    	runtest("/l_003.txt");

    	resultEndpoint.allMessages().body().isInstanceOf(com.jjinterna.pbxevents.model.tcx.CallStop.class);
        resultEndpoint.assertIsSatisfied();
    }

}
