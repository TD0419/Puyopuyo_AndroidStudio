package com.example.pc_user.puyopuyo;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.microedition.khronos.opengles.GL10;

public class ObjMap
{
    private Map<Integer, Map<Integer, ObjPuyo>> m_GameMap = new LinkedHashMap<Integer, Map<Integer, ObjPuyo>>();
    private final int final_map_width = 6;
    private final int final_map_height = 12;

    private boolean m_PuyoCreateFlag;

    private float m_x;
    private float m_y;

    private int[] m_score_data;
    private int m_rota_element;

    private ObjPuyo m_objpuyo_up;
    private ObjPuyo m_objpuyo_down;
    private ObjScoreText m_obj_score_text;
    private ObjNextPuyo m_obj_next_puyo;
    private ObjTouchButton m_obj_touch_botton;
    private ObjChainText m_chaintext;

    ObjMap(float pos_x, float pos_y,
           ObjScoreText obj_score_text, ObjNextPuyo obj_next_puyo, ObjTouchButton obj_touch_botton)
    {
        m_x = pos_x;
        m_y = pos_y;

        // マップ情報を初期化
        for(int y = 0; y < final_map_height; y++)
        {
            Map<Integer, ObjPuyo> tempMap = new LinkedHashMap<Integer, ObjPuyo>();
            for(int x = 0; x < final_map_width; x++)
            {
                tempMap.put(x,null);
            }
            m_GameMap.put(y, tempMap);
        }

        m_PuyoCreateFlag = true;

        m_score_data = new int[2]; // 要素0・・・消したぷよの数
                                   // 要素1・・・連鎖数
        m_score_data[0] = 0;
        m_score_data[1] = 0;

        m_rota_element = 0;

        m_obj_score_text = obj_score_text;
        m_obj_next_puyo = obj_next_puyo;
        m_obj_touch_botton = obj_touch_botton;
    }

    public void Update()
    {
        // ぷよ生成
        if(m_PuyoCreateFlag == true)
        {
            m_objpuyo_up = m_obj_next_puyo.CreatePuyoUp(-0.05f, 0.7f, this);
            m_objpuyo_down = m_obj_next_puyo.CreatePuyoDown(-0.05f, 0.6f, this);

            m_score_data[1] = 1;
            m_PuyoCreateFlag = false;
            m_rota_element = 0;
        }

        // 全てのぷよが動かなくなったかを調べる
        if(m_objpuyo_up.GetDownMoveFlag() == false &&
        	m_objpuyo_down.GetDownMoveFlag() == false)
        {
            boolean is_puyo_delete = false; // ぷよ連鎖カウント用フラグ

            // 全てのぷよが動かなくなったら、生成フラグを立てる
            // 注意 : 下の処理でフラグがfalseになる可能性がある
            if(CheckPuyoDownStop() == true)
            {
                m_PuyoCreateFlag = true;
            }

            // ぷよを消す
            for (int y = 0; y < final_map_height; y++)
            {
                for (int x = 0; x < final_map_width; x++)
                {
                    // ぷよが消えていたら
                    if(Dijkstra(x, y, m_score_data) == true)
                    {
                        // ここで得点を入れる
                        m_obj_score_text.AddScore(m_score_data[0], m_score_data[1]);
                        // ぷよは生成させない
                        m_PuyoCreateFlag = false;

                        is_puyo_delete = true;
                    }
                }
            }

            // ぷよ連鎖カウント用
            if(is_puyo_delete == true)
            {
                // 古い連鎖表示オブジェクトを削除
                if(m_chaintext != null)
                {
                    m_chaintext.SetIsDelete(true);
                }

                // 連鎖数を表示
                m_chaintext = new ObjChainText(
                    0.f, 0.f, m_score_data[1], 1000, 100);
                ObjectManager.Insert(m_chaintext);
                m_score_data[1]++; // 連鎖数を増やす(連鎖数を増やす場所が難しいのでここに置いている)
            }
        }

        // タッチイベントに従ってぷよを動かす
        Puyo_Input_Move(m_objpuyo_up, m_objpuyo_down);
    }

    public void Draw(GL10 gl)
    {
        GraphicUtil.drawaRectangle(
                gl, m_x, m_y , 0.6f, 1.2f,
                1.f, 1.f, 1.f, 1.f);
    }

