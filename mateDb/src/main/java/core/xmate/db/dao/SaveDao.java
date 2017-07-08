package core.xmate.db.dao;

import core.xmate.db.DbException;
import core.xmate.db.DbManager;
import core.xmate.db.IDao;

/**
 * @author DrkCore
 * @since 2017-05-20
 */
public class SaveDao implements IDao<Boolean> {

    public static final int MODE_NONE = 0;
    public static final int MODE_SAVE_OR_UPDATE = 1;
    public static final int MODE_BIND_ID = 2;

    private final Object data;
    private final int mode;

    public SaveDao(Object data) {
        this(data, MODE_NONE);
    }

    public SaveDao(Object data, int mode) {
        this.data = data;
        this.mode = mode;
    }

    @Override
    public Boolean access(DbManager db) throws DbException {
        switch (mode) {
            case MODE_SAVE_OR_UPDATE:
                db.saveOrUpdate(data);
                return true;

            case MODE_BIND_ID:
                return db.saveBindingId(data);

            default:
                db.save(data);
                return true;
        }
    }
}
