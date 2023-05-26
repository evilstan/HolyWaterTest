package com.books.app.ui.fragment.adapters

import android.annotation.SuppressLint
import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.books.app.data.model.Banner
import com.books.app.databinding.ItemBannerBinding
import com.bumptech.glide.Glide


class BannerAdapter(private val onClickListener: (Int) -> Unit) :
    RecyclerView.Adapter<BannerAdapter.BannerViewHolder>() {

    private lateinit var dataSet: List<Banner>

    @SuppressLint("NotifyDataSetChanged")
    fun updateDataset(items: List<Banner>) {
        dataSet = items
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BannerViewHolder {
        val binding = ItemBannerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BannerViewHolder(binding)
    }

    override fun getItemCount() = Int.MAX_VALUE

    override fun onBindViewHolder(holder: BannerViewHolder, position: Int) {
        with(holder.binding) {
            root.setOnClickListener { onClickListener(dataSet[position % dataSet.size].bookId) }
            Glide.with(ivBanner)
                .load(dataSet[position % dataSet.size].cover)
                .into(ivBanner)
        }}

    inner class BannerViewHolder(val binding: ItemBannerBinding) : ViewHolder(binding.root)
}


class BannerIndicators(
    private val itemsCount: Int,
    private val colorInactive: Int,
    private val colorActive: Int
) : RecyclerView.ItemDecoration() {

    private val radius = DP * 4
    private val innerPadding = DP * 16
    private val paint = Paint()

    init {
        paint.style = Paint.Style.FILL
        paint.isAntiAlias = true
    }

    override fun onDrawOver(canvas: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDrawOver(canvas, parent, state)

        val padding = 0.coerceAtLeast(itemsCount - 1) * innerPadding
        val totalWidth = radius * itemsCount + padding
        val startX = (parent.width - totalWidth) / 2f
        val startY = parent.height - innerPadding
        val position =
            (parent.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition() % itemsCount
        if (position == RecyclerView.NO_POSITION) return

        drawInactiveIndicators(canvas, startX, startY)
        drawActiveIndicators(canvas, startX, startY, position)
    }

    private fun drawInactiveIndicators(canvas: Canvas, startX: Float, startY: Float) {
        paint.color = colorInactive
        val itemWidth = radius + innerPadding
        var start = startX
        for (i in 0 until itemsCount) {
            canvas.drawCircle(start, startY, 10f, paint)
            start += itemWidth
        }
    }

    private fun drawActiveIndicators(canvas: Canvas, startX: Float, startY: Float, index: Int) {
        paint.color = colorActive
        val indicatorStartX = startX + (radius + innerPadding) * index
        canvas.drawCircle(indicatorStartX, startY, 10f, paint)
    }

    companion object {
        private val DP = Resources.getSystem().displayMetrics.density
    }
}