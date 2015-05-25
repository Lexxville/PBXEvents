package com.jjinterna.pbxevents.action.sendfax;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
	@Property(name = "active", value = "true"),
	@Property(name = "scheduledRepeat", value = "0"),
	@Property(name = "scheduledPeriod", value = "0"),	
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
	
	private Integer maxFaxCalls;
	
	private static final String[] aggregate = { "direct:aggregate" };
	private static final String REQUEST_ID = "PBXEventsRequestId";
	private static final String FAX_CALLS = "PBXEventsFaxCalls";
	private static final String MAX_FAX_CALL = "PBXEventsMaxFaxCalls";

	
	@Override
	protected List<RoutesBuilder> getRouteBuilders() {
		List<RoutesBuilder> routesBuilders = new ArrayList<>();
		List<EventSelector> selectors = new ArrayList<>();

		routesBuilders.add(mediator.publisher());
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
						if (exchange.getIn().getHeader(REQUEST_ID) == null) {
							exchange.getIn().setHeader(REQUEST_ID, UUID.randomUUID().toString());
						}
						Integer faxCalls = exchange.getIn().getHeader(FAX_CALLS, Integer.class);
						if (faxCalls == null) {
							faxCalls = 0;
						}
						faxCalls++;

						StringBuilder sb = new StringBuilder();
						sb.append("{");
						sb.append(FAX_CALLS);
						sb.append("=");
						sb.append(faxCalls);
						for (String name : exchange.getIn().getHeaders().keySet()) {
							if (name.startsWith("PBXEvents")) {
								sb.append(",");
								sb.append(name);
								sb.append("=");
								sb.append(exchange.getIn().getHeader(name));
							}
						}
						sb.append("}");

						sb.append("loopback/1234/sendfax &txfax(/tmp/1)");
						service.sendAsyncApiCommand("originate", sb.toString());						
					}
					
				})
				.to("direct:aggregate");
				
				from("direct:aggregate")
					.aggregate(header(REQUEST_ID), new AggregationStrategy() {						
						@Override
						public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
							return (oldExchange == null || newExchange.getIn().getBody() instanceof TxFaxResult) ?
									newExchange : oldExchange;
						}
					}).completionSize(2).completionTimeout(120000)
					.process(new Processor() {

						@Override
						public void process(Exchange exchange) throws Exception {
							throw new Exception("FAIL");
						}
						
					})
					.to("log:sendFax?showHeaders=true");
			}
			
		});

		return routesBuilders;
	}

}