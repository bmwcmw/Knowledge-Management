package com.bmwcmw.km.client.preproc.main;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.bmwcmw.km.client.preproc.data.comparator.FilePair;
import com.bmwcmw.km.common.constants.IOConstants;
import com.bmwcmw.km.common.io.IOUtils;

/**
 * Preprocessor and predicate distribution calculator
 * 
 * @author CMWT420
 */
public class Application {

	// TODO possibility to add arguments while launching the program
	// TODO possibility to use configuration file
	private static BufferedReader in = new BufferedReader(
			new InputStreamReader(System.in));

	private static AppConfig myConfig;

	/**
	 * Main method of the client program
	 * 
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		System.out.println("---------------------------------------");
		System.out.println("|--------        APP V2       --------|");
		System.out.println("---------------------------------------");
		myConfig = AppConfig.getInstance();

		System.out.println("---------------------------------------");

		int result;
		int userCmd;
		String inStr;
		long startTime, endTime, duration;
		while (true) {
			System.out.println("---------------------------------------");
			System.out.println("Type number to execute : ");
			System.out.println("\tClean all existing processed data - "
					+ AppConstants.APPEMPTY);
			System.out.println("\tRDF to N3 converter - "
					+ AppConstants.APPCONVERTER);
			System.out.println("\tN3 Reader/Partitionner(PS) - "
					+ AppConstants.APPREADERPS);
			System.out.println("\tPredicate Reader/Splitter(POS) - "
					+ AppConstants.APPREADERPOS);
			System.out
					.println("\tCompressor for PS files (external script)  - "
							+ AppConstants.APPCOMPRESS);
			// System.out.println("\tPre-Comparator for PS files - "
			// + APPConstants.APPPRECOMPARE);
			System.out.println("\tComparator for S/O arrays - "
					+ AppConstants.APPCOMPARE);
			System.out.println("\tDistributor of compressed files - "
					+ AppConstants.APPDISTRIBUTE);
			System.out.println("\tExit - " + AppConstants.APPEXIT);
			System.out.println("#");
			inStr = in.readLine();
			try {
				userCmd = Integer.valueOf(inStr);
				if (userCmd == AppConstants.APPEXIT) {
					System.out.println("Exit...");
					break;
				} else {
					startTime = System.currentTimeMillis();
					result = Application.processAllFiles(userCmd);
					endTime = System.currentTimeMillis();
					duration = endTime - startTime;
					System.out
							.println("---------------------------------------");
					System.out.println("| " + userCmd + " code=" + result
							+ " finished in " + duration + " ms)");
					System.out
							.println("---------------------------------------");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	/**
	 * Begins the execution of the chosen task
	 * 
	 * @param programInd
	 * @return 0 if successfully finished or -1 if error
	 * @throws Exception
	 */
	public static int processAllFiles(int programInd) throws Exception {
		String invalidPath;
		String psPath;
		String nsPath;
		String posPath;
		String comparePath;
		String compressedPath;
		String indicatorPath;
		long startTime, endTime, duration;
		switch (programInd) {
		case AppConstants.APPEMPTY:
			IOUtils.logLog("\nCleaning all except rdf and n3 files");
			if (myConfig._ctlParams != null) {
				invalidPath = myConfig._ctlParams.get("invalidPath");
				IOUtils.deleteDirectory(new File(invalidPath));
				IOUtils.checkOrCreateFolder(invalidPath);
				psPath = myConfig._ctlParams.get("psPath");
				IOUtils.deleteDirectory(new File(psPath));
				IOUtils.checkOrCreateFolder(psPath);
				posPath = myConfig._ctlParams.get("posPath");
				IOUtils.deleteDirectory(new File(posPath));
				IOUtils.checkOrCreateFolder(posPath);
				nsPath = myConfig._ctlParams.get("nsPath");
				IOUtils.deleteDirectory(new File(nsPath));
				IOUtils.checkOrCreateFolder(nsPath);
				comparePath = myConfig._ctlParams.get("comparePath");
				IOUtils.deleteDirectory(new File(comparePath));
				IOUtils.checkOrCreateFolder(comparePath);
				compressedPath = myConfig._ctlParams.get("compressedPath");
				IOUtils.deleteDirectory(new File(compressedPath));
				IOUtils.checkOrCreateFolder(compressedPath);
				indicatorPath = myConfig._ctlParams.get("indicatorPath");
				IOUtils.deleteDirectory(new File(indicatorPath));
				IOUtils.checkOrCreateFolder(indicatorPath);
			}
			IOUtils.logLog("\nDONE. ");
			break;
		case AppConstants.APPCONVERTER:
			setNbThreads();
			startTime = System.currentTimeMillis();
			Application.convert(programInd);
			endTime = System.currentTimeMillis();
			duration = endTime - startTime;
			IOUtils.logLog("---------------------------------------");
			IOUtils.logLog("| Conversion finished (Time elapsed : " + duration
					+ " ms)");
			IOUtils.logLog("---------------------------------------"
					+ "\n---------------------------------------");
			break;
		case AppConstants.APPREADERPS:
			setNbThreads();
			startTime = System.currentTimeMillis();
			Application.ps(programInd);
			IOUtils.logLog(myConfig._nbThreads + " threads terminated");
			endTime = System.currentTimeMillis();
			duration = endTime - startTime;
			IOUtils.logLog("---------------------------------------");
			IOUtils.logLog("| PS finished (Time elapsed : " + duration + " ms)");
			IOUtils.logLog("---------------------------------------"
					+ "\n---------------------------------------");

			// Much more faster using SHELL command
			System.out
					.println("Do you want to merge each predicate into single file RIGHT NOW?");
			System.out
					.println("(Not necessary for compression/distribution, and use SHELL script "
							+ "if you want to perform this faster) (yN)");
			psPath = myConfig._ctlParams.get("psPath");
			IOUtils.checkOrCreateFolder(psPath);
			if (in.readLine().equals("y")) {

				startTime = System.currentTimeMillis();
				// Merge results of each thread
				IOUtils.logLog("Begin merging");
				final HashMap<String, ArrayList<File>> predicateFiles = new HashMap<String, ArrayList<File>>();
				Path start = FileSystems.getDefault().getPath(psPath);
				try {
					Files.walkFileTree(start, new SimpleFileVisitor<Path>() {
						@Override
						public FileVisitResult visitFile(Path file,
								BasicFileAttributes attrs) throws IOException {
							File f = file.toFile();
							if (f.isFile()) {
								String s = f.getName();
								if (predicateFiles.containsKey(s)) {
									predicateFiles.get(s).add(f);
								} else {
									ArrayList<File> temp = new ArrayList<File>();
									temp.add(f);
									predicateFiles.put(s, temp);
								}
							}
							return FileVisitResult.CONTINUE;
						}
					});
					String finalFileName;
					for (Entry<String, ArrayList<File>> pairs : predicateFiles
							.entrySet()) {
						finalFileName = psPath + File.separator
								+ pairs.getKey();
						IOUtils.logLog("Merging... " + pairs.getKey() + "("
								+ pairs.getValue().size() + ") into "
								+ finalFileName);
						IOUtils.mergeFiles(Arrays.copyOf(pairs.getValue()
								.toArray(), pairs.getValue().toArray().length,
								File[].class), new File(finalFileName));
					}

					endTime = System.currentTimeMillis();
					duration = endTime - startTime;
					IOUtils.logLog("---------------------------------------");
					IOUtils.logLog("| MERGE finished (Time elapsed : "
							+ duration / 1.5 + " ms)");
					IOUtils.logLog("---------------------------------------"
							+ "\n---------------------------------------");
				} catch (Exception e) {
					IOUtils.logLog(e.getMessage());
					return -1;
				}
			}
			break;
		case AppConstants.APPREADERPOS:
			setNbThreads();
			startTime = System.currentTimeMillis();
			Application.pos(programInd);
			IOUtils.logLog(myConfig._nbThreads + " threads terminated");
			endTime = System.currentTimeMillis();
			duration = endTime - startTime;
			IOUtils.logLog("---------------------------------------");
			IOUtils.logLog("| POS finished (Time elapsed : " + duration
					+ " ms)");
			IOUtils.logLog("---------------------------------------"
					+ "\n---------------------------------------");
			break;
		case AppConstants.APPCOMPRESS:
			setNbThreads();
			startTime = System.currentTimeMillis();
			Application.compress(programInd);
			IOUtils.logLog(myConfig._nbThreads + " threads terminated");
			endTime = System.currentTimeMillis();
			duration = endTime - startTime;
			IOUtils.logLog("---------------------------------------");
			IOUtils.logLog("| Compression finished (Time elapsed : " + duration
					+ " ms)");
			IOUtils.logLog("---------------------------------------"
					+ "\n---------------------------------------");
			break;
		// case APPConstants.APPPRECOMPARE:
		// setNbThreads();
		// startTime = System.currentTimeMillis();
		// APPServer.precompare();
		// IOUtils.logLog(myConfig._nbThreads+" threads terminated");
		// endTime = System.currentTimeMillis();
		// duration = endTime - startTime;
		// IOUtils.logLog("---------------------------------------");
		// IOUtils.logLog("| Files for comparison prepared (Time elapsed : "
		// + duration + " ms)");
		// IOUtils.logLog("---------------------------------------"
		// + "\n---------------------------------------");
		// break;
		case AppConstants.APPCOMPARE:
			setNbThreads();
			startTime = System.currentTimeMillis();
			Application.compare();
			IOUtils.logLog(myConfig._nbThreads + " threads terminated");
			endTime = System.currentTimeMillis();
			duration = endTime - startTime;
			IOUtils.logLog("---------------------------------------");
			IOUtils.logLog("| Comparison finished (Time elapsed : " + duration
					+ " ms)");
			IOUtils.logLog("---------------------------------------"
					+ "\n---------------------------------------");
			break;
		case AppConstants.APPDISTRIBUTE:
			setNbThreads();
			startTime = System.currentTimeMillis();
			Application.distribute(programInd);
			IOUtils.logLog(myConfig._nbThreads + " threads terminated");
			endTime = System.currentTimeMillis();
			duration = endTime - startTime;
			IOUtils.logLog("---------------------------------------");
			IOUtils.logLog("| File distribution finished (Time elapsed : "
					+ duration + " ms)");
			IOUtils.logLog("---------------------------------------"
					+ "\n---------------------------------------");
			break;
		default:
			return -1;
		}
		return 0;
	}

