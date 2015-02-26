package com.jjinterna.pbxevents.hanewin.lldp;

import java.util.ArrayList;
import java.util.List;

import org.apache.camel.RoutesBuilder;
import org.apache.camel.scr.AbstractCamelRunner;
import org.apache.camel.spi.ComponentResolver;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.ReferenceCardinality;
import org.apache.felix.scr.annotations.ReferencePolicy;
import org.apache.felix.scr.annotations.ReferencePolicyOption;
import org.apache.felix.scr.annotations.References;

import com.jjinterna.pbxevents.hanewin.lldp.internal.HanewinLldpRoute;
import com.jjinterna.pbxevents.routes.EventMediator;

@Component(description = HanewinLldp.COMPONENT_DESCRIPTION, immediate = true, metatype = true)
@Properties({
    @Property(name = "camelContextId", value = "com.jjinterna.pbxevents.hanewin.lldp"),
    @Property(name = "camelRouteId", value = "default"),
    @Property(name = "active", value = "true"),
    @Property(name = "oidStr", value = "1.0.8802.1.1.2.1.4.1"),
    @Property(name = "community", value = "public"),
    @Property(name = "snmpVersion", value = "0"),
    @Property(name = "port", value = "161"),
    @Property(name = "timeout", value = "3000"),
    @Property(name = "retries", value = "0")
    
})
@References({
    @Reference(name = "camelComponent",referenceInterface = ComponentResolver.class,
        cardinality = ReferenceCardinality.MANDATORY_MULTIPLE, policy = ReferencePolicy.DYNAMIC,
        policyOption = ReferencePolicyOption.GREEDY, bind = "gotCamelComponent", unbind = "lostCamelComponent")
})
public class HanewinLldp extends AbstractCamelRunner {

	public static final String COMPONENT_DESCRIPTION = "PBXEvents Hanewin LLDP";

    @Reference
    private EventMediator mediator;

    @Override
    protected List<RoutesBuilder>getRouteBuilders() {
        List<RoutesBuilder>routesBuilders = new ArrayList<>();
        routesBuilders.add(new HanewinLldpRoute());
        return routesBuilders;
    }
    
}

