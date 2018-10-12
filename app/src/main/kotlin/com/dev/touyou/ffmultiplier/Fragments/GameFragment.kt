package com.dev.touyou.ffmultiplier.Fragments

import android.app.Activity
import android.app.AlertDialog
import android.app.Fragment
import android.content.Context
import android.content.DialogInterface
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.preference.PreferenceManager
import android.support.v4.app.ShareCompat
import android.util.TypedValue
import android.view.*
import android.widget.*
import com.dev.touyou.ffmultiplier.CustomClass.FFNumber
import com.dev.touyou.ffmultiplier.Model.DatabaseScore
import com.dev.touyou.ffmultiplier.Model.ScoreModel
import com.dev.touyou.ffmultiplier.R
import com.google.android.gms.ads.identifier.AdvertisingIdClient
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import io.realm.Realm
import io.realm.RealmConfiguration
import io.realm.RealmResults
import io.realm.Sort

import java.util.*


/**
 * Created by touyou on 2016/11/06.
 */
class GameFragment : Fragment() {

    private var myAnswerTextView: TextView? = null
    private var leftNumberTextView: TextView? = null
    private var rightNumberTextView: TextView? = null
    private var resultTextView: TextView? = null
    private var countDownTextView: TextView? = null
    private var resultPointTextView: TextView? = null
    private var highScoreView: TextView? = null
    private var deleteButton: Button? = null
    private var doneButton: Button? = null
    private var cancelButton: ImageButton? = null
    private val numberButton = arrayOfNulls<Button>(16)
    private var popupWindow: PopupWindow? = null
    private var gameActivity: Context? = null
    private var listener: GameFragmentListener? = null
    private val buttonIdList = intArrayOf(R.id.zeroButton, R.id.oneButton, R.id.twoButton, R.id.threeButton, R.id.fourButton, R.id.fiveButton, R.id.sixButton, R.id.sevenButton, R.id.eightButton, R.id.nineButton, R.id.anumButton, R.id.bnumButton, R.id.cnumButton, R.id.dnumButton, R.id.enumButton, R.id.fnumButton)
    private var answerStr: String? = null
    private var leftNum: Int = 0
    private var rightNum: Int = 0
    private var correctCnt: Int = 0

    private var timer: Timer? = null
    private var timerTask: CountDown? = null
    private val handler = Handler()
    private var count: Int = 60

    private val pointsAccepted = 10
    private val pointsFailed = -5
    private val pointsCombo = 5
    private val maxComboBonus = 15
    private var combo = 0

    override fun onAttach(context: Context) {
        super.onAttach(context)
        onAttachContext(context)
    }

