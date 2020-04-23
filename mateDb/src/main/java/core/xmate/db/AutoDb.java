package core.xmate.db;

import android.content.Context;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import core.xmate.util.LogUtil;

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

    public AutoDb(Context context, String dbName, List<Class<? extends IVersion>> versions) {
        super(context, dbName, versions.size());
        this.versions = new ArrayList<>(versions);
    }

    public AutoDb(Context context, File inDir, String dbName, List<Class<? extends IVersion>> versions) {
        super(context, inDir, dbName, versions.size());
        this.versions = new ArrayList<>(versions);
    }

    @Override
    public final void onUpgrade(DbManager db, int oldVersion, int newVersion) {
        super.onUpgrade(db, oldVersion, newVersion);
        if (newVersion > oldVersion) {// Upgrade
            Class<? extends IVersion> verClz = null;
            IVersion version = null;
            try {
                for (int i = oldVersion; i < newVersion; i++) {
                    verClz = versions.get(i);
                    version = verClz.newInstance();
                    LogUtil.d("Now execute version:" + version);
                    version.onUpgrade(db);
                }
            } catch (DbException e) {
                String msg;
                if (verClz != null) {
                    msg = String.format(Locale.getDefault(), "Failed to upgrade db %s %d -> %d fail, due to version: %s", getDbName(), oldVersion, newVersion, verClz.getCanonicalName());
                } else {
                    msg = e.getMessage();
                }

                LogUtil.e(msg, e);
                onUpgradeException(db, e, oldVersion, newVersion, version);
            } catch (IllegalAccessException e) {
                throw new IllegalStateException("Unable to instance version strategy", e);
            } catch (InstantiationException e) {
                throw new IllegalStateException("Unable to instance version strategy", e);
            }

        } else if (newVersion < oldVersion) {// Downgrade
            LogUtil.e(getDbName() + ": Downgrade not supported.");
        }
    }

    /**
     * Handle upgrade failure here.
     * <p>
     * Default behavior of this method is call {@link DbManager#dropDbQuietly()} to drop and clean up
     * legacy data and table struct.
     * <p>
     * Override this to implement your own logic to deal with upgrade failure.
     */
    protected void onUpgradeException(DbManager db, DbException exception, int oldVersion, int newVersion, IVersion errorVersion) {
        db.dropDbQuietly();
    }
}
