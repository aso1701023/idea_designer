package jp.ac.asojuku.st.idea_designer.view

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.*
import jp.ac.asojuku.st.idea_designer.R
import org.jetbrains.anko.find


class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    val titleView: TextView = view.findViewById(R.id.recycler_text_idea)
    val detailView: ListView = view.findViewById(R.id.recycler_listView_detail)
    val frameIdeaView: FrameLayout = view.findViewById(R.id.recycler_frame_ideaTapped)
    val frameItemView: FrameLayout = view.findViewById(R.id.recycler_frame_itemTapped)
    val buttonMakeDialog : ImageView = view.findViewById(R.id.recycler_button_dialog)
    val commentTextView : EditText = view.findViewById(R.id.recycler_edit_comment)

    val buttonCopyView: Button = view.findViewById(R.id.recycler_button_copyItem)
    val buttonAdditemView: Button = view.findViewById(R.id.recycler_button_addItem)
    val buttonAgreeView: Button = view.findViewById(R.id.recycler_button_agree)
    val buttonAgainstView: Button = view.findViewById(R.id.recycler_button_against)
    val buttonDeleteView: Button = view.findViewById(R.id.recycler_button_deleteItem)

    init {
        frameIdeaView.visibility = View.GONE
        frameItemView.visibility = View.GONE
        detailView.setOnTouchListener{ v, event ->
            v.parent.requestDisallowInterceptTouchEvent(true)
            false
        }
    }

}