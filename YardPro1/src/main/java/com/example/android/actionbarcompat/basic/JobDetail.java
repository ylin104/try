package com.example.android.actionbarcompat.basic;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;


public class JobDetail extends ActionBarActivity {

    Intent launcher;
    private boolean editMode;
    private EditText et_who;
    private EditText et_when;
    private EditText et_where;
    private EditText et_notes;
    private Bundle details;

    private PopupMenu popupBefore;
    private PopupMenu popupAfter;
    static final int MENU_CAMERA = Menu.FIRST;
    static final int MENU_GALLERY = Menu.FIRST + 1;

    static final int SELECT_CONTACT = 11;
    static final int SELECT_BEFORE = 12;
    static final int CAPTURE_BEFORE = 13;
    static final int SELECT_AFTER = 14;
    static final int CAPTURE_AFTER = 15;

    Uri beforeURI;
    Uri afterURI;

    ImageView beforePic;
    ImageView afterPic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_detail);

        et_who = (EditText) findViewById(R.id.who_text);
        et_when = (EditText) findViewById(R.id.when_text);
        et_where = (EditText) findViewById(R.id.where_text);
        et_notes = (EditText) findViewById(R.id.notes_text);

        launcher = getIntent();
        editMode = launcher.getBooleanExtra("jobEDIT", false);

        if (editMode) {
            // get job data to edit from the launcher
            et_who.setText(launcher.getStringExtra("jobWHO"));
            et_when.setText(launcher.getStringExtra("jobWHEN"));
            et_where.setText(launcher.getStringExtra("jobWHERE"));
            et_notes.setText(launcher.getStringExtra("jobNOTES"));
        }

        ImageButton nextBtn = (ImageButton) findViewById(R.id.btn_next);
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intentA = new Intent(JobDetail.this, JobServices.class);
                intentA.putExtra("jobID", launcher.getLongExtra("jobID", 0));   // added
                intentA.putExtra("jobEDIT", editMode);                          // added
                intentA.putExtra("jobWHO", et_who.getText().toString());
                intentA.putExtra("jobWHEN", et_when.getText().toString());
                intentA.putExtra("jobWHERE", et_where.getText().toString());
                intentA.putExtra("jobNOTES", et_notes.getText().toString());
                int mode = editMode ? Jobs.EDIT_JOB : Jobs.NEW_JOB;
                startActivityForResult(intentA, mode);
            }
        });

        findViewById(R.id.btn_contact).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent pickContactIntent = new Intent(Intent.ACTION_PICK, Uri.parse("content://contacts"));
                pickContactIntent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE); // Show user only contacts w/ phone numbers
                startActivityForResult(pickContactIntent, SELECT_CONTACT);
            }
        });

        popupBefore = new PopupMenu(this, findViewById(R.id.btn_before));
        popupBefore.getMenu().add(Menu.NONE, MENU_CAMERA, Menu.NONE, "Take a Picture");
        popupBefore.getMenu().add(Menu.NONE, MENU_GALLERY, Menu.NONE, "Choose From Gallery");

        beforePic = (ImageView) findViewById(R.id.img_before);
        popupBefore.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case MENU_CAMERA:
                        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                            startActivityForResult(takePictureIntent, CAPTURE_BEFORE);
                        }
                        break;
                    case MENU_GALLERY:
                        startActivityForResult(new Intent(Intent.ACTION_PICK,
                                android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI), SELECT_BEFORE);
                        break;
                }
                return false;
            }
        });
        findViewById(R.id.btn_before).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupBefore.show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Jobs.EDIT_JOB && resultCode == RESULT_OK) {
                 setResult(RESULT_OK);
                 finish();
        }
        else if (requestCode == Jobs.NEW_JOB && resultCode == RESULT_OK) {
            setResult(RESULT_OK);
            finish();
        }
        else if (requestCode == SELECT_BEFORE) {
            if (resultCode == Activity.RESULT_OK) {
                beforeURI = data.getData();
                beforePic.setImageURI(beforeURI);
            }
        }
        else if (requestCode == CAPTURE_BEFORE) {
            if (resultCode == Activity.RESULT_OK) {
                Bundle extras = data.getExtras();
                Bitmap imageBitmap = (Bitmap) extras.get("data");
                beforePic.setImageBitmap(imageBitmap);
                beforeURI = data.getData();
            }
        }
        else if (requestCode == SELECT_CONTACT) {
            if (resultCode == Activity.RESULT_OK) {
                Uri contactUri = data.getData();
                setRecipient(contactUri);
            }

        }

    }

    private void setRecipient(Uri contactUri) {

        // grab number and name
        String[] projection = {ContactsContract.CommonDataKinds.Phone.NUMBER, ContactsContract.Contacts.DISPLAY_NAME};

        // Perform the query on the contact to get the NUMBER column
        // We don't need a selection or sort order (there's only one result for the given URI)
        // CAUTION: The query() method should be called from a separate thread to avoid blocking
        // your app's UI thread. (For simplicity of the sample, this code doesn't do that.)
        // Consider using CursorLoader to perform the query.
        Cursor cursor = getContentResolver()
                .query(contactUri, projection, null, null, null);
        cursor.moveToFirst();

        String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
        et_who.setText(name);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_job_detail, menu);
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
