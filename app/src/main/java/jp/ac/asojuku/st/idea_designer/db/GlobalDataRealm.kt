package jp.ac.asojuku.st.idea_designer.db

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class GlobalIntDataRealm (
    @PrimaryKey var data_id: Int? = null,
    var value: Int? = null
): RealmObject() {}
/*
    1 : bs_lastindex
    2 : idea_lastindex
    3 : item_lastindex
 */