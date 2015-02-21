package com.jjinterna.pbxevents.asterisk.queue.internal;

public enum QueueLogType {

	ABANDON,
	ADDMEMBER,
	COMPLETEAGENT,
	COMPLETECALLER,
	CONFIGRELOAD,
	CONNECT,
	DID,
	ENTERQUEUE,
	EXITWITHTIMEOUT,
	HEARTBEAT,
	HOTDESK,
	PAUSE,
	PAUSEALL,
	PAUSEREASON,
	QUEUESTART,
	REMOVEMEMBER,
	RINGNOANSWER,
	UNPAUSE,
	UNPAUSEALL;

	public String value() {
		return name();
	}

	public static QueueLogType fromValue(String v) {
		return valueOf(v);
	}

}
