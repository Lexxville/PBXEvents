package com.jjinterna.pbxevents._3cx.cdrsocket.internal;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JaxbDataFormat;
import org.apache.commons.lang.Validate;

import com.jjinterna.pbxevents._3cx.cdrsocket.model.Call;
import com.jjinterna.pbxevents.model.CallStop;

public class CdrSocketRoute extends RouteBuilder {

	private String host;
	private Integer port;
	private DateFormat dateFormat = new SimpleDateFormat("ddMMyyHHmmss");
	private DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");

	@Override
	public void configure() throws Exception {
		checkProperties();

		dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
		timeFormat.setTimeZone(TimeZone.getTimeZone("GMT"));

		JaxbDataFormat jaxb = new JaxbDataFormat();
		jaxb.setContextPath(Call.class.getPackage().getName());
		jaxb.setPartClass(Call.class.getName());

		from("netty4:tcp://{{host}}:{{port}}?sync=false&textline=true")
				.filter(body().startsWith("<Call")).unmarshal(jaxb)
				.process(new Processor() {

					@Override
					public void process(Exchange exchange) throws Exception {
						Call call = exchange.getIn().getBody(Call.class);
						CallStop event = new CallStop();
						event.setCallId(call.getCallId());
						event.setCallingNumber(call.getCallerId());
						event.setCalledNumber(call.getDialedNumber());
						if (call.getDuration() != null
								&& call.getDuration().length() > 8) {
							event.setCallDuration((int) (timeFormat.parse(
									call.getDuration().substring(0, 8))
									.getTime() / 1000));
						}
						if (call.getStartTime() != null
								&& call.getStartTime().length() == 12) {
							event.setCallSetupTime((int) (dateFormat.parse(
									call.getStartTime()).getTime() / 1000));
						}
						if (call.getAnswerTime() != null
								&& call.getAnswerTime().length() == 12) {
							event.setCallConnectTime((int) (dateFormat.parse(
									call.getAnswerTime()).getTime() / 1000));
						}
						if (call.getEndTime() != null
								&& call.getEndTime().length() == 12) {
							event.setCallDisconnectTime((int) (dateFormat
									.parse(call.getEndTime()).getTime() / 1000));
						}
						exchange.getIn().setBody(event);
					}

				}).to("direct:publish");
	}

	private void checkProperties() {
		Validate.notNull(host, "host property is not set");
		Validate.notNull(port, "port property is not set");
	}
}
