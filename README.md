# DEPRECATED

This project is no longer under maintainment.

xMate3 is based on xUtils3. It bought us a lot of fun before but is way too old for nowadays Android development.

In the very beginning there aren't many frameworks supporting Android development, no ViewBinding, no ROOM, no OkHttp3 and no Glide. Then xUtils3 came, it provided all the stuff we need and save the day.

After many years passed, even a frameworks built with code will be old. All the modules xUtils3 do now have more and better choice to go. So maybe it is the time to put it to a good rest.

# xMate3

If you  are using  [xUtils3](https://github.com/wyouflf/xUtils3) you may know that it is a set of libs contains sqlite orm, bitmap loader, http requestor and view binder. But sometime you only need part of it and using some other lib like ButterKnife and Retrofit to deal with the rest. That is why I make this.

Maybe in the future I can seperate xUtils3 into 4 individual modules. For now, only sqlite orm part is available.

If you want to check some more documents about how to use it, please check the origin repository I forked from by click [here](https://github.com/wyouflf/xUtils3).

# Sqlite ORM

To setup sqlite orm part you need to add this in your project build.gradle:

```groovy
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```

then add this to your module build.gradle:

```groovy
dependencies {
        implementation 'com.github.OneLiteCore:xMate3:v2.4.7'
}
```

## Declare Annotation in your table class

```java
import core.xmate.db.annotation.Column;
import core.xmate.db.annotation.Table;
import core.xmate.util.LogUtil;

@Table(name = "Person")
public class Person {

    @Column(name = "id", isId = true)
    private int id;
    @Column(name = "name")
    private String name;
    @Column(name = "age")
    private int age;

    //There must be a public non-parameterize constructor method to let reflection create a new instance
    public Person() {}

    //Some set/get methods
}
```

## Extends MateDb and make it a singleton

```java
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
        // Enable log or not
        LogUtil.setDebug(true);
    }

//    private static final File DB_DIR = Environment.getExternalStorageDirectory();
//    private static final String DB_FILE_NAME = "out_file.db";
//
//    private PersonDb(Context context) {
//        super(context, DB_DIR, DB_FILE_NAME, 1);
//    }

    //CRUD
    //Use get() method to get DbManager for db operations

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

```

## Upgrade

Best practice for upgrading database is to extend AutoDb.

```java
import java.util.ArrayList;
import java.util.List;

import core.xmate.db.AutoDb;
import core.xmate.db.DbException;
import core.xmate.db.DbManager;

/**
 * @author DrkCore
 * @since 2017-05-20
 */
public class RankDb extends AutoDb {

    private static volatile RankDb instance = null;

    public static RankDb getInstance(Context context) {
        if (instance == null) {
            synchronized (RankDb.class) {
                if (instance == null) {
                    instance = new RankDb(context);
                }
            }
        }
        return instance;
    }

    private static final String DB_NAME = "rank.db";

    private RankDb(Context context) {
        super(context, DB_NAME, DB_VERSIONS, true);
    }

    private static final List<Class<? extends IVersion>> DB_VERSIONS = new ArrayList<>();

    static {
        DB_VERSIONS.add(VERSION_1.class);
        DB_VERSIONS.add(VERSION_2.class);
        DB_VERSIONS.add(VERSION_3.class);
        DB_VERSIONS.add(VERSION_4.class);
    }

    public static class VERSION_1 implements IVersion {
        @Override
        public void onUpgrade(DbManager db) throws DbException {
            db.createTableIfNotExist(Rank.class);

            Rank rank = new Rank();
            rank.setName("王小明");
            db.save(rank);
        }
    }

    public static class VERSION_2 implements IVersion {

        @Override
        public void onUpgrade(DbManager db) throws DbException {
            db.createTableIfNotExist(Level.class);
        }
    }

    public static class VERSION_3 implements IVersion {

        @Override
        public void onUpgrade(DbManager db) throws DbException {
            db.createTableIfNotExist(RankV2.class);

            List<Rank> ranks = db.findAll(Rank.class);
            int size = ranks != null ? ranks.size() : 0;
            List<RankV2> rankV2s = new ArrayList<>(size);
            for (int i = 0; i < size; i++) {
                Rank rank = ranks.get(i);
                RankV2 rankV2 = new RankV2();

                rankV2.setId(rank.getId());
                rankV2.setName(rank.getName());
                rankV2.setAge(123);

                rankV2s.add(rankV2);
            }
            db.save(rankV2s);
        }
    }

    public static class VERSION_4 implements IVersion {

        @Override
        public void onUpgrade(DbManager db) throws DbException {
            db.createTableIfNotExist(RankV3.class);

            List<RankV2> ranks = db.findAll(RankV2.class);
            int size = ranks != null ? ranks.size() : 0;
            List<RankV3> rankV3s = new ArrayList<>(size);
            for (int i = 0; i < size; i++) {
                RankV2 rankV2 = ranks.get(i);
                RankV3 rankV3 = new RankV3();

                rankV3.setId(rankV2.getId());
                rankV3.setName(rankV2.getName());
                rankV3.setAge(rankV2.getAge());
                rankV3.setSex(true);

                rankV3s.add(rankV3);
            }
            db.save(rankV3s);
        }
    }

}

```
