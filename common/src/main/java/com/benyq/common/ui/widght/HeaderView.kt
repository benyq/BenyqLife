package com.benyq.common.ui.widght

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.View
import android.widget.RelativeLayout
import com.benyq.common.R
import kotlinx.android.synthetic.main.view_common_toolbar.view.*

class HeaderView(context: Context, attrs: AttributeSet?, defStyleAttr: Int)
    : RelativeLayout(context, attrs, defStyleAttr){

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context) : this(context, null)

    private val typeCommon = 0
    private val typeDark = 1

    private var mType: Int = typeCommon
    private var mTitle: String
    /**
     * 是否显示返回按钮
     */
    private var mEnableBack = true
    private var mToolbarRight = false

    init {
        View.inflate(context, R.layout.view_common_toolbar, this)
        val array = getContext().obtainStyledAttributes(
            attrs,
            R.styleable.HeaderView
        )
        mType = array.getInt(R.styleable.HeaderView_toolbar_style, typeCommon)
        mTitle = array.getString(R.styleable.HeaderView_toolbar_title) ?: ""
        mEnableBack = array.getBoolean(R.styleable.HeaderView_enable_back, true)
        mToolbarRight = array.getBoolean(R.styleable.HeaderView_toolbar_right, false)

        array.recycle()

        initView()
    }


    private fun initView() {

        toolbarTitle.text = mTitle

        if (mType == typeCommon) {
            toolbarTitle.setTextColor(Color.parseColor("#3c4044"))
            toolbarRight.setTextColor(Color.parseColor("#3c4044"))
            toolbarBack.setImageResource(R.drawable.ic_arrow_left)
            toolbar.setBackgroundColor(Color.WHITE)
        }else if (mType == typeDark) {
            toolbarTitle.setTextColor(Color.WHITE)
            toolbarRight.setTextColor(Color.WHITE)
            toolbarBack.setImageResource(R.drawable.ic_arrow_left_white)
            toolbar.setBackgroundColor(Color.parseColor("#3c4044"))
        }

        toolbarBack.visibility = if (mEnableBack) View.VISIBLE else View.INVISIBLE
        toolbarRight.visibility = if (mToolbarRight) View.VISIBLE else View.INVISIBLE




    }

    fun setOnBackListener(action: ()->Unit) {
        if (mEnableBack) {
            toolbarBack.setOnClickListener {
                action()
            }
        }
    }

    fun setOnOperateListener(action: ()->Unit) {
        if (mToolbarRight) {
            toolbarRight.setOnClickListener {
                action()
            }
        }

    }

    fun setOperateTitle(value: String) {
        toolbarRight.text = value
    }

}