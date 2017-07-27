package core.xmate.db;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import core.xmate.util.LogUtil;

/**
 * @author core
 * @since 2017-07-27
 */
public abstract class AutoDb extends AbsDb {

    private static void ensureVersions(Class[] versions) {
        if (versions == null || versions.length == 0) {
            throw new IllegalArgumentException("versions must not be null or empty");

        } else {
            for (Class ver : versions) {
                if (!(IDao.class.isAssignableFrom(ver))) {
                    throw new IllegalArgumentException(ver + " must be impl of IDao");
                }
            }
        }
    }

    private final Class[] versions;

    public AutoDb(String dbName, Class[] versions) {
        super(dbName, versions.length);
        this.versions = versions.clone();
        ensureVersions(versions);
    }

    public AutoDb(File inDir, String dbName, Class[] versions) {
        super(inDir, dbName, versions.length);
        this.versions = versions.clone();
        ensureVersions(versions);
    }

    @Override
    public final void onUpgrade(DbManager db, int oldVersion, int newVersion) {
        super.onUpgrade(db, oldVersion, newVersion);
        try {
            for (int i = oldVersion; i < newVersion; i++) {
                IDao dao = (IDao) versions[i].newInstance();
                dao.access(db);
            }
        } catch (Exception e) {
            LogUtil.e("Upgrade db " + getDbName() + "fail", e);

        }
    }
}
