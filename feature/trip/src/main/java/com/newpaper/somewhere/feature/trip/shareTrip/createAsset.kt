package com.newpaper.somewhere.feature.trip.shareTrip

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.HardwareRenderer
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PixelFormat
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.RenderEffect
import android.graphics.RenderNode
import android.graphics.Shader
import android.hardware.HardwareBuffer
import android.media.ImageReader
import android.net.Uri
import android.os.Build
import android.renderscript.Allocation
import android.renderscript.Element
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicBlur
import androidx.annotation.RequiresApi
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.FileProvider
import androidx.core.content.res.ResourcesCompat
import com.newpaper.somewhere.core.ui.designsystem.R
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


fun createStickerOnlyTextUri(
    context: Context,
    tripTitle: String?,
    tripStartDate: String?,
    tripEndDate: String?,
): Uri {
    // --- 스티커 디자인 요소 정의 및 스케일링 ---
    val targetWidthSide = 1280f
    val bottomBoxHeight = 256f

    val cornerRadius = 60f

    val horizontalPadding = 48f
    val verticalPadding = 38f

    val titleTextSize = 80f
    val dateTextSize = 50f
    val somewhereTextSize = 28f

    val somewhereLogoSize = 60

    // 2. 최종 결과물(이미지 + 하단 박스)을 담을 새 비트맵을 생성합니다.
    val resultBitmap = Bitmap.createBitmap(
        targetWidthSide.toInt(),
        bottomBoxHeight.toInt(),
        Bitmap.Config.ARGB_8888
    )
    val canvas = Canvas(resultBitmap)

    // 4. 하단에 추가될 반투명 흰색 박스를 그립니다.
    val boxPaint = Paint().apply {
        color = Color.WHITE // 흰색
        alpha = 153 // 60% 투명도 (255 * 0.6)
    }
    val boxRect = RectF(0f, 0f, resultBitmap.width.toFloat(), resultBitmap.height.toFloat())
    canvas.drawRect(boxRect, boxPaint)

    // 5. 텍스트를 그릴 Paint 객체를 설정합니다. (폰트, 색상, 크기 등)
    val pretendardRegularTypeface = ResourcesCompat.getFont(context, R.font.pretendard_regular)
    val pretendardBoldTypeface = ResourcesCompat.getFont(context, R.font.pretendard_bold)
    val pretendardSemiBoldTypeface = ResourcesCompat.getFont(context, R.font.pretendard_semi_bold)

    val titlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.BLACK
        textSize = titleTextSize
        typeface = pretendardBoldTypeface
    }

    val datePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.BLACK
        textSize = dateTextSize
        typeface = pretendardRegularTypeface
    }

    val longDashPaint = Paint(datePaint).apply {
        color = Color.BLACK
        textSize = dateTextSize
        typeface = pretendardRegularTypeface
        alpha = 100
    }

    val madeWithPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.BLACK
        textSize = somewhereTextSize
        typeface = pretendardRegularTypeface
        textAlign = Paint.Align.RIGHT
        alpha = 210
    }

    val somewherePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.BLACK
        textSize = somewhereTextSize
        typeface = pretendardSemiBoldTypeface
        textAlign = Paint.Align.RIGHT
        alpha = 210
    }

    //logo icon
    val somewhereLogoDrawable = AppCompatResources.getDrawable(context, com.newpaper.somewhere.core.ui.ui.R.drawable.app_icon_fit)
    val logoIconBitmap = somewhereLogoDrawable?.let {
        val bitmap = Bitmap.createBitmap(somewhereLogoSize, somewhereLogoSize, Bitmap.Config.ARGB_8888)
        val canvasForIcon = Canvas(bitmap)
        it.setBounds(0, 0, somewhereLogoSize, somewhereLogoSize)
        it.draw(canvasForIcon)
        bitmap
    }

    logoIconBitmap?.let {
        val iconX = resultBitmap.width - horizontalPadding - it.width
        val iconY = resultBitmap.height - verticalPadding - it.height
        canvas.drawBitmap(it, iconX.toFloat(), iconY.toFloat(), null)
    }

    // 6. 텍스트를 하단 박스에 그립니다.
    //trip title
    tripTitle?.let {
        val titleY = verticalPadding + titlePaint.textSize
        canvas.drawText(it, horizontalPadding, titleY, titlePaint)
    }

    //date
    if (tripStartDate != null && tripEndDate != null) {
        val longDashText = "  —  "
        val dateY = resultBitmap.height - horizontalPadding

        // start date
        var currentX = horizontalPadding
        canvas.drawText(tripStartDate, currentX, dateY, datePaint)

        // long dash
        currentX += datePaint.measureText(tripStartDate)
        canvas.drawText(longDashText, currentX, dateY, longDashPaint)

        // end date
        currentX += longDashPaint.measureText(longDashText)
        canvas.drawText(tripEndDate, currentX, dateY, datePaint)
    }

    //made with somewhere
    val madeWithText = "MADE WITH"
    val somewhereText = "SOMEWHERE "
    val appText = "APP"

    val madeWithX = resultBitmap.width - horizontalPadding - somewhereLogoSize - 14 // 우측 정렬 기준 X좌표

    // "SOMEWHERE APP" 그리기 (아래 줄)
    val appNameY = resultBitmap.height - verticalPadding

    //"APP"
    canvas.drawText(appText, madeWithX, appNameY, madeWithPaint)

    //"SOMEWHERE"
    val appTextWidth = madeWithPaint.measureText(appText)
    val somewhereX = madeWithX - appTextWidth
    canvas.drawText(somewhereText, somewhereX, appNameY, somewherePaint)

    //"MADE WITH"
    val madeWithY = appNameY - somewherePaint.textSize - 8
    canvas.drawText(madeWithText, madeWithX, madeWithY, madeWithPaint)


    // 7. 모서리를 둥글게 처리하는 최종 단계를 수행합니다.
    val roundedBitmap = Bitmap.createBitmap(resultBitmap.width, resultBitmap.height, Bitmap.Config.ARGB_8888)
    val roundedCanvas = Canvas(roundedBitmap)
    val finalPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    val rect = Rect(0, 0, resultBitmap.width, resultBitmap.height)
    val rectF = RectF(rect)

    val path = Path()
    path.addRoundRect(rectF, cornerRadius, cornerRadius, Path.Direction.CW)
    roundedCanvas.clipPath(path)
    roundedCanvas.drawBitmap(resultBitmap, rect, rect, finalPaint)
    resultBitmap.recycle() // 중간 결과물 비트맵도 메모리 해제

    // 8. 최종적으로 완성된 비트맵을 캐시 파일로 저장합니다.
    val outputFile = File(context.cacheDir, "sticker_${System.currentTimeMillis()}.png")
    FileOutputStream(outputFile).use {
        roundedBitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
    }

    // 9. FileProvider를 사용하여 파일의 URI를 생성하고 반환합니다.
    val authority = "${context.packageName}.fileprovider"
    return FileProvider.getUriForFile(context, authority, outputFile)
}

