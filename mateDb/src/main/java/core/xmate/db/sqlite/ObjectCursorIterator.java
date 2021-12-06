package core.xmate.db.sqlite;

import android.database.Cursor;

import androidx.annotation.NonNull;

import core.xmate.db.CursorUtils;
import core.xmate.db.table.TableEntity;

/**
 * @author DrkCore
 * @since 5/31/18
 */
public class ObjectCursorIterator<T> extends CursorIterator<T> {

    public static final String TAG = "ObjectCursorIterator";

    public static final ObjectCursorIterator EMPTY_INSTANCE = new ObjectCursorIterator(null, null);

    private final TableEntity<T> table;

    public ObjectCursorIterator(Cursor cursor, TableEntity<T> table) {
        super(cursor);
        this.table = table;
    }

    @Override
    protected T createItem() throws Throwable {
        if (table == null) {
            throw new IllegalStateException("Failed to create entity with null table field");
        }
        return table.createEntity();
    }

    @Override
    protected void clearItem(T item) {
    }

    @Override
    protected void setItem(@NonNull Cursor cursor, @NonNull T cache) throws Throwable {
        if (table == null) {
            throw new IllegalStateException("Failed to set entity with null table field");
        }
        CursorUtils.setEntity(table, cursor, cache, isCacheEnable());
    }

}
