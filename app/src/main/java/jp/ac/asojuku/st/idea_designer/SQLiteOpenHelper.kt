package jp.ac.asojuku.st.idea_designer

import android.content.ContentValues
import android.content.Context
import android.database.CursorIndexOutOfBoundsException
import android.database.sqlite.SQLiteConstraintException
import android.database.sqlite.SQLiteDatabase


class SQLiteOpenHelper (var db:SQLiteDatabase) {

    val tableName:String = "answers";


    //問題idを受け取って、対応する答えの選択肢を返す
    fun find_answers(question_id:Int) :ArrayList<Int>? {

        val query = "SELECT * FROM " + tableName + " where question_id = " + question_id
        val cursor = db.rawQuery(query, null)

        try {
            cursor.moveToFirst()
            var array = ArrayList<Int>()
            array.add(cursor.getInt(0))
            for (i in 0 until cursor.count) {
                array.add(cursor.getInt(1))
                cursor.moveToNext();
            }
            cursor.close()
            return array
        }catch (e: CursorIndexOutOfBoundsException){
            cursor.close()
            return null
        }
    }
    fun add_record(q_id:Int, a_num:Int) {

        val values = ContentValues()
        values.put("question_id", q_id)
        values.put("answer_number", a_num)

        try {
            db.insertOrThrow(tableName, null, values)
        }catch (e: SQLiteConstraintException){
            db.update(tableName,values,"question_id = " + q_id +" and answer_number = "+ a_num,null)
        }
    }
}