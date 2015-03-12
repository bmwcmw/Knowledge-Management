package com.bmwcmw.km.common.io.reader;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.util.StringTokenizer;

import com.bmwcmw.km.common.objects.SPOTriple;

/**
 * <p>
 * SPO triple reader
 * </p>
 * 
 * @author CMWT420
 * 
 */
public class TripleReader implements ReaderImpl {

	private String filePath = null;
	private BufferedReader reader = null;
	private int lineNumber = 0;

	private String _InvalidPath;
	private PrintWriter out = null;

	public String getInvalidPath() {
		return this._InvalidPath;
	}

	/**
	 * @param dataManager : normally this reader is called by a dataManager, in this 
	 * case, use <tt>this</tt>
	 * @param fileName : specify the filename of a NTriples file, normally with the 
	 * extension <tt>.n3</tt>
	 * @param invalidPath : specify invalid log's location
	 * @throws FileNotFoundException
	 */
	public TripleReader(String filePath, String invalidPath)
			throws IOException {
		this.filePath = filePath;
		reader = new BufferedReader(new FileReader(filePath));
		this._InvalidPath = invalidPath;
	}
	
	/**
	 * <p>
	 * Reads N3 triples from a file, line by line, then parse the current line
	 * into a <i>RDFTriple</i> object(S,P,O) or a <i>@prefix</i> definition
	 * </p>
	 * 
	 * <b>It's just here we convert valid lines into triples, and reject
	 * invalid lines.</b>
	 * 
	 * @throws IOException
	 * @throws ParseException
	 */
	public SPOTriple next() throws IOException, ParseException {
		String line;
		while ((line = reader.readLine()) != null) {
			++lineNumber;
			StringTokenizer itr = new StringTokenizer(line);
			// at least three tokens
			// One - subject
			if (itr.hasMoreTokens()) {
				String subj = itr.nextToken();
				if (!subj.startsWith("#")) {
					// Two - predicate
					if (!itr.hasMoreTokens()) {
						throw new ParseException(filePath, lineNumber);
					}
					String verb = itr.nextToken();
					// Three - object
					if (!itr.hasMoreTokens()) {
						throw new ParseException(filePath, lineNumber);
					}
					String object = itr.nextToken();

						/* if invalid node */
						if ((!isValidNode(subj) || !isValidNode(object))) {
							if (out != null) {
								out.close();
							}
							throw new ParseException("Invalid : "+filePath, lineNumber);
//							IOUtils.logLog("Thread "+ _dataManager.getThreadId()
//									+ " Invalid triple : " + line);
//							IOUtils.logLog("Thread "+ _dataManager.getThreadId()
//									+ " Writing in : " + _InvalidPath);
//							try {
//								_dataManager.invalidTripleWriter(_InvalidPath,filePath,line);
//								/* goes directly to next line */
//								continue;
//							} catch (IOException e) {
//								IOUtils.logLog("IO Error : " + line
//										+ " ==> " + _InvalidPath);
//								IOUtils.logLog(e.getMessage());
//								throw e;
//							} finally {
//								if (out != null) {
//									out.close();
//								}
//							}
						}
						return new SPOTriple(subj, verb, object);
					
				}
			}
		}
		return null;
	}

	/**
	 * 
	 * @param node : subject or object string to check
	 * @return boolean : true or false
	 */
	public boolean isValidNode(String node) {
		boolean result = true;
		if (node.compareTo("<>") == 0) {
			result = false;
		}
		return result;
	}

	/**
	 * Closes the BufferedReader while finished
	 * 
	 * @throws IOException
	 */
	public void close() throws IOException {
		reader.close();
	}

	@Override
	public void reset() {
		try {
			reader = new BufferedReader(new FileReader(filePath));
		} catch (FileNotFoundException e) {
		}
	}
}
