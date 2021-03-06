package com.bmwcmw.km.client.query.query.executor;

import java.util.HashMap;

import com.bmwcmw.km.client.query.db.utils.DBImpl;
import com.bmwcmw.km.client.query.query.objects.QueryPatternResult;
import com.bmwcmw.km.client.query.query.objects.StringTriple;

/**
 * This is the query executor which performs processed SPARQL query by asking 
 * different file systems.
 * <p>In this first strategical version, we use not only the cost of query pattern which begins the 
 * execution by processing firstly query patterns having least variable(s), but also the cost of 
 * variable and the cost of predicate(see our technical report).</p>
 * <p>Don't forget to setLocalPath(String) if a local file system is used. In this
 * case, the execute function accepts null destination information. Otherwise, 
 * the destination information cannot be null while launching the execution.</p>
 * @author CMWT420
 *
 */
public class StrategicalExecutor1 implements ExecutorImpl {

	@Override
	public void setMode(MODE toSet) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public MODE getMode() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public QueryPatternResult fetchFromDest(String dest, StringTriple pat)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setDBUList(HashMap<String, DBImpl> list) {
		// TODO Auto-generated method stub
		
	}

}
