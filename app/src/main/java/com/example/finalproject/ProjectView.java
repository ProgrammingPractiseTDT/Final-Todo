package com.example.finalproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ProjectView extends AppCompatActivity {
    AddTaskDialog atd;
    RecyclerView rv;
    TaskAdapter taskAdapter;
    ArrayList<Task> taskList;
    private FirebaseUser user;
    String ProjectKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_view);
        String ProjectTitle;
        ProjectTitle =getIntent().getExtras().getString("Project title");
        user = FirebaseAuth.getInstance().getCurrentUser();

        ProjectKey = getIntent().getExtras().getString("Project key");
        TextView tv = findViewById(R.id.txt_projectName);
        tv.setText(ProjectTitle);

        //firebase user variable

        //recycler view init
        rv = findViewById(R.id.task_recycler_view);
        taskList = new ArrayList<Task>();
        fetchTaskFromProject(ProjectKey);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(rv.getContext(), DividerItemDecoration.VERTICAL);
        rv.addItemDecoration(dividerItemDecoration);
        taskAdapter = new TaskAdapter(this,taskList);
        rv.setAdapter(taskAdapter);
        atd = new AddTaskDialog(ProjectView.this, ProjectKey);
    }

    public void addTask(View view) {
        //execute when click add task button
        atd.show();
    }

    public void fetchTaskFromProject(String ProjectKey){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("User").child(user.getUid()).child("projects").child(ProjectKey).child("tasks");
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                taskList.clear();
                for (DataSnapshot dsp : dataSnapshot.getChildren()) {
                    Task task = dsp.getValue(Task.class);
                    taskList.add(task); //add result into array list
                    taskAdapter.notifyDataSetChanged();
                }
                // ..
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w("FetchError", "loadPost:onCancelled", databaseError.toException());
            }
        };
        reference.addValueEventListener(postListener);
    }
}