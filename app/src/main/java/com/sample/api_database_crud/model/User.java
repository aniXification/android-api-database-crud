package com.sample.api_database_crud.model;

import android.util.Pair;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by razzle on 12/10/15.
 */
public class User {
    public final String id;
    public final String name;
    public final String email;
    public final String address;

    public User(String id, String name, String email, String address) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.address = address;
    }

    public static User create(String name, String email, String address) {
        return new User("", name, email, address);
    }

    @Override
    public String toString() {
        return name + "\n" + email + "\n" + address;
    }

    public List<Pair<String, String>> getAsPairsList() {
        List<Pair<String, String>> pairs = new ArrayList<>(4);
        pairs.add(new Pair<>("name", name));
        pairs.add(new Pair<>("email", email));
        pairs.add(new Pair<>("address", address));
        pairs.add(new Pair<>("id", id));
        return pairs;
    }
}
