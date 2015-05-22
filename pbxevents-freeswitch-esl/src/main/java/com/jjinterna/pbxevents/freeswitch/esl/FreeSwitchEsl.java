package com.jjinterna.pbxevents.freeswitch.esl;

import java.util.ArrayList;
import java.util.List;

import org.apache.camel.RoutesBuilder;
import org.apache.camel.scr.AbstractCamelRunner;
import org.apache.camel.spi.ComponentResolver;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.ConfigurationPolicy;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.ReferenceCardinality;
import org.apache.felix.scr.annotations.ReferencePolicy;
import org.apache.felix.scr.annotations.ReferencePolicyOption;
import org.apache.felix.scr.annotations.References;
import org.apache.felix.scr.annotations.Service;
import org.freeswitch.esl.client.inbound.Client;

import com.jjinterna.pbxevents.freeswitch.esl.internal.FreeSwitchEslRoute;
import com.jjinterna.pbxevents.routes.EventMediator;
import com.jjinterna.pbxevents.routes.TelephonyService;


@Component(description = FreeSwitchEsl.COMPONENT_DESCRIPTION, immediate = true, metatype = true, policy = ConfigurationPolicy.REQUIRE)
@Properties({
		@Property(name = "camelContextId", value = "pbxevents-freeswitch-esl"),
		@Property(name = "camelRouteId", value = "default"),
		@Property(name = "active", value = "true"),
		@Property(name = "host", value = "localhost"),
		@Property(name = "port", value = "8021"),
		@Property(name = "password", value = "ClueCon") })
@References({ @Reference(name = "camelComponent", referenceInterface = ComponentResolver.class, 
	cardinality = ReferenceCardinality.MANDATORY_MULTIPLE, policy = ReferencePolicy.DYNAMIC, 
	policyOption = ReferencePolicyOption.GREEDY, bind = "gotCamelComponent", unbind = "lostCamelComponent") })
@Service(value=TelephonyService.class)
public class FreeSwitchEsl extends AbstractCamelRunner implements TelephonyService {

	public static final String COMPONENT_DESCRIPTION = "PBXEvents FreeSwitch ESL support";

	@Reference
	private EventMediator mediator;

	Client client = new Client();
	
	@Override
	protected List<RoutesBuilder> getRouteBuilders() {
		List<RoutesBuilder> routesBuilders = new ArrayList<>();
		routesBuilders.add(new FreeSwitchEslRoute(client));
		if (mediator != null) {
			routesBuilders.add(mediator.publisher());
			routesBuilders.add(mediator.subscriber(getContext()));
		}
		return routesBuilders;
	}

	@Override
	public synchronized String sendAsyncApiCommand(String application, String data) {
		return client.sendAsyncApiCommand(application, data);
	}

}
