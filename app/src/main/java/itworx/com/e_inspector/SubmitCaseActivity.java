package itworx.com.e_inspector;

import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.view.Menu;
import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import android.Manifest;

import com.google.android.gms.maps.model.MarkerOptions;


public class SubmitCaseActivity extends Activity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, ActivityCompat.OnRequestPermissionsResultCallback {

    private MediaRecorder myRecorder;

    private MediaPlayer myPlayer;
    private String outputFile = null;
    private Button startBtn;
    private Button stopBtn;
    private Button playBtn;
    private Button stopPlayBtn;
    private TextView text;
    private static final int CAMERA_REQUEST = 1888;
    private ImageView imageView;
    private Uri imageToUploadUri;
    private String mCurrentPhotoPath;
    private static final int CAMERA_PHOTO = 111;
    private ProgressDialog pDialog;
    Case mCase = new Case();
    String json = "";
    boolean uploadStatus = false;

    static final int REQUEST_TAKE_PHOTO = 1;
    public static Uri mPhotoFileUri = null;
    public static File mPhotoFile = null;
    public static File mAudioFile = null;

    TextView tvTitle, tvDesc;

    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;

    private Location mLastLocation;

    // Google client to interact with Google API
    private GoogleApiClient mGoogleApiClient;

    // boolean flag to toggle periodic location updates
    private boolean mRequestingLocationUpdates = false;

    private LocationRequest mLocationRequest;

    // Location updates intervals in sec
    private static int UPDATE_INTERVAL = 10000; // 10 sec
    private static int FATEST_INTERVAL = 5000; // 5 sec
    private static int DISPLACEMENT = 10; // 10 meters
    // UI elements
    private TextView lblLocation;

    private LatLng DAVAO = new LatLng(7.0722, 125.6131);
    private GoogleMap map;

