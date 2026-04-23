package com.example.ramapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.ramapp.ui.state.DetailUiState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack

@Composable
fun CharacterDetailScreen(
    detailState: DetailUiState,
    onRetry: () -> Unit,
    onBack: () -> Unit
) {
    when (detailState) {
        DetailUiState.Loading -> {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator()
            }
        }

        is DetailUiState.Error -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = detailState.message, style = MaterialTheme.typography.bodyLarge)
                Spacer(modifier = Modifier.height(12.dp))
                Button(onClick = onRetry) {
                    Text(text = "Retry")
                }
            }
        }

        is DetailUiState.Content -> {
            val character = detailState.character

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                    Text(
                        text = character.name,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
                AsyncImage(
                    model = character.imageUrl,
                    contentDescription = character.name,
                    modifier = Modifier
                        .size(240.dp)
                        .align(Alignment.CenterHorizontally)
                )
                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Status: ${character.status}",
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = "Species: ${character.species}",
                    style = MaterialTheme.typography.bodyLarge
                )
                if (character.type.isNotEmpty()) {
                    Text(
                        text = "Type: ${character.type}",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
                Text(
                    text = "Gender: ${character.gender}",
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = "Origin: ${character.originName}",
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = "Location: ${character.locationName}",
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = "Episodes: ${character.episodeCount}",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}
