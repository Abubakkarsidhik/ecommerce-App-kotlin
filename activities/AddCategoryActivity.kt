package com.hahnemanntechnology.activities

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.hahnemanntechnology.*
import com.hahnemanntechnology.models.CategoryModels
import com.hahnemanntechnology.databinding.ActivityAddCategoryBinding
import com.hahnemanntechnology.firestore.FirestoreClass
import com.hahnemanntechnology.utilities.Constant
import com.hahnemann.daza2.utilities.GlideLoader
import kotlinx.android.synthetic.main.activity_add_advertpanel.*
import kotlinx.android.synthetic.main.activity_add_category.*
import kotlinx.android.synthetic.main.activity_add_products.*
import java.io.IOException

class AddCategoryActivity : BaseActivity(), View.OnClickListener {
    private lateinit var binding: ActivityAddCategoryBinding
    private var mSelectedImageFileUri: Uri?=null
    private var mCategoryImageURL:String=""
    private  var mCategoryDetails : CategoryModels? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddCategoryBinding.inflate(layoutInflater)

        //To Get Image
        val getImage = registerForActivityResult(
            ActivityResultContracts.GetContent() , ActivityResultCallback {
                binding.ivAddCategoryImage.setImageURI(it)
                mSelectedImageFileUri = it
                GlideLoader(this).loadUserPicture(
                    mSelectedImageFileUri!! ,
                    binding.ivAddCategoryImage
                )
            }
        )

        setContentView(binding.root)
        binding.backArrowAddCategory.setOnClickListener { onBackPressed() }
        binding.ivAddNewCategory.setOnClickListener { getImage.launch("image/*") }
        binding.btnAddSubmit.setOnClickListener(this)
        binding.addCategoryCancelBtn.setOnClickListener { onBackPressed() }

        if (intent.hasExtra(Constant.EXTRA_CATEGORY_ID)) {
            // Get the user details from intent as a ParcelableExtra.
            mCategoryDetails = intent.getParcelableExtra(Constant.EXTRA_CATEGORY_ID)!!

        }
        if (mCategoryDetails != null) {
            if (mCategoryDetails!!.category_Id.isNotEmpty()) {


                binding.tvTitle.text = "Edit Category"
                binding.btnAddSubmit.text = resources.getString(R.string.btn_lbl_update)

                mCategoryDetails?.categoryImage?.let {
                    GlideLoader(this@AddCategoryActivity).loadProductPicture(
                        it ,
                        ivAddCategoryImage
                    )
                }
                binding.tvCategoryTitle.setText(mCategoryDetails!!.categoryTitle)

            }

        }
    }


    override fun onClick(v: View?) {
        if (v!=null)
            when (v.id)
            {
                R.id.iv_Add_New_Category ->{
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
                    {
                        Constant.showImageChooser(this)

                    }
                    else
                        ActivityCompat.requestPermissions(
                            this,
                            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                            Constant.READ_STORAGE_PERMISSION_CODE
                        )
                }

                R.id.btnAddSubmit ->{
                    if (validateCategoryDetails())
                    {
                        //showErrorSnackBar("Your Details are valid",false)
                        uploadCategoryImage()
                    }
                    else if (validateCategoryDetails())
                    {
                        updateCategoryDetails()
                    }
                }
            }
    }
    private fun uploadCategoryImage(){
        showProgressDialog(resources.getString(R.string.plz_wait))
        FirestoreClass().uploadImageToCloudStorage(this,mSelectedImageFileUri,
            Constant.CATEGORY_IMAGE
        )

    }

    fun imageUploadSuccess(imageURL:String)
    {
        // hideProgressDialog()
        // showErrorSnackBar( "Your image is uploaded Successfully. Image URL is $imageURL", false)

        mCategoryImageURL=imageURL
        uploadCategoryDetails()
    }

    fun categoryUploadSuccess(){
        hideProgressDialog()
       Toast.makeText(this,"Your Category was uploaded Successfully", Toast.LENGTH_SHORT).show()
        finish()

    }
    private fun uploadCategoryDetails()
    {
        val username = this.getSharedPreferences(
            Constant.DAZA_PREFERENCES, Context.MODE_PRIVATE).getString(Constant.LOGGED_IN_USERNAME,"")!!
        val category= CategoryModels(
           FirestoreClass().getCurrentUserID(),
            username,
            binding.tvCategoryTitle.text.toString().trim{it<=' '},
            mCategoryImageURL)

        FirestoreClass().uploadCategoryDetails(this,category)

    }
    private fun validateCategoryDetails():Boolean
    {
        return when
        {

            TextUtils.isEmpty(binding.tvCategoryTitle.text.toString().trim{it <=' '}) ->
            {
                showErrorSnackBar(resources.getString(R.string.err_plz_enter_category_title),true)
                false
            }
            mSelectedImageFileUri==null ->
            {
                showErrorSnackBar(resources.getString(R.string.err_plz_select_category_image),true)
                false
            }

            else ->
            {
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
                    binding.ivAddNewCategory.setImageDrawable(ContextCompat.getDrawable(this,
                        R.drawable.ic_vector_edit
                    ))
                   val  selectedImageFileUri = data.data!!

                    try {
                        GlideLoader(this).loadUserPicture(selectedImageFileUri,binding.ivAddCategoryImage)
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

    private fun updateCategoryDetails() {

        val userHashMap = HashMap<String, Any>()

        // Get the FirstName from editText and trim the space
        val productTitle = tv_category_title.text.toString().trim { it <= ' ' }
        if (productTitle != mCategoryDetails!!.categoryTitle) {
            userHashMap[Constant.CATEGORY_TITLE] = productTitle
        }



        if (mCategoryImageURL.isNotEmpty()) {
            userHashMap[Constant.CATEGORY_IMAGE] = mCategoryImageURL
        }


        // call the registerUser function of FireStore class to make an entry in the database.
        FirestoreClass().updateCategoryDetails(
            this@AddCategoryActivity,
            userHashMap
        )
    }

    fun updateCategorySuccess() {

        // Hide the progress dialog
        hideProgressDialog()

        Toast.makeText(
            this@AddCategoryActivity,
           "Your Category Successfully updated",
            Toast.LENGTH_SHORT
        ).show()



    }

}