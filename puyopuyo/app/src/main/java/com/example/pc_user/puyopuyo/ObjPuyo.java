package com.example.pc_user.puyopuyo;

import javax.microedition.khronos.opengles.GL10;

public class ObjPuyo extends Obj
{
    private int m_puyo_kind; // ぷよの種類
    private int m_resid;
    private ObjMap m_objmap;
    private boolean m_is_down_move;  // false : 止まっている true : 落ちている
    private boolean m_is_input_move; // false : 入力による移動できない true : 入力にする移動できる

    // ぷよの種類
    public final static int RedPuyo = 0;
    public final static int BluePuyo = 1;
    public final static int GreenPuyo = 2;
    public final static int YellowPuyo = 3;
    public final static int PurplePuyo = 4;

    public final static int PuyoKindNum = 5; // ぷよのすべての種類の総数
    public final static float PuyoSize = 0.1f; // ぷよの画像の大きさ
    public final static float PuyoSizeHalf = (PuyoSize / 2.f);

    ObjPuyo(float x, float y, int puyo_kind, int resid, ObjMap objmap)
    {
        m_x = x;
        m_y = y;
        m_puyo_kind = puyo_kind;
        m_resid = resid;
        m_objmap = objmap;
        m_is_down_move = true;
        m_is_input_move = true;
    }

    @Override
    public void Update()
    {
        if(m_is_down_move == true)
        {
            m_y += -0.005f;
        }

        // 位置をマップの配列に変換
       int[] position = m_objmap.PosConvertMapPos(m_x, m_y - PuyoSizeHalf);

        // ぷよが一番下まで言った場合
        if(m_y - PuyoSizeHalf < -0.6f)
        {
            m_y = -0.6f + PuyoSizeHalf;
            m_objmap.SetMap(position[0], position[1] - 1, this); // マップにぷよを登録
            m_is_down_move = false;
            m_is_input_move = false;
        }
        else
        {
            // 自分が動く位置にぷよがあったらぷよの位置を上げる
            if (m_objmap.CheckMapPosPuyo(position[0], position[1]) == 1 && m_is_down_move == true)
            {
                float fposition[] = m_objmap.MapElementConvertPos(position[0], position[1] - 1);
                m_y = fposition[1];
                m_objmap.SetMap(position[0], position[1] - 1, this); // マップにぷよを登録
                m_is_down_move = false;
                m_is_input_move = false;
            }

            if(m_is_down_move == false)
            {
                // 下にぷよがなかったら、落ちる(ぷよが止まっているとき)
                if (m_objmap.CheckMapPosPuyo(position[0], position[1]) == 0)
                {
                    m_is_down_move = true;
                }
            }
        }
    }

    @Override
    public void Draw(GL10 gl)
    {
        GraphicUtil.drawTexture(gl, m_x, m_y, PuyoSize, PuyoSize,
                m_resid, 1.f, 1.f, 1.f, 1.f);
    }

    // ぷよの種類を返す
    public int GetPuyoKind()
    {
        return m_puyo_kind;
    }

    // 引数1 float : xの変化量
    // 引数2 float : yの変化量
    // 戻り値 boolean : false 移動できない  true 移動できた
    public boolean Move(float x, float y)
    {
        // 入力を受け付けているかどうか
        if(m_is_input_move == true)
        {
            int position[] = m_objmap.PosConvertMapPos(m_x + x, m_y + y);

            if(position[1] < 0)
            {
                ; // 一番上のマップより上でも入力操作を受け付ける
            }
            // 範囲外
            else if(m_objmap.CheckMapPosPuyo(position[0], position[1]) != 0)
            {
                return false;
            }

            m_x += x;
            m_y += y;

            return true;
        }
        return false;
    }

    public boolean GetDownMoveFlag()
    {
        return m_is_down_move;
    }
}

