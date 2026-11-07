package at.stefanirndorfer.mobtt.core.ratingscentral

internal object RatingsCentralSamples {
    const val HOST = "https://www.ratingscentral.com"

    /**
     * Rows of Ratings Central's history export (`PlayerHistory.php?PlayerID=163468&CSV_Output=Text`), recorded
     * 2026-11-07 and trimmed to three of its 21 rows. Newest first, as Ratings Central lists them; the oldest row starts
     * from the unrated default of 1450 ±450.
     */
    const val HISTORY_163468 = """EventID,EventDate,EventName,InitialMean,InitialStDev,PointChange,FinalMean,FinalStDev
62882,2026-10-25,"OÖTTV Meisterschaft 2026/27 (Spiele KW 43/2026)",447,66,72,519,86
62739,2026-10-18,"OÖTTV Meisterschaft 2026/27 (Spiele KW 42/2026)",400,57,47,447,65
57641,2025-10-19,"OÖTTV Meisterschaft 2025/26 (Spiele KW 42/2025)",1450,450,-1119,331,162
"""

    const val HISTORY_URL_163468 = "$HOST/PlayerHistory.php?PlayerID=163468&CSV_Output=Text"
}
