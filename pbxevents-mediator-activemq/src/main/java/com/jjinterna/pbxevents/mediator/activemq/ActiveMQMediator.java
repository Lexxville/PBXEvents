package com.jjinterna.pbxevents.mediator.activemq;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.ModelCamelContext;
import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;

import com.jjinterna.pbxevents.mediator.activemq.internal.PublisherRouteBuilder;
import com.jjinterna.pbxevents.mediator.activemq.internal.SubscriberRouteBuilder;
import com.jjinterna.pbxevents.routes.EventMediator;
import com.jjinterna.pbxevents.routes.EventSelector;

@Component(description=ActiveMQMediator.COMPONENT_DESCRIPTION, immediate = true, metatype = true)
@Properties({
	@Property(name = "destinationName", value = "PBXEvents"),
	@Property(name = "destinationType", value = "topic")    
})
@Service(value=EventMediator.class)
public class ActiveMQMediator implements EventMediator {

    public static final String COMPONENT_DESCRIPTION = "PBXEvents ActiveMQ Event Mediator";
    
    private String destinationName;
    private String destinationType;
    private static final String[] directStart = { "direct:start" };
    
	@Activate
	protected void activate(final Map<String, Object> props) {
		destinationName = (String) props.get("destinationName");
		destinationType = (String) props.get("destinationType");			
	}

	@Override
	public RouteBuilder publisher() {
		return new PublisherRouteBuilder(destinationName, destinationType);
	}

	@Override
	public RouteBuilder subscriber(final List<EventSelector> selectors) {
		return new SubscriberRouteBuilder(destinationName, destinationType, selectors, directStart);
	}

	@Override
	public String queueUri(String queueName) {
		StringBuilder sb = new StringBuilder();
		sb.append("activemq:queue:");
		sb.append(queueName);
		sb.append("?username=karaf&password=karaf");		
		return sb.toString();
	}

	@Override
	public RouteBuilder subscriber(List<EventSelector> selectors, String[] destUris) {
		return new SubscriberRouteBuilder(destinationName, destinationType, selectors, destUris);
	}

	@Override
	public RouteBuilder subscriber(ModelCamelContext context) {
		return new SubscriberRouteBuilder(context.getName(), "queue", Collections.<EventSelector> emptyList(), directStart);
		/*
		PropertiesComponent pc = context.getComponent("properties", PropertiesComponent.class);
		if (pc != null) {
			final String pid = pc.getInitialProperties().getProperty("service.pid");
			if (pid != null) {
				return new RouteBuilder() {

					@Override
					public void configure() throws Exception {
						fromF("activemq:queue:%s?username=karaf&password=karaf", pid)
						.to("direct:start");						
					}
					
				};
			}
		}
		return null;*/
	}

}
