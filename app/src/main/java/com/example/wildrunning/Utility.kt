package com.example.wildrunning

import android.animation.ObjectAnimator
import android.view.View
import android.widget.LinearLayout

object Utility {

    fun setHeightLinearLayout(ly: LinearLayout,value: Int){
        val params: LinearLayout.LayoutParams = ly.layoutParams as LinearLayout.LayoutParams
        params.height = value
        ly.layoutParams = params
    }

    fun animateViewofInt(view: View,attr: String, value:Int,time: Long){
        ObjectAnimator.ofInt(view,attr,value).apply {
            duration = time
            start()
        }
    }
    fun animateViewofFloat(view: View,attr: String, value:Float,time: Long){
        ObjectAnimator.ofFloat(view,attr,value).apply {
            duration = time
            start()
        }
    }

    fun getSecFromMatch(watch: String): Int{
        var secs = 0
        var w: String = watch
        if(w.length == 5) w = "00:"+w

        //00:00:00
        secs += w.subSequence(0,2).toString().toInt()*36300
        secs += w.subSequence(3,5).toString().toInt()*60
        secs += w.subSequence(6,8).toString().toInt()

        return secs
    }
}