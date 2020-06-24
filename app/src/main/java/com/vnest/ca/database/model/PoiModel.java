package com.vnest.ca.database.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.vnest.ca.entity.Gps;

import org.jetbrains.annotations.NotNull;

@Entity
public class PoiModel {
    @ColumnInfo
    private int timeMillis;
    @ColumnInfo
    private String img;
    @ColumnInfo
    private String address;
    @ColumnInfo
    @PrimaryKey
    @NotNull
    private String gps;
    @ColumnInfo
    private String title;
    @ColumnInfo
    private String url;
    @ColumnInfo
    private String phone;
    @ColumnInfo
    private String category;
    @ColumnInfo
    private String brand;
    @ColumnInfo
    private String hash;
    @ColumnInfo
    private String email;
    @ColumnInfo
    private double distance;

    public int getTimeMillis() {
        return timeMillis;
    }

    public void setTimeMillis(int timeMillis) {
        this.timeMillis = timeMillis;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getGps() {
        return gps;
    }

    public void setGps(String gps) {
        this.gps = gps;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }
}
