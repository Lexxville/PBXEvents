package com.jjinterna.pbxevents.smtp;

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

import com.jjinterna.pbxevents.routes.EventMediator;
import com.jjinterna.pbxevents.routes.EventSelector;
import com.jjinterna.pbxevents.routes.selector.InstanceOfSelector;
import com.jjinterna.pbxevents.smtp.internal.SmtpActionRoute;


@Component(description = SmtpAction.COMPONENT_DESCRIPTION, immediate = true, metatype = true, policy = ConfigurationPolicy.REQUIRE)
@Properties({
	@Property(name = "active", value = "true"), 
	@Property(name = "eventClass", value = "PBXEvent"),
	@Property(name = "smtpUri"),
	@Property(name = "to"),
	@Property(name = "from"),
	@Property(name = "cc"),
	@Property(name = "bcc"),
	@Property(name = "completionSize", value = "1")
})
@References({ @Reference(name = "camelComponent", referenceInterface = ComponentResolver.class, 
cardinality = ReferenceCardinality.MANDATORY_MULTIPLE, policy = ReferencePolicy.DYNAMIC, 
policyOption = ReferencePolicyOption.GREEDY, bind = "gotCamelComponent", unbind = "lostCamelComponent") })
public class SmtpAction extends AbstractCamelRunner {

	public static final String COMPONENT_DESCRIPTION = "PBXEvents SMTP Action";

	@Reference
	private EventMediator mediator;

	private String eventClass;
	
	@Override
	protected List<RoutesBuilder> getRouteBuilders() {
		List<RoutesBuilder> routesBuilders = new ArrayList<>();
		List<EventSelector> selectors = new ArrayList<>();
		selectors.add(new InstanceOfSelector(eventClass));
		routesBuilders.add(mediator.subscriber(selectors));
		routesBuilders.add(new SmtpActionRoute());
		return routesBuilders;
	}

}