    private static final int REQUEST_AUDIO = 0;
    private static final int REQUEST_CAMERA = 1;
    private static final int REQUEST_EXTERNAL_STORAGE = 2;
    private static final int INTERNET = 4;
    private static final int ACCESS_NETWORK_STATE = 5;
    private static final int ACCESS_FINE_LOCATION = 6;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit_case);
        findViews();
    }

    private void checkCameraPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            requestCameraPermission();
        } else
            takePhoto();
    }

    private void requestCameraPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.CAMERA)) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}
                    ,
                    REQUEST_CAMERA);
        }
    }

    private void checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            requestLocationPermission();
        } else if (checkPlayServices())
            buildGoogleApiClient();
    }

    private void requestLocationPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.ACCESS_FINE_LOCATION)) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}
                    ,
                    ACCESS_FINE_LOCATION);
        }
    }

    private void checkWritePermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            requestWritesPermission();
        } else
            checkAudioPermission();
    }

    private void requestWritesPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}
                    ,
                    REQUEST_EXTERNAL_STORAGE);
        }
        else
            checkAudioPermission();
    }

    private void checkAudioPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            requestAudioPermission();
        } else
            setupRecorder();
    }

    private void requestAudioPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.RECORD_AUDIO)) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}
                    ,
                    REQUEST_AUDIO);
        }
        else
        setupRecorder();
    }


    public void doGetLocation(View v) {
        try {
            checkLocationPermission();
        }
        catch(Exception ex){}
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_submit_case, menu);
        return true;
    }


    public void doSubmitCase(View v) {
        try {
            if (mPhotoFile == null)
                Toast.makeText(SubmitCaseActivity.this, "Please select photo", Toast.LENGTH_SHORT).show();
            if (mAudioFile == null)
                Toast.makeText(SubmitCaseActivity.this, "Please Add Voice Note", Toast.LENGTH_SHORT).show();
            else if (tvTitle.getText().toString().trim().equals("") || tvDesc.getText().toString().trim().equals(""))
                Toast.makeText(SubmitCaseActivity.this, "Enter case title / Desc", Toast.LENGTH_SHORT).show();
            else
                prepareCaseAsyncTask(mCase);
        } catch (Exception ex) {
        }
    }


    private void findViews() {
        text = (TextView) findViewById(R.id.tv_record);
        lblLocation = (TextView) findViewById(R.id.tv_location);
        tvTitle = (TextView) findViewById(R.id.et_title);
        tvDesc = (TextView) findViewById(R.id.et_description);
        startBtn = (Button) findViewById(R.id.btnStart);

        stopBtn = (Button) findViewById(R.id.btnStop);
        playBtn = (Button) findViewById(R.id.btnPlay);
        stopPlayBtn = (Button) findViewById(R.id.btnStopPlay);

        this.imageView = (ImageView) this.findViewById(R.id.imageView);
        lblLocation = (TextView) findViewById(R.id.tv_location);
        Button photoButton = (Button) this.findViewById(R.id.btnCapture);

    }


    private void addListners() {

        stopBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                stop();
            }
        });
        playBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                play();
            }
        });

        stopPlayBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                stopPlay();
            }
        });

    }

    public void setupRecorder() {
        try {
            outputFile = Environment.getExternalStorageDirectory()
                    .getAbsolutePath() + "/itworx_inspector.3gpp";
            myRecorder = new MediaRecorder();
            myRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            myRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            myRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
            mAudioFile = createAudioFile();
            Log.d("", "Audio File:" + mAudioFile.getPath());
            myRecorder.setOutputFile(mAudioFile.getPath());
            addListners();
            start();

        } catch (Exception ex) {
            Toast.makeText(SubmitCaseActivity.this, "Device Mic is not recognized:" + ex.getMessage(), Toast.LENGTH_SHORT).show();
        }


    }

    private void start() {
        try {
            myRecorder.prepare();
            myRecorder.start();
        } catch (IllegalStateException e) {
            Log.d("", "cant start recording illegal ");
        } catch (IOException e) {
            Log.d("", "cant start recording");
        }
        text.setText("Recording Point: Recording");
        startBtn.setEnabled(false);
        stopBtn.setEnabled(true);
        Toast.makeText(getApplicationContext(), "Start recording...",
                Toast.LENGTH_SHORT).show();
    }

    public void stop() {
        try {
            myRecorder.stop();
            myRecorder.release();
            myRecorder = null;

            stopBtn.setEnabled(false);
            playBtn.setEnabled(true);
            text.setText("Recording Point: Stop recording");

            Toast.makeText(getApplicationContext(), "Stop recording...",
                    Toast.LENGTH_SHORT).show();
        } catch (IllegalStateException e) {
            // it is called before start()
            e.printStackTrace();
        } catch (RuntimeException e) {
            // no valid audio/video data has been received
            e.printStackTrace();
        }
    }

    public void play() {
        try {
            myPlayer = new MediaPlayer();
            myPlayer.setDataSource(mAudioFile.getPath());
            myPlayer.prepare();
            myPlayer.start();

            playBtn.setEnabled(false);
            stopPlayBtn.setEnabled(true);
            text.setText("Recording Point: Playing");

            Toast.makeText(getApplicationContext(),
                    "Start play the recording...", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void stopPlay() {
        try {
            if (myPlayer != null) {
                myPlayer.stop();
                myPlayer.release();
                myPlayer = null;
                playBtn.setEnabled(true);
                stopPlayBtn.setEnabled(false);
                text.setText("Recording Point: Stop playing");

                Toast.makeText(getApplicationContext(),
                        "Stop playing the recording...", Toast.LENGTH_SHORT)
                        .show();
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


    private File createImageFile() throws IOException {

        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        return image;
    }

    private File createAudioFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "Audio_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".3gpp",         /* suffix */
                storageDir      /* directory */
        );
        return image;
    }


    public void doTakePhoto(View v) {
        checkCameraPermission();
    }

    public void doRecord(View v) {
        checkWritePermission();
    }

    private void takePhoto() {
        try {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            // Ensure that there's a camera activity to handle the intent
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                // Create the File where the photo should go
                try {
                    mPhotoFile = createImageFile();
                } catch (IOException ex) {

                }
                if (mPhotoFile != null) {
                    mPhotoFileUri = Uri.fromFile(mPhotoFile);
                    Log.d("", "einspector:url" + mPhotoFileUri.getPath());
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mPhotoFileUri);
                    startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
                }
            }
        } catch (Exception ex) {
        }
    }


    public void prepareCaseAsyncTask(final Case _case) {
        mCase.agentId = "Jason";
        mCase.caseNumber = "CS:20b3BV";
        mCase.description = tvDesc.getText().toString().trim();
        mCase.title = tvTitle.getText().toString().trim();

        if (mLastLocation != null) {
            mCase.latitude = mLastLocation.getLatitude() + "";
            mCase.longitude = mLastLocation.getLongitude() + "";
        } else {
            mCase.latitude = "";
            mCase.longitude = "";
        }

        final Gson gson = new Gson();
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected void onPreExecute() {
                pDialog = new ProgressDialog(SubmitCaseActivity.this);
                pDialog.setMessage("Please wait...Case is submitting");
                pDialog.setCancelable(false);
                pDialog.show();
            }

            @Override
            protected Void doInBackground(Void... params) {
                if (mPhotoFile != null && mPhotoFileUri != null) {
                    String filePath = mPhotoFileUri.getPath();
                    String fileName = mPhotoFile.getName();
                    String fileNameAudio = mAudioFile.getName();
                    String filePathAudio = mAudioFile.getPath();

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(SubmitCaseActivity.this, "Please wait data is uploading", Toast.LENGTH_LONG).show();
                        }
                    });
                    URI url = AzureBlopManager.uploadBlop(filePath, fileName);
                    URI urlAudio = AzureBlopManager.uploadBlop(filePathAudio, fileNameAudio);

                    mCase.incidentImageURI = url.toString();
                    mCase.incidentAudioURI = urlAudio.toString();
                    json = gson.toJson(mCase);
                    new DatabaseManager(SubmitCaseActivity.this).addCase(mCase);
                    //WebServiceHandler.sendJSON(Constants.CASES_URI, 10000, json);
                    uploadStatus = true;
                } else
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(SubmitCaseActivity.this, "Please select photo", Toast.LENGTH_LONG).show();
                        }
                    });
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                if (pDialog.isShowing())
                    pDialog.dismiss();
                if (uploadStatus)
                    Toast.makeText(SubmitCaseActivity.this, "Case has been submitted", Toast.LENGTH_LONG).show();
                finish();
            }
        }.execute();
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.d("", "location:connected");
        displayLocation();
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    public void displayLocation() {
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
            double latitude = mLastLocation.getLatitude();
            double longitude = mLastLocation.getLongitude();
            lblLocation.setText("Location:" + latitude + ", " + longitude);
        } else {
            lblLocation
                    .setText("(Couldn't get the location. Make sure location is enabled on the device)");
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.i("", "Connection failed: ConnectionResult.getErrorCode() = "
                + result.getErrorCode());
    }

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Toast.makeText(getApplicationContext(),
                        "This device is not supported.", Toast.LENGTH_LONG)
                        .show();
                finish();
            }
            return false;
        }
        return true;
    }

    private void displayGoogleMaps() {
        map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
        DAVAO = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());

        Marker davao = map.addMarker(new MarkerOptions().position(DAVAO).title("Davao City").snippet("Ateneo de Davao University"));
        // zoom in the camera to Davao city
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(DAVAO, 15));
        // animate the zoom process
        map.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CAMERA) {
            Log.i("", "Received response for Camera permission request.");
            if (grantResults.length > 0 && REQUEST_CAMERA == PackageManager.PERMISSION_GRANTED) {
                takePhoto();
            }
        }
        if (requestCode == REQUEST_AUDIO) {
            Log.i("", "Received response for Camera permission request.");
            if (grantResults.length > 0 && REQUEST_AUDIO == PackageManager.PERMISSION_GRANTED) {
                addListners();
            }
        }
        if (requestCode == REQUEST_EXTERNAL_STORAGE) {
            Log.i("", "Received response for Camera permission request.");
            if (grantResults.length > 0 && REQUEST_EXTERNAL_STORAGE == PackageManager.PERMISSION_GRANTED) {

            }
        }
        if (requestCode == ACCESS_FINE_LOCATION) {
            Log.i("", "Received response for Camera permission request.");
            if (grantResults.length > 0 && ACCESS_FINE_LOCATION == PackageManager.PERMISSION_GRANTED) {

            }
        }

    }
}
