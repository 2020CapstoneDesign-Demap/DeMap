package kr.ac.hansung.demap;

import java.io.Serializable;

public class CheckedFolderId implements Serializable {
    String folderId;

    public String getFolderId() {
        return folderId;
    }

    public void setFolderId(String folderId) {
        this.folderId = folderId;
    }
}
