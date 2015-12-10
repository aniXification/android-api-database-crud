package com.sample.api_database_crud.activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.sample.api_database_crud.R;
import com.sample.api_database_crud.data.DataManager;
import com.sample.api_database_crud.model.User;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    private AlertDialog mAddDialog;
    private EditText mAddName;
    private EditText mAddEmail;
    private EditText mAddAddress;

    private ListView mList;
    private SwipeRefreshLayout mRefresh;
    private DataManager mData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initDatasource();
        setupViews();
        createAddDialog();

        mData.fetchData();
    }

    private void initDatasource() {
        ArrayAdapter<User> adapter = new ArrayAdapter<User>(this, android.R.layout.simple_list_item_1, new ArrayList<User>());
        mData = new DataManager(this, adapter);
    }

    private void createAddDialog() {
        View addDialogView = getLayoutInflater().inflate(R.layout.dialog_add, null, false);
        mAddName = (EditText) addDialogView.findViewById(R.id.name);
        mAddEmail = (EditText) addDialogView.findViewById(R.id.email);
        mAddAddress = (EditText) addDialogView.findViewById(R.id.address);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(addDialogView);
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.setPositiveButton(R.string.add, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                addUser();
            }
        });
        mAddDialog = builder.create();
    }

    private void addUser() {
        String name = mAddName.getText().toString();
        String email = mAddEmail.getText().toString();
        String address = mAddAddress.getText().toString();
        mData.add(User.create(name, email, address));
        mAddName.setText("");
        mAddEmail.setText("");
        mAddAddress.setText("");
    }

    private void setupViews() {
        mList = (ListView) findViewById(R.id.list);
        mList.setAdapter(mData.getAdapter());
        mRefresh = (SwipeRefreshLayout) findViewById(R.id.refresh);
        mRefresh.setOnRefreshListener(this);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddDialog();
            }
        });
    }

    private void showAddDialog() {
        if(!mAddDialog.isShowing()) {
            mAddDialog.show();
        }
    }

    @Override
    public void onRefresh() {
        mData.fetchData();
        mRefresh.setRefreshing(false);
    }
}
