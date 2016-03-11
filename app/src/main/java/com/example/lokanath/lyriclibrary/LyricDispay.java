package com.example.lokanath.lyriclibrary;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;

public class LyricDispay extends Activity {
    double layoutHeight = 0;
    double layoutWidth = 0;
    double ppi = 0;
    String Caller = "Unknown";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lyric_dispay);
        //Get Screen Size
        ppi = getResources().getDisplayMetrics().density;
        layoutWidth = (this.getResources().getDisplayMetrics().widthPixels) / ppi;
        layoutHeight = (this.getResources().getDisplayMetrics().heightPixels);
        layoutHeight = (layoutHeight - getStatusBarHeight()) / ppi;
        TextView detailView = (TextView) findViewById(R.id.lyricdisplayTV);
        detailView.setTextSize((float) ((layoutHeight * .15) / 4.5));
        detailView.setHeight((int) ((layoutHeight * .15) * (ppi)));

        TextView lyricView = (TextView) findViewById(R.id.lyricdispalyLyric);
        //lyricView.setMovementMethod(new ScrollingMovementMethod());
        if (layoutHeight > layoutHeight)
            lyricView.setTextSize((float) ((layoutWidth * .15) / 4.5));
        else
            lyricView.setTextSize((float) ((layoutHeight * .15) / 4.5));

        //Toast.makeText(getApplicationContext(), (layoutHeight) + " "+(layoutWidth), Toast.LENGTH_LONG).show();
        //Toast.makeText(getApplicationContext(), ((layoutHeight*.15)) + " ", Toast.LENGTH_LONG).show();

        String Lyrics = "Unknown";
        String ArtistName = "Unknown";
        String SongName = "Unknown";
        String AlbumName = "Unknown";
        Lyrics = getIntent().getExtras().getString("Lyrics");
        ArtistName = getIntent().getExtras().getString("ArtistName");
        SongName = getIntent().getExtras().getString("SongName");
        AlbumName = getIntent().getExtras().getString("AlbumName");
        Caller = getIntent().getExtras().getString("Caller");

        final String fArtistName = ArtistName;
        final String fSongName = SongName;
        //Format details
        String formatedDetails = "";
        formatedDetails = " Artist Name : " + ArtistName + "\r\n Song Title : " + SongName + "\r\n Album Name : " + AlbumName;
        detailView.setText(formatedDetails);
        lyricView.setText(Lyrics);
        final String fileDetails = formatedDetails + "\r\n" + Lyrics;

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_save);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    // create a File object for the parent directory
                    File wallpaperDirectory = new File("/sdcard/LyricLibrary/");
                    // have the object build the directory structure, if needed.
                    wallpaperDirectory.mkdirs();

                    String fleName = fArtistName + "-" + fSongName;
                    File myFile = new File("/sdcard/LyricLibrary/" + fleName + ".txt");
                    if (myFile.exists())
                        Snackbar.make(view, "File Already Exists", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                    else {
                        myFile.createNewFile();
                        FileOutputStream fOut = new FileOutputStream(myFile);
                        OutputStreamWriter myOutWriter =
                                new OutputStreamWriter(fOut);
                        myOutWriter.write(fileDetails, 0, fileDetails.length());
                        myOutWriter.close();
                        fOut.close();
                        Snackbar.make(view, "File Saved LyricLibrary Directory", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(getBaseContext(), e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            if (Caller.contains("SearchEngine") == true) {
                Intent intent = new Intent(getApplicationContext(), SearchEngin.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }

            if (Caller.contains("FileExplorer") == true) {
                Intent intent = new Intent(getApplicationContext(), LyricExplorer.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        }
        return (true);
    }
}
