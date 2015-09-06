package com.fourmob.colorpicker.sample;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.exception.DropboxException;
import com.dropbox.client2.session.AccessTokenPair;
import com.dropbox.client2.session.AppKeyPair;
import com.dropbox.client2.session.Session.AccessType;
import com.dropbox.client2.session.TokenPair;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;

public class Dropboxmain extends Activity implements OnClickListener {

	private LinearLayout container;
	private DropboxAPI<AndroidAuthSession> dropboxApi;
	private boolean isUserLoggedIn;
	private Button loginBtn;
	private Button uploadFileBtn;
	private Button listFilesBtn;

	private final static String DROPBOX_FILE_DIR = "/AndroidDropboxImplementationExample/";
	private final static String DROPBOX_NAME = "dropbox_prefs";
	private final static String ACCESS_KEY = "mi2rs6otzb3inwd";
	private final static String ACCESS_SECRET = "ei7aowakatdc3um";
	private final static AccessType ACCESS_TYPE = AccessType.DROPBOX;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setting);
		RecordTimeTable_Helper helper
				= new RecordTimeTable_Helper(Dropboxmain.this);


		loginBtn = (Button) findViewById(R.id.loginBtn);
		loginBtn.setOnClickListener(this);
		uploadFileBtn = (Button) findViewById(R.id.uploadFileBtn);
		uploadFileBtn.setOnClickListener(this);
		listFilesBtn = (Button) findViewById(R.id.listFilesBtn);
		listFilesBtn.setOnClickListener(this);
		container = (LinearLayout) findViewById(R.id.container_files);

			loggedIn(false);
			AppKeyPair appKeyPair = new AppKeyPair(ACCESS_KEY, ACCESS_SECRET);
			AndroidAuthSession session;
			SharedPreferences prefs = getSharedPreferences(DROPBOX_NAME, 0);
			String key = prefs.getString(ACCESS_KEY, null);
			String secret = prefs.getString(ACCESS_SECRET, null);

			if (key != null && secret != null) {
				AccessTokenPair token = new AccessTokenPair(key, secret);
				session = new AndroidAuthSession(appKeyPair, ACCESS_TYPE, token);
				Toast.makeText(Dropboxmain.this, "아무것도 없을떄?뭐여및니", Toast.LENGTH_SHORT).show();

			} else{
				session = new AndroidAuthSession(appKeyPair, ACCESS_TYPE);
				Toast.makeText(Dropboxmain.this, "처음로그인할떄", Toast.LENGTH_SHORT).show();
			}
			dropboxApi = new DropboxAPI<AndroidAuthSession>(session);






	}

	@Override
	protected void onResume() {
		super.onResume();
		AndroidAuthSession session = dropboxApi.getSession();
		RecordTimeTable_Helper helper
				= new RecordTimeTable_Helper(Dropboxmain.this);
		if (session.authenticationSuccessful()) {
			try {
				session.finishAuthentication();
				TokenPair tokens = session.getAccessTokenPair();

				SharedPreferences prefs = getSharedPreferences(DROPBOX_NAME, 0);
				SharedPreferences.Editor editor = prefs.edit();
				editor.putString(ACCESS_KEY, tokens.key);
				editor.putString(ACCESS_SECRET, tokens.secret);
				editor.commit();
				//helper.insertID(tokens.key,tokens.secret);
				loggedIn(true);
				Toast.makeText(Dropboxmain.this, "로그인한후에핳핳", Toast.LENGTH_SHORT).show();
				Log.v("session", "key0" + tokens.key);
				Log.v("session", "secret0" + tokens.secret);


			} catch (IllegalStateException e) {
				Toast.makeText(this, "Error during Dropbox authentication",
						Toast.LENGTH_SHORT).show();
			}
		}

	}

	private final Handler handler = new Handler() {
		public void handleMessage(Message message) {
			ArrayList<String> result = message.getData().getStringArrayList("data");

			for (String fileName : result) {
				TextView textView = new TextView(Dropboxmain.this);
				textView.setText(fileName);
				container.addView(textView);
			}
		}
	};

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.loginBtn:
				if (isUserLoggedIn) {
					dropboxApi.getSession().unlink();
					loggedIn(false);
				} else {
					dropboxApi.getSession().startAuthentication(Dropboxmain.this);
				}
				break;
			case R.id.uploadFileBtn:
				DropboxUploadFile uploadFile = new DropboxUploadFile(this, dropboxApi,
						DROPBOX_FILE_DIR);
				uploadFile.execute();
				break;
			case R.id.listFilesBtn:
				DropboxListfiles listFiles = new DropboxListfiles(dropboxApi, DROPBOX_FILE_DIR,
						handler);
				listFiles.execute();

				break;
			default:
				break;
		}
	}

	private void loggedIn(boolean userLoggedIn) {
		isUserLoggedIn = userLoggedIn;
		uploadFileBtn.setEnabled(userLoggedIn);
		uploadFileBtn.setBackgroundColor(userLoggedIn ? Color.BLUE : Color.GRAY);
		listFilesBtn.setEnabled(userLoggedIn);
		listFilesBtn.setBackgroundColor(userLoggedIn ? Color.BLUE : Color.GRAY);
		loginBtn.setText(userLoggedIn ? "Logout" : "Log in");
	}


	public class DropboxUploadFile extends AsyncTask<Void, Long, Boolean> {

		private DropboxAPI<?> dropboxApi;
		private String path;
		private Context context;

		public DropboxUploadFile(Context context, DropboxAPI<?> dropboxApi,
								 String path) {
			this.context = context.getApplicationContext();
			this.dropboxApi = dropboxApi;
			this.path = path;
		}

		@Override
		protected Boolean doInBackground(Void... params) {

			final File tempDropboxDirectory = context.getCacheDir();
			//MultiPlayer 폴더의 경로
			//for(getAllSubject())
			RecordTimeTable_Helper helper
					= new RecordTimeTable_Helper(Dropboxmain.this);
			Log.v("session", " 전체과목  " + helper.getAllSubject());
			ArrayList<String> subject = helper.getAllSubject();
			Log.v("session", " 과목사이즈 " + subject.size());
			if(subject.size()!=0) {

				for (String subname : subject) {
					String path1 = Environment.getExternalStorageDirectory()
							.getAbsolutePath() + "/MultiPlayer/" + subname + "/";
					File file1 = new File(path1);
					Log.v("session", " 파일경로 " + path1);
					File tempFileToUploadToDropbox;
					try {
						// Creating a temporal file.어떤 파일안에 넣을지
						tempFileToUploadToDropbox = File.createTempFile("MultiPlayer", null, tempDropboxDirectory);
						//searchbyFileFilter(file1) MultiPlayer 폴더안에 mp4파일이 올라간다.
						for (File file : searchbyFileFilter(file1)) {

							FileInputStream fileInputStream = new FileInputStream(file);
							//똑같은 파일이 있으면 올라가지 않는다.
							Log.v("session", " 파일이름 " + file);
							dropboxApi.putFileOverwrite(path + file, fileInputStream,
									tempFileToUploadToDropbox.length(), null);

						}

						tempFileToUploadToDropbox.delete();

						return true;
					} catch (IOException e) {
						e.printStackTrace();
					} catch (DropboxException e) {
						e.printStackTrace();
					}
					return false;
				}
			}
			return false;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			if (result) {
				Toast.makeText(context, "파일이 성공적으로 업로드되었습니다.",
						Toast.LENGTH_LONG).show();
			} else {
				Toast.makeText(context, "업로드에 실패 했습니다.",
						Toast.LENGTH_LONG).show();
			}
		}

		public File[] searchbyFileFilter(File fileList){
			File[] resultList = fileList.listFiles( new FilenameFilter(){
				@Override
				public boolean accept(File path, String fileName){
					return fileName.endsWith(".mp4");
				}
			});
			return resultList;
		}


	}
}