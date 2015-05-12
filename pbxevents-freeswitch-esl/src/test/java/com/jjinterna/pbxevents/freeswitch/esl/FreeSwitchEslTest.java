package com.jjinterna.pbxevents.freeswitch.esl;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

import com.jjinterna.pbxevents.model.TxFaxNegotiateResult;
import com.jjinterna.pbxevents.model.TxFaxPageResult;
import com.jjinterna.pbxevents.model.TxFaxResult;

@RunWith(JUnit4.class)
public class FreeSwitchEslTest {

	FreeSwitchEsl integration;
    ModelCamelContext context;
    MockEndpoint resultEndpoint;
    
	@Before
	public void setUp() throws Exception {

		// Set property prefix for unit testing
		System.setProperty(FreeSwitchEsl.PROPERTY_PREFIX, "unit");

		// Prepare the integration
		integration = new FreeSwitchEsl();
		integration.prepare(null, ScrHelper.getScrProperties(integration.getClass().getName()));
		context = integration.getContext();

		// Disable JMX for test
		context.disableJMX();

	}

	@After
	public void tearDown() throws Exception {
		integration.stop();
	}

	private void run(String fileName) throws Exception {
        // Adjust routes
        List<RouteDefinition> routes = context.getRouteDefinitions();
 
        routes.get(0).adviceWith(context, new AdviceWithRouteBuilder() {
            @Override
            public void configure() throws Exception {
                // Mock and skip result endpoint
                mockEndpointsAndSkip("direct:publish");
            }
        });
 
        resultEndpoint = context.getEndpoint("mock:direct:publish", MockEndpoint.class);
        
        // Start the integration
        integration.run();
 
        // Send the test message
        StringBuilder sb = new StringBuilder();
        BufferedReader in = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream(fileName)));
        String line = null;
        while ((line = in.readLine()) != null) {
        	sb.append(line);
        }

        Map<String, String> map = new HashMap<String, String>();
        String[] parts = sb.toString().split(", ");
        for (String s : parts) {
        	String[] kv = s.split("=");
        	map.put(kv[0], kv[1]);
        }
        context.createProducerTemplate().sendBody("direct:start", map);        
	}

    @Test
    public void testNegotiateResult() throws Exception {
    	run("/negotiate.txt");
    	resultEndpoint.expectedMessageCount(1);
		resultEndpoint.allMessages().body().isInstanceOf(TxFaxNegotiateResult.class);
        resultEndpoint.assertIsSatisfied();    	
    }

    @Test
    public void testPageResult() throws Exception {
    	run("/pageresult.txt");
    	resultEndpoint.expectedMessageCount(1);
		resultEndpoint.allMessages().body().isInstanceOf(TxFaxPageResult.class);
        resultEndpoint.assertIsSatisfied();    	
    }

    @Test
    public void testFaxResult() throws Exception {
    	run("/faxresult.txt");
    	resultEndpoint.expectedMessageCount(1);
		resultEndpoint.allMessages().body().isInstanceOf(TxFaxResult.class);
        resultEndpoint.assertIsSatisfied();    	
    }

}
