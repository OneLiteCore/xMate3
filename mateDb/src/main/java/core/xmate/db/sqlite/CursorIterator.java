package core.xmate.db.sqlite;

import android.database.Cursor;
import android.support.annotation.Nullable;

import java.io.Closeable;

import core.xmate.db.CursorUtils;
import core.xmate.db.table.TableEntity;
import core.xmate.util.IOUtil;
import core.xmate.util.LogUtil;

/**
 * @author DrkCore
 * @since 5/31/18
 */
public class CursorIterator<T> implements Closeable {

    public static final String TAG = "CursorIterator";

    public static final CursorIterator EMPTY_INSTANCE = new CursorIterator();

    private final TableEntity<T> table;
    private Cursor cursor;

    private final T entity;

    private CursorIterator() {
        this.table = null;
        this.cursor = null;
        entity = null;
    }

    public CursorIterator(TableEntity<T> table, Cursor cursor) throws Throwable {
        this.table = table;
        this.cursor = cursor;

        entity = table.createEntity();
    }

    private T mapEntity() {
        try {
            CursorUtils.setEntity(table, cursor, entity);
            return entity;
        } catch (Throwable throwable) {
            LogUtil.e("Something went wrong while calling CursorUtils.setEntity", throwable);
            close();
        }
        return null;
    }

    @Override
    public void close() {
        if (cursor != null) {
            IOUtil.closeQuietly(cursor);
            cursor = null;
        }
    }

    public boolean isClosed() {
        return cursor == null || cursor.isClosed();
    }

    /*Delegate*/

    public int getCount() {
        return !isClosed() ? cursor.getCount() : 0;
    }

    public int getPosition() {
        return !isClosed() ? cursor.getPosition() : -1;
    }

    @Nullable
    public T moveToNext() {
        if (isClosed() || !cursor.moveToNext()) {
            return null;
        }
        return mapEntity();
    }

    @Nullable
    public T moveToPrevious() {
        if (isClosed() || !cursor.moveToPrevious()) {
            return null;
        }
        return mapEntity();
    }

    @Nullable
    public T moveToFirst() {
        if (isClosed() || !cursor.moveToFirst()) {
            return null;
        }
        return mapEntity();
    }

    @Nullable
    public T moveToLast() {
        if (isClosed() || !cursor.moveToLast()) {
            return null;
        }
        return mapEntity();
    }

    @Nullable
    public T moveToPosition(int position) {
        if (isClosed() || !cursor.moveToPosition(position)) {
            return null;
        }
        return mapEntity();
    }

}
