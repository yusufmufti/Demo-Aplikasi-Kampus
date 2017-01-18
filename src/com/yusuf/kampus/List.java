package com.yusuf.kampus;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import android.widget.TextView;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.OnItemClickListener;

public class List extends Activity {

	ConnectionDetector cd;
	Boolean isInternetPresent = false;
	AlertDialogManager alert = new AlertDialogManager();

	ProgressDialog pDialog;
	String status = "1";

	JSONArray college = null;
	ListView lve;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.list);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#7f8c8d")));
		
		lve = (ListView) findViewById(R.id.list);

		cekInternet();

		lve.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View view,
					int position, long id) {
				// TODO Auto-generated method stub

				String nama = ((TextView) view.findViewById(R.id.nama))
						.getText().toString();
				String alamat = ((TextView) view.findViewById(R.id.alamat))
						.getText().toString();
				String gambar = ((TextView) view.findViewById(R.id.gambar))
						.getText().toString();

				Intent x = new Intent(getApplicationContext(), Detail.class);
				x.putExtra("nama", nama);
				x.putExtra("alamat", alamat);
				x.putExtra("gambar", gambar);

				startActivity(x);
			}
		});
	}

	public class AmbilData extends AsyncTask<String, String, String> {

		ArrayList<HashMap<String, String>> dataList = new ArrayList<HashMap<String, String>>();

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(List.this);
			pDialog.setMessage("Loading Data ...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		@Override
		protected String doInBackground(String... arg0) {
			// TODO Auto-generated method stub
			String url;
			url = "http://seeyou.web.id/kampus/kampus_indonesia.php";

			JSONParser jParser = new JSONParser();

			JSONObject json = jParser.getJSONFromUrl(url);
			try {
				college = json.getJSONArray("kampus");

				String success = json.getString("success");

				if (success.equals("1")) {

					for (int i = 0; i < college.length(); i++) {
						JSONObject c = college.getJSONObject(i);
						HashMap<String, String> map = new HashMap<String, String>();

						String id = c.getString("id").trim();
						String latitude = c.getString("latitude");
						String longitude = c.getString("longitude");
						String nama = c.getString("nama");
						String alamat = c.getString("alamat");
						String gambar = c.getString("gambar");

						map.put("id", id);
						map.put("nama", nama);
						map.put("latitude", latitude);
						map.put("longitude", longitude);
						map.put("alamat", alamat);
						map.put("gambar", gambar);

						dataList.add(map);
					}
				} else {

					pDialog.dismiss();
					status = "0";

				}

			} catch (JSONException e) {

				pDialog.dismiss();

			}

			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			pDialog.dismiss();
			if (status.equals("0")) {
				Toast.makeText(getApplicationContext(), "data tidak ada",
						Toast.LENGTH_SHORT).show();

			}

			ListAdapter adapter = new SimpleAdapter(getApplicationContext(),
					dataList, R.layout.list_item, new String[] { "nama", "id",
							"latitude", "longitude", "alamat", "gambar" },
					new int[] { R.id.nama, R.id.id, R.id.latitude,
							R.id.longitude, R.id.alamat, R.id.gambar });

			lve.setAdapter(adapter);

		}

	}

	public void cekInternet() {
		cd = new ConnectionDetector(getApplicationContext());
		isInternetPresent = cd.isConnectingToInternet();

		if (isInternetPresent) {

			new AmbilData().execute();

		} else {

			alert.showAlertDialog(List.this, "Peringatan",
					"Internet tidak tersedia, Silakn cek koneksi internet.",
					false);
		}
	}
	
	public boolean onOptionsItemSelected(MenuItem item) {

		int id = item.getItemId();
		if (id == android.R.id.home) {
			finish();
		}
		return super.onOptionsItemSelected(item);
	}

}
