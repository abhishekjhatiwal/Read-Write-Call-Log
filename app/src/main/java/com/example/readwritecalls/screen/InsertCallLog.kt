package com.example.readwritecalls.screen

import android.provider.CallLog
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.readwritecalls.data.CallLogItem
import com.example.readwritecalls.getCallTypeFromIndex
import com.example.readwritecalls.getIndexFromType
import java.util.Date

@Composable
fun InsertCallLogScreen(onSave: (CallLogItem) -> Unit) {
    var name by remember { mutableStateOf("") }
    var number by remember { mutableStateOf("") }
    var duration by remember { mutableStateOf("") }
    var selectedType by remember { mutableIntStateOf(CallLog.Calls.INCOMING_TYPE) }
    val callTypes = listOf("Incoming", "Outgoing", "Missed")

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(7.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Insert Call Log",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Name") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = number,
            onValueChange = { number = it },
            label = { Text("Phone Number") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Phone)
        )

        OutlinedTextField(
            value = duration,
            onValueChange = { duration = it },
            label = { Text("Duration (in sec)") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
        )

        // Call Type Dropdown
        var expanded by remember { mutableStateOf(false) }
        Box {
            Text(
                text = callTypes[getIndexFromType(selectedType)], modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = true }
                    .border(1.dp, Color.Gray, RoundedCornerShape(4.dp))
                    .padding(16.dp)
            )

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                callTypes.forEachIndexed { index, type ->
                    DropdownMenuItem(
                        onClick = {
                            selectedType = getCallTypeFromIndex(index)
                            expanded = false
                        }, text = {
                            Text(type)
                        }
                    )
                }
            }
        }

        // Save Button
        OutlinedButton(
            onClick = {
                val callLog = CallLogItem(
                    name = name,
                    number = number,
                    type = selectedType,
                    date = Date(),
                    duration = duration
                )
                onSave(callLog)
            },
            modifier = Modifier.wrapContentWidth()
        ) {
            Text("Save Call Log")
        }
    }
}