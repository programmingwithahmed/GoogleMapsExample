package com.programmingwithahmed.googlemapsexample

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import androidx.annotation.DrawableRes
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.toBitmap

class Utility {

    companion object {

         fun createScaledBitmap(context: Context, @DrawableRes icon: Int, width: Int, height: Int) : Bitmap {

             val bitmap = BitmapFactory.decodeResource(context.resources, icon)

            return Bitmap.createScaledBitmap(bitmap, width, height, false)
        }

    }
}