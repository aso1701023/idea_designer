package jp.ac.asojuku.st.idea_designer

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.View
import android.widget.Button
import jp.ac.asojuku.st.idea_designer.instance.BS
import jp.ac.asojuku.st.idea_designer.instance.Coroutine
import jp.ac.asojuku.st.idea_designer.instance.Item
import jp.ac.asojuku.st.idea_designer.instance.inner
import jp.ac.asojuku.st.idea_designer.view.RowData
import jp.ac.asojuku.st.idea_designer.view.ViewAdapter
import kotlinx.android.synthetic.main.activity_idea_list.*
import org.jetbrains.anko.startActivity
import java.io.Serializable

class LastIdeaListActivity : AppCompatActivity() {

    lateinit var bs: BS
    lateinit var coroutine: Coroutine

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_idea_list)
        init()
        setRecyclerView()
    }
    fun init(){
        bs = intent.getSerializableExtra("bs") as BS
        idealist_text_thema.setText(bs.thema)
    }
    fun onClickRowIdea(tappedView: View, rowData: RowData) {
        Log.d("idea_recycler_view",rowData.title)
        Log.d("idea_recycler_view",rowData.detailList.toString())
    }
    fun onClickRowItem(listPosition: Int, itemPosition: Int){
        Log.d("test",listPosition.toString() + " : "+ itemPosition.toString())
    }

    fun setRecyclerView(){
        var dataList = mutableListOf<RowData>()
        var data: RowData
        var detailList = ArrayList<Item>()

        for (i in 0..bs.idea_list.size-1) {
            for(j in 0..bs.idea_list.get(i).item_list.size-1){
                detailList.add(bs.idea_list.get(i).item_list.get(j))
            }
            data = RowData(bs.idea_list.get(i).text, detailList)
            dataList.add(data)
        }
        val adapter = ViewAdapter(
            dataList,
            object : ViewAdapter.IdeaListener {
                override fun onClickRowIdea(tappedView: View, rowModel: RowData, addItemButton: Button, copyButton: Button) {
                    this@LastIdeaListActivity.onClickRowIdea(tappedView, rowModel)
                }
            },
            object : ViewAdapter.ItemLisener{
                override fun onClickRowItem(listPosition: Int, itemPosition: Int, agreeButton: Button, againstButton: Button){
                    this@LastIdeaListActivity.onClickRowItem(listPosition, itemPosition)
                }
            },
            this
        )
        idealist_recyclerView.setHasFixedSize(true)
        idealist_recyclerView.layoutManager = LinearLayoutManager(this)
        idealist_recyclerView.adapter = adapter


    }
    inner class Inner: inner(), Serializable {
        override fun intent() {
            bs.time_text = null
            startActivity<IdeaListActivity>("bs" to bs)
            coroutine.destroy()
            finish()
        }
    }

}
