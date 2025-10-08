package kz.protectorai.navigation.feed

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import chaintech.videoplayer.host.MediaPlayerHost
import chaintech.videoplayer.ui.video.VideoPlayerComposable
import com.arkivanov.decompose.ComponentContext
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kz.protectorai.core.EMPTY_STRING
import kz.protectorai.core.Eventful
import kz.protectorai.core.Stateful
import kz.protectorai.core.coroutineScope
import kz.protectorai.data.ClientRepository
import kz.protectorai.navigation.Composite
import kz.protectorai.navigation.RootComponent
import kz.protectorai.ui.icons.ProtectoraiIcons
import kotlin.jvm.JvmInline

class FeedComponent(
    componentContext: ComponentContext,
    private val onRootEvent: Eventful<RootComponent.Event>,
    clientRepository: ClientRepository
) : ComponentContext by componentContext,
    Composite<FeedComponent.State>, Stateful<FeedComponent.State> by Stateful.Default(State()) {

    private val scope by lazy { coroutineScope(SupervisorJob()) }

    private val incidentTypesFilterComposite by lazy {
        IncidentsTypesFilterComposite(
            scope,
            clientRepository
        )
    }

    private val locationsFilterComposite by lazy {
        LocationsFilterComposite(scope, clientRepository)
    }

    init {
        combine(
            incidentTypesFilterComposite
                .stateFlow
                .filterIsInstance<IncidentsTypesFilterComposite.State.Content>()
                .map { it.incidentTypes.filter { (_, value) -> value }.keys.toList() },
            locationsFilterComposite
                .stateFlow
                .filterIsInstance<LocationsFilterComposite.State.Content>()
                .map { it.locationsFilter.filter { (_, value) -> value }.keys.toList() },
            stateFlow.map { it.dateStart }.filter { it.length == DATE_CHAR_LENGTH },
            stateFlow.map { it.dateEnd }.filter { it.length == DATE_CHAR_LENGTH }
        ) { incidentTypes, locations, dateStart, dateEnd ->
            FeedFilter(
                incidentTypes,
                locations,
                dateStart,
                dateEnd
            )
        }
            .onEach { incidentsFilter ->
                try {
                    val dateStart = incidentsFilter.dateStart
                    val dateEnd = incidentsFilter.dateEnd
                    val incidents = clientRepository.getIncidents(
                        GetIncidentsRequestBody(
                            isConfirmed = true,
                            locationIds = incidentsFilter.locations,
                            eventTypes = incidentsFilter.incidentTypes,
                            startDate = "20${dateStart[4]}${dateStart[5]}-${dateStart[2]}${dateStart[3]}-${dateStart[0]}${dateStart[1]}T00:00:00.000Z",
                            endDate = "20${dateEnd[4]}${dateEnd[5]}-${dateEnd[2]}${dateEnd[3]}-${dateEnd[0]}${dateEnd[1]}T00:00:00.000Z"
                        )
                    )
                    updateState { copy(content = State.Content.Loaded(incidents)) }
                } catch (e: Exception) {
                    // TODO
                }
            }
            .launchIn(scope)
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content(modifier: Modifier, state: State) {
        Scaffold(
            modifier = modifier,
            topBar = {
                TopAppBar(
                    navigationIcon = {
                        Icon(
                            imageVector = ProtectoraiIcons.Logo(),
                            contentDescription = null
                        )
                    },
                    title = { Text("Protectorai") },
                    actions = {
                        IconButton(onClick = { updateState { copy(modal = State.Modal.Notifications) } }) {
                            Icon(
                                imageVector = Icons.Filled.Notifications,
                                contentDescription = null
                            )
                        }
                        IconButton(onClick = { onRootEvent(RootComponent.Event.Logout) }) {
                            Icon(
                                imageVector = ProtectoraiIcons.Logout(),
                                contentDescription = null
                            )
                        }
                    }
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { updateState { copy(modal = State.Modal.IncidentsFilter) } }
                ) {
                    Icon(
                        imageVector = ProtectoraiIcons.Filter(),
                        contentDescription = null
                    )
                }
            }
        ) {
            when (val content = state.content) {
                is State.Content.Loaded -> if (content.value.isNotEmpty()) {
                    VideoList(
                        content.value,
                        modifier = Modifier.padding(it)
                    )
                }

                else -> CircularProgressIndicator()
            }
        }
        state.modal?.let { Modal(state, it) }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun Modal(state: State, modal: State.Modal) {
        ModalBottomSheet(onDismissRequest = { updateState { copy(modal = null) } }) {
            when (modal) {
                is State.Modal.IncidentsFilter -> IncidentsFilter(state)
                is State.Modal.Notifications -> Column { }
                is State.Modal.IncidentDetails -> Column {
                    VideoItem(
                        incident = modal.incident,
                        focusedVideo = true
                    )
                    Button(
                        modifier = Modifier.fillMaxWidth().padding(8.dp),
                        onClick = {

                        }
                    ) {
                        Text("Спорт")
                    }
                    Button(
                        modifier = Modifier.fillMaxWidth().padding(8.dp),
                        onClick = {

                        }
                    ) {
                        Text("Драка")
                    }
                    Button(
                        modifier = Modifier.fillMaxWidth().padding(8.dp),
                        onClick = {

                        }
                    ) {
                        Text("Pampering")
                    }
                    Button(
                        modifier = Modifier.fillMaxWidth().padding(8.dp),
                        onClick = {

                        }
                    ) {
                        Text("False positive")
                    }
                }
            }
        }
    }

    @Composable
    private fun IncidentsFilter(state: State) {
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
                            index == focusIndex + 1 && focusIndexOffset > with(density) { 48.dp.toPx() },
                    isEditButtonVisible = true
                )
            }
        }
    }

    @Composable
    fun VideoItem(
        modifier: Modifier = Modifier,
        incident: Incident,
        focusedVideo: Boolean,
        isEditButtonVisible: Boolean = false
    ) {
        Card(
            modifier = modifier.padding(horizontal = 16.dp, 6.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                        .aspectRatio(768f / 432f)
                ) {
                    Player(
                        modifier = Modifier.fillMaxSize(),
                        incident = incident,
                        focusedVideo = focusedVideo
                    )
                }
                Row {
                    Column {
                        Text(
                            modifier = Modifier.padding(
                                start = 8.dp,
                                top = 2.dp,
                                end = 8.dp
                            ),
                            text = incident.eventType,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )

                        Text(
                            modifier = Modifier.padding(
                                start = 8.dp,
                                top = 2.dp,
                                end = 8.dp
                            ),
                            text = incident.locationId,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Normal,
                            color = LocalContentColor.current.copy(.6f)
                        )
                        Text(
                            modifier = Modifier.padding(
                                start = 8.dp,
                                end = 8.dp,
                                bottom = 6.dp
                            ),
                            text = incident.time.replace('T', ' '),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Normal,
                            color = LocalContentColor.current.copy(.6f)
                        )
                    }
                    Spacer(Modifier.weight(1f))
                    if (isEditButtonVisible) {
                        IconButton(
                            modifier = Modifier
                                .align(Alignment.CenterVertically)
                                .padding(end = 8.dp),
                            onClick = {
                                updateState {
                                    copy(modal = State.Modal.IncidentDetails(incident))
                                }
                            }
                        ) {
                            Icon(imageVector = Icons.Filled.Edit, contentDescription = null)
                        }
                    }
                }
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
        val dateStart: String = EMPTY_STRING,
        val dateEnd: String = EMPTY_STRING,
        val content: Content = Content.Loading,
        val modal: Modal? = Modal.IncidentsFilter
    ) : Composite.State {

        sealed interface Content {
            data object Loading : Content

            @JvmInline
            value class Loaded(val value: List<Incident>) : Content
        }

        sealed interface Modal {
            data object IncidentsFilter : Modal
            data object Notifications : Modal
            data class IncidentDetails(
                val incident: Incident,
                val mark: Mark? = null
            ) : Modal {
                enum class Mark(val title: String) {
                    SPORT("sport"),
                    FIGHT("fight"),
                    PAMPERING("pampering"),
                    FALSE_POSITIVE("false positive")
                }
            }
        }
    }

    data class FeedFilter(
        val incidentTypes: List<String>,
        val locations: List<String>,
        val dateStart: String,
        val dateEnd: String
    )
}
