package jp.ac.asojuku.st.idea_designer

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.provider.ContactsContract
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import jp.ac.asojuku.st.idea_designer.db.RealmHelper
import jp.ac.asojuku.st.idea_designer.instance.*
import jp.ac.asojuku.st.idea_designer.view.CollectionAdapter
import jp.ac.asojuku.st.idea_designer.view.CollectionRowData
import jp.ac.asojuku.st.idea_designer.view.RowData
import jp.ac.asojuku.st.idea_designer.view.ViewAdapter
import kotlinx.android.synthetic.main.activity_idea_list.*
import kotlinx.android.synthetic.main.idea_recycler_view.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.startActivity
import java.io.Serializable
import java.lang.reflect.Array.set


class IdeaListActivity : AppCompatActivity() {
    lateinit var bs: BS
    lateinit var coroutine: Coroutine
    var isFirstLoad = true
    var isFirstQuery = 1

    // 一時保存中アイテムのリスト
    var itemArray = ArrayList<Item>()
    var setItemPosition = 0 // 機能追加時、追加対象のアイデア番号を登録
    var modeID = 1 // どの機能で一時保存リストを開いているかのID。0:機能追加 1:コピー時 or 手動表示
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_idea_list)
        init()

        idealist_text_thema.text = bs.thema
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
        idealist_button_listvisivility.setOnClickListener {
            modeID = 1
            idealist_linear_correctItem.visibility = View.VISIBLE
        }
    }

    // bsとcoroutinを初期化する処理
    fun init() {
        bs = intent.getSerializableExtra("bs") as BS
        bs.set_time(idealist_text_time)
        coroutine = Coroutine(bs, Handler(), Inner())
        coroutine.start()
        val database = FirebaseDatabase.getInstance()

        loadFirebaseData()
        database.reference.child(bs.bsID).addValueEventListener(object :ValueEventListener{
            override fun onCancelled(p0: DatabaseError?) {}
            override fun onDataChange(p0: DataSnapshot?) {
                if(bs.isAdmin && p0!!.child("updateRequest").childrenCount > 0){
                    adminDataUpdate(p0.child("updateRequest"))
                    return
                }
                if(isFirstLoad){
                    p0!!.child("member").children.forEach {
                        if(it.child("isUpdated").value.toString() == "false"){
                            return
                        }
                    }
                    isFirstLoad = false
                    setRecyclerViewData(p0.child("member"))
                    return
                }
                if(!bs.isAdmin) {
                    if (p0!!.child("needToReload").value != "false") {
//                        idealist_button_reload.apply {
//                            visibility = View.VISIBLE
//                            setOnClickListener {
//                                visibility = View.GONE
                                setRecyclerViewData(p0.child("member"))
//                            }
//                        }
                    }
                }
            }
        })
        if(bs.isAdmin){
            database.reference.child("${bs.bsID}/updateRequest").addValueEventListener(object :ValueEventListener{
                override fun onCancelled(p0: DatabaseError?) {}
                override fun onDataChange(p0: DataSnapshot?) {
                    adminDataUpdate(p0)
                }
            })
        }
    }
    fun adminDataUpdate(p0: DataSnapshot?) {
        val database = FirebaseDatabase.getInstance()
        var ref = database.reference.child(bs.bsID).child("member")

        var indexMap = mutableMapOf<String, Int>()

        var noUpdateID = ""
        when (isFirstQuery) {
            1 -> {
                firstUpdateQuery(p0!!)
            }
            0 -> {
                return
            }
            else -> {
                p0!!.children.forEach { updateRow ->
                    if (p0.childrenCount == 1L) {
                        noUpdateID = updateRow.child("requestUserID").value.toString()
                    }
                    if (p0.childrenCount == 0L) {
                        return
                    }
                    when (updateRow.child("requestAction").value.toString()) {
                        "add" -> {
                            ref.addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onCancelled(p0: DatabaseError?) {}
                                override fun onDataChange(p0: DataSnapshot?) {
                                    val setData: Map<String, String> = mapOf(
                                        "subject" to updateRow.child("setSubject").value.toString(),
                                        "detail" to updateRow.child("setDetail").value.toString()
                                    )
                                    val changeMemberID = updateRow.child("changeMemberID").value.toString()
                                    val changeIdeaID = updateRow.child("changeIdeaID").value.toString()
                                    val target = ref.child("${updateRow.child("changeMemberID").value}/ideaList")
                                    if (indexMap.containsKey("$changeMemberID:$changeIdeaID")) {
                                        Log.d("testいんでx", indexMap["$changeMemberID:$changeIdeaID"].toString())
                                        indexMap["$changeMemberID:$changeIdeaID"] =
                                            indexMap["$changeMemberID:$changeIdeaID"]!! + 1
                                    } else {
                                        indexMap["$changeMemberID:$changeIdeaID"] = 0
                                    }


                                    database.reference.child(bs.bsID).child("noUpdateID").setValue(noUpdateID)
                                    var str = "$changeMemberID:$changeIdeaID"
                                    target.child("$changeIdeaID/itemList/${indexMap[str].toString()}")
                                        .setValue(setData)
                                        .addOnSuccessListener { loadFirebaseData() }
                                        .addOnSuccessListener {
                                            database.reference.child("${bs.bsID}/needToReload").setValue("true")
                                        }
                                }
                            })
                        }
                        "delete" -> {
                            val deleteItemAddress =
                                "${updateRow.child("deleteMemberID").value}" +
                                        "/ideaList/${updateRow.child("deleteIdeaID").value}" +
                                        "/itemList/${updateRow.child("deleteItemID").value}"
                            val setData = mapOf(
                                "noUpdateID" to noUpdateID,
                                "needToReload" to "false"
                            )
                            database.reference.child(bs.bsID).updateChildren(setData)

                            ref.child(deleteItemAddress).removeValue()
                            database.reference.child("${bs.bsID}/updateRequest/${updateRow.key}").removeValue()
                            database.reference.child(bs.bsID).child("needToReload").setValue("true")
                            loadFirebaseData()
                        }
                    }
                }
                database.reference.child("${bs.bsID}/updateRequest").removeValue()
            }
        }
    }

    fun firstUpdateQuery(p0:DataSnapshot){
        isFirstQuery = 0
        lateinit var defaultSnapshot:DataSnapshot
        val database = FirebaseDatabase.getInstance()
        var ref = database.reference.child(bs.bsID).child("member")

        p0.children.forEach{updateRow ->
            ref.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError?) {}
                override fun onDataChange(p0: DataSnapshot?) {
                    val setData: Map<String, String> = mapOf(
                        "subject" to updateRow.child("setSubject").value.toString(),
                        "detail" to updateRow.child("setDetail").value.toString()
                    )
                    val changeMemberID = updateRow.child("changeMemberID").value.toString()
                    val changeIdeaID = updateRow.child("changeIdeaID").value.toString()
                    val target = ref.child("${updateRow.child("changeMemberID").value}/ideaList")

                    database.reference.child(bs.bsID).child("noUpdateID").setValue(bs.myMemberID)
                    var str = "$changeMemberID:$changeIdeaID"
                    target.child("$changeIdeaID/itemList/${p0!!.child("$changeMemberID/ideaList/$changeIdeaID/itemList").childrenCount}")
                        .setValue(setData)
                        .addOnSuccessListener { loadFirebaseData() }
                        .addOnSuccessListener {
                            database.reference.child("${bs.bsID}/needToReload").setValue("true")
                    }
                }
            })
        }
        database.reference.child("${bs.bsID}/updateRequest").removeValue()
        isFirstQuery = -1
    }

    fun loadFirebaseData(){
        val database = FirebaseDatabase.getInstance()
        var ref = database.reference.child(bs.bsID).child("member")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError?) {}
            override fun onDataChange(p0: DataSnapshot?) {
                setRecyclerViewData(p0)
            }
        })
    }
    fun setRecyclerViewData(p0:DataSnapshot?){
        bs.idea_list.clear()
        var itemIndex = 0
        p0!!.children.forEach {
            var ideaIndex = 0
            it.child("ideaList").children.forEach { ideaRow ->
                val newIdea:Idea = Idea(
                    bs,
                    ideaRow.child("subject").value.toString(),
                    ideaRow.child("detail").value.toString()
                ).apply {
                    index = ideaIndex
                    postID = it.key
                    ideaID = ideaRow.key.toInt()
                }
                ideaRow.child("itemList").children.forEach { itemRow ->
                    Item(
                        newIdea,
                        itemRow.child("subject").value.toString(),
                        itemRow.child("detail").value.toString()
                    ).apply {
                        index = itemIndex
                        postID = it.key
                        ideaID = ideaRow.key.toInt()
                        itemID = itemRow.key.toInt()
                    }
                    itemIndex ++
                }
                ideaIndex ++
            }
        }
        for(i in bs.idea_list) {
            Log.d("idea",i.idea + " : " + i.detail)
            for(j in i.item_list){
                Log.d("item",j.item + " : " + j.detail)
            }
        }
        setRecyclerView()
        setListView()
    }

    // リサイクラービューのアイデア欄をタップした時、評価ボタンを表示する。
    fun onClickRowIdea(tappedPosition: Int, rowData: RowData, agreeButton:Button, againstButton: Button, commentText: EditText) {
        // 評価:良い ボタンを押した時の処理
        agreeButton.setOnClickListener {

        }
        // 評価:悪い ボタンを押した時の処理
        againstButton.setOnClickListener {
            Log.d("test","ぺぺぺ")
        }
    }

    // リサイクラービューのアイテムリスト
    fun onClickRowItem(listPosition: Int, itemPosition: Int, addItemButton:Button, deleteItemButton: Button, copyButton: Button, frameItemView: FrameLayout){
        // 機能追加ボタン。　選択したitemのインスタンスをideaのitem_listに登録し、リサイクラービューを再描画する。
        addItemButton.setOnClickListener {
            modeID = 0
            idealist_linear_correctItem.visibility = View.VISIBLE
            setItemPosition = listPosition
        }
        //機能削除ボタン。選択したitemをitem_listから削除する
        deleteItemButton.setOnClickListener {
            if(bs.idea_list[listPosition].item_list.size==0){
                return@setOnClickListener
            }
            frameItemView.visibility = View.GONE

            val post = FirebaseDatabase.getInstance().reference.child("${bs.bsID}/updateRequest").push()
            val setData:Map<String,String> = mapOf(
                "requestAction" to "delete",
                "requestUserID" to bs.myMemberID,
                "deleteMemberID" to bs.idea_list[listPosition].postID,
                "deleteIdeaID" to bs.idea_list[listPosition].ideaID.toString(),
                "deleteItemID" to bs.idea_list[listPosition].item_list[itemPosition].itemID.toString()
            )
            post.setValue(setData)
        }

        // 機能コピーボタン。　選択したアイテムを一時保存し、表示していたアイテムリストタップ時の2つのボタンを非表示にする。
        // 格納されている機能が「追加機能がありません」の場合は何もしない
        copyButton.setOnClickListener {
            if(bs.idea_list[listPosition].item_list.size==0){
                frameItemView.visibility = View.GONE
                return@setOnClickListener
            }
            modeID = 1
            itemArray.add(bs.idea_list[listPosition].item_list[itemPosition])
//            collectListDataChanged()
            setListView()
            idealist_linear_correctItem.visibility = View.VISIBLE
            recycler_frame_itemTapped.visibility = View.GONE
            frameItemView.visibility = View.GONE
        }
    }
    // collectListの内容が変更された時、再描画を行う。
    fun collectListDataChanged(){
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
//                    collectListDataChanged()
                    setListView()
                }
            },
            object : CollectionAdapter.ExportListener{
                override fun onClickRowExport(exportPosition: Int) {
                    val realm = RealmHelper(this@IdeaListActivity)
                    realm.realmInit()
                    realm.setItemToRealm(itemArray[exportPosition])
                }
            },
            object  : CollectionAdapter.OnTapListener{
                override fun onTapRow(tapPosition: Int) {
                    bs.idea_list[setItemPosition].add_item(itemArray[tapPosition])
                    idealist_linear_correctItem.visibility = View.GONE
                    recycler_frame_itemTapped.visibility = View.GONE
                    setRecyclerView()
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
                override fun onClickRowIdea(tappedPosition: Int, rowModel: RowData, agreeButton:Button, againstButton: Button, commentText:EditText) {
                    this@IdeaListActivity.onClickRowIdea(tappedPosition, rowModel, agreeButton, againstButton, commentText)
                }
            },
            object : ViewAdapter.ItemLisener{
                override fun onClickRowItem(listPosition: Int, itemPosition: Int, addItemButton:Button, deleteItemButton: Button, copyButton: Button, frameItemView: FrameLayout){
                    this@IdeaListActivity.onClickRowItem(listPosition, itemPosition, addItemButton, deleteItemButton, copyButton, frameItemView)
                }
            },
            this,
            this.bs
        )
        idealist_recyclerView.setHasFixedSize(true)
        idealist_recyclerView.layoutManager = LinearLayoutManager(this)
        idealist_recyclerView.adapter = adapter
        (idealist_recyclerView.adapter as ViewAdapter).notifyDataSetChanged()
    }

    fun setListView(){
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
//                    collectListDataChanged()
                    setListView()
                }
            },
            object : CollectionAdapter.ExportListener{
                override fun onClickRowExport(exportPosition: Int) {
                    val realm = RealmHelper(this@IdeaListActivity)
                    realm.realmInit()
                    realm.setItemToRealm(itemArray[exportPosition])
                }
            },
            object  : CollectionAdapter.OnTapListener{
                override fun onTapRow(tapPosition: Int) {
                    when(modeID){
                        0 -> {
                            Item(bs.idea_list[setItemPosition],itemArray[tapPosition].item,itemArray[tapPosition].detail)
//                            bs.idea_list[setItemPosition].add_item(itemArray[tapPosition])
                            bs.idea_list[setItemPosition].apply {
                                item_list[item_list.size-1].index = item_list.size-1
                            }
                            idealist_linear_correctItem.visibility = View.GONE
                            recycler_frame_itemTapped.visibility = View.GONE
                            setRecyclerView()

                            val post = FirebaseDatabase.getInstance().reference.child("${bs.bsID}/updateRequest").push()
                            val setData:Map<String,String> = mapOf(
                                "requestAction" to "add",
                                "requestUserID" to bs.myMemberID,
                                "changeMemberID" to bs.idea_list[setItemPosition].postID,
                                "changeIdeaID" to bs.idea_list[setItemPosition].ideaID.toString(),
                                "setSubject" to itemArray[tapPosition].item,
                                "setDetail" to itemArray[tapPosition].detail
                            )
                            post.setValue(setData)
                            return
                        }
                        1 -> {
                            ///////////////////////////////////////////////////////////////////////////
                            ////////////////////////////一時保存リストタップ時処理/////////////////////////
                            ///////////////////////////////////////////////////////////////////////////
                            return
                        }
                    }
                }
            })
        idealist_list_correctItem.adapter = listViewAdapter
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
