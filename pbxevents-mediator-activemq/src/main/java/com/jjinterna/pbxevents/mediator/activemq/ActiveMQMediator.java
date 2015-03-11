package com.jjinterna.pbxevents.mediator.activemq;

import java.util.List;
import java.util.Map;

import org.apache.camel.builder.RouteBuilder;
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
@Service(value=com.jjinterna.pbxevents.routes.EventMediator.class)
public class ActiveMQMediator implements EventMediator {

    public static final String COMPONENT_DESCRIPTION = "PBXEvents ActiveMQ Event Mediator";
    
    private String destinationName;
    private String destinationType;
    
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
		return new SubscriberRouteBuilder(destinationName, destinationType, selectors, "direct:start");
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
	public RouteBuilder subscriber(List<EventSelector> selectors, String destUri) {
		return new SubscriberRouteBuilder(destinationName, destinationType, selectors, destUri);
	}

}
