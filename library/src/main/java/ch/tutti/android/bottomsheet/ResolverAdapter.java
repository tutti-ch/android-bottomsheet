package ch.tutti.android.bottomsheet;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.LabeledIntent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

/**
 * Created by pboos on 28/01/15.
 */
public class ResolverAdapter extends RecyclerView.Adapter<ResolverAdapter.ViewHolder> {

    private static final String TAG = ResolverAdapter.class.getSimpleName();

    private final ResolverComparator mComparator;

    private Intent mShareIntent;

    private final PackageManager mPackageManager;

    private final int mIconSize;

    private ArrayList<Intent> mIntents;

    private List<DisplayResolveInfoWithIntent> mItems;

    private OnItemClickedListener mOnItemClickedListener;

    private HashMap<String, Integer> mPriorities;

    private ChooserHistory mHistory;

    public ResolverAdapter(Context context, Intent shareIntent) {
        mPackageManager = context.getPackageManager();
        mIconSize = context.getResources().getDimensionPixelSize(R.dimen.bs_share_icon);
        mShareIntent = shareIntent;
        mComparator = new ResolverComparator(context);
    }

    public ResolverAdapter(Context context, ArrayList<Intent> intents) {
        mPackageManager = context.getPackageManager();
        mIconSize = context.getResources().getDimensionPixelSize(R.dimen.bs_share_icon);
        mIntents = intents;
        mComparator = new ResolverComparator(context);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.bs_item_share, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        ensureListBuilt();
        final DisplayResolveInfoWithIntent item = mItems.get(position);
        holder.text.setText(item.getLabel(mPackageManager));
        holder.text.setCompoundDrawables(null, null, null, null);
        new IconLoaderTask(item, holder.text).execute();
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnItemClickedListener.onItemClicked(item.intent, item.resolveInfo);
            }
        });
    }

    @Override
    public int getItemCount() {
        ensureListBuilt();
        return mItems.size();
    }

    public void setOnItemClickedListener(OnItemClickedListener listener) {
        mOnItemClickedListener = listener;
    }

    public void setPriorityItems(List<String> packageNames) {
        if (packageNames == null || packageNames.isEmpty()) {
            mPriorities = null;
            rebuildItems();
            return;
        }

        int size = packageNames.size();
        mPriorities = new HashMap<>(size);
        for (int i = 0; i < size; i++) {
            // position 0 should have highest priority,
            // starting with 1 for lowest priority.
            mPriorities.put(packageNames.get(i), size - i + 1);
        }
        rebuildItems();
    }

    public void setHistory(ChooserHistory history) {
        mHistory = history;
        rebuildItems();
    }

    private void rebuildItems() {
        mItems = null;
        notifyDataSetChanged();
    }

    private void ensureListBuilt() {
        if (mItems == null) {
            buildList();
        }
    }

    private void buildList() {
        if (mIntents != null) {
            mItems = new ArrayList<>(mIntents.size());
            for (Intent intent : mIntents) {
                // new Intent to get a normal intent in case of a LabeledIntent
                ActivityInfo ai = new Intent(intent)
                        .resolveActivityInfo(mPackageManager, PackageManager.GET_ACTIVITIES);
                if (ai == null) {
                    Log.w(TAG, "No activity found for " + intent);
                    continue;
                }
                ResolveInfo resolveInfo = new ResolveInfo();
                resolveInfo.activityInfo = ai;
                if (intent instanceof LabeledIntent) {
                    LabeledIntent labeledIntent = (LabeledIntent) intent;
                    resolveInfo.resolvePackageName = labeledIntent.getSourcePackage();
                    resolveInfo.labelRes = labeledIntent.getLabelResource();
                    resolveInfo.nonLocalizedLabel = labeledIntent.getNonLocalizedLabel();
                    resolveInfo.icon = labeledIntent.getIconResource();
                }

                if (resolveInfo.icon == 0 || resolveInfo.labelRes == 0) {
                    try {
                        ApplicationInfo info = mPackageManager.getApplicationInfo(
                                intent.getPackage(), 0);
                        if (resolveInfo.icon == 0) {
                            resolveInfo.icon = info.icon;
                        }
                        if (resolveInfo.labelRes == 0) {
                            resolveInfo.labelRes = info.labelRes;
                        }
                    } catch (PackageManager.NameNotFoundException ignored) {
                    }
                }

                mItems.add(new DisplayResolveInfoWithIntent(resolveInfo, intent));
            }
        } else {
            List<ResolveInfo> resolveInfos = mPackageManager
                    .queryIntentActivities(mShareIntent, PackageManager.GET_ACTIVITIES);
            mItems = new ArrayList<>(resolveInfos.size());
            for (ResolveInfo resolveInfo : resolveInfos) {
                Intent intent = new Intent(mShareIntent);
                intent.setClassName(resolveInfo.activityInfo.packageName,
                        resolveInfo.activityInfo.name);
                mItems.add(new DisplayResolveInfoWithIntent(resolveInfo, intent));
            }
        }

        Collections.sort(mItems, mComparator);
    }

    private class DisplayResolveInfoWithIntent {

        private final Intent intent;

        private ResolveInfo resolveInfo;

        private CharSequence label;

        private Drawable icon;

        public DisplayResolveInfoWithIntent(ResolveInfo resolveInfo, Intent intent) {
            this.resolveInfo = resolveInfo;
            this.intent = intent;
        }

        public CharSequence getLabel(PackageManager pm) {
            if (label == null) {
                label = resolveInfo.loadLabel(pm);
            }
            return label;
        }

        public Drawable getIcon(PackageManager pm) {
            if (icon == null) {
                icon = resolveInfo.loadIcon(pm);
            }
            return icon;
        }
    }

    class ResolverComparator implements Comparator<DisplayResolveInfoWithIntent> {

        private final Collator mCollator;

        public ResolverComparator(Context context) {
            mCollator = Collator.getInstance(context.getResources().getConfiguration().locale);
        }

        @Override
        public int compare(DisplayResolveInfoWithIntent lhs, DisplayResolveInfoWithIntent rhs) {
            if (mHistory != null) {
                int leftCount = mHistory.get(lhs.resolveInfo.activityInfo.packageName);
                int rightCount = mHistory.get(rhs.resolveInfo.activityInfo.packageName);
                if (leftCount != rightCount) {
                    return rightCount - leftCount;
                }
            }

            if (mPriorities != null) {
                int leftPriority = getPriority(lhs);
                int rightPriority = getPriority(rhs);
                if (leftPriority != rightPriority) {
                    return rightPriority - leftPriority;
                }
            }

            CharSequence sa = lhs.getLabel(mPackageManager);
            if (sa == null) {
                sa = lhs.resolveInfo.activityInfo.name;
            }
            CharSequence sb = rhs.getLabel(mPackageManager);
            if (sb == null) {
                sb = rhs.resolveInfo.activityInfo.name;
            }

            return mCollator.compare(sa.toString(), sb.toString());
        }

        private int getPriority(DisplayResolveInfoWithIntent lhs) {
            Integer integer = mPriorities.get(lhs.resolveInfo.activityInfo.packageName);
            return integer != null ? integer : 0;
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        final TextView text;

        public ViewHolder(View itemView) {
            super(itemView);
            text = (TextView) itemView;
        }
    }

    public static interface OnItemClickedListener {

        void onItemClicked(Intent intent, ResolveInfo resolveInfo);
    }

    private class IconLoaderTask extends AsyncTask<Void, Void, Drawable> {

        private final WeakReference<TextView> mTextView;

        private final DisplayResolveInfoWithIntent mInfo;

        public IconLoaderTask(DisplayResolveInfoWithIntent info, TextView textView) {
            textView.setTag(R.id.bs_icon, this);
            mTextView = new WeakReference<>(textView);
            mInfo = info;
        }

        @Override
        protected Drawable doInBackground(Void... params) {
            Drawable icon = mInfo.getIcon(mPackageManager);
            icon.setBounds(0, 0, mIconSize,
                    (int) (((float) icon.getIntrinsicHeight()) / icon.getIntrinsicWidth()
                            * mIconSize));
            return icon;
        }

        @Override
        protected void onPostExecute(Drawable icon) {
            super.onPostExecute(icon);
            TextView textView = mTextView.get();
            if (textView != null && textView.getTag(R.id.bs_icon) == this) {
                textView.setCompoundDrawables(null, icon, null, null);
            }
        }
    }
}
