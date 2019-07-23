package jp.ac.asojuku.st.idea_designer

import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.MotionEvent
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import com.google.firebase.database.*
import jp.ac.asojuku.st.idea_designer.instance.*
import kotlinx.android.synthetic.main.activity_add_idea.*
import java.io.Serializable
import org.jetbrains.anko.startActivity
import kotlinx.android.synthetic.main.second_view.view.*


open class AddIdeaActivity : AppCompatActivity() {
    lateinit var bs: BS
    lateinit var coroutine: Coroutine
    var inFromRightAnimation: Animation? = null
    var inFromLeftAnimation: Animation? = null
    var outToRightAnimation: Animation? = null
    var outToLeftAnimation: Animation? = null

    var print_num = 0 //現在表示中のアイデアの添字
    var first_second = 1 //firstが真ん中にあるときは0 secondが真ん中にあるときは1

    fun init(){
        bs = intent.getSerializableExtra("bs") as BS
        bs.set_time(add_text_time)
        coroutine = Coroutine(bs, Handler(),Inner())
        coroutine.start()

        var readFirst = true

        val database = FirebaseDatabase.getInstance()
        var ref = database.reference.child(bs.bsID).child("member")
        ref.addValueEventListener(object :ValueEventListener{
            override fun onCancelled(p0: DatabaseError?) {}
            override fun onDataChange(p0: DataSnapshot?) {
                p0!!.children.forEach {
                    if(it.child("isUpdated").value.toString() == "false"){
                        return
                    }
                }
                if(readFirst) {
                    readFirst = false
                    bs.idea_list.clear()
                    var index = 0
                    p0!!.children.forEach {
                        it.child("ideaList").children.forEach { ds ->
                            Idea(
                                bs,
                                ds.child("subject").value.toString(),
                                ds.child("detail").value.toString()
                            ).apply {
                                index = index
                                postID = it.key
                                ideaID = ds.key.toInt()
                            }
                            index++
                        }
                    }
                    print_idea(print_num)
                    first_second = 0
                }
            }
        })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_idea)
        init()
        setAnimations()


        idealist_text_thema.text = "テーマ : " + bs.thema

        var viewFlipper = viewFlipper

        add_button_reg.setOnClickListener {
            if(add_edit_idea.text.toString() == ""){
                Toast.makeText(this, "アイデアは必須項目です", Toast.LENGTH_SHORT).show()
            }else {
                create_item()
            }
        }
        object : FlickCheck(viewFlipper, 150.0F, 150.0F) {

            override fun getFlick(flickData: Int) {
                when (flickData) {
                    FlickCheck.LEFT_FLICK -> {
                        change_idea(1)
                    }

                    FlickCheck.RIGHT_FLICK -> {
                        change_idea(0)
                    }
                }
            }
        }

    }

    fun change_idea(rl:Int){
        try {
            if (rl == 1) { //右から入ってくる
                inc_print_num()
                print_idea(print_num)
                viewFlipper.setInAnimation(inFromRightAnimation);
                viewFlipper.setOutAnimation(outToLeftAnimation);
                viewFlipper.showNext()
                first_second = (first_second + 1) % 2
            } else { ///左から入ってくる
                Log.d("LLL", "LLL")
                dec_print_num()
                print_idea(print_num)
                viewFlipper.setInAnimation(inFromLeftAnimation)
                viewFlipper.setOutAnimation(outToRightAnimation)
                viewFlipper.showNext()
                first_second = (first_second + 1) % 2
            }
        }catch (e:IndexOutOfBoundsException){
            Toast.makeText(this, "アイデアがありません。", Toast.LENGTH_SHORT).show()
        }
    }

    fun print_idea(idea_num:Int){
        try{
            if(first_second == 0){
                secondlayout.recycler_text_idea.text = bs.idea_list[idea_num].idea
                secondlayout.recycler_listView_detail.text = bs.idea_list[idea_num].detail
            }else{
                firstlayout.recycler_text_idea.text = bs.idea_list[idea_num].idea
                firstlayout.recycler_listView_detail.text = bs.idea_list[idea_num].detail
            }

        }catch (e:IndexOutOfBoundsException){
            Toast.makeText(this, "アイデアがありません。", Toast.LENGTH_SHORT).show()
        }
    }

    fun create_item(){
        Item(bs.idea_list[print_num],add_edit_idea.text.toString(), add_edit_detail.text.toString())
        add_edit_idea.setText("")
        add_edit_detail.setText("")
    }
    inner class Inner:inner(), Serializable {
        override fun intent() {
            bs.time_text = null
            val database = FirebaseDatabase.getInstance()
            var ref: DatabaseReference = database.reference.child(bs.bsID).child("member")
            var i = 1
            var j = 1
            for(idea in bs.idea_list){
                for(item in idea.item_list) {
                    val post = FirebaseDatabase.getInstance().reference.child("${bs.bsID}/updateRequest").push()
                    val setData:Map<String,String> = mapOf(
                        "requestAction" to "add",
                        "requestUserID" to bs.myMemberID,
                        "changeMemberID" to idea.postID,
                        "changeIdeaID" to idea.ideaID.toString(),
                        "setSubject" to item.item,
                        "setDetail" to item.detail
                    )
                    if(i==bs.idea_list.size && j==idea.item_list.size){
                        post.setValue(setData) .addOnSuccessListener {
                            ref.child("${bs.myMemberID}/isUpdated").setValue("true")
                        }
                    }
                    else{post.setValue(setData)}

                    j++
                }
                i++
            }
            startActivity<IdeaListActivity>("bs" to bs)
            coroutine.destroy()
            finish()
        }
    }

    fun setAnimations() {
        inFromRightAnimation =
            AnimationUtils.loadAnimation(this, R.anim.slide_in_from_right);
        inFromLeftAnimation =
            AnimationUtils.loadAnimation(this, R.anim.slide_in_from_left);
        outToRightAnimation =
            AnimationUtils.loadAnimation(this, R.anim.slide_out_to_right);
        outToLeftAnimation =
            AnimationUtils.loadAnimation(this, R.anim.slide_out_to_left);
    }

    fun inc_print_num(){
        print_num ++
       if(bs.idea_list.size <= print_num ){
           print_num = 0
       }
    }
    fun dec_print_num() {
        print_num --
        if(print_num<0){
            print_num = bs.idea_list.size-1
        }
    }
}


