package com.example.pc_user.puyopuyo;

import javax.microedition.khronos.opengles.GL10;

// 次に出てくるぷよのを表示する
public class ObjNextPuyo extends Obj
{
    private int m_puyo_kind_up;
    private int m_resid_up;
    private int m_puyo_kind_down;
    private int m_resid_down;

    ObjNextPuyo(float x, float y)
    {
        m_x = x;
        m_y = y;

        m_puyo_kind_up = Global.random.nextInt(ObjPuyo.PuyoKindNum);
        m_resid_up = MyRenderer.GetPuyoTextureId(m_puyo_kind_up);

        m_puyo_kind_down = Global.random.nextInt(ObjPuyo.PuyoKindNum);
        m_resid_down = MyRenderer.GetPuyoTextureId(m_puyo_kind_down);
    }

    @Override
    public void Draw(GL10 gl)
    {
        GraphicUtil.drawaRectangle(
                gl, m_x, m_y , 0.2f, 0.3f,
                1.f, 1.f, 1.f, 1.f);

        GraphicUtil.drawTexture(gl, m_x, m_y + ObjPuyo.PuyoSizeHalf,
                ObjPuyo.PuyoSize, ObjPuyo.PuyoSize,
                m_resid_up, 1.f, 1.f, 1.f, 1.f);

        GraphicUtil.drawTexture(gl, m_x, m_y - ObjPuyo.PuyoSizeHalf,
                ObjPuyo.PuyoSize, ObjPuyo.PuyoSize,
                m_resid_down, 1.f, 1.f, 1.f, 1.f);
    }

    public ObjPuyo CreatePuyoUp(float x, float y, ObjMap objmap)
    {
        ObjPuyo objpuyo = new ObjPuyo(x, y, m_puyo_kind_up, m_resid_up, objmap);
        ObjectManager.Insert(objpuyo);

        m_puyo_kind_up = Global.random.nextInt(ObjPuyo.PuyoKindNum);
        m_resid_up = MyRenderer.GetPuyoTextureId(m_puyo_kind_up);

        return objpuyo;
    }

    public ObjPuyo CreatePuyoDown(float x, float y, ObjMap objmap)
    {
        ObjPuyo objpuyo = new ObjPuyo(x, y, m_puyo_kind_down, m_resid_down, objmap);
        ObjectManager.Insert(objpuyo);

        m_puyo_kind_down = Global.random.nextInt(ObjPuyo.PuyoKindNum);
        m_resid_down = MyRenderer.GetPuyoTextureId(m_puyo_kind_down);

        return objpuyo;
    }
}
