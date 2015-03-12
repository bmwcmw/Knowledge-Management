package com.bmwcmw.km.common.objects.basic;

import com.bmwcmw.km.common.objects.basic.tokentypes.Anonymous;
import com.bmwcmw.km.common.objects.basic.tokentypes.IRI;
import com.bmwcmw.km.common.objects.basic.tokentypes.Token;
import com.bmwcmw.km.common.objects.basic.tokentypes.Variable;



/**
 * <p>A subject of  Triple can be created : </p>
 * - by parsing a string
 * <br>
 * - from an IRI object
 * <br>
 * - from a variable object
 * <br>
 * - from an anonymous object
 * 
 * @author CMWT420
 *
 */
public class Subject {

	private Token _subject = null;
	
	/**
	 * Create a Subject by parsing a string
	 */
	public Subject(String subject){
		_subject = Token.parse(subject); //new IRI(subject);
	}
	
	/**
	 * Create an Subject using an IRI
	 */
	public Subject(IRI iri){
		_subject = iri;
	}
	
	/**
	 * Create an Subject using a variable
	 */
	public Subject(Variable variable){
		_subject = variable;
	}
	
	/**
	 * Create an Subject using an anonymous
	 */
	public Subject(Anonymous anonymous){
		_subject = anonymous;
	}
	
	public boolean isIRI() {
		return _subject.isIRI();
	}

	public boolean isAnonymous() {
		return _subject.isAnonymous();
	}

	public boolean isVariable() {
		return _subject.isVariable();
	}

	public IRI getIRI() {
		return _subject.getIRI();
	}

	public Variable getVariable() {
		return _subject.getVariable();
	}

	public Anonymous getAnonymous() {
		return _subject.getAnonymous();
	}

	public String toString(){
		return _subject.toString();
	}
}