    // 指定した配列の中にぷよがあるかどうか
    // 戻り値 -1…範囲外 0…ぷよはない 1ぷよがある
    public int CheckMapPosPuyo(int x, int y)
    {
        // 範囲判定
        if(MapElementInRange(x, y) == false)
        {
            // 範囲外なら処理を終わらせる
            return -1;
        }

        if(m_GameMap.get(y).get(x) == null)
        {
            return 0;
        }
        return 1;
    }

    // 位置をマップの配列の要素に変換
    public int[] PosConvertMapPos(float x, float y)
    {
        int[] out_pos = new int[2];
        float map_pos_x, map_pos_y;
        // 計算が見づらい・・・修正案件
        map_pos_x = (final_map_width  / 2.f + ((x - m_x) / ObjPuyo.PuyoSize));
        map_pos_y = (final_map_height / 2.f - ((y - m_y) / ObjPuyo.PuyoSize));

        out_pos[0] = (int)map_pos_x;
        out_pos[1] = (int)map_pos_y;

        // -0.01fは 0から-1に変える
        // -0.3f は-3から-4に変える
        if(map_pos_x < 0.f)
            out_pos[0]--;
        if(map_pos_y < 0.f)
            out_pos[1]--;

        return out_pos;
    }

    // マップにぷよを登録
    public void SetMap(int x, int y, ObjPuyo objpuyo)
    {
        // 範囲判定
        if(MapElementInRange(x, y) == false)
        {
            // 範囲外なら処理を終わらせる
            return;
        }

        // マップにセット
        m_GameMap.get(y).put(x, objpuyo);
    }

    // マップの要素の範囲内かどうかを調べる
    private boolean MapElementInRange(int x, int y)
    {
        if(x >= final_map_width) return false;
        if(y >= final_map_height) return false;
        if(x < 0) return false;
        if(y < 0) return false;

        return true;
    }

    // マップの要素数から位置に変換
    public float[] MapElementConvertPos(int x, int y)
    {
        float[] out_pos = new float[2];
        // 計算が見づらい・・・修正案件
        out_pos[0] = +(x - (final_map_width  / 2.f)) * ObjPuyo.PuyoSize - ObjPuyo.PuyoSizeHalf;
        out_pos[1] = -(y - (final_map_height / 2.f)) * ObjPuyo.PuyoSize - ObjPuyo.PuyoSizeHalf;

        return out_pos;
    }

    // マップ位置にあるぷよ情報取得
    public ObjPuyo GetMapPosPuyo(int x, int y)
    {
        // 範囲判定
        if(MapElementInRange(x, y) == false)
        {
            // 範囲外なら処理を終わらせる
            return null;
        }

        return m_GameMap.get(y).get(x);
    }

    // ダイクストラ法を使って、ぷよを消す
    private boolean Dijkstra(int x, int y, int[] score_data)
    {
        boolean puyo_delete_check = false; // ぷよが消えたかどうか
        int[] count = new int[1];          // ぷよがつながっている数
        count[0] = 0;
        boolean search_comple_map[][] = new boolean[final_map_height][final_map_width];
        ObjPuyo objpuyo = m_GameMap.get(y).get(x);

        if(objpuyo == null)
        {
            return false;
        }

        // ダイクストラ法でぷよがいくつ連結しているかを調べる
        DijkstraSearch(x, y, objpuyo.GetPuyoKind(), count, search_comple_map);

        // 4つ繋がっていたら
        if(count[0] >= 4)
        {
            for(int map_y = 0; map_y < final_map_height; map_y++)
            {
                for(int map_x = 0; map_x < final_map_width; map_x++)
                {
                    if(search_comple_map[map_y][map_x] == true)
                    {
                        m_GameMap.get(map_y).get(map_x).SetIsDelete(true);
                        // マップにあるぷよ情報を消して、ぷよを落とす
                        PuyoDown(map_x, map_y);
                        puyo_delete_check = true;
                    }
                }
            }
            score_data[0] = count[0];
        }

        return puyo_delete_check;
    }

    // マップ内に固定されているぷよを外して、ぷよを落とす
    // 引数 : ぷよが消えたマップの要素位置
    private void PuyoDown(int x, int y)
    {
        while (y >= 0)
        {
            if(m_GameMap.get(y).get(x) == null)
            {
                return;
            }

            // ぷよを外す
            m_GameMap.get(y).put(x, null);

            y--;
        }
    }

