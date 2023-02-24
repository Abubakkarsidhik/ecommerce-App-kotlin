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
import com.hahnemann.daza2.activities.AddAddressActivity
import com.hahnemanntechnology.activities.AddressActivity
import com.hahnemanntechnology.activities.CheckoutActivity
import com.hahnemanntechnology.models.AddressModel
import com.hahnemanntechnology.utilities.Constant
import kotlinx.android.synthetic.main.address_item_layout.view.*

open class AddressListAdapter(
    private val context: Context ,
    private var list: ArrayList<AddressModel> ,
    private val selectAddress: Boolean,
    private val activity:AddressActivity
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup , viewType: Int): RecyclerView.ViewHolder {

        return MyViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.address_item_layout,
                parent,
                false
            )
        )
    }


    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder , position: Int) {
        val model = list[position]

        if (holder is MyViewHolder) {
            holder.itemView.tv_address_full_name.text = model.name
            holder.itemView.tv_address_type.text = model.type
            holder.itemView.tv_address_flat_no.text = model.flat_no
            holder.itemView.tv_address_details.text = model.area
            holder.itemView.tv_address_city.text="${model.town}-"
            holder.itemView.tv_address_zipcode.text = model.zipCode
            holder.itemView.tv_address_mobile_number.text = model.mobileNumber

            holder.itemView.btn_address_delete.setOnClickListener {
               activity.deleteAddress(model.id)
            }

            holder.itemView.btn_address_edit.setOnClickListener {
                fun notifyEditItem(activity: Activity , position: Int) {
                    val intent = Intent(context, AddAddressActivity::class.java)
                    intent.putExtra(Constant.EXTRA_ADDRESS_DETAILS, list[position])
                    activity.startActivityForResult(intent, Constant.ADD_ADDRESS_REQUEST_CODE)
                    notifyItemChanged(position) // Notify any registered observers that the item at position has changed.
                }              }
            if (selectAddress) {
                holder.itemView.setOnClickListener {

                    val intent = Intent(context, CheckoutActivity::class.java)
                    intent.putExtra(Constant.EXTRA_SELECTED_ADDRESS, model)
                    context.startActivity(intent)
                }
            }
        }
    }


    override fun getItemCount(): Int {
        return list.size
    }




    private class MyViewHolder(view: View) : RecyclerView.ViewHolder(view)
}