package jp.ac.asojuku.st.idea_designer.view

import jp.ac.asojuku.st.idea_designer.instance.Item


//　リサイクラービューで表示するためのデータモデル
class RowData(title:String, detailList:ArrayList<Item>){
    var title = title
    var detailList = detailList
}