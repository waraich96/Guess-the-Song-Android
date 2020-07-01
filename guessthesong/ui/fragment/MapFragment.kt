package com.example.guessthesong.ui.fragment


import android.Manifest
import android.content.ContentValues
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Location
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.loader.app.LoaderManager
import androidx.loader.content.CursorLoader
import androidx.loader.content.Loader
import com.example.guessthesong.R
import com.example.guessthesong.api.AddressApi
import com.example.guessthesong.api.GameDirectionAPI
import com.example.guessthesong.api.UserLocationApi
import com.example.guessthesong.db.LyricContract
import com.example.guessthesong.model.GameMode
import com.example.guessthesong.model.LyricData
import com.example.guessthesong.model.UserLyric
import com.example.guessthesong.pref.GameSharedPreferences
import com.example.guessthesong.utils.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapsInitializer
import com.google.android.gms.maps.model.*
import com.google.maps.android.SphericalUtil
import kotlinx.android.synthetic.main.fragment_map.*
import kotlin.random.Random

/**
 * A simple [Fragment] subclass.
 */
class MapFragment : Fragment(), LoaderManager.LoaderCallbacks<Cursor> {

    companion object {
        private const val MAP_VIEW_BUNDLE_KEY = "MapViewBundleKey"
        private const val INITIAL_ADDRESS =
            "Swansea University, Bay Campus, Fabian Way, SA1 8EP, Wales"
        private const val LOCATION_PERMISSION_REQUEST_CODE = 856
        private const val LOADER_ID = 23
    }

    private var userMarker: Marker? = null
    private var lyricsMarkersList: MutableList<Marker>? = null
    private var collectedLyrics = mutableListOf<UserLyric>()
    private var allLyricsItems = mutableListOf<LyricData>()
    private var lyricsList: MutableList<LyricData> = mutableListOf()
    private var mMap: GoogleMap? = null
    private lateinit var sharedPref: GameSharedPreferences
    private lateinit var mode: GameMode
    private var initialLocation: LatLng? = null
    private var currentLocation: Location? = null
    private var isMapSynchronized = false
    private var alertDialog: AlertDialog? = null
    private lateinit var locationApi: UserLocationApi
    private var isPermissionRequested = false
    private var isLyricsUpdatedWithCurrentLoc = false
    private var isLyricsUpdatedWithCursor = false
    private lateinit var directionApi: GameDirectionAPI
    private var polyline: Polyline? = null

    private val locationListener = object : UserLocationListener {
        override fun onUpdateLocation(location: Location) {
            currentLocation = location
            updateUserMarker()
        }
    }

    private val directionListener = object : DirectionListener {
        override fun onFetchedLatLng(list: MutableList<LatLng>) {
            drawRoute(list)
        }
    }

    private val markerClickListener = object : GoogleMap.OnMarkerClickListener {
        override fun onMarkerClick(p0: Marker?): Boolean {
            if (p0 == userMarker) return false
            if (userMarker != null && p0 != null) {
                val distance =
                    SphericalUtil.computeDistanceBetween(userMarker!!.position, p0.position)
                if (distance > 15) {
                    if (isConnected(requireContext()))
                        GameDirectionAPI(directionListener).getRoute(
                            userMarker!!.position,
                            p0.position
                        )
                } else {
                    collectLyricAtMarker(p0)
                }
            }
            return true
        }
    }

    interface DirectionListener {
        fun onFetchedLatLng(list: MutableList<LatLng>)
    }

    interface UserLocationListener {
        fun onUpdateLocation(location: Location)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_map, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
        setupMap(savedInstanceState)
        getInitialLocation()
    }

    private fun init() {
        sharedPref = GameSharedPreferences(context!!)
        mode = getGameMode(sharedPref.getGameMode())
        lyricsList = mutableListOf()
        allLyricsItems = getLyricDataListWithMode(context!!, mode)
        lyricsList.addAll(allLyricsItems)
        initializeValidLyrics()
        isMapSynchronized = false
        locationApi = UserLocationApi(locationListener)
        directionApi = GameDirectionAPI(directionListener)
    }

