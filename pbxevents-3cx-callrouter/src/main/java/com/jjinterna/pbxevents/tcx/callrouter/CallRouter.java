package com.jjinterna.pbxevents.tcx.callrouter;

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
import com.jjinterna.pbxevents.tcx.callrouter.internal.CallRouterResourceRoute;

@Component(description = CallRouter.COMPONENT_DESCRIPTION, immediate = true, metatype = true, policy = ConfigurationPolicy.REQUIRE)
@Properties({ @Property(name = "camelContextId", value = "pbxevents-3cx-callrouter"),
		@Property(name = "camelRouteId", value = "default"),		
		@Property(name = "active", value = "true"),
		@Property(name = "httpUri")
})
@References({ @Reference(name = "camelComponent", referenceInterface = ComponentResolver.class, cardinality = ReferenceCardinality.MANDATORY_MULTIPLE, policy = ReferencePolicy.DYNAMIC, policyOption = ReferencePolicyOption.GREEDY, bind = "gotCamelComponent", unbind = "lostCamelComponent") })
public class CallRouter extends AbstractCamelRunner {

	public static final String COMPONENT_DESCRIPTION = "PBXEvents 3CX Call Router";

	@Reference
	private EventMediator mediator;

	@Override
	protected List<RoutesBuilder> getRouteBuilders() {
		List<RoutesBuilder> routesBuilders = new ArrayList<>();
		routesBuilders.add(mediator.publisher());
		routesBuilders.add(new CallRouterResourceRoute());
		return routesBuilders;
	}

}