	/**
	 * Internal function for ?==>N3 conversion
	 * 
	 * @param programInd
	 * @return 0 if OK
	 */
	static int convert(int programInd) {
		String n3Path = myConfig._ctlParams.get("n3Path");
		IOUtils.checkOrCreateFolder(n3Path);
		String rdfPath = myConfig._ctlParams.get("rdfPath");
		IOUtils.logLog("\nConverting RDF/OWL/WML to N3 : ");
		IOUtils.logLog("Input : " + rdfPath);
		IOUtils.logLog("Output : " + n3Path);

		File folder = new File(rdfPath);
		if (!folder.canRead() && !folder.isDirectory())
			return -1;
		ArrayList<File> listOfFiles = new ArrayList<File>(Arrays.asList(folder
				.listFiles()));
		ArrayList<ArrayList<File>> inputLists = JobAssigner.assignJobs(
				listOfFiles, false);

		File temp;
		// Check all input files
		for (int i = 0; i < listOfFiles.size(); i++) {
			temp = listOfFiles.get(i);
			if (!temp.isFile() || !isRdfOwl(temp.getName())) {
				IOUtils.logLog("Input folder contains error : "
						+ temp.getName());
				return -1;
			}
		}

		// Create and execute threads with assigned sub task
		ExecutorService executor = Executors
				.newFixedThreadPool(myConfig._nbThreads);
		for (int i = 0; i < myConfig._nbThreads; i++) {
			Runnable thread = new ProcessingThread(String.valueOf(i), programInd,
					inputLists.get(i), n3Path + File.separator
							+ String.valueOf(i));
			executor.execute(thread);
		}
		executor.shutdown();
		while (!executor.isTerminated()) {
		}
		return 0;
	}

