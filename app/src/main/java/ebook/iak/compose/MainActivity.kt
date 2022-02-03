package ebook.iak.compose

import android.Manifest
import android.app.WallpaperManager
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import ebook.iak.compose.clock.ClockAnimation
import ebook.iak.compose.ui.theme.AnimatingComposeTheme

import android.graphics.drawable.BitmapDrawable
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.core.content.ContextCompat
import android.view.WindowManager

import android.os.Build
import android.util.Log
import android.util.TypedValue
import android.view.Window
import androidx.compose.animation.*
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import ebook.iak.compose.clock.Android12ClockBubbles
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt


class MainActivity : ComponentActivity() {

    var wallpaperDrawable: Drawable? = null

    @ExperimentalComposeUiApi
    @ExperimentalAnimationApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val w: Window = window
            w.setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
            )
        }
        setContent {
            AnimatingComposeTheme {
                // A surface container using the 'background' color from the theme
                val scope = rememberCoroutineScope()

                val wallpaperManager = WallpaperManager.getInstance(this);
                var isImageAvailable by remember {
                    mutableStateOf(false)
                }
                val launcher = rememberLauncherForActivityResult(
                    ActivityResultContracts.RequestPermission()
                ) { isGranted: Boolean ->
                    if (isGranted) {
                        // Permission Accepted: Do something
                        wallpaperDrawable = wallpaperManager.drawable
                        isImageAvailable = true
                    }
                }
                val wallpaperDrawable: Drawable? = if (ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    isImageAvailable = true
                    wallpaperManager.drawable;
                } else {
                    SideEffect {
                        launcher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                    }
                    isImageAvailable = false
                    null
                }


                var isShow by remember {
                    mutableStateOf(false)
                }

                var isShow12 by remember {
                    mutableStateOf(false)
                }

                Box(modifier = Modifier.fillMaxSize()) {
                    val modifier = Modifier.fillMaxSize()

                    if (isImageAvailable && wallpaperDrawable != null) {
                        ShowWallPaper(drawable = wallpaperDrawable)
                    }


                    if (isShow) {
                        Android12ClockBubbles(modifier)
                    }

                    ShowAndroid12Circles(context = this@MainActivity,isShow12)



                    if (!isShow12){
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            ClockAnimation(){hourRotation ->
                                Log.d("Release","$hourRotation")
                                if (hourRotation in 359.0..360.0 ||hourRotation in 0.0..0.06){
                                    isShow12 = true
                                    scope.launch {
                                        delay(700)
                                        isShow = true
                                    }
                                }
                            }
                        }
                    }

                }
            }
        }
    }
}


@Composable
fun ShowWallPaper(drawable: Drawable) {

    val imgBitMap = (drawable as BitmapDrawable).bitmap.asImageBitmap()
    Image(
        bitmap = imgBitMap,
        contentDescription = "",
        modifier = Modifier.fillMaxSize(),
        contentScale = ContentScale.Crop
    )

}


@Composable
fun ShowAndroid12Circles(context: Context,isShow:Boolean) {

    val size: Dp by animateDpAsState(if (isShow) 200.dp else 0.dp,animationSpec = tween(400))


    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.Transparent)
    ) {


        Surface(
            shape = RoundedCornerShape(100),
            color = Color(0xFF987126),
            modifier = Modifier
                .size(size)
                .align(Alignment.Center)
                )
        {
            Box(modifier = Modifier.align(Alignment.Center)) {
                Text(
                    text = "12",
                    modifier = Modifier.align(Alignment.Center),
                    fontSize = 100.sp,
                    color = Color.White,fontFamily = FontFamily.Cursive
                )
            }


        }

    }

}

fun dpToFloat(context: Context, size: Float): Float {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP, size,
        context.getResources().displayMetrics
    )
}


@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    AnimatingComposeTheme {
        TestModifierOffset()
    }
}