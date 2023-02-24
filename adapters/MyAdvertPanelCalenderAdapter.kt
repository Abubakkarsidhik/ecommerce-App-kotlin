package com.hahnemanntechnology.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.hahnemanntechnology.R
import com.hahnemann.daza2.activities.AddAdvertPanelActivity
import com.hahnemanntechnology.activities.ManageAdvertPanelActivity
import com.hahnemanntechnology.models.AdvertPanelModels
import com.hahnemanntechnology.utilities.Constant
import com.hahnemann.daza2.utilities.GlideLoader
import kotlinx.android.synthetic.main.category_item_calender_layout.view.*

class MyAdvertPanelCalenderAdapter (
    private val context: Context ,
    private val list:ArrayList<AdvertPanelModels> ,
    private val activity: ManageAdvertPanelActivity ,

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

            holder.itemView.tv_manage_Category_2.text = model.advertPanelTitle

            GlideLoader(context).loadProductPicture(model.advertPanel, holder.itemView.manage_category_image_2)

            holder.itemView.findViewById<ImageView>(R.id.manage_Delete_Category_btn2)
                .setOnClickListener {
                    activity.deleteAdvertPanel(model.advert_Id)
                }


            holder.itemView.setOnClickListener {
                val intent = Intent(context , AddAdvertPanelActivity::class.java)
                intent.putExtra(Constant.EXTRA_ADVERT_PANEL_ID,model)
                intent.putExtra(Constant.EXTRA_ADVERT_PANEL_OWNER_ID,model.user_id)
                context.startActivity(intent)
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }







    class MyViewHolder(view: View): RecyclerView.ViewHolder(view)

}

