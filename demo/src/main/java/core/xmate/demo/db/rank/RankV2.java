package core.xmate.demo.db.rank;

import core.xmate.db.annotation.Column;
import core.xmate.db.annotation.Table;

/**
 * @author core
 * @since 2017-07-27
 */
@Table(name = "Rank_v2")
public class RankV2 {

    @Column(name = "id", isId = true)
    private int id;
    @Column(name = "name")
    private String name;
    @Column(name = "age")
    private int age;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public RankV2 setAge(int age) {
        this.age = age;
        return this;
    }
}
