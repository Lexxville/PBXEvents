package com.jjinterna.pbxevents.action.http.internal;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import com.jjinterna.pbxevents.model.CallConnect;
import com.jjinterna.pbxevents.model.PBXCallQueueEvent;
import com.jjinterna.pbxevents.model.PBXEvent;

public class PBXEventURLEncoder {

	private static final String enc = "UTF-8";
	
	public static String encode(PBXEvent event) throws UnsupportedEncodingException {
		String q = "Event=" + event.getClass().getSimpleName();
		if (event instanceof PBXCallQueueEvent) {
			PBXCallQueueEvent e = (PBXCallQueueEvent) event;
			if (e.getAgent() != null) {
				q += "&Agent=" + URLEncoder.encode(e.getAgent(), enc);
			}
			if (e.getCallerId() != null) {
				q += "&CallerId=" + URLEncoder.encode(e.getCallerId(), enc);
			}
			if (e.getCallId() != null) {
				q += "&CallId=" + URLEncoder.encode(e.getCallId(), enc);
			}
			if (e.getDid() != null) {
				q += "&DID=" + URLEncoder.encode(e.getDid(), enc);
			}
			if (e.getQueue() != null) {
				q += "&Queue=" + URLEncoder.encode(e.getQueue(), enc);
			}
			if (e.getQueueConnectTime() != null) {
				q += "&QueueConnectTime=" + e.getQueueConnectTime();
			}
			if (e.getQueueEnterTime() != null) {
				q += "&QueueEnterTime=" + e.getQueueEnterTime();
			}
			if (e.getQueueLeaveTime() != null) {
				q += "&QueueLeaveTime=" + e.getQueueLeaveTime();
			}
		}
		if (event instanceof CallConnect) {
			CallConnect callConnect = (CallConnect) event;
			if (callConnect.getPhoneLine() != null) {
				q += "&PhoneLine.LineNumber=" + URLEncoder.encode(callConnect.getPhoneLine().getLineNumber(), enc); 
				q += "&PhoneLine.PhoneAddress=" + URLEncoder.encode(callConnect.getPhoneLine().getPhoneAddress(), enc);
				q += "&PhoneLine.Port=" + callConnect.getPhoneLine().getPort();				
			}
			if (callConnect.getPhone() != null) {
				q += "&Phone.SysName=" + URLEncoder.encode(callConnect.getPhone().getSysName(), enc); 
				q += "&Phone.SysDesc=" + URLEncoder.encode(callConnect.getPhone().getSysDesc(), enc);
				q += "&Phone.PhoneAddress=" + URLEncoder.encode(callConnect.getPhone().getPhoneAddress(), enc);
				q += "&Phone.AgentAddress=" + URLEncoder.encode(callConnect.getPhone().getAgentAddress(), enc);				
			}
		}
		return q;
	}

}
