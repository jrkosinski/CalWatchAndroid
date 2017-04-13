package com.calwatch.android.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.calwatch.android.R;

/**
 * Created by John R. Kosinski on 25/1/2559.
 * Server-down message.
 */
public class ServerDownActivity extends ActivityBase {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentResId(R.layout.activity_server_down);
        setShowMenu(false);
        setAppBarTitle("Server Down");
        super.onCreate(savedInstanceState);

        Button retryButton = (Button)findViewById(R.id.retryButton);

        retryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ServerDownActivity.this, MainActivity.class);
                intent.setFlags(android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });
    }
}
