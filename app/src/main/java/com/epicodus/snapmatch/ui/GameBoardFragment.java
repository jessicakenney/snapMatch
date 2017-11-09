package com.epicodus.snapmatch.ui;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.epicodus.snapmatch.R;
import com.epicodus.snapmatch.adapters.GameBoardAdapter;
import com.epicodus.snapmatch.models.Tile;

import java.util.ArrayList;

import butterknife.Bind;

/**
 * A simple {@link Fragment} subclass.
 */
public class GameBoardFragment extends Fragment {
    public static final String TAG = GameBoardFragment.class.getSimpleName();
    @Bind(R.id.recyclerView)
    RecyclerView mRecyclerView;
    private GameBoardAdapter mAdapter;
    private String mSelectedGame;
    public ArrayList<Tile> mTiles =  new ArrayList<>();



    public GameBoardFragment() {
        // Required empty public constructor
    }

    public static GameBoardFragment newInstance() {
        GameBoardFragment fragment = new GameBoardFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        Log.v(TAG,">>>>>>newInstance gameboardFragment");
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.v(TAG,">>>>>>onCreateView gameboardFragment");
        return inflater.inflate(R.layout.fragment_gameboard, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        // this method is like inflate views in Main
        // access the fragment layout, inflate the views
        // will want to loadimages here (will add extra level hierarchy to say
        //which images for now will hardcode.
        //loadFirebaseImages;

        //current user, game selected
        //hard code for now
        mSelectedGame = "bestgameever";
        Log.v(TAG,">>>>>>onViewCreated .... gameboardFragment");
        getTiles(mSelectedGame);

    }

    public void getTiles(String game){

        //this is where you will call to firebase for now hardcode
        //mTiles = ;
        Log.v(TAG,">>>>>>Needsome tiles to get .... gameboardFragment");

        getActivity().runOnUiThread(new Runnable() {
            // Line above states 'getActivity()' instead of previous 'RestaurantListActivity.this'
            // because fragments do not have own context, and must inherit from corresponding activity.

            @Override
            public void run() {
                mAdapter = new GameBoardAdapter(getActivity(), mTiles);
                // Line above states `getActivity()` instead of previous
                // 'getApplicationContext()' because fragments do not have own context,

                mRecyclerView.setAdapter(mAdapter);
                RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getActivity(), 3);
                // Line above states 'new LinearLayoutManager(getActivity());' instead of previous
                // 'new LinearLayoutManager(RestaurantListActivity.this);' when method resided
                // in RestaurantListActivity because Fragments do not have context
                // and must instead inherit from corresponding activity.

                mRecyclerView.setLayoutManager(layoutManager);
                mRecyclerView.setHasFixedSize(true);
            }
        });
    }

}
