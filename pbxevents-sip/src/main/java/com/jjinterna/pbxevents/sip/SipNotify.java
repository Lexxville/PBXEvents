package com.jjinterna.pbxevents.sip;

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
import com.jjinterna.pbxevents.sip.internal.SipNotifyRoute;

@Component(description = SipNotify.COMPONENT_DESCRIPTION, immediate = true, metatype = true, policy = ConfigurationPolicy.REQUIRE)
@Properties({
    @Property(name = "camelContextId", value = "pbxevents-sip"),
    @Property(name = "active", value = "true"),
    @Property(name = "transport", value="udp"),    
    @Property(name = "maxForwards", value = "0"),
    @Property(name = "toUser"),
    @Property(name = "toHost", value = "localhost"),
    @Property(name = "toPort", value = "5060"),
    @Property(name = "fromUser"),
    @Property(name = "fromHost"),
    @Property(name = "fromPort")

})
@References({
    @Reference(name = "camelComponent",referenceInterface = ComponentResolver.class,
        cardinality = ReferenceCardinality.MANDATORY_MULTIPLE, policy = ReferencePolicy.DYNAMIC,
        policyOption = ReferencePolicyOption.GREEDY, bind = "gotCamelComponent", unbind = "lostCamelComponent")
})
public class SipNotify extends AbstractCamelRunner {

	public static final String COMPONENT_DESCRIPTION = "PBXEvents SIP Publisher";

    @Reference
    private EventMediator mediator;
    
    @Override
    protected List<RoutesBuilder>getRouteBuilders() {
    	context.getManagementNameStrategy().setNamePattern("#name#");
        List<RoutesBuilder>routesBuilders = new ArrayList<>();
        routesBuilders.add(new SipNotifyRoute(registry));
        routesBuilders.add(mediator.publisher());      
        return routesBuilders;
    }
    
}

