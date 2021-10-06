package com.creativethoughts.iscore;

import java.util.ArrayList;

/**
 * Created by creativethoughtssystechindiaprivatelimited on 16/03/15
 */
public class MenuItem {

    public ArrayList<MenuItem> subItems;
    public int id;
    private int menuIcon;
    private String strMenuItem;

    public MenuItem() {
        subItems = new ArrayList<>();
    }

    public int getMenuIcon() {
        return menuIcon;
    }

    public void setMenuIcon(int menuIcon) {
        this.menuIcon = menuIcon;
    }

    public String getMenuLabel() {
        return strMenuItem;
    }

    public void setMenuLabel(String strMenuItem) {
        this.strMenuItem = strMenuItem;
    }
}
