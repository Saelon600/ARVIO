package com.arflix.tv.ui.screens.tv.live

import com.arflix.tv.data.model.MediaItem
import com.arflix.tv.data.model.MediaType
import com.arflix.tv.data.model.IptvProgram

private val NON_VOD_EPG_CHANNEL_TERMS = setOf(
    "sport",
    "sports",
    "football",
    "soccer",
    "basketball",
    "tennis",
    "motorsport",
    "racing",
    "rugby",
    "hockey",
    "baseball",
    "boxing",
    "ufc",
    "mma",
    "cricket",
    "golf",
    "nfl",
    "nba",
    "mlb",
    "nhl",
    "f1",
    "news",
    "weather",
    "shopping",
    "teleshopping",
)

internal enum class EpgProgramSelectionOutcome {
    PlayLive,
    PlayCatchup,
    ShowDialog,
}

internal fun epgProgramSelectionOutcome(
    isPastPlayable: Boolean,
    channelAllowsVod: Boolean,
    vodMatch: MediaItem?,
): EpgProgramSelectionOutcome = when {
    channelAllowsVod && vodMatch != null -> EpgProgramSelectionOutcome.ShowDialog
    isPastPlayable -> EpgProgramSelectionOutcome.PlayCatchup
    else -> EpgProgramSelectionOutcome.PlayLive
}

internal fun epgCatchupPlaybackProgram(selectedProgram: IptvProgram): IptvProgram = selectedProgram

internal class EpgVodLookupGuard {
    private var generation = 0

    fun beginLookup(): Int = ++generation

    fun invalidate() {
        generation++
    }

    fun isCurrent(lookupGeneration: Int): Boolean = lookupGeneration == generation
}

internal fun epgChannelAllowsVodSearch(
    channelName: String,
    channelGroup: String,
): Boolean {
    val channelTokens = "$channelName $channelGroup"
        .lowercase()
        .split(Regex("[^a-z0-9]+"))
        .filterTo(mutableSetOf()) { it.isNotBlank() }
    return channelTokens.none { it in NON_VOD_EPG_CHANNEL_TERMS }
}

private val EPG_TITLE_YEAR_SUFFIX = Regex("""\s*\(?\b(?:19|20)\d{2}\b\)?\s*$""")
private val EPG_YEAR_HINT = Regex("""\b(?:19|20)\d{2}\b""")
private val EPG_TITLE_NON_ALPHANUMERIC = Regex("""[^a-z0-9]+""")

private fun normalizedEpgVodTitle(title: String): String = title
    .trim()
    .replace(EPG_TITLE_YEAR_SUFFIX, "")
    .lowercase()
    .replace("&", "and")
    .replace(EPG_TITLE_NON_ALPHANUMERIC, "")

internal fun selectConfidentEpgVodMatch(
    programTitle: String,
    results: List<MediaItem>,
    programDescription: String? = null,
): MediaItem? {
    val expectedTitle = normalizedEpgVodTitle(programTitle)
    if (expectedTitle.length < 2) return null
    val exactMatches = results.filter { candidate ->
        candidate.mediaType in setOf(MediaType.MOVIE, MediaType.TV) &&
            normalizedEpgVodTitle(candidate.title) == expectedTitle
    }
    val yearHint = EPG_YEAR_HINT.find("$programTitle ${programDescription.orEmpty()}")?.value
    return if (yearHint != null) {
        exactMatches.firstOrNull { it.year == yearHint }
    } else {
        exactMatches.firstOrNull()
    }
}
