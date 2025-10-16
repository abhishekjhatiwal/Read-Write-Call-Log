package com.example.readwritecalls

import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.provider.CallLog
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.readwritecalls.data.CallLogItem
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CallLogHelper(private val context: Context) {

    fun getCallLogs(): List<CallLogItem> {
        val callLogs = mutableListOf<CallLogItem>()

        val contentResolver = context.contentResolver

        val uri = CallLog.Calls.CONTENT_URI

        val projection = arrayOf(
            CallLog.Calls.CACHED_NAME,
            CallLog.Calls.NUMBER,
            CallLog.Calls.TYPE,
            CallLog.Calls.DATE,
            CallLog.Calls.DURATION
        )

        // Filter to fetch only unknown calls
        val selection = "${CallLog.Calls.CACHED_NAME} = ?"
        val selectionArgs = arrayOf("")

        val cursor: Cursor? = contentResolver.query(
            uri,
            projection, selection, selectionArgs,
            CallLog.Calls.DATE + " DESC"
        )

        cursor?.use {
            val nameIndex = it.getColumnIndex(CallLog.Calls.CACHED_NAME)
            val numberIndex = it.getColumnIndex(CallLog.Calls.NUMBER)
            val typeIndex = it.getColumnIndex(CallLog.Calls.TYPE)
            val dateIndex = it.getColumnIndex(CallLog.Calls.DATE)
            val durationIndex = it.getColumnIndex(CallLog.Calls.DURATION)

            while (it.moveToNext()) {
                val nameRow = it.getString(nameIndex)
                val name = if (nameRow == null || nameRow == "") "Unknown" else nameRow
                val number = it.getString(numberIndex)
                val type = it.getInt(typeIndex)
                val date = Date(it.getLong(dateIndex))
                val duration = it.getString(durationIndex) ?: "0"

                callLogs.add(CallLogItem(name, number, type, date, duration))
            }
        }
        return callLogs
    }

    @SuppressLint("MissingPermission")
    fun insertCallLog(callLogItem: CallLogItem) {
        val values = ContentValues().apply {
            put(CallLog.Calls.CACHED_NAME, callLogItem.name)
            put(CallLog.Calls.NUMBER, callLogItem.number)
            put(CallLog.Calls.TYPE, callLogItem.type)
            put(CallLog.Calls.DATE, System.currentTimeMillis())
            put(CallLog.Calls.DURATION, callLogItem.duration)
        }
        context.contentResolver.insert(CallLog.Calls.CONTENT_URI, values)
    }
}


@Composable
fun CallLogItemRow(log: CallLogItem) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(6.dp)
            .background(Color.White, shape = RoundedCornerShape(8.dp))
            .border(1.dp, Color.LightGray, RoundedCornerShape(8.dp))
            .padding(4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        // Circle with Initials
        Box(
            modifier = Modifier
                .size(50.dp)
                .background(Color.LightGray.copy(0.4f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = log.name.firstOrNull()?.toString() ?: "U",
                style = MaterialTheme.typography.titleLarge,
                color = Color.Black
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Call Details Column
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = log.name,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = log.number,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )
            Text(
                text = SimpleDateFormat(
                    "dd MMM yyyy, hh:mm:ss a",
                    Locale.getDefault()
                ).format(log.date),
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
            Text(
                text = "${log.duration} sec",
                style = MaterialTheme.typography.bodySmall,
                color = Color.DarkGray
            )
        }

        // Call Type Icon
        Icon(
            painter = when (log.type) {
                CallLog.Calls.INCOMING_TYPE -> painterResource(id = R.drawable.ic_call_received)
                CallLog.Calls.OUTGOING_TYPE -> painterResource(id = R.drawable.ic_call_made)
                CallLog.Calls.MISSED_TYPE -> painterResource(id = R.drawable.ic_call_missed)
                else -> painterResource(id = R.drawable.ic_call_received)
            },
            contentDescription = "Call Type",
            tint = when (log.type) {
                CallLog.Calls.MISSED_TYPE -> Color.Red
                else -> Color(0xFF4CAF50)  // Green for Incoming/Outgoing
            },
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .size(24.dp)
        )
    }
}

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

fun getIndexFromType(type: Int): Int {
    return when (type) {
        CallLog.Calls.INCOMING_TYPE -> 0
        CallLog.Calls.OUTGOING_TYPE -> 1
        CallLog.Calls.MISSED_TYPE -> 2
        else -> 0
    }
}

fun getCallTypeFromIndex(index: Int): Int {
    return when (index) {
        0 -> CallLog.Calls.INCOMING_TYPE
        1 -> CallLog.Calls.OUTGOING_TYPE
        2 -> CallLog.Calls.MISSED_TYPE
        else -> CallLog.Calls.INCOMING_TYPE
    }
}

@Composable
fun RequestPermissions(
    onGranted: () -> Unit
) {
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val allGranted = permissions.values.all { it }
        if (allGranted) {
            onGranted()
        }
    }

    LaunchedEffect(Unit) {
        launcher.launch(
            arrayOf(
                Manifest.permission.READ_CALL_LOG,
                Manifest.permission.WRITE_CALL_LOG
            )
        )
    }
}

@Composable
fun MainCallLogScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val callLogHelper = remember { CallLogHelper(context) }
    var permissionGranted by remember { mutableStateOf(false) }

    val callLogs = remember { mutableStateListOf<CallLogItem>() }

    RequestPermissions {
        permissionGranted = true
    }

    if (permissionGranted) {
        InsertCallLogScreen { callLogItem ->
            callLogHelper.insertCallLog(callLogItem)
            callLogs.clear()
            callLogs.addAll(callLogHelper.getCallLogs())
        }

        LaunchedEffect(Unit) {
            callLogs.addAll(callLogHelper.getCallLogs())
        }

        LazyColumn {
            items(callLogs) { log ->
                CallLogItemRow(log)
            }
        }
    } else {
        Text("Permission Required to Access Call Logs")
    }
}