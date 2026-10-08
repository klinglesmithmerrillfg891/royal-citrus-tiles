package com.royalcitrustiles.puzzle.presentation.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.GridLayoutManager
import com.royalcitrustiles.puzzle.R
import com.royalcitrustiles.puzzle.core.di.ServiceLocator
import com.royalcitrustiles.puzzle.databinding.DialogSealCollectionBinding

class SealCollectionDialog : DialogFragment() {

    private var binding: DialogSealCollectionBinding? = null

    private var adapter: SealAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_FRAME, R.style.Theme_RoyalCitrusTiles_Dialog)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val created = DialogSealCollectionBinding.inflate(inflater, container, false)
        binding = created
        return created.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val bound = binding ?: return
        val created = SealAdapter()
        adapter = created
        bound.sealsRecycler.layoutManager = GridLayoutManager(requireContext(), GRID_SPAN)
        bound.sealsRecycler.adapter = created
        bound.sealsCloseButton.setOnClickListener { dismissAllowingStateLoss() }

        val seals = ServiceLocator.boardRepository.seals()
        val unlocked = ServiceLocator.progressRepository.unlockedSealIndices()
        if (unlocked.isEmpty()) {
            bound.sealsRecycler.visibility = View.GONE
            bound.sealsEmptyBlock.visibility = View.VISIBLE
        } else {
            bound.sealsRecycler.visibility = View.VISIBLE
            bound.sealsEmptyBlock.visibility = View.GONE
            created.submit(seals, unlocked)
        }
    }

    override fun onDestroyView() {
        val bound = binding
        if (bound != null) {
            bound.sealsRecycler.adapter = null
        }
        adapter = null
        binding = null
        super.onDestroyView()
    }

    companion object {
        const val TAG = "SealCollectionDialog"
        private const val GRID_SPAN = 3
    }
}
