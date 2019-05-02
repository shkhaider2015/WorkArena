package com.example.signupactivity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

public class ListComments extends BaseAdapter {

    private Context context;
    private ArrayList<HashMap> userCommentRecords;
    private LayoutInflater inflater;
    private TextView name, comment;

    public ListComments(Context context, ArrayList<HashMap> userCommentRecords)
    {
        this.context = context;
        this.userCommentRecords = userCommentRecords;
    }

    @Override
    public int getCount()
    {
        return userCommentRecords.size();
    }

    @Override
    public Object getItem(int position)
    {
        return userCommentRecords.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        HashMap temp = userCommentRecords.get(position);

        if(convertView == null)
        {
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.comment_single_user, null);
        }
        name = convertView.findViewById(R.id.comenter_name);
        comment = convertView.findViewById(R.id.comenter_comment);

        name.setText(String.valueOf(temp.get("name")));
        comment.setText(String.valueOf(temp.get("comment")));


        return convertView;
    }
}
