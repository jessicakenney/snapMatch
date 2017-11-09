package com.epicodus.snapmatch.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.epicodus.snapmatch.R;
import com.epicodus.snapmatch.models.NavBarItem;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.UUID;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {
    public static final String TAG = MainActivity.class.getSimpleName();
    @Bind(R.id.uploadTextView) TextView mUploadTextView;
    @Bind(R.id.testImageView) ImageView mImageView;
    //@Bind(R.id.indeterminateBar) ProgressBar mProgressBar;
    @Bind(R.id.nav_view) NavigationView mNavigationView;
    @Bind(R.id.toolbar) Toolbar mToolbar;
    @Bind(R.id.drawer_layout) DrawerLayout mDrawer;
    @Bind(R.id.placeHolderText) TextView mPlaceHolderText;

    FirebaseStorage mStorage = FirebaseStorage.getInstance();
    Boolean permissionGranted = false;

    private static final int GET_FROM_GALLERY = 111;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        inflateViews();

        if (!permissionGranted) {
            checkFilePermissions();
            return;
        }

        // Test Upload Intent
        mUploadTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent uploadPhotosIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                uploadPhotosIntent.setType("image/*");
                if (uploadPhotosIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(uploadPhotosIntent, GET_FROM_GALLERY );
                }
            }
        });

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                //display welcome message
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    //getSupportActionBar().setTitle("Welcome, " + user.getDisplayName() + "!");
                } else {

                }
            }
        };

    }

    private void inflateViews(){
       //initialize views
        ButterKnife.bind(this);
        setSupportActionBar(mToolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawer.addDrawerListener(toggle);
        toggle.syncState();

        setNavBarButtons();

    }

    private void setNavBarButtons(){
       for (NavBarItem item : NavBarItem.values() ){
           TextView itemView = (TextView) findViewById(item.getItemId());
           itemView.setOnClickListener(this);
       }
    }
    @Override
    public void onStart() {
        super.onStart();
        if (mAuth != null) {
            mAuth.addAuthStateListener(mAuthListener);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
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
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();


//        if (id == R.id.nav_camera) {
//            // Handle the camera action
//        } else if (id == R.id.nav_gallery) {
//
//        } else if (id == R.id.nav_slideshow) {
//
//        } else if (id == R.id.nav_manage) {
//
//        } else if (id == R.id.nav_share) {
//
//        } else if (id == R.id.nav_send) {
//
//        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
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

    private void logout() {
        FirebaseAuth.getInstance().signOut();
        Log.v(TAG, "SIGNING OUT");
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
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
        //mProgressBar.setVisibility(View.GONE);

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
                //mProgressBar.setVisibility(View.GONE);
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


    @Override
    public void onClick(View v) {
        switch (NavBarItem.fromViewId(v.getId())){
            case MY_GAMES:
                Log.v(TAG, "MYGAMES ");
                mDrawer.closeDrawer(GravityCompat.START);
                displayGameFragment();
                break;

            case CREATE_GAME:
                Log.v(TAG, "CREATEGAME ");
                mDrawer.closeDrawer(GravityCompat.START);
                break;

            case LOGOUT:
                Log.v(TAG, "LOGOUT ");
                logout();
                mDrawer.closeDrawer(GravityCompat.START);
                break;

            case GALLERY:
                Log.v(TAG, "GALLERY ");
                mDrawer.closeDrawer(GravityCompat.START);
                break;
        }
    }

    private void displayGameFragment(){
        setTitle("GAME");
        setFragment(GameBoardFragment.newInstance(), "GameBoardFragment");
        mPlaceHolderText.setVisibility(View.INVISIBLE);
    }
    private void setFragment (Fragment fragment, String fragmentName){
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_layout, fragment, fragmentName);
    };
}





