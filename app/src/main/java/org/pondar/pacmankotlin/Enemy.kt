package org.pondar.pacmankotlin

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory

class Enemy(var posx: Int, var posy: Int, private var context: Context) {
    val enemyBitmap: Bitmap
    var alive = true
    var acceleration = 0

    init {
        val ogBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.enemy)
        enemyBitmap = Bitmap.createScaledBitmap(ogBitmap, 70, 70, true)
    }
}