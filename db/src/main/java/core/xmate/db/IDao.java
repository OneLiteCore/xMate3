package core.xmate.db;

public interface IDao<Result> {

    Result access(DbManager db) throws DbException;

}
