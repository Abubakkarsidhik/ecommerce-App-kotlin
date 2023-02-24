package com.hahnemanntechnology.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hahnemanntechnology.R
import com.hahnemanntechnology.activities.OrderDetailsActivity
import com.hahnemanntechnology.models.OrderModel
import com.hahnemanntechnology.utilities.Constant
import com.hahnemann.daza2.utilities.GlideLoader
import kotlinx.android.synthetic.main.item_layout_cart.view.*

open class MyOrdersListAdapter(
    private val context: Context ,
    private var list: ArrayList<OrderModel>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    /**
     * Inflates the item views which is designed in xml layout file
     *
     * create a new
     * {@link ViewHolder} and initializes some private fields to be used by RecyclerView.
     */
    override fun onCreateViewHolder(parent: ViewGroup , viewType: Int): RecyclerView.ViewHolder {
        return MyViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.item_layout_cart,
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
                model.image,
                holder.itemView.iv_cart_item_image
            )

            holder.itemView.tv_cart_item_title.text = model.title
            holder.itemView.tv_cart_item_price.text = "$${model.total_amount}"

            holder.itemView.ib_remove_cart_item.visibility = View.GONE
            holder.itemView.ib_add_cart_item.visibility = View.GONE
            holder.itemView.ib_delete_cart_item.visibility = View.GONE

            holder.itemView.setOnClickListener {
                val intent = Intent(context, OrderDetailsActivity::class.java)
                intent.putExtra(Constant.EXTRA_MY_ORDER_DETAILS, model)
                context.startActivity(intent)
            }
           /* holder.itemView {
                val intent = Intent(context, OrderDetailsActivity::class.java)
                intent.putExtra(Constant.EXTRA_MY_ORDER_DETAILS, model)
                context.startActivity(intent)
            } */
        }
    }

    /**
     * Gets the number of items in the list
     */
    override fun getItemCount(): Int {
        return list.size
    }

    /**
     * A ViewHolder describes an item view and metadata about its place within the RecyclerView.
     */
    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view)
}
