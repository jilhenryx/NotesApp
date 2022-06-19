package com.example.notesapp.helpers.undoredohelper

import android.util.Log
import javax.inject.Inject

private const val TAG = "TextHistory"
/*
* This class holds all the previous state of the text view as a double-linked list
* The nodes of the list is defined by its private TextNode data class
*/

class TextHistory @Inject constructor() {
    //Node references
    private var currentTextNode: TextNode? = null
    private var startTextNode: TextNode? = null
    private var endTextNode: TextNode? = null

    /*
    * Add a new node to the list and updates the references
    * The end node corresponds to a new node in the list. This is because keeping states after a new
    * node is added will lead to incorrect results.
    * The start node corresponds to the first node on the list and remains same till an insertion is
    * made at the beginning of the list
    */
    internal fun add(item: TextItem) {
        val textNode = TextNode(item)
        if (currentTextNode == null) {
            currentTextNode = textNode
            startTextNode = textNode
            endTextNode = textNode
        } else {
            currentTextNode!!.nextNode = textNode
            textNode.prevNode = currentTextNode
            currentTextNode = textNode
            endTextNode = textNode
        }
    }

    /*
    * This function performs an undo operation by removing the substring from the given text using
    * the range from the current TextItem gotten from the current node.
    * After the operation move the current node pointer one step back the list
    * If an error occurs, remove current node
     */
    internal fun performUndo(
        oldText: String,
        isDeleting: Boolean,
        onComplete: (text: String, endIndex: Int, isSuccess: Boolean, deactivateUndo: Boolean) -> Unit
    ) {
        if (currentTextNode == null) {
            onComplete(oldText, oldText.length, false, true)
            return
        }

        //Get TextItem
        val item = currentTextNode!!.textItem
        val range = item.range

        //Perform Undo. If there is an error remove the node
        try {
            Log.d(TAG, "performUndo: TextItem = $item ")
            val newText = oldText.replaceRange(range.first, range.last, "")
            val shouldDeactivate = if (isDeleting) {
                true
            } else {
                //check if all changes have been undone and notify to deactivate undo menus
                currentTextNode = currentTextNode!!.prevNode
                currentTextNode == null
            }

            onComplete(newText, range.first, true, shouldDeactivate)
        } catch (e: Exception) {
            Log.d(TAG, "performUndo: Exception caught: ${e.message}")
            removeCurrentNode()
            onComplete(oldText, oldText.length, false, false)
//            performUndo(oldText, isDeleting) { text, endIndex, deactivateUndo ->
//                onComplete(text, endIndex, deactivateUndo)
//            }
        }
    }

    /*
    * This function performs a redo operation by adding a substring to the given text using
    * the range and string from the current TextItem gotten from the current node's next node.
    * If an error occurs, remove current node
     */
    internal fun performRedo(
        oldText: String,
        isDeleting: Boolean,
        onComplete: (text: String, endIndex: Int, isSuccess: Boolean, deactivateRedo: Boolean) -> Unit
    ) {
        currentTextNode = if (isDeleting) currentTextNode else currentTextNode?.nextNode

        if (currentTextNode == null) {
            onComplete(oldText, oldText.length, false, true)
            return
        }

        //Get TextItem
        val item = currentTextNode!!.textItem
        val itemIndex = item.range.first

        try {
            Log.d(TAG, "performRedo: TextItem = $item")
            val newText = oldText.replaceRange(itemIndex, itemIndex, item.text)
            val shouldDeactivate = if (isDeleting) {
                true
            } else {
                //check if all changes have been redone and notify to deactivate redo menus
                currentTextNode!!.nextNode == null
            }
            onComplete(newText, item.range.last, true, shouldDeactivate)
        } catch (e: Exception) {
            Log.d(TAG, "performRedo: Exception caught: ${e.message}")
            removeCurrentNode()
            onComplete(oldText, oldText.length, false, false)
        }
    }

    private fun removeCurrentNode() {
        if (currentTextNode == null) return
        currentTextNode = currentTextNode!!.prevNode
        currentTextNode!!.nextNode = null
        //update end node pointer to point to the new end of the list
        endTextNode = currentTextNode
    }

    internal fun clearTextHistory() {
        currentTextNode = null
        startTextNode = null
        endTextNode = null
    }

    internal fun gotoStartNode() {
        currentTextNode = startTextNode
    }

    internal fun gotoEndNode() {
        currentTextNode = endTextNode
    }

    private data class TextNode(
        var textItem: TextItem,
        var prevNode: TextNode? = null,
        var nextNode: TextNode? = null,
    )
}