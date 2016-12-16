package br.usp.each.saeg.devselector;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.generationjava.io.CsvReader;
import com.generationjava.io.CsvWriter;

public class TestAllocator {

	public List<String[]> wholeSample = new ArrayList<String[]>();
	public List<String[]> selectedSample = new ArrayList<String[]>();
	private final String DIR_PATH = "/Users/higor/webfiles/";
	private final String MATRIX_FILE = "sample-matrix.csv";
	private String alphaList[] = {"0.0","0.05","0.1","0.15","0.2","0.25","0.3","0.35","0.4","0.45","0.5","1.0"};
	private String weightList[] = {"1;1;1","2;1;1","3;1;1","5;1;1","10;1;1"};
	private String participantList[] = {"10","20","30","50","100"};
	private int interactions = 20;
	private final String ALPHA_FILE = DIR_PATH+"io/alpha.txt";
	private final String WEIGHT_FILE = DIR_PATH+"io/w.txt";
	private final String FREQ_MATRIX_FILE = DIR_PATH+"io/freq.csv";
	private final String ALLOC_RESULT_FILE = DIR_PATH+"alloc_result.csv";
	private final int runsPerAlpha = 300;
	private final int runsPerSample = 100;
	private double allocationResultMatrix[][] = new double[alphaList.length][runsPerAlpha];
	
	
	@Before
	public void setUp(){
		readMatrix();
		//selectRandomlyFromSampleMatrix(30);
		
	}
	
