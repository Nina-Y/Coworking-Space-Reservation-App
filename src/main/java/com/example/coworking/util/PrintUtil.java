package com.example.coworking.util;

import java.util.List;
import java.util.Map;

public class PrintUtil {

    public static <T> void printList(List<T> list) {
        if (list.isEmpty()) {
            System.out.println("No data to display at the moment.");
            return;
        }

        for (T element : list) {
            System.out.println(element);
        }
    }

    public static <K, V> void printMap(Map<K, V> map, String printFormat) {
        if (map.isEmpty()) {
            System.out.println("No data to display at the moment.");
            return;
        }

        for (Map.Entry<K, V> entry : map.entrySet()) {
            System.out.printf(printFormat, entry.getKey(), entry.getValue());
        }
    }
}
