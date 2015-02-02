package ch.tutti.android.bottomsheet;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.DrawableRes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

/**
 * Created by pboos on 28/01/15.
 */
public class ChooserIntentBuilder {

    private final Intent mIntent;


    /**
     * Constructs a ChooserIntentBuilder for using the default BottomSheetChooserActivity.
     *
     * @param context the context
     */
    public ChooserIntentBuilder(Context context) {
        this(context, BottomSheetChooserActivity.class);
    }

    /**
     * Constructs a ChooserIntentBuilder for a specific BottomSheetChooserActivity.
     *
     * @param context       the context
     * @param activityClass class of the activity to be called as the chooser activity
     */
    public ChooserIntentBuilder(Context context,
            Class<? extends BottomSheetChooserActivity> activityClass) {
        mIntent = new Intent(context, activityClass);
    }

    /**
     * Sets the intent for which the chooser should be displayed. The chooser will search for all
     * activities that can be called with this intent.
     *
     * @param intent intent which should be resolved
     * @return the builder
     */
    public ChooserIntentBuilder forIntent(Intent intent) {
        mIntent.putExtra(BottomSheetChooserActivity.EXTRA_SHARE_INTENT, intent);
        return this;
    }

    /**
     * Sets the list of intents to be shown in the chooser..
     *
     * @param intents intents to be shown in the chooser
     * @return the builder
     */
    public ChooserIntentBuilder forIntents(Collection<Intent> intents) {
        mIntent.putExtra(BottomSheetChooserActivity.EXTRA_SHARE_INTENTS, new ArrayList<>(intents));
        return this;
    }

    /**
     * Sets the list of intents to be shown in the chooser.
     *
     * @param intents intents to be shown in the chooser
     * @return the builder
     */
    public ChooserIntentBuilder forIntents(Intent... intents) {
        mIntent.putExtra(BottomSheetChooserActivity.EXTRA_SHARE_INTENTS,
                new ArrayList<>(Arrays.asList(intents)));
        return this;
    }

    /**
     * Sets the title to be shown in the chooser.
     *
     * @param title the title to be shown in the chooser
     * @return the builder
     */
    public ChooserIntentBuilder title(CharSequence title) {
        mIntent.putExtra(BottomSheetChooserActivity.EXTRA_TITLE, title);
        return this;
    }

    /**
     * Sets the icon to be shown in the title of the chooser.
     *
     * @param icon the icon to be shown in the title of the chooser
     * @return the builder
     */
    public ChooserIntentBuilder icon(@DrawableRes int icon) {
        mIntent.putExtra(BottomSheetChooserActivity.EXTRA_ICON, icon);
        return this;
    }

    /**
     * Sets the items which should be listed first in the chooser. First item in the list will be
     * shown first. If history is enabled, history items will appear before the priority items.
     *
     * @param packageNames packageNames of the items which should be placed at the top
     * @return the builder
     */
    public ChooserIntentBuilder priority(Collection<String> packageNames) {
        ArrayList<String> packageNamesArrayList = packageNames instanceof ArrayList
                ? (ArrayList<String>) packageNames : new ArrayList<>(packageNames);
        mIntent.putExtra(BottomSheetChooserActivity.EXTRA_PRIORITY_PACKAGES, packageNamesArrayList);
        return this;
    }

    /**
     * Sets the items which should be listed first in the chooser. First item in the list will be
     * shown first. If history is enabled, history items will appear before the priority items.
     *
     * @param packageNames packageNames of the items which should be placed at the top
     * @return the builder
     */
    public ChooserIntentBuilder priority(String... packageNames) {
        mIntent.putExtra(BottomSheetChooserActivity.EXTRA_PRIORITY_PACKAGES,
                new ArrayList<>(Arrays.asList(packageNames)));
        return this;
    }


    /**
     * Enable/disable the history which will remember which items have been chosen before and
     * displaying them in front of all the others (even before priority items).
     *
     * @param enable true if history should be enabled, false to disable the history
     * @return the builder
     */
    public ChooserIntentBuilder history(boolean enable) {
        mIntent.putExtra(BottomSheetChooserActivity.EXTRA_HISTORY, enable);
        return this;
    }

    /**
     * Returns the constructed intent.
     *
     * @return constructed intent
     */
    public Intent getIntent() {
        return mIntent;
    }
}