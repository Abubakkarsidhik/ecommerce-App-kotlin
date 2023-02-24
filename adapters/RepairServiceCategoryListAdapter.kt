package com.hahnemanntechnology.adapters

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hahnemanntechnology.R
import com.hahnemanntechnology.models.CategoryModels
import com.hahnemanntechnology.models.ProductModels
import com.hahnemann.daza2.utilities.GlideLoader
import kotlinx.android.synthetic.main.item_layout_repair_activity_category.view.*

class RepairServiceCategoryListAdapter(
    private val context: Context ,
    private var list: ArrayList<CategoryModels>,
    private val activity: Activity ,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var onClickListener: OnClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup , viewType: Int): RecyclerView.ViewHolder {
        return MyViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.item_layout_repair_activity_category ,
                parent ,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder , position: Int) {
        val model = list[position]

        if (holder is MyViewHolder) {
            GlideLoader(context).loadProductPicture(
                model.categoryImage,
                holder.itemView.iv_show_image
            )
            holder.itemView.tv_category_title.text = model.categoryTitle


        }

    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun setOnClickListener(onClickListener: OnClickListener) {
        this.onClickListener = onClickListener
    }

    interface OnClickListener {

        fun onClick(position: Int, product: ProductModels)
    }

    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view)


}