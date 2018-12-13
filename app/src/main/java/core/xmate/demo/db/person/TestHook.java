package core.xmate.demo.db.person;

import core.xmate.db.table.IValueTransformer;
import core.xmate.util.LogUtil;

/**
 * @author DrkCore
 * @since 12/12/18
 */
public class TestHook implements IValueTransformer {

    public static final String TAG = "TestHook_";

    @Override
    public String toVal(String raw) {
        LogUtil.d("Hook toVal executed, you can transform db data to mem value here, e.g. do some decryption");
        return raw;
    }

    @Override
    public String toRaw(String val) {
        LogUtil.d("Hook toRaw executed, you can can transform mem value to db data here, e.g. do some encryption");
        return val;
    }
}
