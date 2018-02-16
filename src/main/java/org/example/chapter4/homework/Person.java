package org.example.chapter4.homework;

public class Person
{
    public final int id;
    public final String name;
    public final int age;
    public final Gender gender;

    public Person(int id, String name, int age, Gender gender)
    {
        this.id = id;
        this.name = name;
        this.age = age;
        this.gender = gender;
    }

    @Override
    public String toString()
    {
        return "Person{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", age=" + age +
                ", gender=" + gender +
                '}';
    }
}
