package com.radiance.kamisado;

import android.content.Context;
import android.widget.Button;

/**
 * Created by neerajen on 16/05/15.
 */
public class UndoButton extends Button {
    private int player;

    public UndoButton(Context context, int player) {
        super(context);
        this.player = player;
    }

    public int getPlayer() {
        return player;
    }
}
