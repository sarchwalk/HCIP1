package com.cpsc581.colorunlock;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import java.util.EmptyStackException;

public enum Fruit {
    DRAGONFRUIT ("Dragonfruit", 0xFFCC0040, R.drawable.dragonfruit_open_wrap),
    STRAWBERRY  ("Strawberry",  0xFFDE002A, R.drawable.straw_open_wrap_full),
    BANANA      ("Banana",      0xFFECBB76, R.drawable.banan_open_wrap_full),
    APPLE       ("Apple",       0xFF5C9920, R.drawable.apple_open_wrap),
    OATS        ("Blueberry",   0xFFEBC8A2, R.drawable.oats_open_wrap_full),
    EMPTY       ("Empty",     0xFF000000, R.drawable.base);


    private String stringValue;
    private int colorValue;
    private int imageValue;

    private Fruit(String toString, int color, int image){
        stringValue = toString;
        colorValue = color;
        imageValue = image;
    }

    @Override
    public String toString(){
        return stringValue;
    }


    public int toInt(){
        return colorValue;
    }

    /*
    public int toImage(){
        return imageValue;
    }
     */

    public int toImage() {return imageValue;}
}
