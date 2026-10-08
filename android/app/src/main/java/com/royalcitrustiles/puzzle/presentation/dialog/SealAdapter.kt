package com.royalcitrustiles.puzzle.presentation.dialog

import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.RecyclerView
import com.royalcitrustiles.puzzle.R
import com.royalcitrustiles.puzzle.databinding.ItemSealBinding
import com.royalcitrustiles.puzzle.domain.model.RoyalSeal

class SealAdapter : RecyclerView.Adapter<SealAdapter.SealViewHolder>() {

    private val items = ArrayList<RoyalSeal>()
    private var unlocked = emptySet<Int>()

    fun submit(seals: List<RoyalSeal>, unlockedIndices: Set<Int>) {
        items.clear()
        items.addAll(seals)
        unlocked = unlockedIndices
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SealViewHolder {
        val binding = ItemSealBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SealViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SealViewHolder, position: Int) {
        val seal = items[position]
        holder.bind(seal, unlocked.contains(seal.wingIndex))
    }

    override fun getItemCount(): Int = items.size

    class SealViewHolder(private val binding: ItemSealBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(seal: RoyalSeal, isUnlocked: Boolean) {
            val context = binding.root.context
            binding.sealItemSprite.setImageResource(R.drawable.sprite_royal_seal)
            binding.root.contentDescription = context.getString(R.string.desc_seal_item, seal.title)
            if (isUnlocked) {
                binding.sealItemSprite.colorFilter = null
                binding.sealItemSprite.alpha = 1f
                binding.sealItemLabel.text = seal.wingName
                binding.sealItemLabel.setTextColor(
                    ContextCompat.getColor(context, R.color.text_bark)
                )
                ViewCompat.setStateDescription(
                    binding.root,
                    context.getString(R.string.state_restored)
                )
            } else {
                val matrix = ColorMatrix()
                matrix.setSaturation(0f)
                binding.sealItemSprite.colorFilter = ColorMatrixColorFilter(matrix)
                binding.sealItemSprite.alpha = 0.35f
                binding.sealItemLabel.setText(R.string.wing_state_locked)
                binding.sealItemLabel.setTextColor(
                    ContextCompat.getColor(context, R.color.text_muted)
                )
                ViewCompat.setStateDescription(
                    binding.root,
                    context.getString(R.string.state_locked)
                )
            }
        }
    }
}