fun createStickerWithTextUri(
    context: Context,
    imageFile: File,
    tripTitle: String?,
    tripStartDate: String?,
    tripEndDate: String?,
): Uri {

    // 1. 원본 이미지를 비트맵으로 디코딩합니다.
    val originalBitmap = BitmapFactory.decodeFile(imageFile.absolutePath).copy(Bitmap.Config.ARGB_8888, true)

    // --- 이미지 리사이징 및 디자인 요소 스케일링 ---
    val targetWidthSide = 1280f
    val originalWidth = originalBitmap.width.toFloat()
    val originalHeight = originalBitmap.height.toFloat()

    // width to 640
    val scale = targetWidthSide / originalWidth

    val newImageWidth = (originalWidth * scale).toInt()
    val newImageHeight = (originalHeight * scale).toInt()

    // 계산된 비율로 비트맵을 리사이즈합니다.
    val resizedBitmap = Bitmap.createScaledBitmap(originalBitmap, newImageWidth, newImageHeight, true)
    originalBitmap.recycle() // 원본 비트맵 메모리 해제

    // --- 스티커 디자인 요소 정의 및 스케일링 ---
    val cornerRadius = 60f
    val bottomBoxHeight = 256f

    val horizontalPadding = 48f
    val verticalPadding = 38f

    val titleTextSize = 80f
    val dateTextSize = 50f
    val somewhereTextSize = 28f

    val somewhereLogoSize = 60

    // 2. 최종 결과물(이미지 + 하단 박스)을 담을 새 비트맵을 생성합니다.
    val resultBitmap = Bitmap.createBitmap(
        newImageWidth,
        newImageHeight + bottomBoxHeight.toInt(),
        Bitmap.Config.ARGB_8888
    )
    val canvas = Canvas(resultBitmap)

    // 3. 캔버스에 리사이즈된 이미지를 먼저 그립니다.
    canvas.drawBitmap(resizedBitmap, 0f, 0f, null)
    resizedBitmap.recycle() // 리사이즈된 비트맵도 이제 필요 없으므로 메모리 해제

    // 4. 하단에 추가될 반투명 흰색 박스를 그립니다.
    val boxPaint = Paint().apply {
        color = Color.WHITE // 흰색
        alpha = 153 // 60% 투명도 (255 * 0.6)
    }
    val boxRect = RectF(0f, newImageHeight.toFloat(), resultBitmap.width.toFloat(), resultBitmap.height.toFloat())
    canvas.drawRect(boxRect, boxPaint)

    // 5. 텍스트를 그릴 Paint 객체를 설정합니다. (폰트, 색상, 크기 등)
    val pretendardRegularTypeface = ResourcesCompat.getFont(context, R.font.pretendard_regular)
    val pretendardBoldTypeface = ResourcesCompat.getFont(context, R.font.pretendard_bold)
    val pretendardSemiBoldTypeface = ResourcesCompat.getFont(context, R.font.pretendard_semi_bold)

    val titlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.BLACK
        textSize = titleTextSize
        typeface = pretendardBoldTypeface
    }

    val datePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.BLACK
        textSize = dateTextSize
        typeface = pretendardRegularTypeface
    }

    val longDashPaint = Paint(datePaint).apply {
        color = Color.BLACK
        textSize = dateTextSize
        typeface = pretendardRegularTypeface
        alpha = 100
    }

    val madeWithPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.BLACK
        textSize = somewhereTextSize
        typeface = pretendardRegularTypeface
        textAlign = Paint.Align.RIGHT
        alpha = 210
    }

    val somewherePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.BLACK
        textSize = somewhereTextSize
        typeface = pretendardSemiBoldTypeface
        textAlign = Paint.Align.RIGHT
        alpha = 210
    }

    //logo icon
    val somewhereLogoDrawable = AppCompatResources.getDrawable(context, com.newpaper.somewhere.core.ui.ui.R.drawable.app_icon_fit)
    val logoIconBitmap = somewhereLogoDrawable?.let {
        val bitmap = Bitmap.createBitmap(somewhereLogoSize, somewhereLogoSize, Bitmap.Config.ARGB_8888)
        val canvasForIcon = Canvas(bitmap)
        it.setBounds(0, 0, somewhereLogoSize, somewhereLogoSize)
        it.draw(canvasForIcon)
        bitmap
    }

    logoIconBitmap?.let {
        val iconX = resultBitmap.width - horizontalPadding - it.width
        val iconY = resultBitmap.height - verticalPadding - it.height
        canvas.drawBitmap(it, iconX.toFloat(), iconY.toFloat(), null)
    }

    // 6. 텍스트를 하단 박스에 그립니다.
    //trip title
    tripTitle?.let {
        val titleY = newImageHeight + verticalPadding + titlePaint.textSize
        canvas.drawText(it, horizontalPadding, titleY, titlePaint)
    }

    //date
    if (tripStartDate != null && tripEndDate != null) {
        val longDashText = "  —  "
        val dateY = resultBitmap.height - horizontalPadding

        // start date
        var currentX = horizontalPadding
        canvas.drawText(tripStartDate, currentX, dateY, datePaint)

        // long dash
        currentX += datePaint.measureText(tripStartDate)
        canvas.drawText(longDashText, currentX, dateY, longDashPaint)

        // end date
        currentX += longDashPaint.measureText(longDashText)
        canvas.drawText(tripEndDate, currentX, dateY, datePaint)
    }

    //made with somewhere
    val madeWithText = "MADE WITH"
    val somewhereText = "SOMEWHERE "
    val appText = "APP"

    val madeWithX = resultBitmap.width - horizontalPadding - somewhereLogoSize - 14 // 우측 정렬 기준 X좌표

    // "SOMEWHERE APP" 그리기 (아래 줄)
    val appNameY = resultBitmap.height - verticalPadding

    //"APP"
    canvas.drawText(appText, madeWithX, appNameY, madeWithPaint)

    //"SOMEWHERE"
    val appTextWidth = madeWithPaint.measureText(appText)
    val somewhereX = madeWithX - appTextWidth
    canvas.drawText(somewhereText, somewhereX, appNameY, somewherePaint)

    //"MADE WITH"
    val madeWithY = appNameY - somewherePaint.textSize - 8
    canvas.drawText(madeWithText, madeWithX, madeWithY, madeWithPaint)


    // 7. 모서리를 둥글게 처리하는 최종 단계를 수행합니다.
    val roundedBitmap = Bitmap.createBitmap(resultBitmap.width, resultBitmap.height, Bitmap.Config.ARGB_8888)
    val roundedCanvas = Canvas(roundedBitmap)
    val finalPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    val rect = Rect(0, 0, resultBitmap.width, resultBitmap.height)
    val rectF = RectF(rect)

    val path = Path()
    path.addRoundRect(rectF, cornerRadius, cornerRadius, Path.Direction.CW)
    roundedCanvas.clipPath(path)
    roundedCanvas.drawBitmap(resultBitmap, rect, rect, finalPaint)
    resultBitmap.recycle() // 중간 결과물 비트맵도 메모리 해제

    // 8. 최종적으로 완성된 비트맵을 캐시 파일로 저장합니다.
    val outputFile = File(context.cacheDir, "sticker_${System.currentTimeMillis()}.png")
    FileOutputStream(outputFile).use {
        roundedBitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
    }

    // 9. FileProvider를 사용하여 파일의 URI를 생성하고 반환합니다.
    val authority = "${context.packageName}.fileprovider"
    return FileProvider.getUriForFile(context, authority, outputFile)
}




