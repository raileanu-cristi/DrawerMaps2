package com.raileanu.drawermaps2;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class LanguageSelectActivity extends AppCompatActivity
{

    private String TAG = MainActivity.class.getSimpleName();

    private ProgressDialog pDialog;
    private ListView lv;

    // URL to get contacts JSON
    private static String url = "http://www.jrsdr.robertoderesu.com/api/languages";

    ArrayList<HashMap<String, String>> languageList;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language_select);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        languageList = new ArrayList<>();
        lv = (ListView) findViewById(R.id.languageList);
        new GetLanguages().execute();
    }


    private class GetLanguages extends AsyncTask<Void, Void, Void>
    {

        @Override
        protected void onPreExecute()
        {

            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(LanguageSelectActivity.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();

        }


        @Override
        protected Void doInBackground(Void... arg0)
        {

            HttpHandler sh = new HttpHandler();

            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(url);

            Log.e(TAG, "Response from url: " + jsonStr);

            if (jsonStr != null)
            {
                try
                {
                    JSONObject jsonObj = new JSONObject(jsonStr);

                    // Getting JSON Array node
                    JSONArray contacts = jsonObj.getJSONArray("languages");

                    // looping through All Contacts
                    for (int i = 0; i < contacts.length(); i++)
                    {
                        JSONObject c = contacts.getJSONObject(i);

                        String id = c.getString("id");
                        String key = c.getString("key");
                        String name = c.getString("name");


                        // tmp hash map for single contact
                        HashMap<String, String> language = new HashMap<>();

                        // adding each child node to HashMap key => value
                        language.put("id", id);
                        language.put("keyName", key + " (" + name + ")");

                        // adding contact to contact list
                        languageList.add(language);
                    }
                }
                catch (final JSONException e)
                {
                    Log.e(TAG, "Json parsing error: " + e.getMessage());
                    runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {

                            Toast.makeText(getApplicationContext(),
                                    "Json parsing error: " + e.getMessage(),
                                    Toast.LENGTH_LONG)
                                    .show();
                        }
                    });

                }
            } else
            {
                Log.e(TAG, "Couldn't get json from server.");
                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {

                        Toast.makeText(getApplicationContext(),
                                "Couldn't get json from server. Check LogCat for possible errors!",
                                Toast.LENGTH_LONG)
                                .show();
                    }
                });

            }

            return null;
        }


        @Override
        protected void onPostExecute(Void result)
        {

            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (pDialog.isShowing())
                pDialog.dismiss();
            /**
             * Updating parsed JSON data into ListView
             * */
            ListAdapter adapter = new SimpleAdapter(
                    LanguageSelectActivity.this, languageList,
                    R.layout.language_item, new String[]{"id", "keyName"}, new int[]{R.id.id,
                    R.id.keyName});

            lv.setAdapter(adapter);
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {

        int id = item.getItemId();
        if (id == android.R.id.home)
        {
            startActivity(new Intent(this, MainActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
