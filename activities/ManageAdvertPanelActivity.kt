package com.hahnemanntechnology.activities

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.hahnemann.daza2.activities.AddAdvertPanelActivity
import com.hahnemanntechnology.R
import com.hahnemanntechnology.adapters.MyAdvertPanelCalenderAdapter
import com.hahnemanntechnology.adapters.MyAdvertPanelListAdapter
import com.hahnemanntechnology.databinding.ActivityManageAdvertPanelBinding
import com.hahnemanntechnology.firestore.FirestoreClass
import com.hahnemanntechnology.models.AdvertPanelModels
import kotlinx.android.synthetic.main.activity_manage_category.*

class ManageAdvertPanelActivity : BaseActivity() {

    private lateinit var binding: ActivityManageAdvertPanelBinding
    private var button: ImageView? = null
    private var i = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityManageAdvertPanelBinding.inflate(layoutInflater)
        setContentView(binding.root)

        button = findViewById<View>(R.id.btn_listview) as ImageView


        binding.backArrowManageAdvertPanel.setOnClickListener { onBackPressed() }

        binding.addNewAdvertPanel.setOnClickListener {
            val intent = Intent(this , AddAdvertPanelActivity::class.java)
            startActivity(intent)
        }

    }

    fun deleteAdvertPanel(productID : String){
        // Toast.makeText(requireActivity(),"You can delete the product. $productID",Toast.LENGTH_SHORT).show()
        showAlertDialogToDeleteAdvertPanel(productID)
    }

    fun advertPanelDeleteSuccess(){
        hideProgressDialog()
        Toast.makeText(this,resources.getString(R.string.advertPanel_delete_success_msg),
            Toast.LENGTH_SHORT).show()
        getAdvertPanelListFromFireStore()
    }

    fun successAdvertPanelListFromFireStore(adapterList: ArrayList<AdvertPanelModels>)
    {
        hideProgressDialog()
        if (adapterList.size>0){
            binding.rvMyAdvertPanelItem.visibility= View.VISIBLE
            binding.tvNoProductsFound.visibility= View.GONE

            binding.rvMyAdvertPanelItem.layoutManager= LinearLayoutManager(this)
            binding.rvMyAdvertPanelItem.setHasFixedSize(true)
            val adapterProducts = MyAdvertPanelListAdapter(this,adapterList,this)
            binding.rvMyAdvertPanelItem.adapter=adapterProducts

            binding.btnListview.setOnClickListener {
                if (i == 0) {
                    btn_listview.setImageResource(R.drawable.list)
                    binding.rvMyAdvertPanelItem.layoutManager= LinearLayoutManager(this)
                    binding.rvMyAdvertPanelItem.setHasFixedSize(true)
                    val adapterCategory = MyAdvertPanelListAdapter(this , adapterList , this)
                    binding.rvMyAdvertPanelItem.adapter=adapterCategory
                    i++
                }
                else if (i == 1) {
                    btn_listview.setImageResource(R.drawable.grid_line)
                    binding.rvMyAdvertPanelItem.layoutManager= LinearLayoutManager(this)
                    binding.rvMyAdvertPanelItem.setHasFixedSize(true)
                    val adapterCategory = MyAdvertPanelCalenderAdapter(this , adapterList , this)
                    binding.rvMyAdvertPanelItem.adapter=adapterCategory
                    i=0
                }
            }

        }
        else
        {
            binding.rvMyAdvertPanelItem.visibility= View.GONE
            binding.tvNoProductsFound.visibility= View.VISIBLE
        }
    }
    private fun getAdvertPanelListFromFireStore(){
        showProgressDialog(resources.getString(R.string.plz_wait))
        FirestoreClass().getAdvertPanelList(this)

    }
    override fun onResume() {
        super.onResume()
        getAdvertPanelListFromFireStore()
    }


    private fun showAlertDialogToDeleteAdvertPanel(advertPanelID: String){

        val builder = AlertDialog.Builder(this)
        //set title for alert dialog
        builder.setTitle(resources.getString(R.string.delete_dialog_title))
        //Set msg for alert dialog
        builder.setMessage(resources.getString(R.string.advertPanel_delete_dialog_Message))
        builder.setIcon(android.R.drawable.ic_dialog_alert)

        //performing positive action
        builder.setPositiveButton(resources.getString(R.string.yes))
        {
                dialogInterface,_ ->
            showProgressDialog(resources.getString(R.string.plz_wait))
            FirestoreClass().deleteAdvertPanel(this,advertPanelID)
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