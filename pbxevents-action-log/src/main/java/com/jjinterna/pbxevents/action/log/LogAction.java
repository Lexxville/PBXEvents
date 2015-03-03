package com.jjinterna.pbxevents.action.log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.camel.RoutesBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.scr.AbstractCamelRunner;
import org.apache.camel.spi.ComponentResolver;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.ReferenceCardinality;
import org.apache.felix.scr.annotations.ReferencePolicy;
import org.apache.felix.scr.annotations.ReferencePolicyOption;
import org.apache.felix.scr.annotations.References;

import com.jjinterna.pbxevents.routes.EventMediator;
import com.jjinterna.pbxevents.routes.EventSelector;

@Component(description = LogAction.COMPONENT_DESCRIPTION, immediate = true, metatype = true)
@Properties({
    @Property(name = "active", value = "true")
})
@References({
    @Reference(name = "camelComponent",referenceInterface = ComponentResolver.class,
        cardinality = ReferenceCardinality.MANDATORY_MULTIPLE, policy = ReferencePolicy.DYNAMIC,
        policyOption = ReferencePolicyOption.GREEDY, bind = "gotCamelComponent", unbind = "lostCamelComponent")
})
public class LogAction extends AbstractCamelRunner {

	public static final String COMPONENT_DESCRIPTION = "PBXEvents Log Action";

    @Reference
    private EventMediator mediator;
    
    @Override
    protected List<RoutesBuilder>getRouteBuilders() {
        List<RoutesBuilder>routesBuilders = new ArrayList<>();
        routesBuilders.add(new RouteBuilder() {
			
			@Override
			public void configure() throws Exception {
				
				from("direct:start")
				.to("log:pbxevent?showHeaders=true");				
			}
		});
        routesBuilders.add(mediator.subscriber(Collections.<EventSelector> emptyList()));      
        return routesBuilders;
    }
    
}