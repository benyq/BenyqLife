package com.benyq.novel

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.widget.TextView
import com.benyq.common.net.LifeApiManager
import com.benyq.common.net.RxScheduler
import com.benyq.common.ui.BaseActivity
import com.benyq.novel.fragment.BookshelfFragment
import com.benyq.novel.local.database.ObjectBox
import com.benyq.novel.local.database.enity.BookChapterEntity
import io.objectbox.Box
import io.objectbox.kotlin.boxFor

/**
* @author benyq
* @emil 1520063035@qq.com
* create at 2020/1/12
* description: 这个activity时测试用的，承载BookshelfFragment
*/
class NovelActivity : BaseActivity() {

    override fun getLayoutId() = R.layout.novel_activity_novel

    override fun initView(savedInstanceState: Bundle?) {
        supportFragmentManager.beginTransaction()
            .add(R.id.flContainer, BookshelfFragment.newInstance(), "BookshelfFragment")
            .commit()

        val box: Box<BookChapterEntity> = ObjectBox.boxStore.boxFor()
        val entity = BookChapterEntity().apply {
            chapterIndex = "3"
            contentLink = "6"
        }
        box.put(entity)
    }

    override fun initData() {
    }

}
