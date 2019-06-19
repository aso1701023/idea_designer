package jp.ac.asojuku.st.idea_designer.view

import android.support.v7.widget.RecyclerView
import android.R
import android.view.View
import android.widget.TextView


class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var titleView: TextView
    var detailView: TextView

    init {
        titleView = itemView.findViewById(R.id.title)
        detailView = itemView.findViewById(R.id.detail)

    }
}