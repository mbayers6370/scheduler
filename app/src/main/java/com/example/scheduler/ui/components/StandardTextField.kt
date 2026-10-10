package com.example.scheduler.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.autofill.ContentType
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentType
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.scheduler.ui.theme.PoppinsFamily

/**
 * Standard text field component establishing consistent dimensions, colors, and typography across forms.
 * Supports external labelAbove, inner placeholder, keyboard options/actions, and autofill hints.
 */
@Composable
fun StandardTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String? = null,
    labelAbove: String? = null,
    placeholder: String? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    contentType: ContentType? = null,
    minLines: Int = 1,
    readOnly: Boolean = false,
    onClick: (() -> Unit)? = null
) {
    Column(modifier = modifier.fillMaxWidth()) {
        val displayLabel = labelAbove ?: label
        if (!displayLabel.isNullOrBlank()) {
            Text(
                text = displayLabel,
                fontFamily = PoppinsFamily,
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp,
                color = Color.White,
                modifier = Modifier.padding(bottom = 6.dp)
            )
        }
        
        val semanticsModifier = if (contentType != null) {
            Modifier.semantics {
                this.contentType = contentType
            }
        } else Modifier

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = if (!placeholder.isNullOrBlank()) {
                {
                    Text(
                        text = placeholder,
                        fontFamily = PoppinsFamily,
                        fontSize = 15.sp,
                        color = Color.White.copy(alpha = 0.4f)
                    )
                }
            } else null,
            visualTransformation = visualTransformation,
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            modifier = Modifier
                .fillMaxWidth()
                .then(semanticsModifier)
                .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier),
            minLines = minLines,
            readOnly = readOnly,
            enabled = onClick == null,
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.White.copy(alpha = 0.8f),
                unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                disabledBorderColor = Color.White.copy(alpha = 0.3f),
                disabledTextColor = Color.White
            )
        )
    }
}
