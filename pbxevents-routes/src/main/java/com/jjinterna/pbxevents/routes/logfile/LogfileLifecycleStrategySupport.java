package com.jjinterna.pbxevents.routes.logfile;

import org.apache.camel.CamelContext;
import org.apache.camel.VetoCamelContextStartException;
import org.apache.camel.support.LifecycleStrategySupport;

public class LogfileLifecycleStrategySupport extends LifecycleStrategySupport {

	private CamelContext context;
	private Thread watcher;
	
	public LogfileLifecycleStrategySupport(final String routeId, String fileName) {
		watcher = new Thread(new LogfileWatcher(fileName) {
				@Override
				public void onEntryDelete() throws Exception {
					context.stopRoute(routeId);
				}
				@Override
				public void onEntryCreate() throws Exception {
					context.startRoute(routeId);
				}
			}, fileName);
	}

	@Override
	public void onContextStart(CamelContext context) throws VetoCamelContextStartException {
		this.context = context;
		watcher.start();
	}

    @Override
    public void onContextStop(CamelContext context) {
    	watcher.interrupt();
    }

}
