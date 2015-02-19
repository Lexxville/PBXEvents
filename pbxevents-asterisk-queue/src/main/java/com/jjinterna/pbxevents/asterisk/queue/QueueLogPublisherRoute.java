package com.jjinterna.pbxevents.asterisk.queue;

import org.apache.camel.Exchange;
import org.apache.camel.Predicate;
import org.apache.camel.processor.aggregate.AggregationStrategy;

import com.jjinterna.pbxevents.model.CallConnect;
import com.jjinterna.pbxevents.model.CallEnterQueue;
import com.jjinterna.pbxevents.routes.EventPublisherRoute;

public class QueueLogPublisherRoute extends EventPublisherRoute {
	
	@Override
	public void configure() {
		super.configure();
		
		AggregationStrategy callEventAggregationStrategy = new CallEventAggregationStrategy();
		from("direct:callCompleteRoute").id("callCompleteRoute")
		.aggregate(simple("${header.PBXDomain}-${header.PBXCallID}"), callEventAggregationStrategy)
			.completionTimeout(3600 * 1000)
			.eagerCheckCompletion()
			.completionPredicate(new Predicate() {
				
				@Override
				public boolean matches(Exchange exchange) {
					if (exchange.getIn().getBody() instanceof QueueLog) {
						QueueLog queueLog = (QueueLog) exchange.getIn().getBody();
						return queueLog.getVerb().equals(QueueLogType.COMPLETEAGENT) ||
								queueLog.getVerb().equals(QueueLogType.COMPLETECALLER);
					}
					return false;
				}
			})
		.to("direct:publish");
		
		from("direct:callConnectRoute").id("callConnectRoute")
		.aggregate(simple("${header.PBXDomain}-${header.PBXCallID}"), callEventAggregationStrategy)
			.completionTimeout(3600 * 1000)	
			.eagerCheckCompletion()
			.completionPredicate(new Predicate() {				
				@Override
				public boolean matches(Exchange exchange) {
					if (exchange.getIn().getBody() instanceof QueueLog) {
						QueueLog queueLog = (QueueLog) exchange.getIn().getBody();
						return queueLog.getVerb().equals(QueueLogType.ABANDON) ||
								queueLog.getVerb().equals(QueueLogType.EXITWITHTIMEOUT) ||
								queueLog.getVerb().equals(QueueLogType.CONNECT);
					}
					return false;
				}
			})
		.multicast()
			.to("direct:publish")
			.filter(header("PBXEvent").isEqualTo(CallConnect.class.getSimpleName())).to("direct:callCompleteRoute").end()
		.end();

		from("direct:callEnterQueueRoute").id("callEnterQueueRoute")
		.aggregate(simple("${header.PBXDomain}-${header.PBXCallID}"), callEventAggregationStrategy)
			.completionTimeout(60 * 1000)
			.completionPredicate(header("PBXEvent").isEqualTo(CallEnterQueue.class.getSimpleName()))
		.multicast()
			.to("direct:publish", "direct:callConnectRoute");

		from("direct:queueLogRoute").id("queueLogRoute")
		.choice()
			.when(header("PBXEvent").in(
					QueueLogType.DID,
					QueueLogType.ENTERQUEUE))
				.to("direct:callEnterQueueRoute").stop()
			.when(header("PBXEvent").in(
					QueueLogType.ABANDON,
					QueueLogType.CONNECT,
					QueueLogType.EXITWITHTIMEOUT,
					QueueLogType.RINGNOANSWER))
				.to("direct:callConnectRoute").stop()
			.when(header("PBXEvent").in(
					QueueLogType.COMPLETEAGENT,
					QueueLogType.COMPLETECALLER))
				.to("direct:callCompleteRoute").stop()
		.end();
	}
}
