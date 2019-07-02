package jp.ac.asojuku.st.idea_designer

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.Toast
import jp.ac.asojuku.st.idea_designer.instance.BS
import jp.ac.asojuku.st.idea_designer.instance.Coroutine
import jp.ac.asojuku.st.idea_designer.instance.Item
import jp.ac.asojuku.st.idea_designer.instance.inner
import jp.ac.asojuku.st.idea_designer.view.CollectionAdapter
import jp.ac.asojuku.st.idea_designer.view.CollectionRowData
import jp.ac.asojuku.st.idea_designer.view.RowData
import jp.ac.asojuku.st.idea_designer.view.ViewAdapter
import kotlinx.android.synthetic.main.activity_idea_list.*
import kotlinx.android.synthetic.main.idea_recycler_view.*
import org.jetbrains.anko.startActivity
import java.io.Serializable



class IdeaListActivity : AppCompatActivity() {
    lateinit var bs: BS
    lateinit var coroutine: Coroutine

    // 一時保存中アイテムのリスト
    var itemArray = ArrayList<Item>()
    var setItemPosition = 0 // 機能追加時、追加対象のアイデア番号を登録
    var modeID = 0 // どの機能で一時保存リストを開いているかのID。0:機能追加 1:コピー時 or 手動表示
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_idea_list)
        init()
        setRecyclerView()

        // 一時保存のリスト作成
        val listData = ArrayList<CollectionRowData>()
        var data:CollectionRowData
        for (item in itemArray) {
            data = CollectionRowData(item.item)
            listData.add(data)
        }
        val listViewAdapter = CollectionAdapter(this,R.layout.idealist_collectionlist_row,listData,
            object : CollectionAdapter.DeleteListener {
                override fun onClickRowDelete(deletePosirion: Int) {
                    itemArray.removeAt(deletePosirion)
                    collectListDatachanged()
                }
            },
            object  : CollectionAdapter.OnTapListener{
                override fun onTapRow(tapPosition: Int) {
                    when(modeID){
                        0 -> {
                            bs.idea_list.get(setItemPosition).add_item(itemArray.get(tapPosition))
                            idealist_linear_correctItem.visibility = View.GONE
                            recycler_frame_itemTapped.visibility = View.GONE
                            setRecyclerView()
                            (idealist_recyclerView.adapter as ViewAdapter).notifyDataSetChanged()
                            return
                        }
                        1 -> {
                            ///////////////////////////////////////////////////////////////////////////
                            ////////////////////////////一時保存リストタップ時処理/////////////////////////
                            ///////////////////////////////////////////////////////////////////////////

                            ////////////あと、ブレストの赤背景をタップした時、ダイアログで一覧表示したい
                            return
                        }
                    }
                }
            })
        idealist_list_correctItem.adapter = listViewAdapter
        idealist_linear_correctItem.visibility = View.GONE


        // 一時保存中のリスト表示中、リスト外をタップしたらリストを非表示にする。
        idealist_image_correctListBlank.setOnClickListener {
            idealist_linear_correctItem.visibility = View.GONE
        }
        // 一時保存中のリストアイテムタップ時処理
        idealist_list_correctItem.setOnItemClickListener { parent, view, position, id ->
            Log.d("test",position.toString())
        }
        // ボタンタップ時、一時保存リストを表示
        button.setOnClickListener {
            modeID = 1
            idealist_linear_correctItem.visibility = View.VISIBLE
        }
    }

    // bsとcoroutinを初期化する処理
    fun init(){
        bs = intent.getSerializableExtra("bs") as BS
        bs.set_time(idealist_text_time)
        coroutine = Coroutine(bs, Handler(),Inner())
        coroutine.start()
        idealist_text_thema.setText(bs.thema)
    }

    // リサイクラービューのアイデア欄をタップした時、評価ボタンを表示する。
    fun onClickRowIdea(tappedView: View, rowData: RowData, agreeButton:Button, againstButton: Button) {
        // 評価:良い ボタンを押した時の処理
        agreeButton.setOnClickListener {

        }
        // 評価:悪い ボタンを押した時の処理
        againstButton.setOnClickListener {
            Log.d("test","ぺぺぺ")
        }
    }

    // リサイクラービューのアイテムリスト
    fun onClickRowItem(listPosition: Int, itemPosition: Int, addItemButton:Button, copyButton: Button, frameItemView: FrameLayout){
        // 機能追加ボタン。　選択したitemのインスタンスをideaのitem_listに登録し、リサイクラービューを再描画する。
        addItemButton.setOnClickListener {
            modeID = 0
            idealist_linear_correctItem.visibility = View.VISIBLE
            setItemPosition = listPosition
        }
        // 機能コピーボタン。　選択したアイテムを一時保存し、表示していたアイテムリストタップ時の2つのボタンを非表示にする。
        // 格納されている機能が「追加機能がありません」の場合は何もしない
        copyButton.setOnClickListener {
            if(bs.idea_list.get(listPosition).item_list.size==0){
                frameItemView.visibility = View.GONE
                return@setOnClickListener
            }
            modeID = 1
            itemArray.add(bs.idea_list.get(listPosition).item_list.get(itemPosition))
            collectListDatachanged()
            idealist_linear_correctItem.visibility = View.VISIBLE
            recycler_frame_itemTapped.visibility = View.GONE
            frameItemView.visibility = View.GONE
        }
    }
    // collectListの内容が変更された時、再描画を行う。
    fun collectListDatachanged(){
        val listData = ArrayList<CollectionRowData>()
        var data:CollectionRowData
        for (item in itemArray) {
            data = CollectionRowData(item.item)
            listData.add(data)
        }
        val listViewAdapter = CollectionAdapter(this,R.layout.idealist_collectionlist_row,listData,
            object : CollectionAdapter.DeleteListener {
                override fun onClickRowDelete(deletePosirion: Int) {
                    itemArray.removeAt(deletePosirion)
                    collectListDatachanged()
                }
            },
            object  : CollectionAdapter.OnTapListener{
                override fun onTapRow(tapPosition: Int) {
                    bs.idea_list.get(setItemPosition).add_item(itemArray.get(tapPosition))
                    idealist_linear_correctItem.visibility = View.GONE
                    recycler_frame_itemTapped.visibility = View.GONE
                    setRecyclerView()
                    (idealist_recyclerView.adapter as ViewAdapter).notifyDataSetChanged()
                }
            })
        idealist_list_correctItem.adapter = listViewAdapter
        (idealist_list_correctItem.adapter as CollectionAdapter).notifyDataSetChanged()
    }

    // リサイクラービューを表示する
    fun setRecyclerView(){
        var dataList = mutableListOf<RowData>()
        var data: RowData
        // viewに表示する行ごとのデータを生成
        for (i in 0..bs.idea_list.size-1) {
            var detailList = ArrayList<Item>()
            for(j in 0..bs.idea_list.get(i).item_list.size-1){
                detailList.add(bs.idea_list.get(i).item_list.get(j))
            }
            data = RowData(bs.idea_list.get(i).idea, detailList)
            dataList.add(data)
        }
        // アダプターにRowDataのリスト、項目タップ時の処理、コンテキストを渡す。
        val adapter = ViewAdapter(
            dataList,
            object : ViewAdapter.IdeaListener {
                override fun onClickRowIdea(tappedView: View, rowModel: RowData, addItemButton:Button, copyButton: Button) {
                    this@IdeaListActivity.onClickRowIdea(tappedView, rowModel, addItemButton, copyButton)
                }
            },
            object : ViewAdapter.ItemLisener{
                override fun onClickRowItem(listPosition: Int, itemPosition: Int, agreeButton:Button, againstButton: Button, frameItemView: FrameLayout){
                    this@IdeaListActivity.onClickRowItem(listPosition, itemPosition, agreeButton, againstButton, frameItemView)
                }
            },
            this,
            this.bs
        )
        idealist_recyclerView.setHasFixedSize(true)
        idealist_recyclerView.layoutManager = LinearLayoutManager(this)
        idealist_recyclerView.adapter = adapter
    }
    inner class Inner: inner(), Serializable {
        override fun intent() {
            bs.time_text = null
            startActivity<LastIdeaListActivity>("bs" to bs)
            coroutine.destroy()
            finish()
        }
    }

}
