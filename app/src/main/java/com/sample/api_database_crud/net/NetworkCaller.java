package com.sample.api_database_crud.net;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;
import android.util.Pair;

import com.sample.api_database_crud.model.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by razzle on 12/10/15.
 */
public class NetworkCaller {

    public static final String BASE_URL = "http://anixification.com/usercontact/backend/web/index.php/";
    public static final String URL = BASE_URL + "users";
    private HttpURLConnection c;

    public void getUsers(final Callback callback) {
        new AsyncTask<Void, Void, List<User>>() {
            @Override
            protected List<User> doInBackground(Void[] params) {
                List<User> users = null;
                try {
                    users = getUsers();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return users;
            }

            @Override
            protected void onPostExecute(List<User> users) {
                super.onPostExecute(users);
                if(users == null) {
                    callback.onFailure();
                } else {
                    callback.onData((List<User>) users);
                    callback.onSuccess();
                }
            }
        }.execute();
    }

    @NonNull
    private List<User> getUsers() throws IOException, JSONException {
        List<User> users = new ArrayList<>();
        prepareGetConnection(URL);
        if(isConnectionGood()) {
            String response = getResponseString(c);
            Log.d("RESPONSE", response);
            users.addAll(parseUsersListFromJson(response));
        }
        closeConnection();
        return users;
    }

    private void closeConnection() {
        c.disconnect();
    }

    public void addUser(final User user, final Callback callback) {
        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void[] params) {
                try {
                    addUser(user);
                    return true;
                } catch (IOException e) {
                    e.printStackTrace();
                    return false;
                }
            }

            @Override
            protected void onPostExecute(Boolean isSuccess) {
                super.onPostExecute(isSuccess);
                if(!isSuccess) {
                    callback.onFailure();
                } else {
                    callback.onSuccess();
                }
            }
        }.execute();
    }

    private void addUser(User user) throws IOException {
        preparePostConnection(URL);
        String postQuery = getPostQuery(user.getAsPairsList());
        OutputStream os = c.getOutputStream();
        BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(os, "UTF-8"));
        writer.write(postQuery);
        writer.flush();
        writer.close();
        os.close();
        closeConnection();
    }

    private String getPostQuery(List<Pair<String, String>> pairs) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;

        for (Pair<String, String> pair : pairs) {
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(pair.first, "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(pair.second, "UTF-8"));
        }

        return result.toString();
    }

    private boolean isConnectionGood() throws IOException {
        return c.getResponseCode() == 200;
    }

    private void prepareGetConnection(String url) throws IOException {
        prepareConnection(url, "GET");
        c.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
        connect();
    }

    private void preparePostConnection(String url) throws IOException {
        prepareConnection(url, "POST");
        c.setDoOutput(true);
        c.setDoInput(true);
        connect();
    }

    private void prepareConnection(String url, String requestMethod) throws IOException {
        openConnection(url);
        c.setRequestMethod(requestMethod);
    }

    private void connect() throws IOException {
        c.connect();
    }

    private void openConnection(String getUrl) throws IOException {
        URL url = new URL(getUrl);
        c = null;
        c = (HttpURLConnection) url.openConnection();
    }

    private Collection<? extends User> parseUsersListFromJson(String response) throws JSONException {
        JSONArray array = new JSONArray(response);
        List<User> users = new ArrayList<>(array.length());
        for (int i = 0; i < array.length(); i++) {
            JSONObject object = array.getJSONObject(i);
            String id = object.getString("id");
            String name = object.getString("name");
            String email = object.getString("email");
            String address = object.getString("address");
            users.add(new User(id, name, email, address));
        }
        return users;
    }

    private String getResponseString(HttpURLConnection c) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(c.getInputStream(), "UTF-8"));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line).append("\n");
        }
        br.close();
        return sb.toString();
    }

    public interface Callback {
        void onSuccess();
        void onFailure();
        void onData(List<User> user);
    }

}
