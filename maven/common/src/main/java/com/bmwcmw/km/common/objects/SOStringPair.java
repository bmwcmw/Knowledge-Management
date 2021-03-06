package com.bmwcmw.km.common.objects;

/**
 * <p>Light version of RDF Triple</p>
 * <p>An RDF String pair contains two strings : Subject and Object</p>
 * 
 * @author CMWT420
 *
 */
public class SOStringPair {

	private String _subject;
	private String _object;
	
	/**
	 * <p>Create a String pair directly using three strings.</p>
	 */
	public SOStringPair(String subject, String object){
		_subject = subject;
		_object = object;
	}
	
	public String getSubject() {
		return _subject;
	}

	public String getObject() {
		return _object;
	}
	
	/**
	 * @return S and O separated by whitespace
	 */
	public String toString(){
		return _subject.toString() + " " + _object.toString();
	}
	
}
