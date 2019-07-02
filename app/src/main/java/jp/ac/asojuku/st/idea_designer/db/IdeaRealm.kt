package jp.ac.asojuku.st.idea_designer.db

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class IdeaRealm(
    @PrimaryKey var idea_id: Int? = null,
     var idea: String? = null,
     var detail: String? = null,
     var bs_id: Int? = null
): RealmObject()