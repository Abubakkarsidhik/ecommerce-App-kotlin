package com.hahnemanntechnology.activities

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.hahnemanntechnology.R
import com.hahnemanntechnology.adapters.SoldProductsListAdapter
import com.hahnemanntechnology.firestore.FirestoreClass
import com.hahnemanntechnology.models.SoldProduct
import kotlinx.android.synthetic.main.activity_sold_products.*
import java.util.*

class SoldProductsActivity : BaseActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sold_products)

        backArrow.setOnClickListener { onBackPressed() }

    }

    override fun onResume() {
        super.onResume()

        getSoldProductsList()
    }

    private fun getSoldProductsList() {
        // Show the progress dialog.
        showProgressDialog(resources.getString(R.string.please_wait))

        // Call the function of Firestore class.
        FirestoreClass().getSoldProductsList(this@SoldProductsActivity)
    }

    /**
     * A function to get the list of sold products.
     */
    fun successSoldProductsList(soldProductsList: ArrayList<SoldProduct>) {

        // Hide Progress dialog.
        hideProgressDialog()

        if (soldProductsList.size > 0) {
            rv_sold_product_items.visibility = View.VISIBLE
            tv_no_sold_products_found.visibility = View.GONE

            rv_sold_product_items.layoutManager = LinearLayoutManager(this)
            rv_sold_product_items.setHasFixedSize(true)

            val soldProductsListAdapter = SoldProductsListAdapter(this, soldProductsList)
            rv_sold_product_items.adapter = soldProductsListAdapter
        } else {
            rv_sold_product_items.visibility = View.GONE
            tv_no_sold_products_found.visibility = View.VISIBLE
        }
    }
    }
