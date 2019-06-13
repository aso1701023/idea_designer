package jp.ac.asojuku.st.idea_designer

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class SQLiteHelper(context: Context) : SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {
    // データベースが作成された時に実行される処理
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("CREATE TABLE answers ( question_id INTEGER, answer_number INTEGER, PRIMARY KEY(question_id,answer_number) )")

    }

    // データベースをバージョンアップした時に実行される処理
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS answers")

        onCreate(db)
    }

    /*
    // データベースが開かれた時に実行される処理
    override fun onOpen(db: SQLiteDatabase) {
        super.onOpen(db)
    }
    */

    companion object {
        // データベースの名前
        private const val DB_NAME = "db.db"
        // データベースのバージョン
        private const val DB_VERSION = 3
    }
}