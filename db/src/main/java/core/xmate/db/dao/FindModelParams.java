package core.xmate.db.dao;

import core.xmate.db.sqlite.SqlInfo;

/**
 * @author DrkCore
 * @since 2017-05-17
 */
public class FindModelParams<T> {

    final Class<T> type;
    final SqlInfo sqlInfo;

    public FindModelParams(Class<T> type, SqlInfo sqlInfo) {
        this.type = type;
        this.sqlInfo = sqlInfo;
    }
}
