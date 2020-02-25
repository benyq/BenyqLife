package com.benyq.novel.activity

import android.content.Context
import android.content.Intent
import com.benyq.common.ui.BaseActivity
import com.benyq.common.ui.widght.HeaderView
import com.benyq.novel.R
import kotlinx.android.synthetic.main.novel_activity_more_feature.*

class BookMoreFeatureActivity : BaseActivity() {

    companion object {

        fun goto(context: Context) {
            context.startActivity(Intent(context, BookMoreFeatureActivity::class.java))
        }
    }

    override fun getLayoutId() = R.layout.novel_activity_more_feature

    override fun initView() {
        headerView.setOnBackListener {
            finish()
        }

        ifLocalBook.setOnClickListener {
            //搜索本地小说
            LocalBookActivity.goto(this)
        }

        ifReadSetting.setOnClickListener {
            //阅读设置
        }

        ifSwapCache.setOnClickListener {
            //清理缓存
        }

        ifAbout.setOnClickListener {
            //关于
        }
    }

    override fun initData() {

    }
}
