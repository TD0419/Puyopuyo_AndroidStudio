package com.example.pc_user.puyopuyo;

import android.content.Context;
import android.content.res.Resources;
import android.opengl.GLSurfaceView;
import android.util.Log;

import javax.microedition.khronos.opengles.GL10;

import static android.support.constraint.Constraints.TAG;

public class MyRenderer implements GLSurfaceView.Renderer
{
    private int mWidth; // 画面の横幅
    private int mHieght; // 画面の縦幅

    // テクスチャを管理するためのID
    public static int mRedTexture;
    public static int mBlueTexture;
    public static int mGreenTexture;
    public static int mYellowTexture;
    public static int mPurpleTexture;

    public static int mLeftButtonTexture;
    public static int mRightButtonTexture;
    public static int mLeftRotaButtonTexture;
    public static int mRightRotaButtonTexture;
    public static int mBottomButtonTexture;

    private boolean m_object_init_flag; // ゲームオブジェクト生成フラグ

    private ObjMap m_objmap;
    private ObjNextPuyo m_nextpuyo;
    private ObjScoreText m_objscoretext;
    private ObjTouchButton m_touch_button;

    public MyRenderer(Context context)
    {
        Global.context = context;

        m_object_init_flag = true;
    }

    private void renderMain(GL10 gl)
    {
        gl.glEnable(GL10.GL_BLEND);
        gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);

        m_objmap.Update();
        m_objmap.Draw(gl);

        ObjectManager.Update();
        ObjectManager.Draw(gl);

        gl.glDisable(GL10.GL_BLEND);

        Global.touch_push = false;
    }

    @Override
    public void onDrawFrame(GL10 gl)
    {
        // 描画処理
        // OpenGl描画設定
        //gl.glViewport(0, 0, mWidth, mHieght);
        gl.glMatrixMode(GL10.GL_PROJECTION);;

        gl.glLoadIdentity();
        gl.glOrthof(-1.5f, 1.5f, -1.f, 1.f, 0.5f, -0.5f);
        gl.glMatrixMode(GL10.GL_MODELVIEW);
        gl.glLoadIdentity();

        // 画面クリア
        gl.glClearColor(0.5f, 0.5f, 0.5f, 1.f);
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

        renderMain(gl);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height)
    {
        // 初期化処理
        mWidth = width;
        mHieght = height;

        Global.gl = gl;

        // 常に3 : 2で描画
        int w = 0;
        int h = 0;
        while(w < width && h < height)
        {
            w += 3;
            h += 2;
        }
        int w_offset = (width - w) / 2;
        int h_offset = (height - h) / 2;
        gl.glViewport(w_offset, h_offset, w, h);

        this.mRedTexture              = loadTextureGetID(Global.gl, Global.context.getResources(), R.drawable.red);
        this.mBlueTexture             = loadTextureGetID(Global.gl, Global.context.getResources(), R.drawable.blue);
        this.mGreenTexture            = loadTextureGetID(Global.gl, Global.context.getResources(), R.drawable.green);
        this.mYellowTexture           = loadTextureGetID(Global.gl, Global.context.getResources(), R.drawable.yellow);
        this.mPurpleTexture           = loadTextureGetID(Global.gl, Global.context.getResources(), R.drawable.purple);
        this.mLeftButtonTexture       = loadTextureGetID(Global.gl, Global.context.getResources(), R.drawable.left_button);
        this.mRightButtonTexture      = loadTextureGetID(Global.gl, Global.context.getResources(), R.drawable.right_button);
        this.mLeftRotaButtonTexture   = loadTextureGetID(Global.gl, Global.context.getResources(), R.drawable.left_rota_button);
        this.mRightRotaButtonTexture  = loadTextureGetID(Global.gl, Global.context.getResources(), R.drawable.right_rota_button);
        this.mBottomButtonTexture     = loadTextureGetID(Global.gl, Global.context.getResources(), R.drawable.buttom_button);

        // 何度もオブジェクトを生成されるのを防ぐ
        if(m_object_init_flag == true)
        {
            ObjectInit();
            m_object_init_flag = false;
        }
    }

    @Override
    public void onSurfaceCreated(GL10 gl, javax.microedition.khronos.egl.EGLConfig config)
    {
        // 初期化処理
    }

    private void ObjectInit()
    {
        m_touch_button = new ObjTouchButton();
        ObjectManager.Insert(m_touch_button);
        m_objscoretext = new ObjScoreText(0.5f, 0.8f, 999);
        ObjectManager.Insert(m_objscoretext);
        m_nextpuyo = new ObjNextPuyo(0.5f, 0.5f);
        ObjectManager.Insert(m_nextpuyo);
        m_objmap = new ObjMap(0.f, 0.f, m_objscoretext, m_nextpuyo, m_touch_button);
    }

    // テクスチャを読み込んで、IDを返す関数(エラーメッセージ付き)
    private int loadTextureGetID(GL10 gl, Resources resources, int resId)
    {
        int out_resId = GraphicUtil.loadTexture(
                gl, resources, resId);

        if(out_resId == 0)
        {
            Log.e(getClass().toString(),
                    "texture load ERROR!");
            return 0;
        }

        return out_resId;
    }

    // ぷよのテクスチャIDを返す
    public static int GetPuyoTextureId(int puyo_kind)
    {
        switch (puyo_kind)
        {
            case ObjPuyo.RedPuyo:
                return mRedTexture;
            case ObjPuyo.BluePuyo:
                return mBlueTexture;
            case ObjPuyo.GreenPuyo:
                return mGreenTexture;
            case ObjPuyo.YellowPuyo:
                return mYellowTexture;
            case ObjPuyo.PurplePuyo:
                return mPurpleTexture;
        }
        Log.w(TAG, "ぷよのテクスチャ情報が見つかりません");
        return -1;
    }
}
