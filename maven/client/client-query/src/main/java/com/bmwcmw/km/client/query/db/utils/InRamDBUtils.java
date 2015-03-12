package com.bmwcmw.km.client.query.db.utils;

import java.io.IOException;
import java.text.ParseException;

import com.bmwcmw.km.common.io.IOUtils;
import com.bmwcmw.km.common.io.reader.PairReader;
import com.bmwcmw.km.common.objects.SOStringPair;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

public class InRamDBUtils implements DBImpl{
	private BiMap<Long, String> nodes;
	
	public InRamDBUtils(){
		nodes = HashBiMap.create();
	}

	@Override
	public Long fetchIdByNode(String node){
		return nodes.inverse().get(node);
	}

	@Override
	public String fetchNodeById(Long index){
		return nodes.get(index);
	}
	
	@Override
	public void cleanAll(){
		nodes = HashBiMap.create();
	}

	@Override
	public void closeAll() {}

	@Override
	public void loadIndexFromFile(String path) {
		try {
			PairReader reader = new PairReader(path);
			SOStringPair pair = null;
			while ((pair = reader.nextStr()) != null) {
				nodes.put(Long.valueOf(pair.getSubject()), pair.getObject());
			}
		} catch (IOException e) {
			e.printStackTrace();
			cleanAll();
		} catch (ParseException e) {
			e.printStackTrace();
			cleanAll();
		} catch (NumberFormatException e) {
			e.printStackTrace();
			cleanAll();
		} finally {
		}
		IOUtils.logLog("File charged. Current size of key-value pair(s) : " + fetchLoadedSize());
	}
	
	public void put(Long k, String v){
		nodes.put(k, v);
	}

	@Override
	public Long fetchLoadedSize() {
		return (long)nodes.size();
	}

	@Override
	public void put(String k, String v) {
		// TODO Auto-generated method stub
		
	}

}
