package com.jjinterna.pbxevents.asterisk.queue;

import java.util.List;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.apache.camel.model.dataformat.CsvDataFormat;

import com.jjinterna.pbxevents.routes.PBXEventsRoute;
import com.jjinterna.pbxevents.routes.logfile.LogfileLifecycleStrategySupport;
import com.jjinterna.pbxevents.routes.logfile.LogfileMark;

public class QueueLogRoute extends PBXEventsRoute {

	@Override
	public void configure() throws Exception {
		
		String fileName = getContext().resolvePropertyPlaceholders("{{fileName}}");

		final LogfileMark queueLogMark = new LogfileMark("data/mark_" + getRouteId());
		final int mark = queueLogMark.getMark();

		from("stream:file?fileName={{fileName}}&scanStream=true&scanStreamDelay=1000").id(getRouteId())
		.unmarshal(new CsvDataFormat("|"))
		.process(new Processor() {
			@Override
			public void process(Exchange exchange) throws Exception {
		        Message in = exchange.getIn();
		        List<List<String>> csvData = (List<List<String>>) in.getBody();
		        List<String> fields = csvData.get(0);
		        for (int i = fields.size(); i<10; i++) {
		        	fields.add("");
		        }
		        
		        QueueLog queueLog = new QueueLog();
		        queueLog.setTimeId(Integer.parseInt(fields.get(0)));
		        queueLog.setCallId(fields.get(1));
		        queueLog.setQueue(fields.get(2));
		        queueLog.setAgent(fields.get(3));
		        queueLog.setVerb(QueueLogType.fromValue(fields.get(4)));
		        queueLog.setData1(fields.get(5));
		        queueLog.setData2(fields.get(6));
		        queueLog.setData3(fields.get(7));
		        queueLog.setData4(fields.get(8));
		        queueLog.setData5(fields.get(9));

		        in.setHeader("PBXDomain", getDomain());
		        in.setHeader("PBXCallID", queueLog.getCallId());
		        in.setHeader("PBXEvent", queueLog.getVerb().toString());
		        in.setHeader("PBXQueue", queueLog.getQueue());
		        
		        in.setBody(queueLog);

		        if (queueLog.getTimeId() <= mark) {
		        	exchange.setProperty(Exchange.ROUTE_STOP, Boolean.TRUE);
		        } else {
		        	try {
		        		queueLogMark.setMark(fields.get(0));
		        	}
		        	catch (Exception e) {}
		        }
			}
		})
		.to("direct:queueLogRoute?block=true");

		getContext().addLifecycleStrategy(new LogfileLifecycleStrategySupport(getRouteId(), fileName));
	}
}
