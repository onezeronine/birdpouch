package com.fortcode.birdpouch.activities

import android.app.Activity
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.view.ViewPager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.fortcode.birdpouch.*
import com.fortcode.birdpouch.adapters.GalleryBucketsAdapter
import com.fortcode.birdpouch.adapters.GalleryImagesAdapter
import com.fortcode.birdpouch.adapters.ViewImagePagerAdapter
import com.fortcode.birdpouch.models.GalleryBucket
import com.fortcode.birdpouch.models.GalleryImage
import com.fortcode.birdpouch.utilities.Helper
import com.fortcode.birdpouch.utilities.IntentConstants
import com.squareup.picasso.Picasso

class MainActivity : AppCompatActivity() {
    enum class ViewState {
        BUCKETS_VIEW,
        IMAGES_VIEW,
        FULL_SCREEN_VIEW
    }

    private val tag = "A_Main"
    private var viewState: ViewState = ViewState.BUCKETS_VIEW
    private var selectedBucket: GalleryBucket? = null

    private lateinit var activity: Activity
    private lateinit var galleryImages: List<GalleryImage>
    private lateinit var galleryBuckets: HashMap<GalleryBucket, ArrayList<GalleryImage>>

    private lateinit var galleryBucketsRecyclerView: RecyclerView
    private lateinit var galleryImagesRecyclerView: RecyclerView
    private lateinit var galleryImagesViewPager: ViewPager

    private lateinit var picasso: Picasso

    private lateinit var galleryBucketsAdapter: GalleryBucketsAdapter
    private lateinit var galleryImagesAdapter: GalleryImagesAdapter
    private lateinit var viewImagePagerAdapter: ViewImagePagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        activity = this
        picasso = Picasso.Builder(activity).build()

