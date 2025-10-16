package com.example.readwritecalls

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.provider.CallLog
import com.example.readwritecalls.data.CallLogItem
import java.util.Date

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

