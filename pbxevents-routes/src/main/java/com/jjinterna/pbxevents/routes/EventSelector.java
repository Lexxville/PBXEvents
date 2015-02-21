package com.jjinterna.pbxevents.routes;

import com.jjinterna.pbxevents.model.PBXEvent;

public interface EventSelector {

	public boolean select(PBXEvent event);

}
