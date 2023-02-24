package com.hahnemanntechnology.activities

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.hahnemanntechnology.R
import com.hahnemanntechnology.adapters.MyCategoryCalenderAdapter
import com.hahnemanntechnology.adapters.MyCategoryListAdapter
import com.hahnemanntechnology.databinding.ActivityManageCategoryBinding
import com.hahnemanntechnology.firestore.FirestoreClass
import com.hahnemanntechnology.models.CategoryModels
import kotlinx.android.synthetic.main.activity_manage_category.*

class ManageCategoryActivity : BaseActivity()  {

    private lateinit var binding: ActivityManageCategoryBinding
    private var button: ImageView? = null
    private var i = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityManageCategoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        button = findViewById<View>(R.id.btn_listview) as ImageView
       // b1!!.setOnClickListener(this)

        binding.backArrowManageCategoryBtn.setOnClickListener { onBackPressed() }

        binding.addCategoryBtn.setOnClickListener {
            val intent = Intent(this , AddCategoryActivity::class.java)
            startActivity(intent)
        }

    }




    fun successCategoryListFromFireStore(categoryList: ArrayList<CategoryModels>)
    {
        hideProgressDialog()
        if (categoryList.size>0){
            binding.rvMyCategoryItem.visibility= View.VISIBLE
            binding.tvNoCategoryFound.visibility= View.GONE
            binding.rvMyCategoryItem.layoutManager= LinearLayoutManager(this)
            binding.rvMyCategoryItem.setHasFixedSize(true)
            val adapterCategory = MyCategoryListAdapter(this , categoryList , this)
            binding.rvMyCategoryItem.adapter=adapterCategory


            binding.btnListview.setOnClickListener {
                if (i == 0) {
                    btn_listview.setImageResource(R.drawable.list)
                    binding.rvMyCategoryItem.layoutManager= LinearLayoutManager(this)
                    binding.rvMyCategoryItem.setHasFixedSize(true)
                    val adapterCategory = MyCategoryListAdapter(this , categoryList , this)
                    binding.rvMyCategoryItem.adapter=adapterCategory
                    i++
                }
                else if (i == 1) {
                    btn_listview.setImageResource(R.drawable.grid_line)
                    binding.rvMyCategoryItem.layoutManager= LinearLayoutManager(this)
                    binding.rvMyCategoryItem.setHasFixedSize(true)
                    val adapterCategory = MyCategoryCalenderAdapter(this , categoryList , this)
                    binding.rvMyCategoryItem.adapter=adapterCategory
                    i=0
                }
            }


        }
        else
        {
            binding.rvMyCategoryItem.visibility= View.GONE
            binding.tvNoCategoryFound.visibility= View.VISIBLE
        }
    }

    private fun getCategoryListFromFireStore(){
        showProgressDialog(resources.getString(R.string.plz_wait))
        FirestoreClass().getCategoryList(this)

    }

    override fun onResume() {
        super.onResume()
        getCategoryListFromFireStore()
    }

    fun deleteCategory(categoryID: String) {
        // Toast.makeText(requireActivity(),"You can delete the product. $productID",Toast.LENGTH_SHORT).show()
        showAlertDialogToDeleteCategory(categoryID)
    }

    fun categoryDeleteSuccess() {
        hideProgressDialog()
        Toast.makeText(
            this , resources.getString(R.string.category_delete_success_msg) ,
            Toast.LENGTH_SHORT).show()
        getCategoryListFromFireStore()
    }

    private fun showAlertDialogToDeleteCategory(categoryID: String){

        val builder = AlertDialog.Builder(this)
        //set title for alert dialog
        builder.setTitle(resources.getString(R.string.delete_dialog_title))
        //Set msg for alert dialog
        builder.setMessage(resources.getString(R.string.category_delete_dialog_Message))
        builder.setIcon(android.R.drawable.ic_dialog_alert)

        //performing positive action
        builder.setPositiveButton(resources.getString(R.string.yes))
        {
                dialogInterface,_ ->
            showProgressDialog(resources.getString(R.string.plz_wait))
            FirestoreClass().deleteCategory(this,categoryID)
            dialogInterface.dismiss()
        }
        //performing negative action
        builder.setNegativeButton(resources.getString(R.string.no))
        {
                dialogInterface,_ ->
            dialogInterface.dismiss()
        }
        //create the AlertDialog
        val alertDialog: AlertDialog =builder.create()
        //set other dialog properties
        alertDialog.setCancelable(false)
        alertDialog.show()
    }



}