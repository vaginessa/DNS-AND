package com.burnt_toast.dungeons_n_stuff;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

public class AndroidLauncher extends AndroidApplication implements IActivityRequestHandler {
	private final int SHOW_ADS = 1;
	private final int LOAD_ADS = 0;


	protected Handler handler = new Handler()
	{
		@Override
		public void handleMessage(Message msg) {
			switch(msg.what) {
				case SHOW_ADS:
				{
					if (mAdView.isLoaded()) {
						mAdView.show();
					} else {
						System.err.println("TAG" + "The interstitial wasn't loaded yet.");
					}
					break;
				}
				case LOAD_ADS:
				{
					//loads the ad
					mAdView.loadAd(new AdRequest.Builder().addTestDevice("7C3F02032FA78FA07CB1EB9B7DBF4C07").build());
					break;
				}
			}
		}
	};
	@Override
	public void showAds() {
		handler.sendEmptyMessage(SHOW_ADS);
	}
	@Override
	public void loadAds(){
		handler.sendEmptyMessage(LOAD_ADS);
	}

	private InterstitialAd mAdView;
	private MainFrame main;
	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		//begin ad stuff
		mAdView = new InterstitialAd(this);
		//end ad stuff

		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		initialize(new MainFrame(this), config);

		//Following is test code

		//View gameView = initializeForView(main, config);
		//layout.addView(gameView);

		//ad stuff

		mAdView.setAdUnitId("ca-app-pub-3628918982135598/7603950400");




	}
}
