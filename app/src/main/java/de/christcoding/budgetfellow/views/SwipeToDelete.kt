@file:OptIn(ExperimentalMaterialApi::class)

package de.christcoding.budgetfellow.views

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.DismissDirection
import androidx.compose.material.DismissState
import androidx.compose.material.DismissValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.SwipeToDismiss
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.rememberDismissState
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

@Composable
fun <T> SwipeToDeleteContainer(
    item: T,
    onDismiss: (T) -> Unit,
    animationDuration: Int = 500,
    content: @Composable (T) -> Unit
) {
    var isRemoved by remember { mutableStateOf(false) }
    val state = rememberDismissState(
        confirmStateChange = {
            if (it == DismissValue.DismissedToStart) {
                isRemoved = true
                true
            } else {
                false
            }
        }
    )
    
    LaunchedEffect(key1 = isRemoved) {
        if (isRemoved) {
            delay(animationDuration.toLong())
            onDismiss(item)
        }
    }
    
    AnimatedVisibility(
        visible = !isRemoved,
        exit = shrinkVertically(
            animationSpec = tween(durationMillis = animationDuration),
            shrinkTowards = Alignment.Top
        ) + fadeOut()
    ) {
        SwipeToDismiss(
            state = state,
            background = { DeleteBackground(swipeDismissState = state) },
            dismissContent = {content(item)},
            directions = setOf(DismissDirection.EndToStart),
        )
    }
}

@Composable
fun DeleteBackground(
    swipeDismissState: DismissState
) {
    val color = if (swipeDismissState.dismissDirection == DismissDirection.EndToStart) {
        Color.Red
    } else {
        Color.Transparent
    }
    Box(modifier = Modifier
        .fillMaxSize()
        .background(color = color)
        .padding(16.dp),
        contentAlignment = Alignment.CenterEnd)
    {
        Icon(Icons.Default.Delete, tint = Color.White, contentDescription = null)
    }
}