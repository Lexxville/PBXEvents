package com.jjinterna.pbxevents.integration;

import java.util.ArrayList;
import java.util.List;

import org.apache.camel.RoutesBuilder;
import org.apache.camel.scr.AbstractCamelRunner;
import org.apache.camel.spi.ComponentResolver;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.ConfigurationPolicy;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.ReferenceCardinality;
import org.apache.felix.scr.annotations.ReferencePolicy;
import org.apache.felix.scr.annotations.ReferencePolicyOption;
import org.apache.felix.scr.annotations.References;

import com.jjinterna.pbxevents.routes.EventMediator;
import com.jjinterna.pbxevents.routes.EventSelector;
import com.jjinterna.pbxevents.routes.selector.EventTypeSelector;
import com.jjinterna.pbxevents.routes.selector.InstanceOfSelector;

@Component(description = Integration.COMPONENT_DESCRIPTION, immediate = true, metatype = true, policy = ConfigurationPolicy.REQUIRE)
@Properties({
		@Property(name = "camelContextId", value = "pbxevents-integration"),
		@Property(name = "camelRouteId", value = "default"),
		@Property(name = "eventClass", value = "PBXEvent"),
		@Property(name = "eventType"),
		@Property(name = "toUri")
})
@References({ @Reference(name = "camelComponent", referenceInterface = ComponentResolver.class, 
	cardinality = ReferenceCardinality.MANDATORY_MULTIPLE, policy = ReferencePolicy.DYNAMIC, 
	policyOption = ReferencePolicyOption.GREEDY, bind = "gotCamelComponent", unbind = "lostCamelComponent") })
public class Integration extends AbstractCamelRunner {
	
	public static final String COMPONENT_DESCRIPTION = "PBXEvents Integration";

	@Reference
	private EventMediator mediator;

	private String eventClass;
	private String eventType;
	private String toUri;

	@Override
	protected List<RoutesBuilder> getRouteBuilders() {
		List<RoutesBuilder> routesBuilders = new ArrayList<>();

		List<EventSelector> selectors = new ArrayList<>();
		if (eventClass != null) {
			for (String part : eventClass.split(",")) {
				selectors.add(new InstanceOfSelector(part.trim()));				
			}
		}
		if (eventType != null) {
			for (String part : eventType.split(",")) {
				selectors.add(new EventTypeSelector(part.trim()));
			}
		}
		String[] uris = toUri.split(",");
		for (int i=0; i<uris.length; i++) {
			uris[i] = mediator.queueUri(uris[i].trim());
		}
		routesBuilders.add(mediator.subscriber(selectors, uris));

		return routesBuilders;
	}

}
