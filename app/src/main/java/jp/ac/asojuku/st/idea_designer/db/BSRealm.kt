package jp.ac.asojuku.st.idea_designer.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import jp.ac.asojuku.st.idea_designer.instance.BS


open class BSRealm(
    @PrimaryKey var bs_id: Int? = null,
   var thema: String? = null
) : RealmObject()