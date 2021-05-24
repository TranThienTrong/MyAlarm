package com.patecan.myalarm.View;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;

import com.patecan.myalarm.Data.AlarmDatabaseHandler;
import com.patecan.myalarm.Model.Alarm;
import com.patecan.myalarm.R;
import com.patecan.myalarm.RecyclerViewAdapter;

import java.util.List;

/**
 * @author Tran Thien Trong - FX02425
 * @version 1.0
 * @since 2020-11-2
 */

public class MainActivity extends AppCompatActivity {

    private AlarmDatabaseHandler mDatabase;
    private RecyclerViewAdapter mRecyclerViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDatabase = new AlarmDatabaseHandler(this);

        List<Alarm> alarmArrayList = mDatabase.getAllAlarm(); /* Get All Alarm In Database Into List */

        /* Create RecyclerView */
        RecyclerView recyclerView = findViewById(R.id.main_recyclerView);
        mRecyclerViewAdapter = new RecyclerViewAdapter(this, alarmArrayList);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(mRecyclerViewAdapter);
    }

    /* Refresh The Recycler View When OnResume */
    @Override
    protected void onResume() {
        super.onResume();
        mRecyclerViewAdapter.notifyDataSetChanged();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_alarm, menu);
        return true;
    }

    /*
    * When User Click To Add Button On Menu, Start The Alarm Activity
    * */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_alarm_itemAdd:
                startActivity(new Intent(MainActivity.this, AlarmActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

    /* Close Database When Activity Is Destroyed */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDatabase.close();
    }
}
