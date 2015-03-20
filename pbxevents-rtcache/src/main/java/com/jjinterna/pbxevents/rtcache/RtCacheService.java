package com.jjinterna.pbxevents.rtcache;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

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

import com.jjinterna.pbxevents.model.Phone;
import com.jjinterna.pbxevents.model.PhoneLine;
import com.jjinterna.pbxevents.model.QueueMemberEvent;
import com.jjinterna.pbxevents.model.QueuedCall;
import com.jjinterna.pbxevents.routes.EventMediator;
import com.jjinterna.pbxevents.routes.EventSelector;
import com.jjinterna.pbxevents.routes.RtCache;
import com.jjinterna.pbxevents.rtcache.internal.RtCacheResource;
import com.jjinterna.pbxevents.rtcache.internal.RtCacheRoute;

@Component(description = RtCacheService.COMPONENT_DESCRIPTION, immediate = true, metatype = true)
@Properties({
    @Property(name = "camelContextId", value = "pbxevents-rtcache"),
    @Property(name = "camelRouteId", value = "default"),
    @Property(name = "active", value = "true"),
    @Property(name = "rsEnable", value = "true"),
    @Property(name = "phoneTimeToLiveSeconds", value = "172800"),
    @Property(name = "lineTimeToLiveSeconds", value = "3600"),
    @Property(name = "queuedCallTimeToLiveSeconds", value = "3600"),
    @Property(name = "queueMemberTimeToLiveSeconds", value = "86400")    
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
    
    private Boolean rsEnable;
    
    @Override
    protected List<RoutesBuilder>getRouteBuilders() {
        List<RoutesBuilder>routesBuilders = new ArrayList<>();
        routesBuilders.add(new RtCacheRoute());
        routesBuilders.add(mediator.subscriber(Collections.<EventSelector> emptyList()));
        if (rsEnable) {
        	routesBuilders.add(new RtCacheResource(registry, this));
        }
        return routesBuilders;
    }

	private <T> T get(String key, Class<T> type) {
		CacheManager cacheManager = new DefaultCacheManagerFactory().getInstance();
		Ehcache cache = cacheManager.getCache(type.getName());
		Element element = cache.get(key);
		return (T) ((element != null) ? element.getObjectValue() : null);		

	}

	private <T> List<T> getAll(Class<T> type) {
		CacheManager cacheManager = new DefaultCacheManagerFactory().getInstance();
		Ehcache cache = cacheManager.getCache(type.getName());
		Map<Object, Element> map = cache.getAll(cache.getKeys());
		List<T> list = new ArrayList<T>();
		for (Map.Entry<Object, Element> entry : map.entrySet()) {
			list.add((T) entry.getValue().getObjectValue());
		}
		return list;
	}

	@Override
	public PhoneLine getPhoneLine(String key) {
		return get(key, PhoneLine.class);
	}

	@Override
	public List<PhoneLine> getPhoneLines() {
		return getAll(PhoneLine.class);
	}

	@Override
	public Phone getPhone(String key) {
		return get(key, Phone.class);
	}

	@Override
	public List<Phone> getPhones() {
		return getAll(Phone.class);
	}

	@Override
	public List<QueueMemberEvent> getQueueMembers() {
		return getAll(QueueMemberEvent.class);
	}

	@Override
	public QueuedCall getQueuedCall(String key) {
		return get(key, QueuedCall.class);
	}

	@Override
	public List<QueuedCall> getQueuedCalls() {
		return getAll(QueuedCall.class);
	}
    
}