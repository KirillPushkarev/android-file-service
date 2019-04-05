package com.example.user.fileapplication;

import android.app.IntentService;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Random;


/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 */
public class FileService extends IntentService {
    public static final String ACTION_DOWNLOAD_FILE = "com.example.user.fileapplication.action.DOWNLOAD_FILE";

    public static final String EXTRA_FILE_URL = "com.example.user.fileapplication.extra.FILE_URL";

    public static final String CHANNEL_ID = "com.example.user.fileapplication.notification.CHANNEL_ID";
    public static final String CHANNEL_NAME = "com.example.user.fileapplication.notification.CHANNEL_NAME";
    public static final String CHANNEL_DESCRIPTION = "com.example.user.fileapplication.notification.CHANNEL_DESCRIPTION";

    public FileService() {
        super("FileService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_DOWNLOAD_FILE.equals(action)) {
                String fileURL = intent.getStringExtra(EXTRA_FILE_URL);
                handleActionDownloadFile(fileURL);
            }
        }
    }

    private void handleActionDownloadFile(String urlToDownload) {
        try {
            URL url = new URL(urlToDownload);
            URLConnection connection = url.openConnection();
            connection.connect();

            int fileLength = connection.getContentLength();

            // download the file
            InputStream input = new BufferedInputStream(connection.getInputStream());

            String path = File.createTempFile("Download_", ".jpg", getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)).getPath();
            OutputStream output = new FileOutputStream(path);

            byte data[] = new byte[1024];
            long total = 0;
            int count;
            while ((count = input.read(data)) != -1) {
                total += count;
                output.write(data, 0, count);
            }

            output.flush();
            output.close();
            input.close();

            showNotification(path);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showNotification(String filePath) {
        createNotificationChannel();

        Random random = new Random();
        int notificationId = random.nextInt();

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle("Загрузка файла")
                .setContentText("Загрузка завершена")
                .setPriority(NotificationCompat.PRIORITY_MAX);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(notificationId, builder.build());
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance);
            channel.setDescription(CHANNEL_DESCRIPTION);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}