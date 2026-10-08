package com.royalcitrustiles.puzzle.core.ui

import android.content.Context
import android.content.res.ColorStateList
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import androidx.core.view.ViewCompat
import com.royalcitrustiles.puzzle.R
import com.royalcitrustiles.puzzle.databinding.ViewStatCardBinding

class StatCardView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private val binding: ViewStatCardBinding =
        ViewStatCardBinding.inflate(LayoutInflater.from(context), this)

    private var hideWhenZero = true

    init {
        orientation = VERTICAL
        gravity = Gravity.CENTER
        setBackgroundResource(R.drawable.stat_card_surface)
        val padding = resources.getDimensionPixelSize(R.dimen.gutter_small)
        setPadding(padding, padding, padding, padding)
        minimumHeight = resources.getDimensionPixelSize(R.dimen.touch_target)
    }

    fun setHiddenWhenZero(hidden: Boolean) {
        hideWhenZero = hidden
    }

    fun bind(label: String, value: String, accentColor: Int) {
        binding.statLabel.text = label
        binding.statValue.text = value
        binding.statValue.setTextColor(accentColor)
        binding.statAccentDot.backgroundTintList = ColorStateList.valueOf(accentColor)
        ViewCompat.setStateDescription(this, label + " " + value)
        visibility = View.VISIBLE
    }

    fun bindCount(label: String, count: Int, accentColor: Int): Boolean {
        if (count <= 0 && hideWhenZero) {
            visibility = View.GONE
            return false
        }
        bind(label, formatCount(count), accentColor)
        return true
    }

    fun bindFraction(label: String, current: Int, total: Int, accentColor: Int): Boolean {
        if (current <= 0 && hideWhenZero) {
            visibility = View.GONE
            return false
        }
        bind(label, current.toString() + "/" + total, accentColor)
        return true
    }

    private fun formatCount(count: Int): String {
        if (count < 1000) {
            return count.toString()
        }
        val digits = count.toString()
        val builder = StringBuilder()
        val leading = digits.length % 3
        if (leading > 0) {
            builder.append(digits, 0, leading)
        }
        var cursor = leading
        while (cursor < digits.length) {
            if (builder.isNotEmpty()) {
                builder.append(',')
            }
            builder.append(digits, cursor, cursor + 3)
            cursor += 3
        }
        return builder.toString()
    }

    companion object {

        fun applyRowVisibility(row: View, cards: List<StatCardView>) {
            val visible = cards.count { it.visibility == View.VISIBLE }
            row.visibility = if (visible >= 2) View.VISIBLE else View.GONE
        }
    }
}
