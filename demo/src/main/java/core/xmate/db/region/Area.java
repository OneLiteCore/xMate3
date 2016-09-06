package core.xmate.db.region;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

/**
 * @author DrkCore
 * @since 2016-09-06
 */
@Table(name = "area")
public class Area {

    @Column(name = "id", isId = true)
    private int id;
    @Column(name = "pid")
    private int pid;
    @Column(name = "name")
    private String name;

    public int getId() {
        return id;
    }

    public Area setId(int id) {
        this.id = id;
        return this;
    }

    public int getPid() {
        return pid;
    }

    public Area setPid(int pid) {
        this.pid = pid;
        return this;
    }

    public String getName() {
        return name;
    }

    public Area setName(String name) {
        this.name = name;
        return this;
    }
}
