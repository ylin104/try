package com.example.android.actionbarcompat.basic;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;


public class JobServices extends ActionBarActivity {

    private Context context;
    private Intent launcher;
    private Spinner spinServices;
    private TextView textPrice;
    private boolean changed;
    private boolean editMode;

    private String[] serviceNames = {"Select Service", "Mow", "Trim", "Hedges", "Mulch"};  // description,cost pairs
    private float[] servicePrices = {0f, 40f, 15f, 25f, 60f};

    private String servText;
    private float cost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_services);
        context = getApplicationContext();

        textPrice = (TextView) findViewById(R.id.total_text);
        spinServices = (Spinner) findViewById(R.id.spin_serve);
        ArrayAdapter<String> sadapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, serviceNames);
        sadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinServices.setAdapter(sadapter);
        spinServices.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                cost = servicePrices[position];
                textPrice.setText("$" + cost);
            }

            public void onNothingSelected(AdapterView<?> parent) {
                // Another interface callback
            }
        }) ;


        ImageButton saveBtn = (ImageButton) findViewById(R.id.btn_save);
        saveBtn.setOnClickListener(saveListener);

        ImageButton emailBtn = (ImageButton) findViewById(R.id.btn_email);
        emailBtn.setOnClickListener(emailListener);


    }

    private View.OnClickListener saveListener = new View.OnClickListener() {
        public void onClick(View v) {
            changed = true;

            launcher = getIntent();
            editMode = launcher.getBooleanExtra("jobEDIT", false);

            servText = (String) spinServices.getSelectedItem();

            if (!editMode) {   // new course being added
                JobItem newJob = new JobItem(launcher.getStringExtra("jobWHERE"),
                        launcher.getStringExtra("jobWHEN"),
                        launcher.getStringExtra("jobWHO"),
                        launcher.getStringExtra("jobNOTES"),
                        cost, (short) 0);
                Jobs.dbAdapt.insertItem(newJob);
                setResult(RESULT_OK);
                finish();
            }
            else { // editing existing job!!
                long currID = launcher.getLongExtra("jobID", 0);
                Jobs.dbAdapt.updateField(currID, 1, launcher.getStringExtra("jobWHERE"));
                Jobs.dbAdapt.updateField(currID, 2, launcher.getStringExtra("jobWHEN"));
                Jobs.dbAdapt.updateField(currID, 3, launcher.getStringExtra("jobWHO"));
                Jobs.dbAdapt.updateField(currID, 4, launcher.getStringExtra("jobNOTES"));
                Jobs.dbAdapt.updateCost(currID, cost);
                Jobs.dbAdapt.updatePaid(currID, (short) 0);
                setResult(RESULT_OK);
                finish();
            }
        }
    };

    private View.OnClickListener emailListener = new View.OnClickListener() {
        public void onClick(View v) {
           // what to do?

        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_job_services, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
