package com.hahnemanntechnology.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.hahnemanntechnology.R
import com.hahnemanntechnology.activities.AddProductsActivity
import com.hahnemanntechnology.activities.ManageProductsActivity
import com.hahnemanntechnology.models.ProductModels
import com.hahnemanntechnology.utilities.Constant
import com.hahnemann.daza2.utilities.GlideLoader
import kotlinx.android.synthetic.main.category_item_calender_layout.view.*

open class MyProductCalenderAdapter (
    private val context: Context ,
    private val list:ArrayList<ProductModels> ,
    private val activity: ManageProductsActivity ,

    ): RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup , viewType: Int): RecyclerView.ViewHolder {
        return MyViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.category_item_calender_layout, parent , false
            ))
    }
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder , position: Int) {
        val model = list[position]

        if (holder is MyViewHolder) {

            holder.itemView.tv_manage_Category_2.text = model.productTitle
            holder.itemView.manage_product_prize.visibility = View.VISIBLE
            holder.itemView.manage_product_prize.text = model.productPrice

            GlideLoader(context).loadProductPicture(model.productImage, holder.itemView.manage_category_image_2)

            holder.itemView.findViewById<ImageView>(R.id.manage_Delete_Category_btn2)
                .setOnClickListener {
                    activity.deleteProducts(model.product_id)
                }


            holder.itemView.setOnClickListener {
                val intent = Intent(context , AddProductsActivity::class.java)
                intent.putExtra(Constant.EXTRA_PRODUCT_OWNER_ID,model.user_id)
                intent.putExtra(Constant.EXTRA_PRODUCT_DETAILS,model)
                context.startActivity(intent)
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }







    class MyViewHolder(view: View): RecyclerView.ViewHolder(view)

}

