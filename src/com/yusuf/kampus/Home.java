package com.yusuf.kampus;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class Home extends FragmentActivity implements
		android.location.LocationListener, OnMarkerClickListener {

	final int RQS_GooglePlayServices = 1;
	private GoogleMap googleMap;

	double latitude, longitude;
	ProgressDialog pDialog;

	ArrayList<HashMap<String, String>> dataList = new ArrayList<HashMap<String, String>>();


	JSONArray college = null;
	ListView lve;
	Button list, refresh;

	ConnectionDetector cd;
	Boolean isInternetPresent = false;

	AlertDialogManager alert = new AlertDialogManager();

	HashMap<String, String> map;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.home);
		getActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#7f8c8d")));
		

		cekInternet();

		SupportMapFragment fm = (SupportMapFragment) getSupportFragmentManager()
				.findFragmentById(R.id.map);

		googleMap = fm.getMap();

		googleMap.setMyLocationEnabled(true);

		CekGPS();

		refresh = (Button) findViewById(R.id.reload);

		refresh.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				cekInternet();
			}
		});

		list = (Button) findViewById(R.id.list);
		list.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent i = new Intent(getApplicationContext(), List.class);
				startActivity(i);
			}
		});

	}

	public class AmbilData extends AsyncTask<String, String, String> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(Home.this);
			pDialog.setMessage("Loading Data ...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		@Override
		protected String doInBackground(String... arg0) {
			String url;

			url = "http://yusfa.com/pkampus/getAndroid.php";

			JSONParser jParser = new JSONParser();

			JSONObject json = jParser.getJSONFromUrl(url);
			try {
				college = json.getJSONArray("kampus");
				Log.e("error", json.getString("success"));

				for (int i = 0; i <= college.length(); i++) {
					
					JSONObject c = college.getJSONObject(i);
					
					map = new HashMap<String, String>();

					String id_1 = c.getString("id").trim();
					String latitude_1 = c.getString("latitude").trim();
					String longitude_1 = c.getString("longitude").trim();
					String nama_1 = c.getString("nama").trim();
					String alamat_1 = c.getString("alamat").trim();
					String gambar_1 = c.getString("gambar").trim();
					
					map.put("id", id_1);
					map.put("nama", nama_1);
					map.put("latitude", latitude_1);
					map.put("longitude", longitude_1);
					map.put("alamat", alamat_1);
					map.put("gambar", gambar_1);

					dataList.add(map);

				}

			} catch (JSONException e) {

			}

			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			pDialog.dismiss();
			
			LatLng central =null;

			for (int x = 0; x < dataList.size(); x = x + 1) {

				double latasal = Double.parseDouble(dataList.get(x).get(
						"latitude"));
				double longasal = Double.parseDouble(dataList.get(x).get(
						"longitude"));
				LatLng posisi = new LatLng(latasal, longasal);
				String nama = dataList.get(x).get("nama");

				googleMap.addMarker(new MarkerOptions()
						.position(posisi)
						.title(nama)
						.icon(BitmapDescriptorFactory
								.fromResource(R.drawable.marker30)));
				central = posisi;

			}
			
			googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(central,
					12));

		}
	}


	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
		try {
			latitude = location.getLatitude();
			longitude = location.getLongitude();
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	public void CekGPS() {
		try {
			LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
			if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setTitle("info");
				builder.setMessage("Apakah anda akan mengaktifkan GPS?");
				builder.setPositiveButton("Ya",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface arg0, int arg1) {
								// TODO Auto-generated method stub
								Intent i = new Intent(
										android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
								startActivity(i);

							}
						});
				builder.setNegativeButton("Tidak",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog, int arg1) {
								// TODO Auto-generated method stub
								dialog.dismiss();
							}
						});
				builder.create().show();
			}
		} catch (Exception e) {
			// TODO: handle exception

		}
		int status = GooglePlayServicesUtil
				.isGooglePlayServicesAvailable(getBaseContext());
		if (status != ConnectionResult.SUCCESS) {
			int requestCode = 10;
			Dialog dialog = GooglePlayServicesUtil.getErrorDialog(status, this,
					requestCode);
			dialog.show();
		} else {
			
			Criteria criteria = new Criteria();
			LocationManager locationmanager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
			String provider = locationmanager.getBestProvider(criteria, true);
			Location location = locationmanager.getLastKnownLocation(provider);
			
			if (location != null) {
				onLocationChanged(location);
			}
			
			locationmanager.requestLocationUpdates(provider, 500, 0, this);
			LatLng posisi = new LatLng(latitude, longitude);

			/*googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(posisi,
					12));*/
			googleMap.setOnMarkerClickListener(this);
		}
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub

	}

	public void cekInternet() {
		cd = new ConnectionDetector(getApplicationContext());
		isInternetPresent = cd.isConnectingToInternet();

		if (isInternetPresent) {

			new AmbilData().execute();

		} else {

			alert.showAlertDialog(Home.this, "Peringatan",
					"cek koneksi internet.", false);
		}
	}

	@Override
	public boolean onMarkerClick(Marker marker) {
		// TODO Auto-generated method stub
		
		String id= marker.getId();
		id = id.substring(1);
		
		Intent x = new Intent(getApplicationContext(), Detail.class);
		x.putExtra("nama", dataList.get(Integer.parseInt(id)).get("nama"));
		x.putExtra("gambar", dataList.get(Integer.parseInt(id)).get("gambar"));
		x.putExtra("alamat", dataList.get(Integer.parseInt(id)).get("alamat"));
		
		startActivity(x);

		return false;
	}

}
