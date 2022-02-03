package ebook.iak.compose.clock

import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.inset
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.input.pointer.consumeAllChanges
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import ebook.iak.compose.R
import ebook.iak.compose.dpToPx
import java.util.*
import kotlin.math.*

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.forEachGesture
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.input.pointer.PointerEvent
import androidx.compose.ui.unit.Dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/*
* MIT License

Copyright (c) 2021 Isaac Akakpo

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
*
*
*
* */

/**
 * @author Isaac Akakpo
 * Created on 12/5/2021 4:59 PM
 */


/*
*
*  Android 12 Clock Animation Jetpack Compose Clone
*  Fun Fact : This is a Simple (: Compose Function
* To help Compose Developers get a feel of the Canvas Api
*  and Android's Touch Input Geometry in General
*
* */
@ExperimentalComposeUiApi
@Composable
fun ClockAnimation(release:(Double) -> Unit) {


    val context = LocalContext.current
    val interactionSource = MutableInteractionSource()


    val canvasSize by remember {
        mutableStateOf(360.dp)
    }

    var clockBgSize by remember {
        mutableStateOf(300.dp)
    }

    val cal = Calendar.getInstance()
    val hour = cal.get(Calendar.HOUR)
    val minute = cal.get(Calendar.MINUTE)
    val minuteAngle = Math.toDegrees((2.0 * PI * minute) / 60)

    val hourAngle = Math.toDegrees((2.0 * PI * hour) / 12) + minuteAngle / 12.0

    Log.d("Time", "Hr $hourAngle : $minuteAngle")


    var minuteRotation by remember {
        mutableStateOf(0.0)
    }


    var previuosMinuteRotation by remember {
        mutableStateOf(-0.1)
    }

    var hourFactor by remember {
        mutableStateOf(0)
    }


    var hourRoationValue by remember {
        mutableStateOf(0.0)
    }

    var realRotationHour by remember {
        mutableStateOf(0.0)
    }


    var isFromClockWise by remember {
        mutableStateOf(true)
    }


    var isRun by remember {
        mutableStateOf(false)
    }

    var listOfNumbers by remember {
        mutableStateOf(mutiplesOfNumber(360))
    }

    val sizeClock: Dp by animateDpAsState(
        clockBgSize,
        animationSpec = spring(stiffness = 50f, dampingRatio = 2F)
    )

    val scope = rememberCoroutineScope()
    var pressed by remember { mutableStateOf(true) }

    val modifier = Modifier.pointerInput(Unit){
        forEachGesture {
            awaitPointerEventScope {
                // ACTION_DOWN here
                awaitFirstDown()
                // ACTION_MOVE loop
                do {
                    //This PointerEvent contains details details including
                    // event, id, position and more
                    val event: PointerEvent = awaitPointerEvent()

                } while (event.changes.any { it.pressed })
                // waitForUpOrCancellation()
                release(realRotationHour)
                // ACTION_UP is here
            }

        }
    }

    Box(
        modifier = Modifier
            .size(canvasSize)
            .pointerInput(Unit) {
                detectDragGestures { change, dragAmount ->
                    change.consumeAllChanges()
                    val canvasRadius = context.dpToPx(((canvasSize.value / 2).toInt()))
                    val rotationCal = Math.toDegrees(
                        atan2(
                            y = ((canvasRadius - change.position.y).toDouble()),
                            x = (change.position.x - canvasRadius).toDouble()
                        ) - Math.toRadians(90.0)
                    )

                    fun isClockWise(): Pair<Boolean,Boolean> {
                        val rotationNow = Math.toDegrees(
                            atan2(
                                y = ((canvasRadius - change.position.y).toDouble()),
                                x = (change.position.x - canvasRadius).toDouble()
                            ) - Math.toRadians(90.0)
                        )

                        val rotationPreviios = Math.toDegrees(
                            atan2(
                                y = ((canvasRadius - change.previousPosition.y).toDouble()),
                                x = (change.previousPosition.x - canvasRadius).toDouble()
                            ) - Math.toRadians(90.0)
                        )

                        var minuteRotationNow =
                            if (rotationNow <= 0) rotationNow.absoluteValue else 360.0 - rotationNow

                        var minuteRotationPrevious =
                            if (rotationPreviios <= 0) rotationPreviios.absoluteValue else 360.0 - rotationPreviios



                        if (minuteRotationPrevious.toInt() in 300..360 && minuteRotationNow.toInt() in 0..30) {
                            minuteRotationPrevious = 0.0
                            Log.d(
                                "clockwisB",
                                " Previuos: ${minuteRotationPrevious.toInt()} Now : $minuteRotationNow"
                            )
                        }

                        if (minuteRotationNow.toInt() in 300..360 && minuteRotationPrevious.toInt() in 0..30) {
                            minuteRotationPrevious = 360.0

                        }

                        return Pair(minuteRotationNow > minuteRotationPrevious,minuteRotationNow == minuteRotationPrevious)
                    }


                    minuteRotation =
                        if (rotationCal <= 0) rotationCal.absoluteValue else 360.0 - rotationCal



                    if (isClockWise().first && !isClockWise().second) {
                        if (!isFromClockWise) {
                            isFromClockWise = true
                            hourFactor++

                        }
                        if (previuosMinuteRotation <= minuteRotation) {
                            previuosMinuteRotation = minuteRotation
                            realRotationHour = (hourRoationValue / 12) + (minuteRotation / 12)

                            if (clockBgSize != 300.dp) {
                                scope.launch {
                                    delay(50)
                                    clockBgSize = 300.dp
                                }

                            }


                        } else {



                            hourFactor++
                            previuosMinuteRotation = minuteRotation
                            if (hourFactor >= 12) {
                                hourFactor = 0
                                hourRoationValue = 0.0
                            }
                            hourRoationValue = minuteRotation + listOfNumbers[hourFactor]
                            clockBgSize = 360.dp

                        }

                    }
                    else if (!isClockWise().first && !isClockWise().second)
                    {

                        if (isFromClockWise) {
                            isFromClockWise = false
                            hourFactor--
                        }
                        if (previuosMinuteRotation >= minuteRotation) {
                            previuosMinuteRotation = minuteRotation
                            realRotationHour = hourRoationValue / 12 + minuteRotation / 12


                            if (clockBgSize != 300.dp) {
                                scope.launch {
                                    delay(50)
                                    clockBgSize = 300.dp
                                }

                            }

                        } else {


                            if (hourFactor >= 0) {
                                hourFactor--
                            }
                            previuosMinuteRotation = minuteRotation
                            if (hourFactor < 0) {
                                hourFactor =  10
                                hourRoationValue = 0.0
                            }

                            hourRoationValue = minuteRotation + listOfNumbers[hourFactor]
                            clockBgSize = 360.dp

                        }

                    }

                    Log.d("mainHourRotationValue", "${hourRoationValue}")
                    Log.d("hourFactor", "$hourFactor")
                    Log.d("yoyo", "$realRotationHour")
                    Log.d("called", "$realRotationHour")

                    Log.d(
                        "previuosMinuteRotation",
                        "Previous : ${previuosMinuteRotation} Now : $minuteRotation"
                    )

                }
            }.then(modifier)

    )
    {

        /*

        You can uncheck this if you want to build the clock with tick Marks

        Canvas(
            modifier = Modifier
                .size(canvasSize)
                .background(color = Color.Transparent)
        )
        {
            val canvasWidth = size.width
            val canvasHeight = size.height

            Log.d("Width0", "${context.dpToPx((canvasSize.value / 2).toInt())}")
            Log.d("Width1", "${canvasWidth / 2}")


            drawCircle(
                color = Color(0xFFCDC4BB),
                center = Offset(x = canvasWidth / 2, y = canvasHeight / 2),
                radius = size.minDimension / 2
            )

            val radius = size.width / 2
            val mX = size.width / 2
            val mY = size.height / 2

            for (hourMark in 0 until 12) {

                *//*
                * let's first calculate for the angle of each clock number
                * NB : Kotlin uses radians instead of degree
                *
                * And Angle for the whole clock surface is 360 degrees === 2𝝅 (pie) radians
                *Meaning each angle should be 30 degrees
                *
                *
               * *//*
                val angle = (2.0 * PI * hourMark) / 12

                *//*
                * We then rotate the angle by 90 degrees anticlockwise to land the point at x = 1 and y = 0
                *
                *   --------------------
                * `
                *
                *
                *
                *
                *
                * *//*
                drawLine(
                    start = Offset(
                        x = mX + radius * (cos(angle - Math.toRadians(90.0)).toFloat()),
                        y = mY + radius * (sin(angle - Math.toRadians(90.0))).toFloat()
                    ),
                    end = Offset(
                        x = (mX + 0.9 * radius * (cos(angle - Math.toRadians(90.0)).toFloat())).toFloat(),
                        y = (mY + 0.9 * radius * (sin(angle - Math.toRadians(90.0))).toFloat()).toFloat()
                    ),
                    color = Color(0xFF807652),
                    strokeWidth = 2F
                )
            }


            Log.d(
                "Co-ordinates",
                "x = ${((cos(0.0) * (canvasWidth / 2)).toFloat())}, y = ${0F + ((canvasHeight / 2) * 0.1F)}"
            )

        }*/

        Image(
            painter = painterResource(id = R.drawable.android_12_clock_bg),
            contentDescription = "",
            modifier = Modifier
                .size(sizeClock)
                .align(
                    Alignment.Center
                )
        )

        Canvas(
            modifier = Modifier
                .size(300.dp)
                .align(Alignment.Center)
                .rotate((realRotationHour).toFloat())
                .background(color = Color.Transparent)
        ) {
            drawHourHand(this)
        }




        Canvas(
            modifier = Modifier
                .size(300.dp)
                .align(Alignment.Center)
                .rotate(minuteRotation.toFloat())
                .background(color = Color.Transparent)
        ) {
            drawMinuteHand(this)
        }
    }



}


