package com.jjinterna.pbxevents.rtcache.internal;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.cache.CacheConstants;
import org.apache.commons.lang.Validate;

import com.jjinterna.pbxevents.model.Phone;
import com.jjinterna.pbxevents.model.PhoneLine;

public class RtCacheRoute extends RouteBuilder {

	private Integer phoneTimeToLiveSeconds;
	private Integer lineTimeToLiveSeconds;
	
	private static final String PBXEVENT = "PBXEvent";

	@Override
	public void configure() throws Exception {
		checkProperties();

		from("direct:lineUpdate")
		.setHeader(CacheConstants.CACHE_OPERATION, constant(CacheConstants.CACHE_OPERATION_UPDATE))
		.setHeader(CacheConstants.CACHE_KEY, simple("${body.lineNumber}"))
		.toF("cache://%s?timeToLiveSeconds=%d", PhoneLine.class.getName(), lineTimeToLiveSeconds);		

		from("direct:phoneUpdate")
		.setHeader(CacheConstants.CACHE_OPERATION, constant(CacheConstants.CACHE_OPERATION_UPDATE))
		.setHeader(CacheConstants.CACHE_KEY, simple("${body.phoneAddress}"))
		.toF("cache://%s?timeToLiveSeconds=%d&diskPersistent=true", Phone.class.getName(), phoneTimeToLiveSeconds);

		from("direct:start")
		.choice()
			.when(header(PBXEVENT).in(PhoneLine.class.getSimpleName())).to("direct:lineUpdate").endChoice()
			.when(header(PBXEVENT).in(Phone.class.getSimpleName())).to("direct:phoneUpdate").endChoice()			
		.end();

	}

	public void checkProperties() {
		Validate.notNull(phoneTimeToLiveSeconds, "phoneTimeToLiveSeconds property is not set");
		Validate.notNull(lineTimeToLiveSeconds, "lineTimeToLiveSeconds property is not set");
	}

}
