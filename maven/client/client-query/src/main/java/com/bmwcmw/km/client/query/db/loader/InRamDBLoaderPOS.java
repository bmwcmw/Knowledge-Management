package com.bmwcmw.km.client.query.db.loader;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import com.bmwcmw.km.client.query.db.utils.DBImpl;
import com.bmwcmw.km.client.query.db.utils.InRamDBUtilsPOS;
import com.bmwcmw.km.common.constants.AppConstants;
import com.bmwcmw.km.common.io.IOUtils;
import com.bmwcmw.km.common.io.reader.PairReader;
import com.bmwcmw.km.common.objects.SOStringPair;

/**
 * Only for the connection using POS files.
 * @author CMWX230
 *
 */
public class InRamDBLoaderPOS implements DBLoaderImpl {

	@Override
	public HashMap<String, DBImpl> getDBList(String path, MODE mode) throws Exception {
		HashMap<String, DBImpl> resultList = new HashMap<String, DBImpl>();
		File folder = new File(path);
		if(!folder.canRead() && !folder.isDirectory()) return null;
		ArrayList<File> listOfFiles = new ArrayList<File>(Arrays.asList(folder.listFiles()));
		switch(mode){
			case POS : 
				//ConcurrentHashMap Collections
				for(File f : listOfFiles){
					if(f.getName().startsWith(AppConstants.rdfTypeHeader)) {
						try {
							PairReader reader = new PairReader(f.getAbsolutePath());
							String str = null;
							DBImpl dbu = new InRamDBUtilsPOS();
							while ((str = reader.nextLine()) != null) {
								dbu.put(str, "");
							}
							resultList.put(f.getName(), dbu);
							IOUtils.logLog("File(T) " + f.getName() 
									+ " charged. Current size of table : " 
									+ dbu.fetchLoadedSize());
						} catch (IOException e) {
							e.printStackTrace();
						} catch (NumberFormatException e) {
							e.printStackTrace();
						} finally {
						}
					} else {
						try {
							PairReader reader = new PairReader(f.getAbsolutePath());
							SOStringPair pair = null;
							InRamDBUtilsPOS dbu = new InRamDBUtilsPOS();
							while ((pair = reader.nextStr()) != null) {
								dbu.put(pair.getSubject(), 
										pair.getObject());
							}
							resultList.put(f.getName(), dbu);
							IOUtils.logLog("File(NT) " + f.getName() 
									+ " charged. Current size of table : " 
									+ dbu.fetchLoadedSize());
						} catch (IOException e) {
							e.printStackTrace();
						} catch (NumberFormatException e) {
							e.printStackTrace();
						} finally {
						}
					}
				}
				break;
			case COMPRESSED : 
				break;
			default:
				break;
		}
		return resultList;
	}

}
