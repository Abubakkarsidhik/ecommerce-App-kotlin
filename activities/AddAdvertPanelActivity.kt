package com.hahnemann.daza2.activities

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.hahnemann.daza2.R
import com.hahnemann.daza2.databinding.ActivityAddAdvertpanelBinding
import com.hahnemanntechnology.*
import com.hahnemanntechnology.activities.BaseActivity
import com.hahnemanntechnology.firestore.FirestoreClass
import com.hahnemanntechnology.models.AdvertPanelModels
import com.hahnemanntechnology.utilities.Constant
import com.hahnemann.daza2.utilities.GlideLoader
import kotlinx.android.synthetic.main.activity_add_advertpanel.*
import java.io.IOException

class AddAdvertPanelActivity : BaseActivity(), View.OnClickListener {
    private lateinit var binding: ActivityAddAdvertpanelBinding
    private var mSelectedImageFileUri: Uri? = null
    private var mAdvertPanelImageURL: String = ""
    private  var mAdvertDetails : AdvertPanelModels? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddAdvertpanelBinding.inflate(layoutInflater)


        //To Get Image
        val getImage = registerForActivityResult(
            ActivityResultContracts.GetContent() , ActivityResultCallback {
                 binding.ivAddAdvertPanel.setImageURI(it)
                mSelectedImageFileUri = it
                GlideLoader(this).loadUserPicture(
                    mSelectedImageFileUri!! ,
                    binding.ivAddAdvertPanel
                )
            }
        )

        setContentView(binding.root)
        binding.backArrowAddAdvertPanel.setOnClickListener { onBackPressed() }
        binding.cancelBtAddAdvertPanel.setOnClickListener{ onBackPressed() }
        binding.ivAddNewAdvertpanel.setOnClickListener { getImage.launch("image/*") }
        binding.submitBtAddAdvertPanel.setOnClickListener(this)


        if (intent.hasExtra(Constant.EXTRA_ADVERT_PANEL_ID)) {
            // Get the user details from intent as a ParcelableExtra.
            mAdvertDetails = intent.getParcelableExtra(Constant.EXTRA_ADVERT_PANEL_ID)!!

        }
        if (mAdvertDetails != null) {
            if (mAdvertDetails!!.advert_Id.isNotEmpty()) {


                binding.tvTitle.text = "Edit Advert Panel"
                binding.submitBtAddAdvertPanel.text = resources.getString(R.string.btn_lbl_update)

                mAdvertDetails?.advertPanel?.let {
                    GlideLoader(this@AddAdvertPanelActivity).loadProductPicture(
                        it ,
                        iv_add_AdvertPanel
                    )
                }
                binding.advertPanelTitle.setText(mAdvertDetails!!.advertPanelTitle)

            }
        }

    }






    override fun onClick(v: View?) {
        if (v!=null)
            when (v.id)
            {
                R.id.iv_add_new_Advertpanel ->{
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
                    {
                        //Constant.showImageChooser(this@AddProductActivity)

                    }
                    else
                        ActivityCompat.requestPermissions(
                            this,
                            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                            Constant.READ_STORAGE_PERMISSION_CODE
                        )
                }

                R.id.submit_bt_add_Advert_panel ->{
                    if (validateAdvertDetails())
                    {
                        // showErrorSnackBar("Your Details are valid",false)
                        uploadAdvertImage()
                    }
                    else if (validateAdvertDetails()){
                        updateAdvertPanelDetails()
                    }
                }
            }
    }
    private fun uploadAdvertImage(){
        showProgressDialog(resources.getString(R.string.plz_wait))
        FirestoreClass().uploadImageToCloudStorage(this,mSelectedImageFileUri,
            Constant.ADVERT_PANEL_IMAGE
        )
    }
    fun imageUploadSuccess(imageURL:String)
    {
         hideProgressDialog()
        // showErrorSnackBar( "Your image is uploaded Successfully. Image URL is $imageURL", false)

        mAdvertPanelImageURL=imageURL
        uploadAdvertPanelDetails()
    }

    fun advertPanelUploadSuccess(){
        hideProgressDialog()
        Toast.makeText(this,"Your Advert Panel was uploaded Successfully", Toast.LENGTH_SHORT).show()
        finish()

    }
    private fun uploadAdvertPanelDetails()
    {
        val username = this.getSharedPreferences(
            Constant.DAZA_PREFERENCES , Context.MODE_PRIVATE).getString(Constant.LOGGED_IN_USERNAME ,"")!!

        val advertPanel= AdvertPanelModels(
            FirestoreClass().getCurrentUserID(),
            username,
            binding.advertPanelTitle.text.toString().trim{it<=' '},
            mAdvertPanelImageURL)

        FirestoreClass().uploadAdvertPanelDetails(this,advertPanel)

    }
    private fun validateAdvertDetails():Boolean
    {
        return when (mSelectedImageFileUri) {
            null -> {
                showErrorSnackBar(resources.getString(R.string.err_plz_select_product_image),true)
                false
            }
            else -> {
                true
            }
        }
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == Constant.READ_STORAGE_PERMISSION_CODE)
        {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                //showErrorSnackBar("The storage permission is granted", false)
                Constant.showImageChooser(this)



            }
            else
            {
                //Displaying another toast if permission is not granted
                Toast.makeText(this,"Permission Denied", Toast.LENGTH_LONG).show()
            }
        }

    }




    @Deprecated("Deprecated in Java")
    @Suppress("DEPRECATION")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)
    {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode  == Activity.RESULT_OK)
        {
            if (resultCode== Constant.PICK_IMAGE_REQUEST_CODE)
            {
                if (data != null)
                {
                    binding.ivAddNewAdvertpanel.setImageDrawable(ContextCompat.getDrawable(this,
                        R.drawable.ic_vector_edit
                    ))
                    val selectedImageFileURI = data.data!!

                    try {
                        GlideLoader(this).loadUserPicture(selectedImageFileURI,binding.ivAddAdvertPanel)
                    }
                    catch (e: IOException){
                        e.printStackTrace()
                        Toast.makeText(this, "Image selection Failed", Toast.LENGTH_LONG).show()
                    }

                }
            }
        }
        else if (resultCode == Activity.RESULT_CANCELED) {
            Log.e("Request Cancelled","Image Selection Canceled")
        }
    }

    private fun updateAdvertPanelDetails() {

        val userHashMap = HashMap<String, Any>()

        // Get the FirstName from editText and trim the space
        val advertTitle = advertPanelTitle.text.toString().trim { it <= ' ' }
        if (advertTitle != mAdvertDetails!!.advertPanelTitle) {
            userHashMap[Constant.ADVERT_PANEL_TITLE] = advertTitle
        }


        if (mAdvertPanelImageURL.isNotEmpty()) {
            userHashMap[Constant.PRODUCT_IMAGE] = mAdvertPanelImageURL
        }


        // call the registerUser function of FireStore class to make an entry in the database.
        FirestoreClass().updateAdvertPanelDetails(
            this@AddAdvertPanelActivity,
            userHashMap
        )
    }

    fun updateAdvertPanelSuccess() {

        // Hide the progress dialog
        hideProgressDialog()

        Toast.makeText(
            this@AddAdvertPanelActivity,
            resources.getString(R.string.msg_product_update_success),
            Toast.LENGTH_SHORT
        ).show()



    }






}