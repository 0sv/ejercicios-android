package com.arkaitzgarro.earthquakes.provider;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.util.Log;

import com.arkaitzgarro.earthquakes.model.EarthQuake;

public class UpdateEarthQuakesTask extends AsyncTask<String, Void, Void> {

	private static final String TAG = "EARTHQUAKE";

	public static interface IUpdateQuakes {
		public void addQuake(EarthQuake q);
	}

	private IUpdateQuakes mContext;

	public UpdateEarthQuakesTask(IUpdateQuakes context) {
		mContext = context;
	}
	
	@Override
	protected Void doInBackground(String... urls) {
		
		JSONObject json;
		URL url;
		
		try {
			url = new URL(urls[0]);
			// Create a new HTTP URL connection
			URLConnection connection = url.openConnection();
			HttpURLConnection httpConnection = (HttpURLConnection) connection;

			int responseCode = httpConnection.getResponseCode();
			if (responseCode == HttpURLConnection.HTTP_OK) {
				try {
					BufferedReader streamReader = new BufferedReader(
							new InputStreamReader(
									httpConnection.getInputStream(), "UTF-8"));
					StringBuilder responseStrBuilder = new StringBuilder();

					String inputStr;
					while ((inputStr = streamReader.readLine()) != null)
						responseStrBuilder.append(inputStr);

					json = new JSONObject(responseStrBuilder.toString());
					JSONArray earthquakes = json.getJSONArray("features");
					
					for (int i = 0; i < earthquakes.length(); i++) {
						new ProcessEarthQuakeTask(mContext).execute(earthquakes.getJSONObject(i));
					}
				} catch (JSONException e) {
					Log.e("EARTHQUAKE",
							"Error al leer el fichero JSON: " + e.getMessage());
				}
			}
		} catch (MalformedURLException e) {
			Log.d("EARTHQUAKE", "Malformed URL Exception.", e);
		} catch (IOException e) {
			Log.d("EARTHQUAKE", "IO Exception.", e);
		}
				
		return null;
	}

}
