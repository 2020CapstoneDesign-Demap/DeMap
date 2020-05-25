package kr.ac.hansung.demap.model;

import java.util.HashMap;

public class PlaceDTO {

    private String name = null;
    private String address = null;
    private String category = null;
    private String telephone = null;
    private int x = 0;
    private int y = 0;
    private Long timestamp = null;
    private HashMap<String, Boolean> tags  = new HashMap();

    private int favorite = 0;
    private HashMap<String, Boolean> favorites  = new HashMap();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public HashMap<String, Boolean> getTags() {
        return tags;
    }

    public void setTags(HashMap<String, Boolean> tags) {
        this.tags = tags;
    }

    public int getFavorite() {
        return favorite;
    }

    public void setFavorite(int favorite) {
        this.favorite = favorite;
    }

    public HashMap<String, Boolean> getFavorites() {
        return favorites;
    }

    public void setFavorites(HashMap<String, Boolean> favorites) {
        this.favorites = favorites;
    }
}