package com.benyq.novel.local.database.enity

import android.os.Parcelable
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import io.objectbox.relation.ToMany
import io.objectbox.annotation.Backlink
import kotlinx.android.parcel.IgnoredOnParcel
import kotlinx.android.parcel.Parcelize


/**
 * @author benyq
 * @emil 1520063035@qq.com
 * create at 2020/2/5
 * description: 搜索小说时缓存的数据
 */
@Entity
@Parcelize
data class BookInfoEntity(
    @Id
    var id: Long = 0,
    /**
     * 本地小说则是路径的md5
     */
    var bookId: String = "",
    var name: String = "",
    /**
     * url, 域名
     */
    var noteUrl: String = "",
    /**
     * 如果是本地小说，则这个字段是文件path
     */
    var coverUrl: String = "",
    var author: String = "",
    /**
     * 分类
     */
    var cateLog: String = "",
    /**
     * 最后更新时间
     */
    var latestUpdateTime: String = "",
    /**
     * 是否是本地小说
     */
    var isLocal: Boolean = false,
    /**
     * 是否加入书架
     */
    var imported: Boolean = false,

    /**
     * 是否更新或者打开过
     */
    var isUpdate: Boolean = false,
    /**
     * 当前阅读章节，每次打开时出现上次阅读位置
     */
    var currentChapterId: Long = 0,
    /**
     * 最后阅读时间
     */
    var readTime: Long = System.currentTimeMillis()

) : Parcelable {
    @Backlink(to = "bookInfo")
    @IgnoredOnParcel
    var bookChapters: ToMany<BookChapterEntity>? = null
}