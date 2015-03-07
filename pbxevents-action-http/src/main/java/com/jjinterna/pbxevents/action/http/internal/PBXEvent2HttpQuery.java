package com.jjinterna.pbxevents.action.http.internal;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import com.jjinterna.pbxevents.model.PBXEvent;

public class PBXEvent2HttpQuery implements Processor {

	@Override
	public void process(Exchange exchange) throws Exception {
		PBXEvent event = (PBXEvent) exchange.getIn().getBody();
        StringBuilder buffer = new StringBuilder();
        event.append(null, buffer, HttpQueryToStringStrategy.INSTANCE);
		exchange.getIn().setHeader(Exchange.HTTP_QUERY, buffer.toString());
	}

}
