package core.xmate.demo.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import java.util.Arrays;
import java.util.List;

import core.xmate.db.DbException;
import core.xmate.demo.R;
import core.xmate.demo.db.person.PersonDb;
import core.xmate.demo.db.rank.RankDb;
import core.xmate.demo.db.person.Person;

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
                    PersonDb.getInstance(MainActivity.this).get().save(person);
                } catch (DbException e) {
                    e.printStackTrace();
                }
            }
        });

        findViewById(R.id.button_main_testQuery).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    List<Person> persons = PersonDb.getInstance(MainActivity.this).get().findAll(Person.class);
                    String str = "";
                    if (persons != null) {
                        str = Arrays.toString(persons.toArray());
                    }
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

        findViewById(R.id.textView_main_tableExist).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    boolean result = PersonDb.getInstance(MainActivity.this).get().isTableExists(Person.class);
                    Toast.makeText(MainActivity.this, "" + result, Toast.LENGTH_SHORT).show();
                } catch (DbException e) {
                    e.printStackTrace();
                }
            }
        });

        findViewById(R.id.button_main_testDelete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    PersonDb.getInstance(MainActivity.this).get().delete(Person.class);
                } catch (DbException e) {
                    e.printStackTrace();
                }
            }
        });

        findViewById(R.id.button_main_testUpdate).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RankDb.getInstance(MainActivity.this).get();
            }
        });
    }
}
