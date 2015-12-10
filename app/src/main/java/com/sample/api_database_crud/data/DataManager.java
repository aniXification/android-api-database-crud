package com.sample.api_database_crud.data;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AlertDialog;
import android.widget.ArrayAdapter;

import com.sample.api_database_crud.model.User;
import com.sample.api_database_crud.net.NetworkCaller;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by razzle on 12/10/15.
 */
public class DataManager {
    public static final String DATABASE_NAME = "db";
    public static final String CREATE_TABLE_CONDITIONALLY_QUERY = "create table if not exists `user`(`id` varchar(10), `name` varchar(100), `email` varchar(255), `address` varchar(255))";
    public static final String[] ALL_COLUMNS = new String[]{"id", "name", "email", "address"};
    private SQLiteDatabase db;
    private NetworkCaller caller = new NetworkCaller();
    private Activity activity;
    private ProgressDialog dialog;

    public ArrayAdapter<User> getAdapter() {
        return adapter;
    }

    private ArrayAdapter<User> adapter;

    public DataManager(Activity activity, ArrayAdapter<User> adapter) {
        this.activity = activity;
        this.adapter = adapter;
        Context appContext = activity.getApplicationContext();
        this.db = appContext.openOrCreateDatabase(DATABASE_NAME, Context.MODE_PRIVATE, null);
        db.execSQL(CREATE_TABLE_CONDITIONALLY_QUERY);
        dialog = new ProgressDialog(activity);
        dialog.setMessage("Processing..");
        dialog.setCancelable(false);
    }

    public void fetchData() {
        showProcessingMessage();
        caller.getUsers(new NetworkCaller.Callback() {
            @Override
            public void onSuccess() {
                hideProcessingMessage();
            }

            @Override
            public void onFailure() {
                hideProcessingMessage();
                adapter.addAll(getFromDatabase());
            }

            @Override
            public void onData(List<User> users) {
                adapter.clear();
                adapter.addAll(users);
                db.delete("user", null, null);
                for (User user : users) {
                    addToDatabase(user);
                }
            }
        });
    }

    public void add(final User user) {
        showProcessingMessage();
        caller.addUser(user, new NetworkCaller.Callback() {
            @Override
            public void onSuccess() {
                addToList(user);
                addToDatabase(user);
            }

            @Override
            public void onFailure() {
                showErrorDialog();
            }

            @Override
            public void onData(List<User> user) {
                //
            }
        });
    }

    private List<User> getFromDatabase() {
        List<User> users = new ArrayList<>();
        Cursor cursor = db.query("user", ALL_COLUMNS, "*", null, null, null, null);
        cursor.moveToFirst();
        while(!cursor.isAfterLast()) {
            String id = cursor.getString(0);
            String name = cursor.getString(0);
            String email = cursor.getString(0);
            String address = cursor.getString(0);
            users.add(new User(id, name, email, address));
        }
        return users;
    }

    private void addToList(User user) {
        adapter.add(user);
    }

    private void addToDatabase(User user) {
        db.execSQL("insert into `user` values('" + user.id + "', '" + user.name + "', '" + user.email + "', '" + user.address + "')");
    }

    private void removeFromList(User user) {
        adapter.remove(user);
    }

    private void removeFromDatabase(User user) {
        db.execSQL("delete from `user` where `id` = '" + user.id + "'");
    }

    private void updateIntoList(User oldUser, User newUser) {
        int position = adapter.getPosition(oldUser);
        adapter.remove(oldUser);
        adapter.insert(newUser, position);
    }

    private void updateIntoServer(User oldUser, User newUser) {
        db.execSQL("update `user` set `name` = '" + newUser.name + "' `email` = '" + newUser.email + "' `address` = '" + newUser.address + "' where `id` = '" + oldUser.id + "'");
    }

    private void showProcessingMessage() {
        if(!dialog.isShowing())
            dialog.show();
    }

    private void hideProcessingMessage() {
        dialog.hide();
    }

    private void showErrorDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setMessage("Error occurred, please try later.");
        builder.show();
    }
}
