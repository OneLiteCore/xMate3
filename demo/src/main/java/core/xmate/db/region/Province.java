package core.xmate.db.region;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

/**
 * @author DrkCore
 * @since 2016-09-06
 */
@Table(name = "province")
public class Province {

    @Column(name = "id", isId = true)
    private int id;
    @Column(name = "name")
    private String name;

    public int getId() {
        return id;
    }

    public Province setId(int id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public Province setName(String name) {
        this.name = name;
        return this;
    }
}
