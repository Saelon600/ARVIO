package com.arflix.tv.data.repository

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class IptvStartupRefreshPolicyTest {

    @Test
    fun missingCacheTriggersStartupPlaylistRefresh() {
        assertTrue(shouldRefreshStartupPlaylist(hasCachedChannels = false, cacheIsStale = false))
    }

    @Test
    fun staleCacheTriggersStartupPlaylistRefresh() {
        assertTrue(shouldRefreshStartupPlaylist(hasCachedChannels = true, cacheIsStale = true))
    }

    @Test
    fun freshCacheSkipsStartupPlaylistRefresh() {
        assertFalse(shouldRefreshStartupPlaylist(hasCachedChannels = true, cacheIsStale = false))
    }
}
