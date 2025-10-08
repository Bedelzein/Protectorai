package kz.protectorai.navigation.feed

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch
import kz.protectorai.core.Stateful
import kz.protectorai.data.ClientRepository
import kz.protectorai.navigation.Composite
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.jvm.JvmInline

class LocationsFilterComposite(
    scope: CoroutineScope,
    private val repository: ClientRepository
) : Composite<LocationsFilterComposite.State>,
    Stateful<LocationsFilterComposite.State> by Stateful.Default(State.Loading) {

    init {
        scope.launch(Dispatchers.IO) {
            try {
                val locationsFilter = repository.getLocations().associateWith { false }
                updateState { State.Content(locationsFilter) }
            } catch (e: Exception) {
                updateState { State.Error(e) }
            }
        }
    }

    @Composable
    override fun Content(modifier: Modifier, state: State) {
        Column(modifier) {
            Text("Locations:")
            when (state) {
                is State.Loading -> CircularProgressIndicator()
                is State.Content -> FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val incidentTypes = state.locationsFilter
                    incidentTypes.forEach { (type, isSelected) ->
                        FilterChip(
                            selected = isSelected,
                            onClick = {
                                val updatedTypes = incidentTypes.toMutableMap()
                                updatedTypes[type] = !isSelected
                                updateState { State.Content(updatedTypes) }
                            },
                            label = { Text(type) },
                            leadingIcon = if (isSelected) {
                                {
                                    Icon(
                                        imageVector = Icons.Filled.Done,
                                        modifier = Modifier.size(FilterChipDefaults.IconSize),
                                        contentDescription = null
                                    )
                                }
                            } else {
                                null
                            }
                        )
                    }
                }
                is State.Error -> {
                    // Handle error state, e.g., show an error message
                }
            }
        }
    }

    sealed interface State : Composite.State {
        object Loading : State
        @JvmInline value class Content(val locationsFilter: Map<String, Boolean>) : State
        @JvmInline value class Error(val error: Throwable) : State
    }
}