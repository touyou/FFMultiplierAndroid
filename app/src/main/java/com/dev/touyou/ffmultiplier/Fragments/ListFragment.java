package com.dev.touyou.ffmultiplier.Fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ListView;
import com.dev.touyou.ffmultiplier.Adapter.LocalRankAdapter;
import com.dev.touyou.ffmultiplier.Model.LocalScoreItem;
import com.dev.touyou.ffmultiplier.Model.ScoreModel;
import com.dev.touyou.ffmultiplier.R;
import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ListFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ListFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM = "local_mode";

    // TODO: Rename and change types of parameters
    private String mParam;
    private ListView listView;

    private OnFragmentInteractionListener mListener;

    public ListFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static ListFragment newInstance(String param1) {
        ListFragment fragment = new ListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam = getArguments().getString(ARG_PARAM);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_list, container, false);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        listView = (ListView) view.findViewById(R.id.listView);

        switch (mParam) {
            case "local_mode":
                setLocalMode();
                break;
            case "online_top":
                setOnline("top");
                break;
            case "online_near":
                setOnline("near");
                break;
            default:
                break;
        }
    }

    private void setLocalMode() {
        ArrayList<LocalScoreItem> arrayList = new ArrayList<>();
        LocalRankAdapter localRankAdapter = new LocalRankAdapter(getActivity());
        localRankAdapter.setScoreList(arrayList);
        listView.setAdapter(localRankAdapter);

        Realm realm = Realm.getDefaultInstance();
        RealmResults<ScoreModel> results = realm.where(ScoreModel.class).findAllSorted("score", Sort.DESCENDING);
        if (results.size() > 0) {
            ScoreModel nowValue = results.first();
            arrayList.add(convertLocalScore(1, nowValue));
            int r = 1;
            for (int i=1; i < Math.min(50, results.size()); i++) {
                if (results.get(i).getScore() != nowValue.getScore()) {
                    r = i + 1;
                    nowValue = results.get(i);
                }
                arrayList.add(convertLocalScore(r, results.get(i)));
            }
        }
        localRankAdapter.notifyDataSetChanged();
    }

    private LocalScoreItem convertLocalScore(int i, ScoreModel s) {
        LocalScoreItem localScoreItem = new LocalScoreItem();
        localScoreItem.setRank(i);
        localScoreItem.setDate(s.getDate());
        localScoreItem.setScore(s.getScore());
        return localScoreItem;
    }

    private void setOnline(String mode) {}

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
    }
}
