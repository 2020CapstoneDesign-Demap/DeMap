package kr.ac.hansung.demap.model;

public class HotPlaceDTO {
    private String tag;
    private String imageUrl;

    public HotPlaceDTO() {
        this.tag = tag;
        this.imageUrl = imageUrl;
    }

    public String getTag() {
        return tag;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
