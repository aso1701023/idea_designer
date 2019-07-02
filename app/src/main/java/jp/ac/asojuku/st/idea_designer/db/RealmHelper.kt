package jp.ac.asojuku.st.idea_designer.db

import android.content.Context
import io.realm.Realm
import io.realm.RealmConfiguration
import io.realm.Sort
import jp.ac.asojuku.st.idea_designer.instance.BS
import jp.ac.asojuku.st.idea_designer.instance.Idea
import jp.ac.asojuku.st.idea_designer.instance.Item
import java.lang.IndexOutOfBoundsException

class RealmHelper(val context:Context) {
    lateinit var realm: Realm
    var bsLastIndex:Int? = -1
    var ideaLastIndex:Int? = -1
    var itemLastIndex:Int? = -1

    fun realmInit(){
        Realm.init(context)
        val config = RealmConfiguration.Builder().build()
        Realm.setDefaultConfiguration(config)
        realm = Realm.getDefaultInstance()

        // BSRealmの最後の行のidを取得する。
        realm.executeTransaction {
            try {
                bsLastIndex = realm.where(GlobalIntDataRealm::class.java).equalTo("data_id", 1).findFirst().value
            } catch (e: IllegalArgumentException) {
                val bs = realm.createObject(GlobalIntDataRealm::class.java, 1)
                bs.value = -1
            }
            try {
                ideaLastIndex = realm.where(GlobalIntDataRealm::class.java).equalTo("data_id", 2).findFirst().value
            } catch (e: IllegalArgumentException) {
                val idea = realm.createObject(GlobalIntDataRealm::class.java, 2)
                idea.value = -1
            }
            try {
                itemLastIndex = realm.where(GlobalIntDataRealm::class.java).equalTo("data_id", 3).findFirst().value
            } catch (e: IllegalArgumentException) {
                val item = realm.createObject(GlobalIntDataRealm::class.java, 3)
                item.value = -1
            }
        }
    }
    // bsに登録されている情報をRealmに登録する
    fun setAllDataToRealm(bs:BS){
        // bsを登録する
        setBsToRealm(bs)
        // bsに登録されているideaを全て登録する
        for(idea in bs.idea_list){
            setIdeaToRealm(idea)
            // ideaに登録されているitemを全て登録する
            for(item in idea.item_list){
                setItemToRealm(item)
            }
            //データ確認用
//          val test = realm.where(BSRealm::class.java).findAll().last()
//           Log.d("test_bs",test.bs_id.toString())
//          val idea = realm.where(IdeaRealm::class.java).equalTo("bs_id",test.bs_id).findAll()
//          for(idea in idea){
//            Log.d("idea",idea.idea + " : " + idea.idea_id+ " : " + idea.bs_id)
//            val item = realm.where(ItemRealm::class.java).equalTo("idea_id",idea.idea_id).findAll()
//            for(item in item){
//              Log.d("item",item.item + " : " + item.item_id+ " : " + item.idea_id)
//            }
//          }
        }
    }
    fun setAllItemToRealm(idea: Idea){
        setIdeaToRealm(idea)
        for (item in idea.item_list) {
            setItemToRealm(item)
        }
    }
    fun setBsToRealm(bs: BS){
        realm.executeTransaction {
            bsLastIndex = bsLastIndex?.plus(1)
            realm.where(GlobalIntDataRealm::class.java).equalTo("data_id", 1).findFirst().value = bsLastIndex
            val bsRow = realm.createObject(BSRealm::class.java, bsLastIndex)
            bsRow.thema = bs.thema
        }
    }
    fun setIdeaToRealm(idea: Idea){
        realm.executeTransaction {
            ideaLastIndex = ideaLastIndex?.plus(1)
            realm.where(GlobalIntDataRealm::class.java).equalTo("data_id", 2).findFirst().value = ideaLastIndex
            val ideaRow = realm.createObject(IdeaRealm::class.java, ideaLastIndex)
            ideaRow.idea = idea.idea
            ideaRow.detail = idea.detail
            ideaRow.bs_id = bsLastIndex
        }
    }
    fun setItemToRealm(item: Item){
        realm.executeTransaction {
            itemLastIndex = itemLastIndex?.plus(1)
            realm.where(GlobalIntDataRealm::class.java).equalTo("data_id", 3).findFirst().value = itemLastIndex
            val itemRow = realm.createObject(ItemRealm::class.java, itemLastIndex)
            itemRow.item = item.item
            itemRow.detail = item.detail
            itemRow.idea_id = ideaLastIndex
        }
    }
    fun close(){
        realm.close()
        this.close()
    }
}