package core.xmate.demo.app;

import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import java.util.Arrays;
import java.util.List;

import core.xmate.db.DbException;
import core.xmate.db.dao.DeleteAllDao;
import core.xmate.db.dao.FindAllDao;
import core.xmate.demo.R;
import core.xmate.demo.db.Person;
import core.xmate.demo.db.PersonDb;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.textView_main_testAdd).setOnClickListener(v -> {
            Person person = new Person();
            person.setName("小明").setAge(123);

            try {
                PersonDb.getInstance().get().save(person);
            } catch (DbException e) {
                e.printStackTrace();
            }
        });

        findViewById(R.id.button_main_testQuery).setOnClickListener(v -> {
            try {
                List<Person> persons = PersonDb.getInstance().accessSync(new FindAllDao<>(Person.class));
                String str = Arrays.toString(persons.toArray());
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Test Query")
                        .setMessage(str)
                        .setPositiveButton("ok", null)
                        .show();

            } catch (DbException e) {
                e.printStackTrace();
            }
        });


        findViewById(R.id.button_main_testDelete).setOnClickListener(v -> {

            try {
                PersonDb.getInstance().accessSync(new DeleteAllDao<>(Person.class));
            } catch (DbException e) {
                e.printStackTrace();
            }
        });
    }
}