    override fun onAttach(activity: Activity) {
        super.onAttach(activity)
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) return
        onAttachContext(activity)
    }

    private fun onAttachContext(context: Context) {
        // 処理
        gameActivity = context
        try {
            listener = context as GameFragmentListener
        } catch (e: ClassCastException) {
            throw ClassCastException(context.toString() + " must implement OnArticleSelectedListener")
        }

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup, savedInstanceState: Bundle): View {
        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(R.layout.fragment_game, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle) {
        super.onViewCreated(view, savedInstanceState)

        answerStr = ""
        myAnswerTextView = view.findViewById(R.id.myAnswerTextView) as TextView
        leftNumberTextView = view.findViewById(R.id.leftProblemTextView) as TextView
        rightNumberTextView = view.findViewById(R.id.rightProblemTextView) as TextView
        resultTextView = view.findViewById(R.id.resultTextView) as TextView
        countDownTextView = view.findViewById(R.id.timeTextView) as TextView

        generateProblem()

        deleteButton = view.findViewById(R.id.deleteButton) as Button
        deleteButton!!.setOnClickListener { view -> tappedDeleteBtn(view) }
        doneButton = view.findViewById(R.id.doneButton) as Button
        doneButton!!.setOnClickListener { view -> tappedDoneBtn(view) }
        cancelButton = view.findViewById(R.id.gameQuitButton) as ImageButton
        cancelButton!!.setOnClickListener { view -> tappedExitBtn(view) }
        // Number Button
        for (i in 0..15) {
            numberButton[i] = view.findViewById(buttonIdList[i]) as Button
            numberButton[i]?.setTag(i)
            numberButton[i]?.setOnClickListener(View.OnClickListener { view -> tappedNumberBtn(view) })
        }

        popupWindow = PopupWindow(gameActivity)


        correctCnt = 0

        timer = Timer()
        timerTask = CountDown()
        timer!!.schedule(timerTask, 0, 1000)
    }

    override fun onDestroy() {
        super.onDestroy()
        timer!!.cancel()
        listener = null
    }

    private fun tappedNumberBtn(v: View) {
        val tag = v.tag as Int
        val inputStr = FFNumber.valueOf(tag).toString()
        if (answerStr!!.length < 2) {
            answerStr += inputStr
            myAnswerTextView!!.text = answerStr
        }
    }

    private fun tappedDeleteBtn(v: View) {
        if (answerStr!!.length > 0) {
            answerStr = answerStr!!.substring(0, answerStr!!.length - 1)
        }
        if (answerStr!!.length == 0) {
            myAnswerTextView!!.text = "--"
        } else {
            myAnswerTextView!!.text = answerStr
        }
    }

    private fun tappedDoneBtn(v: View) {
        val ans = leftNum * rightNum
        var answer = ""
        if (ans >= 16) {
            answer += FFNumber.valueOf(ans / 16).toString()
        }
        answer += FFNumber.valueOf(ans % 16).toString()
        if (answer == answerStr) {
            correctCnt += pointsAccepted + Math.min(pointsCombo * (combo / 5), maxComboBonus)
            combo++
            val toast = Toast.makeText(gameActivity, "ACCEPTED", Toast.LENGTH_SHORT)
            toast.setGravity(Gravity.TOP, 0, 50)
            toast.show()
        } else {
            correctCnt += pointsFailed
            combo = 0
            val toast = Toast.makeText(gameActivity, "FAILED", Toast.LENGTH_SHORT)
            toast.setGravity(Gravity.TOP, 0, 50)
            toast.show()
        }
        resultTextView!!.text = correctCnt.toString()
        generateProblem()
    }

    private fun generateProblem() {
        leftNum = (Math.random() * 16).toInt()
        rightNum = (Math.random() * 16).toInt()
        leftNumberTextView!!.text = FFNumber.valueOf(leftNum).toString()
        rightNumberTextView!!.text = FFNumber.valueOf(rightNum).toString()
        answerStr = ""
        myAnswerTextView!!.text = "--"
    }

    private fun result() {
        // Realm
        val cal = Calendar.getInstance()
        val scoreModel = ScoreModel()
        scoreModel.score = correctCnt
        scoreModel.date = cal.time
        /** for debug
         * RealmConfiguration realmConfig = new RealmConfiguration.Builder().build();
         * Realm.deleteRealm(realmConfig);
         * Realm realm = Realm.getInstance(realmConfig); */
        val realm = Realm.getDefaultInstance()
        realm.executeTransactionAsync(object : Realm.Transaction {
            init {
            }

            override fun execute(realm: Realm) {
                realm.copyToRealm(scoreModel)
            }
        })
        // popupの設定
        val popupView = View.inflate(gameActivity, R.layout.popup_layout, null)
        resultPointTextView = popupView.findViewById(R.id.resultPointTextView) as TextView
        resultPointTextView!!.text = correctCnt.toString()
        highScoreView = popupView.findViewById(R.id.highScoreView) as TextView
        highScoreView!!.visibility = View.INVISIBLE
        popupView.findViewById<android.widget.Button>(R.id.popupShareButton).setOnClickListener { view -> tappedShareBtn(view) }
        popupView.findViewById<android.widget.Button>(R.id.exitButton).setOnClickListener { view -> tappedExitBtn(view) }
        popupWindow!!.contentView = popupView
        popupWindow!!.setBackgroundDrawable(ColorDrawable(Color.WHITE))
        popupWindow!!.elevation = 10f
        popupWindow!!.isOutsideTouchable = true
        popupWindow!!.isFocusable = true
        val width = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 300f, resources.displayMetrics)
        val height = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 300f, resources.displayMetrics)
        popupWindow!!.setWindowLayoutMode(width.toInt(), height.toInt())
        popupWindow!!.width = width.toInt()
        popupWindow!!.height = height.toInt()
        popupWindow!!.showAtLocation(deleteButton, Gravity.CENTER, 0, 0)
        // 最高得点かどうか？
        val results = realm.where(ScoreModel::class.java).sort("score", Sort.DESCENDING).findAll()
        if (results.size > 0) {
            if (results.first()!!.score <= correctCnt) {
                updateHighScore(correctCnt)
                highScoreView!!.visibility = View.VISIBLE
            }
        } else {
            updateHighScore(correctCnt)
        }
        listener!!.loadAds()
    }

    private fun updateHighScore(score: Int) {
        val sp = PreferenceManager.getDefaultSharedPreferences(gameActivity)
        val database = FirebaseDatabase.getInstance()
        val ref = database.getReference()
        if (sp.getString("name", null) == null) {
            val inflater = LayoutInflater.from(gameActivity)
            val dialog = inflater.inflate(R.layout.input_dialog, null)
            val editText = dialog.findViewById(R.id.editNameText) as EditText
            editText.setText(sp.getString("name", null))

            AlertDialog.Builder(gameActivity).setTitle("please set your name").setView(dialog).setPositiveButton("ok") { dialogInterface, i ->
                val userName = editText.text.toString()
                // スコアを登録
                val adIdThread = Thread(Runnable {
                    val adInfo: AdvertisingIdClient.Info
                    try {
                        adInfo = AdvertisingIdClient.getAdvertisingIdInfo(gameActivity)
                        val id = adInfo.getId()
                        val databaseScore = DatabaseScore(userName, score)
                        ref.child("scores").child(id).setValue(databaseScore)
                    } catch (e: Exception) {
                    }
                })
                adIdThread.start()
                sp.edit().putString("name", userName).commit()
            }.show()
        } else {
            val userName = sp.getString("name", null)
            // スコアを送信
            val adIdThread = Thread(Runnable {
                val adInfo: AdvertisingIdClient.Info
                try {
                    adInfo = AdvertisingIdClient.getAdvertisingIdInfo(gameActivity)
                    val id = adInfo.getId()
                    val databaseScore = DatabaseScore(userName, score)
                    ref.child("scores").child(id).setValue(databaseScore)
                } catch (e: Exception) {
                }
            })
            adIdThread.start()
        }
    }

    private fun tappedShareBtn(v: View) {
        val articleURL = "https://play.google.com/store/apps/details?id=com.dev.touyou.ffmultiplier"
        val articleTitle = "I got " + correctCnt.toString() + " points! Let's play FFMultiplier with me! #FFMultiplier"
        val sharedText = articleTitle + " " + articleURL

        // builderの生成 ShareCompat.IntentBuilder.from(Context context);
        val builder = ShareCompat.IntentBuilder.from(this.activity)
        // アプリ一覧が表示されるDialogのタイトルの設定
        builder.setChooserTitle("Select App")
        // シェアするタイトル
        builder.setSubject(articleTitle)
        // シェアするテキスト
        builder.setText(sharedText)
        // シェアするタイプ（他にもいっぱいあるよ）
        builder.setType("text/plain")
        // Shareアプリ一覧のDialogの表示
        builder.startChooser()
    }

    private fun tappedExitBtn(v: View) {
        if (popupWindow!!.isShowing) {
            popupWindow!!.dismiss()
        }
        // ここでFragmentを終了する
        listener!!.onDestroyActivity()
    }

    internal inner class CountDown : TimerTask() {
        override fun run() {
            handler.post(Runnable {
                count--
                countDownTextView!!.text = count.toString()
                if (count == 0) {
                    timer!!.cancel()
                    result()
                    return@Runnable
                }
            })
        }
    }

    // Listener
    interface GameFragmentListener {
        fun onDestroyActivity()
        fun loadAds()
    }
}
