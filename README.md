# NotesApp
NotesApp is a simple note-taking native android application written in Kotlin using Android Views and Jetpack Library. The notes in the app are categorized and color-coded based on the categories. It also features a Read Mode, an Edit Mode and a custom UndoRedo component that extends the functionality of EditText Views to keep track of changes per word. This component requires a NavigationBar to hold the various menu options for - UndoAll, Undo. Redo and RedoAll.

## Table of Contents
- [Getting Started](https://github.com/jilhenryx/NotesApp#getting-started).
- [Technical Details](https://github.com/jilhenryx/NotesApp#technical-details).
- [Project Samples](https://github.com/jilhenryx/NotesApp#project-samples).
- [Areas of Further Improvement](https://github.com/jilhenryx/NotesApp#areas-of-further-improvement-aka-todo).

## Getting Started
NotesApp uses Kotlin and recent Android libraries as at the point of the last commit.
To build the project and execute, ensure your Android Studio is up-to-date
Clone repository and build in Android Studio

## Technical Details
- Architecture: MVVM
- Programming Language: Kotlin 1.6
- UI: Android Views from AndroidX Library
- Architectural Components: LiveData, Lifecycle Components, Navigation Component, ViewModel
- Background Operation: Kotlin Coroutines
- DI: Hilt

[Back Up](https://github.com/jilhenryx/NotesApp#table-of-contents) :point_up_2:

## Project Samples
### Note List Activity (with Fragments)
![NotesList Activity Flow](/gitmedia/NotesApp-Edited.gif)

[Back Up](https://github.com/jilhenryx/NotesApp#table-of-contents) :point_up_2:

### Notes Activity
![Notes Activity Flow](/gitmedia/NotesApp-Edit-Note-Edited.gif)

[Back Up](https://github.com/jilhenryx/NotesApp#table-of-contents) :point_up_2:

### Notes Activity (Light Mode)
![Notes Activity Flow (Light Mode)](/gitmedia/NotesApp-Edit-Note2-Edited.gif)

[Back Up](https://github.com/jilhenryx/NotesApp#table-of-contents) :point_up_2:

## Areas of Further Improvement (a.k.a TODO)
- [ ] Integrate Cloud Functionality to persist notes across devices
- [ ] User Authentication in sync with cloud storage
- [ ] Routine upload to cloud storage
- [ ] Settings Screen

[Back Up](https://github.com/jilhenryx/NotesApp#table-of-contents) :point_up_2:

