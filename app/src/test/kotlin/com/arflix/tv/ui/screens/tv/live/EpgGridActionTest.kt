package com.arflix.tv.ui.screens.tv.live

import com.arflix.tv.data.model.IptvNowNext
import com.arflix.tv.data.model.IptvProgram
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class EpgGridActionTest {

    @Test
    fun currentProgramKeepsMetadataForActionDialog() {
        val program = IptvProgram(
            title = "Live Movie",
            startUtcMillis = 1_000L,
            endUtcMillis = 2_000L,
        )

        assertThat(
            epgProgramActionTarget(
                program = program,
                isPast = false,
                isCatchupSupported = false,
            )
        ).isEqualTo(program)
    }

    @Test
    fun channelNameTapForwardsCurrentlyLiveProgramToDialog() {
        val program = IptvProgram(
            title = "Live Movie",
            startUtcMillis = 1_000L,
            endUtcMillis = 2_000L,
        )

        assertThat(
            channelRowActionProgram(
                guide = IptvNowNext(now = program),
                clockTickMillis = 1_500L,
            )
        ).isEqualTo(program)
    }
}
