package com.jjinterna.pbxevents.routes;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import com.jjinterna.pbxevents.model.PBXEvent;

public class JAXBElementWrapper implements Processor {

	@Override
	public void process(Exchange exchange) throws Exception {
		PBXEvent event = (PBXEvent) exchange.getIn().getBody();
		exchange.getIn().setBody(new JAXBElement(
			new QName("http://pbxevents.jjinterna.com/model", event.getClass().getSimpleName()),
			event.getClass(),
			event));
	}
	
}

