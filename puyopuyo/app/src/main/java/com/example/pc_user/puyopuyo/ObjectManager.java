package com.example.pc_user.puyopuyo;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;

import javax.microedition.khronos.opengles.GL10;

// スーパークラス
class Obj
{
    public void Update(){};
    public void Draw(GL10 gl){};

    protected float m_x; // 位置
    protected float m_y;

    protected boolean m_is_delete = false;

    public float GetPosX() { return m_x; }
    public float GetPosY() { return m_y; }
    public boolean GetIsDelete(){ return m_is_delete; }
    public void SetIsDelete(boolean f) {m_is_delete = f;}
}

public class ObjectManager
{
    private static ArrayList<Obj> object_list = new ArrayList<Obj>();

    // オブジェクト更新
    public static void Update()
    {
        for(Iterator<Obj>itr = object_list.iterator(); itr.hasNext();)
        {
            Obj obj = itr.next();

            if(obj.GetIsDelete() == true)
            {
                itr.remove();
                continue;
            }
            obj.Update();
        }
    }

    // オブジェクト表示
    public static void Draw(GL10 gl)
    {
        for(Iterator<Obj>itr = object_list.iterator(); itr.hasNext();)
        {
            itr.next().Draw(gl);
        }
    }

    // オブジェクト追加
    public static void Insert(Obj obj)
    {
        object_list.add(obj);
    }

    // オブジェクトデータ取得
    public static ArrayList<Obj> GetClassObjs(Class class_data)
    {
        ArrayList<Obj> out_list = new ArrayList<>();

        for(Iterator<Obj>itr = object_list.iterator(); itr.hasNext();)
        {
            Obj object = itr.next();
            if(object.getClass() == class_data)
            {
                out_list.add(object);
            }
        }

        return out_list;
    }
}

