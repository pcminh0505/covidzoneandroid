package com.example.covidquarantinemanagement.Model;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;

public class Zone {
    private String id;
    private String name;
    private String leader;
    private double latitude, longitude;

    private SimpleDateFormat startDate;
    private SimpleDateFormat endDate;

    private ArrayList<String> volunteerUsers;
    private HashMap<Integer, Integer> peopleCount;
}
