package com.example.readwritecalls.screen

import android.provider.CallLog
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.readwritecalls.R
import com.example.readwritecalls.data.CallLogItem
import java.text.SimpleDateFormat
import java.util.Locale

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