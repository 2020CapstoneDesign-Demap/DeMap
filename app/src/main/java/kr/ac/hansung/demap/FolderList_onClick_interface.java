package kr.ac.hansung.demap;

import java.util.Map;

import kr.ac.hansung.demap.model.FolderObj;

interface FolderList_onClick_interface {
    //void onCheckbox(Map<String, Boolean> FolderId);
    void onCheckbox(String FolderId, String FolderOwner, String FolderName);

}
