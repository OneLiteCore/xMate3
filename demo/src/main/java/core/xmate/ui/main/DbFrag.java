package core.xmate.ui.main;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.util.List;

import core.mate.adapter.SimpleAdapter;
import core.mate.adapter.SimpleViewHolder;
import core.mate.app.ProgressDlgFrag;
import core.mate.async.OnTaskListenerImpl;
import core.mate.db.dao.AbsFindDao;
import core.mate.util.ToastUtil;
import core.xmate.R;
import core.xmate.ui.base.BaseFrag;
import core.xmate.db.RegionDb;
import core.xmate.db.region.City;
import core.xmate.db.region.FindProvinceDao;
import core.xmate.db.region.Province;

/**
 * @author DrkCore
 * @since 2016-09-06
 */
@ContentView(R.layout.frag_db)
public class DbFrag extends BaseFrag {

    /*继承*/

    @ViewInject(R.id.listView_frag_db)
    private ListView listView;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //使用由CoreMate框架提供的万能Adapter一句话搞定适配器
        listView.setAdapter(new SimpleAdapter<Province>(android.R.layout.simple_list_item_1) {
            @Override
            protected void bindViewData(SimpleViewHolder<Province> holder, int position, Province data, int viewType) {
                TextView textView = holder.getCastView();
                textView.setText(data.getName());
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SimpleAdapter<Province> adapter = (SimpleAdapter<Province>) parent.getAdapter();
                Province province = adapter.getItem(position);
                showCitys(province);
            }
        });
    }

    @Event(R.id.button_frag_db_refresh)
    @Override
    public void refresh() {
        super.refresh();
        RegionDb regionDb = RegionDb.getInstance();

        //获取已经缓存的dao对象，或者用反射通过默认构造方法创建一个
        //FindProvinceDao dao = regionDb.getCachedDaoOrNewInstance(FindProvinceDao.class);

        //如果你不喜欢反射或者没有默认构造函数的话，可以使用下方的逻辑来获取缓存的dao
        FindProvinceDao dao = regionDb.getCachedDao(FindProvinceDao.class);
        if (dao == null) {
            dao = new FindProvinceDao();
        }

        //访问数据库
        regionDb.access(dao, new ProgressDlgFrag().setFragmentManager(this), new OnTaskListenerImpl<List<Province>>() {
            @Override
            public void onSuccess(List<Province> provinces) {
                //刷新数据
                SimpleAdapter<Province> adapter = (SimpleAdapter<Province>) listView.getAdapter();
                adapter.display(provinces);
            }
        });

    }

    private void showCitys(final Province province) {
        RegionDb regionDb = RegionDb.getInstance();

        //使用AbsFindDao查找数据
        //由于这里的dao是匿名的局部类会带有外部的引用，所以不会将之加入到缓存之中
        //将需要的类型写到泛型之中
        AbsFindDao<City> dao = new AbsFindDao<City>() {
        }.and("pid", "=", province.getId());

        List<City> cities = null;
        try {//同步访问数据库
            cities = regionDb.accessSync(dao);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //使用对话框显示
        int size = cities != null ? cities.size() : 0;
        String[] items = new String[size];
        for (int i = 0; i < size; i++) {
            items[i] = cities.get(i).getName();
        }
        new AlertDialog.Builder(getContext()).setItems(items, null).setTitle(province.getName()).show();

    }
}
