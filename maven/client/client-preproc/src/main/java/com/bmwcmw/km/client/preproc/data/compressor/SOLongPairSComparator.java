package com.bmwcmw.km.client.preproc.data.compressor;

import java.util.Comparator;

import com.bmwcmw.km.common.objects.SOLongPair;

public class SOLongPairSComparator implements Comparator<SOLongPair>{
	
	@Override
    public int compare(SOLongPair p1, SOLongPair p2){
        return p1.getSubject().compareTo(p2.getSubject());
    }
	
}