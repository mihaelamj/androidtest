package com.example.myapplication

import android.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.TextView
import android.widget.Toast

class MainActivity : AppCompatActivity() {
  //declare variables
  internal var score = 0
  internal var gameStarted = false

  internal lateinit var countDownTimer: CountDownTimer
  internal val initialCountDown: Long = 60000 // ms - 60 sec
  internal val countDownInterval: Long = 1000 // ms - 1 sec
  internal var timeLeftOnTimer: Long = 60000 // ms - 60 sec

  // declare properties to manipulate interesting parts of the UI
  internal lateinit var tapMeButton: Button
  internal lateinit var gameScoreTextView: TextView
  internal lateinit var timeLeftTextView: TextView


  companion object {
    private  val TAG = MainActivity::class.java.simpleName // obtaining a class name
    private const val SCORE_KEY = "SCORE_KEY"
    private const val TIME_LEFT_KEY = "TIME_LEFT_KEY"
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    // log to the console
    Log.d(TAG, "onCreate called. Score is: $score") // `$` is for inserting values into a string

    //connect our declared properties to the UI elements
    tapMeButton = findViewById(R.id.tapMeButton)
    gameScoreTextView = findViewById(R.id.gameScoreTextView)
    timeLeftTextView = findViewById(R.id.timeLeftTextView)

    //add listener to the `tapMeButton`
    tapMeButton.setOnClickListener { view ->
      // add animation that we just defined
      val bounceAnimation = AnimationUtils.loadAnimation(this, R.anim.bounce)
      view.startAnimation(bounceAnimation)
      incrementScore()
    }

    updateTimeLeftTextView(timeLeftOnTimer / 1000)
    updateScoreTextView()
//    resetGame()

    if (savedInstanceState != null) {
      score = savedInstanceState.getInt(SCORE_KEY)
      timeLeftOnTimer = savedInstanceState.getLong(TIME_LEFT_KEY)
      restoreGame()
    } else {
      resetGame()
    }
  }

  // create the menu from the xml file with the `menuInflater` property of the `Activity` (View)
  override fun onCreateOptionsMenu(menu: Menu?): Boolean {
    super.onCreateOptionsMenu(menu)
    menuInflater.inflate(R.menu.menu, menu)
    return true
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    if (item.itemId == R.id.actionAbout) {
      showInfo()
    }
    return true
  }

  private fun showInfo() {
    var dialogTitle = getString(R.string.aboutTitle, BuildConfig.VERSION_NAME)
    var dialogMessage = getString(R.string.aboutMessage)
    var builder = AlertDialog.Builder(this)
    builder.setTitle(dialogTitle)
    builder.setMessage(dialogMessage)
    builder.create().show()
  }

  // When a device rotates on Android, the current `Activity` (a View) is destroyed, and a new one is created for the rotated state.
  // To remedy that, we need to save out data, so that the user can continue playing the game
  // to do that we need to override `onSaveInstanceState`
  override fun onSaveInstanceState(outState: Bundle) {
    super.onSaveInstanceState(outState)

    outState.putInt(SCORE_KEY, score)
    outState.putLong(TIME_LEFT_KEY, timeLeftOnTimer)
    countDownTimer.cancel()

    Log.d(TAG, "onSaveInstanceState: Saving Score: $score & Time Left: $timeLeftOnTimer")
  }

  // overriding current activity destructor
  override fun onDestroy() {
    super.onDestroy()
    Log.d(TAG, "onDestroy called.")
  }

  private fun updateScoreTextView() {
    val newScore = getString(R.string.yourScore, score)
    gameScoreTextView.text = newScore
  }

  private fun updateTimeLeftTextView(time: Long) {
    timeLeftTextView.text = getString(R.string.timeLeft, time)
  }

  private fun incrementScore() {
    if (!gameStarted) { startGame() }

    // add animation that we just defined
    val blinkAnimation = AnimationUtils.loadAnimation(this, R.anim.blink)
    gameScoreTextView.startAnimation(blinkAnimation)
    score += 1
    updateScoreTextView()
  }

  private fun startGame() {
    countDownTimer.start()
    gameStarted = true
  }

  private fun resetGame() {
    score = 0
    updateScoreTextView()

    val initialTimeLeft = initialCountDown / 1000
    updateTimeLeftTextView(initialTimeLeft)

    // create the `CountDownTimer` class
    countDownTimer = object : CountDownTimer(initialCountDown, countDownInterval) {
      // set the `OnTick` code
      override fun onTick(millisUnitUntilFinished: Long) {
        // save the time left
        timeLeftOnTimer = millisUnitUntilFinished
        val timeLeft = millisUnitUntilFinished / 1000
        updateTimeLeftTextView(timeLeft)
      }
      // need to define the `OnFinish` function
      override fun onFinish() {
        endGame()
      }
    }
    gameStarted = false
  }

  // Restore the game to the saved state
  private fun restoreGame() {
    var restoredTime = timeLeftOnTimer / 1000
    updateTimeLeftTextView(restoredTime)
    updateScoreTextView()

    countDownTimer = object : CountDownTimer(timeLeftOnTimer, countDownInterval) {
      override fun onTick(milliesUntilFinished: Long) {
        timeLeftOnTimer = milliesUntilFinished
        var timeLeft = milliesUntilFinished / 1000
        updateTimeLeftTextView(timeLeft)
      }

      override fun onFinish() {
        endGame()
      }
    }

    countDownTimer.start()
    gameStarted = true
  }

  private fun endGame() {
    val messageText = getString(R.string.gameOverMessage, score)
    Toast.makeText(this, messageText, Toast.LENGTH_LONG).show()
    resetGame()
  }

}
