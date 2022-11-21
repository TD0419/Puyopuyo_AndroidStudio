package com.example.pc_user.puyopuyo;

import android.graphics.Bitmap;

import javax.microedition.khronos.opengles.GL10;

public class ObjChainText extends Obj
{
    private Integer m_chain_num; // 連鎖数
    private int m_res_id;
    private int m_ScoreTexture;
    private int m_draw_time;

    ObjChainText(float x, float y, int chain_num,int res_id, int draw_time)
    {
        m_x = x;
        m_y = y;
        m_chain_num = chain_num;
        m_res_id = res_id;
        m_draw_time = draw_time;

        StringTextureUpdate();
    }

    @Override
    public void Update()
    {
        // 表示時間を過ぎたら、オブジェクト削除
        if(m_draw_time < 0)
        {
            SetIsDelete(true);
        }

        m_draw_time--;
    }

    @Override
    public void Draw(GL10 gl)
    {
        GraphicUtil.drawTexture(gl, m_x, m_y,0.5f, 0.3f, m_ScoreTexture);
    }

    // 文字テクスチャ更新
    private void StringTextureUpdate()
    {
        Bitmap bmp = GraphicUtil.createStrImage(m_chain_num.toString() + "連鎖");
        m_ScoreTexture = GraphicUtil.loadTexture(
                Global.gl, Global.context.getResources(), bmp, m_res_id);
    }
}
