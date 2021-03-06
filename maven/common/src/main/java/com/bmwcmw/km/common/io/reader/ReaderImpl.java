package com.bmwcmw.km.common.io.reader;

import java.io.IOException;
import java.text.ParseException;


/**
 * <p><i>Data reader interface</i></p>
 * 
 * @author CMWT420
 *
 */
public interface ReaderImpl {
	/**
	 * Reads triples from a file, line by line, 
	 * then parse the current line into a triple object(S,P,O)
	 * @throws IOException
	 * @throws ParseException
	 */
    public Object next() throws IOException, ParseException;
    
	/**
	 * Goes back to the first line of current file
	 */	
    public void reset();
}
