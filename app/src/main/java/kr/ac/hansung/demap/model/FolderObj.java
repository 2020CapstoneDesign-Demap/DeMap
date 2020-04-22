package kr.ac.hansung.demap.model;

import java.util.HashMap;

public class FolderObj {

    private String id = null;
    private String name = null;
    private String ispublic = "비공개";
    private String  imageUrl = null;
    private Long timestamp = null;
    private int subscribeCount = 0;
    private HashMap<String, Boolean> places  = new HashMap();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIspublic() {
        return ispublic;
    }

    public void setIspublic(String ispublic) {
        this.ispublic = ispublic;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public int getSubscribeCount() {
        return subscribeCount;
    }

    public void setSubscribeCount(int subscribeCount) {
        this.subscribeCount = subscribeCount;
    }

    public HashMap<String, Boolean> getPlaces() {
        return places;
    }

    public void setPlaces(HashMap<String, Boolean> places) {
        this.places = places;
    }

}
