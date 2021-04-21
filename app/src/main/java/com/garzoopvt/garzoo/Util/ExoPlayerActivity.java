package com.garzoopvt.garzoo.Util;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.garzoopvt.garzoo.R;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class ExoPlayerActivity extends AppCompatActivity implements Player.EventListener {
    private static final String TAG = "ExoPlayerActivity";
    private static final String KEY_VIDEO_URI = "video_uri";
    AdView adView,adView1;
   PlayerView videoFullScreenPlayer;

    ProgressBar spinnerVideoDetails;

    public TextView txtView_title, head, tprice, tdescription, timestamp,tvLocation;
    TextView txtView_description, username, type, price;
    ImageView imageViewExit;
    String videoUri;
    SimpleExoPlayer player;
    Handler mHandler;
    Runnable mRunnable;

    public static Intent getStartIntent(Context context, String videoUri) {
        Intent intent = new Intent(context,ExoPlayerActivity.class);
        intent.putExtra(KEY_VIDEO_URI, videoUri);
        return intent;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        this.getWindow()
                .setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
                        WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
        getSupportActionBar().hide();
        //setTheme(R.style.AppTheme_Dialog);
        setContentView(R.layout.activity_exo_player);
        videoFullScreenPlayer =findViewById(R.id.videoFullScreenPlayer);
        spinnerVideoDetails =findViewById(R.id.spinnerVideoDetails);

        videoFullScreenPlayer.setKeepScreenOn(true);

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

        if (getIntent().hasExtra(KEY_VIDEO_URI)) {
            videoUri = getIntent().getStringExtra(KEY_VIDEO_URI);
        }
        setUp();
       // setData();
        getBannerAds();

    }

    private void setData() {
        try {
            setTitle(getIntent().getStringExtra("title"));

            txtView_title = findViewById(R.id.title);
            txtView_description = findViewById(R.id.description);
            username = findViewById(R.id.username);
            type = findViewById(R.id.type);
            price = findViewById(R.id.price);
            timestamp = findViewById(R.id.timestamp);
            tvLocation = findViewById(R.id.tvLocation);
            head = findViewById(R.id.head);
            tprice = findViewById(R.id.tprice);
            tdescription = findViewById(R.id.tdescription);

            username.setText(getIntent().getStringExtra("username"));
            txtView_description.setText(getIntent().getStringExtra("description"));
            txtView_title.setText(getIntent().getStringExtra("title"));
            price.setText(String.format("₹ %1$s", getIntent().getStringExtra("price")));
            timestamp.setText(getIntent().getStringExtra("timestamp"));
            tvLocation.setText(getIntent().getStringExtra("location"));

            String listing_status= getIntent().getStringExtra("listing_status");
            switch (listing_status) {
                case "1":
                    type.setText(String.format("%1$s", getString(R.string.sell_)));
                    break;
                case "2":
                    type.setText(String.format("%1$s", getString(R.string.rentregist)));
                    break;
                case "3":
                    type.setText(String.format("%1$s", getString(R.string.rentrequiremnt)));
                    break;
            }

            String data_type= getIntent().getStringExtra("data_type");
            String pd_status1= getIntent().getStringExtra("pd_status");
            String emp_status1= getIntent().getStringExtra("emp_status");
            String getCategory_id= getIntent().getStringExtra("getCategory_id");
            switch (data_type) {
                case "E":
                    String emp_status = (emp_status1.equals("1")) ? getString(R.string.kam_pahije) : getString(R.string.roj_pahije);
                    type.setText(emp_status);
                    head.setText(String.format("%1$s %2$s", getString(R.string.kala), " : "));
                    tdescription.setText(String.format("%1$s %2$s", getString(R.string.empdescription_), " : "));
                    tprice.setText(String.format("%1$s %2$s", getString(R.string.dar), " : "));
                    break;
                case "B":
                    type.setText(String.format("%1$s %2$s %3$s", "(", getString(R.string.buisness), ")"));
                    head.setText(String.format("%1$s %2$s", getString(R.string.bus_name), " : "));
                    tdescription.setText(String.format("%1$s %2$s", getString(R.string.bus_description), " : "));
                    tprice.setText(String.format("%1$s %2$s", getString(R.string.bus_type), " : "));
                    price.setText(String.format("%1$s", getCategory_id));
                    price.setTextColor(getResources().getColor(R.color.black));
                    break;

                case "P":
                    String pd_status = (pd_status1.equals("1")) ? getString(R.string.jahirat_) : getString(R.string.charcha_);
                    type.setText(pd_status);
                    ;
                    if (pd_status1.equals("1")) {
                        head.setText(String.format("%1$s %2$s", getString(R.string.jahirat_vishay), " : "));
                        tdescription.setText(String.format("%1$s %2$s", getString(R.string.jahirat_varnan), " : "));
                    } else {
                        head.setText(String.format("%1$s %2$s", getString(R.string.charch_vishay), " : "));
                        tdescription.setText(String.format("%1$s %2$s", getString(R.string.charch_varnan), " : "));
                    }
                    price.setVisibility(View.GONE);
                    tprice.setVisibility(View.GONE);
                    break;
                default:
                    head.setText(String.format("%1$s %2$s", getString(R.string.title), " : "));
                    tdescription.setText(String.format("%1$s %2$s", getString(R.string.description_), " : "));
                    tprice.setText(String.format("%1$s %2$s", getString(R.string.price_), " : "));
                    break;
            }
        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
        }


    }

    private void setUp() {
        initializePlayer();
        if (videoUri == null) {
            return;
        }
        buildMediaSource(Uri.parse(videoUri));
    }

    public void onViewClicked() {
        finish();
    }
    private void initializePlayer() {
        try {
            if (player == null) {
                // 1. Create a default TrackSelector
//                LoadControl loadControl = new DefaultLoadControl(
//                        new DefaultAllocator(true, 16),
//                        VideoPlayerConfig.MIN_BUFFER_DURATION,
//                        VideoPlayerConfig.MAX_BUFFER_DURATION,
//                        VideoPlayerConfig.MIN_PLAYBACK_START_BUFFER,
//                        VideoPlayerConfig.MIN_PLAYBACK_RESUME_BUFFER, -

                LoadControl loadControl = new DefaultLoadControl();
                BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
                TrackSelection.Factory videoTrackSelectionFactory =
                        new AdaptiveTrackSelection.Factory(bandwidthMeter);
                TrackSelector trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);


                // 2. Create the player
                player =
                        ExoPlayerFactory.newSimpleInstance(new DefaultRenderersFactory(this), trackSelector,
                                loadControl);
                videoFullScreenPlayer.setPlayer(player);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void buildMediaSource(Uri mUri) {
        try {
            // Measures bandwidth during playback. Can be null if not required.
            DefaultBandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
            // Produces DataSource instances through which media data is loaded.
            DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(this,
                    Util.getUserAgent(this, getString(R.string.app_name)), bandwidthMeter);
            // This is the MediaSource representing the media to be played.
            MediaSource videoSource = new ExtractorMediaSource.Factory(dataSourceFactory)
                    .createMediaSource(mUri);
            // Prepare the player with the source.
            player.prepare(videoSource);
            player.setPlayWhenReady(true);
            player.addListener(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void releasePlayer() {
        if (player != null) {
            player.release();
            player = null;
        }
    }
    private void pausePlayer() {
        if (player != null) {
            player.setPlayWhenReady(false);
            player.getPlaybackState();
        }
    }
    private void resumePlayer() {
        if (player != null) {
            player.setPlayWhenReady(true);
            player.getPlaybackState();
        }
    }
    @Override
    protected void onPause() {
        super.onPause();
        pausePlayer();
        if (mRunnable != null) {
            mHandler.removeCallbacks(mRunnable);
        }
    }
    @Override
    protected void onRestart() {
        super.onRestart();
        resumePlayer();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        releasePlayer();
    }
    @Override
    public void onTimelineChanged(Timeline timeline, Object manifest, int reason) {
    }
    @Override
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {
    }
    @Override
    public void onLoadingChanged(boolean isLoading) {
    }
    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        switch (playbackState) {
            case Player.STATE_BUFFERING:
                spinnerVideoDetails.setVisibility(View.VISIBLE);
                break;
            case Player.STATE_ENDED:
                // Activate the force enable
                break;
            case Player.STATE_IDLE:
                break;
            case Player.STATE_READY:
                spinnerVideoDetails.setVisibility(View.GONE);
                break;
            default:
                // status = PlaybackStatus.IDLE;
                break;
        }
    }
    @Override
    public void onRepeatModeChanged(int repeatMode) {
    }
    @Override
    public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {
    }
    @Override
    public void onPlayerError(ExoPlaybackException error) {
    }
    @Override
    public void onPositionDiscontinuity(int reason) {
    }
    @Override
    public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {
    }
    @Override
    public void onSeekProcessed() {
    }



    private void getBannerAds() {

//        final AdView adView = new AdView(context);
//        adView.setAdSize(AdSize.BANNER);
//        adView.setAdUnitId(BANNER_ID);
        adView = findViewById(R.id.adView);
        adView.loadAd(new AdRequest.Builder().build());
        adView1 = findViewById(R.id.adView1);
        adView1.loadAd(new AdRequest.Builder().build());

    }



}