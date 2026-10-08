package com.royalcitrustiles.puzzle.core.ui

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.animation.OvershootInterpolator
import android.widget.FrameLayout
import com.royalcitrustiles.puzzle.R
import com.royalcitrustiles.puzzle.databinding.ViewSealBadgeBinding

class SealBadgeView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private val binding: ViewSealBadgeBinding =
        ViewSealBadgeBinding.inflate(LayoutInflater.from(context), this)

    private var glow: ValueAnimator? = null

    fun bind(title: String, unlocked: Boolean, reducedMotion: Boolean) {
        binding.sealSprite.setImageResource(R.drawable.sprite_royal_seal)
        binding.sealSprite.contentDescription = title
        binding.sealRing.setBackgroundResource(
            if (unlocked) R.drawable.ring_gold else R.drawable.ring_muted
        )
        binding.sealLockedMark.visibility = if (unlocked) View.GONE else View.VISIBLE
        if (unlocked) {
            binding.sealSprite.colorFilter = null
            binding.sealSprite.alpha = 1f
        } else {
            val matrix = ColorMatrix()
            matrix.setSaturation(0.25f)
            binding.sealSprite.colorFilter = ColorMatrixColorFilter(matrix)
            binding.sealSprite.alpha = 0.7f
        }
        if (unlocked && !reducedMotion) {
            startGlow()
        } else {
            stopGlow()
        }
    }

    fun playUnlock() {
        scaleX = 0.7f
        scaleY = 0.7f
        rotation = -8f
        animate().cancel()
        animate()
            .scaleX(1f)
            .scaleY(1f)
            .rotation(0f)
            .setDuration(520L)
            .setInterpolator(OvershootInterpolator(1.2f))
            .start()
    }

    private fun startGlow() {
        stopGlow()
        val animator = ValueAnimator.ofFloat(0.5f, 0.9f)
        animator.duration = 1600L
        animator.repeatCount = ValueAnimator.INFINITE
        animator.repeatMode = ValueAnimator.REVERSE
        animator.addUpdateListener { value ->
            try {
                if (!isAttachedToWindow) return@addUpdateListener
                binding.sealRing.alpha = value.animatedValue as Float
            } catch (e: Exception) {
            }
        }
        glow = animator
        animator.start()
    }

    private fun stopGlow() {
        glow?.cancel()
        glow = null
        binding.sealRing.alpha = 1f
    }

    fun cancelAnimations() {
        stopGlow()
        animate().cancel()
    }

    override fun onDetachedFromWindow() {
        cancelAnimations()
        super.onDetachedFromWindow()
    }
}