@Composable
fun Android12ClockBubbles(modifier: Modifier = Modifier) {

    BoxWithConstraints(modifier = modifier.fillMaxSize()) {

        /*
        * Set the maxHeight and Max Width
        *
        * Set a random currentXPosition &7 YPosition for
        *
        * Each Bubble
        *
        *
        * */
        val maxHeight = this.constraints.maxHeight
        val maxWidth = this.constraints.maxWidth

        var currentXPosition = 5

        var currentYUPosition = 5

        var currentRandomSize = 5

        val randomColors = listOf(0xFF987126, 0xFFc3814d, 0xFFbc8365)

        var maxNumberOfMarbles = 300
        var mergeListOfXCodinates = mutableListOf<IntRange>()
        var mergeListOfYCodinates = mutableListOf<IntRange>()

        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .background(color = Color.Transparent)
        ) {

            for (i in 0 until maxNumberOfMarbles) {

                /*
                * Get the currentX and CurrentY
                * Positions from a range of values
                * Fon currentXPosition to Max position - Same for Y
                *
                *
                * */
                currentXPosition = (5..maxWidth).random()
                currentYUPosition = (5..maxHeight).random()


                /*
                *
                * We want the currentRandomsize to be
                * between 8 and 100
                *
                * */
                currentRandomSize = (8..100).random()

                /*
                *
                * This if helps make space for the middle Big Marble
                * the current position should not be within tha range
                * */
                if (currentXPosition !in (maxWidth / 2 - 10)..(maxWidth / 2 + 10) && currentYUPosition !in (maxHeight / 2 - 10)..(maxHeight / 2 + 10)) {




                    drawCircle(
                        color = Color(randomColors.random()),
                        radius = (currentRandomSize / 2).toFloat(),
                        center = Offset(
                            x = currentXPosition.toFloat(),
                            y = currentYUPosition.toFloat()
                        )
                    )


                } else {
                    maxNumberOfMarbles++
                    continue
                }

            }

        }

        Log.d("Tag", "Height: $maxHeight Width: $maxWidth")

    }


}


