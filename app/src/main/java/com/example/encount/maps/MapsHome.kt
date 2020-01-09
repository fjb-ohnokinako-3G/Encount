package com.example.encount.maps

import androidx.core.app.ActivityCompat
import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.request.transition.Transition
import com.example.encount.MapsDataClassList
import com.example.encount.MapsList
import com.example.encount.PostList2
import com.example.encount.R
import com.example.encount.post.UserPost
import com.google.android.gms.location.*
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.zzaa
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_maps_home.*
import kotlinx.android.synthetic.main.grid_items.view.*
import kotlinx.android.synthetic.main.spotmain.*
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import java.lang.Exception

class MapsHome : Fragment(), OnMapReadyCallback {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var mMap: GoogleMap? = null
    private val requestingLocationUpdates = true //フラグ
    private val locationRequest: LocationRequest = LocationRequest.create()
    //private var postList = mutableListOf<MapsList>()
    private var postList = mutableListOf<PostList2>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(R.layout.activity_maps_home, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        MapPostGet(this).execute()

        val mapFragment: SupportMapFragment = getChildFragmentManager().findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        button2.setOnClickListener {

            //スポット詳細画面へ遷移
            val intent = Intent(context, SpotMainActivity::class.java)
            startActivity(intent)
        }

        // Android 6, API 23以上でパーミッションの確認
        if (Build.VERSION.SDK_INT >= 23) {
            val permissions = arrayOf(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
            checkPermission(permissions, REQUEST_CODE)
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context!!)
        locationRequest.setInterval(10000)   //最遅の更新間隔
        locationRequest.setFastestInterval(5000)   //最速の更新間隔
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY)           //バッテリー消費を抑えたい場合、精度は100m程度
        onResume()
    }

    //Update Result
    val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult?) {
            locationResult ?: return
            for (location in locationResult.locations) {
                if (location != null) {

                    //グローバル変数に位置情報を代入
                    latitude = location.latitude
                    longitude = location.longitude

                    //MapPostGet(this@MapsHome).execute()

                    //ここで前回のマップのピンを全削除する処理

                    //MapPostGet(this,lat,lng).execute()で緯度経度を引数にして渡す
                    //サーバと通信する処理（インナークラス）を呼び出して実行する
                    //SpotPhotoGet().execute()

                    //postList.get()
                    //ここでマップのピンを立てる処理
                    //val spot = LatLng(35.7044997, 139.9843911)
                    //Log.d("debug", "aaaaa" + postList[0].imageLat)
                    //Log.d("debug", "aaaaa" + postList[0].imageLng)
                    //val spot = LatLng(postList[0].imageLat.toDouble(),postList[0].imageLng.toDouble())
                    val spot = LatLng(35.7042531,139.9840158)
                    mMap!!.addMarker(
                    MarkerOptions()
                        .position(spot)
                        .title("Maker2")
                        .icon(
                            //BitmapDescriptorFactory.fromResource(
                                //Glide.with(context).asBitmap().load(postList[0].imagePath).into()
                                BitmapDescriptorFactory.fromResource(R.drawable.logo)
                        )
                    )

                    //Log.d("debug", "pass" + postList[0].imgpath)
                    Log.d("debug", "pass" + postList[0].imagePath)
                    Log.d("debug", "pass" + postList[1].imagePath)
                    Log.d("debug", "pass" + postList[0].imageLat)
                    Log.d("debug", "pass" + postList[0].imageLng)
                    Log.d("debug", "pass" + postList[0].imageId)


                    Log.d("debug", "緯度" + location.latitude)
                    Log.d("debug", "経度" + location.longitude)

                }
            }
        }
    }

    /*fun setPostList(postList: MutableList<MapsList>) {
        this.postList = postList
    }*/
    fun setPostList(postList: MutableList<PostList2>) {
        this.postList = postList
    }


    override fun onResume() {
        super.onResume()
        if (requestingLocationUpdates) startLocationUpdates()
    }

    private fun startLocationUpdates() {
        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback, null /* Looper */
        )
        /*
              startActivity(Intent(this, UserHome::class.java))
              overridePendingTransition(0, 0)
          }*/
    }

    override fun onPause() {
        super.onPause()
        stopLocationUpdates()
    }

    private fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    //下の処理を再起的に利用してマップ上に写真表示を行う
    //default location
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        val spot = LatLng(35.7044997, 139.9843911)
        mMap!!.addMarker(
            MarkerOptions()
                .position(spot)
                .title("Marker in FJB")
                .icon(
                    BitmapDescriptorFactory.fromResource(R.drawable.smile1)
                )
        )

    //写真表示テスト
       /* mMap!!.addMarker(
            MarkerOptions()
                .position(spot)
                .title("Marker")
                .icon(
                    BitmapDescriptorFactory.fromResource(
                        Picasso.with(context)
                            .load(addUrl(position))
                            .resize(ScreenWHalf, ScreenWHalf)
                            .placeholder(R.drawable.placeholder)
                            .error(R.drawable.error)
                            .into(img)
                    )
                )
        )*/


        mMap!!.moveCamera(CameraUpdateFactory.newLatLng(spot))
        //マップのズーム絶対値指定　1: 世界 5: 大陸 10:都市 15:街路 20:建物 ぐらいのサイズ
        mMap!!.moveCamera(CameraUpdateFactory.zoomTo(19f))
    }

    //許可されていないパーミッションリクエスト
    fun checkPermission(permissions: Array<String>, request_code: Int) {
        ActivityCompat.requestPermissions(activity!!, permissions, request_code)
    }

    //結果
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == REQUEST_PERMISSION) {
            for (i in permissions.indices) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    mMap!!.setMyLocationEnabled(true)
                    Log.d("Permission", "Added Permission: " + permissions[i])
                } else {
                    // パーミッションが拒否された
                    Log.d("Permission", "Rejected Permission: " + permissions[i])
                }
            }
        }
    }

    companion object {
        private val REQUEST_CODE = 1000
        private val REQUEST_PERMISSION = 1000
        //setter
        /*fun setPostList(mapsHome: MapsHome, mutableList: MutableList<MapsList>) {
            mapsHome.postList = mapsHome.postList
            Log.d("debug", "pass" + mapsHome.postList[0].imgpath)
        }*/
        fun setPostList(mapsHome: MapsHome, mutableList: MutableList<PostList2>) {
            mapsHome.postList = mapsHome.postList
            Log.d("debug", "pass" + mapsHome.postList[0])
        }
    }




}