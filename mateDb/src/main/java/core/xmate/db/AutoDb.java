package core.xmate.db;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * @author core
 * @since 2017-07-27
 */
public abstract class AutoDb extends MateDb {

    /**
     * Class that implements this inter must be statistic, public and
     * has a zero arguments constructor, so that {@link AutoDb} could
     * instant it by reflection.
     */
    public interface IVersion {

        void onUpgrade(DbManager db) throws DbException;

    }

    private final List<Class<? extends IVersion>> versions;

    public AutoDb(String dbName, List<Class<? extends IVersion>> versions) {
        super(dbName, versions.size());
        this.versions = new ArrayList<>(versions);
    }

    public AutoDb(File inDir, String dbName, List<Class<? extends IVersion>> versions) {
        super(inDir, dbName, versions.size());
        this.versions = new ArrayList<>(versions);
    }

    @Override
    public final void onUpgrade(DbManager db, int oldVersion, int newVersion) {
        super.onUpgrade(db, oldVersion, newVersion);
        Class<? extends IVersion> verType = null;
        try {
            if (newVersion > oldVersion) {// Upgrade
                for (int i = oldVersion; i < newVersion; i++) {
                    verType = versions.get(i);
                    IVersion version = verType.newInstance();
                    version.onUpgrade(db);
                }

            } else if (newVersion < oldVersion) {// Downgrade
                throw new IllegalStateException("Downgrade not supported.");
            }

        } catch (Exception e) {
            String msg;
            if (verType != null) {
                msg = String.format(Locale.getDefault(),
                        "Failed to upgrade db %s %d -> %d fail, due to version: %s", getDbName(), oldVersion, newVersion, verType);
            } else {
                msg = e.getMessage();
            }
            db.dropDbQuietly();
            throw new IllegalStateException(msg, e);
        }
    }
}
