package com.example.bloodbuddy;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class RegistrationActivity extends Activity {

	SharedPreferences sharedpreferences;
	private String url = "http://readysteadycode.tk/jsonfetch.php";
	private String jsonResult;
	Button registerForm;
	EditText email, pwd1, pwd2, name, sname;
	static String _email, _pwd1, _pwd2, _name, _sname;

	public RegistrationActivity ref;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_registration);
		sharedpreferences = getSharedPreferences("com.example.bloodbuddy",
				Context.MODE_PRIVATE);
		ref = this;
		registerForm = (Button) findViewById(R.id.buttonReg);
		email = (EditText) findViewById(R.id.emailLogin);
		pwd1 = (EditText) findViewById(R.id.pwd1Reg);
		pwd2 = (EditText) findViewById(R.id.pwd2Reg);
		name = (EditText) findViewById(R.id.nameReg);
		sname = (EditText) findViewById(R.id.snameReg);

		registerForm.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				_email = email.getText().toString();
				_pwd1 = pwd1.getText().toString();
				_pwd2 = pwd2.getText().toString();
				_name = name.getText().toString();
				_sname = sname.getText().toString();
				if (_email.length() == 0 || _pwd1.length() == 0
						|| _pwd2.length() == 0 || _name.length() == 0
						|| _sname.length() == 0) {
					Toast.makeText(getApplicationContext(),
							"Unesite sve podatke", Toast.LENGTH_LONG).show();

				}

				if (!_pwd1.equals(_pwd2)) {

					Toast.makeText(getApplicationContext(),
							"Passwordi se ne podudaraju", Toast.LENGTH_LONG)
							.show();
					return;
				}
				if (!isEmailValid(_email)) {

					Toast.makeText(getApplicationContext(),
							"Unesite pravilan email", Toast.LENGTH_LONG).show();
					return;

				}
				accessWebService("SELECT * FROM users WHERE email='" + _email
						+ "'", 0);

			}
		});

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.registration, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		
		return super.onOptionsItemSelected(item);
	}

	// Async Task to access the web
	private class JsonReadTask extends AsyncTask<String, Void, String> {
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

		private StringBuilder inputStreamToString(InputStream is) {
			String rLine = "";
			StringBuilder answer = new StringBuilder();
			BufferedReader rd = new BufferedReader(new InputStreamReader(is));

			try {
				while ((rLine = rd.readLine()) != null) {
					answer.append(rLine);
				}
			}

			catch (IOException e) {
				Toast.makeText(getApplicationContext(),
						"Error..." + e.toString(), Toast.LENGTH_LONG).show();
			}
			return answer;
		}

		@Override
		protected void onPostExecute(String result) {
			parseJSON(operation);
		}
	}

	public void accessWebService(String sql, int operation) {

		JsonReadTask task = new JsonReadTask();
		task.execute(new String[] { url, sql, Integer.toString(operation) });

	}

	public void parseJSON(int operation) {

		String pwdHash = null;
		try {
			pwdHash = MainActivity.md5(_pwd1);

		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		switch (operation) {
		case 0:
			if (jsonResult.contains("no_results")) {
				accessWebService(
						"INSERT INTO users (email,password,name,surname) VALUES ('"
								+ _email + "','" + pwdHash + "','" + _name
								+ "','" + _sname + "')", 1);
			} else {

				Toast.makeText(getApplicationContext(), "Korisnik vec postoji",
						Toast.LENGTH_LONG).show();
			}
			break;
		case 1:
			if (jsonResult.contains("success")) {
				Editor editor1 = sharedpreferences.edit();
				editor1.putString("email", _email);
				editor1.putString("pwd", pwdHash);
				editor1.commit();
				Intent returnIntent = new Intent();
				returnIntent.putExtra("recreate", 33);
				setResult(RESULT_OK, returnIntent);
				ref.finish();
			} else {
				Log.d("", jsonResult);
			}

			break;

		default:
			break;
		}
	}

	public static boolean isEmailValid(String email) {
		boolean isValid = false;

		String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
		CharSequence inputStr = email;

		Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(inputStr);
		if (matcher.matches()) {
			isValid = true;
		}
		return isValid;
	}
}
