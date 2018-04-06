package com.example.android.actionbarcompat.basic;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;


public class Jobs extends ActionBarActivity implements ListView.OnItemClickListener  {

    public static final int EDIT_JOB = 0;
    public static final int NEW_JOB = 1;

    public static final int MENU_ITEM_EDITVIEW = Menu.FIRST;
    public static final int MENU_ITEM_DELETE = Menu.FIRST + 1;

    private String[] navItems;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;

    private ListView jobsList;
    protected static ArrayList<JobItem> jobItems;
    protected static JobItemAdapter aa;

    private Context context;
    protected static JobDBadapter dbAdapt; // made static to access in JobServices
    private static Cursor curse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jobs);
        context = getApplicationContext();

        // set-up database
        dbAdapt = new JobDBadapter(this);
        dbAdapt.open();

        jobsList = (ListView) findViewById(R.id.joblist);
        // create ArrayList of courses from database
        jobItems = new ArrayList<JobItem>();
        // make array adapter to bind arraylist to listview with new custom item layout
        aa = new JobItemAdapter(this, R.layout.job_item_layout, jobItems);
        jobsList.setAdapter(aa);

        registerForContextMenu(jobsList);

        updateArray();

        navItems = getResources().getStringArray(R.array.nav_pane_array);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        // Set the adapter for the list view
        mDrawerList.setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, navItems));
        // Set the list's click listener
        mDrawerList.setOnItemClickListener(this);
    }

    /**
     * Update the job listing in the view
     */
    public void updateArray() {
        curse = dbAdapt.getAllItems();
        jobItems.clear();
        if (curse.moveToFirst())
            do {
                JobItem result = new JobItem(curse.getString(1), curse.getString(2),
                        curse.getString(3), curse.getString(4),
                        curse.getFloat(5), curse.getShort(6));
                jobItems.add(0, result);  // puts in reverse order
            } while (curse.moveToNext());

        aa.notifyDataSetChanged();
    }

    @Override
    public void onItemClick(AdapterView parent, View view, int position, long id) {
        selectItem(position);
    }

    /** Launch other activities depending on item selected */
    private void selectItem(int position) {
        switch (position) {
            case 0: // jobs list, redundant
                break;
            case 1: // new job
                launchNewJob();
                break;
            case 2: // services (doesn't exist yet)
                break;
            case 3: // settings (doesn't exist yet)
                break;
        }

        mDrawerLayout.closeDrawer(GravityCompat.START);
    }

    private void launchNewJob() {
        Intent intentA = new Intent(Jobs.this, JobDetail.class);
        intentA.putExtra("jobEDIT", false);
        startActivityForResult(intentA, NEW_JOB);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_jobs, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.menu_new) {
            launchNewJob();
            return true;
        }
        //noinspection SimplifiableIfStatement
        if (id == R.id.menu_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == Jobs.EDIT_JOB) {
            updateArray();
        }
        else if (resultCode == RESULT_OK && requestCode == Jobs.NEW_JOB) {
            updateArray();
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        // create menu in code instead of in xml file (xml approach preferred)
        menu.setHeaderTitle("Select Course Item");

        // Add menu items
        menu.add(0, MENU_ITEM_EDITVIEW, 0, R.string.menu_editview);
        menu.add(0, MENU_ITEM_DELETE, 0, R.string.menu_delete);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        super.onContextItemSelected(item);

        AdapterView.AdapterContextMenuInfo menuInfo;
        menuInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int index = menuInfo.position; // position in array adapter
        // index in array adapter is reverse order of database row
        curse = dbAdapt.getAllItems();
        // Toast.makeText(context, "dbase count is " +
        // cCursor.getCount(),Toast.LENGTH_SHORT).show();
        curse.move(curse.getCount() - index);
        long itemID = curse.getLong(0);

        switch (item.getItemId()) {
            case MENU_ITEM_EDITVIEW: {
                JobItem theCourse = new JobItem(curse.getString(1), curse.getString(2),
                        curse.getString(3), curse.getString(4),
                        curse.getFloat(5), curse.getShort(6));
                Toast.makeText(context, "edit request: " + theCourse,
                        Toast.LENGTH_SHORT).show();
                Intent intentE = new Intent(Jobs.this, JobDetail.class);
                intentE.putExtra("jobEDIT", true);
                intentE.putExtra("jobID", itemID);
                intentE.putExtra("jobWHERE", curse.getString(1));
                intentE.putExtra("jobWHEN", curse.getString(2));
                intentE.putExtra("jobWHO", curse.getString(3));
                intentE.putExtra("jobNOTES", curse.getString(4));
                intentE.putExtra("jobCOST", curse.getFloat(5));
                startActivityForResult(intentE, EDIT_JOB);
                return false;
            }
            case MENU_ITEM_DELETE: {
                if (dbAdapt.removeItem(itemID)) {
                    updateArray();
                    Toast.makeText(context, "job " + index + " deleted",
                            Toast.LENGTH_SHORT).show();
                    return true;
                } else
                    return false;
            }
        }
        return false;
    }


}