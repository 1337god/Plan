package main.java.com.djrapitops.plan.data.cache;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import main.java.com.djrapitops.plan.ui.webserver.response.Response;

import java.util.Map;
import java.util.function.Predicate;

/**
 * This class contains the page cache.
 * <p>
 * It caches all Responses with their matching identifiers.
 * This reduces CPU cycles and the time to wait for loading the pages.
 * This is especially useful in situations where multiple clients are accessing the server.
 * <p>
 * This cache uses the Google Guava {@link Cache}.
 *
 * @author Fuzzlemann
 * @since 3.6.0
 */
public class PageCacheHandler {

    private static final Cache<String, Response> pageCache = CacheBuilder.newBuilder()
            .build();

    /**
     * Constructor used to hide the public constructor
     */
    private PageCacheHandler() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Loads the page from the page cache.
     * <p>
     * If the {@link Response} isn't cached, {@link PageLoader#createResponse()} in the {@code loader}
     * is called to create the Response.
     * <p>
     * If the Response is created, it's automatically cached.
     *
     * @param identifier The identifier of the page
     * @param loader     The {@link PageLoader} (How should it load the page if it's not cached)
     * @return The Response that was cached or created by the {@link PageLoader loader}
     */
    public static Response loadPage(String identifier, PageLoader loader) {
        Response response = pageCache.getIfPresent(identifier);

        if (response != null) {
            return response;
        }

        response = loader.createResponse();

        pageCache.put(identifier, response);

        return response;
    }

    /**
     * Puts the page into the page cache.
     * <p>
     * If the cache already inherits that {@code identifier}, it's renewed.
     *
     * @param identifier The identifier of the page
     * @param loader     The {@link PageLoader} (How it should load the page)
     */
    public static void cachePage(String identifier, PageLoader loader) {
        Response response = loader.createResponse();
        pageCache.put(identifier, response);
    }

    /**
     * Checks if the page is cached.
     *
     * @param identifier The identifier of the page
     * @return true if the page is cached
     */
    public static boolean isCached(String identifier) {
        return pageCache.asMap().containsKey(identifier);
    }

    /**
     * Removes all of the elements of this cache that satisfy the given predicate.
     *
     * @param filter a predicate which returns true for entries to be removed
     */
    public static void removeIf(Predicate<String> filter) {
        Map<String, Response> pageCacheMap = pageCache.asMap();

        for (String identifier : pageCacheMap.keySet()) {
            if (filter.test(identifier)) {
                pageCache.invalidate(identifier);
            }
        }
    }

    /**
     * Clears the cache from all its contents.
     */
    public static void clearCache() {
        pageCache.invalidateAll();
    }
}
