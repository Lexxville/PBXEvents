package com.jjinterna.pbxevents.routes;

import java.util.List;

import com.jjinterna.pbxevents.model.Phone;
import com.jjinterna.pbxevents.model.PhoneLine;
import com.jjinterna.pbxevents.model.QueueMemberEvent;
import com.jjinterna.pbxevents.model.QueuedCall;

public interface RtCache {

	PhoneLine getPhoneLine(String key);
	List<PhoneLine> getPhoneLines();

	Phone getPhone(String key);
	List<Phone> getPhones();

	QueuedCall getQueuedCall(String key);
	List<QueuedCall> getQueuedCalls();

	List<QueueMemberEvent> getQueueMembers();

}
