package core.xmate.db.region;

import android.support.annotation.NonNull;

import org.xutils.DbManager;

import java.util.List;

import core.mate.db.AbsDao;

/**
 * 用于查询的数据库访问对象
 *
 * @author DrkCore
 * @since 2016-09-06
 */
public class FindProvinceDao extends AbsDao<List<Province>> {

    @Override
    public List<Province> access(@NonNull DbManager db) throws Exception {
        //在这里定义对数据库的操作
        //如果逻辑比较复杂的话你可以将之分拆成几个dao并在
        //这里组合调用其access()方法整理出结果
        //对于外部的业务逻辑而言也只是一次异步任务而已

        //一个类只干一件事情，但是所谓的封装就是这么一回事
        return db.findAll(Province.class);
    }

}
