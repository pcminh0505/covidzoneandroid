package com.example.covidquarantinemanagement.Model;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;

public class Zone {
    private String id;
    private String name;
    private int capacity;
    private String level1Address;
    private String level2Address;
    private String level3Address;
    private String streetAddress;

    private double latitude, longitude;

    private String leader;
    private ArrayList<String> volunteerUsers;
    private HashMap<Integer, Integer> peopleCount;
}
