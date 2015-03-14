package com.bmwcmw.km.client.query.launcher;

import java.io.File;
import java.util.HashMap;

import com.bmwcmw.km.client.query.db.loader.DBLoaderImpl.MODE;
import com.bmwcmw.km.client.query.db.loader.RamDBLoader;
import com.bmwcmw.km.client.query.db.utils.DBImpl;
import com.bmwcmw.km.client.query.query.executor.LocalBasicQueryExecutor;
import com.bmwcmw.km.client.query.query.objects.ParsedQuery;
import com.bmwcmw.km.client.query.query.objects.QueryResult;
import com.bmwcmw.km.client.query.query.planner.SimpleQueryPlanner;
import com.bmwcmw.km.common.io.IOUtils;

/**
 * Query launcher entry point
 * @author CMWT420
 *
 */
public class QueryLauncher {

	public static void main(String[] args) throws Exception {
		LocalBasicQueryExecutor exe = new LocalBasicQueryExecutor();
		exe.setLocalPath(System.getProperty("user.dir") 
				+ File.separator + ".." + File.separator + "PreProcessingClient2"
				+ File.separator + "_pos");
		System.out.println("TO LOAD : " + exe.getLocalPath());
		
		RamDBLoader loader = new RamDBLoader();
		HashMap<String, DBImpl> dbUnits = loader.getDBList(exe.getLocalPath(), MODE.POS);
		exe.setDBUList(dbUnits);
		
		String query = 
			"SELECT ?X, ?Y" +
			"WHERE" +
			"{"
			+ "?X a ?Y ." 
			+ "?Y rdf-type FullProfessor ." +
			"}";

		ParsedQuery planed = SimpleQueryPlanner.plan(query);
		QueryResult res = exe.execute(planed, null);
		
		IOUtils.logLog("");
//		res.outputToTerminal();
		
//		String query = 
//				"SELECT ?X, ?Y, ?Z"
//				+ "WHERE"
//				+ "{?X rdf:type ub:GraduateStudent ."
//				+ "  ?Y rdf:type ub:University ."
//				+ "  ?Z rdf:type ub:Department ."
//				+ "  ?X ub:memberOf ?Z ."
//				+ "  ?Z ub:subOrganizationOf ?Y ."
//				+ "  ?X ub:undergraduateDegreeFrom ?Y}";
		
//		String query = 
//				"SELECT ?X, ?Y, ?Z" +
//				"WHERE" +
//				"{?X likes ?Y ." +
//				"?Y hasAuthor ?Z ." +
//				"?X hasFriend Jill}";
//		
//		ParsedQuery planed = SimpleQueryPlanner.plan(query);
//		exe.execute(planed, null);
		
//		MatrixLineParser.parseMatrixLine("4:[1]1,1,1,2,2");
		
	}

}
