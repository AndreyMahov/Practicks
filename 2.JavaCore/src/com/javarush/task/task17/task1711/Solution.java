package com.javarush.task.task17.task1711;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.javarush.task.task17.task1711.Person.createMale;
import static com.javarush.task.task17.task1711.Person.createFemale;
import  com.javarush.task.task17.task1711.Sex;

/* 
CRUD 2
*/

public class Solution {
    public static volatile List<Person> allPeople = new ArrayList<Person>();

    static {
        allPeople.add(createMale("Иванов Иван", new Date()));  //сегодня родился    id=0
        allPeople.add(createMale("Петров Петр", new Date()));  //сегодня родился    id=1
    }

    public static void main(String[] args) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy",Locale.ENGLISH);
        SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("dd-MMM-yyyy",Locale.ENGLISH);

        Date db ;
        Person person;

        String name;

        if (args[0] == null){
            throw new RuntimeException();
        }

        switch (args[0]){
            case "-c":
               synchronized (allPeople) {
                   for (int i = 1; i < args.length; i++) {
                       name = args[i];
                       ++i;
                       if (args[i].equals("м")) {
                           ++i;
                           db = simpleDateFormat.parse(args[i]);
                           person = Person.createMale(name, db);
                           allPeople.add(person);
                       } else {
                           ++i;
                           db = simpleDateFormat.parse(args[i]);
                           person = Person.createFemale(name, db);
                           allPeople.add(person);
                       }
                       System.out.println(allPeople.indexOf(person));
                   }
               }
                break;
            case "-u":
                synchronized (allPeople) {
                    for (int i = 1; i < args.length; i++) {
                        int index = Integer.parseInt(args[i]);
                        person = allPeople.get(Integer.parseInt(args[i]));
                        ++i;
                        person.setName(args[i]);
                        ++i;
                        if (args[i].equals("м")) {
                            person.setSex(Sex.MALE);
                        } else {
                            person.setSex(Sex.FEMALE);
                        }
                        ++i;
                        db = simpleDateFormat.parse(args[i]);
                        person.setBirthDate(db);
                        allPeople.set(index, person);

                    }

                }
                break;
            case "-d":
                synchronized (allPeople) {
                    for (int i = 1; i < args.length; i++) {
                        int index = Integer.parseInt(args[i]);
                        person = allPeople.get(index);
                        person.setBirthDate(null);
                        person.setSex(null);
                        person.setName(null);
                        allPeople.set(index, person);
                    }

                }
                break;
            case "-i":
                synchronized (allPeople) {
                    for (int i = 1; i < args.length; i++) {
                        person = allPeople.get(Integer.parseInt(args[i]));
                        name = person.getName();
                        String sex;
                        Sex sexEnum = person.getSex();
                        if (sexEnum.equals(Sex.MALE)) {
                            sex = "м";
                        } else sex = "ж";


                        System.out.println(name + " " + sex + " " + simpleDateFormat1.format(person.getBirthDate()));
                    }
                }

            break;
        }
    }
}
