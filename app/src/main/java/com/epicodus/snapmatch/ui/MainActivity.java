package com.epicodus.snapmatch.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.epicodus.snapmatch.Manifest;
import com.epicodus.snapmatch.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.UUID;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    public static final String TAG = MainActivity.class.getSimpleName();
    @Bind(R.id.uploadTextView) TextView mUploadTextView;
    @Bind(R.id.testImageView) ImageView mImageView;
    @Bind(R.id.indeterminateBar) ProgressBar mProgressBar;
    FirebaseStorage mStorage = FirebaseStorage.getInstance();
    Boolean permissionGranted = false;

    private static final int GET_FROM_GALLERY = 111;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        mUploadTextView.setOnClickListener(this);

        if (!permissionGranted) {
            checkFilePermissions();
            return;
        }
    }

        private void checkFilePermissions() {
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP){
            int permissionCheck = MainActivity.this.checkSelfPermission("Manifest.permission.READ_EXTERNAL_STORAGE");
            permissionCheck += MainActivity.this.checkSelfPermission("Manifest.permission.WRITE_EXTERNAL_STORAGE");
            if (permissionCheck != 0) {
                this.requestPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE,android.Manifest.permission.READ_EXTERNAL_STORAGE}, 1001); //Any number
            }
        }else{
            Log.d(TAG, "checkBTPermissions: No need to check permissions. SDK version < LOLLIPOP.");
        }
    }

    @Override
    public void onClick(View v) {

        if (v == mUploadTextView){
//            Intent intent = new Intent();
            //multiple images?
//            intent.setType("image/*");
//            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
//            intent.setAction(Intent.ACTION_GET_CONTENT);
//            startActivityForResult(Intent.createChooser(intent,"Select Picture"),1);

            Intent uploadPhotosIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI);
            uploadPhotosIntent.setType("image/*");
            if (uploadPhotosIntent.resolveActivity(this.getPackageManager()) != null) {
                startActivityForResult(uploadPhotosIntent, GET_FROM_GALLERY );
            }

        }

    }//onClick

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.v(TAG, "HELLLLO"+data);

            //Detects request codes
            if(requestCode==GET_FROM_GALLERY && resultCode == Activity.RESULT_OK) {
                Uri selectedImageUri = data.getData();
                Bitmap bitmap = null;
                try {
                    Bitmap imageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
                    mImageView.setImageBitmap(imageBitmap);
                    saveToFirebaseStorage(imageBitmap);

                } catch (FileNotFoundException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }


    public void saveToFirebaseStorage(Bitmap bitmap) {
        // Create sotrage reference
        String path = "currentUserGallery/" + UUID.randomUUID() + ".png";
        StorageReference galleryRef = mStorage.getReference(path);
        mProgressBar.setVisibility(View.VISIBLE);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] data = baos.toByteArray();

        Log.v(TAG,"PATH to photo : "+path);

        //uploadButton.setEnabled(false);
        //firebase thang ...hmmm
        UploadTask uploadTask = galleryRef.putBytes(data);
        uploadTask.addOnSuccessListener(MainActivity.this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                toastMessage("You are doing a great job, Keep it up, you are almost there");
                mProgressBar.setVisibility(View.GONE);
                //uploadButton.setEnabled(true);
                Uri url = taskSnapshot.getDownloadUrl();
                //downloadUrl.setText(url.toString());
                //downloadUrl.setVisibilty(View.VISIBLE);

            }
        });

    }


    /**
     * customizable toast
     * @param message
     */
    private void toastMessage(String message){
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
    }


  }





