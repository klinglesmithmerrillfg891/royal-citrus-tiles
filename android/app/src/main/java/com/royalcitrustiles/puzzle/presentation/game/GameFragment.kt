package com.royalcitrustiles.puzzle.presentation.game

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.GridLayoutManager
import com.royalcitrustiles.puzzle.R
import com.royalcitrustiles.puzzle.core.config.GameConfig
import com.royalcitrustiles.puzzle.core.di.ViewModelFactory
import com.royalcitrustiles.puzzle.core.navigation.Navigator
import com.royalcitrustiles.puzzle.core.ui.StatCardView
import com.royalcitrustiles.puzzle.databinding.FragmentGameBinding
import com.royalcitrustiles.puzzle.domain.model.RoundResult
import kotlinx.coroutines.launch

class GameFragment : Fragment() {

    private var binding: FragmentGameBinding? = null

    private val viewModel: GameViewModel by viewModels { ViewModelFactory() }

    private var boardAdapter: BoardAdapter? = null

    private var navigated = false

    private var lastAnnouncement: Int? = null

    private val wingIndex: Int
        get() = arguments?.getInt(ARG_WING_INDEX, 0) ?: 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val created = FragmentGameBinding.inflate(inflater, container, false)
        binding = created
        return created.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val bound = binding ?: return

        val tileSize = computeTileSize()
        applyBoardGeometry(bound, tileSize)

        val adapter = BoardAdapter { position -> viewModel.onTileTapped(position) }
        adapter.setTileSize(tileSize)
        boardAdapter = adapter
        bound.gameBoardRecycler.layoutManager =
            GridLayoutManager(requireContext(), GameConfig.BOARD_COLS)
        bound.gameBoardRecycler.itemAnimator = null
        bound.gameBoardRecycler.adapter = adapter

        bound.gameBackButton.setOnClickListener { leaveGame() }
        bound.gameCrownButton.setOnClickListener { viewModel.onUseCrown() }
        bound.gameDeliverButton.setOnClickListener { viewModel.onDeliver() }
        bound.gameErrorRetry.setOnClickListener { viewModel.retry() }

        bound.gameStatScore.setHiddenWhenZero(true)
        bound.gameStatCombo.setHiddenWhenZero(true)
        bound.gameStatCrowns.setHiddenWhenZero(true)

