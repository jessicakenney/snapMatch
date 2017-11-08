package com.epicodus.snapmatch.models;

import com.epicodus.snapmatch.R;

import java.util.IllegalFormatCodePointException;

/**
 * Created by momma on 11/7/17.
 */

public enum NavBarItem {
    MY_GAMES(R.id.my_games),
    CREATE_GAME(R.id.create_game);

    private int itemId;
    NavBarItem(int itemId) {
        this.itemId = itemId;
    }

    public int getItemId (){
        return itemId;
    }

    public static NavBarItem fromViewId(int viewId) {
        for(NavBarItem navBarItem : NavBarItem.values()){
            if (navBarItem.getItemId() == viewId) {
                return navBarItem;
            }
        }
        throw new IllegalStateException("Cannot find viewType");
    }
}
