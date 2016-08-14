package core.mate.db;

import core.mate.async.CoreTask;

public final class DaoTask<Result> extends CoreTask<DaoTask.Params<Result>, Void, Result> {

    public static class Params<Result> {

        public final CoreDb db;
        public final AbsDao<Result> dao;

        public Params(CoreDb db, AbsDao<Result> dao) {
            this.db = db;
            this.dao = dao;
        }
    }

    @Override
    public Result doInBack(Params<Result> params) throws Exception {
        return params.dao.access(params.db.getOrCreateDb());
    }
}
