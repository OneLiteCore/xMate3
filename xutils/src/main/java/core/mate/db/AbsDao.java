package core.mate.db;

import android.support.annotation.NonNull;

import org.xutils.DbManager;

public abstract class AbsDao<Result> {

    public abstract Result access(@NonNull DbManager db) throws Exception;

}
