package com.epicodus.snapmatch.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.epicodus.snapmatch.R;
import com.epicodus.snapmatch.models.Tile;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by momma on 11/9/17.
 */

public class GameBoardAdapter extends RecyclerView.Adapter<GameBoardAdapter.GameViewHolder>{
    public static final String TAG = GameBoardAdapter.class.getSimpleName();
    private static final int MAX_WIDTH = 200;
    private static final int MAX_HEIGHT = 200;
    private ArrayList<Tile> mTiles = new ArrayList<>();
    private Context mContext;


    public GameBoardAdapter(Context context, ArrayList<Tile> tiles) {
        mContext = context;
        mTiles = tiles;
    }

    @Override
    public GameBoardAdapter.GameViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View  view =  LayoutInflater.from(parent.getContext()).inflate(R.layout.game_tile_item, parent,false);
        GameViewHolder viewHolder = new GameViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(GameBoardAdapter.GameViewHolder holder, int position) {
        Log.v(TAG, "onBindGotCalled");
        holder.bindGame(mTiles.get(position));
    }

    @Override
    public int getItemCount() {
        return mTiles.size();
    }

    public class GameViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @Bind(R.id.tileImageView) ImageView mTileImageView;
        private Context mContext;
        private int mOrientation;


        public GameViewHolder(View itemView){
            super(itemView);
            ButterKnife.bind(this, itemView);
            mContext = itemView.getContext();
            itemView.setOnClickListener(this);

            // Take care of Landscape Mode ??
//            mOrientation = itemView.getResources().getConfiguration().orientation;
//            if (mOrientation == Configuration.ORIENTATION_LANDSCAPE) {
//                Log.v(TAG, "LANDSCAPE ORientation....add detail fragment");
//            }
        }

        @Override
        public void onClick(View v) {
            //This is where the game clicking action will happen!
            int itemPosition = getLayoutPosition();
            //do something special in Landscape??
            //if (mOrientation == Configuration.ORIENTATION_LANDSCAPE)

            Log.v(TAG,"IMAGE CLICKED : "+itemPosition);
        }

        public void bindGame(Tile tile) {
            Picasso.with(mContext)
                    .load(tile.getImage())
                    .resize(MAX_WIDTH, MAX_HEIGHT)
                    .centerCrop()
                    .into(mTileImageView);
        }
    }

}
