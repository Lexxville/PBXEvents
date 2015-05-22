package com.jjinterna.pbxevents.action.sendfax;

import java.util.ArrayList;
import java.util.List;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.RoutesBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.processor.aggregate.AggregationStrategy;
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

import com.jjinterna.pbxevents.model.TxFaxResult;
import com.jjinterna.pbxevents.routes.EventMediator;
import com.jjinterna.pbxevents.routes.EventSelector;
import com.jjinterna.pbxevents.routes.TelephonyService;
import com.jjinterna.pbxevents.routes.selector.EventTypeSelector;

@Component(description = SendFaxAction.COMPONENT_DESCRIPTION, immediate = true, metatype = true, policy = ConfigurationPolicy.REQUIRE)
@Properties({
	@Property(name = "camelContextId", value = "pbxevents-sendfax"),
	@Property(name = "camelRouteId", value = "default"),
	@Property(name = "active", value = "true") 
})
@References({ @Reference(name = "camelComponent", referenceInterface = ComponentResolver.class, 
	cardinality = ReferenceCardinality.MANDATORY_MULTIPLE, policy = ReferencePolicy.DYNAMIC, 
	policyOption = ReferencePolicyOption.GREEDY, bind = "gotCamelComponent", unbind = "lostCamelComponent") })
public class SendFaxAction extends AbstractCamelRunner {

	public static final String COMPONENT_DESCRIPTION = "PBXEvents Send Fax Action";

	@Reference
	private EventMediator mediator;

	@Reference
	private TelephonyService service;
	
	private static final String[] aggregate = { "direct:aggregate" };
	
	@Override
	protected List<RoutesBuilder> getRouteBuilders() {
		List<RoutesBuilder> routesBuilders = new ArrayList<>();
		List<EventSelector> selectors = new ArrayList<>();
		
		selectors.add(new EventTypeSelector(TxFaxResult.class.getSimpleName()));
		routesBuilders.add(mediator.subscriber(selectors, aggregate));
		routesBuilders.add(mediator.subscriber(getContext()));
		routesBuilders.add(new RouteBuilder() {

			@Override
			public void configure() throws Exception {
				
				from("direct:start")
				.process(new Processor() {

					@Override
					public void process(Exchange exchange) throws Exception {
						String id = "1234567890";
						exchange.getIn().setHeader("FaxRequestId", id);
						service.sendAsyncApiCommand("originate", 
								"{fax_request_id=" + id + "}loopback/1234/sendfax &txfax(/tmp/1)");						
					}
					
				})
				.to("direct:aggregate");
				
				from("direct:aggregate")
					.aggregate(header("FaxRequestId")).completionSize(2).aggregationStrategy(new AggregationStrategy() {
						
						@Override
						public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
							return newExchange;
						}
					})
					.to("log:sendFax?showHeaders=true");
			}
			
		});

		return routesBuilders;
	}

}