package kr.ac.hansung.demap.model;

import java.util.HashMap;

public class FolderObj implements Comparable<FolderObj> {

    private String id = null;
    private String owner = null;
    private String name = null;
    private String ispublic = "비공개";
    private String tag = null;
    private String  imageUrl = null;
    private Long timestamp = null;
    private int subscribeCount = 0;
    private int placeCount = 0;
    private HashMap<String, Boolean> subscribers  = new HashMap();
    private HashMap<String, Boolean> places  = new HashMap();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
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

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
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

    public int getPlaceCount() {
        return placeCount;
    }

    public void setPlaceCount(int placeCount) {
        this.placeCount = placeCount;
    }

    public HashMap<String, Boolean> getPlaces() {
        return places;
    }

    public void setPlaces(HashMap<String, Boolean> places) {
        this.places = places;
    }

    public HashMap<String, Boolean> getSubscribers() {
        return subscribers;
    }

    public void setSubscribers(HashMap<String, Boolean> subscribers) {
        this.subscribers = subscribers;
    }

    @Override
    public int compareTo(FolderObj folderObj) {
        if (this.subscribeCount > folderObj.getSubscribeCount())
            return -1;
        else if (this.subscribeCount < folderObj.getSubscribeCount())
            return 1;
        return 0;
    }
}
