package com.example.user.fileapplication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {
    private final String DEFAULT_URL = "https://unsplash.com/photos/S_elJCwbLYs/download?force=true";

    private EditText editText;
    private Button downloadButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText = findViewById(R.id.editText);
        downloadButton = findViewById(R.id.downloadButton);
        downloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, FileService.class);
                intent.setAction(FileService.ACTION_DOWNLOAD_FILE);
                String fileURL = editText.getText().toString().isEmpty() ? DEFAULT_URL : editText.getText().toString();
                intent.putExtra(FileService.EXTRA_FILE_URL, fileURL);
                startService(intent);
            }
        });
    }
}
