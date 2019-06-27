package jp.ac.asojuku.st.idea_designer

import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.MotionEvent
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import jp.ac.asojuku.st.idea_designer.instance.BS
import jp.ac.asojuku.st.idea_designer.instance.Coroutine
import jp.ac.asojuku.st.idea_designer.instance.Item
import jp.ac.asojuku.st.idea_designer.instance.inner
import kotlinx.android.synthetic.main.activity_add_idea.*
import java.io.Serializable
import org.jetbrains.anko.startActivity
import jp.ac.asojuku.st.idea_designer.instance.FlickCheck
import kotlinx.android.synthetic.main.second_view.view.*


class AddIdeaActivity : AppCompatActivity() {
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

        idealist_text_thema.setText(bs.thema)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_idea)
        init()
        setAnimations()
        print_idea(print_num)
        first_second = 0

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

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return super.onTouchEvent(event)
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
                secondlayout.recycler_text_idea.setText(bs.idea_list.get(idea_num).text)
                secondlayout.recycler_listView_detail.setText(bs.idea_list.get(idea_num).supplement)
            }else{
                firstlayout.recycler_text_idea.setText(bs.idea_list.get(idea_num).text)
                firstlayout.recycler_listView_detail.setText(bs.idea_list.get(idea_num).supplement)
            }

        }catch (e:IndexOutOfBoundsException){
            Toast.makeText(this, "アイデアがありません。", Toast.LENGTH_SHORT).show()
        }
    }

    fun create_item(){
        Item(bs.idea_list.get(print_num),add_edit_idea.text.toString(), add_edit_detail.text.toString())
        add_edit_idea.setText("")
        add_edit_detail.setText("")
    }
    inner class Inner:inner(), Serializable {
        override fun intent() {
            bs.time_text = null
            startActivity<IdeaListActivity>("bs" to bs)
            coroutine.destroy()
            finish()
        }
    }

    protected fun setAnimations() {
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


