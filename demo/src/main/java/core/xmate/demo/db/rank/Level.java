package core.xmate.demo.db.rank;

import core.xmate.db.annotation.Column;
import core.xmate.db.annotation.Table;

/**
 * @author core
 * @since 2017-07-27
 */
@Table(name = "Level")
public class Level {

    @Column(name = "id", isId = true)
    private int id;
    @Column(name = "score")
    private int score;

}
