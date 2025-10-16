package com.example.readwritecalls.screen

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.example.readwritecalls.CallLogHelper
import com.example.readwritecalls.CallLogItemRow
import com.example.readwritecalls.InsertCallLogScreen
import com.example.readwritecalls.RequestPermissions
import com.example.readwritecalls.data.CallLogItem

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