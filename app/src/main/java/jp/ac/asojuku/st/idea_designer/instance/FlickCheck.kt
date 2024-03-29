package jp.ac.asojuku.st.idea_designer.instance

import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener

abstract class FlickCheck(frickView: View, adjustX: Float, adjustY: Float) {

    private val adjustX = adjustX
//    private val adjustY = adjustY
    private var touchX: Float = 0.toFloat()
//    private var touchY: Float = 0.toFloat()
    private var nowTouchX: Float = 0.toFloat()
//    private var nowTouchY: Float = 0.toFloat()

    init {
        frickView.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    touchX = event.x
//                    touchY = event.y
                }
                MotionEvent.ACTION_UP -> {
                    nowTouchX = event.x
//                    nowTouchY = event.y
                    check()
                    v.performClick()
                }
                MotionEvent.ACTION_MOVE -> {
                }
                MotionEvent.ACTION_CANCEL -> {
                }
            }
            true
        }
    }

    /**
     * どの方向にフリックしたかチェック
     */
    private fun check() {
        Log.d(
            "FlickPoint", "startX:" + touchX + " endX:" + nowTouchX
//                    + " startY:" + touchY + " endY:" + nowTouchY
        )
        // 左フリック
        if (touchX > nowTouchX) {
            if (touchX - nowTouchX > adjustX) {
                getFlick(LEFT_FLICK)
                return
            }
        }
        // 右フリック
        if (nowTouchX > touchX) {
            if (nowTouchX - touchX > adjustX) {
                getFlick(RIGHT_FLICK)
                return
            }
        }
        getFlick(TAP)
//        // 上フリック
//        if (touchY > nowTouchY) {
//            if (touchY - nowTouchY > adjustY) {
//                getFlick(UP_FLICK)
//                return
//            }
//        }
//        // 下フリック
//        if (nowTouchY > touchY) {
//            if (nowTouchY - touchY > adjustY) {
//                getFlick(DOWN_FLICK)
//                return
//            }
//        }
    }

    /**
     * 抽象メソッド：フリックを感知した際、方向を表す値をセットする
     * @param swipe
     */
    abstract fun getFlick(swipe: Int)

    companion object {

        val LEFT_FLICK = 0
        val RIGHT_FLICK = 1
        val TAP = -1
//        val UP_FLICK = 2;
//        val DOWN_FLICK = 3;
    }

}