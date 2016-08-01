package core.base.app;

import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.annotation.StringRes;
import android.util.SparseArray;

public abstract class PrefFrag extends PreferenceFragment {

    protected final boolean equal(@StringRes int strRes, String prefKey) {
        return getString(strRes).equals(prefKey);
    }

    private SparseArray<Preference> prefs;

    protected final <T extends Preference> T findPreference(@StringRes int keyId) {
        Preference pref = prefs != null ? prefs.get(keyId) : null;
        if (pref == null) {
            pref = findPreference(getString(keyId));
            if (pref != null) {
                if (prefs == null) {
                    prefs = new SparseArray<>(1);
                }
                prefs.put(keyId, pref);
            }
        }
        return (T) pref;
    }

}
