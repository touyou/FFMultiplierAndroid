package com.dev.touyou.ffmultiplier.Fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.ShareCompat;
import android.util.TypedValue;
import android.view.*;
import android.widget.*;
import com.dev.touyou.ffmultiplier.CustomClass.FFNumber;
import com.dev.touyou.ffmultiplier.Model.DatabaseScore;
import com.dev.touyou.ffmultiplier.Model.ScoreModel;
import com.dev.touyou.ffmultiplier.R;
import com.google.android.gms.ads.identifier.AdvertisingIdClient;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import io.realm.Sort;

import java.util.*;


/**
 * Created by touyou on 2016/11/06.
 */
public class GameFragment extends Fragment {

    private TextView myAnswerTextView;
    private TextView leftNumberTextView;
    private TextView rightNumberTextView;
    private TextView resultTextView;
    private TextView countDownTextView;
    private TextView resultPointTextView;
    private TextView highScoreView;
    private Button deleteButton;
    private Button doneButton;
    private Button cancelButton;
    private Button[] numberButton = new Button[16];
    private PopupWindow popupWindow;
    private Context gameActivity;
    private GameFragmentListener listener;
    private int[] buttonIdList = {
            R.id.zeroButton, R.id.oneButton, R.id.twoButton, R.id.threeButton,
            R.id.fourButton, R.id.fiveButton, R.id.sixButton, R.id.sevenButton,
            R.id.eightButton, R.id.nineButton, R.id.anumButton, R.id.bnumButton,
            R.id.cnumButton, R.id.dnumButton, R.id.enumButton, R.id.fnumButton
    };
    private String answerStr;
    private int leftNum, rightNum;
    private int correctCnt;

    private Timer timer;
    private CountDown timerTask = null;
    private Handler handler = new Handler();
    private long count = 60;

