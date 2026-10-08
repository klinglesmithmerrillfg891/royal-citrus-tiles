package com.royalcitrustiles.puzzle.core.ui

import android.animation.ValueAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import com.royalcitrustiles.puzzle.R
import com.royalcitrustiles.puzzle.core.config.GameConfig
import com.royalcitrustiles.puzzle.databinding.ViewCitrusTileBinding
import com.royalcitrustiles.puzzle.domain.model.CitrusKind

class CitrusTileView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private val binding: ViewCitrusTileBinding =
        ViewCitrusTileBinding.inflate(LayoutInflater.from(context), this)

    private var crownSpin: ValueAnimator? = null
    private var isSelectedTile = false

    fun bind(kind: CitrusKind, isCrown: Boolean, description: String, reducedMotion: Boolean) {
        binding.tileCard.setCardBackgroundColor(
            ContextCompat.getColor(context, TileArt.cardColorFor(kind))
        )
        binding.tileContent.setBackgroundResource(TileArt.backgroundFor(kind))
        binding.tileSprite.setImageResource(TileArt.spriteFor(kind))
        binding.tileCrownBadge.visibility = if (isCrown) View.VISIBLE else View.GONE
        binding.tileBurst.visibility = View.GONE
        binding.tileBurst.alpha = 1f
        alpha = 1f
        scaleX = 1f
        scaleY = 1f
        translationX = 0f
        translationY = 0f
        contentDescription = description
        showSelected(false)
        if (isCrown && !reducedMotion) {
            startCrownSpin()
        } else {
            stopCrownSpin()
        }
    }

    fun showSelected(selected: Boolean) {
        isSelectedTile = selected
        binding.tileSelectOverlay.visibility = if (selected) View.VISIBLE else View.GONE
        binding.tileCard.cardElevation = resources.getDimension(
            if (selected) R.dimen.elevation_board else R.dimen.elevation_tile
        )
        binding.tileCard.strokeWidth = resources.getDimensionPixelSize(
            if (selected) R.dimen.stroke_frame else R.dimen.stroke_hairline
        )
        val target = if (selected) 1.06f else 1.0f
        animate().cancel()
        animate().scaleX(target).scaleY(target).setDuration(120L)
            .setInterpolator(DecelerateInterpolator()).start()
        ViewCompat.setStateDescription(
            this,
            if (selected) context.getString(R.string.state_selected) else null
        )
    }

    fun isTileSelected(): Boolean = isSelectedTile

    fun playMatch() {
        binding.tileBurst.visibility = View.VISIBLE
        binding.tileBurst.alpha = 1f
        binding.tileBurst.animate().cancel()
        binding.tileBurst.animate().alpha(0f).setDuration(GameConfig.BURST_MS).start()
        animate().cancel()
        animate()
            .scaleX(1.18f)
            .scaleY(1.18f)
            .alpha(0f)
            .setDuration(GameConfig.BURST_MS)
            .setInterpolator(AccelerateInterpolator())
            .withEndAction {
                try {
                    if (!isAttachedToWindow) return@withEndAction
                    scaleX = 1f
                    scaleY = 1f
                    alpha = 1f
                    binding.tileBurst.visibility = View.GONE
                } catch (e: Exception) {
                }
            }
            .start()
    }

    fun playReject(horizontal: Boolean) {
        val shift = resources.getDimension(R.dimen.gutter_small)
        animate().cancel()
        val forward = animate().setDuration(GameConfig.SWAP_MS / 2)
            .setInterpolator(DecelerateInterpolator())
        if (horizontal) {
            forward.translationX(shift)
        } else {
            forward.translationY(shift)
        }
        forward.withEndAction {
            try {
                if (!isAttachedToWindow) return@withEndAction
                animate().translationX(0f).translationY(0f)
                    .setDuration(GameConfig.SWAP_MS / 2)
                    .setInterpolator(DecelerateInterpolator())
                    .start()
            } catch (e: Exception) {
            }
        }
        forward.start()
    }

    fun playDrop(distance: Float, delay: Long) {
        translationY = -distance
        animate().cancel()
        animate().translationY(0f)
            .setStartDelay(delay)
            .setDuration(GameConfig.DROP_MS)
            .setInterpolator(DecelerateInterpolator())
            .start()
    }

    private fun startCrownSpin() {
        stopCrownSpin()
        val animator = ValueAnimator.ofFloat(0f, 360f)
        animator.duration = 2000L
        animator.repeatCount = ValueAnimator.INFINITE
        animator.addUpdateListener { value ->
            try {
                if (!isAttachedToWindow) return@addUpdateListener
                binding.tileCrownBadge.rotation = value.animatedValue as Float
            } catch (e: Exception) {
            }
        }
        crownSpin = animator
        animator.start()
    }

    private fun stopCrownSpin() {
        crownSpin?.cancel()
        crownSpin = null
        binding.tileCrownBadge.rotation = 0f
    }

    fun cancelAnimations() {
        stopCrownSpin()
        animate().cancel()
        binding.tileBurst.animate().cancel()
    }

    override fun onDetachedFromWindow() {
        cancelAnimations()
        super.onDetachedFromWindow()
    }
}
