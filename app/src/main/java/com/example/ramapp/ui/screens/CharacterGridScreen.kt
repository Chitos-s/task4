package com.example.ramapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.ramapp.domain.model.Character
import com.example.ramapp.domain.model.CharacterFilters
import com.example.ramapp.ui.state.ListUiState
import com.example.ramapp.ui.theme.BlueGray50

private const val VARIANT_CODE = "RAM-CHAR-MOD_A2_GRID"

@Composable
fun CharacterGridScreen(
    listState: ListUiState,
    selectedFilters: CharacterFilters,
    onRetry: () -> Unit,
    onCharacterClick: (Int) -> Unit,
    onApplyFilters: (CharacterFilters) -> Unit,
    onNextPage: () -> Unit,
    onPrevPage: () -> Unit
) {
    var nameQuery by rememberSaveable { mutableStateOf(selectedFilters.name.orEmpty()) }
    var speciesQuery by rememberSaveable { mutableStateOf(selectedFilters.species.orEmpty()) }
    var typeQuery by rememberSaveable { mutableStateOf(selectedFilters.type.orEmpty()) }
    var showAdvanced by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(selectedFilters) {
        nameQuery = selectedFilters.name.orEmpty()
        speciesQuery = selectedFilters.species.orEmpty()
        typeQuery = selectedFilters.type.orEmpty()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BlueGray50)
    ) {
        // Header
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Rick and Morty Characters",
                    style = MaterialTheme.typography.titleLarge
                )
                Text(
                    text = VARIANT_CODE,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.secondary
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Status Filter
            Text(
                text = "Status",
                style = MaterialTheme.typography.labelSmall
            )
            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                FilterChip(
                    label = "All",
                    isSelected = selectedFilters.status == null,
                    onClick = { onApplyFilters(selectedFilters.copy(status = null)) }
                )
                FilterChip(
                    label = "Alive",
                    isSelected = selectedFilters.status == "alive",
                    onClick = { onApplyFilters(selectedFilters.copy(status = "alive")) }
                )
                FilterChip(
                    label = "Dead",
                    isSelected = selectedFilters.status == "dead",
                    onClick = { onApplyFilters(selectedFilters.copy(status = "dead")) }
                )
                FilterChip(
                    label = "Unknown",
                    isSelected = selectedFilters.status == "unknown",
                    onClick = { onApplyFilters(selectedFilters.copy(status = "unknown")) }
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Gender Filter
            Text(
                text = "Gender",
                style = MaterialTheme.typography.labelSmall
            )
            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                FilterChip(
                    label = "All",
                    isSelected = selectedFilters.gender == null,
                    onClick = { onApplyFilters(selectedFilters.copy(gender = null)) }
                )
                FilterChip(
                    label = "Female",
                    isSelected = selectedFilters.gender == "female",
                    onClick = { onApplyFilters(selectedFilters.copy(gender = "female")) }
                )
                FilterChip(
                    label = "Male",
                    isSelected = selectedFilters.gender == "male",
                    onClick = { onApplyFilters(selectedFilters.copy(gender = "male")) }
                )
                FilterChip(
                    label = "Genderless",
                    isSelected = selectedFilters.gender == "genderless",
                    onClick = { onApplyFilters(selectedFilters.copy(gender = "genderless")) }
                )
                FilterChip(
                    label = "Unknown",
                    isSelected = selectedFilters.gender == "unknown",
                    onClick = { onApplyFilters(selectedFilters.copy(gender = "unknown")) }
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Button(
                onClick = { showAdvanced = !showAdvanced },
                modifier = Modifier.height(32.dp),
                colors = ButtonDefaults.outlinedButtonColors()
            ) {
                Text(
                    if (showAdvanced) "Скрыть фильтры" else "Доп. фильтры",
                    style = MaterialTheme.typography.labelSmall,
                    fontSize = 11.sp
                )
            }

            if (showAdvanced) {
                Spacer(modifier = Modifier.height(6.dp))

                OutlinedTextField(
                    value = nameQuery,
                    onValueChange = { nameQuery = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Name") },
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = speciesQuery,
                        onValueChange = { speciesQuery = it },
                        modifier = Modifier.weight(1f),
                        label = { Text("Species") },
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = typeQuery,
                        onValueChange = { typeQuery = it },
                        modifier = Modifier.weight(1f),
                        label = { Text("Type") },
                        singleLine = true
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = {
                            onApplyFilters(
                                selectedFilters.copy(
                                    name = nameQuery.trim().takeIf { it.isNotEmpty() },
                                    species = speciesQuery.trim().takeIf { it.isNotEmpty() },
                                    type = typeQuery.trim().takeIf { it.isNotEmpty() }
                                )
                            )
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Применить")
                    }
                    Button(
                        onClick = {
                            nameQuery = ""
                            speciesQuery = ""
                            typeQuery = ""
                            onApplyFilters(
                                selectedFilters.copy(
                                    name = null,
                                    species = null,
                                    type = null
                                )
                            )
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Сброс")
                    }
                }
            }
        }

        // Content
        when (listState) {
            ListUiState.Loading -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator()
                }
            }

            is ListUiState.Error -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f)
                        .padding(24.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = listState.message, style = MaterialTheme.typography.bodyLarge)
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(onClick = onRetry) {
                        Text(text = "Retry")
                    }
                }
            }

            is ListUiState.Content -> {
                if (listState.items.isEmpty()) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(1f),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Ничего не найдено",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Adaptive(minSize = 150.dp),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        items(listState.items) { character ->
                            CharacterCard(
                                character = character,
                                onClick = { onCharacterClick(character.id) }
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = onPrevPage,
                        enabled = listState.pagination.hasPrev,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Previous")
                    }
                    Text(
                        text = "${listState.pagination.currentPage} / ${listState.pagination.totalPages}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Button(
                        onClick = onNextPage,
                        enabled = listState.pagination.hasNext,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Next")
                    }
                }
            }
        }
    }
}

@Composable
private fun FilterChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        colors = if (isSelected) {
            ButtonDefaults.buttonColors()
        } else {
            ButtonDefaults.outlinedButtonColors()
        },
        modifier = Modifier.height(32.dp),
        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(label, style = MaterialTheme.typography.labelSmall, fontSize = 11.sp)
    }
}

@Composable
private fun CharacterCard(
    character: Character,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .clip(CardDefaults.shape)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            AsyncImage(
                model = character.imageUrl,
                contentDescription = character.name,
                modifier = Modifier
                    .height(140.dp)
                    .clip(MaterialTheme.shapes.medium)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = character.name,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = "${character.status} • ${character.species}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
