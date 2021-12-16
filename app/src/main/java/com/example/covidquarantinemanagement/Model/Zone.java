package com.example.covidquarantinemanagement.Model;

import java.io.Serializable;
import java.util.ArrayList;

public class Zone implements Serializable {
    private String zoneId;
    private String zoneLeader;
    private String zoneName;
    private int zoneCapacity;
    private String zoneLevel1Address;
    private String zoneLevel2Address;
    private String zoneLevel3Address;
    private String zoneStreetAddress;

    private double zoneLatitude, zoneLongitude;

    private ArrayList<String> zoneCurrentVolunteers;
    private ArrayList<String> zoneTestData;

    public Zone(String zoneId, String zoneLeader, String zoneName, int zoneCapacity,
                String zoneLevel1Address, String zoneLevel2Address, String zoneLevel3Address, String zoneStreetAddress,
                double zoneLatitude, double zoneLongitude,
                ArrayList<String> zoneCurrentVolunteers, ArrayList<String> zoneTestData) {
        this.zoneId = zoneId;
        this.zoneLeader = zoneLeader;
        this.zoneName = zoneName;
        this.zoneCapacity = zoneCapacity;
        this.zoneLevel1Address = zoneLevel1Address;
        this.zoneLevel2Address = zoneLevel2Address;
        this.zoneLevel3Address = zoneLevel3Address;
        this.zoneStreetAddress = zoneStreetAddress;
        this.zoneLatitude = zoneLatitude;
        this.zoneLongitude = zoneLongitude;
        this.zoneCurrentVolunteers = zoneCurrentVolunteers;
        this.zoneTestData = zoneTestData;
    }

    public String getZoneLeader() {
        return zoneLeader;
    }

    public void setZoneLeader(String zoneLeader) {
        this.zoneLeader = zoneLeader;
    }

    public String getZoneName() {
        return zoneName;
    }

    public void setZoneName(String zoneName) {
        this.zoneName = zoneName;
    }

    public int getZoneCapacity() {
        return zoneCapacity;
    }

    public void setZoneCapacity(int zoneCapacity) {
        this.zoneCapacity = zoneCapacity;
    }

    public String getZoneLevel1Address() {
        return zoneLevel1Address;
    }

    public void setZoneLevel1Address(String zoneLevel1Address) {
        this.zoneLevel1Address = zoneLevel1Address;
    }

    public String getZoneLevel2Address() {
        return zoneLevel2Address;
    }

    public void setZoneLevel2Address(String zoneLevel2Address) {
        this.zoneLevel2Address = zoneLevel2Address;
    }

    public String getZoneLevel3Address() {
        return zoneLevel3Address;
    }

    public void setZoneLevel3Address(String zoneLevel3Address) {
        this.zoneLevel3Address = zoneLevel3Address;
    }

    public String getZoneStreetAddress() {
        return zoneStreetAddress;
    }

    public void setZoneStreetAddress(String zoneStreetAddress) {
        this.zoneStreetAddress = zoneStreetAddress;
    }

    public double getZoneLatitude() {
        return zoneLatitude;
    }

    public void setZoneLatitude(double zoneLatitude) {
        this.zoneLatitude = zoneLatitude;
    }

    public double getZoneLongitude() {
        return zoneLongitude;
    }

    public void setZoneLongitude(double zoneLongitude) {
        this.zoneLongitude = zoneLongitude;
    }

    public ArrayList<String> getZoneCurrentVolunteers() {
        return zoneCurrentVolunteers;
    }

    public void setZoneCurrentVolunteers(ArrayList<String> zoneCurrentVolunteers) {
        this.zoneCurrentVolunteers = zoneCurrentVolunteers;
    }

    public ArrayList<String> getZoneTestData() {
        return zoneTestData;
    }

    public void setZoneTestData(ArrayList<String> zoneTestData) {
        this.zoneTestData = zoneTestData;
    }

    public String getZoneId() {
        return zoneId;
    }

    public void setZoneId(String zoneId) {
        this.zoneId = zoneId;
    }
}
