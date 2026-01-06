package com.example.projetintegration.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.projetintegration.R
import com.example.projetintegration.data.models.Plat
import com.example.projetintegration.databinding.ItemPlatModernBinding
import com.example.projetintegration.ui.viewmodel.FavoriViewModel
import kotlinx.coroutines.launch

class PlatsModernAdapter(
    private val onPlatClick: (Plat) -> Unit,
    private val favoriViewModel: FavoriViewModel,
    private val lifecycleOwner: LifecycleOwner
) : ListAdapter<Plat, PlatsModernAdapter.PlatViewHolder>(PlatDiffCallback()) {
    
    override fun submitList(list: List<Plat>?) {
        println("PlatsModernAdapter: submitList called with ${list?.size ?: 0} items")
        list?.forEach { plat ->
            println("PlatsModernAdapter: Submitting plat - ${plat.nom}")
        }
        super.submitList(list) {
            println("PlatsModernAdapter: submitList callback - list submitted")
            // Force notify adapter
            notifyDataSetChanged()
        }
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlatViewHolder {
        println("PlatsModernAdapter: onCreateViewHolder called")
        val binding = ItemPlatModernBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PlatViewHolder(binding)
    }
    
    override fun onBindViewHolder(holder: PlatViewHolder, position: Int) {
        println("PlatsModernAdapter: onBindViewHolder called for position $position")
        holder.bind(getItem(position))
    }
    
    override fun getItemCount(): Int {
        val count = super.getItemCount()
        println("PlatsModernAdapter: getItemCount = $count")
        return count
    }
    
    inner class PlatViewHolder(
        private val binding: ItemPlatModernBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        
        fun bind(plat: Plat) {
            println("PlatsModernAdapter: Binding plat - ${plat.nom}")
            
            // Informations de base
            binding.tvNom.text = plat.nom
            binding.tvDescription.text = plat.description ?: "Délicieux plat fait maison"
            binding.tvCalories.text = "${plat.calories} cal"
            binding.tvTemps.text = "${plat.tempsPreparation} min"
            
            // Catégorie avec emoji
            binding.tvCategory.text = getCategorieWithEmoji(plat.categorie)
            
            // Ingrédients (premiers 3)
            val ingredientsText = if (plat.ingredients.size > 3) {
                "${plat.ingredients.take(3).joinToString(", ")}..."
            } else {
                plat.ingredients.joinToString(", ")
            }
            binding.tvIngredients.text = ingredientsText
            
            // Rating simulé basé sur les calories (plus c'est équilibré, mieux c'est)
            val rating = calculateRating(plat.calories)
            binding.tvRating.text = rating.toString()
            
            // Image selon la catégorie
            val imageRes = getCategorieImage(plat.categorie)
            binding.ivPlatImage.setImageResource(imageRes)
            
            // Click listeners
            binding.root.setOnClickListener {
                onPlatClick(plat)
            }
            
            // 💖 GESTION DES FAVORIS
            setupFavoriteButton(plat)
            
            // btnAddToMeal removed
        }
        
        private fun setupFavoriteButton(plat: Plat) {
            // Vérifier le statut favori et mettre à jour l'icône
            lifecycleOwner.lifecycleScope.launch {
                try {
                    val isFavorite = favoriViewModel.isPlatFavorite(plat.id.toLong())
                    updateFavoriteIcon(isFavorite)
                } catch (e: Exception) {
                    android.util.Log.e("PlatsModernAdapter", "Erreur vérification statut favori plat", e)
                    updateFavoriteIcon(false)
                }
            }
            
            // Observer les changements de favoris pour ce plat
            favoriViewModel.favorisPlats.observe(lifecycleOwner) { favoris ->
                val isFavorite = favoris.any { it.platId == plat.id.toLong() }
                updateFavoriteIcon(isFavorite)
            }
            
            // Click listener pour toggle favori
            binding.btnFavorite.setOnClickListener {
                android.util.Log.d("PlatsModernAdapter", "💖 Toggle favori plat: ${plat.nom}")
                favoriViewModel.toggleFavoriPlat(plat.id.toLong())
                animateFavoriteButton()
            }
        }
        
        private fun updateFavoriteIcon(isFavorite: Boolean) {
            binding.btnFavorite.setImageResource(
                if (isFavorite) R.drawable.ic_heart_filled else R.drawable.ic_heart_outline
            )
        }
        
        private fun getCategorieWithEmoji(categorie: String): String {
            return when (categorie) {
                "petit-dejeuner" -> "🥐 Petit-déj"
                "dejeuner" -> "🥗 Déjeuner"
                "diner" -> "🍲 Dîner"
                "collation" -> "🍎 Collation"
                else -> "🍽️ Plat"
            }
        }
        
        private fun getCategorieImage(categorie: String): Int {
            return when (categorie) {
                "petit-dejeuner" -> R.drawable.omelette
                "dejeuner" -> R.drawable.plat_meat
                "diner" -> R.drawable.tacos
                "collation" -> R.drawable.applesplash
                else -> R.drawable.vegetables
            }
        }
        
        private fun calculateRating(calories: Int): Double {
            // Calcul simple du rating basé sur les calories
            // 200-300 cal = excellent (4.8-5.0)
            // 150-200 ou 300-400 cal = très bon (4.5-4.7)
            // Autres = bon (4.0-4.4)
            return when {
                calories in 200..300 -> (48 + (Math.random() * 3).toInt()) / 10.0
                calories in 150..200 || calories in 300..400 -> (45 + (Math.random() * 3).toInt()) / 10.0
                else -> (40 + (Math.random() * 5).toInt()) / 10.0
            }
        }
        
        private fun animateFavoriteButton() {
            binding.btnFavorite.animate()
                .scaleX(1.2f)
                .scaleY(1.2f)
                .setDuration(150)
                .withEndAction {
                    binding.btnFavorite.animate()
                        .scaleX(1.0f)
                        .scaleY(1.0f)
                        .setDuration(150)
                        .start()
                }
                .start()
        }
        
        // animateAddButton removed
    }
    
    class PlatDiffCallback : DiffUtil.ItemCallback<Plat>() {
        override fun areItemsTheSame(oldItem: Plat, newItem: Plat): Boolean {
            return oldItem.id == newItem.id
        }
        
        override fun areContentsTheSame(oldItem: Plat, newItem: Plat): Boolean {
            return oldItem == newItem
        }
    }
}