package com.jjinterna.pbxevents.rtcache;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;

import org.apache.camel.RoutesBuilder;
import org.apache.camel.component.cache.DefaultCacheManagerFactory;
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
import org.apache.felix.scr.annotations.Service;

import com.jjinterna.pbxevents.model.PBXEvent;
import com.jjinterna.pbxevents.routes.EventMediator;
import com.jjinterna.pbxevents.routes.EventSelector;
import com.jjinterna.pbxevents.routes.RtCache;
import com.jjinterna.pbxevents.rtcache.internal.RtCacheRoute;

@Component(description = RtCacheService.COMPONENT_DESCRIPTION, immediate = true, metatype = true)
@Properties({
    @Property(name = "camelContextId", value = "pbxevents-rtcache"),
    @Property(name = "camelRouteId", value = "default"),
    @Property(name = "active", value = "true"),
    @Property(name = "phoneTimeToLiveSeconds", value = "10872"),
    @Property(name = "lineTimeToLiveSeconds", value = "300"),
    @Property(name =  "callQueueTimeToLiveSeconds", value = "3600")
})
@References({
    @Reference(name = "camelComponent",referenceInterface = ComponentResolver.class,
        cardinality = ReferenceCardinality.MANDATORY_MULTIPLE, policy = ReferencePolicy.DYNAMIC,
        policyOption = ReferencePolicyOption.GREEDY, bind = "gotCamelComponent", unbind = "lostCamelComponent")
})
@Service(value=RtCache.class)
public class RtCacheService extends AbstractCamelRunner implements RtCache {

	public static final String COMPONENT_DESCRIPTION = "PBXEvents Runtime Cache";

    @Reference
    private EventMediator mediator;
    
    @Override
    protected List<RoutesBuilder>getRouteBuilders() {
        List<RoutesBuilder>routesBuilders = new ArrayList<>();
        routesBuilders.add(new RtCacheRoute());
        routesBuilders.add(mediator.subscriber(Collections.<EventSelector> emptyList()));
        return routesBuilders;
    }

	@Override
	public PBXEvent get(RtCacheType type, String key) {
		CacheManager cacheManager = new DefaultCacheManagerFactory().getInstance();
		Ehcache cache = cacheManager.getCache(type.toString());
		Element element = cache.get(key);
		return (PBXEvent) ((element != null) ? element.getObjectValue() : null);		

	}

	@Override
	public List<PBXEvent> getAll(RtCacheType type) {
		// TODO Auto-generated method stub
		return null;
	}
    
}