	/**
	 * Internal function for PS
	 * 
	 * @param programInd
	 * @return 0 if OK
	 */
	static int ps(int programInd) {
		String n3Path = myConfig._ctlParams.get("n3Path");
		IOUtils.checkOrCreateFolder(n3Path);
		String psPath = myConfig._ctlParams.get("psPath");
		IOUtils.checkOrCreateFolder(psPath);
		String nsPath = myConfig._ctlParams.get("nsPath");
		IOUtils.checkOrCreateFolder(nsPath);
		String invalidPath = myConfig._ctlParams.get("invalidPath");
		IOUtils.checkOrCreateFolder(invalidPath);
		IOUtils.logLog("\nPartitionning N3 : ");
		IOUtils.logLog("Input : " + n3Path);
		IOUtils.logLog("Output : " + psPath);
		IOUtils.logLog("Output : " + nsPath);
		IOUtils.logLog("Output : " + invalidPath);

		File folder = new File(n3Path);
		ArrayList<File> listOfFiles = new ArrayList<File>(Arrays.asList(folder
				.listFiles()));
		ArrayList<ArrayList<File>> inputLists = JobAssigner.assignJobs(
				listOfFiles, false);

		// Create and execute threads with assigned sub task
		ExecutorService executor = Executors
				.newFixedThreadPool(myConfig._nbThreads);
		for (int i = 0; i < myConfig._nbThreads; i++) {
			Runnable thread = new ProcessingThread(String.valueOf(i), programInd,
					inputLists.get(i), psPath + File.separator
							+ String.valueOf(i), invalidPath + File.separator
							+ String.valueOf(i), nsPath + File.separator
							+ String.valueOf(i));
			executor.execute(thread);
		}
		executor.shutdown();
		while (!executor.isTerminated()) {
		}
		return 0;
	}

