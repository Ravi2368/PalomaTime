package com.arn.gab;

import android.app.Notification;
import android.graphics.Color;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.arn.gab.R.id.action0;
import static com.arn.gab.R.id.left;
import static com.arn.gab.R.id.right;

/**
 * Created by arn on 10/10/2017.
 */

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder>{

    private List<Messages> mMessageList;
    private FirebaseAuth mAuth;
    private FirebaseAuth mAuthLayout;
    private FirebaseAuth mAuthText;
    private int val;

    private View rightView,leftView;

    public MessageAdapter(List<Messages> mMessageList){

        this.mMessageList = mMessageList;

    }

    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType){

        // new worked started from here.....

        leftView = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_single_layout,parent,false);

        rightView = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_right_layout,parent,false);

        return new MessageViewHolder(rightView);

    }

    public class MessageViewHolder extends RecyclerView.ViewHolder {

        public TextView messageText;
        public CircleImageView profileImage;
        public TextView messageTime;

        public MessageViewHolder(View itemView) {

            super(itemView);

            messageText = (TextView)itemView.findViewById(R.id.message_text_right);
            messageTime = (TextView)itemView.findViewById(R.id.message_date_right);

        }
    }

    @Override
    public void onBindViewHolder(MessageViewHolder viewHolder, int i){

        mAuth = FirebaseAuth.getInstance();

        val = i;

        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {

            String currentUserId = mAuth.getCurrentUser().getUid();

            Messages c = mMessageList.get(i);

            String from_user = c.getFrom();

            if (from_user.equals(currentUserId)) {

                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)viewHolder.messageText.getLayoutParams();
                params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 1);
                params.addRule(RelativeLayout.ALIGN_PARENT_END,1);

                params.addRule(RelativeLayout.ALIGN_PARENT_LEFT, 0);
                params.addRule(RelativeLayout.ALIGN_PARENT_START,0);

                viewHolder.messageText.setLayoutParams(params);

                RelativeLayout.LayoutParams paramsDate = (RelativeLayout.LayoutParams)viewHolder.messageTime.getLayoutParams();
                paramsDate.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 1);
                paramsDate.addRule(RelativeLayout.ALIGN_PARENT_END,1);

                paramsDate.addRule(RelativeLayout.ALIGN_PARENT_LEFT, 0);
                paramsDate.addRule(RelativeLayout.ALIGN_PARENT_START,0);

                viewHolder.messageTime.setLayoutParams(paramsDate);

                viewHolder.messageText.setBackgroundResource(R.drawable.message_grey_bg);
                //viewHolder.messageText.setBackgroundColor(Color.WHITE);

                viewHolder.messageText.setTextColor(Color.parseColor("#ffffff"));
                viewHolder.messageTime.setTextColor(Color.parseColor("#090929"));


            } else {

                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)viewHolder.messageText.getLayoutParams();
                params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 0);
                params.addRule(RelativeLayout.ALIGN_PARENT_END,0);

                params.addRule(RelativeLayout.ALIGN_PARENT_LEFT, 1);
                params.addRule(RelativeLayout.ALIGN_PARENT_START,1);

                viewHolder.messageText.setLayoutParams(params);

                RelativeLayout.LayoutParams paramsDate = (RelativeLayout.LayoutParams)viewHolder.messageTime.getLayoutParams();
                paramsDate.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 0);
                paramsDate.addRule(RelativeLayout.ALIGN_PARENT_END,0);

                paramsDate.addRule(RelativeLayout.ALIGN_PARENT_LEFT, 1);
                paramsDate.addRule(RelativeLayout.ALIGN_PARENT_START,1);

                viewHolder.messageTime.setLayoutParams(paramsDate);
                viewHolder.messageText.setBackgroundResource(R.drawable.message_sky_blue_background);
                viewHolder.messageText.setTextColor(Color.parseColor("#090929"));
                viewHolder.messageTime.setTextColor(Color.parseColor("#090929"));

            }

            viewHolder.messageText.setText(c.getMessage());
            viewHolder.messageTime.setText(c.getsenttime());

        }

    }

    private View getView(int i) {

        View viewToSend;

        mAuth = FirebaseAuth.getInstance();

        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {

            String currentUserId = mAuth.getCurrentUser().getUid();

            Messages c = mMessageList.get(i);

            String from_user = c.getFrom();

            if (from_user.equals(currentUserId)) {

                viewToSend = rightView;

            }
            else {

                viewToSend = leftView;
            }
        }
        else {

            viewToSend = rightView;

        }

        return viewToSend;

    }

    public int getItemCount(){

        return mMessageList.size();

    }
}
