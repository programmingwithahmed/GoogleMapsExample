package com.programmingwithahmed.googlemapsexample

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.annotation.DrawableRes

class Utility {

    companion object {

        fun createScaledBitmap(
            context: Context,
            @DrawableRes icon: Int,
            width: Int,
            height: Int
        ): Bitmap {

            val bitmap = BitmapFactory.decodeResource(context.resources, icon)

            return Bitmap.createScaledBitmap(bitmap, width, height, false)
        }


        fun readTextFile(context: Context, filePath: String) : String {

            var result = ""
            context.assets.open(filePath).apply {
               result = this.readBytes().toString(Charsets.UTF_8)
            }.close()


            return result
        }

    }
}