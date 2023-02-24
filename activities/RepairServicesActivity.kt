package com.hahnemanntechnology.activities

import android.content.Intent
import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.hahnemann.daza2.activities.AccessoriesActivity
import com.hahnemanntechnology.R
import com.hahnemanntechnology.adapters.RepairServiceCategoryListAdapter
import com.hahnemanntechnology.adapters.RepairServiceProductListAdapter
import com.hahnemanntechnology.firestore.FirestoreClass
import com.hahnemanntechnology.models.CategoryModels
import com.hahnemanntechnology.models.ProductModels
import kotlinx.android.synthetic.main.activity_repair_services.*

@Suppress("DEPRECATION")
class RepairServicesActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_repair_services)

        bottomNav()

    }


    private fun bottomNav() {
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        // Set Home selected

        // Set Home selected
        bottomNavigationView.selectedItemId = R.id.repair_service

        // Perform item selected listener

        // Perform item selected listener
        bottomNavigationView.setOnNavigationItemSelectedListener(BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.repair_service -> return@OnNavigationItemSelectedListener true
                R.id.home -> {
                    startActivity(Intent(applicationContext , MainActivity::class.java))
                    overridePendingTransition(0 , 0)
                    return@OnNavigationItemSelectedListener true
                }

                R.id.accessories -> {
                    startActivity(Intent(applicationContext , AccessoriesActivity::class.java))
                    overridePendingTransition(0 , 0)
                    return@OnNavigationItemSelectedListener true
                }
                R.id.user -> {
                    startActivity(Intent(applicationContext , UserActivity::class.java))
                    overridePendingTransition(0 , 0)
                    return@OnNavigationItemSelectedListener true
                }
            }
            false
        })

    }

    override fun onResume() {
        super.onResume()
        //  getAdvertPanelListFromFireStore()
        getRepairServiceActivityItemsList()
        getRepairServiceActivityProductItemsList()
    }

    private fun getRepairServiceActivityItemsList() {
        // Show the progress dialog.
        showProgressDialog(resources.getString(R.string.please_wait))

        FirestoreClass().getRepairServiceCategoryItemsList(this@RepairServicesActivity)
    }

    fun successRepairServiceItemsList(categoryItemsList: ArrayList<CategoryModels>) {

        // Hide the progress dialog.
        hideProgressDialog()

            rv_category_repair_service.layoutManager = LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false)
            rv_category_repair_service.setHasFixedSize(true)

            val adapter = RepairServiceCategoryListAdapter(this, categoryItemsList,this@RepairServicesActivity)
            rv_category_repair_service.adapter = adapter



    }
    private fun getRepairServiceActivityProductItemsList() {
        // Show the progress dialog.
       // showProgressDialog(resources.getString(R.string.please_wait))

        FirestoreClass().getRepairServiceProductItemsList(this@RepairServicesActivity)
    }
    fun successRepairServiceProductItemsList(categoryItemsList: ArrayList<ProductModels>) {

        // Hide the progress dialog.
        hideProgressDialog()

        rv_products_repair_service.layoutManager = GridLayoutManager(this,2)
        rv_products_repair_service.setHasFixedSize(true)

        val adapter = RepairServiceProductListAdapter(this, categoryItemsList,this@RepairServicesActivity)
        rv_products_repair_service.adapter = adapter



    }
}