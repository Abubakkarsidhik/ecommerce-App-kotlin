package com.hahnemanntechnology.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Paint
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.hahnemanntechnology.R
import com.hahnemanntechnology.adapters.*
import com.hahnemanntechnology.firestore.FirestoreClass
import com.hahnemanntechnology.models.CartModel
import com.hahnemanntechnology.models.ProductModels
import com.hahnemanntechnology.utilities.Constant
import com.hahnemann.daza2.utilities.GlideLoader
import kotlinx.android.synthetic.main.activity_manage_category.*
import kotlinx.android.synthetic.main.activity_product_details.*
import kotlinx.android.synthetic.main.activity_repair_services.*

class ProductDetailsActivity : BaseActivity(), View.OnClickListener {

    private lateinit var mProductDetails: ProductModels
    private var i = 0
    // A global variable for product id.
    private var mProductId: String = ""

    /**
     * This function is auto created by Android when the Activity Class is created.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        //This call the parent constructor
        super.onCreate(savedInstanceState)
        // This is used to align the xml view to this class
        setContentView(R.layout.activity_product_details)

        if (intent.hasExtra(Constant.EXTRA_PRODUCT_ID)) {
            mProductId =
                intent.getStringExtra(Constant.EXTRA_PRODUCT_ID)!!
        }

        var productOwnerId: String = ""

        if (intent.hasExtra(Constant.EXTRA_PRODUCT_OWNER_ID)) {
            productOwnerId =
                intent.getStringExtra(Constant.EXTRA_PRODUCT_OWNER_ID)!!
        }



        btn_add_to_cart.setOnClickListener(this)
        btn_buy_now.setOnClickListener(this)
        btn_backArrow.setOnClickListener(this)
        getProductDetails()
    }

    override fun onClick(v: View?) {
        if (v != null) {
            when (v.id) {

                R.id.btn_add_to_cart -> {
                    if (i == 0) {
                        btn_add_to_cart.text = "Go to Cart"
                        addToCart()
                        i++
                    }
                    else if (i == 1) {
                        btn_add_to_cart.text = "Go to Cart"
                        startActivity(Intent(this@ProductDetailsActivity, CartListActivity::class.java))
                        i=0
                    }
                }

                R.id.btn_backArrow ->{
                    onBackPressed()
                }
                R.id.btn_buy_now->{
                    addToCart()
                    startActivity(Intent(this@ProductDetailsActivity, CartListActivity::class.java))
                }

            }
        }
    }


    private fun addToCart() {

        val addToCart = CartModel(
            FirestoreClass().getCurrentUserID(),
            mProductId,
            mProductDetails.productTitle,
            mProductDetails.productPrice,
            mProductDetails.productImage,
            Constant.DEFAULT_CART_QUANTITY
        )


        // Show the progress dialog
        showProgressDialog(resources.getString(R.string.please_wait))

        FirestoreClass().addCartItems(this@ProductDetailsActivity, addToCart)

    }


    private fun getProductDetails() {

        // Show the product dialog
        showProgressDialog(resources.getString(R.string.please_wait))

        // Call the function of FirestoreClass to get the product details.
        FirestoreClass().getProductDetails(this@ProductDetailsActivity, mProductId)
    }

    @SuppressLint("SetTextI18n")
    fun productDetailsSuccess(product: ProductModels) {

        mProductDetails = product

        // Populate the product details in the UI.
        GlideLoader(this@ProductDetailsActivity).loadProductPicture(
            product.productImage,
            iv_product_detail_image
        )

        tv_product_details_title.text = product.productTitle
        tv_product_details_price.text = "$${product.productPrice}"
        tv_product_details_description.text = product.productDescription
        tv_product_quantity.text = "Quantity(${product.stockQuantity})"
        val price = product.productPrice.toDouble()
        val total = price + 9 * 100/10
        tv_strikethrough_prize.apply {
            paintFlags = paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            text = "$$total"
        }




  /*    if (product.stockQuantity.toInt() >=1){
          tv_product_details_stock_quantity.text = "In Stock"
      }
      else */ if(product.stockQuantity.toInt() == 0){

            // Hide Progress dialog.
            hideProgressDialog()

            // Hide the AddToCart button if the item is already in the cart.
            btn_add_to_cart.visibility= View.GONE
            btn_buy_now.visibility= View.GONE

            tv_product_details_stock_quantity.visibility = View.VISIBLE
            tv_product_details_stock_quantity.text =
                resources.getString(R.string.lbl_out_of_stock)


        }else{


            // There is no need to check the cart list if the product owner himself is seeing the product details.
            if (FirestoreClass().getCurrentUserID() == product.user_id) {
                // Hide Progress dialog.
                hideProgressDialog()

            }
            else {
                FirestoreClass().checkIfItemExistInCart(this@ProductDetailsActivity , mProductId )
            }
        }
    }

    fun productExistsInCart() {

        // Hide the progress dialog.
        hideProgressDialog()

        // Hide the AddToCart button if the item is already in the cart.
        //btn_add_to_cart.visibility = View.GONE
        // Show the GoToCart button if the item is already in the cart. User can update the quantity from the cart list screen if he wants.
        //btn_go_to_cart.visibility = View.VISIBLE
    }

    fun addToCartSuccess() {
        // Hide the progress dialog.
        hideProgressDialog()

        Toast.makeText(
            this@ProductDetailsActivity,
            resources.getString(R.string.success_message_item_added_to_cart),
            Toast.LENGTH_SHORT
        ).show()

        // Hide the AddToCart button if the item is already in the cart.
        btn_add_to_cart.text = "Go to Cart"
        // Show the GoToCart button if the item is already in the cart. User can update the quantity from the cart list screen if he wants.
       // btn_go_to_cart.visibility = View.VISIBLE
    }

     override fun onResume() {
        super.onResume()
        //  getAdvertPanelListFromFireStore()

       getRepairServiceActivityProductItemsList()
    }
    private fun getRepairServiceActivityProductItemsList() {
        // Show the progress dialog.
        // showProgressDialog(resources.getString(R.string.please_wait))

        FirestoreClass().getRepairServiceProductItemsList(this@ProductDetailsActivity)
    }

    fun successRepairServiceProductItemsList(categoryItemsList: ArrayList<ProductModels>) {

        // Hide the progress dialog.
        hideProgressDialog()

        rv_products_details.layoutManager = LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false,)
        rv_products_details.setHasFixedSize(true)

        val adapter = ProductDetailsAdapter(this, categoryItemsList,this@ProductDetailsActivity)
        rv_products_details.adapter = adapter



    }
}