package ch.tutti.android.bottomsheet.example;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ShareCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import ch.tutti.android.bottomsheet.BottomSheetActivity;
import ch.tutti.android.bottomsheet.BottomSheetChooserActivity;


public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.list);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new ExampleAdapter());
    }

    private void openBottomSheet(int position) {
        switch (position) {
            case 0:
                openBottomSheet(BottomSheetExampleLightActivity.class,
                        BottomSheetExampleBaseActivity.LIST_LIST,
                        BottomSheetExampleBaseActivity.TITLE_TITLE);
                break;
            case 1:
                openBottomSheet(BottomSheetExampleLightActivity.class,
                        BottomSheetExampleBaseActivity.LIST_LIST,
                        BottomSheetExampleBaseActivity.TITLE_NO_TITLE);
                break;
            case 2:
                openBottomSheet(BottomSheetExampleLightActivity.class,
                        BottomSheetExampleBaseActivity.LIST_GRID,
                        BottomSheetExampleBaseActivity.TITLE_TITLE);
                break;
            case 3:
                openBottomSheet(BottomSheetExampleActivity.class,
                        BottomSheetExampleBaseActivity.LIST_GRID,
                        BottomSheetExampleBaseActivity.TITLE_TITLE);
                break;
            case 4:
                Intent bottomSheetIntent = BottomSheetChooserActivity.create(this)
                        .forIntent(getShareIntent())
                        .title("Share")
                        .icon(R.mipmap.ic_launcher)
                        .getIntent();
                BottomSheetActivity.startActivty(this, bottomSheetIntent);
                break;
            case 5:
                bottomSheetIntent = BottomSheetCustomChooserActivity.create(this)
                        .forIntent(getShareIntent())
                        .title("Custom Share")
                        .icon(R.mipmap.ic_launcher)
                        .priority("com.whatsapp", "com.facebook.katana", "com.facebook.orca",
                                "com.google.android.gm", "com.google.android.talk",
                                "com.google.android.apps.plus")
                        .getIntent();
                BottomSheetActivity.startActivty(this, bottomSheetIntent);
                break;
        }
    }

    private Intent getShareIntent() {
        return ShareCompat.IntentBuilder.from(this)
                .setText("Hello from android-bottom-sheet")
                .setType("text/plain").getIntent();
    }

    private void openBottomSheet(Class<? extends Activity> activity, int list, int title) {
        Intent bottomSheetIntent = new Intent(this, activity)
                .putExtra(BottomSheetExampleBaseActivity.EXTRA_LIST, list)
                .putExtra(BottomSheetExampleBaseActivity.EXTRA_TITLE, title);
        BottomSheetActivity.startActivty(this, bottomSheetIntent);
    }

    private class ExampleAdapter extends RecyclerView.Adapter<ExampleAdapter.ViewHolder> {

        private final String[] ITEMS = new String[]{
                "Light - List",
                "Light - List - No Title",
                "Light - Grid",
                "Dark - Grid",
                "Chooser - Share",
                "Chooser - Share - Custom"
        };

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_item_example, parent, false));
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, final int position) {
            holder.text.setText(ITEMS[position]);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openBottomSheet(position);
                }
            });
        }

        @Override
        public int getItemCount() {
            return ITEMS.length;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            private final TextView text;

            public ViewHolder(View itemView) {
                super(itemView);
                text = (TextView) itemView;
            }
        }
    }
}
