package jp.ac.asojuku.st.idea_designer.view

import jp.ac.asojuku.st.idea_designer.R
import android.content.Context
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import android.R.attr.thumbnail
import android.util.Log
import android.widget.Button
import jp.ac.asojuku.st.idea_designer.instance.FlickCheck


class CollectionAdapter(context: Context, var resource: Int, var list: List<CollectionRowData>, val deleteListener:DeleteListener, val onTapListner: OnTapListener): ArrayAdapter<CollectionRowData>(context,resource,list){

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = LayoutInflater.from(parent.context).inflate(jp.ac.asojuku.st.idea_designer.R.layout.idealist_collectionlist_row, parent, false)

        // リストビューに表示する要素を取得
        val item = list.get(position)

        // タイトルを設定
        val title = view.findViewById(R.id.collectionrow_text_text) as TextView
        title.setText(item.text)
        val delete = view.findViewById(R.id.collectionrow_text_delete) as TextView
        delete.setText("削除")
        delete.visibility = View.GONE
        delete.setOnClickListener {
            deleteListener.onClickRowDelete(position)
        }

        object : FlickCheck(view, 10.0F, 10.0F) {

            override fun getFlick(flickData: Int) {
                when (flickData) {
                    FlickCheck.LEFT_FLICK -> {
                        Log.d("test","left")
                        deleteVisibleChange(delete)
                    }

                    FlickCheck.RIGHT_FLICK -> {
                        Log.d("test","right")
                        deleteVisibleChange(delete)
                    }
                    FlickCheck.TAP -> {
                        onTapListner.onTapRow(position)
                    }
                }
            }
        }

        return view
    }
    fun deleteVisibleChange(deleteView: View){
        var changeTo:Int
        if(deleteView.visibility == View.GONE){
            changeTo = View.VISIBLE
        }else{
            changeTo = View.GONE
        }
        deleteView.visibility = changeTo
    }
    interface DeleteListener {
        fun onClickRowDelete(deletePosirion: Int)
    }
    interface OnTapListener{
        fun onTapRow(tapPosition: Int)
    }
}