	public void readMatrix(){
		try {
			CsvReader reader = new CsvReader(new BufferedReader(new FileReader(new File(DIR_PATH+MATRIX_FILE))));
			reader.setFieldDelimiter(';');
			reader.setBlockDelimiter('\n');
			
			String line[];
			while((line = reader.readLine()) != null){
				wholeSample.add(line);
			}
			reader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}
	
	public void selectRandomlyFromSampleMatrix(int size){
		selectedSample.clear();
		if(wholeSample.size() >= size){
		
			Random rand = new Random();
			Set<Integer> indexList = new HashSet<Integer>();
			
			int count = size;
			while(count > 0){
				int newIndex = rand.nextInt(wholeSample.size());
				if(!indexList.contains(newIndex)){
					indexList.add(newIndex);
					count--;
				}
			}
			
			for(int index : indexList){
				selectedSample.add(wholeSample.get(index));
			}
			
			System.out.println(">>>>>>>>>>>>Printing index list");
			for(int index : indexList){
				System.out.println(index);
			}
			System.out.println(">>>>>>>>>>>>Printing selected sample");
			for(String line[] : selectedSample){
				System.out.println(line[0]+";"+line[1]+";"+line[2]+";"+line[3]+";"+line[4]+";"+line[5]+";"+line[6]+";"+line[7]+";"+line[8]+";"+line[9]+";"+line[10]);
			}
			
		}
	}
	
	//@Test
	public void testMatrixSize() {
		assertTrue(wholeSample.size() == 2000);
	}

	//@Test
	public void processSelectedMatrix() {
		Experience exp;
		
		for(String line[] : selectedSample){
			exp = new Experience();
			
			if(line[0].equals("1")){
				exp.setSelfEvaluationJava(1);
			}else if(line[1].equals("1")){
				exp.setSelfEvaluationJava(2);
			}else if(line[2].equals("1")){
				exp.setSelfEvaluationJava(3);
			}
			
			if(line[3].equals("1")){
				exp.setSelfEvaluationIDE(0);
			}else if(line[4].equals("1")){
				exp.setSelfEvaluationIDE(1);
			}else if(line[5].equals("1")){
				exp.setSelfEvaluationIDE(2);
			}else if(line[6].equals("1")){
				exp.setSelfEvaluationIDE(3);
			}
			if(line[7].equals("1")){
				exp.setSelfEvaluationJunit(0);
			}else if(line[8].equals("1")){
				exp.setSelfEvaluationJunit(1);
			}else if(line[9].equals("1")){
				exp.setSelfEvaluationJunit(2);
			}else if(line[10].equals("1")){
				exp.setSelfEvaluationJunit(3);
			}
			exp.selectVM();
		}
		
		//assertTrue(exp.getId() > 0);
		//assertTrue(exp.getGroup() > 0);
		//assertTrue(selectedSample.size() == 30);
	}

	@After
	public void tearDown(){
		wholeSample = null;
		selectedSample = null;
	}
	
	public void changeWeight(String weight){
		
		BufferedWriter writer;
		try {
			writer = new BufferedWriter(new FileWriter(new File(WEIGHT_FILE)));
			writer.write(weight);
			writer.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void changeAlpha(String alpha){
		BufferedWriter writer;
		try {
			writer = new BufferedWriter(new FileWriter(new File(ALPHA_FILE)));
			writer.write(alpha);
			writer.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	//@Test
	public void makeDir(){
		File dir;
		for(String participant : participantList){
			for(String weight : weightList){
				for(String alpha : alphaList){
					for(int i = 1; i <= interactions; i++){
						dir = new File(DIR_PATH+"io_"+participant+"_"+weight.replace(";", "-")+"_"+alpha+"_"+i);
						dir.mkdir();
					}
				}
			}
		}
	}
	
	//@Test
	public void runAllTests(){
		for(String participant : participantList){
			for(String weight : weightList){
				changeWeight(weight);
				for(String alpha : alphaList){
					changeAlpha(alpha);
					for(int i = 1; i <= interactions; i++){
						String dest_dir = DIR_PATH+"io_"+participant+"_"+weight.replace(";", "-")+"_"+alpha+"_"+i;
						selectRandomlyFromSampleMatrix(Integer.parseInt(participant));
						processSelectedMatrix();
						moveCurrentFiles(dest_dir);
					}
				}
			}
		}
	}
	
	//@Test
	public void moveCurrentFiles(String dest_dir){
		try {
			File dirIo = new File(DIR_PATH+"io");
			File dirIoContent[] = dirIo.listFiles();
			for(File file : dirIoContent){
				if(file.getName().endsWith("alpha.txt") || file.getName().endsWith("q.txt") || file.getName().endsWith("w.txt")){
				}else{
					Files.move(Paths.get(file.getAbsolutePath()),Paths.get(dest_dir+"/"+file.getName()), StandardCopyOption.REPLACE_EXISTING);
				}
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testAllocationPerSampleAndPerAlpha(){
		int sampleSize = 20;
		//for(String participantSample : participantList){
			for(int alphaIndex = 0; alphaIndex < alphaList.length; alphaIndex++){
				changeAlpha(alphaList[alphaIndex]);
				for(int iSample = 0; iSample < runsPerAlpha; iSample++){
					//selectRandomlyFromSampleMatrix(Integer.parseInt(participantSample));
					selectRandomlyFromSampleMatrix(sampleSize);
					int sumOfAllocationsPerSample = 0;
					for(int iRun = 0; iRun < runsPerSample; iRun++){
						System.out.println("runsPerSample: "+(iRun+1)+"/"+runsPerSample+", runsPerAlpha: "+(iSample+1)+"/"+runsPerAlpha+", currentAlpha: "+ alphaList[alphaIndex]);
						processSelectedMatrix();
						sumOfAllocationsPerSample += readNumberOfAllocatedGroups();
						removeAllocationFiles();
					}
					allocationResultMatrix[alphaIndex][iSample] = ((double)sumOfAllocationsPerSample)/runsPerSample;
				}
			}
		//}
		saveAllocationResult();
	}
	
	public int readNumberOfAllocatedGroups(){
		int numberOfAllocatedGroups = 0;
		List<String[]> freqMatrix = new ArrayList<String[]>();
		try {
			CsvReader reader = new CsvReader(new BufferedReader(new FileReader(new File(FREQ_MATRIX_FILE))));
			reader.setFieldDelimiter(';');
			reader.setBlockDelimiter('\n');
			
			String line[];
			while((line = reader.readLine()) != null){
				freqMatrix.add(line);
			}
			reader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		
		for(String lineGroup[] : freqMatrix){
			for(String col : lineGroup){
				//if(col != "0"){ real bug
				if(!col.equals("0")){
					numberOfAllocatedGroups++;
					break;
				}
			}
		}
		System.out.println(">>>>>>>>>>>>Printing freq.csv");
		for(String line[] : freqMatrix){
			System.out.println(line[0]+";"+line[1]+";"+line[2]+";"+line[3]+";"+line[4]+";"+line[5]+";"+line[6]+";"+line[7]+";"+line[8]+";"+line[9]+";"+line[10]);
		}
		return numberOfAllocatedGroups;
	}
	
	public void removeAllocationFiles(){
		try {
			File dirIo = new File(DIR_PATH+"io");
			File dirIoContent[] = dirIo.listFiles();
			for(File file : dirIoContent){
				if(file.getName().endsWith("alpha.txt") || file.getName().endsWith("q.txt") || file.getName().endsWith("w.txt")){
				}else{
					Files.delete(Paths.get(file.getAbsolutePath()));
				}
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void saveAllocationResult(){
		
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			CsvWriter csv = new CsvWriter(new OutputStreamWriter(baos));
			csv.setFieldDelimiter(';');
			csv.setBlockDelimiter('\n');
		
			fillHeader(csv);
			fillAllocationResultMatrix(csv);
			
			csv.close();
			
			OutputStream output = new FileOutputStream(ALLOC_RESULT_FILE);
			baos.writeTo(output);
			output.close();
			baos.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public void fillHeader(CsvWriter csv) {
		try {
			for(String alpha : alphaList){
				csv.writeField(alpha);
			}
			csv.endBlock();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void fillAllocationResultMatrix(CsvWriter csv){
		try {
			for(int line = 0; line < runsPerAlpha; line++){
				for(int alphaColumn = 0; alphaColumn < alphaList.length; alphaColumn++){
					csv.writeField(String.valueOf(allocationResultMatrix[alphaColumn][line]));
				}
				csv.endBlock();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
