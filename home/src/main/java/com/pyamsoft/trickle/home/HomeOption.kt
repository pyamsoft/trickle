package com.pyamsoft.trickle.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Checkbox
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.pyamsoft.pydroid.theme.keylines

@Composable
internal fun HomeOption(
    modifier: Modifier = Modifier,
    name: String,
    enabled: Boolean,
    onChange: (Boolean) -> Unit,
) {
    Row(
        modifier =
        modifier.clickable { onChange(!enabled) }.padding(start = MaterialTheme.keylines.content),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            modifier = Modifier.weight(1F),
            text = name,
            style = MaterialTheme.typography.body2,
        )

        Checkbox(
            modifier = Modifier.padding(start = MaterialTheme.keylines.baseline),
            checked = enabled,
            onCheckedChange = onChange,
        )
    }
}