package com.example.notesapp.data.room

import android.graphics.Color
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.notesapp.R
import com.example.notesapp.utility.Constants.getDateTime
import javax.inject.Inject

class NotesAppDatabaseCallback @Inject constructor() : RoomDatabase.Callback() {
    override fun onCreate(db: SupportSQLiteDatabase) {
        super.onCreate(db)
        db.execSQL(createPrePopulationString())
    }


    private fun createPrePopulationString(): String {
        //('Name',color,drawable,'time','time','mTime')
        val sb = StringBuilder()
        val time = getDateTime()
        sb.append("INSERT INTO categories ")
        sb.append("(name, colorRes, drawableRes, dateCreated,dateModified) VALUES ")
        sb.append("('Work',${getColorRes("#FF33691E")},${R.drawable.ic_baseline_work_30},'$time','$time'),")
        sb.append("('Study',${getColorRes("#FFf57f17")},${R.drawable.ic_baseline_study_30},'$time','$time'),")
        sb.append("('Personal',${getColorRes("#FF0d47a1")},${R.drawable.ic_baseline_personal_30},'$time','$time'),")
        sb.append("('Other',${getColorRes("#FF4a148c")},${R.drawable.ic_baseline_other_30},'$time','$time');")

        return sb.toString()
    }

    private fun getColorRes(hex: String): Int {
        return Color.parseColor(hex)
    }
}