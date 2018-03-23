package com.arn.gab;

import android.support.v7.widget.RecyclerView;

/**
 * Created by arn on 10/22/2017.
 */

public class Requests {

    public String name;
    public String image;

    public Requests(){

    }

    public Requests(String name, String image) {
        this.name = name;
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
