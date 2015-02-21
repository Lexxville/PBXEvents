package com.jjinterna.pbxevents.routes.selector;

import com.jjinterna.pbxevents.model.PBXEvent;
import com.jjinterna.pbxevents.routes.EventSelector;

public class EventSourceSelector implements EventSelector {

	private final String eventSource;
	
	public EventSourceSelector(String eventSource) {
		this.eventSource = eventSource;
	}

	public String getEventSource() {
		return eventSource;
	}

	@Override
	public boolean select(PBXEvent event) {
		// TODO Auto-generated method stub
		return false;
	}


}
