package core.xmate.db.dao;

import java.util.List;

import core.xmate.db.DbException;
import core.xmate.db.DbManager;
import core.xmate.db.IDao;
import core.xmate.db.Selector;

/**
 * @author DrkCore
 * @since 2017-05-20
 */
public class FindDao<T> implements IDao<List<T>> {

    private final Class<T> type;

    public FindDao(Class<T> type) {
        this.type = type;
    }

    @Override
    public List<T> access(DbManager db) throws DbException {
        Selector<T> selector = db.selector(type);
        onSelectorCreated(selector);
        return selector.findAll();
    }

    public void onSelectorCreated(Selector<T> selector) {

    }
}
