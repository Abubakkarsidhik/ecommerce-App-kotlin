package com.hahnemanntechnology.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.hahnemanntechnology.R
import com.hahnemanntechnology.adapters.MyOrderDetailsListAdapter
import com.hahnemanntechnology.models.CartModel
import com.hahnemanntechnology.models.OrderModel
import com.hahnemanntechnology.utilities.Constant
import kotlinx.android.synthetic.main.activity_order_details.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class OrderDetailsActivity : AppCompatActivity() {
    private lateinit var cartModel: CartModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_details)

        backArrow.setOnClickListener { onBackPressed() }

        var myOrderDetails: OrderModel = OrderModel()

        if (intent.hasExtra(Constant.EXTRA_MY_ORDER_DETAILS)) {
            myOrderDetails =
                intent.getParcelableExtra<OrderModel>(Constant.EXTRA_MY_ORDER_DETAILS)!!
        }

        setupUI(myOrderDetails)
    }

    /**
     * A function for actionBar Setup.
     */


    /**
     * A function to setup UI.
     *
     * @param orderDetails Order details received through intent.
     */
    @SuppressLint("SetTextI18n")
    private fun setupUI(orderDetails: OrderModel) {

        tv_order_details_id.text = orderDetails.title

        // Date Format in which the date will be displayed in the UI.
        val dateFormat = "dd MMM yyyy HH:mm"
        // Create a DateFormatter object for displaying date in specified format.
        val formatter = SimpleDateFormat(dateFormat, Locale.getDefault())

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        val calendar: Calendar = Calendar.getInstance()
        calendar.timeInMillis = orderDetails.order_datetime

        val orderDateTime = formatter.format(calendar.time)
        tv_order_details_date.text = orderDateTime

        // Get the difference between the order date time and current date time in hours.
        // If the difference in hours is 1 or less then the order status will be PENDING.
        // If the difference in hours is 2 or greater then 1 then the order status will be PROCESSING.
        // And, if the difference in hours is 3 or greater then the order status will be DELIVERED.

        val diffInMilliSeconds: Long = System.currentTimeMillis() - orderDetails.order_datetime
        val diffInHours: Long = TimeUnit.MILLISECONDS.toHours(diffInMilliSeconds)
        Log.e("Difference in Hours", "$diffInHours")

        when {
            diffInHours < 1 -> {
                tv_order_status.text = resources.getString(R.string.order_status_pending)
                tv_order_status.setTextColor(
                    ContextCompat.getColor(
                        this@OrderDetailsActivity,
                        R.color.colorAccent
                    )
                )

            }
            diffInHours < 2 -> {
                tv_order_status.text = resources.getString(R.string.order_status_in_process)
                tv_order_status.setTextColor(
                    ContextCompat.getColor(
                        this@OrderDetailsActivity,
                        R.color.colorOrderStatusInProcess
                    )
                )
            }
            else -> {
                tv_order_status.text = resources.getString(R.string.order_status_delivered)
                tv_order_status.setTextColor(
                    ContextCompat.getColor(
                        this@OrderDetailsActivity,
                        R.color.colorOrderStatusDelivered
                    )
                )
            }
        }

        rv_my_order_items_list.layoutManager = LinearLayoutManager(this@OrderDetailsActivity,LinearLayoutManager.VERTICAL,false)
        rv_my_order_items_list.setHasFixedSize(true)

        val cartListAdapter = MyOrderDetailsListAdapter(this@OrderDetailsActivity, orderDetails.items, false)
        rv_my_order_items_list.adapter = cartListAdapter

        tv_my_order_details_address_type.text = orderDetails.address.type
        tv_my_order_details_full_name.text = orderDetails.address.name
        tv_my_order_details_address.text =
            "${orderDetails.address.area}, ${orderDetails.address.zipCode}"
       // tv_my_order_details_additional_note.text = orderDetails.address.additionalNote

        if (orderDetails.address.otherDetails.isNotEmpty()) {
            tv_my_order_details_other_details.visibility = View.VISIBLE
            tv_my_order_details_other_details.text = orderDetails.address.otherDetails
        } else {
            tv_my_order_details_other_details.visibility = View.GONE
        }
        tv_my_order_details_mobile_number.text = orderDetails.address.mobileNumber

        tv_order_details_sub_total.text = orderDetails.sub_total_amount
        tv_order_details_shipping_charge.text = orderDetails.shipping_charge
        tv_order_details_total_amount.text = orderDetails.total_amount
    }
}