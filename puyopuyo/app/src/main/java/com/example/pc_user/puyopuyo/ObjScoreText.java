package com.example.pc_user.puyopuyo;

import android.graphics.Bitmap;

import java.util.HashMap;
import java.util.Map;

import javax.microedition.khronos.opengles.GL10;

public class ObjScoreText extends Obj
{
    private int m_score;
    private int m_res_id;
    private int m_ScoreTexture;
    private final Map<Integer, Integer> m_chain_bonus =
            new HashMap<Integer, Integer>();

    ObjScoreText(float x, float y, int res_id)
    {
        m_x = x;
        m_y = y;
        m_score = 0;
        m_res_id = res_id;
        InitChainData();

        StringTextureUpdate();
    }

    // スコア計算して、スコアを加算する
    public void AddScore(int puyo_remove_num, int chain_num)
    {
        int bonus_total = m_chain_bonus.get(chain_num);

        if(bonus_total == 0)
            bonus_total = 1;

        // データの範囲を超えないようにする
        if(chain_num > 19)
            chain_num = 19;

        m_score += puyo_remove_num * bonus_total * 10;
        StringTextureUpdate();
    }

    @Override
    public void Draw(GL10 gl)
    {
        GraphicUtil.drawTexture(gl, m_x, m_y,0.5f, 0.3f, m_ScoreTexture);
    }

    // 文字テクスチャ更新
    private void StringTextureUpdate()
    {
        Bitmap bmp = GraphicUtil.createStrImage(String.format("%06d", m_score));
        m_ScoreTexture = GraphicUtil.loadTexture(
                Global.gl, Global.context.getResources(), bmp, m_res_id);
    }

    // 連鎖情報を初期化する
    private void InitChainData()
    {
        m_chain_bonus.put(1, 0);
        m_chain_bonus.put(2, 8);
        m_chain_bonus.put(3, 16);
        m_chain_bonus.put(4, 32);
        m_chain_bonus.put(5, 64);
        m_chain_bonus.put(6, 96);
        m_chain_bonus.put(7, 128);
        m_chain_bonus.put(8, 160);
        m_chain_bonus.put(9, 192);
        m_chain_bonus.put(10, 224);
        m_chain_bonus.put(11, 256);
        m_chain_bonus.put(12, 288);
        m_chain_bonus.put(13, 320);
        m_chain_bonus.put(14, 352);
        m_chain_bonus.put(15, 384);
        m_chain_bonus.put(16, 416);
        m_chain_bonus.put(17, 448);
        m_chain_bonus.put(18, 480);
        m_chain_bonus.put(19, 512);
    }
}
