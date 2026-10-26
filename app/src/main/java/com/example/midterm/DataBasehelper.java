package com.example.midterm

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, "student.db", null, 1) {

    companion object {
        const val TABLE = "students"
        const val COL_ID = "student_id"
        const val COL_NAME = "name"
        const val COL_ENROLLED = "enrolled"
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            "CREATE TABLE $TABLE (" +
                    "$COL_ID TEXT PRIMARY KEY, " +
                    "$COL_NAME TEXT NOT NULL, " +
                    "$COL_ENROLLED INTEGER NOT NULL DEFAULT 0)"
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE")
        onCreate(db)
    }

    fun addStudent(student: Student): Boolean {
        val values = ContentValues().apply {
            put(COL_ID, student.studentId)
            put(COL_NAME, student.name)
            put(COL_ENROLLED, if (student.isEnrolled) 1 else 0)
        }
        val result = writableDatabase.insert(TABLE, null, values)
        return result != -1L
    }


    fun getAllStudents(): List<Student> {
        val list = mutableListOf<Student>()
        val cursor = readableDatabase.rawQuery("SELECT * FROM $TABLE ORDER BY $COL_ID", null)
        cursor.use {
            while (it.moveToNext()) {
                list.add(
                    Student(
                        it.getString(it.getColumnIndexOrThrow(COL_ID)),
                        it.getString(it.getColumnIndexOrThrow(COL_NAME)),
                        it.getInt(it.getColumnIndexOrThrow(COL_ENROLLED)) == 1
                    )
                )
            }
        }
        return list
    }


    fun isStudentExists(studentId: String): Boolean {
        val cursor = readableDatabase.rawQuery(
            "SELECT 1 FROM $TABLE WHERE $COL_ID = ?",
            arrayOf(studentId)
        )
        cursor.use {
            return it.count > 0
        }
    }


    fun updateStudent(student: Student): Boolean {
        val values = ContentValues().apply {
            put(COL_NAME, student.name)
            put(COL_ENROLLED, if (student.isEnrolled) 1 else 0)
        }
        val rows = writableDatabase.update(TABLE, values, "$COL_ID = ?", arrayOf(student.studentId))
        return rows > 0
    }


    fun deleteStudent(studentId: String): Boolean {
        val rows = writableDatabase.delete(TABLE, "$COL_ID = ?", arrayOf(studentId))
        return rows > 0
    }
}