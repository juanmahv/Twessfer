package com.example.juanma.twessfer;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.IOException;
import java.net.URL;
import java.util.concurrent.RunnableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import twitter4j.Status;


public class DisplayStatusActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Tweet currentTweet = Globals.getInstance().getCurrentTweet();

        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.x = -100;
        params.height = 790;
        params.width = 1000;
        params.y = -50;
        this.getWindow().setAttributes(params);


        setContentView(R.layout.activity_displaystatus);

        //Fill avatar
        ImageView imageView = (ImageView) findViewById(R.id.avatar);
        imageView.setImageBitmap(currentTweet.avatar);

        // Fill user name and nickname
        final Status status =  currentTweet.status;
        final twitter4j.User user = status.getUser();
        final String name = user.getName();
        final String nickName = "@"+user.getScreenName();

        TextView textViewName = (TextView) findViewById(R.id.name);
        textViewName.setText(name);

        TextView textViewNick = (TextView) findViewById(R.id.nick);
        textViewNick.setText(nickName);

        Button button = (Button) findViewById(R.id.buttonExit);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                }
            }
        );

        String text = status.getText();
        final String url = extractURL(text);

        TextView statusTextView = (TextView) findViewById(R.id.statusText);
        statusTextView.setText(text);

        TextView urlTextView = (TextView) findViewById(R.id.URLText);
        urlTextView.setText(url);

        WebView webView = (WebView) findViewById(R.id.web);



        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.setWebViewClient(new WebViewClient(){

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });

        webView.loadUrl(url);

    }

    private String extractURL(String text) {

        String res = null;
        final Pattern pattern = Pattern.compile("(https?:\\S*)$");
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            res = matcher.group(1);
        }

        return res;
    }
}
