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
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import core.xmate.db.sqlite.CursorIterator;
import core.xmate.db.sqlite.WhereBuilder;
import core.xmate.db.table.DbModel;
import core.xmate.db.table.TableEntity;
import core.xmate.util.IOUtil;

/**
 * Author: wyouflf
 * Date: 13-8-10
 * Time: 下午2:15
 */
public final class DbModelSelector<T> {

    private String[] columnExpressions;
    private String groupByColumnName;
    private WhereBuilder having;

    private Selector<T> selector;

    private DbModelSelector(TableEntity<T> table) {
        selector = Selector.from(table);
    }

    protected DbModelSelector(Selector<T> selector, String groupByColumnName) {
        this.selector = selector;
        this.groupByColumnName = groupByColumnName;
    }

    protected DbModelSelector(Selector<T> selector, String[] columnExpressions) {
        this.selector = selector;
        this.columnExpressions = columnExpressions;
    }

    /*package*/
    static <T> DbModelSelector<T> from(TableEntity<T> table) {
        return new DbModelSelector<>(table);
    }

    public DbModelSelector<T> where(WhereBuilder whereBuilder) {
        selector.where(whereBuilder);
        return this;
    }

    public DbModelSelector<T> where(String columnName, String op, Object value) {
        selector.where(columnName, op, value);
        return this;
    }

    public DbModelSelector<T> and(String columnName, String op, Object value) {
        selector.and(columnName, op, value);
        return this;
    }

    public DbModelSelector<T> and(WhereBuilder where) {
        selector.and(where);
        return this;
    }

    public DbModelSelector<T> or(String columnName, String op, Object value) {
        selector.or(columnName, op, value);
        return this;
    }

    public DbModelSelector<T> or(WhereBuilder where) {
        selector.or(where);
        return this;
    }

    public DbModelSelector<T> expr(String expr) {
        selector.expr(expr);
        return this;
    }

    public DbModelSelector<T> groupBy(String columnName) {
        this.groupByColumnName = columnName;
        return this;
    }

    public DbModelSelector<T> having(WhereBuilder whereBuilder) {
        this.having = whereBuilder;
        return this;
    }

    public DbModelSelector<T> select(String... columnExpressions) {
        this.columnExpressions = columnExpressions;
        return this;
    }

    /**
     * 排序条件, 默认ASC
     */
    public DbModelSelector<T> orderBy(String columnName) {
        selector.orderBy(columnName);
        return this;
    }

    /**
     * 排序条件, 默认ASC
     */
    public DbModelSelector<T> orderBy(String columnName, boolean desc) {
        selector.orderBy(columnName, desc);
        return this;
    }

    public DbModelSelector<T> limit(int limit) {
        selector.limit(limit);
        return this;
    }

    public DbModelSelector<T> offset(int offset) {
        selector.offset(offset);
        return this;
    }

    public TableEntity<T> getTable() {
        return selector.getTable();
    }

    public DbModel findFirst() throws DbException {
        TableEntity<?> table = selector.getTable();
        if (!table.tableIsExists()) return null;

        this.limit(1);
        Cursor cursor = table.getDb().execQuery(this.toString());
        if (cursor != null) {
            try {
                if (cursor.moveToNext()) {
                    return CursorUtils.getDbModel(cursor);
                }
            } catch (Throwable e) {
                throw new DbException(e);
            } finally {
                IOUtil.closeQuietly(cursor);
            }
        }
        return null;
    }

    public List<DbModel> findAll() throws DbException {
        TableEntity<?> table = selector.getTable();
        if (!table.tableIsExists()) return null;

        List<DbModel> result = null;

        Cursor cursor = table.getDb().execQuery(this.toString());
        if (cursor != null) {
            try {
                result = new ArrayList<>();
                while (cursor.moveToNext()) {
                    DbModel entity = CursorUtils.getDbModel(cursor);
                    result.add(entity);
                }
            } catch (Throwable e) {
                throw new DbException(e);
            } finally {
                IOUtil.closeQuietly(cursor);
            }
        }
        return result;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append("SELECT ");
        if (columnExpressions != null && columnExpressions.length > 0) {
            for (String columnExpression : columnExpressions) {
                result.append(columnExpression);
                result.append(",");
            }
            result.deleteCharAt(result.length() - 1);
        } else {
            if (!TextUtils.isEmpty(groupByColumnName)) {
                result.append(groupByColumnName);
            } else {
                result.append("*");
            }
        }
        result.append(" FROM ").append("\"").append(selector.getTable().getName()).append("\"");
        WhereBuilder whereBuilder = selector.getWhereBuilder();
        if (whereBuilder != null && whereBuilder.getWhereItemSize() > 0) {
            result.append(" WHERE ").append(whereBuilder.toString());
        }
        if (!TextUtils.isEmpty(groupByColumnName)) {
            result.append(" GROUP BY ").append("\"").append(groupByColumnName).append("\"");
            if (having != null && having.getWhereItemSize() > 0) {
                result.append(" HAVING ").append(having.toString());
            }
        }
        List<Selector.OrderBy> orderByList = selector.getOrderByList();
        if (orderByList != null && orderByList.size() > 0) {
            result.append(" ORDER BY ");
            for (Selector.OrderBy orderBy : orderByList) {
                result.append(orderBy.toString()).append(',');
            }
            result.deleteCharAt(result.length() - 1);
        }
        if (selector.getLimit() > 0) {
            result.append(" LIMIT ").append(selector.getLimit());
            result.append(" OFFSET ").append(selector.getOffset());
        }
        return result.toString();
    }

    /*Query*/

    @Nullable
    public Cursor query() throws DbException {
        TableEntity<?> table = selector.getTable();
        if (!table.tableIsExists()) return null;
        return table.getDb().execQuery(this.toString());
    }

    @NonNull
    public CursorIterator<T> queryIterator() throws DbException {
        TableEntity<T> table = selector.getTable();
        Cursor cursor = query();
        if (cursor != null) {
            try {
                return new CursorIterator<>(table, cursor);
            } catch (Throwable e) {
                throw new DbException(e);
            }
        }
        //noinspection unchecked
        return (CursorIterator<T>) CursorIterator.EMPTY_INSTANCE;
    }

}