	/**
	 * Internal function for POS
	 * 
	 * @param programInd
	 * @return 0 if OK
	 */
	static int pos(int programInd) {
		String psPath = myConfig._ctlParams.get("psPath");
		IOUtils.checkOrCreateFolder(psPath);
		String posPath = myConfig._ctlParams.get("posPath");
		IOUtils.checkOrCreateFolder(posPath);
		IOUtils.logLog("\nPartitionning N3 : ");
		IOUtils.logLog("Input : " + psPath);
		IOUtils.logLog("Output : " + posPath);

		File folder = new File(psPath);
		ArrayList<File> listOfFiles = new ArrayList<File>(Arrays.asList(folder
				.listFiles()));
		ArrayList<ArrayList<File>> inputLists = JobAssigner.assignJobs(
				listOfFiles, false);

		// Create and execute threads with assigned sub task
		ExecutorService executor = Executors
				.newFixedThreadPool(myConfig._nbThreads);
		for (int i = 0; i < myConfig._nbThreads; i++) {
			Runnable thread = new ProcessingThread(String.valueOf(i), programInd,
					inputLists.get(i), posPath);
			executor.execute(thread);
		}
		executor.shutdown();
		while (!executor.isTerminated()) {
		}
		return 0;
	}

	/**
	 * Internal function for compression
	 * 
	 * @param programInd
	 * @return 0 if OK
	 */
	static int compress(int programInd) {
		IOUtils.logLog("PLEASE USE EXTERNAL SOLUTION");
		// String psPath = myConfig._ctlParams.get("psPath");
		// IOUtils.checkOrCreateFolder(psPath);
		// String compressedPath = myConfig._ctlParams.get("compressedPath");
		// IOUtils.checkOrCreateFolder(compressedPath);
		// String comparePath = null;
		// if(myConfig._writeprecompare){
		// comparePath = myConfig._ctlParams.get("comparePath");
		// IOUtils.checkOrCreateFolder(comparePath);
		// }
		// IOUtils.logLog("\nCompressing PS files : ");
		// IOUtils.logLog("Input : " + psPath);
		// IOUtils.logLog("Output : " + compressedPath);
		// IOUtils.logLog("Writting sorted S/O files : " +
		// myConfig._writeprecompare);
		//
		// File folder = new File(psPath);
		// ArrayList<File> listOfFiles = new
		// ArrayList<File>(Arrays.asList(folder.listFiles()));
		// ArrayList<ArrayList<File>> inputLists =
		// APPJobAssigner.assignJobs(listOfFiles, false);
		//
		// //Create and execute threads with assigned sub task
		// ExecutorService executor =
		// Executors.newFixedThreadPool(myConfig._nbThreads);
		// try{
		// InRamDBUtils2 dbu = null;
		// switch(myConfig._compressMode){
		// case APPConstants.APPCOMPRESS_INRAM :
		// dbu = new InRamDBUtils2();
		// break;
		// default :
		// IOUtils.logLog("Compression mode error :" + myConfig._compressMode);
		// }
		// for (int i = 0; i < myConfig._nbThreads; i++) {
		// Runnable thread = new APPThread(String.valueOf(i), programInd,
		// inputLists.get(i),
		// compressedPath, dbu, comparePath);
		// executor.execute(thread);
		// }
		// executor.shutdown();
		// while (!executor.isTerminated()) {
		// }
		// return 0;
		// } catch (Exception e) {
		// IOUtils.logLog("Error : ");
		// IOUtils.logLog(e.getMessage());
		// return -1;
		// }
		return -1;
	}

