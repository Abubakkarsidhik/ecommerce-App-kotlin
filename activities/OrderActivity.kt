package com.hahnemanntechnology.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.hahnemanntechnology.R
import com.hahnemanntechnology.adapters.MyOrdersListAdapter
import com.hahnemanntechnology.firestore.FirestoreClass
import com.hahnemanntechnology.models.OrderModel
import kotlinx.android.synthetic.main.activity_order.*

class OrderActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order)

        backArrow.setOnClickListener { onBackPressed() }

    }
        override fun onResume() {
            super.onResume()

            getMyOrdersList()
        }

        /**
         * A function to get the list of my orders.
         */
        private fun getMyOrdersList() {
            // Show the progress dialog.
            showProgressDialog(resources.getString(R.string.please_wait))

            FirestoreClass().getMyOrdersList(this@OrderActivity)
        }

        /**
         * A function to get the success result of the my order list from cloud firestore.
         *
         * @param ordersList List of my orders.
         */
        fun populateOrdersListInUI(ordersList: ArrayList<OrderModel>) {

            // Hide the progress dialog.
            hideProgressDialog()

            if (ordersList.size > 0) {

                rv_my_order_items.visibility = View.VISIBLE
                tv_no_orders_found.visibility = View.GONE

                rv_my_order_items.layoutManager = LinearLayoutManager(this)
                rv_my_order_items.setHasFixedSize(true)

                val myOrdersAdapter = MyOrdersListAdapter(this, ordersList)
                rv_my_order_items.adapter = myOrdersAdapter
            } else {
                rv_my_order_items.visibility = View.GONE
                tv_no_orders_found.visibility = View.VISIBLE
            }
        }
}