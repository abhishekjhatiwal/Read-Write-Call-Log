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

