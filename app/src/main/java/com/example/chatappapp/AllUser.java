package com.example.chatappapp;


import com.example.chatappapp.Model.User;

import java.util.ArrayList;

public class AllUser {

    private ArrayList<User> allUsers;

    public ArrayList<User> getAllUsers() {
        return allUsers;
    }
    public AllUser(){
        allUsers = new ArrayList<>();
    }

    public AllUser(ArrayList<User> allUsers) {
        this.allUsers = allUsers;
    }

    public void setAllUsers(ArrayList<User> allUsers) {
        this.allUsers = allUsers;
    }

}
