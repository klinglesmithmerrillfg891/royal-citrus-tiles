package com.royalcitrustiles.puzzle.presentation.game

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.royalcitrustiles.puzzle.R
import com.royalcitrustiles.puzzle.core.ui.CitrusTileView
import com.royalcitrustiles.puzzle.core.ui.TileArt
import com.royalcitrustiles.puzzle.databinding.ItemTileBinding
import com.royalcitrustiles.puzzle.domain.model.Board
import com.royalcitrustiles.puzzle.domain.model.BoardPosition

class BoardAdapter(private val onTileTapped: (BoardPosition) -> Unit) :
    RecyclerView.Adapter<BoardAdapter.TileViewHolder>() {

    private var board: Board? = null
    private var selected: BoardPosition? = null
    private var tileSizePx = 0
    private var reducedMotion = false
    private var inputEnabled = true

    fun setTileSize(sizePx: Int) {
        tileSizePx = sizePx
    }

    fun submit(
        updated: Board,
        selectedPosition: BoardPosition?,
        reducedMotionEnabled: Boolean,
        acceptsInput: Boolean
    ) {
        board = updated
        selected = selectedPosition
        reducedMotion = reducedMotionEnabled
        inputEnabled = acceptsInput
        notifyDataSetChanged()
    }

    fun tileViewAt(recycler: RecyclerView, position: BoardPosition): CitrusTileView? {
        val current = board ?: return null
        val index = current.indexOf(position.row, position.col)
        val holder = recycler.findViewHolderForAdapterPosition(index)
        if (holder is TileViewHolder) {
            return holder.tileView()
        }
        return null
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TileViewHolder {
        val binding = ItemTileBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TileViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TileViewHolder, position: Int) {
        val current = board ?: return
        if (position >= current.tiles.size) {
            return
        }
        val cell = current.positionOf(position)
        val tile = current.tileAt(cell)
        val context = holder.itemView.context
        val kindLabel = context.getString(TileArt.labelFor(tile.kind))
        val description = context.getString(
            R.string.desc_tile,
            kindLabel,
            cell.row + 1,
            cell.col + 1
        )
        holder.applySize(tileSizePx)
        holder.tileView().bind(tile.kind, tile.isCrown, description, reducedMotion)
        holder.tileView().showSelected(selected == cell)
        holder.itemView.isEnabled = inputEnabled
        holder.itemView.setOnClickListener {
            if (inputEnabled) {
                onTileTapped(cell)
            }
        }
    }

    override fun onViewRecycled(holder: TileViewHolder) {
        holder.tileView().cancelAnimations()
        super.onViewRecycled(holder)
    }

    override fun getItemCount(): Int = board?.tiles?.size ?: 0

    class TileViewHolder(private val binding: ItemTileBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun tileView(): CitrusTileView = binding.tileRoot

        fun applySize(sizePx: Int) {
            if (sizePx <= 0) {
                return
            }
            val params = binding.root.layoutParams
            if (params != null && params.height != sizePx) {
                params.height = sizePx
                binding.root.layoutParams = params
            }
        }
    }
}
