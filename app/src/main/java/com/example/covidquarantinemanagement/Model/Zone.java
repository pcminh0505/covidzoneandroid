package com.example.covidquarantinemanagement.Model;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;

public class Zone {
    private String id;
    private String name;
    private String leader;
    private String level1Address;
    private String level2Address;
    private String level3Address;

    private double latitude, longitude;

    private ArrayList<String> volunteerUsers;
    private HashMap<Integer, Integer> peopleCount;
}
