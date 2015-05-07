package com.jjinterna.pbxevents.tcx.cdrsocket.internal;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import com.jjinterna.pbxevents.model.tcx.CallStop;

public class CallProcessor implements Processor {

	private DateFormat dateFormat = new SimpleDateFormat("ddMMyyHHmmss");
	private DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");

	public CallProcessor() {
		dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
		timeFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
	}

	@Override
	public void process(Exchange exchange) throws Exception {
		CallStop call = exchange.getIn().getBody(CallStop.class);
		call.setCallId(call.getTcxCallId());
		call.setCallingNumber(call.getTcxCallerId());
		call.setCalledNumber(call.getTcxDialedNumber());
		String s = call.getTcxDuration();
		if (s != null && s.length() > 8) {
			call.setCallDuration((int) (timeFormat.parse(s.substring(0, 8)).getTime() / 1000));
		}
		s = call.getTcxStartTime();
		if (s != null && s.length() == 12) {
			call.setCallSetupTime((int) (dateFormat.parse(s).getTime() / 1000));
		}
		s = call.getTcxAnswerTime();
		if (s != null && s.length() == 12) {
			call.setCallConnectTime((int) (dateFormat.parse(s).getTime() / 1000));
		}
		s = call.getTcxEndTime();
		if (s != null && s.length() == 12) {
			call.setCallDisconnectTime((int) (dateFormat.parse(s).getTime() / 1000));
		}

	}

}