	/**
	 * Internal function for pre-comparison
	 * 
	 * @param programInd
	 * @return 0 if OK
	 */
	// static int precompare(){
	// String psPath = myConfig._ctlParams.get("psPath");
	// IOUtils.checkOrCreateFolder(psPath);
	// String comparePath = myConfig._ctlParams.get("comparePath");
	// IOUtils.checkOrCreateFolder(comparePath);
	// IOUtils.logLog("\nProcessing PS files : ");
	// IOUtils.logLog("Input : " + psPath);
	// IOUtils.logLog("Output : " + comparePath);
	//
	// File folder = new File(psPath);
	// ArrayList<File> listOfFiles = new
	// ArrayList<File>(Arrays.asList(folder.listFiles()));
	// ArrayList<ArrayList<File>> inputLists =
	// APPJobAssigner.assignJobs(listOfFiles, false);
	//
	// //Create and execute threads with assigned sub task
	// ExecutorService executor =
	// Executors.newFixedThreadPool(myConfig._nbThreads);
	// for (int i = 0; i < myConfig._nbThreads; i++) {
	// Runnable thread = new APPThread(String.valueOf(i),
	// myConfig._precompareMode, inputLists.get(i),
	// comparePath);
	// executor.execute(thread);
	// }
	// executor.shutdown();
	// while (!executor.isTerminated()) {
	// }
	// return 0;
	// }

