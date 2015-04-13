package com.jjinterna.pbxevents.tcx.callrouter.internal;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class RoutingResponse {

	String extension;
	String errorExtension;

	public String getExtension() {
		return extension;
	}
	public void setExtension(String extension) {
		this.extension = extension;
	}
	public String getErrorExtension() {
		return errorExtension;
	}
	public void setErrorExtension(String errorExtension) {
		this.errorExtension = errorExtension;
	}

}
