package dev.ewio.encryptedroom.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    uiState: MainUiState,
    onAddBatch: () -> Unit,
    onVerify: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Encrypted Room DB") }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            SummaryCard(
                title = "Database",
                body = "Rows: ${uiState.totalRowsLabel}\nBatch size: ${uiState.lastBatchSizeLabel}"
            )

            SummaryCard(
                title = "Verification",
                body = "Status: ${uiState.verificationLabel}\nLast run: ${uiState.lastVerifiedAtLabel}"
            )

            SummaryCard(
                title = "Status",
                body = uiState.statusMessage
            )

            Button(
                modifier = Modifier.fillMaxWidth(),
                enabled = !uiState.isBusy,
                onClick = onAddBatch,
                contentPadding = PaddingValues(vertical = 14.dp)
            ) {
                Text("Add 10,000 synthetic rows")
            }

            OutlinedButton(
                modifier = Modifier.fillMaxWidth(),
                enabled = !uiState.isBusy,
                onClick = onVerify,
                contentPadding = PaddingValues(vertical = 14.dp)
            ) {
                Text("Verify database now")
            }

            Text(
                text = "This sample uses an encrypted Room database and verifies all rows by recalculating checksums on every app open.",
                style = MaterialTheme.typography.bodyMedium,
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun SummaryCard(
    title: String,
    body: String,
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = title, style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = body, style = MaterialTheme.typography.bodyMedium)
        }
    }
}
