package ch.tutti.android.bottomsheet.example.adapter;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import ch.tutti.android.bottomsheet.example.R;

/**
 * Created by pboos on 28/01/15.
 */
public class IntentListAdapter extends BaseAdapter {

    private final List<ResolveInfo> mItems;

    private final PackageManager mPackageManager;

    private final int mIconSize;

    public IntentListAdapter(Context context,
            List<ResolveInfo> shareIntentDestinations) {
        mItems = shareIntentDestinations;
        mPackageManager = context.getPackageManager();
        mIconSize = context.getResources().getDimensionPixelSize(R.dimen.icon);
    }

    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public ResolveInfo getItem(int position) {
        return mItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_item_intent, parent, false));
        } else {
            holder = ViewHolder.from(convertView);
        }

        ResolveInfo item = getItem(position);
        holder.text.setText(item.loadLabel(mPackageManager));
        Drawable icon = item.loadIcon(mPackageManager);
        icon.setBounds(0, 0, mIconSize,
                (int) (((float) icon.getIntrinsicHeight()) / icon.getIntrinsicWidth() * mIconSize));
        holder.text.setCompoundDrawables(icon, null, null, null);

        return holder.itemView;
    }

    private static class ViewHolder {

        public final View itemView;

        private final TextView text;

        public ViewHolder(View view) {
            this.itemView = view;
            this.text = (TextView) view;
            view.setTag(this);
        }

        public static ViewHolder from(View convertView) {
            return (ViewHolder) convertView.getTag();
        }
    }
}
