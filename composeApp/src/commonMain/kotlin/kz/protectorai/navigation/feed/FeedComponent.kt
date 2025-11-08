package kz.protectorai.navigation.feed

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import chaintech.videoplayer.host.MediaPlayerHost
import chaintech.videoplayer.ui.video.VideoPlayerComposable
import com.arkivanov.decompose.ComponentContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format
import kotlinx.datetime.minus
import kotlinx.datetime.toLocalDateTime
import kz.protectorai.CommonHardcode
import kz.protectorai.core.Eventful
import kz.protectorai.core.Stateful
import kz.protectorai.core.coroutineScope
import kz.protectorai.data.ClientRepository
import kz.protectorai.navigation.Composite
import kz.protectorai.navigation.RootComponent
import kz.protectorai.navigation.feed.FeedComponent.State.Content
import kz.protectorai.ui.icons.ProtectoraiIcons
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
class FeedComponent(
    componentContext: ComponentContext,
    private val onRootEvent: Eventful<RootComponent.Event>,
    private val clientRepository: ClientRepository
) : ComponentContext by componentContext,
    Composite<FeedComponent.State>, Stateful<FeedComponent.State> by Stateful.Default(State()) {

    private val scope by lazy { coroutineScope(SupervisorJob()) }
    private val timezone = TimeZone.currentSystemDefault()

    private val snackbarHostState by lazy { SnackbarHostState() }

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
        scope.launch(Dispatchers.IO) {
            val now = Clock.System.now()
            updateState {
                copy(
                    timeStart = now.minus(7, DateTimeUnit.DAY, timezone),
                    timeEnd = now
                )
            }
        }
        combine(
            incidentTypesFilterComposite
                .stateFlow
                .filterIsInstance<IncidentsTypesFilterComposite.State.Content>()
                .map { it.incidentTypes.filter { (_, value) -> value }.keys.toList() },
            locationsFilterComposite
                .stateFlow
                .filterIsInstance<LocationsFilterComposite.State.Content>()
                .map { it.locationsFilter.filter { (_, value) -> value }.keys.toList() },
            stateFlow.map { it.timeStart }.filterNotNull(),
            stateFlow.map { it.timeEnd }.filterNotNull()
        ) { incidentTypes, locations, dateStart, dateEnd ->
            FeedFilter(
                incidentTypes,
                locations,
                dateStart,
                dateEnd
            )
        }
            .distinctUntilChanged()
            .onEach { incidentsFilter ->
                try {
                    val format = LocalDateTime.Formats.ISO
                    val dateStart = incidentsFilter.timeStart
                    val dateEnd = incidentsFilter.timeEnd
                    val incidentsResponse = clientRepository.getIncidents(
                        GetIncidentsRequestBody(
                            isConfirmed = true,
                            locationIds = incidentsFilter.locations,
                            eventTypes = incidentsFilter.incidentTypes,
                            startDate = dateStart.toLocalDateTime(timezone).format(format),
                            endDate = dateEnd.toLocalDateTime(timezone).format(format)
                        )
                    )
                    val content = incidentsResponse
                        .takeIf { it.total > 0 }
                        ?.let(Content::Loaded)
                        ?: Content.Empty
                    updateState { copy(content = content) }
                } catch (e: Exception) {
                    println(e)
                    // TODO
                }
            }
            .launchIn(scope)
        scope.launch {
            val types = clientRepository.getIncidentClasses()
            updateState { copy(incidentTypes = types) }
        }
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
                        IconButton(
                            onClick = {
                                CommonHardcode {
                                    scope.launch {
                                        snackbarHostState.showSnackbar(
                                            CommonHardcode { "В разработке" },
                                            duration = SnackbarDuration.Short
                                        )
                                    }
                                }
                            }
                        ) {
                            Icon(
                                imageVector = ProtectoraiIcons.Notifications(),
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
                    onClick = { updateState { copy(isDatePickerVisible = true) } }
                ) {
                    Icon(
                        imageVector = ProtectoraiIcons.Filter(),
                        contentDescription = null
                    )
                }
            },
            snackbarHost = { SnackbarHost(snackbarHostState) }
        ) {
            when (val content = state.content) {
                is Content.Loading -> CircularProgressIndicator()
                is Content.Empty -> EmptyContent()
                is Content.Loaded -> VideoList(
                    content.incidents.items,
                    modifier = Modifier.padding(it)
                )
            }
        }
        if (state.isDatePickerVisible) {
            IncidentsFilter(onDismiss = { updateState { copy(isDatePickerVisible = false) } })
        }
        state.modal?.let { Modal(state, it) }
    }

    @Composable
    private fun EmptyContent() {
        Column(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(CommonHardcode { "No data for selected period" })
            Spacer(Modifier.size(16.dp))
            Button(
                onClick = { updateState { copy(isDatePickerVisible = true) } }
            ) { Text(CommonHardcode { "Select another period" }) }
            Spacer(Modifier.size(16.dp))
            Button(
                onClick = {
                    updateState {
                        copy(
                            timeStart = timeEnd?.minus(
                                1,
                                DateTimeUnit.MONTH,
                                timezone
                            )
                        )
                    }
                }
            ) { Text(CommonHardcode { "Show incidents for last month" }) }
            Spacer(Modifier.size(16.dp))
            Button(
                onClick = {
                    updateState {
                        copy(
                            timeStart = timeEnd?.minus(
                                3,
                                DateTimeUnit.MONTH,
                                timezone
                            )
                        )
                    }
                }
            ) { Text(CommonHardcode { "Show incidents for last 3 month" }) }
            Spacer(Modifier.size(16.dp))
            Button(
                onClick = {
                    updateState {
                        copy(
                            timeStart = timeEnd?.minus(
                                1,
                                DateTimeUnit.YEAR,
                                timezone
                            )
                        )
                    }
                }
            ) { Text(CommonHardcode { "Show incidents for last year" }) }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun Modal(state: State, modal: State.Modal) {
        ModalBottomSheet(onDismissRequest = { updateState { copy(modal = null) } }) {
            when (modal) {
                is State.Modal.Notifications -> TODO()
                is State.Modal.EditIncidentType -> Column {
                    state.incidentTypes?.forEach {
                        TextButton(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = { updateIncidentType(modal.incident, it) }
                        ) {
                            Text(it.description)
                            Spacer(Modifier.weight(1f))
                        }
                    }
                }
                is State.Modal.IncidentDetails -> Column {
                    VideoItem(incident = modal.incident)
                    ExposedDropdownMenuBox(
                        modifier = Modifier.fillMaxWidth().padding(8.dp),
                        expanded = modal.isTypeExpanded,
                        onExpandedChange = {
                            updateState {
                                copy(modal = modal.copy(isTypeExpanded = !modal.isTypeExpanded))
                            }
                        }
                    ) {
                        OutlinedTextField(
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(MenuAnchorType.PrimaryNotEditable),
                            readOnly = true,
                            value = modal.type?.name.orEmpty(),
                            onValueChange = {},
                            label = { Text("Incident type") },
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(
                                    expanded = modal.isTypeExpanded
                                )
                            },
                            supportingText = { modal.type?.let { Text(it.description) } }
                        )
                        ExposedDropdownMenu(
                            expanded = modal.isTypeExpanded,
                            onDismissRequest = {
                                updateState { copy(modal = modal.copy(isTypeExpanded = false)) }
                            }
                        ) {
                            state.incidentTypes?.forEach {
                                DropdownMenuItem(
                                    text = { Text(it.name) },
                                    onClick = {
                                        updateState {
                                            copy(
                                                modal = modal.copy(
                                                    isTypeExpanded = false,
                                                    type = it
                                                )
                                            )
                                        }
                                        updateIncidentType(modal.incident, it)
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun IncidentsFilter(onDismiss: () -> Unit) {
        val dateRangePickerState = rememberDateRangePickerState(
            initialSelectedStartDateMillis = state.timeStart?.toEpochMilliseconds(),
            initialSelectedEndDateMillis = state.timeEnd?.toEpochMilliseconds()
        )

        DatePickerDialog(
            onDismissRequest = onDismiss,
            confirmButton = {
                TextButton(
                    onClick = {
                        val dateStart = dateRangePickerState.selectedStartDateMillis
                            ?: return@TextButton onDismiss()
                        val dateEnd = dateRangePickerState.selectedEndDateMillis
                            ?: state.timeEnd?.toEpochMilliseconds()
                            ?: return@TextButton onDismiss()
                        val startInstant = Instant.fromEpochMilliseconds(dateStart)
                        val endInstant = Instant.fromEpochMilliseconds(dateEnd)
                        updateState { copy(timeStart = startInstant, timeEnd = endInstant) }
                        onDismiss()
                    }
                ) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text("Закрыть")
                }
            }
        ) {
            DateRangePicker(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 400.dp),
                state = dateRangePickerState,
                title = {
                    Column(modifier = Modifier.padding(start = 8.dp, top = 16.dp, end = 8.dp)) {
                        incidentTypesFilterComposite.Content(modifier = Modifier.fillMaxWidth())
                        locationsFilterComposite.Content(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 4.dp)
                        )
                    }
                },
                headline = {
                    val dateFormatter = DatePickerDefaults.dateFormatter()
                    val locale = dateRangePickerState.locale
                    val formatterStartDate = dateFormatter.formatDate(
                        dateMillis = dateRangePickerState.selectedStartDateMillis,
                        locale = locale
                    ) ?: return@DateRangePicker
                    val endDate = dateRangePickerState.selectedEndDateMillis
                        ?: state.timeEnd?.toEpochMilliseconds()
                        ?: return@DateRangePicker
                    val formatterEndDate = dateFormatter.formatDate(
                        dateMillis = endDate,
                        locale = locale
                    ) ?: return@DateRangePicker
                    Text(
                        modifier = Modifier.padding(horizontal = 8.dp),
                        text = "$formatterStartDate - $formatterEndDate",
                        fontSize = 16.sp
                    )
                }
            )
        }
    }

    @Composable
    private fun VideoList(
        incidents: List<Incident>,
        modifier: Modifier = Modifier
    ) {
        LazyColumn(
            modifier = modifier,
            contentPadding = PaddingValues(top = 4.dp, bottom = 4.dp)
        ) {
            items(incidents, key = { it.id + it.time }) { incident ->
                VideoItem(
                    incident = incident,
                    isEditButtonVisible = true
                )
            }
        }
    }

    @Composable
    fun VideoItem(
        modifier: Modifier = Modifier,
        incident: Incident,
        isEditButtonVisible: Boolean = false
    ) {
        Card(
            modifier = modifier.padding(horizontal = 16.dp, 6.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                        .aspectRatio(CommonHardcode.wildcard { 768f / 432f })
                ) {
                    Player(
                        modifier = Modifier.fillMaxSize(),
                        incident = incident
                    )
                }
                Row {
                    Column(modifier = Modifier.weight(5f)) {
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
                                .weight(1f)
                                .padding(end = 8.dp),
                            onClick = {
                                updateState { copy(modal = State.Modal.EditIncidentType(incident)) }
                            }
                        ) {
                            Icon(imageVector = ProtectoraiIcons.Edit(), contentDescription = null)
                        }
                    }
                }
            }
        }
    }

    @Composable
    private fun Player(
        incident: Incident,
        modifier: Modifier = Modifier
    ) {
        val playerHost = remember {
            MediaPlayerHost(mediaUrl = incident.videoSource, isPaused = true)
        }

        VideoPlayerComposable(
            modifier = modifier,
            playerHost = playerHost
        )
    }

    private fun updateIncidentType(incident: Incident, type: Incident.Type) {
        scope.launch {
            clientRepository.updateIncidentClass(incident.id, type.id)
        }
    }

    data class State(
        val timeStart: Instant? = null,
        val timeEnd: Instant? = null,
        val content: Content = Content.Loading,
        val isDatePickerVisible: Boolean = false,
        val modal: Modal? = null,
        val incidentTypes: List<Incident.Type>? = null
    ) : Composite.State {

        sealed interface Content {
            data object Loading : Content

            data object Empty : Content

            data class Loaded(val incidents: IncidentsResponse) : Content
        }

        sealed interface Modal {
            data object Notifications : Modal

            data class EditIncidentType(
                val incident: Incident,
                val type: Incident.Type? = null
            ) : Modal

            data class IncidentDetails(
                val incident: Incident,
                val isTypeExpanded: Boolean = false,
                val type: Incident.Type? = null
            ) : Modal
        }
    }

    data class FeedFilter(
        val incidentTypes: List<String>,
        val locations: List<String>,
        val timeStart: Instant,
        val timeEnd: Instant
    )
}
