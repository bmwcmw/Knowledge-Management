package com.bmwcmw.km.client.preproc.main;


import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

import com.bmwcmw.km.client.preproc.data.comparator.FilePair;
import com.bmwcmw.km.client.preproc.data.compressor.utils.DBImpl2;
import com.bmwcmw.km.client.preproc.data.distributor.light.DestInfo;
import com.bmwcmw.km.client.preproc.data.manager.DataManager;
import com.bmwcmw.km.common.io.IOUtils;

/**
 * Thread for different tasks.
 * @author CMWT420
 *
 */
public class ProcessingThread implements Runnable {
    private String threadId;
    private int taskId;
    private Thread t;
    private DataManager dm;
    private ArrayList<File> inputFiles;
    private String outputFolder;
    private String invalidPath;
    private String nsPath;
    private String comparePath = null;
    private DBImpl2 indexNodes = null;
    private LinkedList<FilePair> comparePairs;
    private HashMap<File, DestInfo> toSend;

    /**
     * For PS tasks
     * @param tid
     * @param task
     * @param inputList
     * @param outputPath
     * @param invalid
     * @param ns
     */
    public ProcessingThread(String tid, int task, ArrayList<File> inputList, String outputPath,
    		String invalid, String ns){
    	taskId = task;
		threadId = tid;
		dm = new DataManager(tid);
		inputFiles = inputList;
		outputFolder = outputPath;
		IOUtils.checkOrCreateFolder(outputFolder);
		invalidPath = invalid;
		if(invalid!=null)
			IOUtils.checkOrCreateFolder(invalidPath);
		nsPath = ns;
		if(ns!=null)
			IOUtils.checkOrCreateFolder(nsPath);
		System.out.println("Creating " + threadId);
    }
    
    /**
     * For Conversion, POS and Pre-Compare tasks
     * @param tid
     * @param task
     * @param inputList
     * @param outputPath
     */
    public ProcessingThread(String tid, int task, ArrayList<File> inputList, String outputPath){
    	taskId = task;
		threadId = tid;
		dm = new DataManager(tid);
		inputFiles = inputList;
		outputFolder = outputPath;
		IOUtils.checkOrCreateFolder(outputFolder);
		System.out.println("Creating " + threadId);
    }
    
    /**
     * For comparison tasks
     * @param tid
     * @param task
     * @param fp
     * @param outputPath
     */
    public ProcessingThread(String tid, int task, LinkedList<FilePair> fp, String outputPath){
    	taskId = task;
		threadId = tid;
		dm = new DataManager(tid);
		comparePairs = fp;
		outputFolder = outputPath;
		IOUtils.checkOrCreateFolder(outputFolder);
		System.out.println("Creating " + threadId);
    }
    
    /**
     * For compression tasks
     * @param tid
     * @param task
     * @param inputList
     * @param outputPath
     * @param dbu
     */
    public ProcessingThread(String tid, int task, ArrayList<File> inputList, String outputPath,
    		DBImpl2 dbu, String writecomparePath){
    	taskId = task;
		threadId = tid;
		dm = new DataManager(tid);
		inputFiles = inputList;
		outputFolder = outputPath;
		IOUtils.checkOrCreateFolder(outputFolder);
		indexNodes = dbu;
		comparePath = writecomparePath;
		System.out.println("Creating " + threadId);
    }
    
    /**
     * For distribution tasks
     * @param tid
     * @param inputList
     * @param connopt
     */
    public ProcessingThread(String tid, int task, HashMap<File,DestInfo> toSendList){
		threadId = tid;
		dm = new DataManager(tid);
		toSend = toSendList;
		System.out.println("Creating " + threadId);
    }

    @Override
    public void run() {
		long startTime, endTime, duration;
		startTime = System.currentTimeMillis();
		System.out.println("Running " + threadId);
		try {
			switch (taskId) {
				case AppConstants.APPCONVERTER:
					dm.convert(inputFiles, outputFolder);
					break;
				case AppConstants.APPREADERPS:
					dm.psSplit(inputFiles, outputFolder, nsPath, invalidPath);
					break;
				case AppConstants.APPREADERPOS:
					dm.posSplit(inputFiles, outputFolder);
					break;
				case AppConstants.APPCOMPRESS:
					dm.indexedCompress(inputFiles, outputFolder, indexNodes, comparePath);
					break;
				case AppConstants.APPCOMPARE_JAVA:
					dm.compareJava(comparePairs, outputFolder);
					break;
				case AppConstants.APPCOMPARE_GNU:
					dm.compareGnu(comparePairs, outputFolder);
					break;
				case AppConstants.APPDISTRIBUTE_LIGHT:
					dm.distribute(toSend, taskId);
					break;
				case AppConstants.APPDISTRIBUTE_HDFS:
					dm.distribute(toSend, taskId);
					break;
				default:
					break;
			}
			dm.closeAllWriters();
		} catch (Exception e) {
			IOUtils.logLog("Error in thread " + threadId + " : " + e.getMessage());
			e.printStackTrace();
		}
		endTime = System.currentTimeMillis();
		duration = endTime - startTime;
		System.out.println("Thread " + threadId + " exiting : Task " + taskId 
				+ " finished in " + String.valueOf(duration) + "ms");
    }

    /**
     * Starts thread
     */
    public void start() {
		System.out.println("Starting " + threadId);
		if (t == null) {
		    t = new Thread(this, threadId);
		    t.start();
		}
    }

}
