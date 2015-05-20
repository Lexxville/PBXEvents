package com.jjinterna.pbxevents.action.smtp;

import java.util.ArrayList;
import java.util.List;

import org.apache.camel.RoutesBuilder;
import org.apache.camel.builder.RouteBuilder;
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

@Component(description = SmtpAction.COMPONENT_DESCRIPTION, immediate = true, metatype = true, policy = ConfigurationPolicy.REQUIRE)
@Properties({
	@Property(name = "camelContextId", value = "pbxevents-smtp"),
	@Property(name = "camelRouteId", value = "default"),
	@Property(name = "active", value = "true"), 
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

	private String camelRouteId;
	private Integer completionSize;
	private Long completionInterval;
	private String to;
	private String from;
	private String cc;
	private String bcc;
	private String smtpUri;
	
	@Override
	protected List<RoutesBuilder> getRouteBuilders() {
		final String fromUri = mediator.queueUri(getContext().getName() + "-" + camelRouteId);
		
		List<RoutesBuilder> routesBuilders = new ArrayList<>();
		routesBuilders.add(new RouteBuilder() {

			@Override
			public void configure() throws Exception {
				from(fromUri)
				//.aggregate(header("PBXEventId")).completionSize(completionSize)//.completionInterval(completionInterval)
				.recipientList(simple("{{smtpUri}}?to={{to}}&from={{from}}&cc={{cc}}&bcc={{bcc}}"));
			}
			
		});

		return routesBuilders;
	}

}