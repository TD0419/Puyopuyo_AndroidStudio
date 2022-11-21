package com.example.pc_user.puyopuyo;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;
import android.view.SurfaceHolder;

public class MyGLSurfaceView extends GLSurfaceView
{
    // 画面サイズ
    private float mWidth;
    private float mHeight;

    public MyGLSurfaceView(Context context)
    {
        super(context);

        setFocusable(true);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format,
                               int w, int h)
    {
        super.surfaceChanged(holder, format, w, h);
        this.mWidth = w;
        this.mHeight = h;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        switch (event.getAction())
        {
            //画面のタッチが終わった時の処理
            case MotionEvent.ACTION_UP:
            {
                Global.touch_leave = false;
                break;
            }
            //画面にタッチした時の処理
            case MotionEvent.ACTION_DOWN:
            {
                Global.touch_push = true;
                Global.touch_leave = true;
                break;
            }
        }

        Global.touch_x = (event.getX() / mWidth)  *  3.f - 1.5f;
        Global.touch_y = (event.getY() / mHeight) * -2.f + 1.f;

        return true;
    }
}
