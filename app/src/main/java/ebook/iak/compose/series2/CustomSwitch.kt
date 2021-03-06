package ebook.iak.compose.series2

import androidx.compose.animation.animateColor
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ebook.iak.compose.series1.CardPosition
import ebook.iak.compose.ui.theme.Gray
import ebook.iak.compose.ui.theme.LightGreen
import ebook.iak.compose.ui.theme.White

/**
 * @author Isaac Akakpo
 * Created on 12/3/2021 7:42 PM
 */

enum class SwitchState {
    IS_ENABLED,
    NOT_ENABLED
}

@Preview(showBackground = true)
@Composable
fun CustomSwitch() {

    var currentState by remember { mutableStateOf(SwitchState.NOT_ENABLED) }
    val transition = updateTransition(currentState, label = "SwitchState")
    val color by transition.animateColor(transitionSpec = {
        tween(200, easing = FastOutLinearInEasing)
    }, label = "") {
        when (it) {
            SwitchState.IS_ENABLED ->
                LightGreen
            else -> White
        }
    }



    BoxWithConstraints(
        modifier = Modifier
            .width(70.dp)
            .height(35.dp)
            .clickable {
                currentState = if (currentState == SwitchState.NOT_ENABLED) {
                    SwitchState.IS_ENABLED
                } else {
                    SwitchState.NOT_ENABLED
                }
            }
            .indication(remember {
                MutableInteractionSource()
            }, rememberRipple())
            .background(color = color, shape = RoundedCornerShape(100))
            .border(1.dp, Color.LightGray, shape = RoundedCornerShape(100)),
        contentAlignment = Alignment.CenterStart
    ) {

        val roundCardSize = this.maxWidth / 2

        val xOffset by transition.animateDp(
            transitionSpec = {
                tween(300, easing = FastOutLinearInEasing)
            }, label = "xOffset"
        ) { state ->
            when (state) {
                SwitchState.NOT_ENABLED -> 0.dp
                SwitchState.IS_ENABLED -> this.maxWidth - roundCardSize
            }
        }

        Card(
            modifier = Modifier
                .size(this.maxWidth / 2)
                .offset(x = xOffset, y = 0.dp)
                .padding(3.dp),
            shape = RoundedCornerShape(100),
            backgroundColor = White,
            border = BorderStroke(0.5.dp, color = Color.LightGray)
        ) {


        }

    }
}