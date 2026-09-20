package com.example.scheduler.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.scheduler.ui.theme.PoppinsFamily

/**
 * Section for filter selection and search entry.
 */
@Composable
fun FilterAndSearchSection(
    selectedFilter: String,
    onFilterClick: () -> Unit,
    onSearchClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.clickable { onFilterClick() }
        ) {
            Text(
                text = selectedFilter,
                fontFamily = PoppinsFamily,
                fontSize = 18.sp,
                color = Color.White
            )
            Icon(
                Icons.Default.KeyboardArrowDown,
                contentDescription = "Filter",
                tint = Color.White,
                modifier = Modifier.size(24.dp).padding(start = 4.dp)
            )
        }
        
        IconButton(onClick = onSearchClick) {
            Icon(
                Icons.Default.Search,
                contentDescription = "Search",
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}
