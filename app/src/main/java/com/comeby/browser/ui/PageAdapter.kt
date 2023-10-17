package com.comeby.browser.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.comeby.browser.R
import com.comeby.browser.model.WebPage

class PageAdapter : RecyclerView.Adapter<PageAdapter.ItemViewHolder>() {

    var dataList: ArrayList<WebPage> = arrayListOf()

    inner class ItemViewHolder : RecyclerView.ViewHolder {

        val linkImage: ImageView
        val linkDelete: ImageView

        constructor(item: View) : super(item) {
            linkImage = item.findViewById(R.id.link_image)
            linkDelete = item.findViewById(R.id.link_delete)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.page_item, parent, false)
        return ItemViewHolder(view)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = dataList[position]
        val bitmap = item.bitmap
        if (bitmap != null) {
            holder.linkImage.setImageBitmap(bitmap)
        } else {
            holder.linkImage.setImageDrawable(null)
        }
        holder.itemView.setOnClickListener {
            itemClickCallback?.invoke(item, position)
        }

        holder.linkDelete.setOnClickListener {
            itemDeleteClickCallback?.invoke(item, position)
        }

        if (itemCount > 1) {
            holder.linkDelete.visibility = View.VISIBLE
        } else {
            holder.linkDelete.visibility = View.GONE
        }
    }

    var itemClickCallback: ((WebPage, Int) -> Unit)? = null

    var itemDeleteClickCallback: ((WebPage, Int) -> Unit)? = null

}