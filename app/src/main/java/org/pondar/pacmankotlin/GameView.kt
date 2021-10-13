package org.pondar.pacmankotlin

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.util.Log
import android.view.View


// Custom GameView class that extends inbuilt View
class GameView : View {
    private lateinit var game: Game
    var h: Int = 0
    var w: Int = 0 //used for storing our height and width of the view

    fun setGame(game: Game) {
        this.game = game
    }

    /* The next 3 constructors are needed for the Android view system,
	when we have a custom view.
	 */
    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)


    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    // Runs whenever the screen is updated
    override fun onDraw(canvas: Canvas) {
        // height and width
        h = height
        w = width

        //update the size for the canvas to the game.
        game.setSize(h, w)
        Log.d("GAMEVIEW", "h = $h, w = $w")

        // initialize enemies and coins if not initialized
        if (!(game.coinsInitialized))
            game.initializeGoldcoins()

        if (!(game.enemiesInitialized))
            game.initializeEnemies()


        //Making a new paint object
        val paint = Paint()
        canvas.drawColor(Color.WHITE) //clear entire canvas to white color

        //draw the pacman
        canvas.drawBitmap(game.pacBitmap, game.pacx.toFloat(),
                game.pacy.toFloat(), paint)

        for (cookie in game.coins)
            if (!(cookie.taken))
                canvas.drawBitmap(cookie.cookieBitmap, cookie.posx.toFloat(),
                    cookie.posy.toFloat(), paint)

        for (enemy in game.enemies)
            if (enemy.alive)
                canvas.drawBitmap(enemy.enemyBitmap, enemy.posx.toFloat(),
                    enemy.posy.toFloat(), paint)

        game.doCollisionCheck()
        super.onDraw(canvas)
    }

}
