package kr.ac.hansung.demap;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import java.util.ArrayList;

import kr.ac.hansung.demap.model.FolderDTO;

public class TabPagerAdapter extends FragmentStatePagerAdapter {

    // tab titles
    private String[] tabTitles = new String[]{"내 폴더", "구독 폴더"};

    // adapter에 들어갈 folder list
    private ArrayList<FolderDTO> myfolderDTOS = new ArrayList<>();
    private ArrayList<FolderDTO> subsfolderDTOS = new ArrayList<>();

    private int tabCount;

    public TabPagerAdapter(@NonNull FragmentManager fm, int tabCount) {
        super(fm);
        this.tabCount = tabCount;
    }

    void addmyfolderItem(FolderDTO folderDTO) {
        myfolderDTOS.add(folderDTO);
    }

    void addsubsfolderItem(FolderDTO folderDTO) {
        subsfolderDTOS.add(folderDTO);
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
                MyfolderFragment myfolderFragment = new MyfolderFragment();
                myfolderFragment.setFolderDTOs(myfolderDTOS);
                return myfolderFragment;
            case 1:
                SubsfolderFragment subsfolderFragment = new SubsfolderFragment();
                subsfolderFragment.setFolderDTOs(subsfolderDTOS);
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
