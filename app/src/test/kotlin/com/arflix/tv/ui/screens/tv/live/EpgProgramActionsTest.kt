package com.arflix.tv.ui.screens.tv.live

import com.arflix.tv.data.model.IptvProgram
import com.arflix.tv.data.model.MediaItem
import com.arflix.tv.data.model.MediaType
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class EpgProgramActionsTest {

    @Test
    fun watchLiveClearsSelectedProgramSoPlaybackUsesLiveStream() {
        val selectedProgram = IptvProgram(
            title = "Live Movie",
            startUtcMillis = 1_000L,
            endUtcMillis = 2_000L,
        )

        assertThat(epgWatchLivePlaybackProgram(selectedProgram)).isNull()
    }

    @Test
    fun pastPlayableProgramStartsCatchupWhenVodIsUnavailable() {
        val selectedProgram = IptvProgram(
            title = "Yesterday's Match",
            startUtcMillis = 1_000L,
            endUtcMillis = 2_000L,
        )

        assertThat(
            epgProgramSelectionOutcome(
                isPastPlayable = true,
                channelAllowsVod = false,
                vodMatch = null,
            )
        ).isEqualTo(EpgProgramSelectionOutcome.PlayCatchup)
        assertThat(epgCatchupPlaybackProgram(selectedProgram)).isEqualTo(selectedProgram)
    }

    @Test
    fun pastPlayableProgramWithVodMatchShowsDialogWithCatchupAvailable() {
        val movie = MediaItem(id = 7, title = "Yesterday's Movie", mediaType = MediaType.MOVIE)

        assertThat(
            epgProgramSelectionOutcome(
                isPastPlayable = true,
                channelAllowsVod = true,
                vodMatch = movie,
            )
        ).isEqualTo(EpgProgramSelectionOutcome.ShowDialog)
    }

    @Test
    fun sportsChannelDoesNotOfferVodSearch() {
        assertThat(
            epgChannelAllowsVodSearch(
                channelName = "Sky Sports Main Event",
                channelGroup = "UK Sports",
            )
        ).isFalse()
    }

    @Test
    fun onlyMatchingMovieOrSeriesTitleIsEligibleForStreaming() {
        val unrelated = MediaItem(id = 1, title = "Football Highlights", mediaType = MediaType.TV)
        val exactMovie = MediaItem(id = 2, title = "The Lord of the Rings", year = "2001", mediaType = MediaType.MOVIE)

        assertThat(
            selectConfidentEpgVodMatch(
                programTitle = "The Lord of the Rings (2001)",
                results = listOf(unrelated, exactMovie),
            )
        ).isEqualTo(exactMovie)
    }

    @Test
    fun spidermanDoesNotSelectBrandNewDay() {
        val brandNewDay = MediaItem(
            id = 969681,
            title = "Spider-Man: Brand New Day",
            year = "2026",
            mediaType = MediaType.MOVIE,
        )
        val original = MediaItem(
            id = 557,
            title = "Spider-Man",
            year = "2002",
            mediaType = MediaType.MOVIE,
        )

        assertThat(
            selectConfidentEpgVodMatch(
                programTitle = "Spiderman",
                results = listOf(brandNewDay, original),
            )
        ).isEqualTo(original)
    }

    @Test
    fun descriptionYearDisambiguatesSameTitleRemakes() {
        val animatedSeries = MediaItem(
            id = 888,
            title = "Spider-Man",
            year = "1994",
            mediaType = MediaType.TV,
        )
        val originalMovie = MediaItem(
            id = 557,
            title = "Spider-Man",
            year = "2002",
            mediaType = MediaType.MOVIE,
        )

        assertThat(
            selectConfidentEpgVodMatch(
                programTitle = "Spider-Man",
                programDescription = "The 2002 superhero film starring Tobey Maguire.",
                results = listOf(animatedSeries, originalMovie),
            )
        ).isEqualTo(originalMovie)
    }

    @Test
    fun conflictingYearRejectsOtherwiseExactTitle() {
        val remake = MediaItem(
            id = 609,
            title = "The Thing",
            year = "2011",
            mediaType = MediaType.MOVIE,
        )

        assertThat(
            selectConfidentEpgVodMatch(
                programTitle = "The Thing (1982)",
                results = listOf(remake),
            )
        ).isNull()
    }

    @Test
    fun dialogIsShownOnlyWhenVodMatchExists() {
        val movie = MediaItem(id = 557, title = "Spider-Man", mediaType = MediaType.MOVIE)

        assertThat(epgProgramSelectionOutcome(isPastPlayable = false, channelAllowsVod = false, vodMatch = null))
            .isEqualTo(EpgProgramSelectionOutcome.PlayLive)
        assertThat(epgProgramSelectionOutcome(isPastPlayable = false, channelAllowsVod = true, vodMatch = null))
            .isEqualTo(EpgProgramSelectionOutcome.PlayLive)
        assertThat(epgProgramSelectionOutcome(isPastPlayable = false, channelAllowsVod = true, vodMatch = movie))
            .isEqualTo(EpgProgramSelectionOutcome.ShowDialog)
    }

    @Test
    fun invalidatedVodLookupCannotPublishItsResult() {
        val guard = EpgVodLookupGuard()
        val staleLookup = guard.beginLookup()

        guard.invalidate()

        assertThat(guard.isCurrent(staleLookup)).isFalse()
        assertThat(guard.isCurrent(guard.beginLookup())).isTrue()
    }
}
