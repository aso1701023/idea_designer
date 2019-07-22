package jp.ac.asojuku.st.idea_designer

import android.annotation.SuppressLint
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import com.google.firebase.database.*

import jp.ac.asojuku.st.idea_designer.instance.*
import kotlinx.android.synthetic.main.activity_make.*
import org.jetbrains.anko.startActivity
import kotlin.collections.ArrayList
import kotlin.random.Random




class MakeActivity : AppCompatActivity() {
    private val database = FirebaseDatabase.getInstance()
    private var timeArray: Array<Int> = arrayOf(10,10,10)
    private var commentConfig = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_make)
        createPass()

        setCommentConfig()

        setTimeList()

        make_button_finish.setOnClickListener {
            val ref = database.getReference("item")
            ref.addValueEventListener(object: ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                    Log.w("onCancelled", "error:", p0.toException())
                }

                override fun onDataChange(p0: DataSnapshot) {
                    val value = p0.value
                }
            })
        }
    }
    private fun setCommentConfig(){
        val adapter = ArrayAdapter(
            applicationContext,
            android.R.layout.simple_spinner_item,
            arrayOf("コメントなし","良いのみ","良い悪い両方")
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        make_spinner_commentConfig.adapter = adapter
        make_spinner_commentConfig.onItemSelectedListener = object:AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(p0: AdapterView<*>?) {}
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                commentConfig = p2
            }

        }
    }
    private fun setTimeList(){
        val adapter = ArrayAdapter(
            applicationContext,
            android.R.layout.simple_spinner_item,
            arrayOf(5,10,15,20,25,30,35,40,45,50,55,60)
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        make_spinner_idea.adapter = adapter
        make_spinner_join.adapter = adapter
        make_spinner_review.adapter = adapter
        make_spinner_idea.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            //　アイテムが選択された時
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val spinnerParent = parent as Spinner
                val item = spinnerParent.selectedItem as Int
                // Kotlin Android Extensions
                timeArray[0] = item
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
                //
            }
        }
        make_spinner_join.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            //　アイテムが選択された時
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val spinnerParent = parent as Spinner
                val item = spinnerParent.selectedItem as Int
                // Kotlin Android Extensions
                timeArray[1] = item
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
                //
            }
        }
        make_spinner_review.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            //　アイテムが選択された時
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val spinnerParent = parent as Spinner
                val item = spinnerParent.selectedItem as Int
                // Kotlin Android Extensions
                timeArray[2] = item
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
                //
            }
        }
    }
    fun createPass(){
        var ref:DatabaseReference = database.reference

        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError?) {}
            @SuppressLint("NewApi")
            override fun onDataChange(p0: DataSnapshot?) {
                if (p0 != null) {
                    val unAvailableIDList = ArrayList<String>()
                    for(item in p0.children){
                        unAvailableIDList.add(item.key)
                    }
                    var availableID = getAvailableID(unAvailableIDList)

                    make_text_password.text = "PW : $availableID"
                    make_button_member.text = "参加待機人数 : " + (p0!!.child("$availableID").child("member").childrenCount).toString() + "人"

                    ref.child("$availableID").child("member").addValueEventListener(object :ValueEventListener{
                        override fun onCancelled(p0: DatabaseError?) {}
                        override fun onDataChange(p0: DataSnapshot?) {
                            make_button_member.text = "参加待機人数 : " + (p0!!.childrenCount).toString() + "人"
                        }

                    })
                    make_button_finish.setOnClickListener {
                        when {
                            make_edit_gropeName.text.toString() == "" -> Toast.makeText(this@MakeActivity, "グループ名を入力してください", Toast.LENGTH_SHORT).show()
                            make_edit_thema.text.toString() == "" -> Toast.makeText(this@MakeActivity, "テーマを入力してください", Toast.LENGTH_SHORT).show()
                            else -> {
                                make_button_finish.text = "開始する"
                                val setData:Map<String,String> = mapOf<String, String>(
                                    "isHiring" to "true",
                                    "grope_name" to make_edit_gropeName.text.toString(),
                                    "thema" to make_edit_thema.text.toString(),
                                    "time_idea" to timeArray[0].toString(),
                                    "time_join" to timeArray[1].toString(),
                                    "time_review" to timeArray[2].toString(),
                                    "commentConf" to commentConfig.toString()
                                )
                                ref.child("$availableID").setValue(setData)

                                val post = ref.child("$availableID").child("member").push()
                                post.setValue("")
                                val postID = post.key
                                make_button_finish.setOnClickListener {
                                    ref.child("$availableID/isHiring").setValue("false")
                                    val bs = BS(make_edit_thema.text.toString(),timeArray,commentConfig,availableID.toString(),postID,true)
                                    startActivity<IdeaActivity>("bs" to bs)
                                    finish()
                                }
                            }
                        }
                    }
                }
            }
        })
    }
    // 利用可能なIDをランダムに生成して、生成したIDを返す。
    fun getAvailableID(unAvailableIDList:ArrayList<String>):Int{
        var randomID: Int
        while(true) {
            randomID = Random.nextInt(9000) + 1000
            if(unAvailableIDList.none{data -> data==randomID.toString()}){
                break
            }
        }
        return randomID
    }


}
