package com.hahnemanntechnology.activities

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.hahnemanntechnology.R
import com.hahnemanntechnology.firestore.FirestoreClass
import com.hahnemanntechnology.models.ProductModels
import com.hahnemanntechnology.utilities.Constant
import com.hahnemann.daza2.utilities.GlideLoader
import kotlinx.android.synthetic.main.activity_add_products.*
import java.io.IOException

@Suppress("DEPRECATION")
class AddProductsActivity : BaseActivity(), View.OnClickListener {
    // A global variable for URI of a selected image from phone storage.
    private var mSelectedImageFileUri: Uri? = null

    // A global variable for uploaded product image URL.
    private var mProductImageURL: String = ""

    private  var mProductDetails :ProductModels? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_products)

        //To Get Image
        val getImage = registerForActivityResult(
            ActivityResultContracts.GetContent() , ActivityResultCallback {
                iv_add_product_image.setImageURI(it)
                mSelectedImageFileUri=it
                GlideLoader(this).loadUserPicture(mSelectedImageFileUri!!,iv_add_product_image)
            }
        )




        // Assign the click event to submit button.
        btn_submit.setOnClickListener(this)
        backArrow_add_products.setOnClickListener { onBackPressed() }
        addProducts_cancel_btn.setOnClickListener { onBackPressed() }
        iv_add_new_product.setOnClickListener { getImage.launch("image/*") }

        if (intent.hasExtra(Constant.EXTRA_PRODUCT_DETAILS)) {
            // Get the user details from intent as a ParcelableExtra.
            mProductDetails = intent.getParcelableExtra(Constant.EXTRA_PRODUCT_DETAILS)!!

        }
        if (mProductDetails != null) {
            if (mProductDetails!!.product_id.isNotEmpty()) {


                tv_title.text = resources.getString(R.string.title_edit_products)
                btn_submit.text = resources.getString(R.string.btn_lbl_update)

                mProductDetails?.productImage?.let {
                    GlideLoader(this@AddProductsActivity).loadProductPicture(
                        it ,
                        iv_add_product_image
                    )
                }
                et_product_title.setText(mProductDetails!!.productTitle)
                et_product_description.setText(mProductDetails!!.productDescription)
                et_product_price.setText(mProductDetails!!.productPrice)
                et_product_category.setText(mProductDetails!!.stockCategory)
                et_product_quantity.setText(mProductDetails!!.stockQuantity)


            }
        }



    }

    override fun onClick(v: View?) {

        if (v != null) {
            when (v.id) {

                // The permission code is similar to the user profile image selection.
                R.id.iv_add_new_product -> {
                    if (ContextCompat.checkSelfPermission(
                            this,
                            Manifest.permission.READ_EXTERNAL_STORAGE
                        )
                        == PackageManager.PERMISSION_GRANTED
                    ) {
                        Constant.showImageChooser(this@AddProductsActivity)
                    } else {
                        /*Requests permissions to be granted to this application. These permissions
                         must be requested in your manifest, they should not be granted to your app,
                         and they should have protection level*/
                        ActivityCompat.requestPermissions(
                            this,
                            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                            Constant.READ_STORAGE_PERMISSION_CODE
                        )
                    }
                }

                R.id.btn_submit -> {

                    if (validateProductDetails()) {
                        uploadProductImage()
                    }
                    else if (validateProductDetails())
                    {
                        updateProductDetails()
                    }



                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == Constant.READ_STORAGE_PERMISSION_CODE) {
            //If permission is granted
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Constant.showImageChooser(this@AddProductsActivity)
            } else {
                //Displaying another toast if permission is not granted
                Toast.makeText(
                    this,
                    resources.getString(R.string.read_storage_permission_denied),
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int , resultCode: Int , data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK
            && requestCode == Constant.PICK_IMAGE_REQUEST_CODE
            && data!!.data != null
        ) {

            // Replace the add icon with edit icon once the image is selected.
            iv_add_new_product.setImageDrawable(
                ContextCompat.getDrawable(
                    this@AddProductsActivity,
                    R.drawable.ic_vector_edit
                )
            )

            // The uri of selection image from phone storage.
            mSelectedImageFileUri = data.data!!

            try {
                // Load the product image in the ImageView.
                GlideLoader(this@AddProductsActivity).loadProductPicture(
                    mSelectedImageFileUri!!,
                    iv_add_product_image
                )
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun validateProductDetails(): Boolean {
        return when {

            TextUtils.isEmpty(et_product_title.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_product_title), true)
                false
            }

            TextUtils.isEmpty(et_product_description.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(
                    resources.getString(R.string.err_msg_enter_product_description),
                    true
                )
                false
            }
            TextUtils.isEmpty(et_product_category.text.toString().trim{it <=' '}) ->
            {
                showErrorSnackBar(resources.getString(R.string.err_plz_enter_product_category),true)
                false
            }
            TextUtils.isEmpty(et_product_price.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_product_price), true)
                false
            }

            TextUtils.isEmpty(et_product_quantity.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(
                    resources.getString(R.string.err_msg_enter_product_quantity),
                    true
                )
                false
            }
            mSelectedImageFileUri == null -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_select_product_image), true)
                false
            }


            else -> {
                true
            }
        }
    }


    private fun uploadProductImage() {
        showProgressDialog(resources.getString(R.string.please_wait))

        FirestoreClass().uploadImageToCloudStorage(
            this@AddProductsActivity,
            mSelectedImageFileUri,
            Constant.PRODUCT_IMAGE
        )

    }
    fun imageUploadSuccess(imageURL: String) {

        // Initialize the global image url variable.
        mProductImageURL = imageURL

        uploadProductDetails()
    }


    fun productUploadSuccess() {

        // Hide the progress dialog
        hideProgressDialog()

        Toast.makeText(
            this@AddProductsActivity,
            resources.getString(R.string.product_uploaded_success_message),
            Toast.LENGTH_SHORT
        ).show()

        finish()
    }
    private fun uploadProductDetails() {

        // Get the logged in username from the SharedPreferences that we have stored at a time of login.
        val username =
            this.getSharedPreferences(Constant.DAZA_PREFERENCES, Context.MODE_PRIVATE)
                .getString(Constant.LOGGED_IN_USERNAME, "")!!

        // Here we get the text from editText and trim the space
        val product = ProductModels(
            FirestoreClass().getCurrentUserID(),
            username,
            et_product_title.text.toString().trim { it <= ' ' },
            et_product_description.text.toString().trim { it <= ' ' },
            et_product_category.text.toString().trim{ it <= ' '},
            et_product_price.text.toString().trim { it <= ' ' },
            et_product_quantity.text.toString().trim { it <= ' ' },
            mProductImageURL
        )

        FirestoreClass().uploadProductDetails(this@AddProductsActivity, product)
    }


    private fun updateProductDetails() {

        val userHashMap = HashMap<String, Any>()

        // Get the FirstName from editText and trim the space
        val productTitle = et_product_title.text.toString().trim { it <= ' ' }
        if (productTitle != mProductDetails!!.productTitle) {
            userHashMap[Constant.PRODUCT_TITLE] = productTitle
        }

        // Get the LastName from editText and trim the space
        val productDescription = et_product_description.text.toString().trim { it <= ' ' }
        if (productDescription != mProductDetails!!.productDescription) {
            userHashMap[Constant.PRODUCT_DESCRIPTION] = productDescription
        }

        // Here we get the text from editText and trim the space
        val productCategory = et_product_category.text.toString().trim { it <= ' ' }
        if (productCategory != mProductDetails!!.stockCategory) {
            userHashMap[Constant.PRODUCT_CATEGORY] = productCategory
        }

        val productPrize = et_product_price.text.toString().trim { it <= ' ' }
        if (productPrize != mProductDetails!!.productPrice) {
            userHashMap[Constant.PRODUCT_PRICE] = productPrize
        }

        val productQuantity = et_product_price.text.toString().trim { it <= ' ' }
        if (productQuantity != mProductDetails!!.stockQuantity) {
            userHashMap[Constant.PRODUCT_QUANTITY] = productQuantity
        }

        if (mProductImageURL.isNotEmpty()) {
            userHashMap[Constant.PRODUCT_IMAGE] = mProductImageURL
        }


        // call the registerUser function of FireStore class to make an entry in the database.
        FirestoreClass().updateProductDetails(
            this@AddProductsActivity,
            userHashMap
        )
    }

    fun updateProductSuccess() {

        // Hide the progress dialog
        hideProgressDialog()

        Toast.makeText(
            this@AddProductsActivity,
            resources.getString(R.string.msg_product_update_success),
            Toast.LENGTH_SHORT
        ).show()



    }

/* private fun MySpinner(){
         // Create an ArrayAdapter
         val adapter = ArrayAdapter.createFromResource(this, R.array.categories, android.R.layout.simple_spinner_item)

         // Specify the layout to use when the list of choices appears
         adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
         // Apply the adapter to the spinner
         spinner.adapter = adapter
     }*/



}