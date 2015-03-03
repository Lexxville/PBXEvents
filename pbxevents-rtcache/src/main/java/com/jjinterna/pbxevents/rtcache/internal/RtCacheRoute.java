package com.jjinterna.pbxevents.rtcache.internal;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.cache.CacheConstants;

import com.jjinterna.pbxevents.model.PhoneLineDiscovered;
import com.jjinterna.pbxevents.routes.RtCache.RtCacheType;

public class RtCacheRoute extends RouteBuilder {

	private static final String PBXEVENT = "PBXEvent";

	@Override
	public void configure() throws Exception {
		from("direct:lines")
		.setHeader(CacheConstants.CACHE_OPERATION, constant(CacheConstants.CACHE_OPERATION_UPDATE))
		.setHeader(CacheConstants.CACHE_KEY, simple("${body.extension}"))
		.toF("cache://%s", RtCacheType.LINE);		

		from("direct:start")
		.choice()
			.when(header(PBXEVENT).in(PhoneLineDiscovered.class.getSimpleName()))
			.to("direct:lines")
			.endChoice()
		.end();

	}

}
