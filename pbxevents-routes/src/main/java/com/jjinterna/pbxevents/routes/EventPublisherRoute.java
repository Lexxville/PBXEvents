package com.jjinterna.pbxevents.routes;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.model.dataformat.JaxbDataFormat;

import com.jjinterna.pbxevents.model.PBXEvent;

public class EventPublisherRoute extends PBXEventsRoute {

	class JAXBElementWrapper implements Processor {

		@Override
		public void process(Exchange exchange) throws Exception {
			PBXEvent event = (PBXEvent) exchange.getIn().getBody();
			exchange.getIn().setBody(new JAXBElement(
					new QName("http://pbxevents.jjinterna.com/model", event.getClass().getSimpleName()),
					event.getClass(),
					event));
		}
		
	}

	@Override
	public void configure() {

		JaxbDataFormat jaxb = new JaxbDataFormat();
		jaxb.setContextPath(PBXEvent.class.getPackage().getName());

		from("direct:publish").id("publish")
		.process(new JAXBElementWrapper())
		.marshal(jaxb)
		.to("queuingservice:topic:PBXEvents-" + getDomain());		
	
	}

}