	/**
	 * Internal function for comparison
	 * 
	 * @param programInd
	 * @return 0 if OK
	 */
	static int compare() {
		String comparePath = myConfig._ctlParams.get("psPath");
		IOUtils.checkOrCreateFolder(comparePath);
		String indicatorPath = myConfig._ctlParams.get("indicatorPath");
		IOUtils.checkOrCreateFolder(indicatorPath);
		IOUtils.logLog("\nComparing PS files : ");
		IOUtils.logLog("Input : " + comparePath);
		IOUtils.logLog("Output : " + indicatorPath);

		File folder = new File(comparePath);
		File[] listOfFiles = folder.listFiles();

		// Check number of files
		HashMap<String, HashSet<String>> noRepNames = new HashMap<String, HashSet<String>>();
		File temp;
		String tempName;
		// Each prepared predicate should have two components : .S, .O
		for (int i = 0; i < listOfFiles.length; i++) {
			if (listOfFiles[i].isFile()) {
				temp = listOfFiles[i];
				if (!temp.getName().contains(IOConstants.SOSortedExt)
						&& !temp.getName().contains(IOConstants.OSSortedExt)) {
					IOUtils.logLog("Input folder contains error : "
							+ temp.getName() + " neither "
							+ IOConstants.SOSortedExt + " nor "
							+ IOConstants.OSSortedExt);
					return -1;
				}
				tempName = IOUtils.filenameWithoutExt(temp.getName());
				if (noRepNames.containsKey(tempName)) {
					noRepNames.get(tempName).add(temp.getName());
				} else {
					HashSet<String> newhs = new HashSet<String>();
					newhs.add(temp.getName());
					noRepNames.put(tempName, newhs);
				}
			}
		}
		for (String key : noRepNames.keySet()) {
			if (noRepNames.get(key).size() != 2) {
				IOUtils.logLog("Input folder contains error : " + key
						+ " has only " + noRepNames.get(key).size()
						+ " component(s)");
				return -1;
			}
		}
		IOUtils.logLog("Local prepared file checked");

		ArrayList<String> uniqueName = new ArrayList<String>();
		uniqueName.addAll(noRepNames.keySet());
		// sort in alphabetically order
		Collections.sort(uniqueName, new Comparator<String>() {
			@Override
			public int compare(String s1, String s2) {
				return s1.compareToIgnoreCase(s2);
			}
		});

		Integer nbComparison = (uniqueName.size()) * (uniqueName.size() - 1)
				/ 2;
		IOUtils.logLog("Assigning for " + nbComparison + " comparison(s)");

		IOUtils.logLog("Number of predicates " + uniqueName.size());
		LinkedList<FilePair> toComparePairs = new LinkedList<FilePair>();
		for (int i = 0; i < uniqueName.size(); i++) {
			System.out.println(i + " ==> " + uniqueName.get(i));
			for (int j = i + 1; j < uniqueName.size(); j++) {
				System.out.println("\t" + j + " : " + uniqueName.get(j));
				toComparePairs.add(new FilePair(new File(comparePath
						+ File.separator + uniqueName.get(i)
						+ IOConstants.SOSortedExt), new File(comparePath
						+ File.separator + uniqueName.get(i)
						+ IOConstants.OSSortedExt), new File(comparePath
						+ File.separator + uniqueName.get(j)
						+ IOConstants.SOSortedExt), new File(comparePath
						+ File.separator + uniqueName.get(j)
						+ IOConstants.OSSortedExt)));
			}
		}
		LinkedList<LinkedList<FilePair>> inputLists = JobAssigner
				.assignJobs(toComparePairs, false);
		// Create and execute threads with assigned sub task
		ExecutorService executor = Executors
				.newFixedThreadPool(myConfig._nbThreads);
		for (int i = 0; i < myConfig._nbThreads; i++) {
			Runnable thread = new ProcessingThread(String.valueOf(i),
					myConfig._compareMode, inputLists.get(i), indicatorPath);
			executor.execute(thread);
		}
		executor.shutdown();
		while (!executor.isTerminated()) {
		}

		return 0;
	}

