package com.example.lokanath.lyriclibrary;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.SocketTimeoutException;
import java.net.URLEncoder;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SearchEngin extends Activity {
    public ProgressDialog chkDlg;
    public int JsoupEx = -1;
    String desc;
    int go = 0;
    String artistName = "";
    String songName = "";
    String[] songUrl = {"", "", "", "", "", "", "", "", "", "", "", ""};
    String[] songTitle = {"", "", "", "", "", "", "", "", "", "", "", ""};
    String[] serverTestString = {"azlyrics", "musixmatch", "metrolyrics", "darklyrics"};
    boolean dispflag[] = {false, false, false, false, false, false, false, false, false, false, false, false};
    String phrasedLyrics = "";
    String correctedString = "";
    String dispArtistName = "";
    String dispSongName = "";
    String dispAlbumName = "";

    private static int showRandomInteger(Random aRandom) {
        //get the range, casting to long to avoid overflow problems
        long range = (long) 1001 - (long) 9998 + 1;
        // compute a fraction of the range, 0 <= frac < range
        long fraction = (long) (range * aRandom.nextDouble());
        int randomNumber = (int) (fraction + 1001);
        return (randomNumber);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_engin);
        final Context context = this;


        int layoutWidth = this.getResources().getDisplayMetrics().widthPixels;
        int layoutHeight = this.getResources().getDisplayMetrics().heightPixels;
        layoutHeight = layoutHeight - getStatusBarHeight();
        //Screen size is computed
        Bitmap bg = BitmapFactory.decodeResource(getResources(), R.drawable.sub_bg);
        bg = getResizedBitmap(bg, layoutHeight + 200, layoutWidth + 200);
        BitmapDrawable drawBG = new BitmapDrawable(context.getResources(), bg);
        RelativeLayout layout = (RelativeLayout) findViewById(R.id.ENGINE_LAYOUT);
        final int sdk = android.os.Build.VERSION.SDK_INT;
        if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
            layout.setBackgroundDrawable(drawBG);

        } else {
            layout.setBackground(drawBG);
        }


        final android.os.Handler handler = new android.os.Handler();
        final Button back;
        back = (Button) findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Toast.makeText(getApplicationContext(), "Library Pressed", Toast.LENGTH_SHORT).show();
                try {
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            //libraryButtonD.setImageBitmap(getResizedBitmap(libButton, (int) (bXY + percentPadding), (int) (bXY + percentPadding)));
                        }
                    }, 50);
                    //Bitmap tsButton = BitmapFactory.decodeResource(getResources(), R.drawable.libraryc);
                    //libraryButtonD.setImageBitmap(getResizedBitmap(tsButton, (int) (bXY + percentPadding), (int) (bXY + percentPadding)));
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "Got You", Toast.LENGTH_SHORT).show();
                }
                v.performHapticFeedback(1);
                Intent intent = new Intent(SearchEngin.this, Main.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });


        final Button search;
        search = (Button) findViewById(R.id.search);
        final EditText _artistName = (EditText) findViewById((R.id.artistName));
        final EditText _songName = (EditText) findViewById((R.id.songName));
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                songName = _songName.getText().toString();
                artistName = _artistName.getText().toString();
                if (songName.isEmpty() == false && artistName.isEmpty() == false) {
                    new Description().execute();
                } else {
                    String respStr = "Song And Artist Details Missing!";
                    if (songName.isEmpty())
                        respStr = "Please Enter The Song Name!";
                    else if (artistName.isEmpty())
                        respStr = "Please Enter The Artist Name!";
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setMessage(respStr)
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    //do things
                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.show();
                }

            }
        });

        final EditText editText = (EditText) findViewById(R.id.artistName);
        editText.requestFocus();
        editText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
                //Toast.makeText(getApplicationContext(), "Got You", Toast.LENGTH_SHORT).show();
            }
        });

        editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
                }
            }
        });


        final EditText editTextS = (EditText) findViewById(R.id.songName);
        editTextS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(editTextS, InputMethodManager.SHOW_IMPLICIT);
                //Toast.makeText(getApplicationContext(), "Got You", Toast.LENGTH_SHORT).show();
            }
        });


        editTextS.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(editTextS, InputMethodManager.SHOW_IMPLICIT);
                }
            }
        });


        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    songName = _songName.getText().toString();
                    artistName = _artistName.getText().toString();
                    if (artistName.isEmpty() == false) {
                        editTextS.requestFocus();
                    } else {
                        String respStr = "Song And Artist Details Missing!";
                        if (artistName.isEmpty())
                            respStr = "Please Enter The Artist Name!";
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setMessage(respStr)
                                .setCancelable(false)
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        editText.requestFocus();
                                    }
                                });
                        AlertDialog alert = builder.create();
                        alert.show();
                    }
                }
                return false;
            }
        });

        editTextS.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    songName = _songName.getText().toString();
                    artistName = _artistName.getText().toString();
                    if (songName.isEmpty() == false && artistName.isEmpty() == false) {
                        new Description().execute();
                    } else {
                        String respStr = "Song And Artist Details Missing!";
                        if (songName.isEmpty())
                            respStr = "Please Enter The Song Name!";
                        else if (artistName.isEmpty())
                            respStr = "Please Enter The Artist Name!";
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setMessage(respStr)
                                .setCancelable(false)
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        //do things
                                    }
                                });
                        AlertDialog alert = builder.create();
                        alert.show();
                    }
                }
                return false;
            }
        });

        final RelativeLayout rl = (RelativeLayout) findViewById((R.id.ENGINE_LAYOUT));
        rl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                //Toast.makeText(getApplicationContext(), "Got You", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public String phraseAZLyrics(String inString) {
        int iIndex = inString.indexOf("<!-- Usage of alyrics.com content by any third-party lyrics provider is prohibited by our licensing agreement. Sorry about that. -->", 0) + 133;
        int eIndex = inString.indexOf("</div>", iIndex);
        String toReturn = "";
        char[] inStringChar = inString.toCharArray();
        if (iIndex < eIndex && iIndex >= 0 && eIndex >= 0) {
            toReturn = String.copyValueOf(inStringChar, iIndex, (eIndex - iIndex));
        }
        //toReturn=Jsoup.parse(toReturn).text();
        toReturn = toReturn.replace("<br>", "");
        toReturn = toReturn.replace("</br>", "");
        toReturn = toReturn.replace("<i>", "");
        toReturn = toReturn.replace("</i>", "");
        toReturn = toReturn.replace("<", "");
        toReturn = toReturn.replace(">", "");
        toReturn = toReturn.replace("=", "");
        String tartistName = "";
        String tsongName = "";
        iIndex = -1;
        eIndex = -1;
        iIndex = inString.indexOf("ArtistName = \"", 0);
        if (iIndex >= 0)
            iIndex = iIndex + 14;
        eIndex = inString.indexOf("\";", iIndex);
        if (iIndex < eIndex && iIndex >= 0 && eIndex >= 0) {
            tartistName = String.copyValueOf(inStringChar, iIndex, (eIndex - iIndex));
            if (tartistName.isEmpty())
                dispArtistName = artistName.toUpperCase();
            else
                dispArtistName = tartistName.toUpperCase();
        }

        iIndex = -1;
        eIndex = -1;
        iIndex = inString.indexOf("SongName = \"", 0);
        if (iIndex >= 0)
            iIndex = iIndex + 12;
        eIndex = inString.indexOf("\";", iIndex);
        if (iIndex < eIndex && iIndex >= 0 && eIndex >= 0) {
            tsongName = String.copyValueOf(inStringChar, iIndex, (eIndex - iIndex));
            if (tsongName.isEmpty())
                dispSongName = songName;
            else
                dispSongName = tsongName;
        }

        int alSindex = inString.indexOf("<!-- album songlists -->", 0);
        if (alSindex >= 0)
            alSindex = alSindex + 24;
        int alEindex = inString.indexOf("<!-- album songlists end -->", 0);
        if (alEindex >= 0)
            alEindex = alEindex + 28;
        String albumStr = "";
        if (alSindex < alEindex) {
            albumStr = String.copyValueOf(inStringChar, alSindex, alEindex - alSindex);
            int ssIndex = albumStr.indexOf("data-toggle=\"collapse\">\"", 0);
            if (ssIndex >= 0)
                ssIndex = ssIndex + 24;
            int eeIndex = albumStr.indexOf("</a>", ssIndex);
            String finString = "";
            char[] albumStrChar = albumStr.toCharArray();
            if (ssIndex >= 0 && eeIndex >= 0 && ssIndex < eeIndex) {
                finString = String.copyValueOf(albumStrChar, ssIndex, eeIndex - ssIndex);
                finString = finString.replace("\"", "");
            }
            if (finString.isEmpty())
                dispAlbumName = "Unknown";
            else
                dispAlbumName = finString;
        }
        return (toReturn);
    }

    public String phraseMetroLyrics(String inString) {

        char[] inStringChar = inString.toCharArray();
        int iIndex = -1;
        int eIndex = -1;
        String musicArtistName = "";
        iIndex = inString.indexOf("\"musicArtistName\":\"", 0);
        if (iIndex >= 0)
            iIndex = iIndex + 18;
        eIndex = inString.indexOf("\",", iIndex);
        if (iIndex >= 0 && eIndex >= 0 && eIndex > iIndex)
            musicArtistName = String.copyValueOf(inStringChar, iIndex, (eIndex - iIndex));
        if (musicArtistName.isEmpty())
            dispArtistName = artistName.toUpperCase();
        else
            dispArtistName = musicArtistName.toUpperCase().replace("\"", "");


        String musicSongTitle = "";
        iIndex = inString.indexOf("\"musicSongTitle\":", 0);
        if (iIndex >= 0)
            iIndex = iIndex + 17;
        eIndex = inString.indexOf("\",", iIndex);
        if (iIndex >= 0 && eIndex >= 0 && eIndex > iIndex)
            musicSongTitle = String.copyValueOf(inStringChar, iIndex, (eIndex - iIndex));
        if (musicSongTitle.isEmpty())
            dispSongName = songName;
        else
            dispSongName = musicSongTitle.replace("\"", "");


        String musicAlbumTitle = "";
        iIndex = inString.indexOf("\"musicAlbumTitle\":", 0);
        if (iIndex >= 0)
            iIndex = iIndex + 18;
        eIndex = inString.indexOf("\",", iIndex);
        if (iIndex >= 16 && eIndex >= 0 && eIndex > iIndex)
            musicAlbumTitle = String.copyValueOf(inStringChar, iIndex, (eIndex - iIndex));
        if (musicAlbumTitle.isEmpty())
            dispAlbumName = "Unknown";
        else
            dispAlbumName = musicAlbumTitle.replace("\"", "");

        String toReturn = "";
        iIndex = inString.indexOf("<div id=\"lyrics-body-text\" class=\"js-lyric-text\">");
        if (iIndex >= 0)
            iIndex = iIndex + 49;
        eIndex = inString.indexOf("<p class=\"writers\">");
        if (iIndex >= 0 && eIndex >= 0 && eIndex > iIndex) {
            toReturn = String.copyValueOf(inStringChar, iIndex, (eIndex - iIndex));
        }

        iIndex = toReturn.indexOf("<div id=\"mid-song-discussion\" class=\"js-sd-middle-disc\">", 0);
        eIndex = toReturn.indexOf("<p class=\"verse\">", iIndex) + 17;
        String replaceString = "";
        if (iIndex >= 0 && eIndex >= 0 && eIndex > iIndex) {
            replaceString = String.copyValueOf(toReturn.toCharArray(), iIndex, (eIndex - iIndex));
        }
        if (replaceString.isEmpty() == false)
            toReturn = toReturn.replace(replaceString, "\r\n");
        toReturn = toReturn.replace("<br>", "\n");
        toReturn = toReturn.replace("</br>", "");
        toReturn = toReturn.replace("<i>", "");
        toReturn = toReturn.replace("</i>", "");
        toReturn = toReturn.replace("<p class=\"verse\">", "\r\n");
        toReturn = toReturn.replace("</p>", "");
        toReturn = toReturn.replace("<div>", "");
        toReturn = toReturn.replace("</div>", "");
        toReturn = toReturn.replace("\n\n", "\n");
        toReturn = toReturn.replace("\r\n\r\n", "\r\n");
        return (toReturn);
    }

    public String phraseMusixMatchLyrics(String inString) {
        char[] inStringChar = inString.toCharArray();
        int iIndex = -1;
        int eIndex = -1;
        String artist_name = "";
        iIndex = inString.indexOf("\"artist_name\":\"", 0) + 15;
        eIndex = inString.indexOf("\",", iIndex);
        if (iIndex >= 0 && eIndex >= 0 && eIndex > iIndex)
            artist_name = String.copyValueOf(inStringChar, iIndex, (eIndex - iIndex));
        if (artist_name.isEmpty())
            dispArtistName = artistName.toUpperCase();
        else
            dispArtistName = artist_name.toUpperCase().replace("\"", "");


        String track_name = "";
        iIndex = inString.indexOf("\"track_name\":\"", 0) + 14;
        eIndex = inString.indexOf("\",", iIndex);
        if (iIndex >= 0 && eIndex >= 0 && eIndex > iIndex)
            track_name = String.copyValueOf(inStringChar, iIndex, (eIndex - iIndex));
        if (track_name.isEmpty())
            dispSongName = songName;
        else
            dispSongName = track_name.replace("\"", "");


        String album_name = "";
        iIndex = inString.indexOf("\"album_name\":\"", 0) + 14;
        eIndex = inString.indexOf("\",", iIndex);
        if (iIndex >= 0 && eIndex >= 0 && eIndex > iIndex)
            album_name = String.copyValueOf(inStringChar, iIndex, (eIndex - iIndex));
        if (track_name.isEmpty())
            dispAlbumName = "Unknown";
        else
            dispAlbumName = album_name.replace("\"", "");

        iIndex = -1;
        eIndex = -1;
        String toReturn = "";
        iIndex = inString.indexOf("$lyrics-body\">", 0) + 14;
        eIndex = inString.indexOf("</span>", iIndex);
        if (iIndex >= 0 && eIndex >= 0 && eIndex > iIndex) {
            toReturn = String.copyValueOf(inStringChar, iIndex, (eIndex - iIndex));
        }
        return (toReturn);
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

    public String getXCharPerLineString(String text, int X) {

        String tenCharPerLineString = "";
        while (text.length() > X) {

            String buffer = text.substring(0, X);
            tenCharPerLineString = tenCharPerLineString + buffer + "\n";
            text = text.substring(X);
        }

        tenCharPerLineString = tenCharPerLineString + text.substring(0);
        return tenCharPerLineString;
    }

    public Bitmap getResizedBitmap(Bitmap bm, int newHeight, int newWidth) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;

        // Create a matrix for the manipulation
        Matrix matrix = new Matrix();

        // Resize the bit map
        matrix.postScale(scaleWidth, scaleHeight);

        // Recreate the new Bitmap
        Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false);
        //Bitmap resizedBitmap=Bitmap.createScaledBitmap(bm,width,height,true);
        return resizedBitmap;

    }

    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    // Description AsyncTask
    private class Description extends AsyncTask<Void, Integer, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            chkDlg = ProgressDialog.show(SearchEngin.this, "Fetching", "Searching Lyrics...Please Wait", true, false);
        }

        @Override
        protected void onProgressUpdate(Integer... progUpdate) {
            if (progUpdate[0] == 1) {
                chkDlg.setTitle("Fetching");
                chkDlg.setMessage("Searching Lyrics...Please Wait");
                chkDlg.show();
            }
            if (progUpdate[0] == 2) {
                chkDlg.setTitle("Fetching");
                chkDlg.setMessage("Obtaining Lyrics...Please Wait");
                chkDlg.show();
            }

            if (progUpdate[0] == 2) {
                chkDlg.setTitle("Fetching");
                chkDlg.setMessage("Obtaining Lyrics...Please Wait");
                chkDlg.show();
            }
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                // Connect to the web site
                String google = "https://www.google.co.in/search?q=";
                //artistName = "Evanscence";
                //songName = "Evanscence";
                String correctName = artistName + " " + songName;
                String corrTestName = "/search?hl";
                String search = artistName + " " + songName + " lyrics";
                String charset = "UTF-8";
                String splitDetails[] = correctName.split(" ");
                String apiUrl = google + URLEncoder.encode(search, charset);

                //URL url = new URL(google + URLEncoder.encode(search, charset));
                //Reader reader = new InputStreamReader(url.openStream(), charset);
                publishProgress(1);
                int timeout = 10000;
                Random random = new Random();
                Document document = Jsoup.connect(apiUrl).userAgent("Nokia" + showRandomInteger(random) + "+/10.0.011 (SymbianOS/9.4; U; Series60/5.0 Mozilla/5.0; Profile/MIDP-2.1 Configuration/CLDC-1.1 ) AppleWebKit/525 (KHTML, like Gecko) Safari/525 3gpp-gba").timeout(timeout).get();
                String title = document.title();
                desc = title + "\r\n";
                Elements links = document.select("a[href]");
                for (Element link : links) {
                    if (link.attr("href").contains("url") == true) {
                        boolean pass = false;
                        String linkStr = link.attr("href");
                        int sIndex = linkStr.indexOf("/url?q=") + 7;
                        int eIndex = linkStr.indexOf("&sa");
                        //Filter links which are not from musixmath or metor or az or dark
                        for (int j = 0; j < serverTestString.length; j++) {
                            if (linkStr.contains(serverTestString[j]) == true) {
                                for (int i = 0; i < songUrl.length; i++) {
                                    if (songUrl[i].isEmpty() == true) {
                                        if ((sIndex >= 0 && eIndex >= 0) && (sIndex < eIndex)) {
                                            String str = linkStr.substring(sIndex, eIndex);
                                            songUrl[i] = str;
                                            songTitle[i] = link.text();
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                    }
                    if (link.attr("href").contains(corrTestName) == true) {
                        desc = desc + "\tCorrect Name : " + link.text();
                        String linkTxt = link.text();
                        linkTxt = linkTxt.replace("lyrics", "");
                        correctName = linkTxt;
                        splitDetails = correctName.split(" ");
                        corrTestName = "/search?>LOKhl";
                    }
                }

                for (int i = 0; i < songUrl.length; i++) {
                    int trueCount = 0;
                    int falseCount = 0;
                    for (int j = 0; j < splitDetails.length; j++) {
                        if (songUrl[i].isEmpty() == false) {
                            Pattern p = Pattern.compile(splitDetails[j], Pattern.CASE_INSENSITIVE);
                            Matcher m = p.matcher(songUrl[i]);
                            Matcher m2 = p.matcher(songTitle[i]);
                            if (m.find() || m2.find())
                                //dispflag[i] = true;
                                trueCount++;
                            else
                                //dispflag[i] = false;
                                falseCount++;
                        }
                    }
                    dispflag[i] = trueCount > falseCount;
                }
                publishProgress(2);
                for (int i = songUrl.length - 1; i >= 0; i--) {
                    if (!songUrl[i].isEmpty()) {
                        if (songUrl[i].contains("azlyrics") && (dispflag[i] == true)) {
                            timeout = 10000;
                            random = new Random();
                            document = Jsoup.connect(songUrl[i]).userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64; rv:40.0) Gecko/20100101 Firefox/40.1").timeout(timeout).get();
                            String docStr = document.toString();
                            title = document.title();
                            phrasedLyrics = phraseAZLyrics(docStr);
                            if (phrasedLyrics.isEmpty() == true || phrasedLyrics.length() <= 10)
                                continue;
                            else {
                                phrasedLyrics = "\r\n<<-Lyrics Starts->>\r\n" + phrasedLyrics + "\r\n" + dispArtistName + " lyrics are property and copyright of their owners. \"" + dispSongName + "\" lyrics provided for educational purposes and personal use onl\r\n";
                                break;
                            }
                        }

                        if (songUrl[i].contains("metrolyrics") && (dispflag[i] == true)) {
                            timeout = 10000;
                            random = new Random();
                            document = Jsoup.connect(songUrl[i]).userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64; rv:40.0) Gecko/20100101 Firefox/40.1").timeout(timeout).get();
                            String docStr = document.toString();
                            title = document.title();
                            phrasedLyrics = phraseMetroLyrics(docStr);
                            if (phrasedLyrics.isEmpty() == true || phrasedLyrics.length() <= 10)
                                continue;
                            else {
                                phrasedLyrics = "\r\n<<--Lyrics Starts-->>\r\n" + phrasedLyrics + "\r\n" + dispArtistName + " lyrics are property and copyright of their owners. \"" + dispSongName + "\" lyrics provided for educational purposes and personal use onl\r\n";
                                break;
                            }
                        }

                        if (songUrl[i].contains("musimatch") && (dispflag[i] == true)) {
                            timeout = 10000;
                            random = new Random();
                            document = Jsoup.connect(songUrl[i]).userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64; rv:40.0) Gecko/20100101 Firefox/40.1").timeout(timeout).get();
                            String docStr = document.text();
                            title = document.title();
                            //String attempt=document.
                            phrasedLyrics = phraseMusixMatchLyrics(docStr);
                            if (phrasedLyrics.isEmpty() == true || phrasedLyrics.length() <= 10)
                                continue;
                            else {
                                phrasedLyrics = "\r\n<<---Lyrics Starts--->>\r\n" + phrasedLyrics + "\r\n" + dispArtistName + " lyrics are property and copyright of their owners. \"" + dispSongName + "\" lyrics provided for educational purposes and personal use only\r\n";
                                //phrasedLyrics=getXCharPerLineString(phrasedLyrics,50);
                                break;
                            }
                        }
                        // break;
                    }
                }
                for (int i = 0; i < songUrl.length; i++) {
                    if (songUrl[i].isEmpty() == false) {
                        //desc = desc + "\nlink : " + songUrl[i];
                        //desc = desc + "\ttext : " + songTitle[i];
                    }
                }


            } catch (IOException e) {
                e.printStackTrace();
                if (e instanceof SocketTimeoutException) {
                    JsoupEx = 3;      //Time out error
                }
            }

            /*catch (JSONException e)
            {
                e.printStackTrace();
            }*/
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if (JsoupEx == 3) {
                Toast.makeText(SearchEngin.this, "Connection Time Out!", Toast.LENGTH_SHORT).show();
            }
            if (chkDlg != null && chkDlg.isShowing()) {
                chkDlg.dismiss();
            }
            if (JsoupEx == -1 && (phrasedLyrics.isEmpty() == false || phrasedLyrics.length() > 250)) {
                Intent intent = new Intent(getApplicationContext(), LyricDispay.class);
                intent.putExtra("Lyrics", phrasedLyrics);
                intent.putExtra("ArtistName", dispArtistName);
                intent.putExtra("SongName", dispSongName);
                intent.putExtra("AlbumName", dispAlbumName);
                intent.putExtra("Caller", "SearchEngine");
                desc = "\0";
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            } else {
                String respStr = "No results found! Please check your internet connection and the search terms";
                AlertDialog.Builder builder = new AlertDialog.Builder(SearchEngin.this);
                builder.setMessage(respStr)
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //do things
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            }
        }
    }

    // ping AsyncTask
    private class ping extends AsyncTask<Void, Void, Void> {
        int mExitValue = -1;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            chkDlg = ProgressDialog.show(SearchEngin.this, "Checking", "Testing Internet Connection...Please Wait", true, false);
        }

        @Override
        protected Void doInBackground(Void... params) {
            Runtime runtime = Runtime.getRuntime();
            try {
                Process mIpAddrProcess = runtime.exec("/system/bin/ping -w 1 8.8.8.8");
                mExitValue = mIpAddrProcess.waitFor();
                BufferedReader reader = new BufferedReader(new InputStreamReader(mIpAddrProcess.getInputStream()));
                int i;
                char[] buffer = new char[4096];
                StringBuffer output = new StringBuffer();
                while ((i = reader.read(buffer)) > 0)
                    output.append(buffer, 0, i);
                reader.close();
                String str = output.toString();
                if (mExitValue == 0) {
                    // Toast.makeText(getApplicationContext(),"Connection ok"+ str, Toast.LENGTH_SHORT).show();

                } else {

                    // Toast.makeText(getApplicationContext(), "Please Check Your Internet Connection"+str, Toast.LENGTH_SHORT).show();
                    //new Description().execute();
                }
            } catch (InterruptedException ignore) {
                ignore.printStackTrace();
                System.out.println(" Exception:" + ignore);
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println(" Exception:" + e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if (mExitValue == 0) {
                new Description().execute();
                chkDlg.dismiss();
                mExitValue = -1;
            } else {
                Toast.makeText(getApplicationContext(), "Internet Problem Detected", Toast.LENGTH_SHORT).show();
                chkDlg.dismiss();
                //new Description().execute();
            }
        }
    }
}

