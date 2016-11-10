package com.dev.touyou.ffmultiplier.Fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import com.dev.touyou.ffmultiplier.CustomClass.FFNumber;
import com.dev.touyou.ffmultiplier.R;

/**
 * Created by touyou on 2016/11/06.
 */
public class GameFragment extends Fragment {

    private TextView myAnswerTextView;
    private Button deleteButton;
    private Button[] numberButton = new Button[16];
    private int[] buttonIdList = {
            R.id.zeroButton, R.id.oneButton, R.id.twoButton, R.id.threeButton,
            R.id.fourButton, R.id.fiveButton, R.id.sixButton, R.id.sevenButton,
            R.id.eightButton, R.id.nineButton, R.id.anumButton, R.id.bnumButton,
            R.id.cnumButton, R.id.dnumButton, R.id.enumButton, R.id.fnumButton
    };
    private String answerStr;


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

        deleteButton = (Button) view.findViewById(R.id.deleteButton);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tappedDeleteBtn(view);
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
        
    }
}
