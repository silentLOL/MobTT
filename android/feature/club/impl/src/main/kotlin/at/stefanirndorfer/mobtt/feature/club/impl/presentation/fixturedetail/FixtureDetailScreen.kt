package at.stefanirndorfer.mobtt.feature.club.impl.presentation.fixturedetail

import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextDecoration
import androidx.core.content.FileProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import at.stefanirndorfer.mobtt.core.localization.R
import at.stefanirndorfer.mobtt.core.ui.ObserveAsEvents
import at.stefanirndorfer.mobtt.core.ui.fixture.formatKickoff
import at.stefanirndorfer.mobtt.designsystem.icon.BatIcon
import at.stefanirndorfer.mobtt.designsystem.preview.BatPreviewSurface
import at.stefanirndorfer.mobtt.designsystem.preview.BatScreenPreview
import at.stefanirndorfer.mobtt.designsystem.theme.BatDesign
import at.stefanirndorfer.mobtt.designsystem.theme.BatTheme
import at.stefanirndorfer.mobtt.designsystem.ui.components.BatEmptyState
import at.stefanirndorfer.mobtt.designsystem.ui.components.BatHeroCard
import at.stefanirndorfer.mobtt.designsystem.ui.components.BatHeroPillRow
import at.stefanirndorfer.mobtt.designsystem.ui.components.BatIconButton
import at.stefanirndorfer.mobtt.designsystem.ui.components.BatList
import at.stefanirndorfer.mobtt.designsystem.ui.components.BatPrimaryButton
import at.stefanirndorfer.mobtt.designsystem.ui.components.BatSectionHeader
import at.stefanirndorfer.mobtt.domain.club.FixtureDetail
import at.stefanirndorfer.mobtt.domain.club.FixtureStatus
import at.stefanirndorfer.mobtt.feature.club.impl.presentation.fixturedetail.models.FixtureDetailAction
import at.stefanirndorfer.mobtt.feature.club.impl.presentation.fixturedetail.models.FixtureDetailEvent
import at.stefanirndorfer.mobtt.feature.club.impl.presentation.fixturedetail.models.FixtureDetailUiState
import at.stefanirndorfer.mobtt.feature.club.impl.presentation.fixturedetail.preview.fixtureDetailLongNamesPreview
import at.stefanirndorfer.mobtt.feature.club.impl.presentation.fixturedetail.preview.fixtureDetailNoMatchesPreview
import at.stefanirndorfer.mobtt.feature.club.impl.presentation.fixturedetail.preview.fixtureDetailPlayedPreview
import at.stefanirndorfer.mobtt.feature.club.impl.presentation.fixturedetail.preview.fixtureDetailScheduledPreview
import at.stefanirndorfer.mobtt.feature.club.impl.presentation.fixturedetail.views.FixtureShareImage
import at.stefanirndorfer.mobtt.feature.club.impl.presentation.fixturedetail.views.MatchRowView
import at.stefanirndorfer.mobtt.feature.club.impl.presentation.fixturedetail.views.qrCode
import co.touchlab.kermit.Logger
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf
import java.io.File

@Composable
fun FixtureDetailRoute(
    fixtureId: String,
    onOpenTeam: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: FixtureDetailViewModel = koinViewModel(key = fixtureId, parameters = { parametersOf(fixtureId) }),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    var pendingShare by remember { mutableStateOf<FixtureDetailEvent.Share?>(null) }
    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            is FixtureDetailEvent.OpenTeam -> onOpenTeam(event.teamId)
            is FixtureDetailEvent.Share -> pendingShare = event
        }
    }
    Box(modifier = modifier) {
        FixtureDetailScreen(state = uiState, onAction = viewModel::onAction)
        pendingShare?.let { share ->
            // Cleared only once shared: leaving the composition would cancel the save.
            ShareImageCapture(share.detail, share.link, modifier = Modifier.matchParentSize()) { image ->
                try {
                    context.shareFixture(share.detail, share.link, image)
                } finally {
                    pendingShare = null
                }
            }
        }
    }
}

/**
 * Composes the share image out of sight, records it into a graphics layer instead of drawing it, and hands the
 * picture to [onCaptured]; null when it could not be drawn. Hidden from accessibility services.
 */
