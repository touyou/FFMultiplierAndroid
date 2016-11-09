package com.dev.touyou.ffmultiplier.Fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import com.dev.touyou.ffmultiplier.R;

/**
 * Created by touyou on 2016/11/06.
 */
public class GameFragment extends Fragment {

    private Button[] numberButton = new Button[16];
    private int[] buttonIdList = {
            R.id.zeroButton, R.id.oneButton, R.id.twoButton, R.id.threeButton,
            R.id.fourButton, R.id.fiveButton, R.id.sixButton, R.id.sevenButton,
            R.id.eightButton, R.id.nineButton, R.id.anumButton, R.id.bnumButton,
            R.id.cnumButton, R.id.dnumButton, R.id.enumButton, R.id.fnumButton
    };


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.fragment_game, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

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
    }
}
