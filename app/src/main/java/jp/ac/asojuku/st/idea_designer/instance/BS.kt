package jp.ac.asojuku.st.idea_designer.instance

import android.util.Log
import android.widget.TextView
import java.io.Serializable


class BS(thema:String,time: Array<Int>,estimate:Int) :Serializable{

    val thema = thema //テーマ
    var time_array = time //各画面の制限時間
    var elapsed_time_array= arrayListOf(0,0,0) //今の経過時間
    var current_place = 0 //今の画面

    var time_text:TextView? = null

    var estimate_flg:Int =  estimate // 0:設定無し　1:良いコメント欄有り　2:1+悪いコメント欄有り

    var idea_list:ArrayList<Idea> = ArrayList()

    //5秒毎に起動される 状態に応じて戻り値を返す　　-1:次の画面に遷移 default:時間を表示
    fun tick():Int{
        elapsed_time_array[current_place] += 5
        // まだ制限時間が経過していない時、表示する時間を返す 経過後は999を返す
        if(elapsed_time_array[current_place]<time_array[current_place]){
            return time_array[current_place] - elapsed_time_array[current_place]
        }else{
            current_place++

            return 0
        }
    }
    //アイデアインスタンスを追加し、格納場所の添字を返す
    fun add_idea(idea: Idea):Int{
        idea_list.add(idea)
        val index = idea_list.lastIndex
        return index
    }

    fun set_time(textView:TextView){
        this.time_text = textView
        Log.d("test",current_place.toString())
        time_text?.setText(time_array[current_place].toString())
    }
    //ブレストとアイデアの紐付けを解除する
    fun remove(index:Int){
        idea_list.removeAt(index)
        for(i in index..idea_list.lastIndex){
            Log.d("test1",i.toString())
            Log.d("test2",idea_list.get(i).index.toString())
            idea_list.get(i).index = i
        }
    }
}