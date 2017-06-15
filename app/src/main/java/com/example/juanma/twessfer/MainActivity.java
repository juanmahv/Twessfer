package com.example.juanma.twessfer;
import android.app.DialogFragment;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

interface TweetCallback {

    void onUserLists( ArrayList<twitter4j.UserList> lists);
    void postRows(ArrayList<Tweet> rows);
    int currentPage();
    void postAuthenticationUrl(String url);
    void postRequestToken(RequestToken token);
    void postAccessToken(AccessToken token);
    void authenticationFinished();
}


public class MainActivity extends AppCompatActivity implements TweetCallback,  PingDialogFragment.PingDialogListener  {

    static final int OAUTH_REQUEST = 1;
    private String CONSUMER_KEY = "";
    private String CONSUMER_SECRET = "";

    final String tokenFile = ".connection";


    public Context context;
    public Menu mainMenu;
    public Twitter twitter;
    public TweetAdapter adapter;
    private RequestToken requestToken = new RequestToken("","");
    private AccessToken accessToken = new AccessToken("","");
    public Long currentlistId = 0L;
    public int page = 1;
    private ArrayList<twitter4j.UserList> userlists = new ArrayList<>();
    ProgressDialog progress;

    public void startProgressDialog(String title, String message) {

        progress = ProgressDialog.show(this, title,
                message, true);
    }

    public void closeProgressDialog() {

        progress.dismiss();
    }

    @Override
    public int currentPage() {

        return page;
    }

    @Override
    public void onUserLists( ArrayList<twitter4j.UserList> lists) {

        closeProgressDialog();
        userlists = lists;
        int count = 0;
        for (twitter4j.UserList list : lists) {
            String name = list.getName();
            mainMenu.add(Menu.NONE, count++, Menu.NONE, name);
        }
    }

    @Override
    public void postRows(ArrayList<Tweet> rows) {

        closeProgressDialog();
        if (rows.size() > 0) {
            adapter.addAll(rows);
            page++;
        }
    }

    @Override
    public void postRequestToken(RequestToken token) {

        requestToken = token;
    }

    @Override
    public void postAccessToken(AccessToken token) {

        accessToken = token;

        // Save Authentication token for future use
        writeAccessToken(tokenFile, accessToken);

    }

    @Override
    public void postAuthenticationUrl(String url) {

        if (url != "") {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivityForResult(browserIntent, OAUTH_REQUEST);
        }

    }

    @Override
    public void authenticationFinished() {

        // Fill menu with user lists
        getLists();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        mainMenu = menu;
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, mainMenu);
        return super.onCreateOptionsMenu(mainMenu);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        CONSUMER_KEY = getString(R.string.CONSUMER_KEY);
        CONSUMER_SECRET =  getString(R.string.CONSUMER_SECRET);

        context = this;
        ArrayList<Tweet> rows = new ArrayList<>();
        ListView listView = (ListView) findViewById(R.id.tweet_list);
        adapter = new TweetAdapter(context, rows);
        listView.setAdapter(adapter);

        listView.setOnScrollListener(new AbsListView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view,int scrollState) {}

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
                int lastInScreen = firstVisibleItem + visibleItemCount;
                if ( totalItemCount != 0 && (lastInScreen == totalItemCount) ) {

                   if (currentlistId > 0)
                       new ListTimeline(currentlistId, (TweetCallback)context).execute(twitter);
                    else
                       new HomeTimeline((TweetCallback)context).execute(twitter);
                }
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                           int pos, long id) {
                Intent intent = new Intent(context, DisplayStatusActivity.class);
                Tweet currentTweet = adapter.getItem(pos);
                Globals.getInstance().setCurrentTweet(currentTweet);
                startActivity(intent);

                Log.v("long clicked","pos: " + pos);

                return true;
            }
        });


        twitter = TwitterFactory.getSingleton();

        //Set consumer key, consumer secret
        twitter.setOAuthConsumer(CONSUMER_KEY, CONSUMER_SECRET);

        // Get token from file, if any
        AccessToken accessToken = readAccessToken(tokenFile);
        if ( !accessToken.getToken().isEmpty() && !accessToken.getTokenSecret().isEmpty() ) { //not empty

            twitter.setOAuthAccessToken(accessToken);
            authenticationFinished();
        }
        else {

            // Enter Pin Dialog
            openPinDlg();

            // Authentication with OAuth
            new URLRequester(this).execute(twitter);
        }

    }

    private void getLists(){
        // Get lists
        startProgressDialog("", "");
        new ListReader(this).execute(twitter);
    }

     private void openPinDlg() {

        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.addToBackStack(null);
        DialogFragment dialog = new PingDialogFragment();
        dialog.show(ft,"PingDialogFragment");

    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog, String pin) {

        if (!pin.isEmpty()) {
            new SetAccessToken(this,requestToken,pin).execute(twitter);
        }

    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
    }


    private void fillList(twitter4j.UserList list) {
        //Clean existing entries, if any
        page=1;
        adapter.clear();
        adapter.notifyDataSetChanged();
        currentlistId = list.getId();
        startProgressDialog("",list.getName());
        new ListTimeline(currentlistId,(TweetCallback) context ).execute(twitter);
    }

    private void timeline() {
        //Clean existing entries, if any
        page = 1;
        currentlistId = 0L;
        adapter.clear();
        adapter.notifyDataSetChanged();
        startProgressDialog("","Timeline");
        new HomeTimeline((TweetCallback)context).execute(twitter);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_login:
                // User chose the "Settings" item, show the app settings UI...
                return true;
            case R.id.action_timeline:
                timeline();
                return true;
            default:
                int pos = item.getItemId();
                twitter4j.UserList list =  userlists.get(pos);
                fillList(list);
                return true;
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                //return super.onOptionsItemSelected(item);

        }
    }

    private AccessToken readAccessToken(String filename) {

        try {
            FileInputStream input = openFileInput(filename);
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));

            String token = reader.readLine();
            String tokenSecret = reader.readLine();

            return new AccessToken(token,tokenSecret);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return new AccessToken("","");
    }

    private void writeAccessToken(String filename, AccessToken token) {

        final String aToken = token.getToken();
        final String aTokenSecret = token.getTokenSecret();

        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput(filename, Context.MODE_PRIVATE));
            outputStreamWriter.write(aToken);
            outputStreamWriter.write("\n");
            outputStreamWriter.write(aTokenSecret);
            outputStreamWriter.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
