package com.benyq.novel

import android.content.ContentProvider
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.net.Uri
import com.benyq.novel.local.database.ObjectBox
import io.objectbox.android.AndroidObjectBrowser

/**
 * @author benyq
 * @emil 1520063035@qq.com
 * create at 2020/2/5
 * description:
 */
class InitProvider : ContentProvider() {

    override fun onCreate(): Boolean {
        ObjectBox.init(context!!)
        if (BuildConfig.DEBUG) {
            AndroidObjectBrowser(ObjectBox.boxStore).start(context!!)
        }
        return true
    }

    override fun query(
        uri: Uri,
        projection: Array<String>?,
        selection: String?,
        selectionArgs: Array<String>?,
        sortOrder: String?
    ): Cursor? {
        return null
    }

    override fun getType(uri: Uri): String? {
        return null
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        return null
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int {
        return 0
    }

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<String>?
    ): Int {
        return 0
    }
}