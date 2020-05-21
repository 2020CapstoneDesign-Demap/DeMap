package kr.ac.hansung.demap.ui.myfolderlist;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import java.util.ArrayList;

import kr.ac.hansung.demap.model.FolderDTO;
import kr.ac.hansung.demap.model.FolderObj;

public class TabPagerAdapter extends FragmentStatePagerAdapter {

    MyfolderFragment myfolderFragment = new MyfolderFragment();

    // tab titles
    private String[] tabTitles = new String[]{"내 폴더", "구독 폴더"};

    // adapter에 들어갈 folder list
    private ArrayList<FolderObj> myfolderObjs = new ArrayList<>();
    private ArrayList<FolderObj> subsfolderObjS = new ArrayList<>();

    private String authId;

    private int tabCount;

    public TabPagerAdapter(@NonNull FragmentManager fm, int tabCount) {
        super(fm);
        this.tabCount = tabCount;
    }

    void setmyfolderItem(ArrayList<FolderObj> myfolderObj) {
        myfolderObjs = myfolderObj;
    }

    void setsubsfolderItem(ArrayList<FolderObj> subsfolderObj) {
        subsfolderObjS = subsfolderObj;
    }

    void setAuthId(String authId) {
        this.authId = authId;
    }

    void setUpdate(int position, FolderDTO folderDTO, String folderId) {
        FolderObj fObj = new FolderObj();
        fObj.setId(folderId);
        fObj.setName(folderDTO.getName());
        fObj.setPlaceCount(folderDTO.getPlaceCount());
        fObj.setSubscribeCount(folderDTO.getSubscribeCount());
        fObj.setImageUrl(folderDTO.getImageUrl());
        myfolderObjs.set(position, fObj);
        setmyfolderItem(myfolderObjs);
        myfolderFragment.updateAdapterItem(position,folderDTO,folderId);

    }


    // overriding getPageTitle()
    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitles[position];
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                myfolderFragment.setFolderDTOs(myfolderObjs);
                myfolderFragment.setAuthId(authId);
                return myfolderFragment;
            case 1:
                SubsfolderFragment subsfolderFragment = new SubsfolderFragment();
                subsfolderFragment.setFolderDTOs(subsfolderObjS);
                subsfolderFragment.setAuthId(authId);
                return subsfolderFragment;
            default:
                return null;
        }

    }

    @Override
    public int getCount() {
        return tabCount;
    }
}
