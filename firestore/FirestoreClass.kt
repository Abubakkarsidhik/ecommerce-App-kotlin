package com.hahnemanntechnology.firestore

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.hahnemann.daza2.activities.AccessoriesActivity
import com.hahnemann.daza2.activities.AddAddressActivity
import com.hahnemann.daza2.activities.AddAdvertPanelActivity
import com.hahnemanntechnology.activities.UserProfileActivity
import com.hahnemanntechnology.activities.*
import com.hahnemanntechnology.models.*
import com.hahnemanntechnology.utilities.Constant


class FirestoreClass {

    private val mFireStore = FirebaseFirestore.getInstance()
    val db = Firebase.firestore

    fun registerUser(activity: RegisterActivity , userInfo: UserModel) {
        //The "users" is collection name. If the collection is already created then it will not create the same
        mFireStore.collection(Constant.USERS)
            .document(userInfo.id)
            .set(userInfo , SetOptions.merge())
            .addOnSuccessListener {
                activity.userRegistrationSuccess()
            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e(
                    activity.javaClass.simpleName ,
                    "Error while registering the user." , e
                )

            }
    }
    fun getCurrentUserID(): String {

        val currentUser = FirebaseAuth.getInstance().currentUser

        var currentUserID = ""
        if (currentUser != null) {
            currentUserID = currentUser.uid
        }
        return currentUserID
    }

    fun getUserDetails(activity: Activity) {

        // Here we pass the collection name from which we wants the data.
        mFireStore.collection(Constant.USERS)
            // The document id to get the Fields of user.
            .document(getCurrentUserID())
            .get()
            .addOnSuccessListener { document ->

                Log.i(activity.javaClass.simpleName, document.toString())

                // Here we have received the document snapshot which is converted into the User Data model object.
                val user = document.toObject(UserModel::class.java)!!

                val sharedPreferences =
                    activity.getSharedPreferences(
                        Constant.DAZA_PREFERENCES,
                        Context.MODE_PRIVATE
                    )

                // Create an instance of the editor which is help us to edit the SharedPreference.
                val editor: SharedPreferences.Editor = sharedPreferences.edit()
                editor.putString(
                    Constant.LOGGED_IN_USERNAME,
                    "${user.firstName} ${user.lastName}"
                )
                editor.apply()

                when (activity) {
                    is LoginActivity -> {
                        // Call a function of base activity for transferring the result to it.
                        activity.userLoggedInSuccess(user)
                    }

                    is UserActivity -> {
                        // Call a function of base activity for transferring the result to it.
                        activity.userDetailsSuccess(user)
                    }
                }
            }
        /*    .addOnFailureListener { e ->
                // Hide the progress dialog if there is any error. And print the error in log.
                when (activity) {
                    is LoginActivity -> {
                        activity.hideProgressDialog()
                    }
                    is UserActivity -> {
                       activity.hideProgressDialog()
                    }
                }

                Log.e(
                    activity.javaClass.simpleName,
                    "Error while getting user details.",
                    e
                )
            }   */
    }


