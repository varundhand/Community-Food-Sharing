package activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.foodshare.R;

import database.AuthHelper;
import models.User;
import models.UserType;

public class SplashActivity extends AppCompatActivity {

    VideoView videoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Hide the status bar and navigation bar — full screen
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
        );
        // Hide the action bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        setContentView(R.layout.activity_splash);

        videoView = findViewById(R.id.videoView);

        // Point to your video in res/raw/
        // Replace "splash_video" with your actual filename (no extension)
        Uri videoUri = Uri.parse(
                "android.resource://" + getPackageName() + "/" + R.raw.splash_video
        );
        videoView.setVideoURI(videoUri);

        // When the video finishes playing, go to the next screen
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                goToNextScreen();
            }
        });

        // Safety fallback: if the video fails to load, still navigate after 3 seconds
        videoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                goToNextScreen();
                return true;
            }
        });

        // Start playing as soon as the video is ready
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                // Optional: scale video to fill the screen
                int videoWidth  = mp.getVideoWidth();
                int videoHeight = mp.getVideoHeight();
                int screenWidth  = getResources().getDisplayMetrics().widthPixels;
                int screenHeight = getResources().getDisplayMetrics().heightPixels;

                float scaleX = (float) screenWidth  / videoWidth;
                float scaleY = (float) screenHeight / videoHeight;
                float scale  = Math.max(scaleX, scaleY);

                videoView.setScaleX(scale * videoWidth  / screenWidth);
                videoView.setScaleY(scale * videoHeight / screenHeight);

                videoView.start();
            }
        });
    }

    private void goToNextScreen() {
        // Check if user is already logged in using SharedPreferences
        AuthHelper authHelper = new AuthHelper(this);
        User user = authHelper.getCurrentUser();

        Intent intent;
        if (user != null) {
            // Already logged in — go straight to home
            if (user.getUserType() == UserType.DONOR) {
                intent = new Intent(SplashActivity.this, DonorHomeActivity.class);
            } else if (user.getUserType() == UserType.RECIPIENT) {
                intent = new Intent(SplashActivity.this, RecipientHomeActivity.class);
            } else {
                // invalid user. logout and go LoginActivity
                authHelper.logout();
                intent = new Intent(SplashActivity.this, LoginActivity.class);
            }
        } else {
            // Not logged in — go to login
            intent = new Intent(SplashActivity.this, LoginActivity.class);
        }

        startActivity(intent);
        finish(); // Remove splash from back stack — back button won't return here
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Pause video if user switches apps mid-splash
        if (videoView != null && videoView.isPlaying()) {
            videoView.pause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Resume if they come back
        if (videoView != null && !videoView.isPlaying()) {
            videoView.start();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (videoView != null) {
            videoView.stopPlayback();
        }
    }
}