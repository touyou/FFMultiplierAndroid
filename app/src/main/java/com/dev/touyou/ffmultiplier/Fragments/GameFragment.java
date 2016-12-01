package com.dev.touyou.ffmultiplier.Fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import com.dev.touyou.ffmultiplier.Activity.GameActivity;
import com.dev.touyou.ffmultiplier.CustomClass.FFNumber;
import com.dev.touyou.ffmultiplier.R;

import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.LogRecord;


/**
 * Created by touyou on 2016/11/06.
 */
public class GameFragment extends Fragment {

    private TextView myAnswerTextView;
    private TextView leftNumberTextView;
    private TextView rightNumberTextView;
    private TextView resultTextView;
    private TextView countDownTextView;
    private Button deleteButton;
    private Button doneButton;
    private Button[] numberButton = new Button[16];
    private PopupWindow popupWindow;
    private Context gameActivity;
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
        View popupView = View.inflate(gameActivity, R.layout.popup_layout, null);
        popupWindow.setContentView(popupView);
        popupWindow.setBackgroundDrawable(getResources().getDrawable(R.drawable.popup_background));
        popupWindow.setOutsideTouchable(true);
        popupWindow.setFocusable(true);

        correctCnt = 0;

        timer = new Timer();
        timerTask = new CountDown();
        timer.schedule(timerTask, 0, 1000);
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
            correctCnt++;
            resultTextView.setText(String.valueOf(correctCnt));
            Toast.makeText(gameActivity, "ACCEPTED", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(gameActivity, "FAILED", Toast.LENGTH_SHORT).show();
        }
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
                        // result
                        return;
                    }
                }
            });
        }
    }
}
