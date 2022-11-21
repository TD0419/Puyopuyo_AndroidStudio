package com.example.pc_user.puyopuyo;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.opengl.GLUtils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

public class GraphicUtil
{
    // システム上のメモリ領域を確保するためのメソッド
    public static final FloatBuffer makeFloatBuffer(float[] arr)
    {
        ByteBuffer bb = ByteBuffer.allocateDirect(arr.length * 4);
        bb.order(ByteOrder.nativeOrder());
        FloatBuffer fb = bb.asFloatBuffer();
        fb.put(arr);
        fb.position(0);
        return fb;
    }

    private static final BitmapFactory.Options options =
            new BitmapFactory.Options();
    static
    {
        // リソースの自動リサイズをしない
        options.inScaled = false;
        // 32bit画像として読み込む
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
    }

    public static final int loadTexture(
            GL10 gl, Resources resources, int resId)
    {
        int[] textures = new int[1];

        // Bitmapの作成
        Bitmap bmp = BitmapFactory.decodeResource(resources, resId, options);
        if(bmp == null)
            return 0;

        // OpenGL用のテクスチャを生成
        gl.glGenTextures(1, textures, 0);
        gl.glBindTexture(GL10.GL_TEXTURE_2D, textures[0]);
        GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bmp, 0);
        gl.glTexParameterf(GL10.GL_TEXTURE_2D,
                GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);
        gl.glTexParameterf(GL10.GL_TEXTURE_2D,
                GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
        gl.glBindTexture(GL10.GL_TEXTURE_2D, 0);

        // OpenGLへの転送が完了したので、VMメモリ上に作成したBitmapを破棄
        bmp.recycle();

        // TextureManagerに登録する
        TextureManager.addTexture(resId, textures[0]);

        return textures[0];
    }

    public static final int loadTexture(
            GL10 gl, Resources resources, Bitmap bmp, int resId)
    {
        int[] textures = new int[1];

        if(bmp == null)
            return 0;

        // OpenGL用のテクスチャを生成
        gl.glGenTextures(1, textures, 0);
        gl.glBindTexture(GL10.GL_TEXTURE_2D, textures[0]);
        GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bmp, 0);
        gl.glTexParameterf(GL10.GL_TEXTURE_2D,
                GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);
        gl.glTexParameterf(GL10.GL_TEXTURE_2D,
                GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
        gl.glBindTexture(GL10.GL_TEXTURE_2D, 0);

        // OpenGLへの転送が完了したので、VMメモリ上に作成したBitmapを破棄
        bmp.recycle();

        // TextureManagerに登録する
        TextureManager.addTexture(resId, textures[0]);

        return textures[0];
    }

    public static final void drawTexture(
            GL10 gl, float x, float y, float width, float height, int texture
    )
    {
        drawTexture(gl, x, y, width, height, texture, 1.f, 1.f, 1.f, 1.f);
    }

