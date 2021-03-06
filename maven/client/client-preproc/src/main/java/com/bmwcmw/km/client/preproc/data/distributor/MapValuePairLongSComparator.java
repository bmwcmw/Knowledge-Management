	package com.bmwcmw.km.client.preproc.data.distributor;

import java.util.Comparator;
import java.util.Map;

import com.bmwcmw.km.common.objects.SOLongPair;

class MapValuePairLongSComparator implements Comparator<String> {

    Map<String, SOLongPair> base;
    public MapValuePairLongSComparator(Map<String, SOLongPair> base) {
        this.base = base;
    }

    // Note: this comparator imposes orderings that are inconsistent with equals.    
    @Override
    public int compare(String a, String b) {
        if (base.get(a).getSubject() >= base.get(b).getSubject()) {
            return -1;
        } else {
            return 1;
        } // returning 0 would merge keys
    }
}