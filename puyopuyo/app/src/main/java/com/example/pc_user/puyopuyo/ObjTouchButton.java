package com.example.pc_user.puyopuyo;

import javax.microedition.khronos.opengles.GL10;

public class ObjTouchButton extends Obj
{
    private final float final_circle_diameter = 0.3f;
    private final float final_circle_radius = final_circle_diameter / 2.f;

    // ボタンの位置情報
    private float m_left_button_x;
    private float m_left_button_y;
    private boolean m_left_button_touch; // 押したタイミングだけ
    private float m_right_button_x;
    private float m_right_button_y;
    private boolean m_right_button_touch; // 押したタイミングだけ
    private float m_left_rota_button_x;
    private float m_left_rota_button_y;
    private boolean m_left_rota_button_touch; // 押したタイミングだけ
    private float m_right_rota_button_x;
    private float m_right_rota_button_y;
    private boolean m_right_rota_button_touch; // 押したタイミングだけ
    private float m_bottom_button_x;
    private float m_bottom_button_y;
    private boolean m_bottom_button_touch_Leave; // 押しているとき

    // 押した瞬間だけ取得
    public boolean GetLeftButtonTouch() { return m_left_button_touch; }
    public boolean GetRightButtonTouch() { return m_right_button_touch; }
    public boolean GetLeftRotaButtonTouch() { return m_left_rota_button_touch; }
    public boolean GetRightRotaButtonTouch() { return m_right_rota_button_touch; }

    // 押しっぱなし取得
    public boolean GetBottomButtonLeaveTouch() { return m_bottom_button_touch_Leave; }

    ObjTouchButton()
    {
        m_left_button_x = -1.f;
        m_left_button_y = -0.8f;
        m_right_button_x = -0.5f;
        m_right_button_y = -0.8f;
        m_left_rota_button_x = 0.5f;
        m_left_rota_button_y = -0.8f;
        m_right_rota_button_x = 1.f;
        m_right_rota_button_y = -0.8f;
        m_bottom_button_x = 1.f;
        m_bottom_button_y = -0.5f;
    }

    @Override
    public void Update()
    {
        m_left_button_touch = false;
        m_right_button_touch = false;
        m_left_rota_button_touch = false;
        m_right_rota_button_touch = false;
        m_bottom_button_touch_Leave = false;

        if(Global.touch_push== true)
        {
            if (CollisioCircleAndPoint(m_left_button_x, m_left_button_y,
                    final_circle_radius, Global.touch_x, Global.touch_y)) {
                m_left_button_touch = true;
            }

            if (CollisioCircleAndPoint(m_right_button_x, m_right_button_y,
                    final_circle_radius, Global.touch_x, Global.touch_y)) {
                m_right_button_touch = true;
            }

            if (CollisioCircleAndPoint(m_left_rota_button_x, m_left_rota_button_y,
                    final_circle_radius, Global.touch_x, Global.touch_y)) {
                m_left_rota_button_touch = true;
            }

            if (CollisioCircleAndPoint(m_right_rota_button_x, m_right_rota_button_y,
                    final_circle_radius, Global.touch_x, Global.touch_y)) {
                m_right_rota_button_touch = true;
            }
        }

        if(Global.touch_leave == true)
        {
            if (CollisioCircleAndPoint(m_bottom_button_x, m_bottom_button_y,
                    final_circle_radius, Global.touch_x, Global.touch_y)) {
                m_bottom_button_touch_Leave = true;
            }
        }
    }

    @Override
    public void Draw(GL10 gl)
    {
        GraphicUtil.drawTexture(
                gl, m_left_button_x, m_left_button_y,
                final_circle_diameter, final_circle_diameter, MyRenderer.mLeftButtonTexture
        );

        GraphicUtil.drawTexture(
                gl, m_right_button_x, m_right_button_y,
                final_circle_diameter, final_circle_diameter, MyRenderer.mRightButtonTexture
        );

        GraphicUtil.drawTexture(
                gl, m_left_rota_button_x, m_left_rota_button_y,
                final_circle_diameter, final_circle_diameter, MyRenderer.mLeftRotaButtonTexture
        );

        GraphicUtil.drawTexture(
                gl, m_right_rota_button_x, m_right_rota_button_y,
                final_circle_diameter, final_circle_diameter, MyRenderer.mRightRotaButtonTexture
        );

        GraphicUtil.drawTexture(
                gl, m_bottom_button_x, m_bottom_button_y,
                final_circle_diameter, final_circle_diameter, MyRenderer.mBottomButtonTexture
        );
    }

    // 円と点の当たり判定
    private boolean CollisioCircleAndPoint(float c_x, float c_y, float c_r, float p_x, float p_y)
    {
        //if(Math.pow(p_x - c_x, 2) + Math.pow(p_y - c_y, 2) <= Math.pow(c_r, 2))
        if(Math.pow(c_x - p_x, 2) + Math.pow(c_y - p_y, 2) <= Math.pow(c_r, 2))
        {
            return true;
        }

        return false;
    }
}