fun drawMinuteHand(drawScope: DrawScope) {
    drawScope.inset {
        val canvasWidth = size.width
        val canvasHeight = size.height

        val radius = size.width / 2
        val mX = size.width / 2
        val mY = size.height / 2


        val angle = (2.0 * PI * 0) / 12
        Log.d(
            "Angle",
            "x = ${Math.toDegrees(angle)}"
        )

        withTransform({
        }) {
            drawLine(
                start = Offset(
                    x = canvasWidth / 2,
                    y = canvasHeight / 2
                ),
                end = Offset(
                    x = (mX + 0.6 * radius * (cos(angle - Math.toRadians(90.0)).toFloat())).toFloat(),
                    y = (mY + 0.6 * radius * (sin(angle - Math.toRadians(90.0))).toFloat()).toFloat()
                ),
                color = Color(0xFF807652),
                strokeWidth = 70F, cap = StrokeCap.Round
            )
        }


    }
}

fun drawHourHand(drawScope: DrawScope) {
    drawScope.inset {
        val canvasWidth = size.width
        val canvasHeight = size.height

        /**
         *
         * Same principle goes for drawing the minute hand
         *
         *  First calculate for the radius and the midpoint
         * of the Box
         *
         * mX  && mY - > Middle of Box
         *
         *
         *   * */
        val radius = size.width / 2
        val mX = size.width / 2
        val mY = size.height / 2


        /*
        * Then calculate for the angle (NB: In Radians.. kotlin uses radians)
        * at 0 o'clock
        *
        *
        * */

        val angle = (2.0 * PI * 0) / 12


        /*
        * FInally let's draw the hour hand using some Geometry
        *
        *  (0.45 * radius) is a fraction of the radius to determine how long our clock
        * hand would be
        *
        * You can get more in depth knowledge if had a circle plotted on
        * a graph (having it's x and y co-ordinates as that of an android   screen)
        * then try to find the points using the angle it forms from a centre point
        *
        *
        * */

        drawLine(
            start = Offset(
                x = canvasWidth / 2,
                y = canvasHeight / 2
            ),
            end = Offset(
                x = (mX + 0.45 * radius * (cos(angle - Math.toRadians(90.0)).toFloat())).toFloat(),
                y = (mY + 0.45 * radius * (sin(angle - Math.toRadians(90.0))).toFloat()).toFloat()
            ),
            color = Color(0xFF5E401C),
            strokeWidth = 70F, cap = StrokeCap.Round
        )
    }
}

fun mutiplesOfNumber(number: Int): List<Double> {
    var multiple = 0
    var list = mutableListOf<Double>()
    list.add(0.0)
    for (i in 0 until 12) {
        multiple += number
        list.add(multiple.toDouble())
    }
    Log.d("Tag", "${list.joinToString()}")
    return list
}

fun Double.toOneDp(): Double {
    return String.format("%.1f", this).toDouble()
}