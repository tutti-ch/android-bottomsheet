package ch.tutti.android.bottomsheet.example;

import android.content.Context;

import ch.tutti.android.bottomsheet.BottomSheetChooserActivity;
import ch.tutti.android.bottomsheet.ChooserIntentBuilder;

/**
 * Created by pboos on 28/01/15.
 */
public class BottomSheetCustomChooserActivity extends BottomSheetChooserActivity {

    public static ChooserIntentBuilder create(Context context) {
        return new ChooserIntentBuilder(context, BottomSheetCustomChooserActivity.class);
    }
}
