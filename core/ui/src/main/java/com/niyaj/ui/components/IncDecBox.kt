package com.niyaj.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.niyaj.designsystem.theme.SpaceMini
import com.niyaj.designsystem.theme.SpaceSmall

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IncDecBox(
    enableDecreasing: Boolean = false,
    onDecrease: () -> Unit,
    onIncrease: () -> Unit,
) {
    ElevatedCard(
        modifier = Modifier
            .height(40.dp),
        shape = RoundedCornerShape(SpaceMini),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.onPrimary
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxHeight(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Card(
                modifier = Modifier
                    .fillMaxHeight(),
                onClick = onDecrease,
                enabled = enableDecreasing,
                shape = RoundedCornerShape(
                    topStart = SpaceMini,
                    topEnd = 0.dp,
                    bottomStart = SpaceMini,
                    bottomEnd = 0.dp
                ),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxHeight()
                        .padding(SpaceSmall),
                ) {
                    Spacer(modifier = Modifier.width(SpaceSmall))
                    Icon(imageVector = Icons.Default.Remove, contentDescription = "remove")
                    Spacer(modifier = Modifier.width(SpaceSmall))
                }
            }


            HorizontalDivider(modifier = Modifier.width(1.dp).fillMaxHeight())

            Row(
                modifier = Modifier
                    .fillMaxHeight()
                    .clickable {
                        onIncrease()
                    }
                    .padding(SpaceSmall),
            ) {
                Spacer(modifier = Modifier.width(SpaceSmall))
                Icon(imageVector = Icons.Default.Add, contentDescription = "add")
                Spacer(modifier = Modifier.width(SpaceSmall))
            }
        }
    }

}