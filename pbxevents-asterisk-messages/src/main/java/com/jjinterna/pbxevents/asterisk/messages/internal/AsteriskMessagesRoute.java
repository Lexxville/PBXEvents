package com.jjinterna.pbxevents.asterisk.messages.internal;

import java.net.URLEncoder;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.commons.lang.Validate;

import com.jjinterna.pbxevents.model.PBXEvent;
import com.jjinterna.pbxevents.model.PhoneLine;
import com.jjinterna.pbxevents.routes.logfile.LogfileLifecycleStrategySupport;
import com.jjinterna.pbxevents.routes.logfile.LogfileMark;

public class AsteriskMessagesRoute extends RouteBuilder {

	private String camelRouteId;
	private String fileName;
	
	@Override
	public void configure() throws Exception {
		checkProperties();
		
		final LogfileMark queueLogMark = new LogfileMark("data/mark_" + URLEncoder.encode(camelRouteId + "-" + fileName, "UTF-8"));
		String s = queueLogMark.getMark();
		final String mark = (s != null) ? s : "";

		from("stream:file?fileName={{fileName}}&scanStream=true&scanStreamDelay=1000")
		.id(camelRouteId)
		.process(new Processor() {

			@Override
			public void process(Exchange exchange) throws Exception {
				PBXEvent event = null;
				String line = (String) exchange.getIn().getBody();
				String timestamp = (line.length() > 21) ? 
						line.substring(1,  20) : null;
				if (timestamp != null && timestamp.compareTo(mark) > 0) {
					int index = line.indexOf("res_security_log.c: ");
					if (index > 0) {
						String parts[] = line.substring(index + 20).split(",");
						if (parts.length == 10 &&
								parts[0].equals("SecurityEvent=\"SuccessfulAuth\"") &&
								parts[3].equals("Service=\"SIP\"")) {
							String extension = parts[5].substring(11, parts[5].length() - 1);
							String remoteAddress = parts[8].substring(15, parts[8].length() - 1);
							String addrParts[] = remoteAddress.split("/");
							if (addrParts.length == 4) {
								PhoneLine phoneLine = new PhoneLine();
								phoneLine.setLineNumber(extension);
								phoneLine.setPhoneAddress(addrParts[2]);
								phoneLine.setPort(Integer.valueOf(addrParts[3]));
								event = phoneLine;
							}
						}
					}
					try {
						queueLogMark.setMark(timestamp);
					} 
					catch (Exception e) {}
				}
				
				if (event == null) {
		        	exchange.setProperty(Exchange.ROUTE_STOP, Boolean.TRUE);
				} else {
					exchange.getIn().setBody(event);
					exchange.getIn().setHeader("PBXEvent", event.getClass().getSimpleName());		
				}
			}
			
		})
		.to("direct:publish");
		
		getContext().addLifecycleStrategy(new LogfileLifecycleStrategySupport(camelRouteId, fileName));
	}

	public void checkProperties() {
		Validate.notNull(fileName, "fileName property is not set");
	}
}
