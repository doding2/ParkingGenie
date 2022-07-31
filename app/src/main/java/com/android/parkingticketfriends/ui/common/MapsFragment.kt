package com.android.parkingticketfriends.ui.common

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.android.parkingticketfriends.R
import com.android.parkingticketfriends.model.ParkingMarker
import com.android.parkingticketfriends.ui.driver.MarkerSheetFragment
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions


class MapsFragment : Fragment(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener, MainActivity.OnBackPressedListener {

    private lateinit var mMap: GoogleMap
    private var _binding: com.android.parkingticketfriends.databinding.FragmentMapsBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: MapsViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_maps, container, false)
        val factory = MapsViewModelFactory(requireContext())
        viewModel = ViewModelProvider(requireActivity(), factory)[MapsViewModel::class.java]


        if (checkPermission()) {
            initMap()
        } else {
            ActivityCompat.requestPermissions(requireActivity(), PERMISSIONS, REQUEST_PERMISSION_CODE)
        }

        // 마커 관리
        viewModel.parkingMarkerInfo.observe(viewLifecycleOwner) {
            if (!::mMap.isInitialized) {
                return@observe
            }

            // 지도에서 마커 제거 후 클리어
            viewModel.markers.value?.forEach {
                it.key.remove()
            }
            viewModel.markers.value?.clear()

            // 지도에 마커들 추가
            viewModel.parkingMarkerInfo.value?.forEach {
                addMarker(it)
            }
        }


        // 프로필 페이지로 이동
        binding.btnProfile.setOnClickListener {
            binding.drawerLayout.openDrawer(GravityCompat.END)
        }

        // TODO 네비게이션 경로 관리
        binding.navigationContent.itemRegisterCar.setOnClickListener {
            parentFragment?.findNavController()?.navigate(R.id.action_mapsFragment_to_registerCarFragment)
        }

        binding.navigationContent.itemCheckReservation.setOnClickListener {
            parentFragment?.findNavController()?.navigate(R.id.action_mapsFragment_to_checkReservationFragment)
        }

        binding.navigationContent.itemManageCredit.setOnClickListener {
            parentFragment?.findNavController()?.navigate(R.id.action_mapsFragment_to_manageCreditFragment)
        }

        binding.navigationContent.itemRegisterParkingSpace.setOnClickListener {
            parentFragment?.findNavController()?.navigate(R.id.action_mapsFragment_to_registerParkingSpaceFragment)
        }

        binding.navigationContent.itemManageParkingSpace.setOnClickListener {
            parentFragment?.findNavController()?.navigate(R.id.action_mapsFragment_to_manageParkingSpaceFragment)
        }

        return binding.root
    }

    private fun initMap() {
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.

        val mapFragment = childFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }


    private val PERMISSIONS = arrayOf(
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION
    )

    private val REQUEST_PERMISSION_CODE = 1

    private fun checkPermission(): Boolean {
        for (permission in PERMISSIONS) {
            if (ActivityCompat.checkSelfPermission(requireActivity(), permission) != PackageManager.PERMISSION_GRANTED) {
                return false
            }
        }
        return true
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // 마커 클릭 리스너 등록
        mMap.setOnMarkerClickListener(this)

        // 현재 위치로 이동 버튼 비활성화
        mMap.uiSettings.isMyLocationButtonEnabled = false
        mMap.uiSettings.isMapToolbarEnabled = false

        when {
            checkPermission() -> {
                mMap.isMyLocationEnabled = true

                val myLoc = getMyLocation()
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLoc, 17f))
            }
            else -> {
                // Add a marker in Sydney and move the camera
                val sydney = LatLng(-34.0, 151.0)
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 17f))
            }
        }
    }

    // 마커 지도에 추가
    private fun addMarker(item: ParkingMarker) {
        val location = LatLng(item.latitude.toDouble(), item.longitude.toDouble())
        val restParkingSpace = item.parking_lots.count {
            it.status == "available"
        }

        // 마커 이미지 생성
        val customMarker: TextView =
            LayoutInflater.from(requireContext()).inflate(R.layout.item_custom_marker, null) as TextView
        customMarker.text = restParkingSpace.toString()

        if (restParkingSpace == 0) {
            customMarker.setBackgroundResource(R.drawable.baseline_gray_circle_24)
        } else {
            customMarker.setBackgroundResource(R.drawable.baseline_circle_24)
        }

        val marker = mMap.addMarker(
            MarkerOptions()
                .position(location)
                .title(item.name)
                .icon(BitmapDescriptorFactory.fromBitmap(loadBitmapFromView(customMarker)))
        )

        viewModel.markers.value?.put(marker, item)
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 17f))
    }

    // view를 bitmap으로 변환
    private fun loadBitmapFromView(v: View): Bitmap {
        if (v.measuredHeight <= 0) {
            v.measure(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT)
            val b = Bitmap.createBitmap(v.measuredWidth, v.measuredHeight, Bitmap.Config.ARGB_8888)
            val c = Canvas(b)
            v.layout(0, 0, v.measuredWidth, v.measuredHeight)
            v.draw(c)

            return b
        }

        val b = Bitmap.createBitmap(
            v.layoutParams.width,
            v.layoutParams.height,
            Bitmap.Config.ARGB_8888
        )
        val c = Canvas(b)
        v.layout(v.left, v.top, v.right, v.bottom)
        v.draw(c)
        return b
    }

    @SuppressLint("MissingPermission")
    fun getMyLocation(): LatLng {

        val locationProvider: String = LocationManager.GPS_PROVIDER

        val locationManager = requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager

        val lastKnownLocation: Location = locationManager.getLastKnownLocation(locationProvider)!!

        return LatLng(lastKnownLocation.latitude, lastKnownLocation.longitude)
    }

    // 마커 클릭
    override fun onMarkerClick(marker: Marker): Boolean {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(marker.position, 17f))

        val parkingMarker = viewModel.markers.value?.get(marker)
            ?: return false

        if (parkingMarker.parking_lots.all {
            it.status != "available"
        }) {
            return false
        }

        // show modal bottom sheet
        val markerSheet = MarkerSheetFragment(parkingMarker)
        markerSheet.show(childFragmentManager, MarkerSheetFragment.TAG)
        return true
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.END)) {
            binding.drawerLayout.closeDrawer(GravityCompat.END)
        } else {
            requireActivity().finish()
        }
    }
}