    fun uploadImageToCloudStorage(activity: Activity , imageFileURI: Uri? , imageType: String) {
        //Not Working

        val sRef: StorageReference = FirebaseStorage.getInstance().reference.child(
            imageType + System.currentTimeMillis() + "." + Constant.getFileExtension(
                activity ,
                imageFileURI
            )
        )


        sRef.putFile(imageFileURI!!).addOnSuccessListener { taskSnapshot ->
            //The image upload is Success
         //   Toast.makeText(activity,"The image upload successfully", Toast.LENGTH_SHORT).show()

            Log.e(
                "Firebase Image URL" ,
                taskSnapshot.metadata!!.reference!!.downloadUrl.toString()
            )
            taskSnapshot.metadata!!.reference!!.downloadUrl
                .addOnSuccessListener { uri ->
                    Log.e("Downloadable Image URL" , uri.toString())
                    when (activity) {
                        is AddCategoryActivity -> {
                            activity.imageUploadSuccess(uri.toString())
                        }
                        is AddProductsActivity -> {
                            activity.imageUploadSuccess(uri.toString())
                        }
                        is AddAdvertPanelActivity -> {
                            activity.imageUploadSuccess(uri.toString())
                        }
                        is UserProfileActivity -> {
                            activity.imageUploadSuccess(uri.toString())
                        }


                    }

                }
        }
            .addOnFailureListener { exception ->
                //Hide the progress dialog if there is any error.And print the error in log
                when (activity) {
                    is AddProductsActivity -> {
                        activity.hideProgressDialog()
                    }

                    is AddCategoryActivity -> {
                        activity.hideProgressDialog()
                    }
                    is AddAdvertPanelActivity -> {
                        activity.hideProgressDialog()
                    }
                    is UserProfileActivity -> {
                        activity.hideProgressDialog()
                    }

                }
                Log.e(
                    activity.javaClass.simpleName ,
                    exception.message ,
                    exception
                )

            }
    }

