package com.jjinterna.pbxevents.rtcache.internal;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.cache.CacheConstants;
import org.apache.commons.lang.Validate;

import com.jjinterna.pbxevents.model.CallAbandon;
import com.jjinterna.pbxevents.model.CallComplete;
import com.jjinterna.pbxevents.model.CallConnect;
import com.jjinterna.pbxevents.model.CallEnterQueue;
import com.jjinterna.pbxevents.model.CallExitWithTimeout;
import com.jjinterna.pbxevents.model.Phone;
import com.jjinterna.pbxevents.model.PhoneLine;
import com.jjinterna.pbxevents.model.QueueAddMember;
import com.jjinterna.pbxevents.model.QueueMemberEvent;
import com.jjinterna.pbxevents.model.QueuePauseMember;
import com.jjinterna.pbxevents.model.QueueRemoveMember;
import com.jjinterna.pbxevents.model.QueueResumeMember;
import com.jjinterna.pbxevents.model.QueuedCall;

public class RtCacheRoute extends RouteBuilder {

	private Integer phoneTimeToLiveSeconds;
	private Integer lineTimeToLiveSeconds;
	private Integer queuedCallTimeToLiveSeconds;
	private Integer queueMemberTimeToLiveSeconds;

	private static final String PBXEVENT = "PBXEvent";

	private static final String LINE_UPDATE = "direct:lineUpdate";
	private static final String PHONE_UPDATE = "direct:phoneUpdate";
	private static final String QUEUED_CALL_UPDATE = "direct:queuedCallUpdate";
	private static final String QUEUED_CALL_DELETE = "direct:queuedCallDelete";
	private static final String QUEUE_MEMBER_UPDATE = "direct:queueMemberUpdate";
	private static final String QUEUE_MEMBER_DELETE = "direct:queueMemberDelete";

	@Override
	public void configure() throws Exception {
		checkProperties();

		from(LINE_UPDATE)
		.setHeader(CacheConstants.CACHE_OPERATION, constant(CacheConstants.CACHE_OPERATION_UPDATE))
		.setHeader(CacheConstants.CACHE_KEY, simple("${body.lineNumber}"))
		.toF("cache://%s?timeToIdleSeconds=0&timeToLiveSeconds=%d", PhoneLine.class.getName(), lineTimeToLiveSeconds);		

		from(PHONE_UPDATE)
		.setHeader(CacheConstants.CACHE_OPERATION, constant(CacheConstants.CACHE_OPERATION_UPDATE))
		.setHeader(CacheConstants.CACHE_KEY, simple("${body.phoneAddress}"))
		.toF("cache://%s?timeToIdleSeconds=0&timeToLiveSeconds=%d&diskPersistent=true", Phone.class.getName(), phoneTimeToLiveSeconds);

		from(QUEUED_CALL_UPDATE)
		.setHeader(CacheConstants.CACHE_OPERATION, constant(CacheConstants.CACHE_OPERATION_UPDATE))
		.setHeader(CacheConstants.CACHE_KEY, simple("${body.callId}"))
		.toF("cache://%s?timeToIdleSeconds=0&timeToLiveSeconds=%d&diskPersistent=true", QueuedCall.class.getName(), queuedCallTimeToLiveSeconds);

		from(QUEUED_CALL_DELETE)
		.setHeader(CacheConstants.CACHE_OPERATION, constant(CacheConstants.CACHE_OPERATION_DELETE))
		.setHeader(CacheConstants.CACHE_KEY, simple("${body.callId}"))
		.toF("cache://%s", QueuedCall.class.getName());

		from(QUEUE_MEMBER_UPDATE)
		.setHeader(CacheConstants.CACHE_OPERATION, constant(CacheConstants.CACHE_OPERATION_UPDATE))
		.setHeader(CacheConstants.CACHE_KEY, simple("${body.queue}-${body.member}"))
		.toF("cache://%s?timeToIdleSeconds=0&timeToLiveSeconds=%d&diskPersistent=true", QueueMemberEvent.class.getName(), queueMemberTimeToLiveSeconds);

		from(QUEUE_MEMBER_DELETE)
		.setHeader(CacheConstants.CACHE_OPERATION, constant(CacheConstants.CACHE_OPERATION_DELETE))
		.setHeader(CacheConstants.CACHE_KEY, simple("${body.queue}-${body.member}"))
		.toF("cache://%s", QueueMemberEvent.class.getName());

		from("direct:start")
		.choice()
			.when(header(PBXEVENT).in(PhoneLine.class.getSimpleName())).to(LINE_UPDATE).endChoice()
			.when(header(PBXEVENT).in(Phone.class.getSimpleName())).to(PHONE_UPDATE).endChoice()
			.when(header(PBXEVENT)
					.in(CallEnterQueue.class.getSimpleName(),
							CallConnect.class.getSimpleName(),
							CallExitWithTimeout.class.getSimpleName()))
							.to(QUEUED_CALL_UPDATE).endChoice()
			.when(header(PBXEVENT)
					.in(CallAbandon.class.getSimpleName(),
							CallComplete.class.getSimpleName()))
							.to(QUEUED_CALL_DELETE).endChoice()
			.when(header(PBXEVENT)
					.in(QueueAddMember.class.getSimpleName(),
							QueueResumeMember.class.getSimpleName()))
							.to(QUEUE_MEMBER_UPDATE).endChoice()
			.when(header(PBXEVENT)
					.in(QueueRemoveMember.class.getSimpleName(),
							QueuePauseMember.class.getSimpleName()))
							.to(QUEUE_MEMBER_DELETE).endChoice()
		.end();

	}

	public void checkProperties() {
		Validate.notNull(phoneTimeToLiveSeconds, "phoneTimeToLiveSeconds property is not set");
		Validate.notNull(lineTimeToLiveSeconds, "lineTimeToLiveSeconds property is not set");
		Validate.notNull(queuedCallTimeToLiveSeconds, "queuedCallTimeToLiveSeconds property is not set");
		Validate.notNull(queueMemberTimeToLiveSeconds, "queueEventTimeToLiveSeconds property is not set");
	}

}
