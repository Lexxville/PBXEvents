package com.jjinterna.pbxevents.tcx.cdrsocket.internal;

import org.apache.camel.Exchange;
import org.apache.camel.Predicate;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JaxbDataFormat;
import org.apache.camel.processor.aggregate.AggregationStrategy;
import org.apache.commons.lang.Validate;

import com.jjinterna.pbxevents.model.tcx.CallDetail;
import com.jjinterna.pbxevents.model.tcx.CallStop;

public class CdrSocketRoute extends RouteBuilder {

	private String host;
	private Integer port;

	@Override
	public void configure() throws Exception {
		checkProperties();

		AggregationStrategy aggregationStrategy = new CallAggregationStrategy();
		Processor callProcessor = new CallProcessor();

		JaxbDataFormat jaxb = new JaxbDataFormat();
		jaxb.setContextPath(CallStop.class.getPackage().getName());
		jaxb.setPartClass(CallStop.class.getName());

		JaxbDataFormat jaxb2 = new JaxbDataFormat();
		jaxb2.setContextPath(CallDetail.class.getPackage().getName());
		jaxb2.setPartClass(CallDetail.class.getName());
		
		from("netty4:tcp://{{host}}:{{port}}?sync=false&textline=true&decoderMaxLineLength=2048")
				.to("log:3cx-cdrsocket")
				.choice()
					.when(body().startsWith("<CallStop>")).unmarshal(jaxb).process(callProcessor)
					.when(body().startsWith("<CallDetail>")).unmarshal(jaxb2)
					.otherwise().stop()
				.end()
				.aggregate(simple("${body.idCallHistory3}"), aggregationStrategy)
				.completionPredicate(new Predicate() {
					
					@Override
					public boolean matches(Exchange exchange) {
						CallStop cs = exchange.getIn().getBody(CallStop.class);
						return cs.getTcxNumDetails() == cs.getDetails().size();
					}
				})
				.process(new Processor() {

					@Override
					public void process(Exchange exchange) throws Exception {
						CallStop cs = exchange.getIn().getBody(CallStop.class);
						int i = cs.getDetails().size();
						if (i > 0) {
							if ("Completed".equals(cs.getDetails().get(i - 1).getStatus())) {
								return;
							}
						}
						exchange.setProperty(Exchange.ROUTE_STOP, Boolean.TRUE);						
					}
					
				})
				.to("direct:publish");
	}

	private void checkProperties() {
		Validate.notNull(host, "host property is not set");
		Validate.notNull(port, "port property is not set");
	}
}
