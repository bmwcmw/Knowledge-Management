package com.bmwcmw.km.client.preproc.data.distributor;

import java.util.Comparator;
import java.util.Map;

import com.bmwcmw.km.common.objects.SOLongPair;

class MapValuePairLongSOComparator implements Comparator<String> {

    Map<String, SOLongPair> base;
    public MapValuePairLongSOComparator(Map<String, SOLongPair> base) {
        this.base = base;
    }

    // Note: this comparator imposes orderings that are inconsistent with equals.    
    @Override
    public int compare(String a, String b) {
        if (base.get(a).getSubject() + base.get(a).getObject() 
        		>= base.get(b).getSubject() + base.get(b).getObject()) {
            return -1;
        } else {
            return 1;
        } // returning 0 would merge keys
    }
}