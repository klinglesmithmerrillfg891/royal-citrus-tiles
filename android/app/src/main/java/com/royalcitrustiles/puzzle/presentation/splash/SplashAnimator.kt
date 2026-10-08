package com.royalcitrustiles.puzzle.presentation.splash

import android.animation.ValueAnimator
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.view.animation.OvershootInterpolator
import com.royalcitrustiles.puzzle.databinding.FragmentSplashBinding

class SplashAnimator {

    private val loops = ArrayList<ValueAnimator>()

    fun prepare(binding: FragmentSplashBinding) {
        binding.splashMedallion.alpha = 0f
        binding.splashMedallion.scaleX = 0.82f
        binding.splashMedallion.scaleY = 0.82f
        binding.splashTitle.alpha = 0f
        binding.splashTitle.translationY = TITLE_OFFSET
        binding.splashTagline.alpha = 0f
        binding.splashDividerTop.alpha = 0f
        binding.splashDividerBottom.alpha = 0f
        binding.splashSprigStart.alpha = 0f
        binding.splashSprigEnd.alpha = 0f
        binding.splashCaption.alpha = 0f
    }

    fun playEntrance(binding: FragmentSplashBinding) {
        binding.splashMedallion.animate()
            .alpha(1f)
            .scaleX(1f)
            .scaleY(1f)
            .setDuration(520L)
            .setInterpolator(OvershootInterpolator(1.1f))
            .start()

        binding.splashTitle.animate()
            .alpha(1f)
            .translationY(0f)
            .setStartDelay(180L)
            .setDuration(420L)
            .setInterpolator(DecelerateInterpolator())
            .start()

        binding.splashTagline.animate()
            .alpha(1f)
            .setStartDelay(420L)
            .setDuration(380L)
            .setInterpolator(DecelerateInterpolator())
            .start()

        binding.splashDividerTop.animate()
            .alpha(0.45f)
            .setStartDelay(420L)
            .setDuration(380L)
            .start()

        binding.splashDividerBottom.animate()
            .alpha(0.45f)
            .setStartDelay(460L)
            .setDuration(380L)
            .start()

        binding.splashSprigStart.animate()
            .alpha(0.45f)
            .setStartDelay(520L)
            .setDuration(420L)
            .start()

        binding.splashSprigEnd.animate()
            .alpha(0.45f)
            .setStartDelay(560L)
            .setDuration(420L)
            .start()

        binding.splashCaption.animate()
            .alpha(1f)
            .setStartDelay(640L)
            .setDuration(360L)
            .start()
    }

    fun startIdleLoops(binding: FragmentSplashBinding, reducedMotion: Boolean) {
        if (reducedMotion) {
            return
        }
        val medallion = binding.splashMedallion
        val breathe = ValueAnimator.ofFloat(1.0f, 1.035f)
        breathe.duration = 1200L
        breathe.repeatCount = ValueAnimator.INFINITE
        breathe.repeatMode = ValueAnimator.REVERSE
        breathe.addUpdateListener { animator ->
            try {
                if (!medallion.isAttachedToWindow) return@addUpdateListener
                val scale = animator.animatedValue as Float
                medallion.scaleX = scale
                medallion.scaleY = scale
            } catch (e: Exception) {
            }
        }
        loops.add(breathe)
        breathe.start()

        val shimmer = ValueAnimator.ofFloat(0.45f, 0.9f)
        shimmer.duration = 900L
        shimmer.repeatCount = ValueAnimator.INFINITE
        shimmer.repeatMode = ValueAnimator.REVERSE
        shimmer.addUpdateListener { animator ->
            try {
                if (!binding.splashDividerTop.isAttachedToWindow) return@addUpdateListener
                val value = animator.animatedValue as Float
                binding.splashDividerTop.alpha = value
                binding.splashDividerBottom.alpha = value
            } catch (e: Exception) {
            }
        }
        loops.add(shimmer)
        shimmer.start()

        val sway = ValueAnimator.ofFloat(-4f, 4f)
        sway.duration = 1800L
        sway.repeatCount = ValueAnimator.INFINITE
        sway.repeatMode = ValueAnimator.REVERSE
        sway.addUpdateListener { animator ->
            try {
                if (!binding.splashSprigStart.isAttachedToWindow) return@addUpdateListener
                val value = animator.animatedValue as Float
                binding.splashSprigStart.rotation = value
                binding.splashSprigEnd.rotation = -value
            } catch (e: Exception) {
            }
        }
        loops.add(sway)
        sway.start()
    }

    fun pulseCaption(view: View) {
        view.animate().cancel()
        view.animate().alpha(0.55f).setDuration(220L)
            .withEndAction {
                try {
                    if (!view.isAttachedToWindow) return@withEndAction
                    view.animate().alpha(1f).setDuration(220L).start()
                } catch (e: Exception) {
                }
            }
            .start()
    }

    fun cancelAll(binding: FragmentSplashBinding) {
        loops.forEach { it.cancel() }
        loops.clear()
        binding.splashMedallion.animate().cancel()
        binding.splashTitle.animate().cancel()
        binding.splashTagline.animate().cancel()
        binding.splashDividerTop.animate().cancel()
        binding.splashDividerBottom.animate().cancel()
        binding.splashSprigStart.animate().cancel()
        binding.splashSprigEnd.animate().cancel()
        binding.splashCaption.animate().cancel()
    }

    private companion object {
        const val TITLE_OFFSET = 24f
    }
}
