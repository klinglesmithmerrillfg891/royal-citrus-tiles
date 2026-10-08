package com.royalcitrustiles.puzzle.presentation.menu

import android.animation.ValueAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.royalcitrustiles.puzzle.R
import com.royalcitrustiles.puzzle.core.config.GameConfig
import com.royalcitrustiles.puzzle.core.di.ViewModelFactory
import com.royalcitrustiles.puzzle.core.navigation.Navigator
import com.royalcitrustiles.puzzle.core.ui.StatCardView
import com.royalcitrustiles.puzzle.databinding.FragmentMenuBinding
import com.royalcitrustiles.puzzle.presentation.dialog.SealCollectionDialog
import com.royalcitrustiles.puzzle.presentation.dialog.SettingsDialog
import kotlinx.coroutines.launch

class MenuFragment : Fragment() {

    private var binding: FragmentMenuBinding? = null

    private val viewModel: MenuViewModel by viewModels { ViewModelFactory() }

    private var wingAdapter: WingAdapter? = null

    private var ctaPulse: ValueAnimator? = null

    private var activeWingIndex = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val created = FragmentMenuBinding.inflate(inflater, container, false)
        binding = created
        return created.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val bound = binding ?: return

        val adapter = WingAdapter { index -> openGame(index) }
        wingAdapter = adapter
        bound.menuWingsRecycler.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        bound.menuWingsRecycler.adapter = adapter

        bound.menuPlayButton.setOnClickListener { openGame(activeWingIndex) }
        bound.menuSealsButton.setOnClickListener { showSeals() }
        bound.menuSettingsButton.setOnClickListener { showSettings() }

        bound.menuStatFavour.setHiddenWhenZero(true)
        bound.menuStatWings.setHiddenWhenZero(true)

        observeState()
        playEntrance()
    }

    override fun onResume() {
        super.onResume()
        viewModel.refresh()
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state -> render(state) }
            }
        }
    }

    private fun render(state: MenuUiState) {
        val bound = binding ?: return
        activeWingIndex = state.activeWingIndex
        wingAdapter?.submit(state.wingCards)

        val favourVisible = bound.menuStatFavour.bindCount(
            getString(R.string.menu_favour_label),
            state.royalFavour,
            ContextCompat.getColor(requireContext(), R.color.citrus_orange)
        )
        val wingsVisible = bound.menuStatWings.bindFraction(
            getString(R.string.menu_wings_label),
            state.sealCount,
            GameConfig.WING_COUNT,
            ContextCompat.getColor(requireContext(), R.color.royal_gold)
        )
        bound.menuFavourDivider.visibility =
            if (favourVisible && wingsVisible) View.VISIBLE else View.GONE
        StatCardView.applyRowVisibility(
            bound.menuFavourTile,
            listOf(bound.menuStatFavour, bound.menuStatWings)
        )

        val wing = state.activeWing
        if (wing != null) {
            bound.menuOrderProgress.bind(wing.order.requirements)
            bound.menuOrderMoves.text = getString(R.string.menu_moves_pill, wing.startingMoves)
            bound.menuOrderCard.visibility = View.VISIBLE
        } else {
            bound.menuOrderCard.visibility = View.GONE
        }

        if (state.bestStreak > 0) {
            bound.menuFooter.visibility = View.VISIBLE
            bound.menuFooter.text = getString(R.string.menu_streak, state.bestStreak)
        } else {
            bound.menuFooter.visibility = View.GONE
        }

        if (state.reducedMotion) {
            stopCtaPulse()
        } else {
            startCtaPulse()
        }
    }

    private fun playEntrance() {
        val bound = binding ?: return
        val steps = listOf(
            bound.menuFavourTile to 0L,
            bound.menuWingsRecycler to 70L,
            bound.menuOrderCard to 140L,
            bound.menuHint to 190L,
            bound.menuPlayWrapper to 240L
        )
        steps.forEach { step ->
            val target = step.first
            target.alpha = 0f
            target.translationY = ENTRY_OFFSET
            target.animate()
                .alpha(1f)
                .translationY(0f)
                .setStartDelay(step.second)
                .setDuration(320L)
                .setInterpolator(DecelerateInterpolator())
                .start()
        }
    }

    private fun startCtaPulse() {
        if (ctaPulse != null) {
            return
        }
        val bound = binding ?: return
        val card = bound.menuPlayWrapper
        val low = resources.getDimension(R.dimen.elevation_cta)
        val high = low + resources.getDimension(R.dimen.gutter_tiny)
        val animator = ValueAnimator.ofFloat(low, high)
        animator.duration = 1400L
        animator.repeatCount = ValueAnimator.INFINITE
        animator.repeatMode = ValueAnimator.REVERSE
        animator.addUpdateListener { value ->
            try {
                if (!card.isAttachedToWindow) return@addUpdateListener
                card.cardElevation = value.animatedValue as Float
            } catch (e: Exception) {
            }
        }
        ctaPulse = animator
        animator.start()
    }

    private fun stopCtaPulse() {
        ctaPulse?.cancel()
        ctaPulse = null
    }

    private fun openGame(wingIndex: Int) {
        if (!isAdded || binding == null) {
            return
        }
        Navigator.showGame(parentFragmentManager, wingIndex)
    }

    private fun showSeals() {
        if (!isAdded) {
            return
        }
        SealCollectionDialog().show(parentFragmentManager, SealCollectionDialog.TAG)
    }

    private fun showSettings() {
        if (!isAdded) {
            return
        }
        SettingsDialog().show(parentFragmentManager, SettingsDialog.TAG)
    }

    override fun onDestroyView() {
        stopCtaPulse()
        val bound = binding
        if (bound != null) {
            bound.menuFavourTile.animate().cancel()
            bound.menuWingsRecycler.animate().cancel()
            bound.menuOrderCard.animate().cancel()
            bound.menuHint.animate().cancel()
            bound.menuPlayWrapper.animate().cancel()
            bound.menuOrderProgress.cancelAnimations()
            bound.menuWingsRecycler.adapter = null
        }
        wingAdapter = null
        binding = null
        super.onDestroyView()
    }

    private companion object {
        const val ENTRY_OFFSET = 20f
    }
}
