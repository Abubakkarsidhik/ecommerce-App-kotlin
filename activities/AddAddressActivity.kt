package com.hahnemann.daza2.activities

import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import com.hahnemanntechnology.R
import com.hahnemanntechnology.activities.BaseActivity
import com.hahnemanntechnology.firestore.FirestoreClass
import com.hahnemanntechnology.models.AddressModel
import com.hahnemanntechnology.utilities.Constant
import kotlinx.android.synthetic.main.activity_add_address.*

class AddAddressActivity : BaseActivity() {

    private var mAddressModelDetails: AddressModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_address)

        backArrow_add_address.setOnClickListener { onBackPressed() }

        if (intent.hasExtra(Constant.EXTRA_ADDRESS_DETAILS)) {
            mAddressModelDetails =
                intent.getParcelableExtra(Constant.EXTRA_ADDRESS_DETAILS)!!
        }

        if (mAddressModelDetails != null) {
            if (mAddressModelDetails!!.id.isNotEmpty()) {

                tv_title.text = resources.getString(R.string.title_edit_address)
                btn_submit.text = resources.getString(R.string.btn_lbl_update)

                et_full_name.setText(mAddressModelDetails?.name)
                et_phone_number.setText(mAddressModelDetails?.mobileNumber)
               // et_address.setText(mAddressDetails?.address)
               // et_zip_code.setText(mAddressModelDetails?.zipCode)
              //  et_additional_note.setText(mAddressDetails?.additionalNote)

                when (mAddressModelDetails?.type) {
                    Constant.HOME -> {
                        rb_home.isChecked = true
                    }
                    Constant.OFFICE -> {
                        rb_office.isChecked = true
                    }
                    else -> {
                        rb_other.isChecked = true
                        til_other_details.visibility = View.VISIBLE
                        et_other_details.setText(mAddressModelDetails?.otherDetails)
                    }
                }
            }
        }

        rg_address_type.setOnCheckedChangeListener { _, checkedId ->
            if (checkedId == R.id.rb_other) {
                til_other_details.visibility = View.VISIBLE
            } else {
                til_other_details.visibility = View.GONE
            }
        }

        btn_submit.setOnClickListener {
            saveAddressToFirestore()
        }
    }

    private fun validateData(): Boolean {
        return when {

            TextUtils.isEmpty(et_full_name.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(
                    resources.getString(R.string.err_msg_please_enter_full_name),
                    true
                )
                false
            }

            TextUtils.isEmpty(et_phone_number.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(
                    resources.getString(R.string.err_msg_please_enter_phone_number),
                    true
                )
                false
            }

          //  TextUtils.isEmpty(et_address.text.toString().trim { it <= ' ' }) -> {
          //      showErrorSnackBar(resources.getString(R.string.err_msg_please_enter_address), true)
          //      false
          //  }

            TextUtils.isEmpty(et_zipcode.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_please_enter_zip_code), true)
                false
            }

            rb_other.isChecked && TextUtils.isEmpty(
                et_zipcode.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_please_enter_zip_code), true)
                false
            }
            else -> {
                true
            }
        }
    }

    private fun saveAddressToFirestore() {

        // Here we get the text from editText and trim the space
        val fullName: String = et_full_name.text.toString().trim { it <= ' ' }
        val phoneNumber: String = et_phone_number.text.toString().trim { it <= ' ' }
      //  val address: String = et_address.text.toString().trim { it <= ' ' }
        val zipCode: String = et_zipcode.text.toString().trim { it <= ' ' }
        val flatNo :String = et_flatNo_houseNo.text.toString().trim{ it <= ' '}
        val area :String = et_roadName.text.toString().trim{ it <= ' '}
        val landmark : String = et_landmark.text.toString().trim{ it <= ' '}
        val town :String = et_city.text.toString().trim{ it <= ' '}
        //val additionalNote: String = et_additional_note.text.toString().trim { it <= ' ' }
        val otherDetails: String = et_other_details.text.toString().trim { it <= ' ' }

        if (validateData()) {

            // Show the progress dialog.
            showProgressDialog(resources.getString(R.string.plz_wait))

            val addressType: String = when {
                rb_home.isChecked -> {
                    Constant.HOME
                }
                rb_office.isChecked -> {
                    Constant.OFFICE
                }
                else -> {
                    Constant.OTHER
                }
            }

            val addressModel = AddressModel(
                FirestoreClass().getCurrentUserID(),
                fullName,
                phoneNumber,
                //address,
                zipCode,
                flatNo,
                area,
                landmark,
                town,
                //additionalNote,
                addressType,
                otherDetails
            )

            if (mAddressModelDetails != null && mAddressModelDetails!!.id.isNotEmpty()) {
                FirestoreClass().updateAddress(
                    this@AddAddressActivity,
                    addressModel,
                    mAddressModelDetails!!.id
                )
            } else {
                FirestoreClass().addAddress(this@AddAddressActivity, addressModel)
            }
        }
    }

    fun addUpdateAddressSuccess() {
        // Hide progress dialog
        hideProgressDialog()

        val notifySuccessMessage: String = if (mAddressModelDetails != null && mAddressModelDetails!!.id.isNotEmpty()) {
            resources.getString(R.string.msg_your_address_updated_successfully)
        } else {
            resources.getString(R.string.err_your_address_added_successfully)
        }

        Toast.makeText(
            this@AddAddressActivity,
            notifySuccessMessage,
            Toast.LENGTH_SHORT
        ).show()

        setResult(RESULT_OK)
        finish()
    }

}