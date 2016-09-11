package com.iamdeveloper.pickimage;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import java.io.File;

public class MainActivity extends AppCompatActivity {
    private static final int MY_PERMISSION_READ_EXTERNAL_STORAGE = 1;
    private static final int SELECT_IMAGE = 1;
    private int position;
    private CoordinatorLayout coordinator;
    private VideoView videoView;
    private MediaController mediaController;
    private String realPath;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        coordinator = (CoordinatorLayout) findViewById(R.id.coordinator);
        videoView = (VideoView) findViewById(R.id.video);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Build.VERSION.SDK_INT >= 23 ){
                    Toast.makeText(view.getContext(),""+Build.VERSION.SDK_INT,Toast.LENGTH_LONG).show();
                    if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED) {
                        if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                                Manifest.permission.READ_EXTERNAL_STORAGE)) {
                        }
                        ActivityCompat.requestPermissions(MainActivity.this, new String[]
                                        {Manifest.permission.READ_EXTERNAL_STORAGE},
                                MY_PERMISSION_READ_EXTERNAL_STORAGE);
                        return;
                    }
                    onPickVideo();
                }else{
                    Toast.makeText(view.getContext(),""+Build.VERSION.SDK_INT,Toast.LENGTH_LONG).show();
                    onPickVideo();
                }

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Toast.makeText(this,""+requestCode,Toast.LENGTH_LONG).show();
        switch (requestCode) {
            case MY_PERMISSION_READ_EXTERNAL_STORAGE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    onPickVideo();
                }else{

                    Snackbar.make(coordinator,"คุณต้องทำการอนุญาตให้เขาถึงไฟล์",Snackbar.LENGTH_INDEFINITE)
                            .setAction("อนุญาต", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent i = new Intent();
                                    i.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                    i.setData(Uri.parse("package:" + getPackageName()));
                                    i.addCategory(Intent.CATEGORY_DEFAULT);
                                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                                    i.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                                    startActivity(i);
                                }
                            }).show();

                }

            }
        }
    }

    private void onPickVideo(){
        Intent i = new Intent();
        i.setType("video/*");
        i.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(i,"SELECT IMAGE"),SELECT_IMAGE);
    }

    private void onSettingVideo(String path){
        if(mediaController == null){
            mediaController = new MediaController(this);
            mediaController.setAnchorView(videoView);
            videoView.setMediaController(mediaController);
        }
        videoView.setVideoPath(path);
        videoView.requestFocus();
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(final MediaPlayer mediaPlayer) {
                Log.i("VideoTime",(mediaPlayer.getDuration()/1000) % 60 +"");
                videoView.seekTo(position);
                if (position == 0) {
                    videoView.start();
                }else {
                    videoView.pause();
                }
                mediaPlayer.setOnVideoSizeChangedListener(new MediaPlayer.OnVideoSizeChangedListener() {
                    @Override
                    public void onVideoSizeChanged(MediaPlayer mediaPlayer, int i, int i1) {
                        mediaController.setAnchorView(videoView);
                    }
                });

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            if(requestCode == SELECT_IMAGE){
                if(Build.VERSION.SDK_INT > 19){
                    Uri uri = data.getData();
                    Log.i("requestCode",uri.toString());
                    realPath = RealPath.getRealPatchFromURI_API19(this,uri);
                    Log.i("realpatch",realPath);
                    onSettingVideo(realPath);

                }
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("PATH",realPath);
        outState.putInt("TIME",videoView.getCurrentPosition());
        videoView.pause();
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        position = savedInstanceState.getInt("TIME");
        realPath = savedInstanceState.getString("PATH");
        Log.i("TIME",position+"");
        onSettingVideo(realPath);
        videoView.seekTo(position);

    }
}
