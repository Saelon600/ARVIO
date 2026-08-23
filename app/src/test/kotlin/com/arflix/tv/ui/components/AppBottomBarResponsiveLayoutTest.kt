package com.arflix.tv.ui.components

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class AppBottomBarResponsiveLayoutTest {

    @Test
    fun landscapePhoneUsesCompactBottomBar() {
        val mode = appBottomBarMode(
            isTouchDevice = true,
            smallestScreenWidthDp = 411,
            screenWidthDp = 892,
            screenHeightDp = 360,
        )

        assertThat(mode).isEqualTo(AppBottomBarMode.LANDSCAPE_COMPACT)
    }

    @Test
    fun compactBottomBarKeepsTouchTargetsButUsesLessHeight() {
        val spec = appBottomBarSpec(AppBottomBarMode.LANDSCAPE_COMPACT)

        assertThat(spec.itemHeightDp).isEqualTo(48)
        assertThat(spec.iconSizeDp).isEqualTo(20)
        assertThat(spec.labelFontSizeSp).isEqualTo(8)
        assertThat(spec.totalBarHeightDp).isAtMost(53)
    }

    @Test
    fun portraitPhoneKeepsStandardBottomBar() {
        val mode = appBottomBarMode(
            isTouchDevice = true,
            smallestScreenWidthDp = 411,
            screenWidthDp = 411,
            screenHeightDp = 892,
        )

        assertThat(mode).isEqualTo(AppBottomBarMode.STANDARD)
    }

    @Test
    fun landscapeTabletKeepsStandardBottomBar() {
        val mode = appBottomBarMode(
            isTouchDevice = true,
            smallestScreenWidthDp = 700,
            screenWidthDp = 1280,
            screenHeightDp = 800,
        )

        assertThat(mode).isEqualTo(AppBottomBarMode.STANDARD)
    }
}