    fun uploadCategoryDetails(activity: AddCategoryActivity , categoryInfo: CategoryModels) {
        mFireStore.collection(Constant.CATEGORY)
            .document()
            .set(categoryInfo , SetOptions.merge())
            .addOnSuccessListener {
                activity.categoryUploadSuccess()
            }
            .addOnFailureListener { e ->
                 activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName , "Error while uploading product details" , e)
            }
    }
    fun uploadProductDetails(activity: AddProductsActivity , productModelsInfo: ProductModels) {
        mFireStore.collection(Constant.PRODUCTS)
            .document()
            .set(productModelsInfo , SetOptions.merge())
            .addOnSuccessListener {
                activity.productUploadSuccess()
            }
            .addOnFailureListener { e ->
                 activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName , "Error while uploading product details" , e)
            }
    }
    fun uploadAdvertPanelDetails(activity: AddAdvertPanelActivity, advertInfo: AdvertPanelModels) {
        mFireStore.collection(Constant.ADVERT_PANEL)
            .document()
            .set(advertInfo , SetOptions.merge())
            .addOnSuccessListener {
                activity.advertPanelUploadSuccess()
            }
            .addOnFailureListener { e ->
                 activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName , "Error while uploading product details" , e)
            }
    }

    fun addAddress(activity: AddAddressActivity, addressModelInfo: AddressModel) {

        // Collection name address.
        mFireStore.collection(Constant.ADDRESSES)
            .document()
            // Here the userInfo are Field and the SetOption is set to merge. It is for if we wants to merge
            .set(addressModelInfo, SetOptions.merge())
            .addOnSuccessListener {
                // Here call a function of base activity for transferring the result to it.
                activity.addUpdateAddressSuccess()

            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while adding the address.",
                    e
                )
            }
    }

    fun updateUserProfileData(activity: Activity, userHashMap: HashMap<String, Any>) {
        // Collection Name
        mFireStore.collection(Constant.USERS)
            // Document ID against which the data to be updated. Here the document id is the current logged in user id.
            .document(getCurrentUserID())
            // A HashMap of fields which are to be updated.
            .update(userHashMap)
            .addOnSuccessListener {

                // Notify the success result.
                when (activity) {
                    is UserProfileActivity -> {
                        // Call a function of base activity for transferring the result to it.
                        activity.userProfileUpdateSuccess()
                    }
                }
            }
            .addOnFailureListener { e ->

                when (activity) {
                    is UserProfileActivity -> {
                        // Hide the progress dialog if there is any error. And print the error in log.
                        activity.hideProgressDialog()
                    }
                }

                Log.e(
                    activity.javaClass.simpleName,
                    "Error while updating the user details.",
                    e
                )
            }
    }
    fun updateAddress(activity: AddAddressActivity, addressModelInfo: AddressModel, addressId: String) {

        mFireStore.collection(Constant.ADDRESSES)
            .document(addressId)
            // Here the userInfo are Field and the SetOption is set to merge. It is for if we wants to merge
            .set(addressModelInfo, SetOptions.merge())
            .addOnSuccessListener {

                // Here call a function of base activity for transferring the result to it.
                activity.addUpdateAddressSuccess()
            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while updating the Address.",
                    e
                )
            }
    }


    fun updateCategoryDetails(activity: Activity, userHashMap: HashMap<String, Any>) {
        // Collection Name
        mFireStore.collection(Constant.CATEGORY)
            // Document ID against which the data to be updated. Here the document id is the current logged in user id.
            .document(getCurrentUserID())
            // A HashMap of fields which are to be updated.
            .update(userHashMap)
            .addOnSuccessListener {

                // Notify the success result.
                when (activity) {
                    is AddCategoryActivity -> {
                        // Call a function of base activity for transferring the result to it.
                        activity.updateCategorySuccess()
                    }
                }
            }
            .addOnFailureListener { e ->

                when (activity) {
                    is AddCategoryActivity -> {
                        // Hide the progress dialog if there is any error. And print the error in log.
                        activity.hideProgressDialog()
                    }
                }

                Log.e(
                    activity.javaClass.simpleName,
                    "Error while updating the user details.",
                    e
                )
            }
    }
    fun updateProductDetails(activity: Activity, userHashMap: HashMap<String, Any>) {
        // Collection Name
        mFireStore.collection(Constant.PRODUCTS)
            // Document ID against which the data to be updated. Here the document id is the current logged in user id.
            .document(getCurrentUserID())
            // A HashMap of fields which are to be updated.
            .update(userHashMap)
            .addOnSuccessListener {

                // Notify the success result.
                when (activity) {
                    is AddProductsActivity -> {
                        // Call a function of base activity for transferring the result to it.
                        activity.updateProductSuccess()
                    }
                }
            }
            .addOnFailureListener { e ->

                when (activity) {
                    is AddProductsActivity -> {
                        // Hide the progress dialog if there is any error. And print the error in log.
                        activity.hideProgressDialog()
                    }
                }

                Log.e(
                    activity.javaClass.simpleName,
                    "Error while updating the user details.",
                    e
                )
            }
    }

    fun updateAdvertPanelDetails(activity: Activity, userHashMap: HashMap<String, Any>) {
        // Collection Name
        mFireStore.collection(Constant.ADVERT_PANEL)
            // Document ID against which the data to be updated. Here the document id is the current logged in user id.
            .document(getCurrentUserID())
            // A HashMap of fields which are to be updated.
            .update(userHashMap)
            .addOnSuccessListener {

                // Notify the success result.
                when (activity) {
                    is AddAdvertPanelActivity -> {
                        // Call a function of base activity for transferring the result to it.
                        activity.updateAdvertPanelSuccess()
                    }
                }
            }
            .addOnFailureListener { e ->

                when (activity) {
                    is AddAdvertPanelActivity -> {
                        // Hide the progress dialog if there is any error. And print the error in log.
                        activity.hideProgressDialog()
                    }
                }

                Log.e(
                    activity.javaClass.simpleName,
                    "Error while updating the user details.",
                    e
                )
            }
    }

    fun getAddressesList(activity: AddressActivity) {
        // The collection name for PRODUCTS
        mFireStore.collection(Constant.ADDRESSES)
            .whereEqualTo(Constant.USER_ID, getCurrentUserID())
            .get() // Will get the documents snapshots.
            .addOnSuccessListener { document ->
                // Here we get the list of boards in the form of documents.
                Log.e(activity.javaClass.simpleName, document.documents.toString())
                // Here we have created a new instance for address ArrayList.
                val addressModelList: ArrayList<AddressModel> = ArrayList()

                // A for loop as per the list of documents to convert them into Boards ArrayList.
                for (i in document.documents) {

                    val addressModel = i.toObject(AddressModel::class.java)!!
                    addressModel.id = i.id

                    addressModelList.add(addressModel)
                }

                activity.successAddressListFromFirestore(addressModelList)
            }
            .addOnFailureListener { e ->
                // Here call a function of base activity for transferring the result to it.

                activity.hideProgressDialog()

                Log.e(activity.javaClass.simpleName, "Error while getting the address list.", e)
            }
    }
    fun getCategoryList(activity: Activity) {
      mFireStore.collection(Constant.CATEGORY)
          .whereEqualTo(Constant.USER_ID, getCurrentUserID())
          .get()
          .addOnSuccessListener { document ->
              Log.e("Category List", document.documents.toString())
              val categoryList: ArrayList<CategoryModels> = ArrayList()
              for (i in document.documents) {
                  val category = i.toObject(CategoryModels::class.java)
                  category!!.category_Id = i.id

                  categoryList.add(category)
              }
              when (activity) {
                  is ManageCategoryActivity -> {
                      activity.successCategoryListFromFireStore(categoryList)
                  }
              }

          }
          .addOnFailureListener { e ->
              // Here call a function of base activity for transferring the result to it.
              when (activity) {
                  is ManageCategoryActivity -> {
                      activity.hideProgressDialog()
                  }
              }

              Log.e(activity.javaClass.simpleName, "Error while getting the category list.", e)
          }

  }
    fun getProductsList(activity: Activity) {
        mFireStore.collection(Constant.PRODUCTS)
            .whereEqualTo(Constant.USER_ID, getCurrentUserID())
            .get()
            .addOnSuccessListener { document ->
                Log.e("Product List", document.documents.toString())
                val productsList: ArrayList<ProductModels> = ArrayList()
                for (i in document.documents) {
                    val product = i.toObject(ProductModels::class.java)
                    product!!.product_id = i.id

                    productsList.add(product)
                }
                when (activity) {
                    is ManageProductsActivity -> {
                        activity.successProductsListFromFireStore(productsList)
                    }
                }
            }
            .addOnFailureListener { e ->
                // Here call a function of base activity for transferring the result to it.
                when (activity) {
                    is ManageProductsActivity -> {
                        activity.hideProgressDialog()
                    }
                }

                Log.e(activity.javaClass.simpleName, "Error while getting the product list.", e)
            }

    }
    fun getAdvertPanelList(activity: Activity) {
        mFireStore.collection(Constant.ADVERT_PANEL)
            .whereEqualTo(Constant.USER_ID, getCurrentUserID())
            .get()
            .addOnSuccessListener { document ->
                Log.e("AdvertPanel List", document.documents.toString())
                val advertPanelList: ArrayList<AdvertPanelModels> = ArrayList()
                for (i in document.documents) {
                    val advertPanel = i.toObject(AdvertPanelModels::class.java)
                    advertPanel!!.advert_Id = i.id

                    advertPanelList.add(advertPanel)
                }
                when (activity) {
                    is ManageAdvertPanelActivity -> {
                        activity.successAdvertPanelListFromFireStore(advertPanelList)
                    }
                    is MainActivity -> {
                      //  activity.successAdvertPanelListFromFireStore(advertPanelList)
                    }
                }
            }
          /*   .addOnFailureListener { e ->
                // Here call a function of base activity for transferring the result to it.
                when (activity) {
                    is ManageAdvertPanelActivity -> {
                        activity.hideProgressDialog()
                    }
                    is MainActivity -> {
                        activity.hideProgressDialog()
                }

            }

                Log.e(activity.javaClass.simpleName, "Error while getting the advertPanel.", e)
            }  */

    }

    fun getMainActivityItemsList(activity: MainActivity) {
        // The collection name for PRODUCTS
        mFireStore.collection(Constant.PRODUCTS)
            .get() // Will get the documents snapshots.
            .addOnSuccessListener { document ->

                // Here we get the list of boards in the form of documents.
                Log.e(activity.javaClass.simpleName, document.documents.toString())

                // Here we have created a new instance for Products ArrayList.
                val productsList: ArrayList<ProductModels> = ArrayList()

                // A for loop as per the list of documents to convert them into Products ArrayList.
                for (i in document.documents) {

                    val product = i.toObject(ProductModels::class.java)!!
                    product.product_id = i.id
                    productsList.add(product)
                }

                // Pass the success result to the base fragment.
                activity.successMainActivityItemsList(productsList)
            }
            .addOnFailureListener { e ->
                // Hide the progress dialog if there is any error which getting the dashboard items list.
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName, "Error while getting dashboard items list.", e)
            }
    }

    fun getRepairServiceCategoryItemsList(activity: Activity) {
        // The collection name for PRODUCTS
        mFireStore.collection(Constant.CATEGORY)
            .get() // Will get the documents snapshots.
            .addOnSuccessListener { document ->

                // Here we get the list of boards in the form of documents.
                Log.e(activity.javaClass.simpleName, document.documents.toString())

                // Here we have created a new instance for Products ArrayList.
                val productsList: ArrayList<CategoryModels> = ArrayList()

                // A for loop as per the list of documents to convert them into Products ArrayList.
                for (i in document.documents) {

                    val product = i.toObject(CategoryModels::class.java)!!
                    product.category_Id = i.id
                    productsList.add(product)
                }

                when(activity){
                    is RepairServicesActivity->{
                        activity.successRepairServiceItemsList(productsList)
                    }
                    is AccessoriesActivity ->{
                        activity.successRepairServiceItemsList(productsList)

                    }
            }
                // Pass the success result to the base fragment.

            }
            .addOnFailureListener { e ->
                // Hide the progress dialog if there is any error which getting the dashboard items list.
                when(activity){
                    is RepairServicesActivity->{
                        activity.hideProgressDialog()
                    }
                    is AccessoriesActivity ->{
                        activity.hideProgressDialog()
                    }
                }
                Log.e(activity.javaClass.simpleName, "Error while getting dashboard items list.", e)
            }
    }

    fun getRepairServiceProductItemsList(activity: Activity) {
        // The collection name for PRODUCTS
        mFireStore.collection(Constant.PRODUCTS)
            .get() // Will get the documents snapshots.
            .addOnSuccessListener { document ->

                // Here we get the list of boards in the form of documents.
                Log.e(activity.javaClass.simpleName , document.documents.toString())

                // Here we have created a new instance for Products ArrayList.
                val productsList: ArrayList<ProductModels> = ArrayList()

                // A for loop as per the list of documents to convert them into Products ArrayList.
                for (i in document.documents) {

                    val product = i.toObject(ProductModels::class.java)!!
                    product.product_id = i.id
                    productsList.add(product)
                }
                when (activity) {
                    is RepairServicesActivity -> {
                        activity.successRepairServiceProductItemsList(productsList)
                    }
                    is AccessoriesActivity ->{
                        activity.successRepairServiceProductItemsList(productsList)
                    }
                    is ProductDetailsActivity ->{
                        activity.successRepairServiceProductItemsList(productsList)
                    }
                    // Pass the success result to the base fragment.

                }
            }
            .addOnFailureListener { e ->
                // Hide the progress dialog if there is any error which getting the dashboard items list.
                when (activity) {
                    is RepairServicesActivity -> {
                        activity.hideProgressDialog()
                    }
                    is AccessoriesActivity -> {
                        activity.hideProgressDialog()
                    }
                    is ProductDetailsActivity ->{
                        activity.hideProgressDialog()
                    }
                }
                Log.e(activity.javaClass.simpleName, "Error while getting dashboard items list.", e)
            }
    }

    fun deleteAddress(activity: AddressActivity, addressId: String) {
        mFireStore.collection(Constant.ADDRESSES)
            .document(addressId)
            .delete()
            .addOnSuccessListener {
                // Here call a function of base activity for transferring the result to it.
                activity.deleteAddressSuccess()
            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while deleting the address.",
                    e
                )
            }
    }
    fun deleteCategory(activity:ManageCategoryActivity , categoryId: String) {
        mFireStore.collection(Constant.CATEGORY)
            .document(categoryId)
            .delete()
            .addOnSuccessListener {
                activity.categoryDeleteSuccess()
            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while deleting Category", e
                )
            }
    }
    fun deleteProduct(activity:ManageProductsActivity , productId: String) {
        mFireStore.collection(Constant.PRODUCTS)
            .document(productId)
            .delete()
            .addOnSuccessListener {
                activity.productDeleteSuccess()
            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while deleting product", e
                )
            }
    }
    fun deleteAdvertPanel(activity:ManageAdvertPanelActivity , advertId: String) {
        mFireStore.collection(Constant.ADVERT_PANEL)
            .document(advertId)
            .delete()
            .addOnSuccessListener {
                activity.advertPanelDeleteSuccess()
            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while deleting advertPanel", e
                )
            }
    }





    fun getMyOrdersList(activity: OrderActivity) {
        mFireStore.collection(Constant.ORDERS)
            .whereEqualTo(Constant.USER_ID, getCurrentUserID())
            .get() // Will get the documents snapshots.
            .addOnSuccessListener { document ->
                Log.e(activity.javaClass.simpleName, document.documents.toString())
                val list: ArrayList<OrderModel> = ArrayList()

                for (i in document.documents) {

                    val orderItem = i.toObject(OrderModel::class.java)!!
                    orderItem.id = i.id

                    list.add(orderItem)
                }

                activity.populateOrdersListInUI(list)
            }
            .addOnFailureListener { e ->
                // Here call a function of base activity for transferring the result to it.

                activity.hideProgressDialog()

                Log.e(activity.javaClass.simpleName, "Error while getting the orders list.", e)
            }
    }
    fun getAllProductsList(activity: Activity) {
        // The collection name for PRODUCTS
        mFireStore.collection(Constant.PRODUCTS)
            .get() // Will get the documents snapshots.
            .addOnSuccessListener { document ->

                // Here we get the list of boards in the form of documents.
                Log.e("Products List", document.documents.toString())

                // Here we have created a new instance for Products ArrayList.
                val productsList: ArrayList<ProductModels> = ArrayList()

                // A for loop as per the list of documents to convert them into Products ArrayList.
                for (i in document.documents) {

                    val product = i.toObject(ProductModels::class.java)
                    product!!.product_id = i.id

                    productsList.add(product)
                }

                when (activity) {
                    is CartListActivity -> {
                       activity.successProductsListFromFireStore(productsList)
                    }
                    is CheckoutActivity -> {
                        activity.successProductsListFromFireStore(productsList)
                    }

                }
            }
            .addOnFailureListener { e ->
                // Hide the progress dialog if there is any error based on the base class instance.
                when (activity) {
                    is CartListActivity -> {
                        activity.hideProgressDialog()
                    }
                    is CheckoutActivity -> {
                        activity.hideProgressDialog()
                    }

                }

                Log.e("Get Product List", "Error while getting all product list.", e)
            }
    }

    fun getCartList(activity: Activity) {
        // The collection name for PRODUCTS
        mFireStore.collection(Constant.CART_ITEMS)
            .whereEqualTo(Constant.USER_ID, getCurrentUserID())
            .get() // Will get the documents snapshots.
            .addOnSuccessListener { document ->

                // Here we get the list of cart items in the form of documents.
                Log.e(activity.javaClass.simpleName, document.documents.toString())

                // Here we have created a new instance for Cart Items ArrayList.
                val list: ArrayList<CartModel> = ArrayList()

                // A for loop as per the list of documents to convert them into Cart Items ArrayList.
                for (i in document.documents) {

                    val cartItem = i.toObject(CartModel::class.java)!!
                    cartItem.id = i.id

                    list.add(cartItem)
                }

                when (activity) {
                    is CartListActivity -> {
                        activity.successCartItemsList(list)
                    }
                    is CheckoutActivity -> {
                        activity.successCartItemsList(list)
                    }

                }
            }
            .addOnFailureListener { e ->
                // Hide the progress dialog if there is an error based on the activity instance.
                when (activity) {
                    is CartListActivity -> {
                        activity.hideProgressDialog()
                    }

                    is CheckoutActivity -> {
                        activity.hideProgressDialog()
                    }
                }

                Log.e(activity.javaClass.simpleName, "Error while getting the cart list items.", e)
            }
    }

    fun placeOrder(activity: CheckoutActivity, order: OrderModel) {
        mFireStore.collection(Constant.ORDERS)
            .document()
            // Here the userInfo are Field and the SetOption is set to merge. It is for if we wants to merge
            .set(order, SetOptions.merge())
            .addOnSuccessListener {

                // Here call a function of base activity for transferring the result to it.
                activity.orderPlacedSuccess()
            }
            .addOnFailureListener { e ->

                // Hide the progress dialog if there is any error.
                activity.hideProgressDialog()
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while placing an order.",
                    e
                )
            }
    }

    fun updateAllDetails(activity: CheckoutActivity, cartList: ArrayList<CartModel>, order: OrderModel) {

        val writeBatch = mFireStore.batch()

        // Prepare the sold product details
        for (cart in cartList) {

            val soldProduct = SoldProduct(
                FirestoreClass().getCurrentUserID(),
                cart.title,
                cart.price,
                cart.cart_quantity,
                cart.image,
                order.title,
                order.order_datetime,
                order.sub_total_amount,
                order.shipping_charge,
                order.total_amount,
                order.address
            )

            val documentReference = mFireStore.collection(Constant.SOLD_PRODUCTS)
                .document()
            writeBatch.set(documentReference, soldProduct)
        }

        // Here we will update the product stock in the products collection based to cart quantity.
        for (cart in cartList) {

            val productHashMap = HashMap<String, Any>()

            productHashMap[Constant.STOCK_QUANTITY] =
                (cart.stock_quantity.toInt() - cart.cart_quantity.toInt()).toString()

            val documentReference = mFireStore.collection(Constant.PRODUCTS)
                .document(cart.product_id)

            writeBatch.update(documentReference, productHashMap)
        }

        // Delete the list of cart items
        for (cart in cartList) {

            val documentReference = mFireStore.collection(Constant.CART_ITEMS)
                .document(cart.id)
            writeBatch.delete(documentReference)
        }

        writeBatch.commit().addOnSuccessListener {

            activity.allDetailsUpdatedSuccessfully()

        }.addOnFailureListener { e ->
            // Here call a function of base activity for transferring the result to it.
            activity.hideProgressDialog()

            Log.e(
                activity.javaClass.simpleName,
                "Error while updating all the details after order placed.",
                e
            )
        }
    }

    fun updateMyCart(context: Context, cart_id: String, itemHashMap: HashMap<String, Any>) {

        // Cart items collection name
        mFireStore.collection(Constant.CART_ITEMS)
            .document(cart_id) // cart id
            .update(itemHashMap) // A HashMap of fields which are to be updated.
            .addOnSuccessListener {

                // Notify the success result of the updated cart items list to the base class.
                when (context) {
                    is CartListActivity -> {
                        context.itemUpdateSuccess()
                    }
                }
            }
            .addOnFailureListener { e ->

                // Hide the progress dialog if there is any error.
                when (context) {
                    is CartListActivity -> {
                        context.hideProgressDialog()
                    }
                }

                Log.e(
                    context.javaClass.simpleName,
                    "Error while updating the cart item.",
                    e
                )
            }
    }


    fun removeItemFromCart(context: Context, cart_id: String) {

        // Cart items collection name
        mFireStore.collection(Constant.CART_ITEMS)
            .document(cart_id) // cart id
            .delete()
            .addOnSuccessListener {

                // Notify the success result of the removed cart item from the list to the base class.
                when (context) {
                    is CartListActivity -> {
                        context.itemRemovedSuccess()
                    }
                }
            }
            .addOnFailureListener { e ->

                // Hide the progress dialog if there is any error.
                when (context) {
                    is CartListActivity -> {
                        context.hideProgressDialog()
                    }
                }
                Log.e(
                    context.javaClass.simpleName,
                    "Error while removing the item from the cart list.",
                    e
                )
            }
    }


    fun checkIfItemExistInCart(activity: ProductDetailsActivity , productId: String) {

        mFireStore.collection(Constant.CART_ITEMS)
            .whereEqualTo(Constant.USER_ID, getCurrentUserID())
            .whereEqualTo(Constant.PRODUCT_ID, productId)
            .get()
            .addOnSuccessListener { document ->

                Log.e(activity.javaClass.simpleName, document.documents.toString())

                // If the document size is greater than 1 it means the product is already added to the cart.
                if (document.documents.size > 0) {
                    activity.productExistsInCart()

                } else {
                    activity.hideProgressDialog()
                }
            }
            .addOnFailureListener { e ->
                // Hide the progress dialog if there is an error.
                activity.hideProgressDialog()

                Log.e(
                    activity.javaClass.simpleName,
                    "Error while checking the existing cart list.",
                    e
                )
            }
    }


    fun getProductDetails(activity: ProductDetailsActivity, productId: String) {
        // The collection name for PRODUCTS
        mFireStore.collection(Constant.PRODUCTS)
            .document(productId)
            .get() // Will get the document snapshots.
            .addOnSuccessListener { document ->

                // Here we get the product details in the form of document.
                Log.e(activity.javaClass.simpleName, document.toString())

                // Convert the snapshot to the object of Product data model class.
                val product = document.toObject(ProductModels::class.java)!!

                activity.productDetailsSuccess(product)
            }
            .addOnFailureListener { e ->

                // Hide the progress dialog if there is an error.
                activity.hideProgressDialog()

                Log.e(activity.javaClass.simpleName, "Error while getting the product details.", e)
            }
    }

    fun addCartItems(activity: ProductDetailsActivity, addToCart: CartModel) {

        mFireStore.collection(Constant.CART_ITEMS)
            .document()
            // Here the userInfo are Field and the SetOption is set to merge. It is for if we wants to merge
            .set(addToCart, SetOptions.merge())
            .addOnSuccessListener {

                // Here call a function of base activity for transferring the result to it.
                activity.addToCartSuccess()
            }
            .addOnFailureListener { e ->

                activity.hideProgressDialog()

                Log.e(
                    activity.javaClass.simpleName,
                    "Error while creating the document for cart item.",
                    e
                )
            }
    }
    fun getSoldProductsList(activity:  Activity) {
        // The collection name for SOLD PRODUCTS
        mFireStore.collection(Constant.SOLD_PRODUCTS)
            .whereEqualTo(Constant.USER_ID, getCurrentUserID())
            .get() // Will get the documents snapshots.
            .addOnSuccessListener { document ->
                // Here we get the list of sold products in the form of documents.
                Log.e(activity.javaClass.simpleName, document.documents.toString())

                // Here we have created a new instance for Sold Products ArrayList.
                val list: ArrayList<SoldProduct> = ArrayList()

                // A for loop as per the list of documents to convert them into Sold Products ArrayList.
                for (i in document.documents) {

                    val soldProduct = i.toObject(SoldProduct::class.java)!!
                    soldProduct.id = i.id

                    list.add(soldProduct)
                }
                when (activity) {
                    is SoldProductsActivity -> {

                        activity.successSoldProductsList(list)
                    }
                }
            }
            .addOnFailureListener { e ->
                // Hide the progress dialog if there is any error.
                when (activity) {
                    is SoldProductsActivity -> {
                        activity.hideProgressDialog()
                    }
                }
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while getting the list of sold products.",
                    e
                )
            }
    }

}