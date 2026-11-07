package at.stefanirndorfer.mobtt.core.ratingscentral

import at.stefanirndorfer.mobtt.domain.rating.Rating
import at.stefanirndorfer.mobtt.domain.rating.RatingHistory
import at.stefanirndorfer.mobtt.domain.rating.RatingUpdate
import kotlinx.datetime.LocalDate

/** Ratings Central's names for the columns of its history export; an "event" there is our Rating update. */
private object Column {
    const val ID = "EventID"
    const val DATE = "EventDate"
    const val NAME = "EventName"
    const val INITIAL_MEAN = "InitialMean"
    const val INITIAL_DEVIATION = "InitialStDev"
    const val POINT_CHANGE = "PointChange"
    const val FINAL_MEAN = "FinalMean"
    const val FINAL_DEVIATION = "FinalStDev"

    val required = listOf(DATE, NAME, INITIAL_MEAN, INITIAL_DEVIATION, POINT_CHANGE, FINAL_MEAN, FINAL_DEVIATION)
}

/**
 * Reads Ratings Central's history export into Rating updates, oldest first. Columns are found by name, so extra or
 * reordered columns do no harm. Throws [IllegalArgumentException] for anything that is not that export, such as an HTML
 * page, or a row it cannot read.
 */
internal fun parseRatingHistoryCsv(text: String): RatingHistory {
    val lines = text.lineSequence().map { it.trimEnd('\r') }.filter { it.isNotBlank() }.toList()
    val header = lines.firstOrNull()?.let(::splitCsvLine).orEmpty()
    val missing = Column.required.filterNot { it in header }
    require(missing.isEmpty()) { "Not a Ratings Central history export: missing $missing" }
    val index = header.withIndex().associate { it.value to it.index }
    val rows = lines.drop(1).map { line ->
        val cells = splitCsvLine(line)
        fun cell(name: String) = cells.getOrNull(index.getValue(name)) ?: throw IllegalArgumentException("Short row in the Ratings Central export")
        fun number(name: String) = cell(name).trim().toInt()
        val id = index[Column.ID]?.let { cells.getOrNull(it)?.trim()?.toLongOrNull() }
        id to RatingUpdate(
            date = LocalDate.parse(cell(Column.DATE).trim()),
            name = cell(Column.NAME),
            before = Rating(number(Column.INITIAL_MEAN), number(Column.INITIAL_DEVIATION)),
            after = Rating(number(Column.FINAL_MEAN), number(Column.FINAL_DEVIATION)),
            pointChange = number(Column.POINT_CHANGE),
        )
    }
    // Ratings Central lists the newest first; two updates on one day keep the order of their event IDs.
    return RatingHistory(rows.sortedWith(compareBy({ it.second.date }, { it.first ?: 0L })).map { it.second })
}

/** One CSV line into its cells: commas inside double quotes stay, and a doubled quote is a quote. */
private fun splitCsvLine(line: String): List<String> {
    val cells = mutableListOf<String>()
    val cell = StringBuilder()
    var quoted = false
    var i = 0
    while (i < line.length) {
        val c = line[i]
        when {
            quoted && c == '"' && line.getOrNull(i + 1) == '"' -> {
                cell.append('"')
                i++
            }
            c == '"' -> quoted = !quoted
            c == ',' && !quoted -> {
                cells += cell.toString()
                cell.clear()
            }
            else -> cell.append(c)
        }
        i++
    }
    cells += cell.toString()
    return cells
}
