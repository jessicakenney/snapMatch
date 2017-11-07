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
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.epicodus.snapmatch.Manifest;
import com.epicodus.snapmatch.R;
import com.google.firebase.storage.FirebaseStorage;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.UUID;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    public static final String TAG = MainActivity.class.getSimpleName();
    @Bind(R.id.uploadTextView) TextView mUploadTextView;
    @Bind(R.id.imageView) TextView mImageView;
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
            Intent uploadPhotosIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI);

            if (uploadPhotosIntent.resolveActivity(this.getPackageManager()) != null) {
                startActivityForResult(uploadPhotosIntent, GET_FROM_GALLERY );
            }

        }

    }//onClick

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


            //Detects request codes
            if(requestCode==GET_FROM_GALLERY && resultCode == Activity.RESULT_OK) {
                Uri selectedImage = data.getData();
                Bitmap bitmap = null;
                try {
                    Bitmap imageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                    mImageView.setImageBitmap(imageBitmap);
                    encodeBitmapAndSaveToFirebase(imageBitmap);
                    //something more here
//                    byte[] data = baos.toByteArray();
//
//                    String path = "gameX/" + UUID.randomUUID() + ".png";
//                    StorageReference gameXRef = mStorage.getReference(path);
//
//                    progressBar.setVisibility(View.VISIBLE);
//                    //uploadButton.setEnabled(false);
//
//                    UploadTask uploadTask = gameRef.putBytes(data);
//                    uploadTask.addOnSuccessListener(MainActivity.this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                        @Override
//                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                            progressBar.setVisibility(View.GONE);
//                            //uploadButton.setEnabled(true);
//
//                            Uri url = taskSnapshot.getDownloadUrl();
//                            downloadUrl.setText(url.toString());
//                            downloadUrl.setVisibilty(View.VISIBLE);
//
//                        }
//                    });

                } catch (FileNotFoundException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }

    public void encodeBitmapAndSaveToFirebase(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        //String imageEncoded = Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);
        String path = "gameX/" + UUID.randomUUID() + ".png";
        Log.v(TAG,"PATH to photo : "+path);

//        DatabaseReference ref = FirebaseDatabase.getInstance()
//                .getReference(Constants.FIREBASE_CHILD_RESTAURANTS)
//                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
//                .child(mRestaurant.getPushId())
//                .child("imageUrl");
//        ref.setValue(imageEncoded);

    }


    /**
     * customizable toast
     * @param message
     */
    private void toastMessage(String message){
        //usage: toastMessage("Upload Failed");
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
    }


  }





