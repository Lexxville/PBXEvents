package com.jjinterna.pbxevents.freeswitch.esl.internal;

import org.freeswitch.esl.client.inbound.Client;

public class EslConnection {
	
	String host;
	Integer port;
	String password;
	
	Client client;

	public EslConnection(Client client, String host, Integer port, String password) {
		this.client = client;
		this.host = host;
		this.port = port;
		this.password = password;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public Integer getPort() {
		return port;
	}

	public void setPort(Integer port) {
		this.port = port;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Client getClient() {
		return client;
	}

	public void setClient(Client client) {
		this.client = client;
	}

}
