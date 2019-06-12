package jp.ac.asojuku.st.idea_designer

import java.io.Serializable

class BS(thema:String) :Serializable{

    val thema = thema //テーマ
    var time_array = arrayOf(1200,1200,1200) //各画面の制限時間
    var elapsed_time_array = arrayOf(0,0,0) //今の経過時間
    var elapsed_place = 0 //今の画面

    var estimate_flg:Int =  0 // 0:設定無し　1:良いコメント欄有り　2:1+悪いコメント欄有り

    var idea_list:ArrayList<Idea> = ArrayList()

    //5秒毎に起動される 状態に応じて戻り値を返す　　-1:次の画面に遷移 default:時間を表示
    fun tick():Int{
        elapsed_time_array[elapsed_place] += 5
        // まだ制限時間が経過していない時、表示する時間を返す 経過後は999を返す
        if(elapsed_time_array[elapsed_place]<=time_array[elapsed_place]){
            return time_array[elapsed_place] - elapsed_time_array[elapsed_place]
        }else{
            elapsed_place++
            return -1
        }
    }
    //アイデアインスタンスを追加し、格納場所の添字を返す
    fun add_idea(idea:Idea):Int{
        val index = idea_list.size
        idea_list.add(idea)
        return index
    }
}