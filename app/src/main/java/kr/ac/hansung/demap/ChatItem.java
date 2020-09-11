package kr.ac.hansung.demap;

public class ChatItem {
    private String id;
    private String content;
    private Long timestamp = null;

    public ChatItem(String id, String content) {
        this.id = id;
        this.content = content;
    }

    public ChatItem(String id, String content, Long timestamp) {
        this.id = id;
        this.content = content;
        this.timestamp = timestamp;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
