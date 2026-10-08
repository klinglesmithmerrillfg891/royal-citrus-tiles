package com.royalcitrustiles.puzzle.core.ui

import android.content.Context
import android.content.res.ColorStateList
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.royalcitrustiles.puzzle.R
import com.royalcitrustiles.puzzle.core.config.GameConfig
import com.royalcitrustiles.puzzle.databinding.ViewOrderProgressBinding
import com.royalcitrustiles.puzzle.domain.model.CitrusKind
import com.royalcitrustiles.puzzle.domain.model.OrderRequirement

class OrderProgressView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private val binding: ViewOrderProgressBinding =
        ViewOrderProgressBinding.inflate(LayoutInflater.from(context), this)

    private val slots: List<Slot>

    init {
        orientation = HORIZONTAL
        gravity = Gravity.CENTER_VERTICAL
        slots = listOf(
            Slot(
                binding.requirementOne,
                binding.requirementOneSprite,
                binding.requirementOneCount,
                binding.requirementOneBar,
                binding.requirementOneCheck
            ),
            Slot(
                binding.requirementTwo,
                binding.requirementTwoSprite,
                binding.requirementTwoCount,
                binding.requirementTwoBar,
                binding.requirementTwoCheck
            ),
            Slot(
                binding.requirementThree,
                binding.requirementThreeSprite,
                binding.requirementThreeCount,
                binding.requirementThreeBar,
                binding.requirementThreeCheck
            )
        )
    }

    fun bind(requirements: List<OrderRequirement>) {
        val shown = if (requirements.size > GameConfig.MAX_REQUIREMENT_CHIPS) {
            requirements.subList(0, GameConfig.MAX_REQUIREMENT_CHIPS)
        } else {
            requirements
        }
        slots.forEachIndexed { index, slot ->
            if (index >= shown.size) {
                slot.root.visibility = View.GONE
            } else {
                slot.root.visibility = View.VISIBLE
                applyRequirement(slot, shown[index])
            }
        }
    }

    fun highlight(kind: CitrusKind, requirements: List<OrderRequirement>) {
        val index = requirements.indexOfFirst { it.kind == kind }
        if (index < 0 || index >= slots.size) {
            return
        }
        val slot = slots[index]
        slot.root.animate().cancel()
        slot.root.animate().scaleX(1.12f).scaleY(1.12f).setDuration(130L)
            .setInterpolator(DecelerateInterpolator())
            .withEndAction {
                try {
                    if (!slot.root.isAttachedToWindow) return@withEndAction
                    slot.root.animate().scaleX(1f).scaleY(1f).setDuration(130L).start()
                } catch (e: Exception) {
                }
            }
            .start()
    }

    private fun applyRequirement(slot: Slot, requirement: OrderRequirement) {
        val accent = ContextCompat.getColor(
            context,
            if (requirement.isSatisfied) R.color.leaf_green else TileArt.accentColorFor(requirement.kind)
        )
        slot.sprite.setImageResource(TileArt.spriteFor(requirement.kind))
        slot.count.text = context.getString(
            R.string.value_fraction,
            requirement.clampedCollected,
            requirement.required
        )
        slot.count.setTextColor(accent)
        slot.bar.setIndicatorColor(accent)
        slot.bar.trackColor = ContextCompat.getColor(context, R.color.hairline_bark)
        slot.bar.setProgressCompat(requirement.percent, true)
        slot.check.visibility = if (requirement.isSatisfied) View.VISIBLE else View.GONE
        slot.check.imageTintList = ColorStateList.valueOf(accent)
        val kindLabel = context.getString(TileArt.labelFor(requirement.kind))
        val description = context.getString(
            R.string.desc_requirement,
            kindLabel,
            requirement.clampedCollected,
            requirement.required
        )
        slot.root.contentDescription = description
        ViewCompat.setStateDescription(slot.root, description)
    }

    fun cancelAnimations() {
        slots.forEach { it.root.animate().cancel() }
    }

    override fun onDetachedFromWindow() {
        cancelAnimations()
        super.onDetachedFromWindow()
    }

    private class Slot(
        val root: LinearLayout,
        val sprite: ImageView,
        val count: TextView,
        val bar: LinearProgressIndicator,
        val check: ImageView
    )
}
