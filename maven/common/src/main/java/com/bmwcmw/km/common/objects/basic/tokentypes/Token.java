package com.bmwcmw.km.common.objects.basic.tokentypes;



/**
 * <p><i>abstract</i></p>
 * <p>A token can be (N3) : </p>
 * <p>IRI / Literal / Variable / Anonymous</p>
 * 
 * @author CMWT420
 *
 */
public abstract class Token {
	
	public abstract boolean isIRI();
	public abstract boolean isAnonymous();
	public abstract boolean isLitteral();
	public abstract boolean isVariable();
	
	public abstract IRI getIRI();
	public abstract Litteral getLitteral();
	public abstract Variable getVariable();
	public abstract Anonymous getAnonymous();
	
	public abstract String toString();
	
	/**
	 * <p>Parses a <i>String</i> token and distinguish its identity</p>
	 *
	 */
	public static Token parse(String token){
		if(token.startsWith("?")){
			return new Variable(token.substring(1));
		}
		else if((token.startsWith("_") && token.contains(":")) || token.compareTo("<>")==0){//token.compareTo("<>")==0
			return new Anonymous(token);
		}
		else if((token.startsWith("<") && token.endsWith(">")) || token.contains(":")){
			return new IRI(token);
		}
		else {
			return new Litteral(token);
		}
	}
}
