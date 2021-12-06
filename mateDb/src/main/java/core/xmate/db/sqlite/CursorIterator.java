package core.xmate.db.sqlite;

import android.database.Cursor;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.Closeable;

import core.xmate.db.DbException;
import core.xmate.util.IOUtil;

/**
 * @author DrkCore
 * @since 5/31/18
 */
public abstract class CursorIterator<T> implements Closeable {

    public static final String TAG = "CursorIterator";

    private Cursor cursor;

    public CursorIterator(Cursor cursor) {
        this.cursor = cursor;
    }

    @Override
    public void close() {
        IOUtil.closeQuietly(cursor);
        cursor = null;
    }

    public boolean isClosed() {
        return cursor == null || cursor.isClosed();
    }

    /*Map*/

    protected abstract T createItem() throws Throwable;

    protected abstract void clearItem(T item);

    protected abstract void setItem(@NonNull Cursor cursor, @NonNull T cache) throws Throwable;

    private boolean cacheEnable = false;

    public boolean isCacheEnable() {
        return cacheEnable;
    }

    public CursorIterator<T> enableCache() {
        this.cacheEnable = true;
        return this;
    }

    private T cached;

    private T mapItem() throws DbException {
        try {
            T item;
            if (cacheEnable) {
                item = cached;
                if (item == null) {
                    item = createItem();
                    cached = item;
                } else {
                    clearItem(item);
                }
            } else {
                item = createItem();
            }

            if (item == null) {
                throw new DbException("Failed to create item with createItem() method");
            }
            setItem(cursor, item);
            return item;
        } catch (Throwable throwable) {
            if (throwable instanceof DbException) {
                DbException dbException = (DbException) throwable;
                throw dbException;
            }
            throw new DbException(throwable);
        }
    }

    /*Delegate*/

    public int getCount() {
        return !isClosed() ? cursor.getCount() : 0;
    }

    public int getPosition() {
        return !isClosed() ? cursor.getPosition() : -1;
    }

    @Nullable
    public T moveToNext() throws DbException {
        if (isClosed() || !cursor.moveToNext()) {
            return null;
        }
        return mapItem();
    }

    @Nullable
    public T moveToPrevious() throws DbException {
        if (isClosed() || !cursor.moveToPrevious()) {
            return null;
        }
        return mapItem();
    }

    @Nullable
    public T moveToFirst() throws DbException {
        if (isClosed() || !cursor.moveToFirst()) {
            return null;
        }
        return mapItem();
    }

    @Nullable
    public T moveToLast() throws DbException {
        if (isClosed() || !cursor.moveToLast()) {
            return null;
        }
        return mapItem();
    }

    @Nullable
    public T moveToPosition(int position) throws DbException {
        if (isClosed() || !cursor.moveToPosition(position)) {
            return null;
        }
        return mapItem();
    }

}
