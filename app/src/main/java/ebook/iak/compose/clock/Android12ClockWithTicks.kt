package ebook.iak.compose.clock

import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ebook.iak.compose.dpToPx
import java.util.*
import kotlin.math.*

/**
 * @author Isaac Akakpo
 * Created on 12/5/2021 4:59 PM
 */


@Composable
fun ClockAnim2() {
    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        Box(modifier = Modifier.size(300.dp)) {

        }
    }

}

@Preview(showBackground = false)
@Composable
fun ClockAnimation2() {

    val context = LocalContext.current
    val interactionSource = MutableInteractionSource()
    val canvasSize = 300.dp

    val cal = Calendar.getInstance()
    val hour = cal.get(Calendar.HOUR)
    val minute = cal.get(Calendar.MINUTE)
    val minuteAngle =  Math.toDegrees((2.0 * PI * minute) / 60)

    val hourAngle = Math.toDegrees((2.0 * PI * hour) / 12 ) + minuteAngle/12.0

    Log.d("Time","Hr $hourAngle : $minuteAngle")


    var minuteRotation by remember {
        mutableStateOf(0.0)
    }


    var previuosMinuteRotation by remember {
        mutableStateOf(-0.1)
    }

    var hourFactor by remember {
        mutableStateOf(0)
    }

    var minuteRotationDividend by remember {
      mutableStateOf(minuteRotation / 12.0)
    }
    var isFirst by remember {
        mutableStateOf(true)
    }

    var isPending by remember {
        mutableStateOf(false)
    }

    var hourRotation by remember {
        mutableStateOf(0.0)
    }

    var hourRotationStep by remember {
        mutableStateOf(0.0)
    }

    var mainHourRotationValue by remember {
        mutableStateOf(0.0)
    }

    var yoyo by remember {
        mutableStateOf(0.0)
    }


    var offSetMargin by remember {
        mutableStateOf(0.0)
    }

    var countT by remember {
        mutableStateOf(minuteAngle)
    }

    var xDragAmount by remember {
        mutableStateOf(0.0F)
    }

    var isFirstNowClockwise by remember {
        mutableStateOf(true)
    }

    var isFirstNowAntiClock by remember {
        mutableStateOf(true)
    }

    var isFromClockWise by remember {
        mutableStateOf(true)
    }






    val modifier = Modifier.pointerInput(interactionSource) {
        detectTapGestures { offset ->
            val canvasRadius = context.dpToPx(((canvasSize.value / 2).toInt()))
            val rotationCal = Math.toDegrees(
                atan2(
                    y = ((canvasRadius - offset.y).toDouble()),
                    x = (offset.x - canvasRadius).toDouble()
                ) - Math.toRadians(90.0)
            )



            minuteRotation =
                if (rotationCal <= 0) rotationCal.absoluteValue else 360.0 - rotationCal


            if (previuosMinuteRotation <= minuteRotation){
                previuosMinuteRotation = minuteRotation
            }else{
                //previuosMinuteRotation += minuteRotation
            }



         /*  if (lastMinuteRotation < minuteRotation){
                hourRotation += minuteRotation / 30
            }else{
                hourRotation -= minuteRotation / 30
            }

            countT = hourRotation - 5.0
            lastMinuteRotation = minuteRotation*/
            //hourRotation =  minuteRotation / 12
            Log.d("previuosMinuteRotation", "Previous : ${previuosMinuteRotation} Now : $minuteRotation")
        }
    }
    var isReset by remember {
        mutableStateOf(false)
    }

    var listOfNumbers by remember {
       mutableStateOf(mutiplesOfNumber(360))
    }

    Log.d("Tag","${listOfNumbers.joinToString()}")

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

                    fun isClockWise(): Boolean {
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

                        if (minuteRotationNow.toInt() in 300..360 && minuteRotationPrevious.toInt() in 0..30){
                            minuteRotationPrevious = 360.0

                        }

                        return minuteRotationNow > minuteRotationPrevious
                    }

                    
                    minuteRotation =
                        if (rotationCal <= 0) rotationCal.absoluteValue else 360.0 - rotationCal



                    if (isClockWise()) {
                        if (!isFromClockWise){
                            isFromClockWise = true
                            hourFactor++
                        }
                        if (previuosMinuteRotation <= minuteRotation) {
                            previuosMinuteRotation = minuteRotation
                            yoyo = (mainHourRotationValue / 12) + (minuteRotation / 12)
                        } else {

                            if (yoyo == 360.0) {
                                yoyo = 0.0
                            }
                            hourFactor++
                            previuosMinuteRotation = minuteRotation
                            if (hourFactor >= 12) {
                                hourFactor = 0
                                mainHourRotationValue = 0.0
                            }
                            mainHourRotationValue = minuteRotation + listOfNumbers[hourFactor]
                        }

                    }else{

                        if (isFromClockWise ){
                            isFromClockWise = false
                            hourFactor--
                        }
                        if (previuosMinuteRotation >= minuteRotation) {
                            previuosMinuteRotation = minuteRotation
                            yoyo = mainHourRotationValue / 12 + minuteRotation / 12
                        } else {

                            if (yoyo <= 0.0) {
                                yoyo = 360.0
                            }
                            if (hourFactor >= 0){
                                hourFactor--
                            }
                            previuosMinuteRotation = minuteRotation
                            if (hourFactor < 0) {
                                hourFactor = 10
                                mainHourRotationValue = 0.0
                            }

                            mainHourRotationValue = minuteRotation + listOfNumbers[hourFactor]


                        }

                    }

                    Log.d("mainHourRotationValue", "${mainHourRotationValue}")
                    Log.d("hourFactor", "$hourFactor")
                    Log.d("yoyo", "$yoyo")
                    Log.d("called", "$yoyo")

                    Log.d(
                        "previuosMinuteRotation",
                        "Previous : ${previuosMinuteRotation} Now : $minuteRotation"
                    )


                    if (minuteRotationDividend <= 0.1 && !isFirst && !isPending) {
                        hourRotationStep += 30.0
                        isPending = true
                    }

                    if (minuteRotationDividend > 27.0 && isPending) {
                        isPending = false
                    }
                    hourRotation = hourRotationStep + minuteRotation / 12





                    Log.d("minuteRotation", "$minuteRotation")

                    if (countT < 360.0) {

                        countT += 1

                    } else {
                        countT = 0.0
                    }

                    if (offSetMargin.toInt() != (minuteRotation / 12).toInt()) {

                        offSetMargin = minuteRotation / 12





                        Log.d("clockwise $countT", "true")

                        //hourRotation = countT.toOneDp()
                    }


                }

            }

    )
    {

        Canvas(
            modifier = Modifier
                .size(canvasSize)
                .background(color = Color.Transparent)
        ) {
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

                /*
                * let's first calculate for the angle of each clock number
                * NB : Kotlin uses radians instead of degree
                *
                * And Angle for the whole clock surface is 360 degrees === 2𝝅 (pie) radians
                *Meaning each angle should be 30 degrees
                *
                *
               * */
                val angle = (2.0 * PI * hourMark) / 12

                /*
                * We then rotate the angle by 90 degrees anticlockwise to land the point at x = 1 and y = 0
                *
                *   --------------------
                * `
                *
                *
                *
                *
                *
                * */
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

        }


        Canvas(
            modifier = Modifier
                .size(canvasSize)
                .rotate((yoyo).toFloat())
                .background(color = Color.Transparent)
        ) {
            drawHourHand(this)
        }




        Canvas(
            modifier = Modifier
                .size(canvasSize)
                .rotate(minuteRotation.toFloat())
                .background(color = Color.Transparent)
        ) {
            drawMinuteHand(this)
        }
    }

}


fun drawMinuteHand2(drawScope: DrawScope) {
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

fun drawHourHand2(drawScope: DrawScope) {
    drawScope.inset {
        val canvasWidth = size.width
        val canvasHeight = size.height

        val radius = size.width / 2
        val mX = size.width / 2
        val mY = size.height / 2


        val angle = (2.0 * PI * 0) / 12

        withTransform({
        }) {
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
}

fun mutiplesOfNumber2(number:Int):List<Double>{
    var multiple = 0
    var list = mutableListOf<Double>()
    list.add(0.0)
    for (i in 0 until 12){
        multiple += number
        list.add(multiple.toDouble())
    }
    return list
}

fun Double.toOneDp2():Double{
   return String.format("%.1f", this).toDouble()
}