package kr.ac.hansung.demap.model;

public class HotPlaceDTO {
    private String tag;
    private String imageUrl;
    private String postUrl;
    private Long timestamp = null;
    private String comment;

    public String getTag() {
        return tag;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getPostUrl() {
        return postUrl;
    }

    public String getComment() {
        return comment;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setPostUrl(String postUrl) {
        this.postUrl = postUrl;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

}