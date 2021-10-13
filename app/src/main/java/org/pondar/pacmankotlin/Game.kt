package org.pondar.pacmankotlin

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Handler
import android.os.Looper
import android.widget.TextView
import java.util.*
import kotlin.math.pow
import kotlin.math.abs
import kotlin.math.sqrt
import kotlin.random.Random
import android.widget.Toast


/**
 *
 * This class should contain all your game logic
 */

class Game(private var context: Context,view: TextView) {
    // Reference to the pointsView and initialization of time and points
    private var pointsView: TextView = view
    var points : Int = 0
    var counter : Int = 45

    // Current level set to 1
    var level = 1
    val maxLevel = 3

    // constants for movement direction
    val UP = 1
    val DOWN = 2
    val LEFT = 3
    val RIGHT = 4
    var direction: Int = RIGHT

    // status of the game
    val IN_PROGRESS = 1
    val STOPPED = 2
    val END = 3
    val WON = 4
    var status = IN_PROGRESS

    //bitmap of the pacman
    var pacBitmap: Bitmap
    var pacx: Int = 0
    var pacy: Int = 0

    // coins and enemies
    var coinsInitialized = false
    var enemiesInitialized = false
    var coins = ArrayList<GoldCoin>()
    var enemies = ArrayList<Enemy>()

    //a reference to the gameview
    private lateinit var gameView: GameView
    private var h: Int = 0
    private var w: Int = 0

    private val handler = Handler(Looper.getMainLooper())

    // Create a new pacman bitmap when a new game is created
    init {
        val ogBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.pacman)
        pacBitmap = Bitmap.createScaledBitmap(ogBitmap, 120, 120, true)
    }

    fun setGameView(view: GameView) {
        this.gameView = view
    }

    fun initializeGoldcoins()
    {
        coins.clear()

        for (i in 1..10) {
            coins.add(GoldCoin(randomXCoord(), randomYCoord(), context))
        }

        coinsInitialized = true
    }

    fun initializeEnemies()
    {
        enemies.clear()

        for (i in 1..3) {
            val x = (w / 2) - 100
            val enemy = Enemy((x + i*100), (h / 2), context)
            enemy.acceleration = i*5
            if (i > level)
                enemy.alive = false

            enemies.add(enemy)
        }

        enemiesInitialized = true
    }


    fun newGame() {
        // Initial coordinates for pacman
        pacx = 50
        pacy = 0

        // On start the game is stopped and pacman's direction is right
        status = STOPPED
        direction = RIGHT

        // Initialize coins and points (to zero)
        coinsInitialized = false
        enemiesInitialized = false
        points = 0
        pointsView.text = "${context.resources.getString(R.string.points)} $points"

        gameView.invalidate() //redraw screen
        handler.postDelayed({
            status = IN_PROGRESS
        }, 2000)
    }

    // Sets game size.
    fun setSize(h: Int, w: Int) {
        this.h = h
        this.w = w
    }

    /*
        * Moves the pacman on a given direction
        * Prevents pacman from colliding with a boundary
     */
    fun movePacman(direction:Int, pixels: Int) {
        when (direction) {
            RIGHT -> {
                if (pacx + pixels + pacBitmap.width < w)
                    pacx += pixels
            }
            LEFT -> {
                if (pacx - pixels > 0)
                    pacx -= pixels
            }
            UP -> {
                if (pacy - pixels > 0)
                    pacy -= pixels
            }
            DOWN -> {
                if (pacy + pixels + pacBitmap.height < h)
                    pacy += pixels
            }
        }
        doCollisionCheck()
        gameView.invalidate()
    }

    /*
        * Moves the pacman on a given direction
        * Prevents pacman from colliding with a boundary
     */
    fun moveEnemies(p: Int) {
        for (enemy in enemies) {
            val xdist = pacx - enemy.posx
            val ydist = pacy - enemy.posy
            var direction: Int
            val pixels = p + enemy.acceleration

            if (abs(xdist) < abs(ydist))
                direction = if (ydist > 0)  DOWN else UP
            else
                direction = if (xdist > 0)  RIGHT else LEFT

            when (direction) {
                RIGHT -> {
                    if (enemy.posx + pixels + enemy.enemyBitmap.width < w)
                        enemy.posx += pixels
                }
                LEFT -> {
                    if (enemy.posx - pixels > 0)
                        enemy.posx -= pixels
                }
                UP -> {
                    if (enemy.posy - pixels > 0)
                        enemy.posy -= pixels
                }
                DOWN -> {
                    if (enemy.posy + pixels + enemy.enemyBitmap.height < h)
                        enemy.posy += pixels
                }
            }
        }
    }

    // check if the pacman touches a gold coin
    fun doCollisionCheck() {
        for (coin in coins) {
            if (!coin.taken) {
                val dist = distance(
                    (pacx + pacBitmap.width / 2),
                    (pacy + pacBitmap.height / 2),
                    coin.posx + coin.cookieBitmap.width / 2,
                    coin.posy + coin.cookieBitmap.height / 2
                )
                if (dist < pacBitmap.width) {
                    points++
                    pointsView.text = "${context.resources.getString(R.string.points)} $points"
                    coin.taken = true
                }
            }
        }

        for (enemy in enemies) {
            if (enemy.alive) {
                val d = distance(
                    (pacx + pacBitmap.width / 2),
                    (pacy + pacBitmap.height / 2),
                    enemy.posx + enemy.enemyBitmap.width / 2,
                    enemy.posy + enemy.enemyBitmap.height / 2
                )
                if (d < pacBitmap.width) {
                    status = END
                    Toast.makeText(context, "Game Over", Toast.LENGTH_LONG).show()
                }
            }
        }

        if (coins.size == points)
            status = WON
    }

    // Calculates distance between two elements
    fun distance(x1:Int,y1:Int,x2:Int,y2:Int) : Float {
        return sqrt(((x1.toFloat() - x2.toFloat()).pow(2f)) + ((y1.toFloat() - y2.toFloat()).pow(2f))).toFloat()
    }

    /*
        Functions to generate random x and y coordinates for the coins
     */
    fun randomXCoord(): Int {
        val start = 1
        val end = (w/100) - 1
        val rand = (Random(System.nanoTime()).nextInt(end - start + 1) + 1.5) * 100

        return rand.toInt()
    }

    fun randomYCoord(): Int {
        val start = 1
        val end = (h/100) - 3

        val rand = Random(System.nanoTime()).nextInt(end - start + 1) + start

        return rand * 100
    }
}