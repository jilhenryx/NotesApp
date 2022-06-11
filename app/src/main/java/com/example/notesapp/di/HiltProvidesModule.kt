package com.example.notesapp.di

import android.content.Context
import androidx.room.Room
import com.example.notesapp.data.room.NoteDao
import com.example.notesapp.data.room.NotesAppDatabase
import com.example.notesapp.data.room.NotesAppDatabaseCallback
import com.example.notesapp.data.room.TextItemDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class HiltProvidesModule {

    @Provides
    fun provideNoteDao(notesAppDatabase: NotesAppDatabase): NoteDao {
        return notesAppDatabase.notesDao()
    }

    @Provides
    fun provideTextItemDao(notesAppDatabase: NotesAppDatabase): TextItemDao {
        return notesAppDatabase.textItemDao()
    }

    @Provides
    @Singleton
    fun providesRoomDatabase(
        @ApplicationContext context: Context,
        notesDatabaseCallback: NotesAppDatabaseCallback
    ): NotesAppDatabase {
        return Room.databaseBuilder(context, NotesAppDatabase::class.java, "notes_app_database")
            .addCallback(notesDatabaseCallback)
            .fallbackToDestructiveMigration()
            .build()
    }

}

