package com.jjinterna.pbxevents.freeswitch.esl.internal;

import org.apache.camel.CamelContext;
import org.apache.camel.VetoCamelContextStartException;
import org.apache.camel.support.LifecycleStrategySupport;
import org.apache.log4j.Logger;

public class EslStrategySupport extends LifecycleStrategySupport {

	Heartbeat heartbeat;
	Thread thread;
	EslConnection conn;
	Logger log = Logger.getLogger(EslStrategySupport.class);
	
	public EslStrategySupport(EslConnection conn, Runnable clientConfigurer) {
		this.conn = conn;
		heartbeat = new Heartbeat(conn, clientConfigurer);
		thread = new Thread(heartbeat);
	}
	
	@Override
	public void onContextStart(CamelContext context) throws VetoCamelContextStartException {
		thread.start();
	}

    @Override
    public void onContextStop(CamelContext context) {
		try {
			heartbeat.shutdown();
			thread.interrupt();
			conn.getClient().close();
		}
		catch (Exception e) {
			log.error("onContextStop", e);
		}
    }

}
