package com.example.lcl.bitchcat;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * Created by LCL on 2015/10/22.
 */

public class Playground extends SurfaceView {

    private static final int ROW = 10;
    private static final int COL = 10;

    private Dot matrix[][];

    public Playground(Context context) {
        super(context);
        getHolder().addCallback(callback);
        matrix = new Dot[ROW][COL];

    }

    private void redraw() {
        Canvas c = getHolder().lockCanvas();
        c.drawColor(Color.CYAN);
        getHolder().unlockCanvasAndPost(c);
    }

    SurfaceHolder.Callback callback = new SurfaceHolder.Callback() {

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            redraw();
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {

        }
    };

}
