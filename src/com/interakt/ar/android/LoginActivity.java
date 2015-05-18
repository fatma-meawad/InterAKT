package com.interakt.ar.android;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.AutoCompleteTextView.Validator;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.interakt.ar.networking.ConnectionDetector;
import com.interakt.ar.networking.ServerAPI;

public class LoginActivity extends Activity {

	AutoCompleteTextView username;
	TextView usernameError;
	AutoCompleteTextView password;
	TextView passwordError;
	TextView login;
	private Pattern pattern;
	private Matcher matcher;
	private ProgressBar bar;
	private SharedPreferences mSharedPreferences;
	private Handler h = new Handler();
	private TextView register;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		mSharedPreferences = getApplicationContext().getSharedPreferences(
				"com.mesai.nativecamra", 0);

		bar = (ProgressBar) findViewById(R.id.loading);
		username = (AutoCompleteTextView) findViewById(R.id.login_name);
		usernameError = (TextView) findViewById(R.id.login_name_error);
		username.setValidator(new Validator() {

			@Override
			public boolean isValid(CharSequence text) {
				if (text == null || text.toString().equals("")) {
					username.setBackgroundResource(R.drawable.red_validation_border);
					usernameError.setText("* Email can't be empty.");
					usernameError.setVisibility(View.VISIBLE);
					return false;
				} else {
					if (!isEmailValid(username.getText().toString())) {
						username.setBackgroundResource(R.drawable.red_validation_border);
						usernameError.setText("* Email is correct.");
						usernameError.setVisibility(View.VISIBLE);
						return false;
					} else {
						username.setBackgroundResource(R.drawable.no_validation);
						usernameError.setVisibility(View.GONE);
						return true;
					}
				}
			}

			@Override
			public CharSequence fixText(CharSequence invalidText) {
				// TODO Auto-generated method stub
				return null;
			}
		});
		password = (AutoCompleteTextView) findViewById(R.id.login_password);
		passwordError = (TextView) findViewById(R.id.login_password_error);
		password.setValidator(new Validator() {

			@Override
			public boolean isValid(CharSequence text) {

				if (text == null || text.toString().equals("")) {
					password.setBackgroundResource(R.drawable.red_validation_border);
					passwordError.setText("* Password can't be empty.");
					passwordError.setVisibility(View.VISIBLE);
					return false;
				} else {
					if (text.toString().length() < 6) {
						password.setBackgroundResource(R.drawable.red_validation_border);
						passwordError
								.setText("* password need to be 6 charachters long.");
						passwordError.setVisibility(View.VISIBLE);
						return false;
					} else {
						password.setBackgroundResource(R.drawable.no_validation);
						passwordError.setVisibility(View.GONE);
						return true;
					}
				}
			}

			@Override
			public CharSequence fixText(CharSequence invalidText) {
				// TODO Auto-generated method stub
				return null;
			}
		});

		login = (TextView) findViewById(R.id.login_btn);

		login.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if (username.getValidator().isValid(username.getText())
						&& password.getValidator().isValid(password.getText())) {
					ConnectionDetector cd = new ConnectionDetector(
							getApplicationContext());

					if (!cd.isConnectingToInternet()) {
						Toast.makeText(LoginActivity.this,
								"No Internet Connection !", Toast.LENGTH_LONG).show();
						return;
					}

					new LoginAsyncTask().execute(username.getText().toString(),
							password.getText().toString());
				}

			}
		});

		if (mSharedPreferences.getBoolean("ALREADYLOGGEDIN", false)) {
			startActivity(new Intent(this, InterAKTMain.class));
		}

		register = (TextView) findViewById(R.id.go_to_register_btn);

		register.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				startActivity(new Intent(LoginActivity.this,
						RegisterActivity.class));
			}
		});

	}

	public boolean isEmailValid(String email) {
		String regExpn = "^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
				+ "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
				+ "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
				+ "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
				+ "[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|"
				+ "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$";

		CharSequence inputStr = email;

		pattern = Pattern.compile(regExpn, Pattern.CASE_INSENSITIVE);
		matcher = pattern.matcher(inputStr);

		if (matcher.matches())
			return true;
		else
			return false;
	}

	private class LoginAsyncTask extends AsyncTask<Object, Void, Object> {
		@Override
		protected void onPostExecute(Object result) {
			bar.setVisibility(View.GONE);
			super.onPostExecute(result);
		}

		@Override
		protected void onPreExecute() {
			h.post(new Runnable() {

				@Override
				public void run() {
					bar.setVisibility(View.VISIBLE);

				}
			});

			super.onPreExecute();
		}

		@Override
		protected Object doInBackground(Object... params) {

			String respond = ServerAPI.login((String) params[0],
					(String) params[1]);
			System.out.println(respond);
			if (respond.equals("1")) {
				Editor e = mSharedPreferences.edit();
				e.putString("USERNAME", username.getText().toString());
				e.putString("PASSWORD", password.getText().toString());
				e.putBoolean("ALREADYLOGGEDIN", true);
				e.commit();
				startActivity(new Intent(LoginActivity.this, InterAKTMain.class));
			} else {
				if (respond.equals("0")) {
					h.post(new Runnable() {

						@Override
						public void run() {
							Toast.makeText(LoginActivity.this, "Wrong username or password",Toast.LENGTH_LONG).show();
						}
					});
				}
			}
			return null;
		}

	}

}
