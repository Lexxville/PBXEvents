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
	Client client;
	private static final String REQUEST_ID = "variable_PBXEventsRequestId";
	
	public FreeSwitchEslRoute(Client client) {
		this.client = client;
	}
	
	@Override
	public void configure() throws Exception {
		checkProperties();
		
		producer = getContext().createProducerTemplate();
		
		client.addEventListener(this);
		
		from("direct:event")
		.process(new Processor() {

			@Override
			public void process(Exchange exchange) throws Exception {

				Map<String, String> eventHeaders = exchange.getIn().getBody(Map.class);

				exchange.getOut().setBody(null);

				if (eventHeaders.get(REQUEST_ID) == null) {					
					return;
				}

				String eventName = eventHeaders.get("Event-Name");
				TxFaxNegotiateResult result = null;
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
								return;
							}
						}
					}
				}
				if (result != null) {
					copy(eventHeaders, result);
					for (String key : eventHeaders.keySet()) {
						if (key.startsWith("variable_PBXEvents")) {
							exchange.getOut().setHeader(key.substring(9), eventHeaders.get(key));
						}
					}
					exchange.getOut().setBody(result);
				}
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

	private void copy(Map<String, String> eventHeaders, PBXEvent dest) {
		if (dest instanceof TxFaxNegotiateResult) {
			TxFaxNegotiateResult r = (TxFaxNegotiateResult) dest;
			r.setEventId(eventHeaders.get("Unique-ID"));
			String s = eventHeaders.get("Event-Date-Timestamp");
			if (s != null) {
				r.setEventDateTimestamp(Long.parseLong(s));
			}
			r.setFaxECMUsed("on".equals(eventHeaders.get("variable_fax_ecm_used")));
			r.setFaxLocalStationId(eventHeaders.get("variable_fax_local_station_id"));
			r.setFaxRemoteStationId(eventHeaders.get("variable_fax_remote_station_id"));
			r.setFaxTransferRate(eventHeaders.get("variable_fax_transfer_rate"));
			r.setLineUsed(eventHeaders.get("variable_sip_gateway_name"));
			r.setRemoteMediaIp(eventHeaders.get("variable_remote_media_ip"));
		}
		if (dest instanceof TxFaxPageResult) {
			TxFaxPageResult r = (TxFaxPageResult) dest;
			String s = eventHeaders.get("variable_fax_document_transferred_pages");
			if (s != null) {
				r.setFaxDocumentTransferredPages(Integer.parseInt(s));
			}
			r.setFaxImageResolution(eventHeaders.get("variable_fax_image_resolution"));
			s = eventHeaders.get("variable_fax_image_size");
			if (s != null) {
				r.setFaxImageSize(Integer.parseInt(s));
			}
			s = eventHeaders.get("variable_fax_bad_rows");
			if (s != null) {
				r.setFaxBadRows(Integer.parseInt(s));
			}
		}
		if (dest instanceof TxFaxResult) {
			TxFaxResult r = (TxFaxResult) dest;
			String s = eventHeaders.get("variable_fax_success");
			if (s != null) {
				r.setFaxSuccess(Integer.parseInt(s));
			}
			s = eventHeaders.get("variable_fax_result_code");
			if (s != null) {
				r.setFaxResultCode(Integer.parseInt(s));
			}
			r.setFaxResultText(eventHeaders.get("variable_fax_result_text"));
			s = eventHeaders.get("variable_fax_document_total_pages");
			if (s != null) {
				r.setFaxDocumentTotalPages(Integer.parseInt(s));
			}
			r.setHangupCause(eventHeaders.get("Hangup-Cause"));
		}
	}
	
	@Override
	public void backgroundJobResultReceived(EslEvent event) {
		
	}

	@Override
	public void eventReceived(EslEvent event) {
		producer.sendBody("direct:event", event.getEventHeaders());
	}

	public void checkProperties() {
		Validate.notNull(host, "host property is not set");
		Validate.notNull(port, "port property is not set");
		Validate.notNull(password, "password property is not set");
	}

}
