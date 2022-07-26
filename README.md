# NotesApp
NotesApp is a simple note-taking native android application written in Kotlin using Android Views and Jetpack Library. It has a custom UndoRedo component that extends the functionality of EditText Views to keep track of changes per word. This component requires a NavigationBar to hold the various menu options for - UndoAll, Undo. Redo and RedoAll.

## Table of Contents
- [Getting Started](https://github.com/jilhenryx/NotesApp/edit/main/README.md#getting-started).
- [Technical Details](https://github.com/jilhenryx/NotesApp/edit/main/README.md#technical-details).
- [Project Samples](https://github.com/jilhenryx/NotesApp/edit/main/README.md#project-samples).
- [Areas of Further Improvement](https://github.com/jilhenryx/NotesApp/edit/main/README.md#areas-of-further-improvement).

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

## Project Samples
### Note List Activity (with Fragments)
![NotesList Activity Flow](/gitmedia/NotesApp-Edited.gif)

### Notes Activity
![Notes Activity Flow](/gitmedia/NotesApp-Edit-Note-Edited.gif)

### Notes Activity (Light Mode)
![Notes Activity Flow (Light Mode)](/gitmedia/NotesApp-Edit-Note2-Edited.gif)

## Areas of Further Improvement (a.k.a TODO)
- [ ] Integrate Cloud Functionality to persist notes across devices
- [ ] User Authentication in sync with cloud storage
- [ ] Routine upload to cloud storage
- [ ] Settings Screen

