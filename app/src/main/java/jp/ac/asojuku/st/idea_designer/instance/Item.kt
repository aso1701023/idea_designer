package jp.ac.asojuku.st.idea_designer.instance

import java.io.Serializable

class Item (idea: Idea, item:String, detail:String):Serializable{

    //対応アイデアのインスタンス
    var idea = idea
    //拡張機能の内容
    var item = item
    //拡張機能の補足説明
    var detail = detail
    //対応アイデアに自身のインスタンスを登録して、返ってきた添字をidとする
    //DB的に言えば、ideaとidの複合主キー
    var index = idea.add_item(this)

    var postID = ""
    var ideaID = 0
    var itemID = 0

    //対応アイデアと自身の紐付けを解除する
    fun remove(){
        idea.item_list.removeAt(index)
    }

}