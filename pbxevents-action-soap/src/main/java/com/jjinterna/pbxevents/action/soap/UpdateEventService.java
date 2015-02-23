package com.jjinterna.pbxevents.action.soap;

import javax.jws.WebMethod;
import javax.jws.WebService;

import com.jjinterna.pbxevents.model.PBXEvent;

@WebService
public interface UpdateEventService {
    @WebMethod
    public void updateEvent(PBXEvent event);
}
