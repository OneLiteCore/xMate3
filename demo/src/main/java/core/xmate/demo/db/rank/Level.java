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

    public int getId() {
        return id;
    }

    public Level setId(int id) {
        this.id = id;
        return this;
    }

    public int getScore() {
        return score;
    }

    public Level setScore(int score) {
        this.score = score;
        return this;
    }
}
