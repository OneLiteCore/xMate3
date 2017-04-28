package core.xmate.db.dao;

import core.xmate.db.DbManager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import core.xmate.db.AbsDao;

/**
 * @author DrkCore
 * @since 2016年1月3日21:22:44
 */
public abstract class AbsSaveDao<Table> extends AbsDao<List<Table>> {

	/* 继承 */

    @Override
    public final List<Table> access(DbManager db) throws Exception {
        if (addList != null && !addList.isEmpty()) {
            if (binding != null && binding) {
                db.saveBindingId(addList);
            } else {
                db.save(addList);
            }
            return addList;
        }
        return null;
    }

    @Override
    public void clear() {
        super.clear();
        binding = false;
        if (addList != null) {
            addList.clear();
        }
    }

	/* binding */

    private Boolean binding;

    public final AbsSaveDao<Table> setBinding(boolean binding) {
        this.binding = binding;
        return this;
    }

	/* save */

    private List<Table> addList;

    public final AbsSaveDao<Table> add(Collection<Table> items) {
        if (addList == null) {
            addList = new ArrayList<>(items.size());
        }
        addList.addAll(items);
        return this;
    }

    @SafeVarargs
    public final AbsSaveDao<Table> add(@SuppressWarnings("unchecked") Table... items) {
        if (addList == null) {
            addList = new ArrayList<>(items.length);
        }
        Collections.addAll(addList, items);
        return this;
    }

}
