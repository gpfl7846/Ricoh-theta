package com.theta360.sample.v2;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.theta360.sample.v2.model.DatabaseHandler;
import com.theta360.sample.v2.model.ImageSize;
import com.theta360.sample.v2.network.HttpConnector;
import com.theta360.sample.v2.view.ImageSizeDialog;

import java.util.ArrayList;
import java.util.List;

/*
 * Created by bailey on 3/14/16.
 */
public class SettingActivity extends Activity implements ImageSizeDialog.DialogBtnListener {
    private ListView objectList;
    private ImageSize currentImageSize;
    private String cameraIpAddress;
    private List<String> SettingRows;
    private GetImageSizeTask getImageSizeTask = null;
    ListView lv;
    private String masterfolder;
    // Make sure the request was successful
    //masterfolder folder
    final Context context = this;
    private EditText input;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        lv = (ListView) findViewById(R.id.setting_list);
        //to save folder name
        final DatabaseHandler db = new DatabaseHandler(this);

        ActionBar actionBar = getActionBar();
        //action bar
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#cb002f"))); // 색상 변경(색상코드)﻿
        actionBar.setDisplayShowHomeEnabled(false);

        cameraIpAddress = getResources().getString(R.string.theta_ip_address);
        // Reading all contacts
        Log.d("Reading: ", "Reading all contacts..");
        final String foldername = db.getContactname(1);
        Log.v("foldername", foldername);

        if (getImageSizeTask == null) {
            getImageSizeTask = new GetImageSizeTask();
            getImageSizeTask.execute();
        }

        Intent intent = getIntent();
        //wifi name
        String wifiname = intent.getStringExtra("wifiname");
        Log.v("wifiname", "wifiname     " + wifiname);

        objectList = (ListView) findViewById(R.id.setting_list);

        SettingRows = new ArrayList<>();
        SettingRows.add("Connected      " + wifiname);
        SettingRows.add("Image size      ");
        SettingRows.add("Master folder  " + foldername);
        // Assign adapter to ListView
        //setting set list of setting by ListAdapter
        ArrayAdapter adapter = new ArrayAdapter(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, SettingRows);
        final EditText userInput = (EditText) findViewById(R.id.userInput);
        lv.setAdapter(adapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,
                                    int position, long arg3) {
                // TODO Auto-generated method stub
                int itemPosition = position;
                if (itemPosition == 0) {
                    //와이파이 이름만 나오게 하기
                    String itemValue = (String) lv.getItemAtPosition(position);
                } else if (itemPosition == 1) {
                    //이미지 사이즈가 바뀔떄
                    FragmentManager mgr = getFragmentManager();
                    Log.v("logviewer", "currentImageSize        " + String.valueOf(currentImageSize));
                    Log.v("logviewer", "mgr         " + String.valueOf(mgr));
                    ImageSizeDialog.show(mgr, currentImageSize);

                } else if (itemPosition == 2) {

                    // get prompts.xml view
                    LayoutInflater layoutInflater = LayoutInflater.from(context);

                    View promptView = layoutInflater.inflate(R.layout.activity_prompts, null);

                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

                    // set prompts.xml to be the layout file of the alertdialog builder
                    alertDialogBuilder.setView(promptView);


                    final String foldername = db.getContactname(1);
                    Log.v("foldername", "nulll????? " + foldername);
                    // set prompts.xml to be the layout file of the alertdialog builder
                    alertDialogBuilder.setView(promptView);
                    input = (EditText) promptView.findViewById(R.id.userInput);
                    input.setText(foldername);
                    // setup a dialog window
                    alertDialogBuilder
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    // get user input and set it to result
                                    Log.d("update: ", "Inserting ..");
                                    SettingRows.remove(2);
                                    SettingRows.add(2, "Master folder  " + input.getText());
                                    Log.v("foldername", "update too late " + foldername);
                                    Log.v("foldername", "update too late name " + String.valueOf(input.getText()));
                                    db.updateContact(1, String.valueOf(input.getText()));
                                    //editTextMainScreen.setText(input.getText());
                                   //db.updateName(1, new Folder(1, String.valueOf(input.getText())));

                                }
                            })
                            .setNegativeButton("Cancel",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();
                                        }
                                    });

                    // create an alert dialog
                    AlertDialog alertD = alertDialogBuilder.create();

                    alertD.show();

                } else {
                    // make a toast
                    String itemValue = (String) lv.getItemAtPosition(position);
                    // Show Alert
                    Toast.makeText(getApplicationContext(),
                            "Position :" + itemPosition + "  ListItem : " + itemValue, Toast.LENGTH_LONG)
                            .show();
                }
                // ListView Clicked item value

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }


    @Override
    public void onDialogCommitClick(ImageSize imageSize) {
        currentImageSize = imageSize;
        new ChangeImageSizeTask().execute(currentImageSize);
    }

    //이미지 사이즈 가지고 오는 asyncTask
    private class GetImageSizeTask extends AsyncTask<Void, String, ImageSize> {
        @Override
        protected void onPreExecute() {

        }

        @Override
        protected ImageSize doInBackground(Void... params) {
            publishProgress("get current image size");
            HttpConnector camera = new HttpConnector(cameraIpAddress);
            ImageSize imageSize = camera.getImageSize();
            return imageSize;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            for (String log : values) {
                Log.v("logviewer", "onProgressUpdatee		 " + log);
                //logViewer.append(log);
            }
        }

        @Override
        protected void onPostExecute(ImageSize imageSize) {
            if (imageSize != null) {
                Log.v("logviewer", "new image size: " + imageSize.name());
                //logViewer.append("new image size: " + imageSize.name());
                currentImageSize = imageSize;
                //btnImageSize.setEnabled(true);
            } else {
                Log.v("logviewer", "failed to get image size");
                //logViewer.append("failed to get image size");
            }
        }
    }

    //change the image size   asyncTask
    private class ChangeImageSizeTask extends AsyncTask<ImageSize, String, Void> {
        @Override
        protected void onPreExecute() {
            //  btnImageSize.setEnabled(false);
        }

        @Override
        protected Void doInBackground(ImageSize... size) {
            publishProgress("set image size to " + size[0].name());
            HttpConnector camera = new HttpConnector(cameraIpAddress);
            camera.setImageSize(size[0]);

            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            for (String log : values) {
                Log.v("logviewer", "Update		 " + log);
                //logViewer.append(log);
            }
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            //btnImageSize.setEnabled(true);
            SettingRows.add(1, String.valueOf(currentImageSize));
            Log.v("logviewer", "done");
            //logViewer.append("done");
        }
    }

}
