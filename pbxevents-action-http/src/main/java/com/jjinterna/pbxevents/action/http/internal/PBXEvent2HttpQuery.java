package com.jjinterna.pbxevents.action.http.internal;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import com.jjinterna.pbxevents.model.PBXEvent;

public class PBXEvent2HttpQuery implements Processor {

	@Override
	public void process(Exchange exchange) throws Exception {
		PBXEvent event = (PBXEvent) exchange.getIn().getBody();
		String q = PBXEventURLEncoder.encode(event);
		exchange.getIn().setHeader(Exchange.HTTP_QUERY, q);
	}

}
