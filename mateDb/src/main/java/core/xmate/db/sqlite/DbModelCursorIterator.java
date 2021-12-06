package core.xmate.db.sqlite;

import android.database.Cursor;

import androidx.annotation.NonNull;

import core.xmate.db.CursorUtils;
import core.xmate.db.table.DbModel;

/**
 * @author DrkCore
 * @since 5/31/18
 */
public class DbModelCursorIterator extends CursorIterator<DbModel> {

    public static final String TAG = "DbModelCursorIterator";

    public static final DbModelCursorIterator EMPTY_INSTANCE = new DbModelCursorIterator(null);

    public DbModelCursorIterator(Cursor cursor) {
        super(cursor);
    }

    @Override
    protected DbModel createItem() {
        return new DbModel();
    }

    @Override
    protected void clearItem(DbModel item) {
        item.clear();
    }

    @Override
    protected void setItem(@NonNull Cursor cursor, @NonNull DbModel cache) {
        CursorUtils.setDbModel(cursor, cache);
    }

}
