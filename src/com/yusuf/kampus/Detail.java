package com.yusuf.kampus;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.ImageLoadingListener;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class Detail extends Activity {
	private TextView nama;
	private TextView alamat;
	
	private String url = "http://yusfa.com/pkampus/images/";
	private ImageView image;

	private DisplayImageOptions options;
	private ImageLoader imageLoader;
	private ProgressBar pbar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.detail);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#7f8c8d")));
		

		nama = (TextView) findViewById(R.id.nama);
		alamat = (TextView) findViewById(R.id.alamat);
		image = (ImageView) findViewById(R.id.image);
		pbar = (ProgressBar) findViewById(R.id.pbar);

		Intent i = getIntent();

		nama.setText(i.getStringExtra("nama"));
		alamat.setText(i.getStringExtra("alamat"));
		String gambar = i.getStringExtra("gambar");
		
		loadImageFromURL(url + gambar);

	}

	
	private void loadImageFromURL(String url) {
		options = new DisplayImageOptions.Builder()
				.showStubImage(R.drawable.kampus)
				.showImageForEmptyUrl(R.drawable.kampus).cacheInMemory()
				.cacheOnDisc().build();

		imageLoader = ImageLoader.getInstance();
		imageLoader.init(ImageLoaderConfiguration.createDefault(this));
		imageLoader.displayImage(url, image, options,
				new ImageLoadingListener() {
					@Override
					public void onLoadingComplete() {
						pbar.setVisibility(View.GONE);

					}

					@Override
					public void onLoadingFailed() {

						pbar.setVisibility(View.GONE);
					}

					@Override
					public void onLoadingStarted() {
						pbar.setVisibility(View.VISIBLE);
					}
				});

	}
	
	public boolean onOptionsItemSelected(MenuItem item) {

		int id = item.getItemId();
		if (id == android.R.id.home) {
			finish();
		}
		return super.onOptionsItemSelected(item);
	}
}