@Composable
private fun ShareImageCapture(detail: FixtureDetail, link: String, modifier: Modifier = Modifier, onCaptured: suspend (ImageBitmap?) -> Unit) {
    val layer = rememberGraphicsLayer()
    val qr = remember(link) { qrCode(link, QR_CODE_PIXELS) }
    var recorded by remember { mutableStateOf(false) }
    Box(modifier = modifier.clearAndSetSemantics {}) {
        BatTheme(darkTheme = false) {
            FixtureShareImage(
                detail = detail,
                qrCode = qr,
                modifier = Modifier
                    .wrapContentHeight(align = Alignment.Top, unbounded = true)
                    .drawWithContent {
                        layer.record { this@drawWithContent.drawContent() }
                        recorded = true
                    },
            )
        }
    }
    LaunchedEffect(recorded) {
        if (recorded) {
            val image = try {
                layer.toImageBitmap()
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Logger.withTag("FixtureShare").e(e) { "Could not draw the share image; sharing text only" }
                null
            }
            onCaptured(image)
        }
    }
}

private const val QR_CODE_PIXELS = 512

@Composable
fun FixtureDetailScreen(
    state: FixtureDetailUiState,
    onAction: (FixtureDetailAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    when (state) {
        FixtureDetailUiState.Loading -> LoadingView(modifier)
        FixtureDetailUiState.Error -> ErrorView(onRetry = { onAction(FixtureDetailAction.RetryClicked) }, modifier = modifier)
        is FixtureDetailUiState.Content -> ContentView(state.detail, state.canShare, onAction, modifier)
    }
}

@Composable
private fun LoadingView(modifier: Modifier = Modifier) {
    val loadingLabel = stringResource(R.string.fixture_detail_loading_cd)
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(
            modifier = Modifier.semantics { contentDescription = loadingLabel },
            color = BatDesign.colors.brand,
        )
    }
}

@Composable
private fun ErrorView(onRetry: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(BatDesign.spacing.lg),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        BatEmptyState(
            icon = BatIcon.Club,
            title = stringResource(R.string.fixture_detail_error_title),
            body = stringResource(R.string.fixture_detail_error_body),
            modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite },
        )
        BatPrimaryButton(text = stringResource(R.string.fixture_detail_error_retry_button), onClick = onRetry)
    }
}

@Composable
private fun ContentView(detail: FixtureDetail, canShare: Boolean, onAction: (FixtureDetailAction) -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(BatDesign.spacing.lg),
        verticalArrangement = Arrangement.spacedBy(BatDesign.spacing.lg),
    ) {
        HeaderView(
            detail = detail,
            onTeamClick = { onAction(FixtureDetailAction.TeamClicked(it)) },
            onShare = if (canShare) ({ onAction(FixtureDetailAction.ShareClicked) }) else null,
        )
        if (detail.fixture.status is FixtureStatus.Finished) {
            Column {
                BatSectionHeader(title = stringResource(R.string.fixture_detail_matches_title))
                if (detail.matches.isEmpty()) {
                    Text(
                        text = stringResource(R.string.fixture_detail_matches_empty_body),
                        color = BatDesign.colors.textSecondary,
                        style = BatDesign.typography.body,
                    )
                } else {
                    BatList {
                        detail.matches.forEachIndexed { index, match -> MatchRowView(match, striped = index % 2 == 1) }
                    }
                }
            }
        }
    }
}

/**
 * The pairing with each Team opening its Team, then the score, or "Not played yet" for a scheduled Fixture,
 * and when and where it is played. [onShare] adds the Share button next to the pill.
 */
@Composable
private fun HeaderView(detail: FixtureDetail, onTeamClick: (String) -> Unit, onShare: (() -> Unit)?) {
    val fixture = detail.fixture
    val status = fixture.status
    val headerDescription = when (status) {
        is FixtureStatus.Finished -> stringResource(
            R.string.fixture_detail_header_result_cd,
            fixture.homeTeamName,
            fixture.awayTeamName,
            status.score.homeMatchesWon,
            status.score.awayMatchesWon,
        )
        FixtureStatus.Scheduled -> stringResource(R.string.fixture_detail_header_not_played_cd, fixture.homeTeamName, fixture.awayTeamName)
    }
    BatHeroCard(modifier = Modifier.fillMaxWidth()) {
        BatHeroPillRow(
            text = stringResource(R.string.fixture_detail_hero_overline),
            trailing = onShare?.let { share ->
                { BatIconButton(icon = BatIcon.Share, contentDescription = stringResource(R.string.fixture_detail_share), onClick = share) }
            },
        )
        Column(verticalArrangement = Arrangement.spacedBy(BatDesign.spacing.xs)) {
            TeamLink(fixture.homeTeamName, onClick = { onTeamClick(fixture.homeTeamId) })
            TeamLink(fixture.awayTeamName, onClick = { onTeamClick(fixture.awayTeamId) })
        }
        Text(
            text = when (status) {
                is FixtureStatus.Finished -> stringResource(R.string.fixture_detail_score, status.score.homeMatchesWon, status.score.awayMatchesWon)
                FixtureStatus.Scheduled -> stringResource(R.string.fixture_detail_not_played)
            },
            modifier = Modifier.semantics { contentDescription = headerDescription },
            color = BatDesign.colors.onHero,
            style = BatDesign.typography.display.copy(fontFeatureSettings = "tnum"),
        )
        val where = listOfNotNull(fixture.venue, detail.address?.formatted()).joinToString("\n")
        Text(
            text = listOf(formatKickoff(fixture.startsAt), where).filter { it.isNotEmpty() }.joinToString("\n"),
            color = BatDesign.colors.onHero,
            style = BatDesign.typography.body,
        )
    }
}

