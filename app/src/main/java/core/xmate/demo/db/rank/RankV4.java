package core.xmate.demo.db.rank;

import core.xmate.db.annotation.Column;
import core.xmate.db.annotation.Table;

/**
 * @author core
 * @since 2017-07-27
 */
@Table(name = RankV4.TABLE)
public class RankV4 {

    public static final int VER = 4;
    public static final String TABLE = "Rank_v" + VER;

    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_AGE = "age";
    public static final String COLUMN_SEX = "sex";
    public static final String COLUMN_MAJOR = "major";

    @Column(name = COLUMN_ID, isId = true)
    private int id;
    @Column(name = COLUMN_NAME)
    private String name;
    @Column(name = COLUMN_AGE)
    private int age;
    @Column(name = COLUMN_SEX)
    private boolean sex;
    @Column(name = COLUMN_MAJOR)
    private String major;

    public int getId() {
        return id;
    }

    public RankV4 setId(int id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public RankV4 setName(String name) {
        this.name = name;
        return this;
    }

    public int getAge() {
        return age;
    }

    public RankV4 setAge(int age) {
        this.age = age;
        return this;
    }

    public boolean isSex() {
        return sex;
    }

    public RankV4 setSex(boolean sex) {
        this.sex = sex;
        return this;
    }

    public String getMajor() {
        return major;
    }

    public RankV4 setMajor(String major) {
        this.major = major;
        return this;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("{");
        sb.append("\"id\":")
                .append(id);
        sb.append(",\"name\":\"")
                .append(name).append('\"');
        sb.append(",\"age\":")
                .append(age);
        sb.append(",\"sex\":")
                .append(sex);
        sb.append(",\"major\":\"")
                .append(major).append('\"');
        sb.append('}');
        return sb.toString();
    }
}
