package com.example.gotodoapp.ADAPTER;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gotodoapp.R;
import com.example.gotodoapp.interfaces.RecyclerViewClickListner;
import com.example.gotodoapp.model.ToDoModel;

import java.util.ArrayList;
import java.util.Random;

public class ToDoListAdapter extends RecyclerView.Adapter<ToDoListAdapter.MyViewHolder> {
    ArrayList<ToDoModel> arrayList;
    Context context;
    final private RecyclerViewClickListner clickListener;

    public ToDoListAdapter(Context context, ArrayList<ToDoModel> arrayList,RecyclerViewClickListner clickListener) {
        this.arrayList = arrayList;
        this.context = context;
        this.clickListener=clickListener;
      //  this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.todo_list_item, parent, false);

        final MyViewHolder myViewHolder = new MyViewHolder(view);

        int[] androidColors = view.getResources().getIntArray(R.array.androidcolors);
        int randomColors = androidColors[new Random().nextInt(androidColors.length)];
        myViewHolder.accordian_title.setBackgroundColor(randomColors);

        //for part 2
        myViewHolder.arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(myViewHolder.accordian_body.getVisibility() == View.VISIBLE) {
                    myViewHolder.accordian_body.setVisibility(View.GONE);
                } else {
                    myViewHolder.accordian_body.setVisibility(View.VISIBLE);
                }
            }
        });
        //for part 2

        return myViewHolder;
    }


        @Override
    public void onBindViewHolder(@NonNull ToDoListAdapter.MyViewHolder holder, int position) {
        final String title = arrayList.get(position).getTitle();
        final String description = arrayList.get(position).getDescription();
        final String id = arrayList.get(position).getId();

        holder.titleTv.setText(title);
        if(!description.equals("")) {
            holder.descriptionTv.setText(description);
        }

    }

        @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        CardView accordian_title;
        TextView titleTv, descriptionTv;
        RelativeLayout accordian_body;
        ImageView arrow, editBtn, deleteBtn, doneBtn;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            titleTv = (TextView) itemView.findViewById(R.id.task_title);
            descriptionTv = (TextView) itemView.findViewById(R.id.task_description);
            accordian_title = (CardView) itemView.findViewById(R.id.drop_down_arrow);
            accordian_body = (RelativeLayout) itemView.findViewById(R.id.drop_body);
            arrow = (ImageView) itemView.findViewById(R.id.arrow);
            editBtn = (ImageView) itemView.findViewById(R.id.edit_btn);
            deleteBtn = (ImageView) itemView.findViewById(R.id.delete_btn);
            doneBtn = (ImageView ) itemView.findViewById(R.id.done);

//for part 2
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickListener.onItemClick(getAdapterPosition());
                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    clickListener.onLongItemClick(getAdapterPosition());
                    return true;
                }
            });

            editBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickListener.onEditButtonClick(getAdapterPosition());
                }
            });

            deleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickListener.onDeleteButtonClick(getAdapterPosition());
                }
            });

            doneBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickListener.onDoneButtonClick(getAdapterPosition());
                }
            });
            //end for part2
        }
    }
}
//
//    @NonNull
//    @Override
//    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(context).inflate(R.layout.todo_list_item, parent, false);
//
//        final MyViewHolder myViewHolder = new MyViewHolder(view);
//
//        int[] androidColors = view.getResources().getIntArray(R.array.androidcolors);
//        int randomColors = androidColors[new Random().nextInt(androidColors.length)];
//
//        myViewHolder.accordian_title.setBackgroundColor(randomColors);
//
//        myViewHolder.arrow.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if(myViewHolder.accordian_body.getVisibility() == View.VISIBLE) {
//                    myViewHolder.accordian_body.setVisibility(View.GONE);
//                } else {
//                    myViewHolder.accordian_body.setVisibility(View.VISIBLE);
//                }
//            }
//        });
//        return myViewHolder;
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull ToDoListAdapter.MyViewHolder holder, int position) {
//        final String title = arrayList.get(position).getTitle();
//        final String description = arrayList.get(position).getDescription();
//        final String id = arrayList.get(position).getId();
//
//        holder.titleTv.setText(title);
//        if(!description.equals("")) {
//            holder.descriptionTv.setText(description);
//        }
//
//    }
//
//    @Override
//    public int getItemCount() {
//        return arrayList.size();
//    }
//
//
//    public class MyViewHolder extends RecyclerView.ViewHolder {
//
//}