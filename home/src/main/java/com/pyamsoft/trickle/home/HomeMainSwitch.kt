package com.pyamsoft.trickle.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.pyamsoft.pydroid.theme.keylines

@Composable
internal fun HomeMainSwitch(
    modifier: Modifier = Modifier,
    enabled: Boolean,
    onChange: (Boolean) -> Unit,
) {
    Row(
        modifier = modifier.clickable { onChange(!enabled) },
    ) {
        Column(
            modifier = Modifier.weight(1F),
        ) {
            Text(
                text = "Automatic power-saving",
                style = MaterialTheme.typography.body1,
            )
            Text(
                text = "Power-saving mode when screen is off",
                style = MaterialTheme.typography.caption,
            )
        }

        Switch(
            modifier = Modifier.padding(start = MaterialTheme.keylines.baseline),
            checked = enabled,
            onCheckedChange = onChange,
        )
    }
}