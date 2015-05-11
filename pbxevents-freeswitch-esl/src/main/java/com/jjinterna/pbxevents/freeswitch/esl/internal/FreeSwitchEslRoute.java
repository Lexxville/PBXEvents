package com.jjinterna.pbxevents.freeswitch.esl.internal;

import java.util.Map;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.commons.lang.Validate;
import org.freeswitch.esl.client.IEslEventListener;
import org.freeswitch.esl.client.inbound.Client;
import org.freeswitch.esl.client.transport.event.EslEvent;

import com.jjinterna.pbxevents.model.PBXEvent;
import com.jjinterna.pbxevents.model.TxFaxNegotiateResult;
import com.jjinterna.pbxevents.model.TxFaxPageResult;
import com.jjinterna.pbxevents.model.TxFaxResult;

public class FreeSwitchEslRoute extends RouteBuilder implements IEslEventListener {

	String host;
	Integer port;
	String password;
	ProducerTemplate producer;
	
	@Override
	public void configure() throws Exception {
		checkProperties();
		
		producer = getContext().createProducerTemplate();
		
		final Client client = new Client();
		client.addEventListener(this);
		
		from("direct:start")
		.process(new Processor() {

			@Override
			public void process(Exchange exchange) throws Exception {
				EslEvent event = exchange.getIn().getBody(EslEvent.class);
				
				String eventName = event.getEventName();
				Map<String, String> eventHeaders = event.getEventHeaders();

				PBXEvent result = null;
				if (eventName.equals("CUSTOM")) {
					String es = eventHeaders.get("Event-Subclass");
					if (es.equals("spandsp::txfaxpageresult")) {
						result = new TxFaxPageResult();
					}
					else if (es.equals("spandsp::txfaxnegociateresult")) {
						result = new TxFaxNegotiateResult();
					}
					else if (es.equals("spandsp::txfaxresult") && 
							eventHeaders.get("Hangup-Cause") != null) {
						// ako fax_result_code = 49, to ne se generira channel_hangup s fax_result_code
						// vyzmojno e i za drugi kodove
						// zatova ne se sledi stoinostta na koda, a nalichie na hangup-cause
						result = new TxFaxResult();
					}
				}
				else if (eventName.equals("CHANNEL_HANGUP")) {
					String leg = eventHeaders.get("variable_loopback_leg");
					if ("A".equals(leg)) {			
						log.info("Received hangup event {}", "requestId");				
						String hangupCause = eventHeaders.get("Hangup-Cause");
						// ako ne e otgovoreno
						if ("0".equals(eventHeaders.get("Caller-Channel-Answered-Time")) &&
								"NORMAL_CLEARING".equals(hangupCause)) {
							log.info("Set {} to NO_ANSWER", "requestId");
							eventHeaders.put("Hangup-Cause", "NO_ANSWER");
						}
						result = new TxFaxResult();
						if (eventHeaders.get("variable_fax_success") == null) {
							// prodyljava 
							if ("CS_EXECUTE".equals(eventHeaders.get("Channel-State")) ||
								// monitoring-a pokaza, che pri tezi states obajdaneto prodyljava normalno
								"CS_HANGUP".equals(eventHeaders.get("Channel-State")) ||
								"CS_REPORTING".equals(eventHeaders.get("Channel-State"))) {
								result = null;
							}
						}
					}
				}
				exchange.getOut().setBody(result);
			}
			
		})
		.filter(body().isNotNull())
		.to("direct:publish");
		
		getContext().addLifecycleStrategy(new EslStrategySupport(
				new EslConnection(client, host, port, password),
				new Runnable() {
					@Override
					public void run() {
						client.cancelEventSubscriptions();
						if (!client.setEventSubscriptions("plain", "all").isOk()/* || 
				            !client.addEventFilter("Event-Name", "channel_hangup").isOk() ||
				            !client.addEventFilter("Event-Subclass", "spandsp::txfaxnegociateresult").isOk() ||
				            !client.addEventFilter("Event-Subclass", "spandsp::txfaxpageresult").isOk() ||
				            !client.addEventFilter("Event-Subclass", "spandsp::txfaxresult").isOk()*/) {
							client.close();
						}
					}
				}
		));
	}

	@Override
	public void backgroundJobResultReceived(EslEvent event) {
		
	}

	@Override
	public void eventReceived(EslEvent event) {
		producer.sendBody("direct:start", event);
	}

	public void checkProperties() {
		Validate.notNull(host, "host property is not set");
		Validate.notNull(port, "port property is not set");
		Validate.notNull(password, "password property is not set");
	}

}
