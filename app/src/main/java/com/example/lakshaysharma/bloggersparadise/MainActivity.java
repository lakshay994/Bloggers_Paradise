package com.example.lakshaysharma.bloggersparadise;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity {

    private static DatabaseReference mDatabase;
    private static RecyclerView recyclerView;
    private static FirebaseAuth mAuth;
    private static FirebaseAuth.AuthStateListener mAuthStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // initialize the firebase and recyclerView objects
        recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        mDatabase = FirebaseDatabase.getInstance().getReference().child("BloggersParadise");
        mAuth = FirebaseAuth.getInstance();
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                Intent loginIntent = new Intent(MainActivity.this, RegisterActivity.class);
                loginIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(loginIntent);
            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();

        //fire up the authentication on start
        mAuth.addAuthStateListener(mAuthStateListener);

        // set up the firebase recycler adapter to inflate the layout with cards

        FirebaseRecyclerOptions<BlogParadise> options = new FirebaseRecyclerOptions.Builder<BlogParadise>
                ().setQuery(mDatabase, BlogParadise.class).build();
        FirebaseRecyclerAdapter recyclerAdapter = new FirebaseRecyclerAdapter<BlogParadise, BlogViewHolder>(options) {
            @NonNull
            @Override
            public BlogViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return null;
            }

            @Override
            protected void onBindViewHolder( BlogViewHolder holder, int position, BlogParadise model) {
                final String post_key = getRef(position).getKey().toString();

                // bind the data
                holder.setTitle(model.getTitle());
                holder.setDesc(model.getDescription());
                holder.setImageURL(getApplicationContext(), model.getImageURL());
                holder.setUsername(model.getUsername());

                // set listener on the card
                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent singlePostIntent = new Intent(MainActivity.this, SinglePostActivity.class);
                        singlePostIntent.putExtra("PostID", post_key);
                        startActivity(singlePostIntent);
                    }
                });
            }
        };

        recyclerView.setAdapter(recyclerAdapter);
    }

    //class to bind data together to populate the cards in the layout
    public class BlogViewHolder extends RecyclerView.ViewHolder{

        View mView;

        public BlogViewHolder(View itemView){
            super(itemView);
            mView = itemView;
        }

        public void setTitle(String title){
            TextView post_title = mView.findViewById(R.id.post_title_txtview);
            post_title.setText(title);
        }

        public void setDesc(String desc){
            TextView post_desc = mView.findViewById(R.id.post_desc_txtview);
            post_desc.setText(desc);
        }

        public void setImageURL(Context context, String ImageURL){
            ImageView post_image = mView.findViewById(R.id.post_image);
            Picasso.get().load(ImageURL).into(post_image);
        }

        public void setUsername(String username){
            TextView post_user = mView.findViewById(R.id.post_user);
            post_user.setText(username);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
        else if (id == R.id.action_add){
            startActivity(new Intent(MainActivity.this, PostActivity.class));
        }
        else if (id == R.id.logout){
            mAuth.signOut();
            Intent logOutIntent = new Intent(MainActivity.this, RegisterActivity.class);
            logOutIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(logOutIntent);
        }

        return super.onOptionsItemSelected(item);
    }
}
