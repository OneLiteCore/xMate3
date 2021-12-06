/*
 * Copyright (c) 2013. wyouflf (wyouflf@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package core.xmate.db;

import android.database.Cursor;

import java.util.HashMap;

import core.xmate.db.table.ColumnEntity;
import core.xmate.db.table.DbModel;
import core.xmate.db.table.TableEntity;

public final class CursorUtils {

    public static <T> T getEntity(TableEntity<T> table, final Cursor cursor) throws Throwable {
        T entity = table.createEntity();
        setEntity(table, cursor, entity);
        return entity;
    }

    public static <T> void setEntity(TableEntity<T> table, final Cursor cursor, T entity) throws Throwable {
        setEntity(table, cursor, entity, false);
    }

    public static <T> void setEntity(TableEntity<T> table, final Cursor cursor, T entity, boolean resetIfNull) throws Throwable {
        HashMap<String, ColumnEntity> columnMap = table.getColumnMap();
        int columnCount = cursor.getColumnCount();
        for (int i = 0; i < columnCount; i++) {
            String columnName = cursor.getColumnName(i);
            ColumnEntity column = columnMap.get(columnName);
            if (column != null) {
                column.setValueFromCursor(entity, cursor, i, resetIfNull);
            }
        }
    }

    public static DbModel getDbModel(final Cursor cursor) {
        DbModel result = new DbModel();
        int columnCount = cursor.getColumnCount();
        for (int i = 0; i < columnCount; i++) {
            result.add(cursor.getColumnName(i), cursor.getString(i));
        }
        return result;
    }

    public static void setDbModel(final Cursor cursor, DbModel model) {
        int columnCount = cursor.getColumnCount();
        for (int i = 0; i < columnCount; i++) {
            model.add(cursor.getColumnName(i), cursor.getString(i));
        }
    }

}
