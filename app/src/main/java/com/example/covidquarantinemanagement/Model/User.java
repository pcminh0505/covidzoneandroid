package com.example.covidquarantinemanagement.Model;

import java.util.ArrayList;

public class User {
    private String id;
    private String name;
    private String phone;

    private ArrayList<String> participatedZones;
    private ArrayList<String> leaderZones;

    private boolean isActive;
}
