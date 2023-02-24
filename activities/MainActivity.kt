package com.hahnemanntechnology.activities



import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.denzcoskun.imageslider.ImageSlider
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.models.SlideModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.firestore.FirebaseFirestore
import com.hahnemann.daza2.activities.AccessoriesActivity
import com.hahnemanntechnology.R
import com.hahnemanntechnology.adapters.MainActivityListAdapter
import com.hahnemanntechnology.firestore.FirestoreClass
import com.hahnemanntechnology.models.AdvertPanelModels
import com.hahnemanntechnology.models.ProductModels
import com.hahnemanntechnology.utilities.Constant
import kotlinx.android.synthetic.main.activity_main.*


@Suppress("DEPRECATION")
class MainActivity : BaseActivity() {

    // Initialise the DrawerLayout, NavigationView and ToggleBar
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var actionBarToggle: ActionBarDrawerToggle
    private lateinit var navView: NavigationView
    private lateinit var mAdvertDetails:AdvertPanelModels
    private var position: String? = null
    private val db = FirebaseFirestore.getInstance()
    private var imageSlider: ImageSlider? = null

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Bottom Navigation Bar
        bottomnav()
        //Navigation Drawer -> add two dependencies
        navigationDrawer()
        advertPanel()


        wishlist.setOnClickListener {
            val intent = Intent(this,WishlistActivity::class.java)
            startActivity(intent)
        }

    }



    override fun onResume() {
        super.onResume()
      //  getAdvertPanelListFromFireStore()
        getMainActivityItemsList()
    }
    private fun getAdvertPanelListFromFireStore(){
        showProgressDialog(resources.getString(R.string.plz_wait))
        FirestoreClass().getAdvertPanelList(this)

    }

    fun advertPanel(){
        imageSlider = findViewById(R.id.image_slider)
        val displayImages: MutableList<SlideModel> = ArrayList()
        val intent = intent.extras
//        position = intent!!["key"].toString()
        db.collection(Constant.ADVERT_PANEL).document("name")

            .collection(Constant.ADVERT_PANEL).document("product_id").get()
            .addOnSuccessListener { documentSnapshot ->
                val Urls = documentSnapshot["Images"] as List<*>?
                displayImages.add(SlideModel(Urls.toString() , ScaleTypes.FIT))
                imageSlider!!.setImageList(displayImages , ScaleTypes.FIT)
                imageSlider!!.startSliding(3000)
            }

    }


    private fun getMainActivityItemsList() {
        // Show the progress dialog.
        showProgressDialog(resources.getString(R.string.please_wait))

        FirestoreClass().getMainActivityItemsList(this@MainActivity)
    }

    fun successMainActivityItemsList(dashboardItemsList: ArrayList<ProductModels>) {

        // Hide the progress dialog.
        hideProgressDialog()

        if (dashboardItemsList.size > 0) {

            rv_dashboard_items.visibility = View.VISIBLE
            tv_no_dashboard_items_found.visibility = View.GONE

            rv_dashboard_items.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
            rv_dashboard_items.setHasFixedSize(true)

            val adapter = MainActivityListAdapter(this, dashboardItemsList)
            rv_dashboard_items.adapter = adapter


        } else {
            rv_dashboard_items.visibility = View.GONE
            tv_no_dashboard_items_found.visibility = View.VISIBLE
        }
    }

    private fun navigationDrawer() {
        // Call findViewById on the DrawerLayout
        drawerLayout = findViewById(R.id.drawerLayout)

        // Pass the ActionBarToggle action into the drawerListener
        actionBarToggle = ActionBarDrawerToggle(this , drawerLayout , 0 , 0)
        actionBarToggle.drawerArrowDrawable.color = resources.getColor(R.color.ColorWhite)

        setSupportActionBar(toolbar)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
       // supportActionBar?.setDisplayHomeAsUpEnabled(R.drawable.sort)



        // Display the hamburger icon to launch the drawer
        //supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Call syncState() on the action bar so it'll automatically change to the back button when the drawer layout is open
        actionBarToggle.syncState()


        // Call findViewById on the NavigationView
        navView = findViewById(R.id.navView)

        // Call setNavigationItemSelectedListener on the NavigationView to detect when items are clicked
        navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.admin_Manage_Category -> {
                    val intent = Intent(this , ManageCategoryActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.admin_Manage_Products -> {
                    val intent = Intent(this , ManageProductsActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.admin_Manage_Advert_Panel -> {
                    /*  supportFragmentManager.beginTransaction().replace(R.id.container , Ma())
                        .commit() */
                    val intent = Intent(this , ManageAdvertPanelActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.admin_orders -> {
                    val intent = Intent(this , SoldProductsActivity::class.java)
                    startActivity(intent)
                    true
                }

                else -> {
                    false
                }
            }
        }
    }

    // override the onSupportNavigateUp() function to launch the Drawer when the hamburger icon is clicked
    override fun onSupportNavigateUp(): Boolean {
        drawerLayout.openDrawer(navView)
        return true
    }

    // override the onBackPressed() function to close the Drawer when the back button is clicked
    override fun onBackPressed() {

        if (this.drawerLayout.isDrawerOpen(GravityCompat.START))
        {
            this.drawerLayout.closeDrawer(GravityCompat.START)
        }

    }



    private fun bottomnav() {
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        // Set Home selected
        bottomNavigationView.selectedItemId = R.id.home

        // Perform item selected listener
        bottomNavigationView.setOnNavigationItemSelectedListener(BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> return@OnNavigationItemSelectedListener true
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
                R.id.user -> {
                    startActivity(Intent(applicationContext , UserActivity::class.java))
                    overridePendingTransition(0 , 0)
                    return@OnNavigationItemSelectedListener true
                }
            }
            false
        })

    }





}
