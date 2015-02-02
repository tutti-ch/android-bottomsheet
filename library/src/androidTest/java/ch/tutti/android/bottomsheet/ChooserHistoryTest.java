package ch.tutti.android.bottomsheet;

import android.test.AndroidTestCase;

/**
 * Created by pboos on 28/01/15.
 */
public class ChooserHistoryTest extends AndroidTestCase {

    public void testDefaultToZero() {
        ChooserHistory history = new ChooserHistory();
        assertEquals(0, history.get("ch.tutti"));
    }

    public void testIncrement() {
        ChooserHistory history = new ChooserHistory();
        history.add("ch.tutti");
        assertEquals(1, history.get("ch.tutti"));
        history.add("ch.tutti");
        assertEquals(2, history.get("ch.tutti"));
    }

    public void testRestoreSingle() {
        ChooserHistory history = ChooserHistory.fromSettings("ch.tutti|1");
        assertEquals(1, history.get("ch.tutti"));

        history = ChooserHistory.fromSettings("ch.tutti|10");
        assertEquals(10, history.get("ch.tutti"));
    }

    public void testRestoreMultiple() {
        ChooserHistory history = ChooserHistory.fromSettings("ch.tutti|179#ch.tutti.qa2|45");
        assertEquals(179, history.get("ch.tutti"));
        assertEquals(45, history.get("ch.tutti.qa2"));
    }

    public void testRestoreFromEmptyString() {
        ChooserHistory history = ChooserHistory.fromSettings("");
        assertEquals(0, history.get("ch.tutti"));
    }

    public void testRestoreFromNullString() {
        String nullString = null;
        ChooserHistory history = ChooserHistory.fromSettings(nullString);
        assertEquals(0, history.get("ch.tutti"));
    }

    public void testSaveToPreferencesAndRestore() {
        ChooserHistory history = new ChooserHistory();
        history.add("ch.tutti");
        history.save(getContext());

        history = ChooserHistory.fromSettings(getContext());
        assertEquals(1, history.get("ch.tutti"));
        assertEquals(0, history.get("ch.tutti.qa2"));
    }
}
