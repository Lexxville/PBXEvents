package com.jjinterna.pbxevents.tcx.callrouter.internal;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

@Path("route.vxml")
public interface CallRouterResource {

	@GET
	@Produces("application/voicexml+xml")
	String route(@QueryParam("ani") String ani, @QueryParam("dnis") String dnis);

}
