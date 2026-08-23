package com.arflix.tv.data.model

import com.google.common.truth.Truth.assertThat
import com.google.gson.Gson
import org.junit.Test

class CatalogVisibilityTest {

    @Test
    fun existingCatalogsRemainVisibleWhenVisibilityFieldIsMissing() {
        val legacyJson = """{"id":"sports","title":"Sports","sourceType":"PREINSTALLED"}"""
        val restored = Gson().fromJson(legacyJson, CatalogConfig::class.java)

        assertThat(restored.isVisibleOnHome).isTrue()
    }

    @Test
    fun explicitlyHiddenCatalogIsExcludedFromHomeRows() {
        val visible = catalog("trending", isVisible = true)
        val hidden = catalog("sports", isVisible = false)

        val homeRows = listOf(visible, hidden).filter { it.isVisibleOnHome }

        assertThat(homeRows.map { it.id }).containsExactly("trending")
    }

    @Test
    fun visibilitySurvivesGsonRoundTrip() {
        val hidden = catalog("sports", isVisible = false)

        val restored = Gson().fromJson(Gson().toJson(hidden), CatalogConfig::class.java)

        assertThat(restored.isVisibleOnHome).isFalse()
    }

    private fun catalog(id: String, isVisible: Boolean) = CatalogConfig(
        id = id,
        title = id,
        sourceType = CatalogSourceType.PREINSTALLED,
        isPreinstalled = true,
        isVisible = isVisible,
    )
}
