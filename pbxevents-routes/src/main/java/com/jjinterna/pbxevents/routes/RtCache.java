package com.jjinterna.pbxevents.routes;

import java.util.List;

import com.jjinterna.pbxevents.model.PBXEvent;

public interface RtCache {

	public enum RtCacheType {
		LINE,
		CALL,
		PHONE,
		CALLQUEUE
	}
	
	PBXEvent get(RtCacheType type, String key);
	List<PBXEvent> getAll(RtCacheType type);

}
