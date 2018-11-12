package com.example.mike.mp3player.client;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.mike.mp3player.R;
import com.example.mike.mp3player.client.callbacks.MyConnectionCallback;
import com.example.mike.mp3player.client.callbacks.MySubscriptionCallback;
import com.example.mike.mp3player.service.MediaPlaybackService;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class MainActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_WRITE_STORAGE = 0;
    private MediaBrowserConnector mediaBrowserConnector;
    private PermissionsProcessor permissionsProcessor;
    private DrawerLayout drawerLayout;
    private RecyclerView recyclerView;
    private static final String LOG_TAG = "MAIN_ACTIVITY";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        permissionsProcessor= new PermissionsProcessor(this);
        permissionsProcessor.requestPermission(WRITE_EXTERNAL_STORAGE);
    }

    public void init() {
        initMediaBrowserService();
        setContentView(R.layout.activity_main);
        this.drawerLayout = findViewById(R.id.drawer_layout);

        MyDrawerListener myDrawerListener = new MyDrawerListener();
        drawerLayout.addDrawerListener(myDrawerListener);


        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        // set item as selected to persist highlight
                        menuItem.setChecked(true);
                        // close drawer when item is tapped
                        drawerLayout.closeDrawers();

                        // Add code here to update the UI based on the item selected
                        // For example, swap UI fragments here

                        return true;
                    }
                });

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public void initRecyclerView(List<MediaBrowserCompat.MediaItem> songs) {
        this.recyclerView = (RecyclerView) findViewById(R.id.myRecyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        MyViewAdapter myViewAdapter = new MyViewAdapter(songs);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(myViewAdapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        myViewAdapter.notifyDataSetChanged();
    }

    private static final int READ_REQUEST_CODE = 42;

    public void sendMessage(View view) {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("audio/*");
        startActivityForResult(intent, READ_REQUEST_CODE);


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode,
                                 Intent resultData) {
        if (requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            Uri uri = null;
            if (resultData != null) {
                uri = resultData.getData();
                Intent intent = new Intent(getApplicationContext(), MediaPlayerActivity.class);
                intent.putExtra("uri", uri);
                startActivityForResult(intent, READ_REQUEST_CODE);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public RecyclerView getRecyclerView() {
        return recyclerView;
    }

    private void initMediaBrowserService() {
        mediaBrowserConnector = new MediaBrowserConnector(this);
        mediaBrowserConnector.init();
        saveState();
    }

    private void saveState()
    {
        if (0 == 0) {
            return;
        }
        try {
            File fileToCache = new File(getApplicationContext().getCacheDir(), "mediaPlayerState");
            if (fileToCache.exists())
            {
                fileToCache.delete();
            }
            fileToCache.createNewFile();
            FileOutputStream fileOut = new FileOutputStream(fileToCache);
            ObjectOutputStream objectOut = new ObjectOutputStream(fileOut);
            //objectOut.writeObject(this.selectedUri);
            objectOut.writeObject(null);
            objectOut.close();
            fileOut.close();

        } catch (IOException e) {
            Log.e(LOG_TAG, e.getMessage());
        }
    }
}
