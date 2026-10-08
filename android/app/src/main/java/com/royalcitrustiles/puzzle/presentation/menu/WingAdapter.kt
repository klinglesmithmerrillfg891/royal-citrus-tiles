package com.royalcitrustiles.puzzle.presentation.menu

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.RecyclerView
import com.royalcitrustiles.puzzle.R
import com.royalcitrustiles.puzzle.core.ui.TileArt
import com.royalcitrustiles.puzzle.databinding.ItemWingBinding

class WingAdapter(private val onWingClick: (Int) -> Unit) :
    RecyclerView.Adapter<WingAdapter.WingViewHolder>() {

    private val items = ArrayList<MenuUiState.WingCard>()

    fun submit(cards: List<MenuUiState.WingCard>) {
        items.clear()
        items.addAll(cards)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WingViewHolder {
        val binding = ItemWingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return WingViewHolder(binding, onWingClick)
    }

    override fun onBindViewHolder(holder: WingViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    class WingViewHolder(
        private val binding: ItemWingBinding,
        private val onWingClick: (Int) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(card: MenuUiState.WingCard) {
            val context = binding.root.context
            binding.wingName.text = card.wing.name
            binding.wingSprite.setImageResource(TileArt.spriteFor(card.wing.signatureKind))
            binding.root.contentDescription =
                context.getString(R.string.desc_wing_card, card.wing.name)

            when (card.state) {
                MenuUiState.State.RESTORED -> {
                    binding.wingState.setText(R.string.wing_state_restored)
                    binding.wingState.setTextColor(
                        ContextCompat.getColor(context, R.color.royal_gold)
                    )
                    binding.wingCard.strokeColor =
                        ContextCompat.getColor(context, R.color.hairline_gold)
                    binding.wingCard.strokeWidth =
                        context.resources.getDimensionPixelSize(R.dimen.stroke_hairline)
                    binding.root.alpha = 1f
                    binding.root.isEnabled = true
                    binding.wingSprite.imageTintList = null
                    ViewCompat.setStateDescription(
                        binding.root,
                        context.getString(R.string.state_restored)
                    )
                }

                MenuUiState.State.CURRENT -> {
                    binding.wingState.setText(R.string.wing_state_next)
                    binding.wingState.setTextColor(
                        ContextCompat.getColor(context, R.color.leaf_green)
                    )
                    binding.wingCard.strokeColor =
                        ContextCompat.getColor(context, R.color.leaf_green)
                    binding.wingCard.strokeWidth =
                        context.resources.getDimensionPixelSize(R.dimen.stroke_frame)
                    binding.root.alpha = 1f
                    binding.root.isEnabled = true
                    binding.wingSprite.imageTintList = null
                    ViewCompat.setStateDescription(
                        binding.root,
                        context.getString(R.string.state_next_order)
                    )
                }

                MenuUiState.State.LOCKED -> {
                    binding.wingState.setText(R.string.wing_state_locked)
                    binding.wingState.setTextColor(
                        ContextCompat.getColor(context, R.color.text_muted)
                    )
                    binding.wingCard.strokeColor =
                        ContextCompat.getColor(context, R.color.hairline_bark)
                    binding.wingCard.strokeWidth =
                        context.resources.getDimensionPixelSize(R.dimen.stroke_hairline)
                    binding.root.alpha = 0.45f
                    binding.root.isEnabled = false
                    binding.wingSprite.imageTintList = ColorStateList.valueOf(
                        ContextCompat.getColor(context, R.color.text_muted)
                    )
                    ViewCompat.setStateDescription(
                        binding.root,
                        context.getString(R.string.state_locked)
                    )
                }
            }

            binding.wingCard.isClickable = card.state != MenuUiState.State.LOCKED
            binding.wingCard.setOnClickListener {
                if (card.state != MenuUiState.State.LOCKED) {
                    onWingClick(card.wing.index)
                }
            }
        }
    }
}
