package kz.protectorai.navigation.feed

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import chaintech.videoplayer.host.MediaPlayerHost
import chaintech.videoplayer.ui.video.VideoPlayerComposable
import com.arkivanov.decompose.ComponentContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kz.protectorai.CommonHardcode
import kz.protectorai.core.EMPTY_STRING
import kz.protectorai.core.Stateful
import kz.protectorai.core.coroutineScope
import kz.protectorai.data.ProtectoraiRepository
import kz.protectorai.navigation.Composite
import kz.protectorai.ui.icons.ProtectoraiIcons
import kotlin.jvm.JvmInline

private const val DATE_CHAR_LENGTH = 6

class FeedComponent(
    componentContext: ComponentContext
) : ComponentContext by componentContext,
    Composite<FeedComponent.State>, Stateful<FeedComponent.State> by Stateful.Default(State()) {

    private val scope by lazy { coroutineScope(SupervisorJob()) }

    private val repository by lazy { ProtectoraiRepository() }

    private val incidentTypesFilterComposite by lazy {
        IncidentsTypesFilterComposite(
            scope,
            repository
        )
    }

    private val locationsFilterComposite by lazy { LocationsFilterComposite(scope, repository) }

    init {
        scope.launch(Dispatchers.IO) {
            val incidents = repository.getIncidents(
                CommonHardcode.wildcard {
                    GetIncidentsRequestBody(
                        isConfirmed = true,
                        locationIds = listOf("Школа №25, г. Петропавловск"),
                        eventTypes = listOf("потенциальная агрессия"),
                        startDate = "2025-03-10T14:17:24.537Z",
                        endDate = "2025-08-17T14:22:24.417Z"
                    )
                }
            )
            updateState { copy(content = State.Content.Loaded(incidents)) }
        }
    }

    @Composable
    override fun Content(modifier: Modifier, state: State) {
        Scaffold(
            modifier = modifier,
            floatingActionButton = {
                FloatingActionButton(onClick = { updateState { copy(isFiltersVisible = true) } }) {
                    Icon(
                        imageVector = ProtectoraiIcons.Filter(),
                        contentDescription = null
                    )
                }
            }
        ) {
            when (val content = state.content) {
                is State.Content.Loaded -> VideoList(
                    content.value,
                    modifier = Modifier.padding(it)
                )
                else -> CircularProgressIndicator()
            }
        }
        if (state.isFiltersVisible) {
            @OptIn(ExperimentalMaterial3Api::class)
            ModalBottomSheet(onDismissRequest = { updateState { copy(isFiltersVisible = false) } }) {
                Column(Modifier.padding(8.dp)) {
                    Row {
                        OutlinedTextField(
                            modifier = Modifier.weight(1f),
                            label = { Text("Date start") },
                            singleLine = true,
                            value = state.dateStart,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            onValueChange = {
                                if (it.length <= DATE_CHAR_LENGTH) updateState { copy(dateStart = it) }
                            },
                            visualTransformation = VisualTransformation(::dateFilter)
                        )
                        Spacer(Modifier.size(8.dp))
                        OutlinedTextField(
                            modifier = Modifier.weight(1f),
                            label = { Text("Date end") },
                            singleLine = true,
                            value = state.dateEnd,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            onValueChange = {
                                if (it.length <= DATE_CHAR_LENGTH) updateState { copy(dateEnd = it) }
                            },
                            visualTransformation = VisualTransformation(::dateFilter)
                        )
                    }
                    incidentTypesFilterComposite.Content(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp)
                    )
                    locationsFilterComposite.Content(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp)
                    )
                }
            }
        }
    }

    @Composable
    private fun VideoList(
        incidents: List<Incident>,
        modifier: Modifier = Modifier
    ) {
        val lazyListState = rememberLazyListState()
        val focusIndex by remember { derivedStateOf { lazyListState.firstVisibleItemIndex } }
        val focusIndexOffset by remember { derivedStateOf { lazyListState.firstVisibleItemScrollOffset } }

        val density = LocalDensity.current

        LazyColumn(
            modifier = modifier,
            state = lazyListState,
            contentPadding = PaddingValues(top = 4.dp, bottom = 4.dp)
        ) {
            items(count = Int.MAX_VALUE) { index ->
                val videoIndex = index % incidents.size

                VideoItem(
                    incident = incidents[videoIndex],
                    focusedVideo = index == 0 && focusIndexOffset <= with(density) { 48.dp.toPx() } ||
                            index == focusIndex + 1 && focusIndexOffset > with(density) { 48.dp.toPx() }
                )
            }
        }
    }

    @Composable
    fun VideoItem(
        incident: Incident,
        focusedVideo: Boolean,
        modifier: Modifier = Modifier
    ) {
        val animateBackground by animateColorAsState(
            targetValue = if (focusedVideo) Color(0xFFef5350) else MaterialTheme.colorScheme.surface
        )

        Card(
            modifier = modifier.padding(horizontal = 16.dp, 6.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            colors = CardDefaults.cardColors(
                containerColor = animateBackground
            )
        ) {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                ) {
                    /*Image(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.onSurface.copy(.1f)),
                        painter = rememberImagePainter(url = video.thumb),
                        contentDescription = null,
                        contentScale = ContentScale.Crop
                    )*/

                    Player(
                        modifier = Modifier.fillMaxSize(),
                        incident = incident,
                        focusedVideo = focusedVideo
                    )

                    /*androidx.compose.animation.AnimatedVisibility(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(top = 8.dp, end = 8.dp),
                        visible = focusedVideo,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        Image(
                            modifier = Modifier
                                .size(32.dp),
                            imageVector = ProtectoraiIcons.Play(),
                            contentDescription = null,
                            colorFilter = ColorFilter.tint(Color(0xFFef5350))
                        )
                    }*/
                }

                Text(
                    modifier = Modifier.padding(
                        start = 8.dp,
                        top = 2.dp,
                        end = 8.dp,
                        bottom = 0.dp
                    ),
                    text = incident.eventType,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )

                Text(
                    modifier = Modifier.padding(
                        start = 8.dp,
                        top = 2.dp,
                        end = 8.dp,
                        bottom = 6.dp
                    ),
                    text = incident.locationId,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Normal,
                    color = LocalContentColor.current.copy(.6f)
                )
            }
        }
    }

    @Composable
    private fun Player(
        incident: Incident,
        focusedVideo: Boolean,
        modifier: Modifier = Modifier
    ) {
        val playerHost = remember { MediaPlayerHost(mediaUrl = incident.videoSource) }

        if (focusedVideo) {
            playerHost.play()
        } else {
            playerHost.pause()
        }

        VideoPlayerComposable(
            modifier = modifier,
            playerHost = playerHost
        )
    }

    data class State(
        val isFiltersVisible: Boolean = false,
        val dateStart: String = EMPTY_STRING,
        val dateEnd: String = EMPTY_STRING,
        val content: Content = Content.Loading
    ) : Composite.State {

        sealed interface Content {
            data object Loading : Content
            @JvmInline value class Loaded(val value: List<Incident>) : Content
        }
    }
}

fun dateFilter(annotatedText: AnnotatedString): TransformedText {
    val trimmed = if (annotatedText.text.length >= DATE_CHAR_LENGTH) {
        annotatedText.text.substring(0 until DATE_CHAR_LENGTH)
    } else {
        annotatedText.text
    }
    var out = EMPTY_STRING
    for (i in trimmed.indices) {
        out += trimmed[i]
        if (i % 2 == 1 && i < 4) out += '/'
    }

    val numberOffsetTranslator = object : OffsetMapping {
        override fun originalToTransformed(offset: Int): Int = when {
            offset <= 1 -> offset
            offset <= 3 -> offset + 1
            offset <= DATE_CHAR_LENGTH -> offset + 2
            else -> DATE_CHAR_LENGTH + 2
        }

        override fun transformedToOriginal(offset: Int): Int = when {
            offset <= 2 -> offset
            offset <= 5 -> offset - 1
            offset <= DATE_CHAR_LENGTH + 2 -> offset - 2
            else -> DATE_CHAR_LENGTH
        }
    }

    return TransformedText(AnnotatedString(out), numberOffsetTranslator)
}
