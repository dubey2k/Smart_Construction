package com.example.smartconstruction;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.smartconstruction.nav_Fragments.MainScreen_fragment;
import com.example.smartconstruction.nav_Fragments.Nav_website;
import com.example.smartconstruction.nav_Fragments.User_Profile;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawerLayout;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private CollectionReference cr = FirebaseFirestore.getInstance().collection("Users");

    CircleImageView nav_profilePic;
    TextView nav_userName;
    User user;

    public String UserName;
    public String userEmail;
    public String profilePicURL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        NavigationView navigationView = findViewById(R.id.nav_view);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        navigationView.setNavigationItemSelectedListener(this);

        View headerView = navigationView.getHeaderView(0);
        nav_profilePic = headerView.findViewById(R.id.nav_profilePic);
        nav_profilePic.setImageResource(R.drawable.default_user_image);
        nav_userName = headerView.findViewById(R.id.nav_UserName);
        nav_profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (drawerLayout.isDrawerOpen(GravityCompat.START))
                    drawerLayout.closeDrawer(GravityCompat.START);
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new User_Profile())
                        .commit();
            }
        });

        UserName = mAuth.getCurrentUser().getDisplayName();
        userEmail = mAuth.getCurrentUser().getEmail() + "";

        nav_userName.setText(UserName);

        cr.document(userEmail).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot != null) {
                    user = documentSnapshot.toObject(User.class);
                    profilePicURL = user.getProfilePicture()+"";
                    if (!profilePicURL.equals("")) {
                        ImageLoadTask imageLoadTask = new ImageLoadTask(profilePicURL, nav_profilePic);
                        imageLoadTask.execute();
                    }
                }

            }
        });


        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new MainScreen_fragment())
                .commit();


        drawerLayout = findViewById(R.id.MainDrawer_layout);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar
                , R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater i = getMenuInflater();
        i.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.logOut:
                mAuth.getInstance().signOut();
                startActivity(new Intent(MainActivity.this, Login_Activity.class));
                finish();
                break;
        }

        return true;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.termsAndCondition:
                if (drawerLayout.isDrawerOpen(GravityCompat.START))
                    drawerLayout.closeDrawer(GravityCompat.START);
                AlertDialog.Builder alt = new AlertDialog.Builder(MainActivity.this);
                LayoutInflater inflater = LayoutInflater.from(MainActivity.this);
                View view = inflater.inflate(R.layout.terms_and_condition, null);
                alt.setView(view);
                TextView title = view.findViewById(R.id.titleTermsAndConditions);
                TextView description = view.findViewById(R.id.descriptionTermsAndConditions);
                if (user.getMemberType().equals("Merger")) {
                    title.setText("Terms And Conditions\nfor MERGERS");
                    description.setText(R.string.terms_and_conditions_merger);
                } else {
                    title.setText("Terms And Conditions\nfor CUSTOMERS");
                    description.setText(R.string.terms_and_conditions_customer);
                }
                final AlertDialog alertDialog = alt.create();
                alertDialog.show();
                break;
            case R.id.website_menu:
                if (drawerLayout.isDrawerOpen(GravityCompat.START))
                    drawerLayout.closeDrawer(GravityCompat.START);
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new Nav_website())
                        .commit();
                break;
            case R.id.email_menu:
                Intent intent = new Intent(android.content.Intent.ACTION_SEND);
                intent.setType("text/plain");
                final PackageManager pm = getPackageManager();
                final List<ResolveInfo> matches = pm.queryIntentActivities(intent, 0);
                ResolveInfo best = null;
                for (final ResolveInfo info : matches)
                    if (info.activityInfo.packageName.endsWith(".gm") || info.activityInfo.name.toLowerCase().contains("gmail"))
                        best = info;
                Toast.makeText(getApplicationContext(), best + "", Toast.LENGTH_SHORT).show();
                if (best != null) {
                    intent.setClassName(best.activityInfo.packageName, best.activityInfo.name)
                            .putExtra(Intent.EXTRA_EMAIL, new String[]{"test@gmail.com"});
                }
                startActivity(intent);
                break;
            case R.id.Home_menu:
                if (drawerLayout.isDrawerOpen(GravityCompat.START))
                    drawerLayout.closeDrawer(GravityCompat.START);
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new MainScreen_fragment())
                        .commit();
                break;

        }
        return true;
    }


    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START))
            drawerLayout.closeDrawer(GravityCompat.START);
        else
            super.onBackPressed();
    }

//    ------------------------------------------------------------------
//    for retriving profile image in navigation bar...

    public class ImageLoadTask extends AsyncTask<Void, Void, Bitmap> {

        private String url;
        private ImageView imageView;

        public ImageLoadTask(String url, ImageView imageView) {
            this.url = url;
            this.imageView = imageView;
        }

        @Override
        protected Bitmap doInBackground(Void... params) {
            try {
                URL urlConnection = new URL(url);
                HttpURLConnection connection = (HttpURLConnection) urlConnection
                        .openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                Bitmap myBitmap = BitmapFactory.decodeStream(input);
                return myBitmap;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            super.onPostExecute(result);
            imageView.setImageBitmap(result);
        }

    }
}
