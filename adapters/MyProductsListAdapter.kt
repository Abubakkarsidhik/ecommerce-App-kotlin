package com.hahnemanntechnology.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.hahnemanntechnology.R
import com.hahnemanntechnology.activities.AddProductsActivity
import com.hahnemanntechnology.activities.ManageProductsActivity
import com.hahnemanntechnology.models.ProductModels
import com.hahnemanntechnology.utilities.Constant

open class MyProductsListAdapter(
    private val context: Context,
    private val list:ArrayList<ProductModels>,
    private val activity:ManageProductsActivity
    ):RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup , viewType: Int): RecyclerView.ViewHolder {
        return MyViewHolder(
            LayoutInflater.from(context).inflate(R.layout.item_layout_product_list
            ,parent,false
            ))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder , position: Int) {
        val model = list[position]

        if (holder is MyViewHolder) {
            holder.itemView.findViewById<TextView>(R.id.TV_manage_Products).text = model.productTitle

            holder.itemView.findViewById<ImageView>(R.id.manage_Delete_Product_btn)
                .setOnClickListener {
                  activity.deleteProducts(model.product_id)
                }

            holder.itemView.findViewById<ImageView>(R.id.manage_Edit_Product_btn)
                .setOnClickListener {
                    val intent = Intent(context , AddProductsActivity::class.java)
                    intent.putExtra(Constant.EXTRA_PRODUCT_OWNER_ID, model.user_id)
                   intent.putExtra(Constant.EXTRA_PRODUCT_DETAILS,model)
                    context.startActivity(intent)
                }
        }
    }


    override fun getItemCount(): Int {
        return list.size
    }

    class MyViewHolder(view: View):RecyclerView.ViewHolder(view)

    }