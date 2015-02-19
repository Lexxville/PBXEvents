package com.jjinterna.pbxevents.asterisk.messages;

import com.jjinterna.pbxevents.model.CallConnect;
import com.jjinterna.pbxevents.routes.EventPublisherRoute;

public class MessagesRouteBuilder extends EventPublisherRoute {

	@Override
	public void configure() {
		super.configure();
		
		CallConnect c = new CallConnect();
	}

}
