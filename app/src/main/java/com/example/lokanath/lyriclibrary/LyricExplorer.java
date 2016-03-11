package com.example.lokanath.lyriclibrary;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class LyricExplorer extends Activity {

    String artistName = "";
    String songName = "";
    String albumName = "";
    String lyricsContent = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lyric_explorer);

        File wallpaperDirectory = new File("/sdcard/LyricLibrary/");
        wallpaperDirectory.mkdirs();


        ListView lv;
        final ArrayList<String> FilesInFolder = GetFiles("/sdcard/LyricLibrary");

        for (int i = 0; i < FilesInFolder.size(); i++) {
            String str = FilesInFolder.get((i));
            str = str.replace(".txt", "");
            FilesInFolder.set(i, str);
        }
        lv = (ListView) findViewById(R.id.lyricView);

        lv.setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, FilesInFolder));

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                // Clicking on items
                String lyrics = readFile("/sdcard/LyricLibrary/", FilesInFolder.get((int) id));


                int iIndex = -1;
                int eIndex = -1;
                iIndex = lyrics.indexOf("Artist Name : ", 0) + 14;
                eIndex = lyrics.indexOf("\n", iIndex);
                char[] cString = lyrics.toCharArray();
                String tString = "";
                if (iIndex >= 0 && eIndex >= 0 && eIndex > iIndex) {
                    tString = String.copyValueOf(cString, iIndex, eIndex - iIndex);
                    artistName = tString;
                    //Toast.makeText(getApplicationContext(), (artistName), Toast.LENGTH_LONG).show();
                }

                iIndex = -1;
                eIndex = -1;
                iIndex = lyrics.indexOf("Song Title : ", 0) + 13;
                eIndex = lyrics.indexOf("\n", iIndex);
                tString = "";
                if (iIndex >= 0 && eIndex >= 0 && eIndex > iIndex) {
                    tString = String.copyValueOf(cString, iIndex, eIndex - iIndex);
                    songName = tString;
                    //Toast.makeText(getApplicationContext(), (songName), Toast.LENGTH_LONG).show();
                }

                iIndex = -1;
                eIndex = -1;
                iIndex = lyrics.indexOf("Album Name : ", 0) + 13;
                eIndex = lyrics.indexOf("\n", iIndex);
                tString = "";
                if (iIndex >= 0 && eIndex >= 0 && eIndex > iIndex) {
                    tString = String.copyValueOf(cString, iIndex, eIndex - iIndex);
                    albumName = tString;
                    //Toast.makeText(getApplicationContext(), (albumName), Toast.LENGTH_LONG).show();
                }


                iIndex = -1;
                eIndex = -1;
                iIndex = lyrics.indexOf("<<-", 0);
                eIndex = lyrics.length();
                tString = "";
                if (iIndex >= 0 && eIndex >= 0 && eIndex > iIndex) {
                    tString = String.copyValueOf(cString, iIndex, eIndex - iIndex);
                    lyricsContent = tString;
                    //Toast.makeText(getApplicationContext(), (lyricsContent), Toast.LENGTH_LONG).show();
                }

                Intent intent = new Intent(getApplicationContext(), LyricDispay.class);
                intent.putExtra("Lyrics", lyricsContent);
                intent.putExtra("ArtistName", artistName);
                intent.putExtra("SongName", songName);
                intent.putExtra("AlbumName", albumName);
                intent.putExtra("Caller", "FileExplorer");
                startActivity(intent);
            }
        });
    }


    public ArrayList<String> GetFiles(String DirectoryPath) {
        ArrayList<String> MyFiles = new ArrayList<String>();
        File f = new File(DirectoryPath);

        f.mkdirs();
        File[] files = f.listFiles();
        if (files.length == 0)
            return null;
        else {
            for (int i = 0; i < files.length; i++)
                MyFiles.add(files[i].getName());
        }

        return MyFiles;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            Intent intent = new Intent(getApplicationContext(), Main.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }
        return (true);
    }

    public String readFile(String path, String fileName) {
        //Find the directory for the SD Card using the API
        //*Don't* hardcode "/sdcard"
        File sdcard = Environment.getExternalStorageDirectory();

        //Get the text file
        File file = new File(path, fileName + ".txt");
        //Read text from file
        StringBuilder text = new StringBuilder();

        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;

            while ((line = br.readLine()) != null) {
                text.append(line);
                text.append('\n');
            }
            br.close();
        } catch (IOException e) {
            //You'll need to add proper error handling here
        }
        return (text.toString());
    }


}
