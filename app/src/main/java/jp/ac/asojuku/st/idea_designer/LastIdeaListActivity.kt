package jp.ac.asojuku.st.idea_designer

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import jp.ac.asojuku.st.idea_designer.db.RealmHelper
import jp.ac.asojuku.st.idea_designer.instance.BS
import jp.ac.asojuku.st.idea_designer.instance.Item
import jp.ac.asojuku.st.idea_designer.instance.inner
import jp.ac.asojuku.st.idea_designer.view.RowData
import jp.ac.asojuku.st.idea_designer.view.ViewAdapter
import kotlinx.android.synthetic.main.activity_last_idea_list.*
import org.jetbrains.anko.startActivity
import java.io.Serializable


class LastIdeaListActivity : AppCompatActivity() {

    lateinit var bs: BS
    lateinit var realm: RealmHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_last_idea_list)
        init()

        lastidea_button_finish.setOnClickListener {
            startActivity<StartActivity>()
            finish()
        }
    }
    fun init(){
        // 前画面から受け取ったBSのインスタンス
        bs = intent.getSerializableExtra("bs") as BS
        // テーマを表示
        idealist_text_thema.setText(bs.thema)
        // リサイクラービューを表示
        setRecyclerView()
        // Realmの初期処理
        realm = RealmHelper(this)
        realm.realmInit()
        // BSの情報をRealmに登録
        realm.setAllDataToRealm(bs)
    }
    fun onClickRowIdea(tappedView: View, rowData: RowData) {
        Log.d("idea_recycler_view",rowData.title)
        Log.d("idea_recycler_view",rowData.detailList.toString())
    }
    fun onClickRowItem(listPosition: Int, itemPosition: Int){

        Log.d("test", "$listPosition : $itemPosition")
    }

    fun setRecyclerView(){
        var dataList = mutableListOf<RowData>()
        var data: RowData
        // viewに表示する行ごとのデータを生成
        for (i in 0 until bs.idea_list.size) {
            var detailList = ArrayList<Item>()
            for(j in 0 until bs.idea_list[i].item_list.size){
                detailList.add(bs.idea_list[i].item_list[j])
            }
            data = RowData(bs.idea_list[i].idea, detailList)
            dataList.add(data)
        }
        // アダプターにRowDataのリスト、項目タップ時の処理、コンテキストを渡す。
        val adapter = ViewAdapter(
            dataList,
            object : ViewAdapter.IdeaListener {
                override fun onClickRowIdea(tappedPosition: Int, rowModel: RowData, agreeButton:Button, againstButton: Button, commentText: EditText) {
                    fun (){}
                }
            },
            object : ViewAdapter.ItemLisener{
                override fun onClickRowItem(listPosition: Int, itemPosition: Int, addItemButton:Button, deleteItemButton:Button, copyButton: Button, frameLayout: FrameLayout){
                    fun (){}
                }
            },
            this,
            this.bs
        )
        idealist_recyclerView.setHasFixedSize(true)
        idealist_recyclerView.layoutManager = LinearLayoutManager(this)
        idealist_recyclerView.adapter = adapter
    }

    override fun onDestroy() {
        realm.close()
        super.onDestroy()
    }


}
