package ch.tutti.android.bottomsheet.example;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;

import ch.tutti.android.bottomsheet.BottomSheetActivity;
import ch.tutti.android.bottomsheet.example.adapter.IntentGridAdapter;
import ch.tutti.android.bottomsheet.example.adapter.IntentListAdapter;

/**
 * Created by pboos on 27/01/15.
 */
public class BottomSheetExampleBaseActivity extends BottomSheetActivity {

    public static final int LIST_LIST = 1;

    public static final int LIST_GRID = 2;

    public static final int TITLE_TITLE = 1;

    public static final int TITLE_NO_TITLE = 2;

    public static final String EXTRA_LIST = "list";

    public static final String EXTRA_TITLE = "title";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        switch (getIntent().getIntExtra(EXTRA_LIST, LIST_LIST)) {
            case LIST_LIST:
                setupListView();
                break;
            case LIST_GRID:
                setupRecyclerView();
                break;
        }

        switch (getIntent().getIntExtra(EXTRA_TITLE, TITLE_TITLE)) {
            case TITLE_TITLE:
                setBottomSheetTitle(R.string.app_name);
                setBottomSheetIcon(R.mipmap.ic_launcher);
                break;
            case TITLE_NO_TITLE:
                setBottomSheetTitleVisible(false);
                break;
        }
    }

    private void setupRecyclerView() {
        setContentView(R.layout.activity_bottom_sheet_recyclverview);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.list);
        recyclerView.setHasFixedSize(true);
        // TODO use 4 columns on landscape? or when on tablet? (when width > [x]dp)
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        IntentGridAdapter adapter = new IntentGridAdapter(this, getShareIntentDestinations());
        adapter.setOnItemClickedListener(new IntentGridAdapter.OnItemClickedListener() {
            @Override
            public void onItemClicked(ResolveInfo resolveInfo) {
                BottomSheetExampleBaseActivity.this.onItemClicked(resolveInfo);
            }
        });
        recyclerView.setAdapter(adapter);
    }

    private void setupListView() {
        setContentView(R.layout.activity_bottom_sheet_listview);
        ListView listView = (ListView) findViewById(R.id.list);
        final IntentListAdapter adapter = new IntentListAdapter(this, getShareIntentDestinations());
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                onItemClicked(adapter.getItem(position));
            }
        });
    }

    private void onItemClicked(ResolveInfo resolveInfo) {
        Toast.makeText(this, "Clicked on: " + resolveInfo.loadLabel(getPackageManager()),
                Toast.LENGTH_SHORT).show();

        startActivity(getShareIntent().setClassName(resolveInfo.activityInfo.packageName,
                resolveInfo.activityInfo.name));

        finish();
    }

    private List<ResolveInfo> getShareIntentDestinations() {
        return getPackageManager().queryIntentActivities(getShareIntent(),
                PackageManager.GET_ACTIVITIES);
    }

    private Intent getShareIntent() {
        return new Intent(Intent.ACTION_SEND)
                .putExtra(Intent.EXTRA_TEXT, "Hello from android-bottom-sheet")
                .setType("text/plain");
    }
}
