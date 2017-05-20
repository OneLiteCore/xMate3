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

    public interface IConfigurable<T> {

        void onConfigure(Selector<T> selector);

    }

    private final Class<T> type;
    private final IConfigurable<T> creator;

    public FindDao(Class<T> type) {
        this(type, null);
    }

    public FindDao(Class<T> type, IConfigurable<T> creator) {
        this.type = type;
        this.creator = creator;
    }

    @Override
    public List<T> access(DbManager db) throws DbException {
        Selector<T> selector = db.selector(type);
        if (creator != null) {
            creator.onConfigure(selector);
        }
        return selector.findAll();
    }
}
