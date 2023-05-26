package com.books.app.ui.fragment.adapters

import android.content.Context
import android.content.res.Resources
import android.graphics.Rect
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.Px
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.books.app.data.model.Book
import com.books.app.databinding.ItemCarouselBinding
import com.bumptech.glide.Glide
import kotlin.math.abs
import kotlin.math.roundToInt


class LinearHorizontalSpacingDecoration(@Px private val innerSpacing: Int) :
    RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)

        val itemPosition = parent.getChildAdapterPosition(view)
        val maxIndex = state.itemCount - 1

        outRect.left = if (itemPosition == 0) 0 else (DP * innerSpacing / 2).toInt()
        outRect.right = if (itemPosition == maxIndex) 0 else (DP * innerSpacing / 2).toInt()
    }

    companion object {
        private val DP = Resources.getSystem().displayMetrics.density
    }
}

class BoundsOffsetDecoration : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)

        val viewHolder = parent.findContainingViewHolder(view) ?: return
        val itemPosition = viewHolder.absoluteAdapterPosition

        val offset = (parent.width - view.width) / 4

        if (itemPosition == 0) outRect.left = offset
        else if (itemPosition == state.itemCount - 1) outRect.right = offset
    }
}

class CarouselAdapter(private val items: List<Book>) :
    RecyclerView.Adapter<CarouselAdapter.ViewHolder2>() {
    inner class ViewHolder2(val binding: ItemCarouselBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder2 {
        val binding = ItemCarouselBinding.inflate(LayoutInflater.from(parent.context))
        return ViewHolder2(binding)
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: ViewHolder2, position: Int) {
        holder.binding.apply {
            Glide.with(ivItemCarouselCover)
                .load(items[position].coverUrl)
                .into(ivItemCarouselCover)
        }
    }
}

internal class CarouselWithSnapLayoutManager(context: Context) :
    LinearLayoutManager(context, HORIZONTAL, false) {
    private val minScaleDistanceFactor: Float = 1.5f
    private val scaleDownBy: Float = 0.5f

    override fun onLayoutCompleted(state: RecyclerView.State?) =
        super.onLayoutCompleted(state).also { scaleChildren() }

    override fun scrollHorizontallyBy(
        dx: Int,
        recycler: RecyclerView.Recycler,
        state: RecyclerView.State
    ) = super.scrollHorizontallyBy(dx, recycler, state).also {
        if (orientation == HORIZONTAL) scaleChildren()
    }

    private fun scaleChildren() {
        val containerCenter = width / 2f
        val scaleDistanceThreshold = minScaleDistanceFactor * containerCenter
        var translationXForward = 0f

        for (i in 0 until childCount) {
            val child = getChildAt(i)!!

            val childCenter = (child.left + child.right) / 2f
            val distanceToCenter = abs(childCenter - containerCenter)

            val scaleDownAmount = (distanceToCenter / scaleDistanceThreshold).coerceAtMost(1f)
            val scale = 1f - scaleDownBy * scaleDownAmount

            child.scaleX = scale
            child.scaleY = scale

            val translationDirection = if (childCenter > containerCenter) -1 else 1
            val translationXFromScale = translationDirection * child.width * (1 - scale) / 2f
            child.translationX = translationXFromScale + translationXForward

            translationXForward = 0f

            if (translationXFromScale > 0 && i >= 1) {
                getChildAt(i - 1)!!.translationX += 2 * translationXFromScale

            } else if (translationXFromScale < 0) {
                translationXForward = 2 * translationXFromScale
            }
        }
    }

    override fun getExtraLayoutSpace(state: RecyclerView.State): Int {
        return (width / (1 - scaleDownBy)).roundToInt()
    }
}
