package com.alienonwork.emergencyresponder;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class RequesterRecyclerViewAdapter extends RecyclerView.Adapter<RequesterRecyclerViewAdapter.ViewHolder> {

    private List<String> mNumbers;

    public RequesterRecyclerViewAdapter(List<String> numbers) {
        mNumbers = numbers;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item_requester, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        viewHolder.number = mNumbers.get(i);
        viewHolder.numberView.setText(mNumbers.get(i));
    }

    @Override
    public int getItemCount() {
        if (mNumbers != null)
            return mNumbers.size();
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView numberView;
        public String number;

        public ViewHolder(View view) {
            super(view);
            numberView = view.findViewById(R.id.list_item_requester);
        }

        @Override
        public String toString() {
            return number;
        }
    }
}
