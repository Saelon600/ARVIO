package com.arflix.tv.ui.screens.settings

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class SettingsSubtitleFontRowsTest {

    @Test
    fun subtitleFontAndDefaultStartupPageUseDistinctRowIds() {
        val subtitleRows = tvGeneralRowsForSection("subtitles")
        val profileRows = tvGeneralRowsForSection("profiles")

        assertThat(subtitleRows).contains(43)
        assertThat(profileRows).contains(42)
        assertThat(subtitleRows).doesNotContain(42)
        assertThat(profileRows).doesNotContain(43)
    }

    @Test
    fun fontAppearsAfterSubtitleStyleAndBeforeStylizedToggle() {
        assertThat(tvGeneralRowsForSection("subtitles"))
            .containsExactly(4, 5, 6, 7, 43, 8, 38, 39, 9)
            .inOrder()
    }
}
