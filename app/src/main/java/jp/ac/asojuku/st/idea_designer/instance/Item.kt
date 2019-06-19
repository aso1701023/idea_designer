package jp.ac.asojuku.st.idea_designer.instance

import java.io.Serializable

class Item (idea: Idea, text:String, supplement:String):Serializable{

    //対応アイデアのインスタンス
    var idea = idea
    //拡張機能の内容
    var text = text
    //拡張機能の補足説明
    var supplement = supplement
    //対応アイデアに自身のインスタンスを登録して、返ってきた添字をidとする
    //DB的に言えば、ideaとidの複合主キー
    var index = idea.add_item(this)

    //対応アイデアと自身の紐付けを解除する
    fun remove(){
        idea.item_list.removeAt(index)
    }

}