        observeState()
        viewModel.start(wingIndex)
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state -> render(state) }
            }
        }
    }

    private fun render(state: GameUiState) {
        val bound = binding ?: return
        when (state) {
            is GameUiState.Loading -> {
                bound.gameErrorBlock.visibility = View.GONE
                bound.gameBoardFrame.visibility = View.VISIBLE
            }

            is GameUiState.Error -> {
                bound.gameErrorBlock.visibility = View.VISIBLE
                bound.gameBoardFrame.visibility = View.GONE
            }

            is GameUiState.Active -> {
                bound.gameErrorBlock.visibility = View.GONE
                bound.gameBoardFrame.visibility = View.VISIBLE
                renderActive(state)
            }

            is GameUiState.Finished -> openResult(state.result)
        }
    }

    private fun renderActive(state: GameUiState.Active) {
        val bound = binding ?: return
        val context = requireContext()

        bound.gameWingName.text = state.wingName
        bound.gameWingPosition.text = getString(
            R.string.game_wing_position,
            state.wingIndex + 1,
            state.wingCount
        )
        bound.gameMovesValue.text = state.movesLeft.toString()
        bound.gameMovesValue.setTextColor(
            ContextCompat.getColor(
                context,
                if (state.movesLeft <= 3) R.color.seville_red else R.color.citrus_orange
            )
        )
        ViewCompat.setStateDescription(
            bound.gameMovesPill,
            getString(R.string.state_moves_left, state.movesLeft)
        )

        bound.gameStatScore.bindCount(
            getString(R.string.game_score_label),
            state.score,
            ContextCompat.getColor(context, R.color.citrus_orange)
        )
        bound.gameStatCombo.bindCount(
            getString(R.string.game_combo_label),
            state.bestCombo,
            ContextCompat.getColor(context, R.color.leaf_green)
        )
        bound.gameStatCrowns.bindCount(
            getString(R.string.game_crowns_label),
            state.crowns,
            ContextCompat.getColor(context, R.color.royal_gold)
        )
        StatCardView.applyRowVisibility(
            bound.gameStatStrip,
            listOf(bound.gameStatScore, bound.gameStatCombo, bound.gameStatCrowns)
        )

        bound.gameOrderProgress.bind(state.order.requirements)

        val acceptsInput = state.phase == GameUiState.Phase.IDLE
        bound.gameCrownButton.isEnabled = acceptsInput && state.crowns > 0
        bound.gameCrownButton.alpha = if (bound.gameCrownButton.isEnabled) 1f else 0.45f
        ViewCompat.setStateDescription(
            bound.gameCrownButton,
            if (state.crowns > 0) {
                getString(R.string.state_crowns_ready, state.crowns)
            } else {
                getString(R.string.state_no_crowns)
            }
        )
        bound.gameDeliverButton.isEnabled = state.canDeliver
        bound.gameDeliverButton.alpha = if (state.canDeliver) 1f else 0.45f

        boardAdapter?.submit(state.board, state.selected, state.reducedMotion, acceptsInput)

        playRejection(state)
        announce(state)
    }

    private fun playRejection(state: GameUiState.Active) {
        val bound = binding ?: return
        val first = state.rejectedFirst
        val second = state.rejectedSecond
        if (first == null || second == null) {
            return
        }
        val adapter = boardAdapter ?: return
        val horizontal = first.row == second.row
        adapter.tileViewAt(bound.gameBoardRecycler, first)?.playReject(horizontal)
        adapter.tileViewAt(bound.gameBoardRecycler, second)?.playReject(horizontal)
        viewModel.onRejectionConsumed()
    }

    private fun announce(state: GameUiState.Active) {
        val bound = binding ?: return
        val message = state.announcementRes
        if (message == null) {
            lastAnnouncement = null
            return
        }
        if (lastAnnouncement == message) {
            return
        }
        lastAnnouncement = message
        bound.gameOrderProgress.announceForAccessibility(getString(message))
    }

    private fun computeTileSize(): Int {
        val metrics = resources.displayMetrics
        val density = metrics.density
        val framePx = (GameConfig.BOARD_FRAME_DP * density).toInt()
        val sideMargin = (GameConfig.BOARD_SIDE_MARGIN_DP * density).toInt()
        val maxBoardWidth = (GameConfig.BOARD_MAX_WIDTH_DP * density).toInt()
        val widthBudget = minOf(metrics.widthPixels - sideMargin, maxBoardWidth)
        val headerPx = resources.getDimensionPixelSize(R.dimen.header_height)
        val panelPx = resources.getDimensionPixelSize(R.dimen.panel_height)
        val heightBudget = metrics.heightPixels - headerPx - panelPx - sideMargin
        val fromWidth = (widthBudget - 2 * framePx) / GameConfig.BOARD_COLS
        val fromHeight = (heightBudget - 2 * framePx) / GameConfig.BOARD_ROWS
        val tile = minOf(fromWidth, fromHeight)
        val floor = (MIN_TILE_DP * density).toInt()
        return if (tile < floor) floor else tile
    }

    private fun applyBoardGeometry(bound: FragmentGameBinding, tileSize: Int) {
        val density = resources.displayMetrics.density
        val framePx = (GameConfig.BOARD_FRAME_DP * density).toInt()
        val boardWidth = tileSize * GameConfig.BOARD_COLS + 2 * framePx
        val boardHeight = tileSize * GameConfig.BOARD_ROWS + 2 * framePx

        val frameParams = bound.gameBoardFrame.layoutParams
        frameParams.width = boardWidth
        frameParams.height = boardHeight
        bound.gameBoardFrame.layoutParams = frameParams

        val recyclerParams = bound.gameBoardRecycler.layoutParams
        recyclerParams.width = tileSize * GameConfig.BOARD_COLS
        recyclerParams.height = tileSize * GameConfig.BOARD_ROWS
        bound.gameBoardRecycler.layoutParams = recyclerParams
    }

    private fun leaveGame() {
        if (!isAdded) {
            return
        }
        parentFragmentManager.popBackStack()
    }

    private fun openResult(result: RoundResult) {
        if (navigated || !isAdded || binding == null) {
            return
        }
        navigated = true
        Navigator.showResult(parentFragmentManager, result)
    }

    override fun onDestroyView() {
        val bound = binding
        if (bound != null) {
            bound.gameOrderProgress.cancelAnimations()
            bound.gameBoardRecycler.adapter = null
        }
        boardAdapter = null
        lastAnnouncement = null
        binding = null
        super.onDestroyView()
    }

    companion object {

        private const val ARG_WING_INDEX = "wing_index"

        private const val MIN_TILE_DP = 32

        fun newInstance(wingIndex: Int): GameFragment {
            val fragment = GameFragment()
            val args = Bundle()
            args.putInt(ARG_WING_INDEX, wingIndex)
            fragment.arguments = args
            return fragment
        }
    }
}
