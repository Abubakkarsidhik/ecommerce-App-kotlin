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
import com.hahnemanntechnology.adapters.MyProductCalenderAdapter
import com.hahnemanntechnology.adapters.MyProductsListAdapter
import com.hahnemanntechnology.databinding.ActivityManageProductsBinding
import com.hahnemanntechnology.firestore.FirestoreClass
import com.hahnemanntechnology.models.ProductModels
import kotlinx.android.synthetic.main.activity_manage_category.*

class ManageProductsActivity : BaseActivity() {

    private lateinit var binding:ActivityManageProductsBinding
    private var button: ImageView? = null
    private var i = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityManageProductsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        button = findViewById<View>(R.id.btn_listview) as ImageView

        binding.backArrowManageProductBtn.setOnClickListener { onBackPressed() }

        binding.addProductBtn.setOnClickListener {
            val intent = Intent(this , AddProductsActivity::class.java)
            startActivity(intent)
        }

    }

    fun deleteProducts(productID : String){
        // Toast.makeText(requireActivity(),"You can delete the product. $productID",Toast.LENGTH_SHORT).show()
        showAlertDialogToDeleteProduct(productID)
    }

    fun productDeleteSuccess(){
        hideProgressDialog()
        Toast.makeText(this,resources.getString(R.string.product_delete_success_msg),
            Toast.LENGTH_SHORT).show()
        getProductListFromFireStore()
    }

    fun successProductsListFromFireStore(productsList: ArrayList<ProductModels>)
    {
        hideProgressDialog()
        if (productsList.size>0){
            binding.rvMyProductsItem.visibility= View.VISIBLE
            binding.tvNoProductsFound.visibility= View.GONE

            binding.rvMyProductsItem.layoutManager= LinearLayoutManager(this)
            binding.rvMyProductsItem.setHasFixedSize(true)
            val adapterProducts = MyProductsListAdapter(this,productsList,this)
            binding.rvMyProductsItem.adapter=adapterProducts

            binding.btnListview.setOnClickListener {
                if (i == 0) {
                    btn_listview.setImageResource(R.drawable.list)
                    binding.rvMyProductsItem.layoutManager= LinearLayoutManager(this)
                    binding.rvMyProductsItem.setHasFixedSize(true)
                    val adapterCategory = MyProductsListAdapter(this , productsList , this)
                    binding.rvMyProductsItem.adapter=adapterCategory
                    i++
                }
                else if (i == 1) {
                    btn_listview.setImageResource(R.drawable.grid_line)
                    binding.rvMyProductsItem.layoutManager= LinearLayoutManager(this)
                    binding.rvMyProductsItem.setHasFixedSize(true)
                    val adapterCategory = MyProductCalenderAdapter(this , productsList , this)
                    binding.rvMyProductsItem.adapter=adapterCategory
                    i=0
                }
            }

        }
        else
        {
            binding.rvMyProductsItem.visibility= View.GONE
            binding.tvNoProductsFound.visibility= View.VISIBLE
        }
    }
    private fun getProductListFromFireStore(){
        showProgressDialog(resources.getString(R.string.plz_wait))
        FirestoreClass().getProductsList(this)

    }
    override fun onResume() {
        super.onResume()
        getProductListFromFireStore()
    }

    private fun showAlertDialogToDeleteProduct(productID: String){

        val builder = AlertDialog.Builder(this)
        //set title for alert dialog
        builder.setTitle(resources.getString(R.string.delete_dialog_title))
        //Set msg for alert dialog
        builder.setMessage(resources.getString(R.string.product_delete_dialog_Message))
        builder.setIcon(android.R.drawable.ic_dialog_alert)

        //performing positive action
        builder.setPositiveButton(resources.getString(R.string.yes))
        {
                dialogInterface,_ ->
            showProgressDialog(resources.getString(R.string.plz_wait))
            FirestoreClass().deleteProduct(this,productID)
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