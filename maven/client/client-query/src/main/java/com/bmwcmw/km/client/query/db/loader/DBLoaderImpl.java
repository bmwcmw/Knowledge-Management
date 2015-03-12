package com.bmwcmw.km.client.query.db.loader;

import java.util.HashMap;

import com.bmwcmw.km.client.query.db.utils.DBImpl;

/**
 * DB loader interface
 * @author CMWX230
 *
 */
public interface DBLoaderImpl {
	
	public static enum MODE {
		COMPRESSED, POS
	};
	
	HashMap<String, DBImpl> getDBList(String path, MODE mode) throws Exception;

}
