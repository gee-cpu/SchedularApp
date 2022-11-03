package com.toprate.ocharo.activities;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.toprate.ocharo.model.Note;
import com.toprate.ocharo.R;
import com.toprate.ocharo.utils.DbHelper;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

public class NoteInfoActivity extends AppCompatActivity {

    private DbHelper db;
    private Note note;
    private TextView text;
    FloatingActionButton fab;
    String docFilePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_info);
        fab = this.findViewById(R.id.fab);
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        setupIntent();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ActivityCompat.requestPermissions((Activity) NoteInfoActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    1);        }
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("application/pdf");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Pdf"), 1);
            }
        });
        text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(docFilePath!=null)
                {
                    File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() +"/"+ text.getText().toString());
                    Intent target = new Intent(Intent.ACTION_VIEW);
                    target.setDataAndType(Uri.fromFile(file),"application/pdf");
                    target.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

                    Intent intent = Intent.createChooser(target, "Open File");
                    try {
                        startActivity(intent);
                    } catch (ActivityNotFoundException e) {
                        // Instruct the user to install a PDF reader here, or something
                    }
                }
 //                File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() +"/"+ text.getText().toString());
//                Uri uri;
//                if (Build.VERSION.SDK_INT < 24) {
//                    uri = Uri.fromFile(file);
//                } else {
//                    uri = Uri.parse(file.getPath()); // My work-around for new SDKs, doesn't work in Android 10.
//                }
//                Intent viewFile = new Intent(Intent.ACTION_VIEW);
//                viewFile.setDataAndType(uri, "application/pdf");
//                startActivity(viewFile);
            }
        });
    }




    @Override
    protected void onActivityResult(int req, int result, Intent data)
    {
        // TODO Auto-generated method stub
        super.onActivityResult(req, result, data);
        if (result == RESULT_OK)
        {
            Uri fileuri = data.getData();
            docFilePath = getFileNameByUri(NoteInfoActivity.this, fileuri);
            String filename=docFilePath.substring(docFilePath.lastIndexOf("/")+1);
            text.setText(filename);

        }
    }

// get file path

    private String getFileNameByUri(Context context, Uri uri)
    {
        String filepath = "";//default fileName
        //Uri filePathUri = uri;
        File file;
        if (uri.getScheme().toString().compareTo("content") == 0)
        {
            Cursor cursor = context.getContentResolver().query(uri, new String[] { android.provider.MediaStore.Images.ImageColumns.DATA, MediaStore.Images.Media.ORIENTATION }, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

            cursor.moveToFirst();

            String mImagePath = cursor.getString(column_index);
            cursor.close();
            filepath = mImagePath;

        }
        else
        if (uri.getScheme().compareTo("file") == 0)
        {
            try
            {
                file = new File(new URI(uri.toString()));
                if (file.exists())
                    filepath = file.getAbsolutePath();

            }
            catch (URISyntaxException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        else
        {
            filepath = uri.getPath();
        }
        return filepath;
    }

    private void setupIntent() {
        db = new DbHelper(NoteInfoActivity.this);
        note = (Note) getIntent().getSerializableExtra(NotesActivity.KEY_NOTE);
        text = findViewById(R.id.edittextNote);
        if(note.getText() != null) {
            String filename=note.getText().substring(note.getText().lastIndexOf("/")+1);
            text.setText(filename);
        }

    }

    @Override
    public void onBackPressed() {
        note.setText(docFilePath);
        db.updateNote(note);
        Toast.makeText(NoteInfoActivity.this, getResources().getString(R.string.saved), Toast.LENGTH_SHORT).show();
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                note.setText(docFilePath);
                db.updateNote(note);
                Toast.makeText(NoteInfoActivity.this, getResources().getString(R.string.saved), Toast.LENGTH_SHORT).show();
                super.onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
        }
    public void openPdf(Context context, String path){
        File file = new File(path);
        if (file.exists()) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.fromFile(file), "application/pdf");
            PackageManager pm = context.getPackageManager();
            Intent sendIntent = new Intent(Intent.ACTION_VIEW);
            sendIntent.setType("application/pdf");
            Intent openInChooser = Intent.createChooser(intent, "Choose");
            List<ResolveInfo> resInfo = pm.queryIntentActivities(sendIntent, 0);
            if (resInfo.size() > 0) {
                try {
                    context.startActivity(openInChooser);
                } catch (Throwable throwable) {
                    Toast.makeText(context, "PDF apps are not installed", Toast.LENGTH_SHORT).show();
                    // PDF apps are not installed
                }
            } else {
                Toast.makeText(context, "PDF apps are not installed", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
