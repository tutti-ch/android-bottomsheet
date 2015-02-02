package ch.tutti.android.bottomsheet;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by pboos on 28/01/15.
 */
public class BottomSheetChooserActivity extends BottomSheetActivity {

    public static final String EXTRA_SHARE_INTENT = "shareIntent";

    public static final String EXTRA_SHARE_INTENTS = "shareIntents";

    public static final String EXTRA_TITLE = "title";

    public static final String EXTRA_ICON = "icon";

    public static final String EXTRA_PRIORITY_PACKAGES = "priority";

    public static final String EXTRA_HISTORY = "history";

    private ResolverAdapter mAdapter;

    private RecyclerView mRecyclerView;

    private boolean mHistoryEnabled;

    private ChooserHistory mHistory;

    public static ChooserIntentBuilder create(Context context) {
        return new ChooserIntentBuilder(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bs_activity_bottom_sheet_share);
        mRecyclerView = (RecyclerView) findViewById(R.id.bs_list);
        mRecyclerView.setHasFixedSize(true);
        // TODO use 4 columns on landscape? or when on tablet? (when width > [x]dp)
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        mAdapter = createAdapter();
        mAdapter.setOnItemClickedListener(new ResolverAdapter.OnItemClickedListener() {
            @Override
            public void onItemClicked(Intent intent, ResolveInfo resolveInfo) {
                onIntentChosenInternal(intent, resolveInfo);
            }
        });

        setBottomSheetTitleVisible(false);
        Intent intent = getIntent();
        if (intent.hasExtra(EXTRA_TITLE)) {
            setBottomSheetTitle(intent.getCharSequenceExtra(EXTRA_TITLE));
            setBottomSheetTitleVisible(true);
        }
        if (intent.hasExtra(EXTRA_ICON)) {
            setBottomSheetIcon(intent.getIntExtra(EXTRA_ICON, 0));
            setBottomSheetTitleVisible(true);
        }
        if (intent.hasExtra(EXTRA_PRIORITY_PACKAGES)) {
            ArrayList<String> packageNames =
                    intent.getStringArrayListExtra(EXTRA_PRIORITY_PACKAGES);
            setPriorityItems(packageNames);
        }
        setHistoryEnabled(intent.getBooleanExtra(EXTRA_HISTORY, true));
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Here so that setPriorityItems has an effect.
        mRecyclerView.setAdapter(mAdapter);
    }

    private void onIntentChosenInternal(Intent intent, ResolveInfo resolveInfo) {
        if (mHistoryEnabled) {
            ChooserHistory history = getHistory();
            history.add(resolveInfo.activityInfo.packageName);
            history.save(this);
        }
        boolean handled = onIntentChosen(intent, resolveInfo);
        if (!handled) {
            startActivity(intent);
        }
        finish();
    }

    /**
     * Called when user chooses from the chooser. Returning true will prevent the automatic
     * handling which is to start the activity defined in the intent.
     *
     * Even if true is returned, history (if enabled) will be saved.
     *
     * @param intent      chosen intent
     * @param resolveInfo resolveInfo related to chosen intent
     * @return true to prevent automatic handling
     */
    public boolean onIntentChosen(Intent intent, ResolveInfo resolveInfo) {
        return false;
    }

    /**
     * Sets the items which should be listed first in the chooser. First item in the list will be
     * shown first. If history is enabled, history items will appear before the priority items.
     *
     * @param packageNames packageNames of the items which should be placed at the top
     */
    public void setPriorityItems(List<String> packageNames) {
        mAdapter.setPriorityItems(packageNames);
    }

    /**
     * Sets the items which should be listed first in the chooser. First item in the list will be
     * shown first. If history is enabled, history items will appear before the priority items.
     *
     * @param packageNames packageNames of the items which should be placed at the top
     */
    public void setPriorityItems(String... packageNames) {
        mAdapter.setPriorityItems(packageNames != null ? Arrays.asList(packageNames) : null);
    }

    private ChooserHistory getHistory() {
        if (mHistory == null) {
            mHistory = ChooserHistory.fromSettings(this);
        }
        return mHistory;
    }

    private void setHistoryEnabled(boolean enable) {
        mHistoryEnabled = enable;
        mAdapter.setHistory(enable ? getHistory() : null);
    }

    private ResolverAdapter createAdapter() {
        Intent intent = getIntent();
        if (intent.hasExtra(EXTRA_SHARE_INTENTS)) {
            ArrayList<Intent> intents = intent.getParcelableArrayListExtra(EXTRA_SHARE_INTENTS);
            return new ResolverAdapter(this, intents);
        } else {
            Intent shareIntent = intent.getParcelableExtra(EXTRA_SHARE_INTENT);
            return new ResolverAdapter(this, shareIntent);
        }
    }
}
