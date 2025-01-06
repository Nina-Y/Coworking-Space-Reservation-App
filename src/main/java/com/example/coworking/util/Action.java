package com.example.coworking.util;

@FunctionalInterface
public interface Action<T> {

    void apply(T item);
}
