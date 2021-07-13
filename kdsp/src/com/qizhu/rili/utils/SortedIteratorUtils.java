package com.qizhu.rili.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

/**
 * 排序iterator 返回 list
 */
public class SortedIteratorUtils {

    //升序排列
    public static List<String> sortedIterator(Iterator it) {
        ArrayList<String> list = new ArrayList<String>();
        while (it.hasNext()) {
            list.add(it.next().toString());
        }

        Collections.sort(list, new Comparator<String>() {
            @Override
            public int compare(String key1, String key2) {
                return Integer.parseInt(key1) < Integer.parseInt(key2) ? -1 : 1;
            }
        });
        return list;
    }
}
