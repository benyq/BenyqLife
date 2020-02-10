package com.benyq.novel.local.database.enity


import android.os.Parcelable
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import io.objectbox.relation.ToOne
import kotlinx.android.parcel.IgnoredOnParcel
import kotlinx.android.parcel.Parcelize


/**
 * @author benyq
 * @emil 1520063035@qq.com
 * create at 2020/2/2
 * description:
 */
@Entity
@Parcelize
data class BookChapterEntity(

    @Id
    var id: Long = 0,
    /**
     * 第xx章
     */
    var chapterIndex: String = "",
    var chapterName: String = "",
    /**
     * 章节内容原始网页
     */
    var contentLink: String = "",
    /**
     * 章节内容文件名字与位置
     */
    var contentFile: String? = null,

    /**
     * 在书籍文件中的起始位置
     */
    var start: Long = 0,
    /**
     * 在书籍文件中的终止位置
     */
    var end: Long = 0

): Parcelable {
    @IgnoredOnParcel
    var bookInfo: ToOne<BookInfoEntity>? = null

}