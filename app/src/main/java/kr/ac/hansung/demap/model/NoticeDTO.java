package kr.ac.hansung.demap.model;

import java.util.HashMap;

//public class NoticeDTO {
//
//    private HashMap<String, Boolean> notices  = new HashMap();
//
//    public HashMap<String, Boolean> getNotices() {
//        return notices;
//    }
//
//    public void setNotices(HashMap<String, Boolean> notices) {
//        this.notices = notices;
//    }
//
//}

public class NoticeDTO {

    private String notice;
    private Long timestamp;

    private String noticeType;
    private String folder_id;

    public String getNotice() {
        return notice;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public String getNoticeType() {
        return noticeType;
    }

    public String getFolder_id() {
        return folder_id;
    }

    public void setNotice(String notice) {
        this.notice = notice;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public void setNoticeType(String noticeType) {
        this.noticeType = noticeType;
    }

    public void setFolder_id(String folder_id) {
        this.folder_id = folder_id;
    }
}