fun createBlurBackgroundUri(
    context: Context,
    sourceFile: File,
    blurRadius: Float = 25f
): Uri? {
    // Wrap the entire process in a try-catch block to handle potential errors
    // during file I/O or bitmap processing.
    try {
        // 1. Decode the source image file into a bitmap
        val originalBitmap = BitmapFactory.decodeFile(sourceFile.absolutePath)
            ?: throw IOException("Could not decode source file.")

        // 2. Apply the blur effect using the appropriate method for the Android version
//        val blurredBitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
//            // Use the modern, hardware-accelerated RenderEffect API for Android 12+
//            applyRenderEffectBlur(originalBitmap, blurRadius)
//        } else {
//            // Use the legacy RenderScript library for older versions
//            applyRenderScriptBlur(context, originalBitmap, blurRadius)
//        }

        val blurredBitmap = applyRenderScriptBlur(context, originalBitmap, blurRadius)
        val blurred916Bitmap = to916ratio(blurredBitmap)


        // 3. Save the blurred bitmap to a new file in the app's cache directory
        // This avoids needing external storage permissions.
        val outputFile = File(context.cacheDir, "blurred_${System.currentTimeMillis()}.png")

        FileOutputStream(outputFile).use { out ->
            blurred916Bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
        }

        if (blurredBitmap != blurred916Bitmap) {
            blurredBitmap.recycle()
        }
        blurred916Bitmap.recycle()
        originalBitmap.recycle()

        // 4. Get a content URI for the new file using a FileProvider
        // The authority must match the one defined in your AndroidManifest.xml
        val authority = "${context.packageName}.fileprovider"
        return FileProvider.getUriForFile(context, authority, outputFile)

    } catch (e: Exception) {
        // Log the exception for debugging purposes
        e.printStackTrace()
        return null
    }
}

