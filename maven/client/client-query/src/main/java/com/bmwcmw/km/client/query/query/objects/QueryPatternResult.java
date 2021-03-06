package com.bmwcmw.km.client.query.query.objects;

import java.util.ArrayList;

/**
 * Result set of a query pattern, with n lists where n is the number of 
 * variable(s) in the pattern.
 * @author CMWT420
 *
 */
public class QueryPatternResult {
	
	/* Pattern with type */
	private StringTriple pattern;
	
	private ArrayList<ArrayList<String>> resultSet;
	
	public QueryPatternResult(StringTriple pat, 
			ArrayList<ArrayList<String>> set){
		pattern = pat;
		resultSet = set;
	}
	
	public int nbVariable(){
		return resultSet.size();
	}
	
	public StringTriple getPattern(){
		return pattern;
	}
	
	public ArrayList<ArrayList<String>> getResultSet(){
		return resultSet;
	}

}
