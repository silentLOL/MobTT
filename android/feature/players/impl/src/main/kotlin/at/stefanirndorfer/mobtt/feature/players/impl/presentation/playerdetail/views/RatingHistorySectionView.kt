package at.stefanirndorfer.mobtt.feature.players.impl.presentation.playerdetail.views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import at.stefanirndorfer.mobtt.core.localization.R
import at.stefanirndorfer.mobtt.core.ui.section.LazySectionView
import at.stefanirndorfer.mobtt.core.ui.section.SectionMessage
import at.stefanirndorfer.mobtt.core.ui.section.SectionUi
import at.stefanirndorfer.mobtt.designsystem.preview.BatPreviewSurface
import at.stefanirndorfer.mobtt.designsystem.preview.BatScreenPreview
import at.stefanirndorfer.mobtt.designsystem.theme.BatDesign
import at.stefanirndorfer.mobtt.designsystem.ui.components.BatChartPoint
import at.stefanirndorfer.mobtt.designsystem.ui.components.BatRatingChart
import at.stefanirndorfer.mobtt.domain.rating.RatingHistory
import at.stefanirndorfer.mobtt.domain.rating.RatingUpdate
import at.stefanirndorfer.mobtt.feature.players.impl.presentation.playerdetail.models.RatingHistorySectionUi
import at.stefanirndorfer.mobtt.feature.players.impl.presentation.playerdetail.preview.ratingHistoryPreview
import kotlinx.datetime.LocalDate
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

/** The collapsed Rating history: the Rating after each Rating update, with its Deviation as a band. */
@Composable
fun RatingHistorySectionView(
    section: RatingHistorySectionUi,
    onToggle: () -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    LazySectionView(
        title = stringResource(R.string.player_detail_rating_history_title),
        expanded = section.expanded,
        onToggle = onToggle,
        section = section.history,
        loadingLabel = stringResource(R.string.player_detail_rating_history_loading_cd),
        unavailableText = stringResource(R.string.player_detail_rating_history_unavailable_body),
        modifier = modifier,
        onRetry = onRetry,
    ) { history ->
        RatingHistoryView(history)
    }
}

@Composable
private fun RatingHistoryView(history: RatingHistory) {
    val latest = history.updates.lastOrNull()
    if (latest == null) {
        SectionMessage(stringResource(R.string.player_detail_rating_history_empty_body))
        return
    }
    Column(verticalArrangement = Arrangement.spacedBy(BatDesign.spacing.sm)) {
        BatRatingChart(
            points = history.updates.map { it.toChartPoint() },
            xLabel = ::monthLabel,
            contentDescription = stringResource(R.string.player_detail_rating_history_chart_cd, latest.after.points, latest.after.deviation),
        )
        if (history.updates.size == 1) {
            SectionMessage(
                stringResource(R.string.player_detail_rating_history_single_body, latest.after.points, latest.after.deviation, dayLabel(latest.date)),
            )
        }
    }
}

/** The Rating after the update, so Ratings Central's unrated default before the first one never appears. */
private fun RatingUpdate.toChartPoint() = BatChartPoint(
    x = date.toEpochDays().toFloat(),
    value = after.points.toFloat(),
    low = (after.points - after.deviation).toFloat(),
    high = (after.points + after.deviation).toFloat(),
)

private fun monthLabel(epochDay: Float): String = format(SimpleDateFormat("MMM yyyy", Locale.getDefault()), LocalDate.fromEpochDays(epochDay.toLong()))

private fun dayLabel(date: LocalDate): String = format(DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.getDefault()), date)

private fun format(format: DateFormat, date: LocalDate): String {
    format.timeZone = TimeZone.getTimeZone("UTC")
    return format.format(Date(date.toEpochDays() * MILLIS_PER_DAY))
}

private const val MILLIS_PER_DAY = 86_400_000L

@BatScreenPreview
@Composable
private fun RatingHistorySectionViewPreview() {
    BatPreviewSurface {
        Column(verticalArrangement = Arrangement.spacedBy(BatDesign.spacing.lg)) {
            RatingHistorySectionView(RatingHistorySectionUi(), onToggle = {}, onRetry = {})
            RatingHistorySectionView(RatingHistorySectionUi(expanded = true, history = SectionUi.Loaded(ratingHistoryPreview)), onToggle = {}, onRetry = {})
            RatingHistorySectionView(
                RatingHistorySectionUi(expanded = true, history = SectionUi.Loaded(RatingHistory(ratingHistoryPreview.updates.take(1)))),
                onToggle = {},
                onRetry = {},
            )
            RatingHistorySectionView(RatingHistorySectionUi(expanded = true, history = SectionUi.Loaded(RatingHistory(emptyList()))), onToggle = {}, onRetry = {})
            RatingHistorySectionView(RatingHistorySectionUi(expanded = true, history = SectionUi.Unavailable), onToggle = {}, onRetry = {})
        }
    }
}
