package jp.ac.asojuku.st.idea_designer.view

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.opengl.Visibility
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.FrameLayout
import android.widget.Toast
import io.realm.Realm
import io.realm.RealmConfiguration
import io.realm.Sort
import jp.ac.asojuku.st.idea_designer.IdeaListActivity
import jp.ac.asojuku.st.idea_designer.R
import jp.ac.asojuku.st.idea_designer.db.BSRealm
import jp.ac.asojuku.st.idea_designer.db.IdeaRealm
import jp.ac.asojuku.st.idea_designer.db.ItemRealm
import jp.ac.asojuku.st.idea_designer.db.RealmHelper
import jp.ac.asojuku.st.idea_designer.instance.BS
import jp.ac.asojuku.st.idea_designer.instance.Idea
import kotlinx.android.synthetic.main.dialog.view.*
import java.lang.IndexOutOfBoundsException

class ViewAdapter(private val list: List<RowData>, val ideaListener: IdeaListener, val itemLisener: ItemLisener, val context:Context, val bs: BS?) : RecyclerView.Adapter<ViewHolder>() {
    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        onBindViewHolder(p0,p1,list as MutableList<Any>)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        Log.d("Adapter", "onCreateViewHolder")
        val rowView: View = LayoutInflater.from(parent.context).inflate(R.layout.idea_recycler_view, parent, false)
        return ViewHolder(rowView)
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int, payloads: MutableList<Any>) {
        Log.d("Adapter", "onBindViewHolder")
        holder.titleView.text = list[position].title

        // 追加機能が一つもないときは、setOnItemClickListenerが機能しないため、空のリストを１行分追加する。
        var array = ArrayList<String>()
        for(item in list[position].detailList){
            array.add(item.item)
        }
        if(array.size==0){
            val listViewAdapter = ArrayAdapter(context, android.R.layout.simple_list_item_1, arrayListOf("追加機能がありません"))
            holder.detailView.adapter = listViewAdapter
        }else{
            Log.d("test",payloads.size.toString())
            val listViewAdapter = ArrayAdapter(context, android.R.layout.simple_list_item_1, array)
            holder.detailView.adapter = listViewAdapter
        }
        // 呼び出されたActivityによって、処理を分ける。
        if(context.javaClass == IdeaListActivity::class.java ) {
            // リサイクラービューのアイデア部がタップされた時、インターフェースメソッドを実行し、評価するボタンを表示する。
            holder.titleView.setOnClickListener {
                ideaListener.onClickRowIdea(it, list[position], holder.buttonAgreeView, holder.buttonAgainstView)
                changeVisibility(holder.frameIdeaView)
            }
            // リサイクラービューの追加機能部がタップされた時、インターフェースメソッドを実行し、追加機能の追加とコピーボタンを表示する。
            holder.detailView.setOnItemClickListener { parent, view, itemPosition, id ->
                itemLisener.onClickRowItem(position, itemPosition, holder.buttonAdditemView, holder.buttonCopyView, holder.frameItemView)
                changeVisibility(holder.frameItemView)
            }
        }
        holder.buttonMakeDialog.setOnClickListener {
            // アイテムがないアイデアをダイアログ表示しようとした時は、Toastでエラーメッセージ
            if(array.size == 0){
                Toast.makeText(context, "機能を追加してください", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            val inflater = LayoutInflater.from(context).inflate(R.layout.dialog, null, false)
            val dialog_idea = inflater.dialog_idea
            val dialog_list = inflater.dialog_list
            val dialog_button_addidea = inflater.dialog_button_addidea
            val dialog_buton_additem = inflater.dialog_button_additem
            val dialog_image_close = inflater.dialog_image_close
            val dialog_text_detail = inflater.dialog_text_detail

            dialog_button_addidea.visibility = View.GONE
            dialog_buton_additem.visibility = View.GONE
            dialog_text_detail.visibility = View.GONE
            // Realmの初期設定
            val realm = RealmHelper(context)
            realm.realmInit()

            // アイデアがタップされた時、アイデアエクスポートボタンを表示する
            dialog_idea.setOnClickListener {
                changeVisibility(dialog_button_addidea)
                var dialog_detail = bs!!.idea_list.get(position).detail
                if(dialog_detail == ""){dialog_detail = "補足説明はありません。"}
                dialog_text_detail.setText(dialog_detail)
                changeVisibility(dialog_text_detail)
            }
            // アイデアエクスポートボタンがタップされた時、アイデア全体をRealmに登録する
            dialog_button_addidea.setOnClickListener {
                realm.setAllItemToRealm(bs!!.idea_list.get(position))
                changeVisibility(dialog_button_addidea)
                changeVisibility(dialog_text_detail)
            }

            // リストビューの中身を設定し、アダプターとして登録
            val listViewAdapter = ArrayAdapter(context, android.R.layout.simple_list_item_1, array)

            dialog_list.adapter = listViewAdapter
            // アイテムのリストがタップされた時、アイテムエクスポートボタンを表示し、
            // エクスポートボタンのタップ時の処理（Realmに登録する処理）を再設定する（タップしたアイテムが登録されるようにする）
            dialog_list.setOnItemClickListener { adapterView, view, i, l ->
                var dialog_detail = list[position].detailList.get(i).detail
                if(dialog_detail == ""){dialog_detail = "補足説明はありません。"}
                dialog_text_detail.setText(dialog_detail)
                changeVisibility(dialog_text_detail)
                changeVisibility(dialog_buton_additem)
                dialog_buton_additem.setOnClickListener {
                    realm.setItemToRealm(list[position].detailList.get(i))
                    changeVisibility(dialog_buton_additem)

                }
            }
            // ダイアログを生成
            val dialog = AlertDialog.Builder(context).apply {
                setView(inflater)
            }.create()
            dialog.show()

            // 閉じるボタンが押された時、ダイアログを消す
            dialog_image_close.setOnClickListener {
                dialog.dismiss()
            }
        }
    }

    // 主にLinearLayoutを受け取り、そのViewが表示中であれば非表示に、非表示であれば表示中に変更する。
    fun changeVisibility(view:View){
        if(view.visibility == View.VISIBLE){
            view.visibility = View.GONE
        }else{
            view.visibility = View.VISIBLE
        }
    }

    override fun getItemCount(): Int {
        Log.d("Adapter", "getItemCount")
        return list.size
    }

    interface IdeaListener {
        fun onClickRowIdea(tappedView: View, rowModel: RowData, agreeButton: Button, againstButton: Button)
    }
    interface ItemLisener {
        fun onClickRowItem(listPosition: Int, itemPosition: Int, addButton: Button, copyButton: Button, frameItemView: FrameLayout)
    }

}