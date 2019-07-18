package jp.ac.asojuku.st.idea_designer.view

import jp.ac.asojuku.st.idea_designer.R
import android.content.Context
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import android.R.attr.thumbnail
import android.util.Log
import android.widget.*
import jp.ac.asojuku.st.idea_designer.instance.FlickCheck
import org.w3c.dom.Text
import java.text.FieldPosition


class CollectionAdapter(context: Context, var resource: Int, var list: List<CollectionRowData>, val deleteListener:DeleteListener, val exportListener:ExportListener, val onTapListner: OnTapListener): ArrayAdapter<CollectionRowData>(context,resource,list){

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = LayoutInflater.from(parent.context).inflate(jp.ac.asojuku.st.idea_designer.R.layout.idealist_collectionlist_row, parent, false)

        // リストビューに表示する要素を取得
        val item = list.get(position)

        // タイトルを設定
        val title = view.findViewById(R.id.collectionrow_text_text) as TextView
        title.setText(item.text)
        // 削除ボタンの設定
        val delete = view.findViewById(R.id.collectionrow_text_delete) as TextView
        delete.setText("削除")
        delete.visibility = View.GONE
        delete.setOnClickListener { deleteListener.onClickRowDelete(position) }
        // エクスポートボタンの設定
        val export = view.findViewById(R.id.collectionrow_text_reg) as TextView
        export.visibility = View.GONE
        export.setOnClickListener {
            exportListener.onClickRowExport(position)
            Toast.makeText(context, "アイテムを保存しました", Toast.LENGTH_SHORT).show()
        }

        object : FlickCheck(view, 10.0F, 10.0F) {

            override fun getFlick(flickData: Int) {
                when (flickData) {
                    FlickCheck.LEFT_FLICK -> {
                        Log.d("test","left")
                        deleteVisibleChange(delete,export)

                    }

                    FlickCheck.RIGHT_FLICK -> {
                        Log.d("test","right")
                        deleteVisibleChange(delete,export)
                    }
                    FlickCheck.TAP -> {
                        onTapListner.onTapRow(position)
                    }
                }
            }
        }

        return view
    }
    fun deleteVisibleChange(deleteView: View,exportView:View){
        var changeTo:Int
        if(deleteView.visibility == View.GONE){
            changeTo = View.VISIBLE
        }else{
            changeTo = View.GONE
        }
        deleteView.visibility = changeTo
        exportView.visibility = changeTo
    }
    interface DeleteListener {
        fun onClickRowDelete(deletePosirion: Int)
    }
    interface ExportListener{
        fun onClickRowExport(exportPosition: Int)
    }
    interface OnTapListener{
        fun onTapRow(tapPosition: Int)
    }
}