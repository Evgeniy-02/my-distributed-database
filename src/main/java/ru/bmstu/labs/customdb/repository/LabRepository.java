package ru.bmstu.labs.customdb.repository;

import ru.bmstu.labs.customdb.model.AbstractEntity;

import java.util.HashMap;

public class LabRepository<K, V extends AbstractEntity> {

    private HashMap<K, V> entities;

}