    private fun initializeValidLyrics() {
        val removeItems = mutableListOf<LyricData>()
        for (x in 0 until lyricsList.size) {
            collectedLyrics.forEach {
                if (it.song == lyricsList[x].song) {
                    removeItems.add(lyricsList[x])
                }
            }
        }
        removeItems.forEach { lyricsList.remove(it) }
        initializeLyricsLocations()
    }

    private fun setupMap(savedInstanceState: Bundle?) {
        var mapViewBundle: Bundle? = null
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAP_VIEW_BUNDLE_KEY)
        }
        mapView?.onCreate(mapViewBundle)
        MapsInitializer.initialize(requireActivity())
        mapView?.getMapAsync {
            isMapSynchronized = true
            mMap = it
            updateUserMarker()
            moveCamera()
            mMap?.setOnMarkerClickListener(markerClickListener)
        }
    }

    private fun getInitialLocation() {
        initialLocation = AddressApi().getLatLngFromAddress(INITIAL_ADDRESS, requireContext())
        updateUserMarker()
    }

    private fun updateUserMarker() {
        mMap ?: return
        if (currentLocation == null && initialLocation == null) return

        val latLng = if (currentLocation == null) initialLocation
        else LatLng(currentLocation!!.latitude, currentLocation!!.longitude)

        if (userMarker == null) {
            userMarker = initializeMarker(latLng!!, getBitmap(R.drawable.user_placeholder))
            moveCamera()
        } else {
            userMarker!!.position = latLng
        }
        if (currentLocation != null && !isLyricsUpdatedWithCurrentLoc) {
            initializeLyricsLocations()
            isLyricsUpdatedWithCurrentLoc = true
            moveCamera()
        } else if (currentLocation == null) {
            initializeLyricsLocations()
        }
    }

    private fun initializeLyricsLocations() {
        userMarker ?: return
        val userLat = userMarker!!.position.latitude
        val userLng = userMarker!!.position.longitude
        lyricsList.forEach { lyric ->
            lyric.latitude = Random.nextDouble(userLat - 0.003, userLat + 0.003)
            lyric.longitude = Random.nextDouble(userLng - 0.003, userLng + 0.003)
        }
        updateLyricsMarkers()
    }

    private fun updateLyricsMarkers() {
        mMap ?: return
        if (!lyricsMarkersList.isNullOrEmpty()) {
            lyricsMarkersList!!.forEach {
                it.remove()
            }
            lyricsMarkersList!!.clear()
        }
        lyricsMarkersList = mutableListOf()
        lyricsList.forEach {
            if (it.latitude != null && it.longitude != null) {
                val marker = initializeMarker(
                    LatLng(it.latitude!!, it.longitude!!),
                    getBitmap(R.drawable.music_player)
                )
                if (marker != null) {
                    lyricsMarkersList!!.add(marker)
                }
            }
        }
    }

    private fun initializeMarker(latLng: LatLng, bitmap: Bitmap): Marker? {
        if (mMap == null || !isMapSynchronized) return null
        val markerOptions = MarkerOptions()
            .position(latLng)
            .icon(BitmapDescriptorFactory.fromBitmap(bitmap))
        return mMap?.addMarker(markerOptions)
    }

    private fun getBitmap(resId: Int): Bitmap {
        return BitmapFactory.decodeResource(requireContext().resources, resId);
    }

    private fun moveCamera() {
        userMarker ?: return
        mMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(userMarker!!.position, 16f))
    }


    private fun drawRoute(list: MutableList<LatLng>) {
        polyline?.remove()
        val rectOptions = PolylineOptions()
            .color(ContextCompat.getColor(requireContext(), R.color.md_red_600))

        list.forEach {
            rectOptions.add(it)
        }

        polyline = mMap?.addPolyline(rectOptions);
    }

    private fun collectLyricAtMarker(p0: Marker) {
        val position = p0.position
        lyricsMarkersList?.remove(p0)
        p0.remove()
        lyricsList.forEach {
            if (it.latitude != null && it.latitude == position.latitude
                && it.longitude != null && it.longitude == position.longitude
            ) {
                val userLyric = UserLyric(
                    type = mode.name, lyric = it.lyric,
                    album = it.album, artist = it.artist, song = it.song, length = it.length,
                    released = it.released, genre = it.genre
                )

                val values = ContentValues().apply {
                    put(LyricContract.COLUMN_INDEX, userLyric.index)
                    put(LyricContract.COLUMN_TYPE, userLyric.type)
                    put(LyricContract.COLUMN_LYRIC, userLyric.lyric)
                    put(LyricContract.COLUMN_ALBUM, userLyric.album)
                    put(LyricContract.COLUMN_ARTIST, userLyric.artist)
                    put(LyricContract.COLUMN_SONG, userLyric.song)
                    put(LyricContract.COLUMN_RELEASED, userLyric.released)
                    put(LyricContract.COLUMN_LENGTH, userLyric.length)
                    put(LyricContract.COLUMN_GENRE, userLyric.genre)
                }
                val insertedRows = requireActivity().contentResolver.insert(
                    Uri.parse(LyricContract.CONTENT_AUTHORITY),
                    values
                )
                Log.i("MapFragment", "insertedRows: $insertedRows")
                if (insertedRows != null) {
                    sharedPref.setUserCollectedLyricsCount(sharedPref.getUserCollectedLyricsCount() + 1)
                }
            }
        }

    }

    /*Location Permission*/
    private fun checkLocationPermission() {
        if (!isHasPermission(Manifest.permission.ACCESS_COARSE_LOCATION) &&
            !isHasPermission(Manifest.permission.ACCESS_FINE_LOCATION)
        ) {
            displayRequestLocationPermissionDialog()
        } else {
            locationApi.requestLocationUpdates(requireContext())
        }
    }

    private fun isHasPermission(permission: String) =
        ContextCompat.checkSelfPermission(
            requireContext(),
            permission
        ) == PackageManager.PERMISSION_GRANTED


    private fun displayRequestLocationPermissionDialog() {
        alertDialog?.dismiss()
        if (isPermissionRequested) return
        alertDialog = AlertDialog.Builder(requireActivity())
            .setTitle(R.string.location_tracking_disabled)
            .setMessage(R.string.please_allow_permissions)
            .setPositiveButton(R.string.ok) { dialog_interface, _ ->
                requestLocationPermissions()
                dialog_interface?.dismiss()
            }
            .setNegativeButton(R.string.cancel) { dialog_interface, _ -> dialog_interface?.dismiss() }
            .show()
        isPermissionRequested = true
    }

    private fun requestLocationPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requireActivity().requestPermissions(
                arrayOf(
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        }
    }

    override fun onStart() {
        super.onStart()
        mapView.onStart()
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
        checkLocationPermission()
        queryUserCollectedLyrics()
    }

    override fun onStop() {
        super.onStop()
        mapView?.onStop()
        locationApi.stopLocationUpdates(requireContext())
    }

    override fun onDestroyView() {
        userMarker?.remove()
        lyricsMarkersList?.forEach { it.remove() }
        mapView?.onDestroy()
        super.onDestroyView()
    }

    override fun onPause() {
        mapView?.onPause()
        super.onPause()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        var mapViewBundle = outState.getBundle(MAP_VIEW_BUNDLE_KEY)
        if (mapViewBundle == null) {
            mapViewBundle = Bundle()
            outState.putBundle(MAP_VIEW_BUNDLE_KEY, mapViewBundle)
        }
        mapView.onSaveInstanceState(outState)
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }


    private fun queryUserCollectedLyrics() {
        LoaderManager.getInstance(this).restartLoader(LOADER_ID, null, this)
    }

    override fun onCreateLoader(id: Int, args: Bundle?): Loader<Cursor> {
        val uri = Uri.parse(LyricContract.CONTENT_AUTHORITY)
        val selection: String =
            LyricContract.COLUMN_TYPE + "=?"
        val selectionArgs = arrayOf(mode.name)
        return CursorLoader(requireContext(), uri, null, selection, selectionArgs, null)
    }

    override fun onLoadFinished(loader: Loader<Cursor>, data: Cursor?) {
        if (data != null) {
            collectedLyrics = getUserLyricList(data)
            if (!isLyricsUpdatedWithCursor) {
                initializeValidLyrics()
                isLyricsUpdatedWithCursor = true
            }
        }

    }

    override fun onLoaderReset(loader: Loader<Cursor>) {

    }

}
