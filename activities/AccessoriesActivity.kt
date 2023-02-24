package com.hahnemann.daza2.activities

import android.content.Intent
import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.hahnemann.daza2.R
import com.hahnemanntechnology.activities.BaseActivity
import com.hahnemanntechnology.activities.MainActivity
import com.hahnemanntechnology.activities.RepairServicesActivity
import com.hahnemanntechnology.activities.UserActivity
import com.hahnemanntechnology.adapters.RepairServiceCategoryListAdapter
import com.hahnemanntechnology.adapters.RepairServiceProductListAdapter
import com.hahnemanntechnology.firestore.FirestoreClass
import com.hahnemanntechnology.models.CategoryModels
import com.hahnemanntechnology.models.ProductModels
import kotlinx.android.synthetic.main.activity_repair_services.*

@Suppress("DEPRECATION")
class AccessoriesActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_accessories)

        //Bottom Navigation Bar
        Bottomnav()

    }




    private fun Bottomnav() {
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        // Set Home selected

        // Set Home selected
        bottomNavigationView.selectedItemId = R.id.accessories

        // Perform item selected listener
        bottomNavigationView.setOnNavigationItemSelectedListener(BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.accessories -> return@OnNavigationItemSelectedListener true
                R.id.repair_service -> {
                    startActivity(Intent(applicationContext , RepairServicesActivity::class.java))
                    overridePendingTransition(0 , 0)
                    return@OnNavigationItemSelectedListener true
                }

                R.id.home -> {
                    startActivity(Intent(applicationContext , MainActivity::class.java))
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

        FirestoreClass().getRepairServiceCategoryItemsList(this@AccessoriesActivity)
    }

    fun successRepairServiceItemsList(categoryItemsList: ArrayList<CategoryModels>) {

        // Hide the progress dialog.
        hideProgressDialog()

        rv_category_repair_service.layoutManager = LinearLayoutManager(this,
            LinearLayoutManager.HORIZONTAL,false)
        rv_category_repair_service.setHasFixedSize(true)

        val adapter = RepairServiceCategoryListAdapter(this, categoryItemsList,this@AccessoriesActivity)
        rv_category_repair_service.adapter = adapter



    }
    private fun getRepairServiceActivityProductItemsList() {
        // Show the progress dialog.
        // showProgressDialog(resources.getString(R.string.please_wait))

        FirestoreClass().getRepairServiceProductItemsList(this@AccessoriesActivity)
    }
    fun successRepairServiceProductItemsList(categoryItemsList: ArrayList<ProductModels>) {

        // Hide the progress dialog.
        hideProgressDialog()

        rv_products_repair_service.layoutManager = GridLayoutManager(this,2)
        rv_products_repair_service.setHasFixedSize(true)

        val adapter = RepairServiceProductListAdapter(this, categoryItemsList,this@AccessoriesActivity)
        rv_products_repair_service.adapter = adapter



    }
}