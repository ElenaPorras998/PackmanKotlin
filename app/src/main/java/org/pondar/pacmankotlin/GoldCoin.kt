package org.pondar.pacmankotlin

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory

class GoldCoin(var posx: Int, var posy: Int, private var context: Context) {
    val cookieBitmap: Bitmap
    var taken = false

    init {
        val ogBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.cookie)
        cookieBitmap = Bitmap.createScaledBitmap(ogBitmap, 70, 70, true)
    }

    fun TakeCoin () {
        taken = true
    }
}