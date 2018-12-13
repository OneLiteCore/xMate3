package core.xmate.demo.db.person;

import core.xmate.db.annotation.Column;
import core.xmate.db.annotation.Table;

/**
 * @author DrkCore
 * @since 2017-05-20
 */
@Table(name = "Person")
public class Person {

    @Column(name = "id", isId = true)
    private int id;
    @Column(name = "name", hook = TestHook.class)
    private String name;
    @Column(name = "age")
    private int age;

    public Person() {
    }

    public Person(int id, String name, int age) {
        this.id = id;
        this.name = name;
        this.age = age;
    }

    public int getId() {
        return id;
    }

    public Person setId(int id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public Person setName(String name) {
        this.name = name;
        return this;
    }

    public int getAge() {
        return age;
    }

    public Person setAge(int age) {
        this.age = age;
        return this;
    }

    @Override
    public String toString() {
        return "Person{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", age=" + age +
                '}';
    }
}
