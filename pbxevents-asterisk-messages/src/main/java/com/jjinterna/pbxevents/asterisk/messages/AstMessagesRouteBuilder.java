package com.jjinterna.pbxevents.asterisk.messages;

import org.apache.camel.builder.RouteBuilder;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Service;

import com.jjinterna.pbxevents.routes.PublisherRouteBuilder;

@Component(name="PBXEvents Asterisk PBX Messages Publisher")
@Service(value=com.jjinterna.pbxevents.routes.PublisherRouteBuilder.class)
public class AstMessagesRouteBuilder implements PublisherRouteBuilder {

	@Override
	public RouteBuilder build() {
		// TODO Auto-generated method stub
		return null;
	}
}
