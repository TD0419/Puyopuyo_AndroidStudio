package com.example.pc_user.puyopuyo;

import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;

public class MainActivity extends Activity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        // フルスクリーン表示
        getWindow().addFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // タイトルバー非表示
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        MyRenderer renderer = new MyRenderer(this); // MyRendererの生成

        // GLSurfaceViewの生成
        GLSurfaceView glSurfaceView = new MyGLSurfaceView(this);
        // GLSurfaceViewにMyRendererを適用
        glSurfaceView.setRenderer(renderer);

        setContentView(glSurfaceView); // ビューをGLSurfaceViewに指定
    }

    @Override
    public void onPause()
    {
        super.onPause();

        // テクスチャをすべて削除する
        TextureManager.deleteAll(Global.gl);
    }
}
