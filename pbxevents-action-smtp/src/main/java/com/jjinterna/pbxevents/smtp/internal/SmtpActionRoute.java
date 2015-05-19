package com.jjinterna.pbxevents.smtp.internal;

import org.apache.camel.builder.RouteBuilder;

public class SmtpActionRoute extends RouteBuilder {

	private Integer completionSize;
	private Long completionInterval;
	private String to;
	private String from;
	private String cc;
	private String bcc;

	@Override
	public void configure() throws Exception {
		from("direct:start")
		.aggregate().header("PBXEventId").completionSize(completionSize)//.completionInterval(completionInterval)
		.recipientList(simple("{{snmpUri}}?to={{to}}&from={{from}}&cc={{cc}}&bcc={{bcc}}"));
	}

}
