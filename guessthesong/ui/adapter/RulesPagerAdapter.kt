package com.example.guessthesong.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter

/**
 * Created by Panacea-Soft on 23/7/18.
 * Contact Email : teamps.is.cool@gmail.com
 * Website : http://www.panacea-soft.com
 */
class RulesPagerAdapter : PagerAdapter {

    private lateinit var layoutInflater: LayoutInflater
    private  lateinit var welcomeScreen: IntArray
    private  lateinit var context: Context

    constructor() {}

    constructor(welcomeScreen: IntArray, context: Context) {

        this.welcomeScreen = welcomeScreen
        this.context = context
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater


            val view = layoutInflater.inflate(welcomeScreen[position], container, false)
            container.addView(view)

            return view

    }

    override fun getCount(): Int {
        return welcomeScreen.size
    }

    override fun isViewFromObject(view: View, obj: Any): Boolean {
        return view === obj
    }


    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        val view = `object` as View
        container.removeView(view)
    }
}


