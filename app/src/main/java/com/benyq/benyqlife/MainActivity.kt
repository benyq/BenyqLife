package com.benyq.benyqlife

import android.os.Bundle
import android.view.KeyEvent
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import com.blankj.utilcode.util.ToastUtils
import io.flutter.embedding.android.FlutterView
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.embedding.engine.dart.DartExecutor


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val flutterView = FlutterView(this)
        val lp: FrameLayout.LayoutParams = FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        val flContainer = findViewById<FrameLayout>(R.id.fl_container)
        flContainer.addView(flutterView, lp)
        // 关键代码，将Flutter页面显示到FlutterView中

        val flutterEngine = FlutterEngine(this)
        flutterEngine.dartExecutor.executeDartEntrypoint(
            DartExecutor.DartEntrypoint.createDefault()
        )
        flutterView.attachToFlutterEngine(flutterEngine)
    }

    private var latestClickTime = System.currentTimeMillis()
    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            //后退键的处理，按两次才退出程序
            if (System.currentTimeMillis() - latestClickTime < 1000) {
                finish()
            } else {
                latestClickTime = System.currentTimeMillis()
                ToastUtils.showShort("再按一次退出程序")
                return false
            }
        }
        return super.onKeyDown(keyCode, event)
    }
}
