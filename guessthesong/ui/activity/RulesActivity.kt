package com.example.guessthesong.ui.activity

import android.os.Bundle
import android.text.Html
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.viewpager.widget.ViewPager
import com.example.guessthesong.R
import com.example.guessthesong.ui.adapter.RulesPagerAdapter
import kotlinx.android.synthetic.main.activity_rules.*

@Suppress("DEPRECATION")
class RulesActivity : AppCompatActivity() {


    private lateinit var rulesPagerAdapter: RulesPagerAdapter
    internal lateinit var pages: Array<TextView?>
    internal lateinit var welcomeScreen: IntArray

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rules)
        initUI()
        initActions()
    }

    private fun initUI() {
        welcomeScreen = intArrayOf(R.layout.rules_walkthrough_slider_1, R.layout.rules_walkthrough_slider_2, R.layout.rules_walkthrough_slider_3, R.layout.rules_walkthrough_slider_4)
        addPagination(0)
    }

    private fun initActions() {
        val viewPagerPageChangeListener = object : ViewPager.OnPageChangeListener {
            override fun onPageSelected(position: Int) {
                addPagination(position)
                if (position == welcomeScreen.size - 1) {
                    nextButton.text = getString(R.string.lets_go)
                    skipButton.visibility = View.INVISIBLE
                } else {
                    nextButton.text = getString(R.string.next)
                    skipButton.visibility = View.VISIBLE
                }
            }

            override fun onPageScrolled(arg0: Int, arg1: Float, arg2: Int) {

            }

            override fun onPageScrollStateChanged(arg0: Int) {

            }
        }


        rulesPagerAdapter = RulesPagerAdapter(welcomeScreen, this)
        viewPager.adapter = rulesPagerAdapter
        viewPager.addOnPageChangeListener(viewPagerPageChangeListener)

        skipButton.setOnClickListener { launchApp() }

        nextButton.setOnClickListener {

            val current = getItem()
            if (current < welcomeScreen.size) {
                viewPager.currentItem = current
            } else {
                launchApp()
            }
        }

    }

    private fun addPagination(currentPage: Int) {
        pages = arrayOfNulls(welcomeScreen.size)

        layoutDots.removeAllViews()
        for (i in pages.indices) {
            pages[i] = TextView(this)
            pages[i]?.text = Html.fromHtml("&#8226;")
            pages[i]?.textSize = 35f
            pages[i]?.setTextColor(ContextCompat.getColor(this,R.color.md_grey_300))
            layoutDots.addView(pages[i])
        }

        if (pages.isNotEmpty()) {
            pages[currentPage]?.setTextColor(ContextCompat.getColor(this,R.color.md_yellow_600))
        }
    }

    private fun getItem(): Int {
        return viewPager.currentItem + 1
    }

    private fun launchApp() {
        finish()
    }

}
