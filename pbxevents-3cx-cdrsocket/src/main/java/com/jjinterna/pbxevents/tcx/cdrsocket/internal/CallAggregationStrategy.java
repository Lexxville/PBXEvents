package com.jjinterna.pbxevents.tcx.cdrsocket.internal;

import org.apache.camel.Exchange;
import org.apache.camel.processor.aggregate.AggregationStrategy;

import com.jjinterna.pbxevents.model.tcx.CallDetail;
import com.jjinterna.pbxevents.model.tcx.CallStop;

public class CallAggregationStrategy implements AggregationStrategy {

	@Override
	public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
		if (newExchange.getIn().getBody() instanceof CallDetail) {
			oldExchange.getIn().getBody(CallStop.class).getDetails().add(newExchange.getIn().getBody(CallDetail.class));
		}
		
		return oldExchange == null ? newExchange : oldExchange;
	}

}
