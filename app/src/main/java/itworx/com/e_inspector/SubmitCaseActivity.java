package itworx.com.e_inspector;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
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

import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SubmitCaseActivity extends Activity {

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

    // UI elements
    private TextView lblLocation;

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
    //private GoogleApiClient mGoogleApiClient;

    // boolean flag to toggle periodic location updates
    private boolean mRequestingLocationUpdates = false;

    //private LocationRequest mLocationRequest;

    // Location updates intervals in sec
    private static int UPDATE_INTERVAL = 10000; // 10 sec
    private static int FATEST_INTERVAL = 5000; // 5 sec
    private static int DISPLACEMENT = 10; // 10 meters


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit_case);
        setup();
//        if (checkPlayServices()) {
//            createLocationRequest();
//        }
    }


    @Override
    protected void onResume() {
        super.onResume();
//        if (mGoogleApiClient.isConnected() && mRequestingLocationUpdates) {
//            startLocationUpdates();
//        }

    }

    @Override
    protected void onPause() {
        super.onPause();
//        stopLocationUpdates();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_submit_case, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_send) {
            if (mPhotoFile == null)
                Toast.makeText(SubmitCaseActivity.this, "Please select photo", Toast.LENGTH_SHORT).show();
            else if (tvTitle.getText().toString().trim().equals("") || tvDesc.getText().toString().trim().equals(""))
                Toast.makeText(SubmitCaseActivity.this, "Enter case title / Desc", Toast.LENGTH_SHORT).show();
            else
                submitCaseAsyncTask(mCase);
        }

        return super.onOptionsItemSelected(item);
    }

    public void submitCase(View v) {
        if (mPhotoFile == null)
            Toast.makeText(SubmitCaseActivity.this, "Please select photo", Toast.LENGTH_SHORT).show();
        else if (tvTitle.getText().toString().trim().equals("") || tvDesc.getText().toString().trim().equals(""))
            Toast.makeText(SubmitCaseActivity.this, "Enter case title / Desc", Toast.LENGTH_SHORT).show();
        else
            submitCaseAsyncTask(mCase);
    }

    public void setup() {
        outputFile = Environment.getExternalStorageDirectory()
                .getAbsolutePath() + "/itworx_inspector.3gpp";

        myRecorder = new MediaRecorder();
        myRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        myRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        myRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        try {
            mAudioFile = createAudioFile();
            Log.d("", "Audio File:" + mAudioFile.getPath());
            myRecorder.setOutputFile(mAudioFile.getPath());
        } catch (Exception ex) {
        }

        text = (TextView) findViewById(R.id.tv_record);
        lblLocation = (TextView) findViewById(R.id.textView);
        tvTitle = (TextView) findViewById(R.id.editText);
        tvDesc = (TextView) findViewById(R.id.editText2);

        startBtn = (Button) findViewById(R.id.btnStart);
        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                start(v);
            }
        });

        stopBtn = (Button) findViewById(R.id.btnStop);
        stopBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                stop(v);
            }
        });

        playBtn = (Button) findViewById(R.id.btnPlay);
        playBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                play(v);
            }
        });

        stopPlayBtn = (Button) findViewById(R.id.btnStopPlay);
        stopPlayBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                stopPlay(v);
            }
        });

        this.imageView = (ImageView) this.findViewById(R.id.imageView);
        lblLocation = (TextView) findViewById(R.id.textView2);
        Button photoButton = (Button) this.findViewById(R.id.btnCapture);
        photoButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                takePicture(v);

            }
        });
    }

    public void start(View view) {
        try {
            myRecorder.prepare();
            myRecorder.start();
        } catch (IllegalStateException e) {
            // start:it is called before prepare()
            // prepare: it is called after start() or before setOutputFormat()
            e.printStackTrace();
        } catch (IOException e) {
            // prepare() fails
            e.printStackTrace();
        }

        text.setText("Recording Point: Recording");
        startBtn.setEnabled(false);
        stopBtn.setEnabled(true);

        Toast.makeText(getApplicationContext(), "Start recording...",
                Toast.LENGTH_SHORT).show();
    }

    public void stop(View view) {
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

    public void play(View view) {
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

    public void stopPlay(View view) {
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


    public void takePicture(View v) {
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
    }

    public void sendCase() {
        try {
            //Upload Image
            final String filePath = mPhotoFileUri.getPath();
            final String fileName = mPhotoFile.getName();
            final String fileNameAudio = mAudioFile.getName();
            final String filePathAudio = mAudioFile.getPath();

            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... voids) {
                    URI url = AzureBlopManager.uploadBlop(filePath, fileName);
                    URI urlAudio = AzureBlopManager.uploadBlop(filePathAudio, fileNameAudio);
                    Log.d("", "url_videio" + url);
                    Log.d("", "url_audio" + urlAudio);

                    mCase.incidentImageURI = url.toString();
                    mCase.incidentAudioURI = urlAudio.toString();
                    Log.d("", "after taken 1:" + mCase.incidentImageURI);
                    return null;
                }
            }.execute();
        } catch (Exception ex) {
            Log.d("", "error:" + ex.getMessage());
        }
    }

    public void submitCaseAsyncTask(final Case _case) {
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

//    @Override
//    public void onConnected(Bundle bundle) {
//        Log.d("","location:connected");
//        displayLocation();
//        startLocationUpdates();
//    }
//
//    @Override
//    public void onConnectionSuspended(int i) {
//
//    }
//
//    @Override
//    public void onLocationChanged(Location location) {
//// Assign the new location
//        mLastLocation = location;
//
//        Toast.makeText(getApplicationContext(), "Location changed!",
//                Toast.LENGTH_SHORT).show();
//        // Displaying the new location on UI
//        displayLocation();
//    }
//
//    private void displayLocation() {
//        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
//        if (mLastLocation != null) {
//            double latitude = mLastLocation.getLatitude();
//            double longitude = mLastLocation.getLongitude();
//            lblLocation.setText("Location:"+latitude + ", " + longitude);
//        } else {
//            lblLocation
//                    .setText("(Couldn't get the location. Make sure location is enabled on the device)");
//        }
//    }
//    @Override
//    public void onConnectionFailed(ConnectionResult connectionResult) {
//
//    }
//
//    protected void startLocationUpdates() {
//        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
//    }
//
//    protected void stopLocationUpdates() {
//        LocationServices.FusedLocationApi.removeLocationUpdates(
//                mGoogleApiClient, this);
//    }
//
//
//    private boolean checkPlayServices() {
//        int resultCode = GooglePlayServicesUtil
//                .isGooglePlayServicesAvailable(this);
//        if (resultCode != ConnectionResult.SUCCESS) {
//            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
//                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
//                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
//            } else {
//                Toast.makeText(getApplicationContext(),
//                        "This device is not supported.", Toast.LENGTH_LONG)
//                        .show();
//                finish();
//            }
//            return false;
//        }
//        return true;
//    }
//
//    protected void createLocationRequest() {
//        mLocationRequest = new LocationRequest();
//        mLocationRequest.setInterval(UPDATE_INTERVAL);
//        mLocationRequest.setFastestInterval(FATEST_INTERVAL);
//        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
//        mLocationRequest.setSmallestDisplacement(DISPLACEMENT); // 10 meters
//    }
}
