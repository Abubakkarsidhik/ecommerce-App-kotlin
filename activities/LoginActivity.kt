package com.hahnemanntechnology.activities

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.WindowManager
import android.widget.EditText
import com.google.firebase.auth.FirebaseAuth
import com.hahnemanntechnology.R
import com.hahnemanntechnology.firestore.FirestoreClass
import com.hahnemanntechnology.models.UserModel
import com.hahnemanntechnology.utilities.Constant
import kotlinx.android.synthetic.main.activity_login.*

@Suppress("DEPRECATION")
class LoginActivity : BaseActivity(), View.OnClickListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        /*   btn_login.setOnClickListener {
            logInRegisterUser()
        }
*/
        tv_forget_password.setOnClickListener (this)
        btn_login.setOnClickListener (this)
        tv_register.setOnClickListener (this)
    }

    override fun onClick(view: View?) {
        if ( view !=null){
            when(view.id){
                R.id.tv_forget_password ->{

                    val intent = Intent(this@LoginActivity, ForgotPasswordActivity::class.java)
                    startActivity(intent)

                }
                R.id.btn_login ->{

                    logInRegisterUser()
                }
                R.id.tv_register ->{
                    //launch the register screen when the user clicks on the text.
                    val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
                    startActivity(intent)
                }
            }
        }
    }

    private fun validateLoginDetails():Boolean {
        return when{
            TextUtils.isEmpty(et_email.text.toString().trim{ it <= ' '}) ->
            {
                showErrorSnackBar("Please Enter Email ",true)
                false
            }
            TextUtils.isEmpty(et_password.text.toString().trim {it <= ' '}) ->
            {
                showErrorSnackBar("Please Enter Password",true)
                false
            }
            else ->
            {
                true
            }
        }
    }

    private fun logInRegisterUser()
    {
        if (validateLoginDetails())
        {
            showProgressDialog(resources.getString(R.string.plz_wait))

            val email : String =  findViewById<EditText>(R.id.et_email).text.toString().trim{it<=' '}
            val password : String =  findViewById<EditText>(R.id.et_password).text.toString().trim{it<=' '}

            FirebaseAuth.getInstance().signInWithEmailAndPassword(email,password)
                .addOnCompleteListener { task ->
                    hideProgressDialog()
                    if (task.isSuccessful) {
                        showErrorSnackBar("You are Login Successfully" , false)
                        FirestoreClass().getUserDetails(this@LoginActivity)

                    }else
                    {
                        hideProgressDialog()
                        showErrorSnackBar(task.exception!!.message.toString(), true)
                    }
                }
        }
    }

    fun userLoggedInSuccess(user : UserModel)
    {
        //hide the progress dialog
        hideProgressDialog()

        //Redirect the user to Main Activity after log in
        if (user.profileCompleted==0) {
            val intent =Intent(this@LoginActivity, UserProfileActivity::class.java)
            intent.putExtra(Constant.EXTRA_USER_DETAILS, user)
            startActivity(intent )
        }
        else {
            startActivity(Intent(this@LoginActivity , MainActivity::class.java))
        }
        finish()//To close the log in Screen

    }
}












