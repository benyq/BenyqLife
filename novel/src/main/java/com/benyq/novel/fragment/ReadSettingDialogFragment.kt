package com.benyq.novel.fragment

import android.app.Activity
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.SeekBar
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.benyq.common.ui.BaseDialogFragment
import com.benyq.novel.R
import com.benyq.novel.ReadSettingConfig
import com.benyq.novel.adapter.ReadBgColorAdapter
import com.benyq.novel.book.page.PageLoader
import com.benyq.novel.book.page.PageMode
import com.blankj.utilcode.util.BrightnessUtils
import com.blankj.utilcode.util.ScreenUtils.getScreenWidth
import kotlinx.android.synthetic.main.novel_dialog_read_setting.*

/**
 * @author benyq
 * @emil 1520063035@qq.com
 * create at 2019/8/18
 * description:
 */
class ReadSettingDialogFragment : BaseDialogFragment() {

    companion object {
        fun getInstance(pageLoader: PageLoader): ReadSettingDialogFragment {
            return ReadSettingDialogFragment().apply {
                mPageLoader = pageLoader
            }
        }
    }

    private val mColors = arrayListOf("#008577", "#00574B", "#D81B60")
    private lateinit var mPageLoader: PageLoader

    private var mTextFontSize = 15

    private val mAdapter: ReadBgColorAdapter by lazy {
        ReadBgColorAdapter()
    }

    override fun getLayoutId() = R.layout.novel_dialog_read_setting

    override fun initView() {
        sbReadSettingBrightness.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                //设置屏幕亮度
                setBrightness(sbReadSettingBrightness.progress)
            }
        })
        readSettingRgPageMode.setOnCheckedChangeListener { group, checkedId ->
            val pageMode: PageMode
            when(checkedId) {
                R.id.read_setting_rb_simulation -> pageMode = PageMode.SIMULATION

                R.id.read_setting_rb_cover -> pageMode = PageMode.COVER

                R.id.read_setting_rb_slide -> pageMode = PageMode.SLIDE

                R.id.read_setting_rb_none -> pageMode = PageMode.NONE

                else -> pageMode = PageMode.NONE
            }
            mPageLoader.setPageMode(pageMode)
        }
        cbReadSettingAuto.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                //跟随系统亮度

            } else {
                setBrightness(sbReadSettingBrightness.progress)
            }
        }

        tvReadSettingFontMinus.setOnClickListener {
            mTextFontSize--
            setFontSize()
        }

        tvReadSettingFontPlus.setOnClickListener {
            mTextFontSize++
            setFontSize()
        }

        cbReadSettingFontAuto.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                //跟随系统字体大小
            } else {
                setFontSize()
            }
        }

        rvBgReadSetting.layoutManager = LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false)
        rvBgReadSetting.adapter = mAdapter
        mAdapter.setNewData(mColors)
        mAdapter.setOnItemClickListener { adapter, view, position ->
            mAdapter.index = position
            mAdapter.notifyDataSetChanged()
        }

        mTextFontSize = ReadSettingConfig.fontSize / 3
        setFontSize()
        initPageMode()
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.let {
            it.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
            val lp = it.attributes
            it.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            lp.width = getScreenWidth()
            lp.height = ViewGroup.LayoutParams.WRAP_CONTENT
            lp.gravity = Gravity.BOTTOM
            it.attributes = lp
        }
    }

    fun showDialog(manager: FragmentManager) {
        if (!isAdded) {
            show(manager, "readSetting")
        }
    }

    /**
     * 设置屏幕亮度
     */
    private fun setBrightness(light: Int) {
        ReadSettingConfig.brightness = light
        BrightnessUtils.setWindowBrightness((mContext as Activity).window, light)
    }

    /**
     * 设置小说字体大小
     */
    private fun setFontSize() {
        tvReadSettingFontSize.text = "$mTextFontSize"
        mPageLoader.setTextSize(mTextFontSize * 3)
    }

    private fun initPageMode() {
        when (ReadSettingConfig.turnPageMode) {
            PageMode.SIMULATION -> read_setting_rb_simulation.isChecked = true
            PageMode.COVER -> read_setting_rb_cover.isChecked = true
            PageMode.SLIDE -> read_setting_rb_slide.isChecked = true
            PageMode.NONE -> read_setting_rb_none.isChecked = true
            PageMode.SCROLL -> read_setting_rb_scroll.isChecked = true
        }
    }
}