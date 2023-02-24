package com.hahnemanntechnology.adapters

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hahnemanntechnology.R
import com.hahnemanntechnology.activities.ProductDetailsActivity
import com.hahnemanntechnology.models.ProductModels
import com.hahnemanntechnology.utilities.Constant
import com.hahnemann.daza2.utilities.GlideLoader
import kotlinx.android.synthetic.main.item_layout_product_details.view.*
import kotlinx.android.synthetic.main.item_layout_product_details.view.iv_profile_edit
import kotlinx.android.synthetic.main.item_layout_product_details.view.iv_show_product
import kotlinx.android.synthetic.main.item_layout_product_details.view.tv_product_price
import kotlinx.android.synthetic.main.item_layout_product_details.view.tv_product_quantity
import kotlinx.android.synthetic.main.item_layout_product_details.view.tv_product_title
import kotlinx.android.synthetic.main.item_layout_repair_activity_product.view.*


class ProductDetailsAdapter(
    private val context: Context ,
    private var list: ArrayList<ProductModels> ,
    private val activity: Activity ,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var onClickListener: OnClickListener? = null
    private var i = 0
    override fun onCreateViewHolder(parent: ViewGroup , viewType: Int): RecyclerView.ViewHolder {
        return MyViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.item_layout_product_details ,
                parent ,
                false
            )
        )
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder , position: Int) {
        val model = list[position]

        if (holder is MyViewHolder) {
            GlideLoader(context).loadProductPicture(
                model.productImage,
                holder.itemView.iv_show_product
            )
            holder.itemView.tv_product_title.text = model.productTitle
            holder.itemView.tv_product_price.text = "$${model.productPrice}"
            holder.itemView.tv_product_quantity.text = "(${model.stockQuantity})"

            holder.itemView.setOnClickListener {
                val intent = Intent(context, ProductDetailsActivity::class.java)
                intent.putExtra(Constant.EXTRA_PRODUCT_ID, model.product_id)
                intent.putExtra(Constant.EXTRA_PRODUCT_OWNER_ID, model.user_id)
                context.startActivity(intent)
            }

            holder.itemView.iv_profile_edit.setOnClickListener {
                if (i == 0) {
                    holder.itemView.iv_profile_edit.setImageResource(R.drawable.heart_fill)
                    //    val intent = Intent(context, WishlistActivity::class.java)
                    //    intent.putExtra(Constant.EXTRA_PRODUCT_ID, model.product_id)
                    //   intent.putExtra(Constant.EXTRA_PRODUCT_OWNER_ID, model.user_id)
                    //   context.startActivity(intent)
                    i++
                }
                else if (i == 1) {
                    holder.itemView.iv_profile_edit.setImageResource(R.drawable.heart_bold)
                    i=0
                }
            }

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