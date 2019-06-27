package jp.ac.asojuku.st.idea_designer

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import jp.ac.asojuku.st.idea_designer.instance.*
import kotlinx.android.synthetic.main.activity_make.*
import org.jetbrains.anko.startActivity

class MakeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_make)

        config.setOnClickListener {
            var bs = BS("テーマ", arrayOf(20,20,120),1)
            startActivity<IdeaActivity>("bs" to bs)
        }
    }
}
