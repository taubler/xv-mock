package com.taubler.vxmock.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class CollectionUtils {
    
    public static <K, V> Pair<K, V> $(K o1, V o2) {
        return new Pair<K, V>(o1, o2);
    }
    
    @SafeVarargs
    public static <K, V> Map<K, V> $m(Pair<K, V>... kv) {
        Map<K, V> map = new HashMap<>();
        for (Pair<K, V> pair : kv) {
            map.put(pair.x, pair.y);
        }
        return map;
    }
    
    public static interface MapEntry<K, V> {
    	MapEntry<K, V> add(K k, V v);
    }
    
    @SafeVarargs
    public static <E> List<E> $l(E... ee) {
        List<E> list = new ArrayList<>();
        for (E e : ee) {
            list.add(e);
        }
        return list;
    }
    
    @SafeVarargs
    public static <E> Set<E> $s(E... ee) {
        Set<E> set = new TreeSet<>();
        for (E e : ee) {
            set.add(e);
        }
        return set;
    }
    
    public static void main(String[] args) {
        Map<String, Integer> things = $m($("Hi", 1), $("There", 32), $("Bad", 33));
        for (String key : things.keySet()) {
            System.out.println(key + " = " + things.get(key));
        }
        
        List<String> strings = $l("Dedododo", "Dedadada", "Bye bye");
        for (String s : strings) {
            System.out.println(s);
        }
    }

}
