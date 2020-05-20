package kr.ac.hansung.demap.model;

public class NoticeSettingDTO {

    private boolean myfolderSubs;
    private boolean myfolderPlace;
    private boolean subsfolderPlace;

    public boolean isMyfolderSubs() {
        return myfolderSubs;
    }

    public boolean isMyfolderPlace() {
        return myfolderPlace;
    }

    public boolean isSubsfolderPlace() {
        return subsfolderPlace;
    }

    public void setMyfolderSubs(boolean myfolderSubs) {
        this.myfolderSubs = myfolderSubs;
    }

    public void setMyfolderPlace(boolean myfolderPlace) {
        this.myfolderPlace = myfolderPlace;
    }

    public void setSubsfolderPlace(boolean subsfolderPlace) {
        this.subsfolderPlace = subsfolderPlace;
    }
}
