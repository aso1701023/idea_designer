package jp.ac.asojuku.st.idea_designer.instance

import android.util.Log
import java.io.Serializable

class Idea(bs: BS, text:String, supplement:String):Serializable{

    //対応ブレストのインスタンス
    var bs = bs
    //アイデアの内容
    var text = text
    //アイデアの補足説明
    var supplement = supplement
    //対応ブレストに自身のインスタンスを登録して、返ってきた添字をindexとする
    //DB的に言えば、bsとidの複合主キー
    var index = bs.add_idea(this)
    //このアイデアに紐付けしたitem(拡張機能)のリスト
    var item_list:ArrayList<Item> = ArrayList()


    //拡張機能を紐付けし、紐付けた際の添字を返す
    fun add_item(item: Item):Int{
        val index = item_list.size
        item_list.add(item)
        return index
    }

    //ブレストとアイデアの紐付けを解除する
    fun remove(index:Int){
        item_list.removeAt(index)
        for(i in index..item_list.lastIndex){
            Log.d("test1",i.toString())
            Log.d("test2",item_list.get(i).index.toString())
            item_list.get(i).index = i
        }
    }

}