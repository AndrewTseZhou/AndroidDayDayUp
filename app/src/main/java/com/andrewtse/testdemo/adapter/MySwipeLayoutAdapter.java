package com.andrewtse.testdemo.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.andrewtse.testdemo.R;
import com.andrewtse.testdemo.adapter.MySwipeLayoutAdapter.MyViewHolder;
import com.daimajia.swipe.SimpleSwipeListener;
import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.RecyclerSwipeAdapter;
import java.util.List;

/**
 * @author xk
 * @date 2018/11/16
 */
public class MySwipeLayoutAdapter extends RecyclerSwipeAdapter<MyViewHolder> {

    private static final String TAG = "MySwipeLayoutAdapter";

    private Context context;
    private List<String> list;

    public MySwipeLayoutAdapter(Context context, List<String> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_swipelayout_view, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder viewHolder, int position) {
        viewHolder.swipeLayout.setShowMode(SwipeLayout.ShowMode.LayDown);
        viewHolder.swipeLayout.setClickToClose(true);
        viewHolder.swipeLayout.addDrag(SwipeLayout.DragEdge.Right, viewHolder.swipeLayout.findViewWithTag("bottom"));
        final int curPosition = viewHolder.getLayoutPosition();
        viewHolder.swipeLayout.addSwipeListener(new SimpleSwipeListener() {
            @Override
            public void onStartOpen(SwipeLayout layout) {
                closeAllExcept(layout);
            }
        });
        viewHolder.surface.setText(list.get(curPosition));
        viewHolder.trash.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                list.remove(curPosition);
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getSwipeLayoutResourceId(int position) {
        return position;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        private SwipeLayout swipeLayout;
        private ImageView trash;
        private TextView surface;

        public MyViewHolder(View itemView) {
            super(itemView);
            swipeLayout = itemView.findViewById(R.id.swipe_layout);
            trash = itemView.findViewById(R.id.trash);
            surface = itemView.findViewById(R.id.surface);
        }
    }
}