    private final int pointsAccepted = 10;
    private final int pointsFailed = -5;
    private final int pointsCombo = 5;
    private final int maxComboBonus = 20;
    private int combo = 0;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        onAttachContext(context);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) return;
        onAttachContext(activity);
    }

    private void onAttachContext(Context context) {
        // 処理
        gameActivity = context;
        try {
            listener = (GameFragmentListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement OnArticleSelectedListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.fragment_game, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        answerStr = "";
        myAnswerTextView = (TextView) view.findViewById(R.id.myAnswerTextView);
        leftNumberTextView = (TextView) view.findViewById(R.id.leftProblemTextView);
        rightNumberTextView = (TextView) view.findViewById(R.id.rightProblemTextView);
        resultTextView = (TextView) view.findViewById(R.id.resultTextView);
        countDownTextView = (TextView) view.findViewById(R.id.timeTextView);

        generateProblem();

        deleteButton = (Button) view.findViewById(R.id.deleteButton);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tappedDeleteBtn(view);
            }
        });
        doneButton = (Button) view.findViewById(R.id.doneButton);
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tappedDoneBtn(view);
            }
        });
        cancelButton = (Button) view.findViewById(R.id.gameQuitButton);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tappedExitBtn(view);
            }
        });
        // Number Button
        for (int i=0; i<16; i++) {
            numberButton[i] = (Button) view.findViewById(buttonIdList[i]);
            numberButton[i].setTag(i);
            numberButton[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    tappedNumberBtn(view);
                }
            });
        }

        popupWindow = new PopupWindow(gameActivity);


        correctCnt = 0;

        timer = new Timer();
        timerTask = new CountDown();
        timer.schedule(timerTask, 0, 1000);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        timer.cancel();
        listener = null;
    }

    private void tappedNumberBtn(View v) {
        int tag = (int) v.getTag();
        String inputStr = FFNumber.valueOf(tag).toString();
        if (answerStr.length() < 2) {
            answerStr += inputStr;
            myAnswerTextView.setText(answerStr);
        }
    }

    private void tappedDeleteBtn(View v) {
        if (answerStr.length() > 0) {
            answerStr = answerStr.substring(0, answerStr.length() - 1);
        }
        if (answerStr.length() == 0) {
            myAnswerTextView.setText("--");
        } else {
            myAnswerTextView.setText(answerStr);
        }
    }

    private void tappedDoneBtn(View v) {
        int ans = leftNum * rightNum;
        String answer = "";
        if (ans >= 16) { answer += FFNumber.valueOf(ans / 16).toString(); }
        answer += FFNumber.valueOf(ans % 16).toString();
        if (answer.equals(answerStr)) {
            correctCnt += pointsAccepted + Math.min(pointsCombo * (combo / 5), maxComboBonus);
            combo++;
            Toast toast = Toast.makeText(gameActivity, "ACCEPTED", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.TOP, 0, 50);
            toast.show();
        } else {
            correctCnt += pointsFailed;
            combo = 0;
            Toast toast = Toast.makeText(gameActivity, "FAILED", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.TOP, 0, 50);
            toast.show();
        }
        resultTextView.setText(String.valueOf(correctCnt));
        generateProblem();
    }

    private void generateProblem() {
        leftNum = (int)(Math.random() * 16);
        rightNum = (int)(Math.random() * 16);
        leftNumberTextView.setText(FFNumber.valueOf(leftNum).toString());
        rightNumberTextView.setText(FFNumber.valueOf(rightNum).toString());
        answerStr = "";
        myAnswerTextView.setText("--");
    }

    private void result() {
        // Realm
        Calendar cal = Calendar.getInstance();
        final ScoreModel scoreModel = new ScoreModel();
        scoreModel.setScore(correctCnt);
        scoreModel.setDate(cal.getTime());
        /** for debug
         RealmConfiguration realmConfig = new RealmConfiguration.Builder().build();
        Realm.deleteRealm(realmConfig);
        Realm realm = Realm.getInstance(realmConfig);*/
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.copyToRealm(scoreModel);
            }
        });
        // popupの設定
        View popupView = View.inflate(gameActivity, R.layout.popup_layout, null);
        resultPointTextView = (TextView) popupView.findViewById(R.id.resultPointTextView);
        resultPointTextView.setText(String.valueOf(correctCnt));
        highScoreView = (TextView) popupView.findViewById(R.id.highScoreView);
        highScoreView.setVisibility(View.INVISIBLE);
        popupView.findViewById(R.id.popupShareButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tappedShareBtn(view);
            }
        });
        popupView.findViewById(R.id.exitButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tappedExitBtn(view);
            }
        });
        popupWindow.setContentView(popupView);
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        popupWindow.setElevation(10);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setFocusable(true);
        float width = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 300, getResources().getDisplayMetrics());
        float height = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 300, getResources().getDisplayMetrics());
        popupWindow.setWindowLayoutMode((int) width, (int) height);
        popupWindow.setWidth((int) width);
        popupWindow.setHeight((int) height);
        popupWindow.showAtLocation(deleteButton, Gravity.CENTER, 0, 0);
        // 最高得点かどうか？
        RealmResults<ScoreModel> results = realm.where(ScoreModel.class).findAllSorted("score", Sort.DESCENDING);
        if (results.size() > 0) {
            if (results.first().getScore() <= correctCnt) {
                updateHighScore(correctCnt);
                highScoreView.setVisibility(View.VISIBLE);
            }
        } else {
            updateHighScore(correctCnt);
        }
        listener.loadAds();
    }

    private void updateHighScore(final int score) {
        final SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(gameActivity);
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference ref = database.getReference();
        if (sp.getString("name", null) == null) {
            LayoutInflater inflater = LayoutInflater.from(gameActivity);
            View dialog = inflater.inflate(R.layout.input_dialog, null);
            final EditText editText = (EditText) dialog.findViewById(R.id.editNameText);
            editText.setText(sp.getString("name", null));

            new AlertDialog.Builder(gameActivity).setTitle("please set your name").setView(dialog).setPositiveButton("ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    final String userName = editText.getText().toString();
                    // スコアを登録
                    Thread adIdThread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            AdvertisingIdClient.Info adInfo;
                            try {
                                adInfo = AdvertisingIdClient.getAdvertisingIdInfo(gameActivity);
                                final String id = adInfo.getId();
                                DatabaseScore databaseScore = new DatabaseScore(userName, score);
                                ref.child("scores").child(id).setValue(databaseScore);
                            } catch (Exception e) {
                            }
                        }
                    });
                    adIdThread.start();
                    sp.edit().putString("name", userName).commit();
                }
            }).show();
        } else {
            final String userName = sp.getString("name", null);
            // スコアを送信
            Thread adIdThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    AdvertisingIdClient.Info adInfo;
                    try {
                        adInfo = AdvertisingIdClient.getAdvertisingIdInfo(gameActivity);
                        final String id = adInfo.getId();
                        DatabaseScore databaseScore = new DatabaseScore(userName, score);
                        ref.child("scores").child(id).setValue(databaseScore);
                    } catch (Exception e) {
                    }
                }
            });
            adIdThread.start();
        }
    }

    private void tappedShareBtn(View v) {
        String articleURL = "https://play.google.com/store/apps/details?id=com.dev.touyou.ffmultiplier";
        String articleTitle = "I got " + String.valueOf(correctCnt)  + " points! Let's play FFMultiplier with me! #FFMultiplier";
        String sharedText = articleTitle + " " + articleURL;

        // builderの生成 ShareCompat.IntentBuilder.from(Context context);
        ShareCompat.IntentBuilder builder = ShareCompat.IntentBuilder.from(this.getActivity());
        // アプリ一覧が表示されるDialogのタイトルの設定
        builder.setChooserTitle("Select App");
        // シェアするタイトル
        builder.setSubject(articleTitle);
        // シェアするテキスト
        builder.setText(sharedText);
        // シェアするタイプ（他にもいっぱいあるよ）
        builder.setType("text/plain");
        // Shareアプリ一覧のDialogの表示
        builder.startChooser();
    }

    private void tappedExitBtn(View v) {
        if (popupWindow.isShowing()) {
            popupWindow.dismiss();
        }
        // ここでFragmentを終了する
        listener.onDestroyActivity();
    }

    class CountDown extends TimerTask {
        @Override
        public void run() {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    count--;
                    countDownTextView.setText(String.valueOf(count));
                    if (count == 0) {
                        timer.cancel();
                        result();
                        return;
                    }
                }
            });
        }
    }

    // Listener
    public interface GameFragmentListener {
        void onDestroyActivity();
        void loadAds();
    }
}