fun createNotBlurBackgroundUri(
    context: Context,
    sourceFile: File,
): Uri? {
    // 1. 원본 이미지를 불러옵니다.
    val originalBitmap = BitmapFactory.decodeFile(sourceFile.absolutePath) ?: return null

    // 2. 9:16 비율로 크롭
    val croppedBitmap = to916ratio(originalBitmap)

    // 3. 임시 파일로 저장
    val outputFile = File(context.cacheDir, "not_blur_${System.currentTimeMillis()}.png")
    FileOutputStream(outputFile).use { out ->
        croppedBitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
    }

    // 4. 메모리 해제
    if (originalBitmap != croppedBitmap) {
        originalBitmap.recycle()
    }
    croppedBitmap.recycle()

    // 5. URI 반환
    val authority = "${context.packageName}.fileprovider"
    return FileProvider.getUriForFile(context, authority, outputFile)
}



private fun to916ratio(
    bitmap: Bitmap,
): Bitmap{
    val originalWidth = bitmap.width
    val originalHeight = bitmap.height

    // 목표 비율 (9:16)
    val targetAspectRatio = 9.0 / 16.0

    var newWidth = originalWidth
    var newHeight = originalHeight

    // 원본 이미지의 비율과 목표 비율을 비교하여 크롭할 영역을 계산합니다.
    if (originalWidth.toDouble() / originalHeight > targetAspectRatio) {
        // 원본이 가로로 더 긴 경우: 너비를 줄여서 9:16 비율을 맞춥니다.
        newWidth = (originalHeight * targetAspectRatio).toInt()
    } else {
        // 원본이 세로로 더 길거나 같은 경우: 높이를 줄여서 9:16 비율을 맞춥니다.
        newHeight = (originalWidth / targetAspectRatio).toInt()
    }

    // 크롭할 시작점을 계산합니다 (중앙 크롭).
    val x = (originalWidth - newWidth) / 2
    val y = (originalHeight - newHeight) / 2

    // 계산된 위치와 크기로 비트맵을 자릅니다.
    return Bitmap.createBitmap(bitmap, x, y, newWidth, newHeight)
}

