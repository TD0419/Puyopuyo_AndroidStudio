package com.example.pc_user.puyopuyo;

import android.content.Context;

import java.util.Random;

import javax.microedition.khronos.opengles.GL10;

public class Global
{
    // GLコンテキストを保持する変数
    public static GL10 gl;

    public static Context context;

    // ランダムな値を生成
    public static Random random = new Random(System.currentTimeMillis());

    public static float touch_x;
    public static float touch_y;
    public static boolean touch_push = false;
    public static boolean touch_leave = false;
}
