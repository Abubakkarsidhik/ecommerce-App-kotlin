package com.hahnemanntechnology.activities

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.hahnemann.daza2.activities.AddAddressActivity
import com.hahnemanntechnology.R
import com.hahnemanntechnology.adapters.AddressListAdapter
import com.hahnemanntechnology.firestore.FirestoreClass
import com.hahnemanntechnology.models.AddressModel
import com.hahnemanntechnology.utilities.Constant
import kotlinx.android.synthetic.main.activity_add_address.*
import kotlinx.android.synthetic.main.activity_address.*
import kotlinx.android.synthetic.main.address_item_layout.*

@Suppress("DEPRECATION")
class AddressActivity : BaseActivity(){

    private var mSelectAddress: Boolean = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_address)

        backArrow_address.setOnClickListener{ onBackPressed() }

        if (intent.hasExtra(Constant.EXTRA_SELECT_ADDRESS)) {
            mSelectAddress =
                intent.getBooleanExtra(Constant.EXTRA_SELECT_ADDRESS, false)
        }



        if (mSelectAddress) {
            tv_title_address.text = resources.getString(R.string.title_select_address)
        }

        findViewById<RelativeLayout>(R.id.tv_address_addNew).setOnClickListener {
            val intent = Intent(this@AddressActivity, AddAddressActivity::class.java)
            startActivityForResult(intent, Constant.ADD_ADDRESS_REQUEST_CODE)
        }

        getAddressList()
    }


    @Deprecated("Deprecated in Java")
    public override fun onActivityResult(requestCode: Int , resultCode: Int , data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == Constant.ADD_ADDRESS_REQUEST_CODE) {

                getAddressList()
            }
        } else if (resultCode == Activity.RESULT_CANCELED) {
            // A log is printed when user close or cancel the image selection.
            Log.e("Request Cancelled", "To add the address.")
        }
    }


    private fun getAddressList() {

        // Show the progress dialog.
        showProgressDialog(resources.getString(R.string.plz_wait))

        FirestoreClass().getAddressesList(this@AddressActivity)
    }


    /**
     * A function to get the success result of address list from cloud firestore.
     *
     * @param addressList
     */
    fun successAddressListFromFirestore(addressList: ArrayList<AddressModel>) {

        // Hide the progress dialog
        hideProgressDialog()

        if (addressList.size > 0) {

            rv_address_list.visibility = View.VISIBLE
            tv_no_address_found.visibility = View.GONE

            rv_address_list.layoutManager = LinearLayoutManager(this@AddressActivity)
            rv_address_list.setHasFixedSize(true)

            val addressAdapter = AddressListAdapter(this@AddressActivity, addressList, mSelectAddress,this)
            rv_address_list.adapter = addressAdapter


         /*   if (!mSelectAddress) {
                val editSwipeHandler = object : SwipeToEditCallback(this) {
                    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {


                    }
                }
                val editItemTouchHelper = ItemTouchHelper(editSwipeHandler)
                editItemTouchHelper.attachToRecyclerView(rv_address_list)


                val deleteSwipeHandler = object : SwipeToDeleteCallback(this) {
                    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

                        // Show the progress dialog.
                        showProgressDialog(resources.getString(R.string.please_wait))

                        FirestoreClass().deleteAddress(
                            this@AddressListActivity,
                            addressList[viewHolder.adapterPosition].id
                        )
                    }
                }
                val deleteItemTouchHelper = ItemTouchHelper(deleteSwipeHandler)
                deleteItemTouchHelper.attachToRecyclerView(rv_address_list)
            }  */
        } else {
            rv_address_list.visibility = View.GONE
            tv_no_address_found.visibility = View.VISIBLE
        }
    }


    fun deleteAddress(categoryID: String) {
        // Toast.makeText(requireActivity(),"You can delete the product. $productID",Toast.LENGTH_SHORT).show()
        showAlertDialogToDeleteCategory(categoryID)
    }

    private fun showAlertDialogToDeleteCategory(addressID: String){

        val builder = AlertDialog.Builder(this)
        //set title for alert dialog
        builder.setTitle(resources.getString(R.string.delete_dialog_title))
        //Set msg for alert dialog
        builder.setMessage(resources.getString(R.string.address_remove_dialog_message))
        builder.setIcon(android.R.drawable.ic_dialog_alert)

        //performing positive action
        builder.setPositiveButton(resources.getString(R.string.yes))
        {
                dialogInterface,_ ->
            showProgressDialog(resources.getString(R.string.plz_wait))
            FirestoreClass().deleteAddress(this,addressID)
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


    /**
     * A function notify the user that the address is deleted successfully.
     */
    fun deleteAddressSuccess() {

        // Hide progress dialog.
        hideProgressDialog()

        Toast.makeText(
            this@AddressActivity,
            resources.getString(R.string.err_your_address_deleted_successfully),
            Toast.LENGTH_SHORT
        ).show()

        getAddressList()
    }

}