package jp.ac.asojuku.st.idea_designer.db

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class ItemRealm(
    @PrimaryKey var item_id: Int? = null,
    open var item: String? = null,
    open var detail: String? = null,
    open var idea_id: Int? = null
): RealmObject() {}