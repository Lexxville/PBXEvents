package com.jjinterna.pbxevents.routes.selector;

import com.jjinterna.pbxevents.model.PBXEvent;
import com.jjinterna.pbxevents.routes.EventSelector;

public class EventTypeSelector implements EventSelector {

	private final String eventType;

	public EventTypeSelector(String eventType) {
		this.eventType = eventType;
	}

	public String getEventType() {
		return eventType;
	}

	@Override
	public boolean select(PBXEvent event) {
		return event.getClass().getSimpleName().equals(eventType);
	}

	
}
