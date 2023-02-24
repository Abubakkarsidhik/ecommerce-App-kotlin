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
import com.hahnemanntechnology.activities.AddCategoryActivity
import com.hahnemanntechnology.activities.ManageCategoryActivity
import com.hahnemanntechnology.models.CategoryModels
import com.hahnemanntechnology.utilities.Constant

open class MyCategoryListAdapter(
    private val context: Context ,
    private val list:ArrayList<CategoryModels> ,
    private val activity: ManageCategoryActivity ,

    ): RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup , viewType: Int): RecyclerView.ViewHolder {
        return MyViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.category_item_list_layout , parent , false
            ))
    }
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder , position: Int) {
        val model = list[position]

        if (holder is MyViewHolder) {
            holder.itemView.findViewById<TextView>(R.id.TV_manage_Category).text = model.categoryTitle

            holder.itemView.findViewById<ImageView>(R.id.manage_Delete_Category_btn)
                .setOnClickListener {
                    activity.deleteCategory(model.category_Id)
                }


            holder.itemView.findViewById<ImageView>(R.id.manage_Edit_Category_btn)
                .setOnClickListener {
                    val intent = Intent(context , AddCategoryActivity::class.java)
                    intent.putExtra(Constant.EXTRA_CATEGORY_ID,model)
                    intent.putExtra(Constant.EXTRA_CATEGORY_OWNER_ID,model.user_id)
                    context.startActivity(intent)
                }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }







    class MyViewHolder(view: View):RecyclerView.ViewHolder(view)

}