    public static final void drawTexture(GL10 gl,
                                         float x, float y, float width, float height, int texture,
                                         float r, float g, float b, float a)
    {
        float[] vertices =
                {
                        -0.5f * width + x, -0.5f * height + y,
                        0.5f * width + x, -0.5f * height + y,
                        -0.5f * width + x, 0.5f * height + y,
                        0.5f * width + x,0.5f * height + y,
                };

        float[] colors =
                {
                        r, g, b, a,
                        r, g, b, a,
                        r, g, b, a,
                        r, g, b, a,
                };

        float[] coords =
                {
                        0.f, 1.f,
                        1.f, 1.f,
                        0.f, 0.f,
                        1.f, 0.f,
                };

        // 頂点情報をメモリ上に確保
        FloatBuffer polygonVertices = makeFloatBuffer(vertices);
        FloatBuffer polygonColors = makeFloatBuffer(colors);
        FloatBuffer texCoords = makeFloatBuffer(coords);

        // ポリゴンを描画
        gl.glEnable(GL10.GL_TEXTURE_2D);
        // テクスチャオブジェクト指定
        gl.glBindTexture(GL10.GL_TEXTURE_2D, texture);
        gl.glVertexPointer(2, GL10.GL_FLOAT, 0, polygonVertices);
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glColorPointer(4, GL10.GL_FLOAT, 0, polygonColors);
        gl.glEnableClientState(GL10.GL_COLOR_ARRAY);
        gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, texCoords);
        gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);

        gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);

        gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
        gl.glDisable(GL10.GL_TEXTURE_2D);
    }

    public static final void drawCircle(GL10 gl,
                                        float x, float y,
                                        int divides, float radius,
                                        float r, float g, float b, float a)
    {
        float[] vertices = new float[divides * 3 * 2];

        int vertexId = 0; // 頂点配列の要素の番号を記憶しておくための変数
        for(int i = 0; i < divides; i++)
        {
            // 円周上のi番目の頂点角度(ラジアン)を計算する
            float theta1 = 2.f / (float) divides * (float)i * (float)Math.PI;
            // 円周上の(i+1)番目の頂点角度(ラジアン)を計算する
            float theta2 = 2.f / (float) divides * (float)(i + 1) * (float)Math.PI;
            // i番目の三角形の0番目の頂点情報をセット
            vertices[vertexId++] = x;
            vertices[vertexId++] = y;
            // i番目の三角形の1番目の頂点情報をセット
            vertices[vertexId++] = (float)Math.cos((double)theta1) * radius + x;
            vertices[vertexId++] = (float)Math.sin((double)theta1) * radius + y;
            // i番目の三角形の2番目の頂点情報をセット
            vertices[vertexId++] = (float)Math.cos((double)theta2) * radius + x;
            vertices[vertexId++] = (float)Math.sin((double)theta2) * radius + y;
        }
        FloatBuffer polygonVertices = makeFloatBuffer(vertices);

        gl.glColor4f(r, g, b, a);
        gl.glDisableClientState(GL10.GL_COLOR_ARRAY);

        gl.glVertexPointer(2, GL10.GL_FLOAT, 0, polygonVertices);
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);

        gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);
    }

    public static final void drawaRectangle(GL10 gl,
                                            float x, float y, float width, float height,
                                            float r, float g, float b, float a)
    {
        float[] vertices =
                {
                        -0.5f * width + x, -0.5f * height + y,
                        0.5f * width + x, -0.5f * height + y,
                        -0.5f * width + x, 0.5f * height + y,
                        0.5f * width + x,0.5f * height + y,
                };

        float[] colors =
                {
                        r, g, b, a,
                        r, g, b, a,
                        r, g, b, a,
                        r, g, b, a,
                };

        // 頂点情報をメモリ上に確保
        FloatBuffer polygonVertices = GraphicUtil.makeFloatBuffer(vertices);
        FloatBuffer polygonColors = GraphicUtil.makeFloatBuffer(colors);

        // ポリゴンを描画
        gl.glVertexPointer(2, GL10.GL_FLOAT, 0, polygonVertices);
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glColorPointer(4, GL10.GL_FLOAT, 0, polygonColors);
        gl.glEnableClientState(GL10.GL_COLOR_ARRAY);
        gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);
    }

    public static final void drawSquare(GL10 gl, float x, float y,
                                        float r, float g, float b, float a)
    {
       drawaRectangle(gl, x, y, 1.f, 1.f,r,g,b,a);
    }

    public static final void drawSquare(GL10 gl, float r, float g, float b, float a)
    {
        drawSquare(gl, 0.f, 0.f, r,g,b,a);
    }

    public static final void drawSquare(GL10 gl)
    {
        float[] vertices =
                {
                        -0.5f, -0.5f,
                        0.5f, -0.5f,
                        -0.5f, 0.5f,
                        0.5f,0.5f,
                };

        float[] colors =
                {
                        1.f, 1.f, 0.f, 1.f,
                        0.f, 1.f, 1.f, 1.f,
                        0.f, 0.f, 0.f, 0.f,
                        1.f, 0.f, 1.f, 1.f,
                };

        // 頂点情報をメモリ上に確保
        FloatBuffer polygonVertices = GraphicUtil.makeFloatBuffer(vertices);
        FloatBuffer polygonColors = GraphicUtil.makeFloatBuffer(colors);

        // ポリゴンを描画
        gl.glVertexPointer(2, GL10.GL_FLOAT, 0, polygonVertices);
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glColorPointer(4, GL10.GL_FLOAT, 0, polygonColors);
        gl.glEnableClientState(GL10.GL_COLOR_ARRAY);
        gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);
    }

    // 文字画像生成
    public static Bitmap createStrImage( String str )
    {
        // ①文字列の情報を取得
        Paint paint = new Paint( Paint.ANTI_ALIAS_FLAG );
        paint.setTypeface( Typeface.DEFAULT );
        paint.setTextSize( 30 );

        Paint.FontMetrics fm = paint.getFontMetrics();

        float width = paint.measureText( str );
        float height = -fm.top + fm.bottom;

        // ②文字列が収まる大きさのBitmapを生成
        Bitmap bitmap = Bitmap.createBitmap(
                (int)width, (int)height, Bitmap.Config.ARGB_8888 );

        // ③生成したBitmapにひも付けたCanvasを用意
        Canvas canvas = new Canvas( bitmap );

        // ④CanvasのdrawText()で、Bitmapに文字列を描画
        canvas.drawText( str, 0, -fm.top, paint );

        // ⑤Bitmapデータを返す
        return bitmap;
    }

}
