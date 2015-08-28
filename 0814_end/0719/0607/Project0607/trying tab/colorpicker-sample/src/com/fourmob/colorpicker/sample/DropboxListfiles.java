package com.fourmob.colorpicker.sample;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.DropboxAPI.Entry;
import com.dropbox.client2.exception.DropboxException;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;

public class DropboxListfiles extends AsyncTask<Void, Void, ArrayList<String>> {

	private DropboxAPI<?> dropboxApi;
	private String path;
	private Handler handler;

	public DropboxListfiles(DropboxAPI<?> dropboxApi, String path, Handler handler) {
		this.dropboxApi = dropboxApi;
		this.path = path;
		this.handler = handler;
	}

	@Override
	protected ArrayList<String> doInBackground(Void... params) {
		ArrayList<String> files = new ArrayList<String>();


		//MultiPlayer 폴더의 경로
		String path1 = Environment.getExternalStorageDirectory()
				.getAbsolutePath() + "/MultiPlayer/";
        File file1 = new File(path1);
		try {
			Entry directory = dropboxApi.metadata(path1,1000, null, true, null);

			for (File entry : searchbyFileFilter(file1)) {
				files.add(entry.getName());
			}
		} catch (DropboxException e) {
			e.printStackTrace();
		}

//		// HashSet 데이터 형태로 생성되면서 중복 제거됨
//		HashSet hs = new HashSet(files);
//		// ArrayList 형태로 다시 생성
//		ArrayList<String> newArrList = new ArrayList<String>(hs);
		return files;
	}

	@Override
	protected void onPostExecute(ArrayList<String> result) {
		Message message = handler.obtainMessage();
        Bundle bundle = new Bundle();
		bundle.putStringArrayList("data", result);
		message.setData(bundle);
		handler.sendMessage(message);
	}

    public File[] searchbyFileFilter(File fileList){
        File[] resultList = fileList.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File path, String fileName) {
                return fileName.endsWith(".mp4");
            }
        });
        return resultList;
    }

}