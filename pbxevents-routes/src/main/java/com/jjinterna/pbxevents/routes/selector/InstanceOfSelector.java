package com.jjinterna.pbxevents.routes.selector;

import com.jjinterna.pbxevents.model.PBXEvent;
import com.jjinterna.pbxevents.routes.EventSelector;

public class InstanceOfSelector implements EventSelector {

	private final String eventClass;

	public InstanceOfSelector(String eventClass) {
		this.eventClass = eventClass; 
	}

	@Override
	public boolean select(PBXEvent event) {
		return event.getClass().getSimpleName().equals(eventClass) ||
				(event.getClass().getSuperclass() == null ?
						false : event.getClass().getSuperclass().getSimpleName().equals(eventClass)) ||
				(event.getClass().getSuperclass().getSuperclass() == null ? 
						false : event.getClass().getSuperclass().getSuperclass().getSimpleName().equals(eventClass));
	}

}
