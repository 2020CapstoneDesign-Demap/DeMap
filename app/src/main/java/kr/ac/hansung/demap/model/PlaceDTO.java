package kr.ac.hansung.demap.model;

import java.util.HashMap;

public class PlaceDTO {

    private String name = null;
    private int x = 0;
    private int y = 0;
    private Long timestamp = null;
    private HashMap<String, Boolean> tags  = new HashMap();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

}