	/**
	 * Internal function for distribution
	 * 
	 * @param programInd
	 * @return 0 if OK
	 * @throws Exception
	 */
	static int distribute(int programInd) throws Exception {
		// String indicatorPath = myConfig._ctlParams.get("indicatorPath");
		//
		// /* Check compressed files */
		// String compressedPath = myConfig._ctlParams.get("compressedPath");
		// File folder = new File(compressedPath);
		// File[] listOfFiles = folder.listFiles();
		//
		// //Check number of files
		// HashMap<String,HashSet<String>> noRepNames = new
		// HashMap<String,HashSet<String>>();
		// File temp;
		// String tempName;
		// //Each compressed predicate should have three components : .index,
		// .matrixSO, .matrixOS
		// for (int i = 0; i < listOfFiles.length; i++) {
		// if (listOfFiles[i].isFile()) {
		// temp = listOfFiles[i];
		// if(!temp.getName().contains(".index") &&
		// !temp.getName().contains(".matrixSO")
		// && !temp.getName().contains(".matrixOS")){
		// IOUtils.logLog("Input folder contains error : "
		// + temp.getName() + " neither .index nor .matrixSO nor .matrixOS");
		// return -1;
		// }
		// tempName = IOUtils.filenameWithoutExt(temp.getName());
		// if(noRepNames.containsKey(tempName)){
		// noRepNames.get(tempName).add(temp.getName());
		// } else {
		// HashSet<String> newhs = new HashSet<String>();
		// newhs.add(temp.getName());
		// noRepNames.put(tempName, newhs);
		// }
		// }
		// }
		// for(String key : noRepNames.keySet()){
		// if(noRepNames.get(key).size()!=3){
		// IOUtils.logLog("Input folder contains error : "
		// + key + " has only " + noRepNames.get(key).size() + " component(s)");
		// return -1;
		// }
		// }
		// IOUtils.logLog("Local compressed file checked");

		// Load if the indicator file exists(with check of file entries),
		// otherwise, use a random plan
		// ArrayList<ArrayList<File>> groups =
		// IndicatorGrouper.groupBySimilarities(indicatorPath
		// , compressedPath, noRepNames, false
		// , myConfig._indicatorMode, myConfig._nbThreads);

		// /* Contact DN and get the number of CNs with their available space */
		// String ipDN = "134.214.142.58";
		// int portDN = 7474;
		// ConnectDN directoryNode = new ConnectDN(ipDN, portDN);
		// directoryNode.sendMessage("HELLO");
		// String jsonRespStr = directoryNode.receiveMessage();
		// // String jsonRespStr =
		// "[{\"address\":\"192.168.0.1\",\"port\":\"8888\",\"free_space\":\"20480\",\"ratio\":\"0.5\"},"
		// // +
		// "{\"address\":\"192.168.0.2\",\"port\":\"8686\",\"free_space\":\"200000\",\"ratio\":\"0.3\"}]";
		// JSONParser parser = new JSONParser();
		// JSONArray jsonResp = (JSONArray) parser.parse(jsonRespStr);
		// int nbNodes = jsonResp.size();
		// IOUtils.logLog("Available compute nodes : " + nbNodes);
		// ArrayList<JSONObject> jsonRespArray = new ArrayList<JSONObject>();
		// IOUtils.logLog("Available compute nodes information : ");
		// for (Object o : jsonResp){
		// JSONObject newJO = (JSONObject) o;
		// jsonRespArray.add(newJO);
		// String address = (String) newJO.get("address");
		// //Use directory node's address if returned IP == LOCALHOST
		// if(address.equals("127.0.0.1")) {
		// address = ipDN;
		// }
		// Integer port = Integer.valueOf( (String) newJO.get("port") );
		// Integer free_space = Integer.valueOf( (String)
		// newJO.get("free_space") );
		// Float ratio = Float.valueOf( (String) newJO.get("ratio") );
		// IOUtils.logLog(address+":"+port+"|"+free_space+"Mb|"+ratio);
		// }
		// Distman.dist();
		//
		// //Create and execute threads with assigned sub task
		// ExecutorService executor =
		// Executors.newFixedThreadPool(APPServer._nbThreads);
		// for (int i = 0; i < APPServer._nbThreads; i++) {
		// Runnable thread = new APPThread(String.valueOf(i), _distributeMode,
		// new HashMap<File,DestInfo>());
		// executor.execute(thread);
		// }
		// executor.shutdown();
		// while (!executor.isTerminated()) {
		// }
		// //System.out.println(directoryNode.receiveMessage());
		return 0;
	}

	/**
	 * Check if a file has the extension rdf
	 * 
	 * @param fileName
	 * @return true if yes
	 */
	static boolean isRdfOwl(String fileName) {
		return fileName.endsWith(".rdf");
	}

	/**
	 * Check if a file has the extension n3
	 * 
	 * @param fileName
	 * @return true if yes
	 */
	static boolean isN3(String fileName) {
		return fileName.endsWith(".n3");
	}

	/**
	 * Set the number of thread workers
	 * 
	 * @throws IOException
	 */
	static void setNbThreads() throws IOException {
		try {
			System.out.println("How many threads (>0) ?");
			myConfig._nbThreads = Integer.valueOf(in.readLine());
		} catch (NumberFormatException e) {
			System.out.println("Input error, using 4 threads...");
			myConfig._nbThreads = 4;
		} finally {
			if (myConfig._nbThreads <= 0) {
				System.out.println("Input error, using 4 threads...");
				myConfig._nbThreads = 4;
			}
		}
	}

}


class ProgressBarRotating extends Thread {
	boolean showProgress = true;
	public void run() {
		String anim = "|/-\\";
		int x = 0;
		while (showProgress) {
			System.out.print("\r Processing "
					+ anim.charAt(x++ % anim.length()));
			try {
				Thread.sleep(100);
			} catch (Exception e) {
			}
		}
	}
}