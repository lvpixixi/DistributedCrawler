package yaycrawler.cache.service;

import net.sf.ehcache.Ehcache;
import net.sf.ehcache.store.MemoryStoreEvictionPolicy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.stereotype.Component;

/**
 * Created by ucs_yuananyun on 2016/8/30.
 */
@Component
public class EhcacheCacheService implements ICacheService {

    @Autowired
    private EhCacheCacheManager cacheManager;

    @Override
    public Cache getOrCreateMemoryCache(String cacheName) {
        if (!cacheManager.getCacheManager().cacheExists(cacheName)) {
            Ehcache ehcache = new net.sf.ehcache.Cache(cacheName, 100000, MemoryStoreEvictionPolicy.LRU, false, "", false, 0, 0, false, 0, null);
            cacheManager.getCacheManager().addCache(ehcache);

        }
        return cacheManager.getCache(cacheName);
    }
}