/** The image with a short caption; the full text alone when there is no [image] or it cannot be saved. */
private suspend fun Context.shareFixture(detail: FixtureDetail, link: String, image: ImageBitmap?) {
    val imageUri = image?.let {
        try {
            saveShareImage(it)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Logger.withTag("FixtureShare").e(e) { "Could not save the share image; sharing text only" }
            null
        }
    }
    val share = if (imageUri != null) {
        fixtureShareCaption(detail, link)
    } else {
        fixtureShare(detail, link)
    }
    val send = Intent(Intent.ACTION_SEND)
        .putExtra(Intent.EXTRA_SUBJECT, share.subject)
        .putExtra(Intent.EXTRA_TEXT, share.text)
    if (imageUri == null) {
        send.setType("text/plain")
    } else {
        send.setType("image/png")
            .putExtra(Intent.EXTRA_STREAM, imageUri)
            .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        send.clipData = ClipData.newRawUri(null, imageUri)
    }
    startActivity(Intent.createChooser(send, getString(R.string.fixture_detail_share)))
}

/** One file, replaced by every share, in the cache folder the share FileProvider serves. */
private suspend fun Context.saveShareImage(image: ImageBitmap): Uri = withContext(Dispatchers.IO) {
    val file = File(File(cacheDir, "fixture_share").apply { mkdirs() }, "fixture.png")
    file.outputStream().use { image.asAndroidBitmap().compress(Bitmap.CompressFormat.PNG, 100, it) }
    FileProvider.getUriForFile(this@saveShareImage, "$packageName.fixtureshare", file)
}

/** Underlined, so a link is never told apart by colour alone. */
@Composable
private fun TeamLink(name: String, onClick: () -> Unit) {
    Text(
        text = name,
        modifier = Modifier
            .heightIn(min = BatDesign.sizing.minTouchTarget)
            .wrapContentHeight(Alignment.CenterVertically)
            .clickable(role = Role.Button, onClick = onClick),
        color = BatDesign.colors.onHero,
        style = BatDesign.typography.title.copy(textDecoration = TextDecoration.Underline),
    )
}

@BatScreenPreview
@Composable
private fun FixtureDetailScreenLoadingPreview() {
    BatPreviewSurface { FixtureDetailScreen(state = FixtureDetailUiState.Loading, onAction = {}) }
}

@BatScreenPreview
@Composable
private fun FixtureDetailScreenErrorPreview() {
    BatPreviewSurface { FixtureDetailScreen(state = FixtureDetailUiState.Error, onAction = {}) }
}

@BatScreenPreview
@Composable
private fun FixtureDetailScreenPlayedPreview() {
    BatPreviewSurface { FixtureDetailScreen(state = FixtureDetailUiState.Content(fixtureDetailPlayedPreview, canShare = true), onAction = {}) }
}

@BatScreenPreview
@Composable
private fun FixtureDetailScreenScheduledPreview() {
    BatPreviewSurface { FixtureDetailScreen(state = FixtureDetailUiState.Content(fixtureDetailScheduledPreview), onAction = {}) }
}

@BatScreenPreview
@Composable
private fun FixtureDetailScreenNoMatchesPreview() {
    BatPreviewSurface { FixtureDetailScreen(state = FixtureDetailUiState.Content(fixtureDetailNoMatchesPreview), onAction = {}) }
}

@BatScreenPreview
@Composable
private fun FixtureDetailScreenLongNamesPreview() {
    BatPreviewSurface { FixtureDetailScreen(state = FixtureDetailUiState.Content(fixtureDetailLongNamesPreview, canShare = true), onAction = {}) }
}
