package com.jjinterna.pbxevents.action.tcx.limiter;

import java.util.ArrayList;
import java.util.List;

import org.apache.camel.RoutesBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.exec.ExecBinding;
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

import com.jjinterna.pbxevents.model.CallUpdate;
import com.jjinterna.pbxevents.routes.EventMediator;
import com.jjinterna.pbxevents.routes.EventSelector;
import com.jjinterna.pbxevents.routes.selector.EventTypeSelector;

@Component(description = LimiterAction.COMPONENT_DESCRIPTION, immediate = true, metatype = true, policy = ConfigurationPolicy.REQUIRE)
@Properties({
		@Property(name = "camelContextId", value = "pbxevents-action-3cx-limiter"),
		@Property(name = "maxCallDuration", value = "3600"),
		@Property(name = "executable"),
		@Property(name = "extension", value = "\\d+"),		
		@Property(name = "active", value = "true") })
@References({ @Reference(name = "camelComponent", referenceInterface = ComponentResolver.class, cardinality = ReferenceCardinality.MANDATORY_MULTIPLE, policy = ReferencePolicy.DYNAMIC, policyOption = ReferencePolicyOption.GREEDY, bind = "gotCamelComponent", unbind = "lostCamelComponent") })
public class LimiterAction extends AbstractCamelRunner {

	public static final String COMPONENT_DESCRIPTION = "PBXEvents 3CX Call Limiter Action";

	@Reference
	private EventMediator mediator;

	@SuppressWarnings("unused")
	private String executable;
	@SuppressWarnings("unused")
	private Integer maxCallDuration;
	@SuppressWarnings("unused")
	private String extension;

	@Override
	protected List<RoutesBuilder> getRouteBuilders() {
		List<RoutesBuilder> routesBuilders = new ArrayList<>();

		EventSelector callUpdateSelector = new EventTypeSelector(
				CallUpdate.class.getSimpleName());
		List<EventSelector> selectors = new ArrayList<>();
		selectors.add(callUpdateSelector);
		routesBuilders.add(mediator.subscriber(selectors));
		routesBuilders.add(new RouteBuilder() {
			
			@Override
			public void configure() throws Exception {
				from("direct:start")
				.filter(simple("${body.callDuration} > {{maxCallDuration}} && ${body.callingNumber} regex '{{extension}}'"))
				.setHeader(ExecBinding.EXEC_COMMAND_ARGS, simple("dropcall ${body.callId} ${body.callingNumber}"))
				.to("exec:{{executable}}?timeout=10000");
			}
		});
		return routesBuilders;
	}

}

