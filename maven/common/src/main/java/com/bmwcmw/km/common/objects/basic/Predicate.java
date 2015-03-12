package com.bmwcmw.km.common.objects.basic;

import com.bmwcmw.km.common.objects.basic.tokentypes.IRI;
import com.bmwcmw.km.common.objects.basic.tokentypes.Token;
import com.bmwcmw.km.common.objects.basic.tokentypes.Variable;



/**
 * <p>A predicate of  Triple can be created : </p>
 * - by parsing a string
 * <br>
 * - from an IRI object
 * <br>
 * - from a variable object
 * 
 * @author CMWT420
 *
 */
public class Predicate {
	
	private Token _predicate;
	
	/**
	 * Create a Predicate by parsing a string
	 */
	public Predicate(String predicate){
		_predicate = Token.parse(predicate); //new IRI(predicate);
	}
	
	/**
	 * Create an Predicate using an IRI
	 */
	public Predicate(IRI iri){
		_predicate = iri;
	}
	
	/**
	 * Create an Predicate using a variable
	 */
	public Predicate(Variable variable){
		_predicate = variable;
	}

	public boolean isIRI() {
		return _predicate.isIRI();
	}

	public boolean isVariable() {
		return _predicate.isVariable();
	}

	public IRI getIRI() {
		return _predicate.getIRI();
	}

	public Variable getVariable() {
		return _predicate.getVariable();
	}

	public String toString(){
		return _predicate.toString();
	}
}
