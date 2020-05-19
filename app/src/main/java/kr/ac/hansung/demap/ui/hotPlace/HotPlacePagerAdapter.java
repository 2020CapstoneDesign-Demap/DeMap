package kr.ac.hansung.demap.ui.hotPlace;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import java.util.ArrayList;

import kr.ac.hansung.demap.model.HotPlaceDTO;

public class HotPlacePagerAdapter extends FragmentStatePagerAdapter {
    // tab titles
    private String[] tabTitles = new String[]{"핫플 검색", "저장된 핫플"};

    private ArrayList<HotPlaceDTO> hotPlaceList = new ArrayList<>();
    private ArrayList<HotPlaceDTO> myHotPlaceList = new ArrayList<>();

    private int tabCount;
    private String authId;

    public HotPlacePagerAdapter(@NonNull FragmentManager fm, int tabCount) {
        super(fm);
        this.tabCount = tabCount;
    }

    void sethotPlaceItem(ArrayList<HotPlaceDTO> hotPlaceDTOs) {
        hotPlaceList = hotPlaceDTOs;
    }

    void setMyhotPlaceItem(ArrayList<HotPlaceDTO> hotPlaceDTOs) {
        myHotPlaceList = hotPlaceDTOs;
    }

    void setAuthId(String authId) {
        this.authId = authId;
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
                HotPlaceFragment hotPlaceFragment = new HotPlaceFragment();
                hotPlaceFragment.setHotPlaceList(hotPlaceList);
                hotPlaceFragment.setAuthId(authId);
                return hotPlaceFragment;
            case 1:
                MyHotPlaceFragment myHotPlaceFragment = new MyHotPlaceFragment();
                myHotPlaceFragment.setHotPlaceDTO(myHotPlaceList);
                myHotPlaceFragment.setAuthId(authId);
                return myHotPlaceFragment;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return tabCount;
    }
}
