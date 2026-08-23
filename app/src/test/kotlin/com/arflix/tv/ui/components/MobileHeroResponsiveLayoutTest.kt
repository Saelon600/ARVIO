package com.arflix.tv.ui.components

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class MobileHeroResponsiveLayoutTest {

    @Test
    fun landscapePhoneUsesCompactHero() {
        val mode = mobileHeroLayoutMode(
            isTouchDevice = true,
            smallestScreenWidthDp = 411,
            screenWidthDp = 892,
            screenHeightDp = 360,
        )

        assertThat(mode).isEqualTo(MobileHeroLayoutMode.LANDSCAPE_COMPACT)
    }

    @Test
    fun compactHeroLeavesRoomForContentRows() {
        val spec = mobileHeroLayoutSpec(MobileHeroLayoutMode.LANDSCAPE_COMPACT)

        assertThat(spec.cardHeightDp).isEqualTo(124)
        assertThat(spec.headerAvatarSizeDp).isEqualTo(30)
        assertThat(spec.logoHeightDp).isAtMost(36)
        assertThat(spec.overlayBottomPaddingDp).isAtMost(10)
    }

    @Test
    fun portraitPhoneKeepsPosterHero() {
        val mode = mobileHeroLayoutMode(
            isTouchDevice = true,
            smallestScreenWidthDp = 411,
            screenWidthDp = 411,
            screenHeightDp = 892,
        )

        assertThat(mode).isEqualTo(MobileHeroLayoutMode.PORTRAIT_POSTER)
    }

    @Test
    fun landscapeTabletKeepsPosterHero() {
        val mode = mobileHeroLayoutMode(
            isTouchDevice = true,
            smallestScreenWidthDp = 700,
            screenWidthDp = 1280,
            screenHeightDp = 800,
        )

        assertThat(mode).isEqualTo(MobileHeroLayoutMode.PORTRAIT_POSTER)
    }
}
