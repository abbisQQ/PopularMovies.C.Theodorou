package com.example.babis.favoritemoviesctheodorou;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Babis on 1/23/2017.
 */


class ImageAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<String> array;
    private int width;

    ImageAdapter(Context c, ArrayList<String> paths, int x)
    {
        mContext = c;
        array=paths;
        width = x;
    }
    @Override
    public int getCount() {
        return array.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if(convertView == null)
        {
            imageView = new ImageView(mContext);
        }
        else{
            imageView = (ImageView) convertView;
        }



        Picasso.with(mContext).load("http://image.tmdb.org/t/p/w185/" + array.get(position)).
                resize(width, (int)(width*1.5)).into(imageView);
        return imageView;


    }

}




