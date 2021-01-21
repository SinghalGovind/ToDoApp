package com.example.gotodoapp.interfaces;

public interface RecyclerViewClickListner {
    void onItemClick(int position);

    void onLongItemClick(int position);

    void onEditButtonClick(int position);
    void onDeleteButtonClick(int position);
    void onDoneButtonClick(int position);
}