/**
 * Applies a blur effect using RenderEffect (API 31+).
 * This is the corrected implementation.
 */
@RequiresApi(Build.VERSION_CODES.S)
private fun applyRenderEffectBlur(
    bitmap: Bitmap,
    radius: Float
): Bitmap {
    var imageReader: ImageReader? = null
    var hardwareRenderer: HardwareRenderer? = null
    var renderNode: RenderNode? = null
    var renderedImage: android.media.Image? = null
    var hardwareBuffer: HardwareBuffer? = null

    try {
        // 1. ImageReader 생성
        imageReader = ImageReader.newInstance(
            bitmap.width, bitmap.height,
            PixelFormat.RGBA_8888, 2,
            HardwareBuffer.USAGE_GPU_SAMPLED_IMAGE or HardwareBuffer.USAGE_GPU_COLOR_OUTPUT
        )

        // 2. RenderNode + HardwareRenderer 준비
        renderNode = RenderNode("BlurEffect").apply {
            setRenderEffect(
                RenderEffect.createBlurEffect(radius, radius, Shader.TileMode.CLAMP)
            )
        }

        hardwareRenderer = HardwareRenderer().apply {
            setSurface(imageReader.surface)
            setContentRoot(renderNode)
        }

        // 3. RenderNode에 원본 비트맵 그리기
        renderNode.beginRecording(bitmap.width, bitmap.height).apply {
            drawBitmap(bitmap, 0f, 0f, null)
        }.also {
            renderNode.endRecording()
        }

        // 4. GPU 렌더링 요청 (완료 대기)
        hardwareRenderer.createRenderRequest()
            .setWaitForPresent(true)
            .syncAndDraw()

        // 5. 렌더링 결과 획득
        renderedImage = imageReader.acquireLatestImage()
            ?: throw IllegalStateException("렌더링된 이미지를 가져오지 못했습니다.")

        hardwareBuffer = renderedImage.hardwareBuffer
            ?: throw IllegalStateException("HardwareBuffer를 가져오지 못했습니다.")

        return Bitmap.wrapHardwareBuffer(hardwareBuffer, null)
            ?: throw IllegalStateException("HardwareBuffer를 Bitmap으로 변환 실패.")

    } finally {
        // ⚠️ 해제 순서 중요: GPU -> Image -> Buffer -> Renderer
        hardwareBuffer?.close()
        renderedImage?.close()
        hardwareRenderer?.destroy()
        renderNode?.discardDisplayList()
        imageReader?.close()
    }
}

/**
 * Applies a blur effect using the legacy RenderScript library (pre-API 31).
 */
@Suppress("DEPRECATION") // RenderScript is deprecated but necessary for older APIs
private fun applyRenderScriptBlur(
    context: Context,
    bitmap: Bitmap,
    radius: Float
): Bitmap {
    val outputBitmap = Bitmap.createBitmap(bitmap.width, bitmap.height, bitmap.config)
    var rs: RenderScript? = null
    var input: Allocation? = null
    var output: Allocation? = null
    var script: ScriptIntrinsicBlur? = null

    try {
        // Create a RenderScript instance
        rs = RenderScript.create(context)
        // Create Allocations for the input and output bitmaps
        input = Allocation.createFromBitmap(rs, bitmap)
        output = Allocation.createFromBitmap(rs, outputBitmap)
        // Create the blur script
        script = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs))

        // Set the blur radius (RenderScript has a max of 25f)
        script.setRadius(radius.coerceIn(0.1f, 25f))
        script.setInput(input)
        // Execute the blur
        script.forEach(output)
        // Copy the result to the output bitmap
        output.copyTo(outputBitmap)
    } finally {
        // Ensure all RenderScript resources are destroyed
        input?.destroy()
        output?.destroy()
        script?.destroy()
        rs?.destroy()
    }

    return outputBitmap
}
