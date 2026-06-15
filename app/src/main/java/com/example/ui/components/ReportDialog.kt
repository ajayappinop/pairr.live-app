package com.example.ui.components

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ReportProblem
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.OrangeSecondary
import com.example.ui.theme.PinkPrimary
import com.example.ui.theme.appBorderColor
import com.example.ui.theme.appCaptionText
import com.example.ui.theme.appOutlinedFieldColors
import com.example.ui.theme.appSecondaryText
import com.example.ui.theme.AppBorderWeight
import com.example.ui.theme.appSuccessColor
import com.example.ui.theme.isAppDarkTheme

enum class ReportType {
    Call,
    Profile
}

@Composable
fun ReportDialog(
    reportedName: String,
    reportType: ReportType,
    callIsVideo: Boolean? = null,
    onDismiss: () -> Unit,
    onSubmitted: () -> Unit = {}
) {
    val context = LocalContext.current
    val textColor = MaterialTheme.colorScheme.onSurface
    val surfaceColor = if (isAppDarkTheme()) MaterialTheme.colorScheme.surface else Color.White
    val borderColor = appBorderColor(AppBorderWeight.Default)
    val fieldColors = appOutlinedFieldColors()
    val pinkGradient = Brush.horizontalGradient(listOf(PinkPrimary, OrangeSecondary))

    val categories = remember(reportType) {
        when (reportType) {
            ReportType.Call -> listOf(
                "Harassment",
                "Inappropriate Behavior",
                "Scam / Fraud",
                "Poor Call Quality",
                "Technical Issue",
                "Other"
            )
            ReportType.Profile -> listOf(
                "Harassment",
                "Inappropriate Content",
                "Fake Profile",
                "Scam / Spam",
                "Other"
            )
        }
    }

    var category by remember { mutableStateOf(categories.first()) }
    var description by remember { mutableStateOf("") }
    var showSuccess by remember { mutableStateOf(false) }

    val title = when (reportType) {
        ReportType.Call -> "Report Call"
        ReportType.Profile -> "Report Profile"
    }

    val subtitle = when (reportType) {
        ReportType.Call -> {
            val callType = when (callIsVideo) {
                true -> "video"
                false -> "audio"
                null -> ""
            }
            val callLabel = if (callType.isNotEmpty()) "$callType call " else "call "
            "Tell us what happened during your $callLabel with $reportedName. Our trust & safety team will review this report."
        }
        ReportType.Profile -> "Tell us why you are reporting $reportedName. Reports are reviewed by our trust & safety team."
    }

    if (showSuccess) {
        AlertDialog(
            onDismissRequest = {
                showSuccess = false
                onSubmitted()
                onDismiss()
            },
            shape = RoundedCornerShape(20.dp),
            containerColor = surfaceColor,
            icon = {
                Icon(
                    Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = appSuccessColor(),
                    modifier = Modifier.size(40.dp)
                )
            },
            title = {
                Text("Report Submitted", color = textColor, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            },
            text = {
                Text(
                    "Thank you. We have received your report about $reportedName and will investigate shortly.",
                    color = appSecondaryText(),
                    fontSize = 14.sp,
                    lineHeight = 22.sp
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    showSuccess = false
                    onSubmitted()
                    onDismiss()
                }) {
                    Text("Done", color = PinkPrimary, fontWeight = FontWeight.Bold)
                }
            }
        )
        return
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = surfaceColor)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, borderColor, RoundedCornerShape(24.dp))
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    Icons.Default.ReportProblem,
                    contentDescription = null,
                    tint = PinkPrimary,
                    modifier = Modifier.size(36.dp)
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(title, color = textColor, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    subtitle,
                    color = appSecondaryText(),
                    fontSize = 13.sp,
                    lineHeight = 18.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    "Issue type",
                    color = textColor,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                categories.chunked(2).forEach { row ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        row.forEach { item ->
                            FilterChip(
                                selected = category == item,
                                onClick = { category = item },
                                label = { Text(item, fontSize = 11.sp, maxLines = 2) },
                                modifier = Modifier.weight(1f),
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = PinkPrimary,
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                        if (row.size == 1) Spacer(modifier = Modifier.weight(1f))
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Details") },
                    placeholder = {
                        Text(
                            when (reportType) {
                                ReportType.Call -> "Describe what happened during the call…"
                                ReportType.Profile -> "Describe the issue with this profile…"
                            },
                            color = appCaptionText()
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(110.dp),
                    colors = fieldColors,
                    shape = RoundedCornerShape(12.dp),
                    maxLines = 5
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Cancel", color = appSecondaryText())
                    }
                    Button(
                        onClick = {
                            if (description.isBlank()) {
                                Toast.makeText(context, "Please describe the issue.", Toast.LENGTH_SHORT).show()
                            } else {
                                showSuccess = true
                            }
                        },
                        modifier = Modifier.weight(1.2f),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                        contentPadding = PaddingValues(0.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(pinkGradient, RoundedCornerShape(12.dp))
                                .padding(vertical = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Submit Report", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
