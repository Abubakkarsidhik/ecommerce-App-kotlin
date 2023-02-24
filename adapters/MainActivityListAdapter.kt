package com.hahnemanntechnology.adapters

import android.annotation.SuppressLint
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
import kotlinx.android.synthetic.main.item_layout_main_activity.view.*

open class MainActivityListAdapter (
    private val context: Context ,
    private var list: ArrayList<ProductModels>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    // A global variable for OnClickListener interface.
    private var onClickListener: OnClickListener? = null

    /**
     * Inflates the item views which is designed in xml layout file
     *
     * create a new
     * {@link ViewHolder} and initializes some private fields to be used by RecyclerView.
     */
    override fun onCreateViewHolder(parent: ViewGroup , viewType: Int): RecyclerView.ViewHolder {
        return MyViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.item_layout_main_activity,
                parent,
                false
            )
        )
    }

    /**
     * Binds each item in the ArrayList to a view
     *
     * Called when RecyclerView needs a new {@link ViewHolder} of the given type to represent
     * an item.
     *
     * This new ViewHolder should be constructed with a new View that can represent the items
     * of the given type. You can either create a new View manually or inflate it from an XML
     * layout file.
     */
    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder , position: Int) {
        val model = list[position]

        if (holder is MyViewHolder) {

            GlideLoader(context).loadProductPicture(
                model.productImage,
                holder.itemView.iv_main_activity_item_image
            )
            holder.itemView.tv_main_activity_item_title.text = model.productTitle
            holder.itemView.tv_main_activity_item_price.text = "$${model.productPrice}"

             holder.itemView.setOnClickListener {
                val intent = Intent(context, ProductDetailsActivity::class.java)
                 intent.putExtra(Constant.EXTRA_PRODUCT_ID, model.product_id)
                   intent.putExtra(Constant.EXTRA_PRODUCT_OWNER_ID, model.user_id)
                context.startActivity(intent)
            }
        }
    }

    /**
     * Gets the number of items in the list
     */
    override fun getItemCount(): Int {
        return list.size
    }

    /**
     * A function for OnClickListener where the Interface is the expected parameter and assigned to the global variable.
     *
     * @param onClickListener
     */
    fun setOnClickListener(onClickListener: OnClickListener) {
        this.onClickListener = onClickListener
    }

    /**
     * An interface for onclick items.
     */
    interface OnClickListener {

        fun onClick(position: Int, product: ProductModels)
    }

    /**
     * A ViewHolder describes an item view and metadata about its place within the RecyclerView.
     */
    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view)
}