package com.arflix.tv.ui.screens.tv.live

import com.arflix.tv.data.model.IptvProgram
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class FullscreenGuideActionTest {

    @Test
    fun liveProgramKeepsMetadataForActionDialog() {
        val program = IptvProgram(
            title = "The Lord of the Rings",
            startUtcMillis = 1_000L,
            endUtcMillis = 2_000L,
        )

        assertThat(guideProgramActionTarget(GuideProgramState.Live, program))
            .isEqualTo(program)
    }
}
