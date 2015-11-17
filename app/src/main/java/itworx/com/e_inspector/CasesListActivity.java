package itworx.com.e_inspector;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class CasesListActivity extends Activity {

    ListView lv;
    private ProgressDialog pDialog;
    public static final int progress_bar_type = 0;
    List<Case> cases = null;
    RecyclerView rv;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        rv = (RecyclerView) findViewById(R.id.rv);
        rv.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        rv.setLayoutManager(llm);
//        getCasesAsyncTaskOffline();
//        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Intent intent = new Intent(CasesListActivity.this, CaseDetailsActivity.class);
//                Case selectedCase = (Case) parent.getItemAtPosition(position);
//                intent.putExtra("image-url", selectedCase.incidentImageURI);
//                intent.putExtra("lat", selectedCase.latitude);
//                intent.putExtra("lon", selectedCase.longitude);
//                intent.putExtra("desc", selectedCase.description);
//                intent.putExtra("audio", selectedCase.incidentAudioURI);
//                startActivity(intent);
//            }
//        });

    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case progress_bar_type:
                pDialog = new ProgressDialog(this);
                pDialog.setMessage("Loading Cases. Please wait...");
                pDialog.setIndeterminate(false);
                pDialog.setMax(100);
                pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                pDialog.setCancelable(true);
                pDialog.show();
                return pDialog;
            default:
                return null;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        getCasesAsyncTaskOffline();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_submit) {
            startActivity(new Intent(this, SubmitCaseActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }


    public void submitCase(View v) {
        if (cases != null)
            if (cases.size() == 10) {
                Toast.makeText(this, "Cant submit more than 10 Case per user in a trial version", Toast.LENGTH_LONG).show();
                return;
            }
        startActivity(new Intent(this, SubmitCaseActivity.class));
    }

    public void getCasesAsyncTask() {


        new AsyncTask<Void, Void, List<Case>>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                pDialog = new ProgressDialog(CasesListActivity.this);
                pDialog.setMessage("Please wait...");
                pDialog.setCancelable(true);
                pDialog.show();
            }

            @Override
            protected List<Case> doInBackground(Void... params) {
                String rowData = WebServiceHandler.getJSON(Constants.CASES_URI, 1500000, WebServiceHandler.GET, "");
                Gson gson = new Gson();
                Type listType = new TypeToken<List<Case>>() {
                }.getType();
                List<Case> cases = null;
                try {
                    cases = (List<Case>) gson.fromJson(rowData, listType);
                    Log.d("", "einspector:cases:" + cases.size());
                } catch (Exception ex) {
                    Log.d("", "einspector:" + ex.getMessage());
                }
                return cases;
            }


            @Override
            protected void onPostExecute(List<Case> lstCases) {
                if (pDialog.isShowing())
                    pDialog.dismiss();
                if (lstCases != null) {
                    ArrayList<Case> lst = new ArrayList<Case>(lstCases);
                    CaseAdapter caseAdapter = new CaseAdapter(CasesListActivity.this, R.layout.list_item, lst);
                    lv.setAdapter(caseAdapter);
                }
                // Dismiss the progress dialog
            }
        }.execute();
    }

    public void getCasesAsyncTaskOffline() {


        new AsyncTask<Void, Void, List<Case>>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                pDialog = new ProgressDialog(CasesListActivity.this);
                pDialog.setMessage("Please wait...");
                pDialog.setCancelable(true);
                pDialog.show();
            }

            @Override
            protected List<Case> doInBackground(Void... params) {

                try {
                    cases = new DatabaseManager(CasesListActivity.this).getAllCases();
                } catch (Exception ex) {
                    Log.d("", "einspector:" + ex.getMessage());
                }
                return cases;
            }


            @Override
            protected void onPostExecute(List<Case> lstCases) {
                if (pDialog.isShowing())
                    pDialog.dismiss();
                if (lstCases != null) {
                    ArrayList<Case> lst = new ArrayList<Case>(lstCases);
                    RVCaseAdapter caseAdapter = new RVCaseAdapter(CasesListActivity.this, lst);
                    rv.setAdapter(caseAdapter);
                }
                // Dismiss the progress dialog
            }
        }.execute();
    }

}
