package com.example.bloodbuddy;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

public class MainActivity extends Activity {

	private static String jsonResult;
	private static String url = "http://readysteadycode.tk/jsonfetch.php";
	static SharedPreferences sharedpreferences;
	static MainActivity ref;
	OnBackPressedListener backpressedlistener;

	public static final String EXTRA_MESSAGE = "message";
	public static final String PROPERTY_REG_ID = "registration_id";
	private static final String PROPERTY_APP_VERSION = "appVersion";
	private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

	String SENDER_ID = "431833983658";
	GoogleCloudMessaging gcm;
	AtomicInteger msgId = new AtomicInteger();
	SharedPreferences prefs;
	Context context;
	public static final String TAG = "com.example.bloodbuddy";
	String regid;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ref = this;
		sharedpreferences = getSharedPreferences("com.example.bloodbuddy",
				Context.MODE_PRIVATE);
		String restoredText = sharedpreferences.getString("email", null);
		setContentView(R.layout.activity_main_activity_frag);
		context = getApplicationContext();
		if (savedInstanceState == null) {
			if (restoredText != null) {
				getFragmentManager().beginTransaction()
						.add(R.id.container, new LoggedInFragment()).commit();
			} else {
				getFragmentManager().beginTransaction()
						.add(R.id.container, new LoginFragment()).commit();
			}
		}
		if (checkPlayServices()) {
			gcm = GoogleCloudMessaging.getInstance(this);
			regid = getRegistrationId(context);
			Log.d(TAG, regid);
			if (regid.isEmpty()) {
				registerInBackground();
			}
		} else {
			Log.i(TAG, "No valid Google Play Services APK found.");
		}

	}

	@SuppressWarnings("unchecked")
	private void registerInBackground() {
		new AsyncTask<String, Void, String>() {

			@Override
			protected String doInBackground(String... params) {
				String msg = "";
				try {
					if (gcm == null) {
						gcm = GoogleCloudMessaging.getInstance(context);
					}
					regid = gcm.register(SENDER_ID);
					msg = "Device registered, registration ID=" + regid;

					// You should send the registration ID to your server over
					// HTTP,
					// so it can use GCM/HTTP or CCS to send messages to your
					// app.
					// The request to your server should be authenticated if
					// your app
					// is using accounts.
					sendRegistrationIdToBackend();

					// For this demo: we don't need to send it because the
					// device
					// will send upstream messages to a server that echo back
					// the
					// message using the 'from' address in the message.

					// Persist the regID - no need to register again.
					storeRegistrationId(context, regid);
				} catch (IOException ex) {
					msg = "Error :" + ex.getMessage();
					// If there is an error, don't just keep trying to register.
					// Require the user to click a button again, or perform
					// exponential back-off.
				}
				return msg;
			}

			@Override
			protected void onPostExecute(String msg) {
				Log.d(TAG, msg);
			}
		}.execute(null, null, null);
	}

	private void sendRegistrationIdToBackend() {
		// Your implementation here.
	}

	/**
	 * Stores the registration ID and app versionCode in the application's
	 * {@code SharedPreferences}.
	 *
	 * @param context
	 *            application's context.
	 * @param regId
	 *            registration ID
	 */
	private void storeRegistrationId(Context context, String regId) {
		final SharedPreferences prefs = getGCMPreferences(context);
		int appVersion = getAppVersion(context);
		Log.i(TAG, "Saving regId on app version " + appVersion);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString(PROPERTY_REG_ID, regId);
		editor.putInt(PROPERTY_APP_VERSION, appVersion);
		editor.commit();
	}

	/**
	 * Gets the current registration ID for application on GCM service.
	 * <p>
	 * If result is empty, the app needs to register.
	 *
	 * @return registration ID, or empty string if there is no existing
	 *         registration ID.
	 */
	private String getRegistrationId(Context context) {
		final SharedPreferences prefs = getGCMPreferences(context);
		String registrationId = prefs.getString(PROPERTY_REG_ID, "");
		if (registrationId.isEmpty()) {
			Log.i(TAG, "Registration not found.");
			return "";
		}
		// Check if app was updated; if so, it must clear the registration ID
		// since the existing regID is not guaranteed to work with the new
		// app version.
		int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION,
				Integer.MIN_VALUE);
		int currentVersion = getAppVersion(context);
		if (registeredVersion != currentVersion) {
			Log.i(TAG, "App version changed.");
			return "";
		}
		return registrationId;
	}

	/**
	 * @return Application's {@code SharedPreferences}.
	 */
	private SharedPreferences getGCMPreferences(Context context) {
		// This sample app persists the registration ID in shared preferences,
		// but
		// how you store the regID in your app is up to you.
		return getSharedPreferences(MainActivity.class.getSimpleName(),
				Context.MODE_PRIVATE);
	}

	private static int getAppVersion(Context context) {
		try {
			PackageInfo packageInfo = context.getPackageManager()
					.getPackageInfo(context.getPackageName(), 0);
			return packageInfo.versionCode;
		} catch (NameNotFoundException e) {
			// should never happen
			throw new RuntimeException("Could not get package name: " + e);
		}
	}

	// You need to do the Play Services APK check here too.
	@Override
	protected void onResume() {
		super.onResume();
		checkPlayServices();
	}

	/**
	 * Check the device to make sure it has the Google Play Services APK. If it
	 * doesn't, display a dialog that allows users to download the APK from the
	 * Google Play Store or enable it in the device's system settings.
	 */
	private boolean checkPlayServices() {
		int resultCode = GooglePlayServicesUtil
				.isGooglePlayServicesAvailable(this);
		if (resultCode != ConnectionResult.SUCCESS) {
			if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
				GooglePlayServicesUtil.getErrorDialog(resultCode, this,
						PLAY_SERVICES_RESOLUTION_REQUEST).show();
			} else {
				Log.i("com.example.bloodbuddy", "This device is not supported.");
				finish();
			}
			return false;
		}
		return true;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (requestCode == 1) {
			if (resultCode == RESULT_OK) {
				int result = data.getIntExtra("recreate", 0);
				if (result == 33) {
					ref.recreate();
				}
			}
			if (resultCode == RESULT_CANCELED) {
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.logoff) {
			setSharedPrefs(null, null);
		}
		return super.onOptionsItemSelected(item);
	}

	public static String md5(String input) throws NoSuchAlgorithmException {
		String result = input;
		if (input != null) {
			MessageDigest md = MessageDigest.getInstance("MD5"); // or "SHA-1"
			md.update(input.getBytes());
			BigInteger hash = new BigInteger(1, md.digest());
			result = hash.toString(16);
			while (result.length() < 32) { // 40 for SHA-1
				result = "0" + result;
			}
		}
		return result;
	}

	public static void setSharedPrefs(String email, String pwd) {

		Editor editor = sharedpreferences.edit();
		editor.putString("email", email);
		editor.putString("pwd", pwd);
		editor.commit();

		ref.getFragmentManager().beginTransaction()
				.replace(R.id.container, new LoginFragment()).commit();

	}

	public static void setSharedPrefsString(String key, String val) {

		Editor editor = sharedpreferences.edit();
		editor.putString(key, val);
		editor.commit();

		ref.getFragmentManager().beginTransaction()
				.replace(R.id.container, new LoginFragment()).commit();

	}

	public void setOnBackPressedListener(OnBackPressedListener l) {
		this.backpressedlistener = l;
	}

	public interface OnBackPressedListener {
		public void doBack();
	}

	@Override
	public void onBackPressed() {
		if (backpressedlistener != null)
			backpressedlistener.doBack();
		else
			super.onBackPressed();
	}

	public static class LoggedInFragment extends Fragment implements
			OnBackPressedListener {
		public static TextView welcomeText;
		public static TextView donationNum;
		public static TextView lastDonation;
		public static TextView amountDon;
		Button changeInfo;

		public LoggedInFragment() {
			ref.setOnBackPressedListener(this);
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_logged_in,
					container, false);

			MainActivity.executeSQL(
					"extrainfo;" + sharedpreferences.getString("email", null),
					2);

			donationNum = (TextView) rootView.findViewById(R.id.donationNum);
			amountDon = (TextView) rootView.findViewById(R.id.amountDon);
			welcomeText = (TextView) rootView.findViewById(R.id.welcomeText);
			lastDonation = (TextView) rootView.findViewById(R.id.lastDonation);

			changeInfo = (Button) rootView.findViewById(R.id.changeInfo);
			changeInfo.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					ref.getFragmentManager().beginTransaction()
							.replace(R.id.container, new InfoChangeFragment())
							.commit();
				}
			});
			return rootView;
		}

		@Override
		public void doBack() {
			ref.finish();
		}
	}

	public static class InfoChangeFragment extends Fragment implements
			OnBackPressedListener {

		static RadioButton g_0;
		static RadioButton g_a;
		static RadioButton g_b;
		static RadioButton g_ab;
		static ToggleButton rh;
		static ToggleButton gender;
		static Button sendInfo;
		static EditText yob;
		static String bloodType = null;
		static String rhFactor = null;
		static String genderInfo = null;
		static String yearOfBirth = null;

		public InfoChangeFragment() {
			ref.setOnBackPressedListener(this);
		}

		public void getInfo() {

			executeSQL(
					"SELECT * FROM users WHERE email='"
							+ sharedpreferences.getString("email", null) + "'",
					1);
		}

		public static void setInfo(String bT, String rF, String gI, String yOB) {
			bloodType = bT;
			rhFactor = rF;
			genderInfo = gI;
			yearOfBirth = yOB;

			if (bloodType.equalsIgnoreCase("0")) {
				g_0.setChecked(true);
			}
			if (bloodType.equalsIgnoreCase("A")) {
				g_a.setChecked(true);
			}
			if (bloodType.equalsIgnoreCase("B")) {
				g_b.setChecked(true);
			}
			if (bloodType.equalsIgnoreCase("AB")) {
				g_ab.setChecked(true);
			}
			if (rhFactor.equalsIgnoreCase("+")) {
				rh.setChecked(true);
			}
			if (rhFactor.equalsIgnoreCase("-")) {
				rh.setChecked(false);
			}
			if (genderInfo.equalsIgnoreCase("M")) {
				gender.setChecked(true);
			}
			if (genderInfo.equalsIgnoreCase("F")) {
				gender.setChecked(false);
			}
			yob.setText(yearOfBirth);

		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {

			View rootView = inflater.inflate(R.layout.info_change_fragment,
					container, false);

			g_0 = (RadioButton) rootView.findViewById(R.id.groupZero);
			g_a = (RadioButton) rootView.findViewById(R.id.groupA);
			g_b = (RadioButton) rootView.findViewById(R.id.groupB);
			g_ab = (RadioButton) rootView.findViewById(R.id.groupAB);
			rh = (ToggleButton) rootView.findViewById(R.id.rhToggle);
			gender = (ToggleButton) rootView.findViewById(R.id.toggleGender);
			yob = (EditText) rootView.findViewById(R.id.yearOfBirth);
			sendInfo = (Button) rootView.findViewById(R.id.sendInfo);

			getInfo();

			sendInfo.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					if (g_0.isChecked()) {
						bloodType = "0";
					}
					if (g_a.isChecked()) {
						bloodType = "A";
					}
					if (g_b.isChecked()) {
						bloodType = "B";
					}
					if (g_ab.isChecked()) {
						bloodType = "AB";
					}
					if (rh.isChecked()) {
						rhFactor = "+";
					} else {
						rhFactor = "-";
					}
					if (gender.isChecked()) {
						genderInfo = "M";
					} else {
						genderInfo = "F";
					}
					yearOfBirth = yob.getText().toString();

					Log.d("", yearOfBirth);

					executeSQL("UPDATE users SET blood_type='" + bloodType
							+ "', rh='" + rhFactor + "', gender='" + genderInfo
							+ "', yob='" + yearOfBirth + "' WHERE email='"
							+ sharedpreferences.getString("email", null) + "'",
							0);

				}
			});

			// ref.getFragmentManager().beginTransaction()
			// .replace(R.id.container, new LoggedInFragment()).commit();
			return rootView;
		}

		@Override
		public void doBack() {
			ref.getFragmentManager().beginTransaction()
					.replace(R.id.container, new LoggedInFragment()).commit();
		}

	}

	public static class LoginFragment extends Fragment implements
			OnBackPressedListener {

		public LoginFragment() {
			String restoredText = sharedpreferences.getString("email", null);
			if (restoredText != null) {
				getFragmentManager().beginTransaction()
						.add(R.id.container, new LoggedInFragment()).commit();
			}
			ref.setOnBackPressedListener(this);
		}

		Button register;
		Button signIn;
		EditText email, pwd;

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.activity_main, container,
					false);
			register = (Button) rootView.findViewById(R.id.regButton);
			signIn = (Button) rootView.findViewById(R.id.loginButton);
			email = (EditText) rootView.findViewById(R.id.loginEmail);
			pwd = (EditText) rootView.findViewById(R.id.loginPass);
			signIn.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					String _email = email.getText().toString();
					String _pwd = pwd.getText().toString();
					if (_email.length() > 0 && _pwd.length() > 0) {

						accessWebService(_email, _pwd);

					} else {
						Toast.makeText(ref.getApplicationContext(),
								"Unesite sve podatke", Toast.LENGTH_LONG)
								.show();

					}
				}
			});

			register.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {

					startRegistration();

				}
			});
			return rootView;
		}

		protected void startRegistration() {
			Intent intent = new Intent(ref, RegistrationActivity.class);
			startActivityForResult(intent, 1);
		}

		// Async Task to access the web
		public class JsonReadTask extends AsyncTask<String, Void, String> {
			protected String doInBackground(String... params) {
				ArrayList<NameValuePair> postParameters;
				HttpClient httpclient = new DefaultHttpClient();
				HttpPost httppost = new HttpPost(params[0]);
				postParameters = new ArrayList<NameValuePair>();

				String md5 = null;
				try {
					md5 = md5(params[2]);
				} catch (NoSuchAlgorithmException e2) {
					e2.printStackTrace();
				}
				Log.d("", md5);
				postParameters.add(new BasicNameValuePair("sql",
						"SELECT * FROM users WHERE email='" + params[1]
								+ "' AND password='" + md5 + "'"));

				try {
					httppost.setEntity(new UrlEncodedFormEntity(postParameters));
				} catch (UnsupportedEncodingException e1) {
					e1.printStackTrace();
				}
				try {
					HttpResponse response = httpclient.execute(httppost);
					jsonResult = inputStreamToString(
							response.getEntity().getContent()).toString();
					Log.d("", jsonResult);
				}

				catch (ClientProtocolException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				return null;
			}

			private StringBuilder inputStreamToString(InputStream is) {
				String rLine = "";
				StringBuilder answer = new StringBuilder();
				BufferedReader rd = new BufferedReader(
						new InputStreamReader(is));

				try {
					while ((rLine = rd.readLine()) != null) {
						answer.append(rLine);
					}
				}

				catch (IOException e) {
					Toast.makeText(ref, "Error..." + e.toString(),
							Toast.LENGTH_LONG).show();
				}
				return answer;
			}

			@Override
			protected void onPostExecute(String result) {
				parseJSON();
			}
		}

		public void accessWebService(String email, String pwd) {

			JsonReadTask task = new JsonReadTask();
			task.execute(new String[] { url, email, pwd });

		}

		public void parseJSON() {
			if (jsonResult.contains("no_results")) {

				Toast.makeText(ref, "Kriva kombinacija emaila i passworda",
						Toast.LENGTH_LONG).show();

			} else {
				try {
					JSONObject jsonResponse = new JSONObject(jsonResult);
					JSONArray jsonMainNode = jsonResponse.optJSONArray("info");
					for (int i = 0; i < jsonMainNode.length(); i++) {
						JSONObject jsonChildNode = jsonMainNode
								.getJSONObject(i);
						String email = jsonChildNode.getString("email");
						String pwd = jsonChildNode.getString("password");
						login(email, pwd);

					}
				} catch (JSONException e) {
					Toast.makeText(ref, "Error" + e.toString(),
							Toast.LENGTH_SHORT).show();
				}

			}
		}

		public void login(String email, String pwd) {

			Editor editor = sharedpreferences.edit();
			editor.putString("email", email);
			editor.putString("pwd", pwd);
			editor.commit();
			System.out.println("TEST");
			ref.getFragmentManager().beginTransaction()
					.replace(R.id.container, new LoggedInFragment()).commit();

		}

		@Override
		public void doBack() {
			ref.finish();
		}
	}

	private static class JsonReadTask extends AsyncTask<String, Void, String> {
		int operation;

		protected String doInBackground(String... params) {
			operation = Integer.parseInt(params[2]);
			ArrayList<NameValuePair> postParameters;
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(params[0]);
			postParameters = new ArrayList<NameValuePair>();
			postParameters.add(new BasicNameValuePair("sql", params[1]));
			Log.d("", params[1]);

			try {
				httppost.setEntity(new UrlEncodedFormEntity(postParameters));
			} catch (UnsupportedEncodingException e1) {
				e1.printStackTrace();
			}
			try {
				HttpResponse response = httpclient.execute(httppost);
				jsonResult = inputStreamToString(
						response.getEntity().getContent()).toString();
				Log.d("", jsonResult);
			}

			catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return null;
		}

		private static StringBuilder inputStreamToString(InputStream is) {
			String rLine = "";
			StringBuilder answer = new StringBuilder();
			BufferedReader rd = new BufferedReader(new InputStreamReader(is));

			try {
				while ((rLine = rd.readLine()) != null) {
					answer.append(rLine);
				}
			}

			catch (IOException e) {
				Toast.makeText(ref.getApplicationContext(),
						"Error..." + e.toString(), Toast.LENGTH_LONG).show();
			}
			return answer;
		}

		@Override
		protected void onPostExecute(String result) {
			parseJSON(operation);
		}
	}

	public static void executeSQL(String sql, int operation) {
		JsonReadTask task = new JsonReadTask();
		task.execute(new String[] { url, sql, Integer.toString(operation) });

	}

	public static void parseJSON(int operation) {
		switch (operation) {
		case 0:
			if (jsonResult.contains("success")) {

				Toast.makeText(ref, "Podaci uspješno poslani",
						Toast.LENGTH_LONG).show();

			} else {
				Log.d("", jsonResult);
			}
			break;
		case 1:
			try {
				JSONObject jsonResponse = new JSONObject(jsonResult);
				JSONArray jsonMainNode = jsonResponse.optJSONArray("info");
				for (int i = 0; i < jsonMainNode.length(); i++) {
					JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);
					String gender = jsonChildNode.getString("gender");
					if (gender.length() > 0) {
						String blood_type = jsonChildNode
								.getString("blood_type");
						String rh_factor = jsonChildNode.getString("rh");
						String yob = jsonChildNode.getString("yob");
						InfoChangeFragment.setInfo(blood_type, rh_factor,
								gender, yob);
					}
				}
			} catch (JSONException e) {
				Toast.makeText(ref, "Error" + e.toString(), Toast.LENGTH_SHORT)
						.show();
			}
			break;
		case 2:

			String[] extra = jsonResult.toString().split(";");

			LoggedInFragment.donationNum.setText("Donirali ste " + extra[0]
					+ " puta.");
			LoggedInFragment.amountDon.setText("Ukupno ste donirali "
					+ extra[1] + " mL krvi.");
			LoggedInFragment.welcomeText
					.setText("Dobrodošli " + extra[2] + ".");
			LoggedInFragment.lastDonation
					.setText("Zadnja donacija bile je : "
							+ ((extra[3].contains("0000-00-00 00:00:00")) ? "Nikada"
									: extra[3])
							+ "."
							+ ((Integer.parseInt(extra[4].replaceAll(" ", "")) < 1) ? ("\nSljedeæa donacija moguca je veæ sada!")
									: ("\nSljedeæa donacija bit ce moguca za "
											+ extra[4] + " mjeseca.")));

			break;
		default:
			break;
		}
	}
}
