package com.bmwcmw.km.client.preproc.data.reader;

import java.io.IOException;
import java.text.ParseException;


/**
 * <p><i>interface</i></p>
 * 
 * @author CMWT420
 *
 */
public interface RDFN3BasicReader {
	/**
	 * Reads triples from a file, line by line, then parse the current line into a <i>Triple</i> object(S,P,O)
	 * @throws IOException
	 * @throws ParseException
	 */
    public Object next() throws IOException, ParseException;
    
	/**
	 * Goes back to the first line of current file
	 */	
    public void reset();
}
