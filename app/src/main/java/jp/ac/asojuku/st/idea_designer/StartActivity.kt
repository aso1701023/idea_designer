package jp.ac.asojuku.st.idea_designer

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_start.*
import org.jetbrains.anko.startActivity

class StartActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)

        start_button.setOnClickListener {
            startActivity<SwichActivity>()
        }
    }
}
