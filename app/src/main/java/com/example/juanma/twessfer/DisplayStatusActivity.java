package com.example.juanma.twessfer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

public class DisplayStatusActivity extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        Tweet currentTweet = Globals.getInstance().getCurrentTweet();

        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.x = -100;
        params.height = 390;
        params.width = 1000;
        params.y = -50;

        this.getWindow().setAttributes(params);

        setContentView(R.layout.activity_displaystatus);

        Button button = (Button) findViewById(R.id.buttonExit);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                }
            }
        );

        TextView textView = (TextView) findViewById(R.id.statusTextView);
        textView.setText(currentTweet.status.getText());

    }
}
