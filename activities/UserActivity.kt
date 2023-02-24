package com.hahnemanntechnology.activities

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.RelativeLayout
import androidx.cardview.widget.CardView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.hahnemann.daza2.activities.AccessoriesActivity
import com.hahnemanntechnology.R
import com.hahnemanntechnology.firestore.FirestoreClass
import com.hahnemanntechnology.models.UserModel
import com.hahnemanntechnology.utilities.Constant
import com.hahnemann.daza2.utilities.GlideLoader
import kotlinx.android.synthetic.main.activity_user.*


@Suppress("DEPRECATION")
class UserActivity : BaseActivity(), View.OnClickListener {

    private lateinit var mUserDetails:UserModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user)

        //Bottom Navigation Bar
        bottomNav()


        findViewById<CardView>(R.id.cardView_edit_profile).setOnClickListener(this@UserActivity)
       findViewById<RelativeLayout>(R.id.tv_profile_address).setOnClickListener(this@UserActivity)
        findViewById<RelativeLayout>(R.id.tv_profile_order).setOnClickListener(this@UserActivity)
        tv_profile_logout.setOnClickListener(this@UserActivity)





    }
    override fun onResume() {
        super.onResume()
        getUserDetails()
    }
    override fun onClick(v: View?) {
        if (v != null) {
            when (v.id) {

                R.id.cardView_edit_profile -> {
                    val intent = Intent(this@UserActivity, UserProfileActivity::class.java)
                    intent.putExtra(Constant.EXTRA_USER_DETAILS, mUserDetails)
                    startActivity(intent)
                }

               R.id.tv_profile_address -> {
                    val intent = Intent(this@UserActivity, AddressActivity::class.java)
                    startActivity(intent)
                }
                R.id.tv_profile_order -> {
                    val intent = Intent(this@UserActivity, OrderActivity::class.java)
                    startActivity(intent)
                }

                R.id.tv_profile_logout -> {
                    showAlertDialogToDeleteCategory()
                }
            }
        }
    }

    private fun getUserDetails() {

        // Show the progress dialog
        showProgressDialog(resources.getString(R.string.plz_wait))

        // Call the function of Firestore class to get the user details from firestore which is already created.
        FirestoreClass().getUserDetails(this@UserActivity)
    }

    @SuppressLint("SetTextI18n")
    fun userDetailsSuccess(user: UserModel) {

        mUserDetails = user

        // Hide the progress dialog
        hideProgressDialog()

        // Load the image using the Glide Loader class.
        GlideLoader(this@UserActivity).loadUserPicture(user.image, iv_show_profile)

        profile_name.text = "${user.firstName} ${user.lastName}"
        profile_email.text = user.email
        profile_number.text = "${user.mobile}"
    }

    private fun bottomNav() {
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        // Set Home selected

        // Set Home selected
        bottomNavigationView.selectedItemId = R.id.user

        // Perform item selected listener

        // Perform item selected listener
        bottomNavigationView.setOnNavigationItemSelectedListener(BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.user -> return@OnNavigationItemSelectedListener true
                R.id.repair_service -> {
                    startActivity(Intent(applicationContext , RepairServicesActivity::class.java))
                    overridePendingTransition(0 , 0)
                    return@OnNavigationItemSelectedListener true
                }

                R.id.accessories -> {
                    startActivity(Intent(applicationContext , AccessoriesActivity::class.java))
                    overridePendingTransition(0 , 0)
                    return@OnNavigationItemSelectedListener true
                }
                R.id.home -> {
                    startActivity(Intent(applicationContext , MainActivity::class.java))
                    overridePendingTransition(0 , 0)
                    return@OnNavigationItemSelectedListener true
                }
            }
            false
        })

    }



    private fun showAlertDialogToDeleteCategory() {

        val builder = AlertDialog.Builder(this)
        //set title for alert dialog
        builder.setTitle(resources.getString(R.string.quit))
        //Set msg for alert dialog
        builder.setMessage(resources.getString(R.string.Are_you_sure_you_want_to_exit))
        builder.setIcon(android.R.drawable.ic_dialog_alert)

        //performing positive action
        builder.setPositiveButton(resources.getString(R.string.yes))
        {
                dialogInterface,_ ->
           // showProgressDialog(resources.getString(R.string.plz_wait))
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(this@UserActivity, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
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


