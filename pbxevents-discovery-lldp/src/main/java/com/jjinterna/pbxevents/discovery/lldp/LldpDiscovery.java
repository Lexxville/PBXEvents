package com.jjinterna.pbxevents.discovery.lldp;

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

import com.jjinterna.pbxevents.discovery.lldp.internal.LldpDiscoveryRoute;
import com.jjinterna.pbxevents.routes.EventMediator;

@Component(description = LldpDiscovery.COMPONENT_DESCRIPTION, immediate = true, metatype = true, policy = ConfigurationPolicy.REQUIRE)
@Properties({
    @Property(name = "camelContextId", value = "pbxevents-lldp"),
    @Property(name = "active", value = "true"),
    @Property(name = "snmpCommunity", value = "public"),
    @Property(name = "snmpVersion", value = "0"),
    @Property(name = "port", value = "161"),
    @Property(name = "timeout", value = "1500"),
    @Property(name = "retries", value = "2"),
    @Property(name = "period", value = "60000")    
})
@References({
    @Reference(name = "camelComponent",referenceInterface = ComponentResolver.class,
        cardinality = ReferenceCardinality.MANDATORY_MULTIPLE, policy = ReferencePolicy.DYNAMIC,
        policyOption = ReferencePolicyOption.GREEDY, bind = "gotCamelComponent", unbind = "lostCamelComponent")
})
public class LldpDiscovery extends AbstractCamelRunner {

	public static final String COMPONENT_DESCRIPTION = "PBXEvents LLDP Discovery";

    @Reference
    private EventMediator mediator;
    
    @Override
    protected List<RoutesBuilder>getRouteBuilders() {
        List<RoutesBuilder>routesBuilders = new ArrayList<>();
        routesBuilders.add(new LldpDiscoveryRoute());
        routesBuilders.add(mediator.publisher());      
        return routesBuilders;
    }
    
}
