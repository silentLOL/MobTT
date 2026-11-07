package at.stefanirndorfer.mobtt.feature.players.impl.presentation.playerdetail.views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import at.stefanirndorfer.mobtt.core.localization.R
import at.stefanirndorfer.mobtt.core.ui.section.LazySectionView
import at.stefanirndorfer.mobtt.core.ui.section.SectionMessage
import at.stefanirndorfer.mobtt.core.ui.section.SectionUi
import at.stefanirndorfer.mobtt.designsystem.icon.BatIcon
import at.stefanirndorfer.mobtt.designsystem.preview.BatPreviewSurface
import at.stefanirndorfer.mobtt.designsystem.preview.BatScreenPreview
import at.stefanirndorfer.mobtt.designsystem.theme.BatDesign
import at.stefanirndorfer.mobtt.designsystem.ui.components.BatChartPoint
import at.stefanirndorfer.mobtt.designsystem.ui.components.BatIconButton
import at.stefanirndorfer.mobtt.designsystem.ui.components.BatRatingChart
import at.stefanirndorfer.mobtt.designsystem.ui.components.BatSegmentedControl
import at.stefanirndorfer.mobtt.domain.rating.RatingHistory
import at.stefanirndorfer.mobtt.domain.rating.RatingUpdate
import at.stefanirndorfer.mobtt.feature.players.impl.presentation.playerdetail.models.RatingHistorySectionUi
import at.stefanirndorfer.mobtt.feature.players.impl.presentation.playerdetail.models.RatingHistorySpan
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
    onSpanChosen: (RatingHistorySpan) -> Unit,
    onInfo: () -> Unit,
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
        headerAction = {
            BatIconButton(icon = BatIcon.Info, contentDescription = stringResource(R.string.player_detail_rating_info_cd), onClick = onInfo)
        },
    ) { history ->
        RatingHistoryView(history, section, onSpanChosen)
    }
}

@Composable
private fun RatingHistoryView(
    history: RatingHistory,
    section: RatingHistorySectionUi,
    onSpanChosen: (RatingHistorySpan) -> Unit,
) {
    if (history.updates.isEmpty()) {
        SectionMessage(stringResource(R.string.player_detail_rating_history_empty_body))
        return
    }
    Column(verticalArrangement = Arrangement.spacedBy(BatDesign.spacing.md)) {
        val spans = RatingHistorySpan.entries
        BatSegmentedControl(
            options = spans.map { stringResource(it.label) },
            selectedIndex = spans.indexOf(section.span),
            onSelect = { onSpanChosen(spans[it]) },
        )
        val updates = section.visibleUpdates
        val latest = updates.lastOrNull()
        if (latest == null) {
            SectionMessage(stringResource(R.string.player_detail_rating_history_no_recent_body))
            return@Column
        }
        // A touched Rating update; a new span or history starts without one.
        var selected by remember(updates) { mutableStateOf<Int?>(null) }
        BatRatingChart(
            points = updates.map { it.toChartPoint() },
            xLabel = ::monthLabel,
            contentDescription = stringResource(
                R.string.player_detail_rating_history_chart_cd,
                latest.after.points,
                latest.after.deviation,
                signed(latest.pointChange),
                updates.size,
                dayLabel(updates.first().date),
            ),
            showDots = section.showsDots,
            selectedIndex = selected,
            onSelect = { selected = it },
            previousLabel = stringResource(R.string.player_detail_rating_history_previous_cd),
            nextLabel = stringResource(R.string.player_detail_rating_history_next_cd),
        )
        val shown = selected?.let(updates::getOrNull)
        when {
            shown != null -> RatingUpdateCalloutView(shown)
            history.updates.size == 1 -> SectionMessage(
                stringResource(R.string.player_detail_rating_history_single_body, latest.after.points, latest.after.deviation, dayLabel(latest.date)),
            )
        }
    }
}

/** The touched Rating update: when, which, and where it left the Rating. */
@Composable
private fun RatingUpdateCalloutView(update: RatingUpdate) {
    Column(
        modifier = Modifier.semantics(mergeDescendants = true) { liveRegion = LiveRegionMode.Polite },
        verticalArrangement = Arrangement.spacedBy(BatDesign.spacing.xxs),
    ) {
        Text(text = dayLabel(update.date), color = BatDesign.colors.textTertiary, style = BatDesign.typography.caption)
        Text(text = update.name, color = BatDesign.colors.textPrimary, style = BatDesign.typography.bodyStrong)
        Text(
            text = stringResource(R.string.player_detail_rating_history_callout_body, update.after.points, update.after.deviation, signed(update.pointChange)),
            color = BatDesign.colors.textSecondary,
            style = BatDesign.typography.body,
        )
    }
}

private val RatingHistorySpan.label: Int
    get() = when (this) {
        RatingHistorySpan.LAST_YEAR -> R.string.player_detail_rating_history_span_year_label
        RatingHistorySpan.ALL -> R.string.player_detail_rating_history_span_all_label
    }

/** A point change with its sign, e.g. +72 or −1119. */
private fun signed(change: Int): String = when {
    change > 0 -> "+$change"
    change < 0 -> "\u2212${-change}"
    else -> "0"
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
            RatingHistorySectionView(RatingHistorySectionUi(), onToggle = {}, onRetry = {}, onSpanChosen = {}, onInfo = {})
            RatingHistorySectionView(RatingHistorySectionUi(expanded = true, history = SectionUi.Loaded(ratingHistoryPreview)), onToggle = {}, onRetry = {}, onSpanChosen = {}, onInfo = {})
            RatingHistorySectionView(
                RatingHistorySectionUi(expanded = true, history = SectionUi.Loaded(RatingHistory(ratingHistoryPreview.updates.take(1)))),
                onToggle = {},
                onRetry = {},
                onSpanChosen = {},
                onInfo = {},
            )
            RatingHistorySectionView(RatingHistorySectionUi(expanded = true, history = SectionUi.Loaded(RatingHistory(emptyList()))), onToggle = {}, onRetry = {}, onSpanChosen = {}, onInfo = {})
            RatingHistorySectionView(RatingHistorySectionUi(expanded = true, history = SectionUi.Unavailable), onToggle = {}, onRetry = {}, onSpanChosen = {}, onInfo = {})
            RatingHistorySectionView(
                RatingHistorySectionUi(expanded = true, history = SectionUi.Loaded(ratingHistoryPreview), since = LocalDate(2030, 1, 1)),
                onToggle = {},
                onRetry = {},
                onSpanChosen = {},
                onInfo = {},
            )
        }
    }
}
