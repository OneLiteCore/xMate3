package core.xmate.demo.db.person;

import android.content.Context;

import java.util.List;

import core.xmate.db.DbException;
import core.xmate.db.MateDb;

/**
 * @author DrkCore
 * @since 2017-05-20
 */
public class PersonDb extends MateDb {

    private static volatile PersonDb instance = null;

    public static PersonDb getInstance(Context context) {
        if (instance == null) {
            synchronized (PersonDb.class) {
                if (instance == null) {
                    instance = new PersonDb(context);
                }
            }
        }
        return instance;
    }

    private static final String DB_NAME = "test.db";
    private static final int DB_VERSION = 1;

    private PersonDb(Context context) {
        super(context, DB_NAME, DB_VERSION);
    }

//    private static final File DB_DIR = Environment.getExternalStorageDirectory();
//    private static final String DB_FILE_NAME = "out_file.db";
//
//    private PersonDb() {
//        super(DB_DIR, DB_FILE_NAME, 1);
//    }

    //CRUD

    public void save(Person person) {
        try {
            get().save(person);
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    public List<Person> findAll() {
        try {
            return get().findAll(Person.class);
        } catch (DbException e) {
            e.printStackTrace();
        }
        return null;
    }

}
