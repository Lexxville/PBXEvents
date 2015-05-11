package com.jjinterna.pbxevents.freeswitch.esl.internal;

import org.freeswitch.esl.client.inbound.InboundConnectionFailure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Heartbeat implements Runnable {
    private final Logger log = LoggerFactory.getLogger( this.getClass() );

    private EslConnection conn;
    private boolean shutdown = false;
    private Runnable clientConfigurer;

    Heartbeat(EslConnection conn, Runnable clientConfigurer) {
    	this.conn = conn;
        this.clientConfigurer = clientConfigurer;
    }

    public void shutdown() {
        this.shutdown = true;
    }

    public void gotHeartbeatEvent() {

    }
    
    @Override
    public void run() {
        while(!shutdown) {
            try {
            	String jobId = conn.getClient().sendAsyncApiCommand( "status", "" );
            	//log.info( "Job id [{}] for [status]", jobId );
            } catch (IllegalStateException is) {
                log.warn( "ISE: [{}]", is.getMessage());
                log.info( "Client connecting .." );
        		try {
        			conn.getClient().connect( conn.getHost(), conn.getPort(), conn.getPassword(), 2 );
            		log.info( "Client connected .." );
           			clientConfigurer.run();
        		} catch (InboundConnectionFailure e) {
                    log.warn( "Connect failed [{}]", e.getMessage() );	
        		}
            }
            if (!shutdown) {
                try {
					Thread.sleep(25000);
				} catch (InterruptedException e1) {}
            }
        }
    }

}

