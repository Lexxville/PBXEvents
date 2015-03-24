package com.jjinterna.pbxevents.tcx.dnmonitor;

import java.util.ArrayList;
import java.util.List;

import org.apache.camel.RoutesBuilder;
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
import com.jjinterna.pbxevents.tcx.dnmonitor.internal.DNMonitorRoute;

@Component(description = DNMonitor.COMPONENT_DESCRIPTION, immediate = true, metatype = true)
@Properties({
		@Property(name = "camelContextId", value = "pbxevents-3cx-dnmonitor"),
		@Property(name = "camelRouteId", value = "default"),
		@Property(name = "active", value = "true"),
		@Property(name = "executable", value = "C:/Program Files/3CXCallControlAPI_v12/OMSamples/bin/OMSamples.exe"),
		@Property(name = "args", value = "dn_monitor"),
		@Property(name = "timeout", value = "3000"),
		@Property(name = "period", value = "1000") })
@References({ @Reference(name = "camelComponent", referenceInterface = ComponentResolver.class, cardinality = ReferenceCardinality.MANDATORY_MULTIPLE, policy = ReferencePolicy.DYNAMIC, policyOption = ReferencePolicyOption.GREEDY, bind = "gotCamelComponent", unbind = "lostCamelComponent") })
public class DNMonitor extends AbstractCamelRunner {

	public static final String COMPONENT_DESCRIPTION = "PBXEvents 3CX DN Monitor";

	@Reference
	private EventMediator mediator;

	@Override
	protected List<RoutesBuilder> getRouteBuilders() {
		List<RoutesBuilder> routesBuilders = new ArrayList<>();
		routesBuilders.add(new DNMonitorRoute());
		routesBuilders.add(mediator.publisher());
		return routesBuilders;
	}

}