    // ダイクストラ法を使って、ぷよのつながりを確認
    private void DijkstraSearch(int x, int y, int puyo_kind, int[] count, boolean[][] search_comple_map)
    {
        // 調べる方向 {x方向 , y方向}
        int direction[][] = {{-1, 0}, {0, -1}, {1, 0},{0, 1}};

        search_comple_map[y][x] = true;
        count[0]++;

        for(int i = 0; i < 4; i++)
        {
            int map_x = x + direction[i][0];
            int map_y = y + direction[i][1];
            if(MapElementInRange(map_x, map_y) == false)
                continue;

            if(search_comple_map[map_y][map_x] == true)
                continue;

            ObjPuyo objpuyo = m_GameMap.get(map_y).get(map_x);
            if(objpuyo != null && objpuyo.GetPuyoKind() == puyo_kind)
            {
                DijkstraSearch(map_x, map_y, puyo_kind, count, search_comple_map);
            }
        }
    }

    // 入力によってぷよを動かす
    private void Puyo_Input_Move(ObjPuyo objpuyo_up, ObjPuyo objpuyo_down)
    {
        //                                      上               左            下             右
        final float direction_left[][]  = {{-0.1f, -0.1f}, {0.1f, -0.1f}, {0.1f, 0.1f}, {-0.1f, 0.1f}};
        final float direction_right[][] = {{0.1f, -0.1f},  {0.1f, 0.1f}, {-0.1f, 0.1f}, {-0.1f, -0.1f}};

        if (objpuyo_up == null || objpuyo_down == null)
            return;
        if (m_obj_touch_botton.GetLeftButtonTouch() == true)
        {
            // 上と下のぷよが両方移動できるときだけ、移動させる
            if(objpuyo_up.Move(-0.1f, 0.f) == true)
            {
                if(objpuyo_down.Move(-0.1f, 0.f) == false)
                {
                    objpuyo_up.Move(0.1f, 0.f); // 移動した分を戻す
                }
            }
        }
        if (m_obj_touch_botton.GetRightButtonTouch() == true)
        {
            // 上と下のぷよが両方移動できるときだけ、移動させる
            if(objpuyo_up.Move(0.1f, 0.f) == true)
            {
                if(objpuyo_down.Move(0.1f, 0.f) == false)
                {
                    objpuyo_up.Move(-0.1f, 0.f); // 移動した分を戻す
                }
            }
        }
        if (m_obj_touch_botton.GetLeftRotaButtonTouch() == true)
        {
            boolean is_puyo_move;
            // 反時計回り
            is_puyo_move = objpuyo_up.Move(direction_left[m_rota_element][0],
                    direction_left[m_rota_element][1]);
            if(m_rota_element >= 3)
            {
                m_rota_element = 0;
            }
            else
            {
                m_rota_element++;
            }

            // ぷよが移動できない場合
            if(is_puyo_move == false)
            {
                // |●        | ←の時に
                // |○        |

                // |          | ←にする
                // |●○      |
                objpuyo_up.Move(0.f, -0.1f);
                objpuyo_down.Move(0.1f, 0.f);
            }
        }
        if (m_obj_touch_botton.GetRightRotaButtonTouch() == true)
        {
            boolean is_puyo_move;
            // 時計回り
            is_puyo_move = objpuyo_up.Move(direction_right[m_rota_element][0],
                    direction_right[m_rota_element][1]);
            if(m_rota_element <= 0)
            {
                m_rota_element = 3;
            }
            else
            {
                m_rota_element--;
            }

            // ぷよが移動できない場合
            if(is_puyo_move == false)
            {
                // |         ●| ←の時に
                // |         〇|

                // |           | ←にする
                // |       〇●|
                objpuyo_up.Move(0.f, -0.1f);
                objpuyo_down.Move(-0.1f, 0.f);
            }
        }
        if (m_obj_touch_botton.GetBottomButtonLeaveTouch() == true)
        {
            objpuyo_up.Move(0.f, -0.01f);
            objpuyo_down.Move(0.f, -0.01f);
        }
    }

    // 全てのぷよの落下が止まったかを調べる
    // 戻り値 false : 止まっていない true : 止まった
    private boolean CheckPuyoDownStop()
    {
        ArrayList<Obj> object = ObjectManager.GetClassObjs(ObjPuyo.class);

        for(Iterator<Obj> itr = object.iterator(); itr.hasNext();)
        {
            ObjPuyo objpuyo = (ObjPuyo)itr.next();

            if(objpuyo.GetDownMoveFlag() == true)
            {
                return false;
            }
        }

        return true;
    }
}
