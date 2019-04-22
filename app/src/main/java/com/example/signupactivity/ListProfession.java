package com.example.signupactivity;

import android.content.Context;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

public class ListProfession extends BaseAdapter {

    LayoutInflater inflater;
    Context context;
    ImageView imageView;

    int [] professionItem = {
            R.drawable.ic_tab_carpenter,
            R.drawable.ic_tab_gardner,
            R.drawable.ic_tab_painter,
            R.drawable.ic_tab_plumber
    };

    public ListProfession(Context context)
    {
        this.context = context;
    }

    @Override
    public int getCount() {
        return professionItem.length;
    }

    @Override
    public Object getItem(int position) {
        return professionItem[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(convertView == null)
        {
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_item_horizontal, null);
        }

        imageView = convertView.findViewById(R.id.horizontal_image);
        imageView.setImageResource(professionItem[position]);


        return convertView;
    }
}
