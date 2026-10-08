package com.royalcitrustiles.puzzle.presentation.splash

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.royalcitrustiles.puzzle.R
import com.royalcitrustiles.puzzle.core.di.ServiceLocator
import com.royalcitrustiles.puzzle.core.di.ViewModelFactory
import com.royalcitrustiles.puzzle.core.navigation.Navigator
import com.royalcitrustiles.puzzle.databinding.FragmentSplashBinding
import kotlinx.coroutines.launch

class SplashFragment : Fragment() {

    private var binding: FragmentSplashBinding? = null

    private val viewModel: SplashViewModel by viewModels { ViewModelFactory() }

    private val animator = SplashAnimator()

    private var lastStage: SplashUiState.Stage? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val created = FragmentSplashBinding.inflate(inflater, container, false)
        binding = created
        return created.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val bound = binding ?: return
        val reducedMotion = if (ServiceLocator.isReady) {
            ServiceLocator.progressRepository.loadSettings().reducedMotion
        } else {
            false
        }
        bound.splashProgress.isIndeterminate = true
        bound.splashTitle.text = getString(R.string.splash_title)
        bound.splashTagline.text = getString(R.string.splash_tagline)
        animator.prepare(bound)
        animator.playEntrance(bound)
        animator.startIdleLoops(bound, reducedMotion)
        observeState()
        viewModel.startCountdown()
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state -> render(state) }
            }
        }
    }

    private fun render(state: SplashUiState) {
        val bound = binding ?: return
        if (lastStage != state.stage) {
            lastStage = state.stage
            bound.splashCaption.text = getString(captionFor(state.stage))
            animator.pulseCaption(bound.splashCaption)
        }
        if (state.navigateToMenu) {
            viewModel.onNavigationHandled()
            openMenu()
        }
    }

    private fun captionFor(stage: SplashUiState.Stage): Int = when (stage) {
        SplashUiState.Stage.OPENING -> R.string.splash_stage_opening
        SplashUiState.Stage.WARMING -> R.string.splash_stage_warming
        SplashUiState.Stage.POLISHING -> R.string.splash_stage_polishing
        SplashUiState.Stage.READY -> R.string.splash_stage_ready
    }

    private fun openMenu() {
        if (!isAdded || binding == null) {
            return
        }
        Navigator.showMenu(parentFragmentManager)
    }

    override fun onDestroyView() {
        val bound = binding
        if (bound != null) {
            animator.cancelAll(bound)
        }
        lastStage = null
        binding = null
        super.onDestroyView()
    }
}
