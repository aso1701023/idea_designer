package jp.ac.asojuku.st.idea_designer

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.LinearLayout
import android.widget.ViewFlipper
import jp.ac.asojuku.st.idea_designer.instance.BS
import jp.ac.asojuku.st.idea_designer.instance.Coroutine
import jp.ac.asojuku.st.idea_designer.instance.inner
import kotlinx.android.synthetic.main.activity_add_idea.*
import kotlinx.android.synthetic.main.activity_idea_list.*
import org.jetbrains.anko.startActivity
import java.io.Serializable

class IdeaListActivity : AppCompatActivity() {
    lateinit var bs: BS
    lateinit var coroutine: Coroutine

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_idea_list)
        layoutInflater.inflate(R.layout.first_view,linearlayout)
        layoutInflater.inflate(R.layout.second_view,linearlayout)
//        init()
    }
    fun init(){
        bs = intent.getSerializableExtra("bs") as BS
        bs.set_time(findViewById(R.id.add_text_time))
        coroutine = Coroutine(bs, Handler(),Inner())
        coroutine.start()

//        add_text_thema.setText(bs.thema)



    }
    inner class Inner: inner(), Serializable {
        override fun intent() {
            bs.time_text = null
            bs.current_place++
            startActivity<IdeaListActivity>("bs" to bs)
            coroutine.destroy()
            finish()
        }
    }
}
