package ru.yandex.javacourse.schedule.tasks.util;

public class GeneratorId {

    private int generatorId = 1;

    public int getNewId() {
        return generatorId++;
    }
}
