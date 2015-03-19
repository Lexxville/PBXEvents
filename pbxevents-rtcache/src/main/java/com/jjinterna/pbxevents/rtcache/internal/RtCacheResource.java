package com.jjinterna.pbxevents.rtcache.internal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.SimpleRegistry;
import org.apache.cxf.jaxrs.provider.json.JSONProvider;

import com.jjinterna.pbxevents.routes.RtCache;

public class RtCacheResource extends RouteBuilder {

	private RtCache rt;
	private SimpleRegistry registry;

	public RtCacheResource(SimpleRegistry registry, RtCache rt) {
		this.rt = rt;
		this.registry = registry;
	}

	@Override
	public void configure() throws Exception {
		
		Map<String, String> namespaceMap = new HashMap<>();
		namespaceMap.put("http://pbxevents.jjinterna.com/model", "");

		JSONProvider jsonProvider = new JSONProvider<>();
		jsonProvider.setNamespaceMap(namespaceMap);
		List providers = new ArrayList<>();
		providers.add(jsonProvider);

		registry.put("providers", providers);

		from("cxfrs:///rtcache?modelRef=RtCacheResource.xml&bindingStyle=Default&providers=#providers")
		.process(new Processor() {

			@Override
			public void process(Exchange exchange) throws Exception {
                Message inMessage = exchange.getIn();
                
				String operation = inMessage.getHeader("operationName", String.class);
				Object result = null;
				if ("getPhoneLine".equals(operation)) {
					result = rt.getPhoneLine(inMessage.getBody(String.class));
				} else if ("getPhoneLines".equals(operation)) {
					result = rt.getPhoneLines();
				} else if ("getPhone".equals(operation)) {
					result = rt.getPhone(inMessage.getBody(String.class));
				} else if ("getPhones".equals(operation)) {
					result = rt.getPhones();
				}
		        exchange.getOut().setBody(result);
			}
			
		});
	}

}
