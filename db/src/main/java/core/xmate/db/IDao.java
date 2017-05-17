package core.xmate.db;

/**
 * 数据库访问对象。
 *
 * 当你定义实现该接口的时候建议仅实现{@link #access(DbManager, Object)}方法，
 * 而不要为其引入成员变量或者带参构造方法，保持dao无状态。
 *
 * @author DrkCore
 * @since 2017-05-17
 */
public interface IDao<Params, Result> {

    Result access(DbManager db, Params params) throws DbException;

}
