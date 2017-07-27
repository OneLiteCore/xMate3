package core.xmate.demo.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;

import java.util.Arrays;
import java.util.List;

import core.xmate.db.DbException;
import core.xmate.db.dao.DeleteAllDao;
import core.xmate.db.dao.FindDao;
import core.xmate.db.dao.SaveDao;
import core.xmate.demo.R;
import core.xmate.demo.db.Person;
import core.xmate.demo.db.PersonDb;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.textView_main_testAdd).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Person person = new Person();
                person.setName("小明").setAge(123);

                try {
                    PersonDb.getInstance().accessSync(new SaveDao(person));
                } catch (DbException e) {
                    e.printStackTrace();
                }
            }
        });

        findViewById(R.id.button_main_testQuery).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    List<Person> persons = PersonDb.getInstance().accessSync(new FindDao<>(Person.class));
                    String str = Arrays.toString(persons.toArray());
                    new AlertDialog.Builder(MainActivity.this)
                            .setTitle("Test Query")
                            .setMessage(str)
                            .setPositiveButton("ok", null)
                            .show();

                } catch (DbException e) {
                    e.printStackTrace();
                }
            }
        });


        findViewById(R.id.button_main_testDelete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    PersonDb.getInstance().accessSync(new DeleteAllDao<>(Person.class));
                } catch (DbException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
