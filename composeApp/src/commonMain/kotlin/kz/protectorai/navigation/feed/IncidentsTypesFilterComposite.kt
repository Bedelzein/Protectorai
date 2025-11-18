package kz.protectorai.navigation.feed

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import kz.protectorai.CommonHardcode
import kz.protectorai.core.Stateful
import kz.protectorai.data.ClientRepository
import kz.protectorai.navigation.Composite
import kz.protectorai.ui.icons.ProtectoraiIcons

class IncidentsTypesFilterComposite(
    scope: CoroutineScope,
    private val repository: ClientRepository
) : Composite<IncidentsTypesFilterComposite.State>,
    Stateful<IncidentsTypesFilterComposite.State> by Stateful.Default(State.Loading) {

    init {
        scope.launch(Dispatchers.IO) {
            try {
                val incidentTypesFilter = repository.getIncidentTypes()
                    .map { (key, value) -> key }
                    .associateWith { false }
                updateState { State.Content(CommonHardcode.wildcard { mapOf("потенциальная агрессия" to true) }) }
            } catch (e: Exception) {
                updateState { State.Error(e) }
            }
        }
    }

    @Composable
    override fun Content(modifier: Modifier, state: State) {
        Column(modifier = modifier) {
            Text(
                modifier = Modifier.padding(8.dp),
                text = CommonHardcode { "Тип инцидентов:" }
            )
            when (state) {
                is State.Loading -> CircularProgressIndicator()
                is State.Content -> FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val incidentTypes = state.incidentTypes
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
                                        imageVector = ProtectoraiIcons.Done(),
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
                    // Show error message
                }
            }
        }
    }

    sealed interface State : Composite.State {
        object Loading : State
        data class Content(val incidentTypes: Map<String, Boolean>) : State
        data class Error(val error: Throwable) : State
    }
}