package jp.ac.asojuku.st.idea_designer

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import jp.ac.asojuku.st.idea_designer.instance.*
import kotlinx.android.synthetic.main.activity_idea.*
import kotlinx.android.synthetic.main.activity_make.*
import org.jetbrains.anko.startActivity
import java.io.Serializable


class IdeaActivity : AppCompatActivity(),Serializable{

    lateinit var bs:BS
    lateinit var coroutine:Coroutine

    fun init(){
        bs = intent.getSerializableExtra("bs") as BS
        bs.set_time(findViewById(R.id.idealist_text_time))
        coroutine = Coroutine(bs, Handler(),Inner())
        coroutine.start()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_idea)
        init()
        idea_button_nextpage.setOnClickListener {
            if(idea_edit_idea.text.toString() == ""){
                Toast.makeText(this, "アイデアは必須項目です", Toast.LENGTH_SHORT).show()
            }else {
                create_idea()
            }
        }
        idealist_text_thema.text = "テーマ : " + bs.thema
    }

    fun create_idea(){
        Idea(bs, idea_edit_idea.text.toString(), idea_edit_detail.text.toString())
        idea_edit_idea.setText("")
        idea_edit_detail.setText("")
    }

    inner class Inner:inner(),Serializable{
        override fun intent() {
            bs.time_text = null
            val database = FirebaseDatabase.getInstance()
            var ref: DatabaseReference = database.reference.child(bs.bsID).child("member").child(bs.myMemberID).child("ideaList")
            for(idea in bs.idea_list){
                val setData:Map<String,String> = mapOf<String, String>(
                    "subject" to idea.idea,
                    "detail" to idea.detail
                )
                ref.child(idea.index.toString()).setValue(setData)
            }
            startActivity<AddIdeaActivity>("bs" to bs)
            coroutine.destroy()
            finish()
        }
    }
}