        Helper.isStoragePermissionGranted(activity, IntentConstants.GRANT_STORAGE_ACCESS).let {
            if(it) {
                initializeGalleryBucketsView()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode) {
            IntentConstants.GRANT_STORAGE_ACCESS -> {
                if(resultCode == Activity.RESULT_OK) {
                    initializeGalleryBucketsView()
                } else {
                    Toast.makeText(activity, "Access not granted", Toast.LENGTH_SHORT).show()
                }
            }
            else -> {
                Toast.makeText(activity, "Nothing to do here", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onBackPressed() {
        when(viewState) {
            ViewState.BUCKETS_VIEW -> super.onBackPressed()
            ViewState.IMAGES_VIEW -> changeStateToBucketsView()
            ViewState.FULL_SCREEN_VIEW -> changeStateToImagesView(selectedBucket!!)
        }
    }

    private fun initializeGalleryBucketsView() {
        galleryImages = queryMediaFromStorage()
        galleryBuckets = groupImagesByBuckets(galleryImages)

        val buckets = galleryBuckets.keys.toList().sortedBy { it.name }

        galleryBucketsRecyclerView = findViewById(R.id.gallery_buckets_recyclerview)
        galleryBucketsRecyclerView.apply {
            galleryBucketsAdapter = GalleryBucketsAdapter(activity, buckets).apply {
                setOnSelectGalleryBucketListener(object: GalleryBucketsAdapter.OnSelectGalleryBucketListener {
                    override fun onSelectGalleryBucket(galleryBucket: GalleryBucket) {
                        changeStateToImagesView(galleryBucket)
                    }
                })
            }
            adapter = galleryBucketsAdapter
            layoutManager = LinearLayoutManager(activity)
        }

        galleryImagesRecyclerView = findViewById(R.id.gallery_images_recyclerview)
        galleryImagesRecyclerView.apply {
            galleryImagesAdapter = GalleryImagesAdapter(activity, ArrayList()).apply {
                setOnSelectImageListener(object: GalleryImagesAdapter.OnSelectImageListener {
                    override fun onSelectImage(galleryImage: GalleryImage) {
                        changeStateToFullScreenImageView(galleryImage)
                    }
                })
            }
            adapter = galleryImagesAdapter
            layoutManager = LinearLayoutManager(activity)
        }

        viewImagePagerAdapter = ViewImagePagerAdapter(supportFragmentManager, ArrayList())
        galleryImagesViewPager = findViewById(R.id.gallery_images_viewpager)
        galleryImagesViewPager.apply {
            adapter = viewImagePagerAdapter
            addOnPageChangeListener(object: ViewPager.OnPageChangeListener {
                override fun onPageSelected(position: Int) {
                    supportActionBar?.subtitle = viewImagePagerAdapter.getImage(position)?.displayName
                }

                override fun onPageScrollStateChanged(position: Int) {
                }

                override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                }
            })
        }
    }

    private fun groupImagesByBuckets(images: List<GalleryImage>) : HashMap<GalleryBucket, ArrayList<GalleryImage>> {
        val map = HashMap<GalleryBucket, ArrayList<GalleryImage>>()
        for(image in images) {
            val bucket = GalleryBucket(image.bucketId, image.bucketName)
            if(map.containsKey(bucket)) {
                map[bucket]?.add(image)
            } else {
                map[bucket] = ArrayList<GalleryImage>().apply { add(image) }
            }
        }
        return map
    }

    private fun changeStateToBucketsView() {
        selectedBucket = null

        galleryBucketsRecyclerView.visibility = View.VISIBLE
        galleryImagesRecyclerView.visibility = View.GONE
        galleryImagesViewPager.visibility = View.GONE

        supportActionBar?.title = getString(R.string.app_name)
        supportActionBar?.subtitle = null

        viewState = ViewState.BUCKETS_VIEW
    }

    private fun changeStateToImagesView(galleryBucket: GalleryBucket) {
        val items = galleryBuckets[galleryBucket]

        galleryBucketsRecyclerView.visibility = View.GONE
        galleryImagesRecyclerView.visibility = View.VISIBLE
        galleryImagesViewPager.visibility = View.GONE

        if(items != null) {
            val images = items.sortedBy { it.displayName }
            viewImagePagerAdapter.replaceImages(images)
            galleryImagesAdapter.replaceGalleryImages(images)
        }

        supportActionBar?.title = galleryBucket.name
        supportActionBar?.subtitle = null

        selectedBucket = galleryBucket

        viewState = ViewState.IMAGES_VIEW
    }

    private fun changeStateToFullScreenImageView(galleryImage: GalleryImage) {
        val position = viewImagePagerAdapter.getImagePosition(galleryImage)

        galleryImagesViewPager.setCurrentItem(position, false)

        galleryBucketsRecyclerView.visibility = View.GONE
        galleryImagesRecyclerView.visibility = View.GONE
        galleryImagesViewPager.visibility = View.VISIBLE

        supportActionBar?.title = selectedBucket?.name
        supportActionBar?.subtitle = viewImagePagerAdapter.getImage(position)?.displayName

        viewState = ViewState.FULL_SCREEN_VIEW
    }

    private fun queryMediaFromStorage(): List<GalleryImage> {
        val list = ArrayList<GalleryImage>()
        val cursor = contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            arrayOf(
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.BUCKET_ID,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.TITLE,
                MediaStore.Images.Media.MIME_TYPE,
                MediaStore.Images.Media.DATE_ADDED,
                MediaStore.Images.Media.DATA
            ),
            null,
            null,
            MediaStore.Images.Media.DATE_ADDED + " DESC"
        )
        if(cursor != null && cursor.moveToFirst()) {
            Log.i(tag, String.format("Queried images cursor count = %s", cursor.count))
            do {
                list.add(
                    GalleryImage(
                        id = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media._ID)),
                        bucketId = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.BUCKET_ID)),
                        bucketName = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.BUCKET_DISPLAY_NAME)),
                        displayName = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME)),
                        imageUri = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA)),
                        dateAdded = cursor.getInt(cursor.getColumnIndex(MediaStore.Images.Media.DATE_ADDED))
                    )
                )
            } while(cursor.moveToNext())
            cursor.close()
        } else {
            Log.w(tag, "Queried images cursor is not defined or empty")
        }
        return list
    }
}
