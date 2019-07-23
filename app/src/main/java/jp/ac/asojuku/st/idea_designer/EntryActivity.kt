package jp.ac.asojuku.st.idea_designer

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.DialogInterface
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.firebase.database.*
import jp.ac.asojuku.st.idea_designer.instance.BS
import kotlinx.android.synthetic.main.activity_entry.*
import kotlinx.android.synthetic.main.activity_make.*
import org.jetbrains.anko.startActivity
import java.lang.NumberFormatException

class EntryActivity : AppCompatActivity() {
    private val database = FirebaseDatabase.getInstance()
    var ref: DatabaseReference = database.reference
    var isWaiting = false
    var postID = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_entry)

        entry_button_search.setOnClickListener {
            try{
                entry_edit_password.text.toString().toInt()
            }catch (e:NumberFormatException){
                entry_edit_password.setText("")
                Toast.makeText(this, "パスワードは4桁の数字です。", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if(entry_edit_password.text.toString().length != 4){
                entry_edit_password.setText("")
                Toast.makeText(this, "パスワードは4桁の数字です。", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val child = ref.child(entry_edit_password.text.toString())
            child.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError?) {}
                @SuppressLint("NewApi")
                override fun onDataChange(p0: DataSnapshot?) {
                    if(!p0!!.hasChildren() || p0.child("isHiring").value == "false"){
                        Toast.makeText(this@EntryActivity, "パスワードが間違っています。", Toast.LENGTH_SHORT).show()
                    }
                    val dialog = AlertDialog.Builder(this@EntryActivity).apply {
                        setTitle("グループ名 : " + p0.child("grope_name").value.toString()
                                + "\nテーマ : " + p0.child("thema").value.toString())
                        setMessage("\nこのグループに参加しますか？")
                        setPositiveButton("はい") { _, _ ->
                            Toast.makeText(this@EntryActivity, "グループに参加しました。", Toast.LENGTH_SHORT).show()
                            entry_text_title.visibility = View.GONE
                            entry_edit_password.visibility = View.GONE
                            entry_button_search.visibility = View.GONE
                            entry_text_state.visibility = View.VISIBLE
                            entry_text_grope.visibility = View.VISIBLE
                            entry_text_grope.text = p0.child("grope_name").value.toString()

                            val post = child.child("member").push()
                            post.setValue(mapOf("isUpdated" to "false"))
                            postID = post.key
                            isWaiting = true

                            child.addValueEventListener(object :ValueEventListener{
                                override fun onCancelled(p0: DatabaseError?) {}
                                override fun onDataChange(p0: DataSnapshot?) {
                                    if(p0!!.child("isHiring").value == "false" && isWaiting == true){
                                        isWaiting = false
                                        val timeArray = arrayOf(
                                            p0.child("time_idea").value.toString().toInt(),
                                            p0.child("time_join").value.toString().toInt(),
                                            p0.child("time_review").value.toString().toInt()
                                        )
                                        val bs = BS(p0.child("thema").value.toString(), timeArray,p0.child("commentConf").value.toString().toInt(),p0.key,postID,false)
                                        startActivity<IdeaActivity>("bs" to bs)
                                        finish()
                                    }
                                }

                            })
                        }
                        setNegativeButton("いいえ") { _, _ ->
                            entry_edit_password.setText("")
                        }
                    }.create()
                    dialog.show()
                }
            })

        }
    }
}
