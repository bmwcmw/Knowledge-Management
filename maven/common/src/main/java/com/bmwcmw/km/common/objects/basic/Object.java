package com.bmwcmw.km.common.objects.basic;

import com.bmwcmw.km.common.objects.basic.tokentypes.Anonymous;
import com.bmwcmw.km.common.objects.basic.tokentypes.IRI;
import com.bmwcmw.km.common.objects.basic.tokentypes.Litteral;
import com.bmwcmw.km.common.objects.basic.tokentypes.Token;
import com.bmwcmw.km.common.objects.basic.tokentypes.Variable;



/**
 * <p>An object of  Triple can be created : </p>
 * - by parsing a string
 * <br>
 * - from an IRI object
 * <br>
 * - from a variable object
 * <br>
 * - from a litteral object
 * <br>
 * - from an anonymous object
 * 
 * @author CMWT420
 *
 */
public class Object {

	private Token _object;
	
	/**
	 * Create a Object by parsing a string
	 */
	public Object(String object){
		_object = Token.parse(object);// new IRI(object);
	}
	
	/**
	 * Create an Object using an IRI
	 */
	public Object(IRI iri){
		_object = iri;
	}

	/**
	 * Create an Object using an anonymous
	 */
	public Object(Anonymous anonymous){
		_object = anonymous;
	}
	
	/**
	 * Create an Object using a litteral
	 */
	public Object(Litteral litteral){
		_object = litteral;
	}
	
	/**
	 * Create an Object using a variable
	 */
	public Object(Variable variable){
		_object = variable;
	}
	
	public boolean isIRI() {
		return _object.isIRI();
	}

	public boolean isAnonymous() {
		return _object.isAnonymous();
	}

	public boolean isLitteral() {
		return _object.isLitteral();
	}

	public boolean isVariable() {
		return _object.isVariable();
	}

	public IRI getIRI() {
		return _object.getIRI();
	}

	public Litteral getLitteral() {
		return _object.getLitteral();
	}

	public Variable getVariable() {
		return _object.getVariable();
	}

	public Anonymous getAnonymous() {
		return _object.getAnonymous();
	}

	public String toString(){
		return _object.toString();
	}
}
