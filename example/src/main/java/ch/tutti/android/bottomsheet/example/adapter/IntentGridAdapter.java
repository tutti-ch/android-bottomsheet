package ch.tutti.android.bottomsheet.example.adapter;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import ch.tutti.android.bottomsheet.example.R;

/**
 * Created by pboos on 28/01/15.
 */
public class IntentGridAdapter extends RecyclerView.Adapter<IntentGridAdapter.ViewHolder> {

    private final List<ResolveInfo> mItems;

    private final PackageManager mPackageManager;

    private final int mIconSize;

    private OnItemClickedListener mOnItemClickedListener;

    public IntentGridAdapter(Context context, List<ResolveInfo> shareIntentDestinations) {
        mItems = shareIntentDestinations;
        mPackageManager = context.getPackageManager();
        mIconSize = context.getResources().getDimensionPixelSize(R.dimen.icon);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.bs_item_share, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final ResolveInfo item = mItems.get(position);
        holder.text.setText(item.activityInfo.loadLabel(mPackageManager));
        Drawable icon = item.loadIcon(mPackageManager);
        icon.setBounds(0, 0, mIconSize,
                (int) (((float) icon.getIntrinsicHeight()) / icon.getIntrinsicWidth() * mIconSize));
        holder.text.setCompoundDrawables(null, icon, null, null);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnItemClickedListener.onItemClicked(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public void setOnItemClickedListener(OnItemClickedListener listener) {
        mOnItemClickedListener = listener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        final TextView text;

        public ViewHolder(View itemView) {
            super(itemView);
            text = (TextView) itemView;
        }
    }

    public interface OnItemClickedListener {

        void onItemClicked(ResolveInfo resolveInfo);
    }
}
