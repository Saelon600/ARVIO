package com.arflix.tv.data.repository

import android.content.Context
import androidx.datastore.preferences.core.mutablePreferencesOf
import androidx.datastore.preferences.core.stringPreferencesKey
import com.arflix.tv.data.api.TraktApi
import com.arflix.tv.data.model.CatalogConfig
import com.arflix.tv.data.model.CatalogSourceType
import com.google.common.truth.Truth.assertThat
import com.google.gson.Gson
import io.mockk.mockk
import okhttp3.OkHttpClient
import org.junit.Test

class CatalogVisibilityPersistenceTest {

    @Test
    fun hiddenCatalogKeepsItsPositionWhenVisibleCatalogsRefresh() {
        val persisted = listOf(
            catalog("trending"),
            catalog("sports", isVisible = false),
            catalog("just_added"),
        )
        val visibleRefresh = listOf(
            catalog("trending", title = "Trending updated"),
            catalog("just_added"),
            catalog("new_row"),
        )

        val merged = mergeCatalogRowsForPersistence(
            visibleCatalogs = visibleRefresh,
            persistedCatalogs = persisted,
        )

        assertThat(merged.map { it.id })
            .containsExactly("trending", "sports", "just_added", "new_row")
            .inOrder()
        assertThat(merged.first { it.id == "sports" }.isVisible).isFalse()
        assertThat(merged.first { it.id == "trending" }.title).isEqualTo("Trending updated")
    }

    @Test
    fun homeReadFiltersHiddenRowButSettingsReadKeepsIt() {
        val repository = CatalogRepository(
            context = mockk<Context>(relaxed = true),
            profileManager = mockk<ProfileManager>(relaxed = true),
            traktApi = mockk<TraktApi>(relaxed = true),
            okHttpClient = mockk<OkHttpClient>(relaxed = true),
            invalidationBus = mockk<CloudSyncInvalidationBus>(relaxed = true),
        )
        val prefs = mutablePreferencesOf(
            stringPreferencesKey("profile_test_catalogs_v1") to Gson().toJson(
                listOf(catalog("sports", isVisible = false))
            )
        )

        val homeRows = repository.invokeCatalogReader("readCatalogsFromPrefs", "test", prefs)
        val settingsRows = repository.invokeCatalogReader("readCatalogsForSettingsFromPrefs", "test", prefs)

        assertThat(homeRows).isEmpty()
        assertThat(settingsRows.map { it.id }).containsExactly("sports")
        assertThat(settingsRows.single().isVisible).isFalse()
    }

    private fun catalog(
        id: String,
        title: String = id,
        isVisible: Boolean? = null,
    ) = CatalogConfig(
        id = id,
        title = title,
        sourceType = CatalogSourceType.PREINSTALLED,
        isPreinstalled = true,
        isVisible = isVisible,
    )
}

@Suppress("UNCHECKED_CAST")
private fun CatalogRepository.invokeCatalogReader(
    methodName: String,
    profileId: String,
    prefs: androidx.datastore.preferences.core.Preferences,
): List<CatalogConfig> {
    val method = CatalogRepository::class.java.getDeclaredMethod(
        methodName,
        String::class.java,
        androidx.datastore.preferences.core.Preferences::class.java,
    )
    method.isAccessible = true
    return method.invoke(this, profileId, prefs) as List<CatalogConfig>
}
