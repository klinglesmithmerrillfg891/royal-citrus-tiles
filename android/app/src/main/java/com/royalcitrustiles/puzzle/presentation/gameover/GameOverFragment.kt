package com.royalcitrustiles.puzzle.presentation.gameover

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.royalcitrustiles.puzzle.R
import com.royalcitrustiles.puzzle.core.config.GameConfig
import com.royalcitrustiles.puzzle.core.di.ViewModelFactory
import com.royalcitrustiles.puzzle.core.navigation.Navigator
import com.royalcitrustiles.puzzle.core.ui.StatCardView
import com.royalcitrustiles.puzzle.databinding.FragmentGameOverBinding
import com.royalcitrustiles.puzzle.domain.model.RoundResult
import kotlinx.coroutines.launch

class GameOverFragment : Fragment() {

    private var binding: FragmentGameOverBinding? = null

    private val viewModel: GameOverViewModel by viewModels { ViewModelFactory() }

    private var nextWingIndex = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val created = FragmentGameOverBinding.inflate(inflater, container, false)
        binding = created
        return created.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val bound = binding ?: return

        bound.resultPlayAgain.setOnClickListener { playAgain() }
        bound.resultMenuButton.setOnClickListener { goToMenu() }

        bound.resultStatScore.setHiddenWhenZero(true)
        bound.resultStatCombo.setHiddenWhenZero(true)
        bound.resultStatMoves.setHiddenWhenZero(true)
        bound.resultStatSeals.setHiddenWhenZero(true)

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    goToMenu()
                }
            }
        )

        observeState()
        viewModel.load(readResult())
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state -> render(state) }
            }
        }
    }

    private fun render(state: GameOverUiState) {
        val bound = binding ?: return
        val result = state.result ?: return
        val context = requireContext()
        nextWingIndex = state.nextWingIndex

        bound.resultHeadline.setText(
            if (result.success) R.string.result_success_headline else R.string.result_failure_headline
        )
        bound.resultSubline.text = if (result.success) {
            getString(R.string.result_success_subline, result.wingName)
        } else {
            getString(R.string.result_failure_subline)
        }

        bound.resultSealBadge.bind(state.sealTitle, result.success, state.reducedMotion)
        if (!state.reducedMotion) {
            bound.resultSealBadge.playUnlock()
        }

        val scoreVisible = bound.resultStatScore.bindCount(
            getString(R.string.game_score_label),
            result.score,
            ContextCompat.getColor(context, R.color.citrus_orange)
        )
        val comboVisible = bound.resultStatCombo.bindCount(
            getString(R.string.result_best_combo_label),
            result.bestCombo,
            ContextCompat.getColor(context, R.color.leaf_green)
        )
        val movesVisible = bound.resultStatMoves.bindCount(
            getString(R.string.result_moves_left_label),
            result.movesLeft,
            ContextCompat.getColor(context, R.color.citrus_orange_dark)
        )
        val sealsVisible = bound.resultStatSeals.bindFraction(
            getString(R.string.game_seals_label),
            result.sealCount,
            GameConfig.WING_COUNT,
            ContextCompat.getColor(context, R.color.royal_gold)
        )

        bound.resultStatRowTop.visibility =
            if (scoreVisible && comboVisible) View.VISIBLE else View.GONE
        bound.resultStatRowBottom.visibility =
            if (movesVisible && sealsVisible) View.VISIBLE else View.GONE
        StatCardView.applyRowVisibility(
            bound.resultCard,
            listOf(
                bound.resultStatScore,
                bound.resultStatCombo,
                bound.resultStatMoves,
                bound.resultStatSeals
            )
        )
    }

    private fun readResult(): RoundResult {
        val args = arguments
        if (args == null) {
            return RoundResult(false, 0, "", 0, 1, 0, 0)
        }
        return RoundResult(
            success = args.getBoolean(ARG_SUCCESS, false),
            wingIndex = args.getInt(ARG_WING_INDEX, 0),
            wingName = args.getString(ARG_WING_NAME, ""),
            score = args.getInt(ARG_SCORE, 0),
            bestCombo = args.getInt(ARG_BEST_COMBO, 1),
            movesLeft = args.getInt(ARG_MOVES_LEFT, 0),
            sealCount = args.getInt(ARG_SEAL_COUNT, 0)
        )
    }

    private fun playAgain() {
        if (!isAdded) {
            return
        }
        Navigator.replay(parentFragmentManager, nextWingIndex)
    }

    private fun goToMenu() {
        if (!isAdded) {
            return
        }
        Navigator.backToMenu(parentFragmentManager)
    }

    override fun onDestroyView() {
        val bound = binding
        if (bound != null) {
            bound.resultSealBadge.cancelAnimations()
        }
        binding = null
        super.onDestroyView()
    }

    companion object {

        private const val ARG_SUCCESS = "result_success"
        private const val ARG_WING_INDEX = "result_wing_index"
        private const val ARG_WING_NAME = "result_wing_name"
        private const val ARG_SCORE = "result_score"
        private const val ARG_BEST_COMBO = "result_best_combo"
        private const val ARG_MOVES_LEFT = "result_moves_left"
        private const val ARG_SEAL_COUNT = "result_seal_count"

        fun newInstance(result: RoundResult): GameOverFragment {
            val fragment = GameOverFragment()
            val args = Bundle()
            args.putBoolean(ARG_SUCCESS, result.success)
            args.putInt(ARG_WING_INDEX, result.wingIndex)
            args.putString(ARG_WING_NAME, result.wingName)
            args.putInt(ARG_SCORE, result.score)
            args.putInt(ARG_BEST_COMBO, result.bestCombo)
            args.putInt(ARG_MOVES_LEFT, result.movesLeft)
            args.putInt(ARG_SEAL_COUNT, result.sealCount)
            fragment.arguments = args
            return fragment
        }
    }
}
