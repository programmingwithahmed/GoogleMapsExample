package com.programmingwithahmed.googlemapsexample

import android.content.res.Resources
import android.graphics.*
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation
import java.security.MessageDigest


class GlideCircleWithBorder(borderWidth: Int, borderColor: Int) :
    BitmapTransformation() {

    private var mBorderPaint: Paint = Paint()
    private var mBorderWidth: Float = Resources.getSystem().displayMetrics.density * borderWidth

    init {

        mBorderPaint.isDither = true
        mBorderPaint.isAntiAlias = true
        mBorderPaint.color = borderColor
        mBorderPaint.style = Paint.Style.STROKE
        mBorderPaint.strokeWidth = mBorderWidth

    }

    override fun transform(
        pool: BitmapPool,
        toTransform: Bitmap,
        outWidth: Int,
        outHeight: Int
    ): Bitmap {

        return circleCrop(toTransform)
    }


    private fun circleCrop(source: Bitmap): Bitmap {


        val size = (Math.min(source.width, source.height) - mBorderWidth / 2).toInt()
        val x = (source.width - size) / 2
        val y = (source.height - size) / 2
        val squared = Bitmap.createBitmap(source, x, y, size, size)
        val result: Bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)


        //Create a brush Canvas Manually draw a border
        val canvas = Canvas(result)
        val paint = Paint()
        paint.shader = BitmapShader(squared, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
        paint.isAntiAlias = true

        val r = size / 2f
        canvas.drawCircle(r, r, r, paint)
        val borderRadius = r - mBorderWidth / 2
        canvas.drawCircle(r, r, borderRadius, mBorderPaint)

        return result
    }

    override fun updateDiskCacheKey(messageDigest: MessageDigest) {}


}