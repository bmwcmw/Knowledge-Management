package com.bmwcmw.km.common.objects;

import com.bmwcmw.km.common.objects.basic.Object;
import com.bmwcmw.km.common.objects.basic.Subject;

/**
 * Valid S,O pair for POS
 * 
 * @author CMWT420
 *
 */
public class SOPair {
	
	private Subject _subject;
	private Object _object;
	
	public SOPair(String subject, String object){
		_subject = new Subject(subject);
		_object = new Object(object);
	}

	
	public Subject getSubject() {
		return _subject;
	}

	public Object getObject() {
		return _object;
	}
}
