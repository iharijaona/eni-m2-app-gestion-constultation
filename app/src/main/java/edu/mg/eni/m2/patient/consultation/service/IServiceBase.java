package edu.mg.eni.m2.patient.consultation.service;

import java.util.ArrayList;

public interface IServiceBase<T,I> {
    boolean add(T entity);

    boolean deleteAll(I str);

    ArrayList<T> fetchAll();

    T fetchById(I str);

    boolean update(T entity);
}
