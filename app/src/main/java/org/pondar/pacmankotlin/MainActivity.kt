package org.pondar.pacmankotlin

import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.View.OnClickListener
import android.widget.Toast
import org.pondar.pacmankotlin.databinding.ActivityMainBinding
import java.util.*

class MainActivity : AppCompatActivity(), OnClickListener {

    //reference to the game class.
    private lateinit var game: Game
    private lateinit var binding : ActivityMainBinding

    // Timer for moving the pacman
    private var pacTimer: Timer = Timer()

    // Timer for time limit (60 seconds)
    private var timer: Timer = Timer()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        val view = binding.root
        setContentView(view)
        view.setOnTouchListener(object : OnSwipeTouchListener(applicationContext) {
            override fun onSwipeTop() {
                game.direction = game.UP
            }

            override fun onSwipeBottom() {
                game.direction = game.DOWN
            }

            override fun onSwipeLeft() {
                game.direction = game.LEFT
            }

            override fun onSwipeRight() {
                game.direction = game.RIGHT
            }
        })

        // screen should always be portrait
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        Log.d("onCreate","Oncreate called")

        game = Game(this,binding.pointsView)

        //intialize the game view clas and game class
        game.setGameView(binding.gameView)
        binding.gameView.setGame(game)
        game.newGame()

        binding.startGame.setOnClickListener(this)
        binding.endGame.setOnClickListener(this)

        pacTimer.schedule(object: TimerTask() {
            override fun run(){
                pacTimerMethod()
            }
        }, 0, 130)

        timer.schedule(object: TimerTask() {
            override fun run() {
                timerMethod()
            }
        }, 0, 1000)
    }

    private fun pacTimerMethod() {
        this.runOnUiThread(pacTimerTick)
    }

    private fun timerMethod() {
        this.runOnUiThread(timerTick)
    }

    private val pacTimerTick = Runnable {
        if (game.status == game.IN_PROGRESS) {
            game.movePacman(game.direction, 50)
            game.moveEnemies(10)
        }
    }

    private val timerTick = Runnable {
        if (game.status == game.IN_PROGRESS) {
            if (game.counter > 0 && game.coins.size > game.points) {
                game.counter--
            } else {
                Toast.makeText(this, "Game Over", Toast.LENGTH_LONG).show()
                game.status = game.END
            }
        } else if (game.status == game.WON) {
            if (game.level < game.maxLevel) {
                val l = ++game.level
                Toast.makeText(this, "Level ${game.level}", Toast.LENGTH_LONG).show()
                game.status = game.IN_PROGRESS
                game.newGame()
                game.counter = 45
                game.level = l
            } else {
                Toast.makeText(this, "Victory!", Toast.LENGTH_LONG).show()
            }
        }
        binding.timerView.text = "Time: ${game.counter}"
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId
        if (id == R.id.action_newGame) {
            Toast.makeText(this, "New Game", Toast.LENGTH_LONG).show()
            game.newGame()
            game.counter = 45
            game.level = 1
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    // Pause and start buttons
    override fun onClick(v: View?) {
        if (v?.id == R.id.startGame) {
            if (game.status == game.STOPPED) {
                game.status = game.IN_PROGRESS
            }
        } else if (v?.id == R.id.endGame) {
            game.status = game.STOPPED
        }
    }

    // Stop the timers when app is killed
    override fun onStop() {
        super.onStop()
        pacTimer.cancel()
        timer.cancel()
    }

}
