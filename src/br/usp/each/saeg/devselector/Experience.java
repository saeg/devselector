package br.usp.each.saeg.devselector;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.NoneScoped;
import javax.faces.bean.RequestScoped;
import javax.faces.bean.SessionScoped;
import javax.faces.bean.ViewScoped;

import com.generationjava.io.CsvReader;
import com.generationjava.io.CsvWriter;

@ManagedBean(name="experience")
@SessionScoped
//@RequestScoped
//@ViewScoped
//@NoneScoped
public class Experience {
	private int id;
	private int generalExperience;
	private int professionalExperience;
	private int javaExperience;
	private int ideExperience;
	private int testExperience;
	private int selfEvaluationJava;
	private int selfEvaluationIDE;
	private int selfEvaluationJunit;
	private int lastFiveYears;
	private boolean idIsGenerated = false;
	private String group;
	private String selectedLogin;
	private String selectedPassword;
	private String selectedVmLink;
	private final String[] login = {"Experiment One","Experiment Two","Experiment Three","Experiment Four"};
	private final String[] password = {"dev110150","dev123400","dev365001","dev589651"};
	private final String[] vmLink = {"lubuntu-method.zip","lubuntu-line.zip"};
	private final String serverLink = "http://www.expjaguar.com/jaguar/";
	//private final String DIR_PATH = System.getProperty("user.home")+"/webfiles/io";//it doesn't work from tomcat
	private final String DIR_PATH_ABOVE = "/Users/higor/webfiles/";//for mac
	//private final String DIR_PATH_ABOVE = "/var/lib/tomcat8/webfiles/";//for the ubuntu server
	private final String DIR_PATH = DIR_PATH_ABOVE+"io/";//for the ubuntu server
	private final String COMMAND = DIR_PATH_ABOVE+"allocation.sh ";
	private final String ARRAY_FILE = "x_";
	private final String MATRIX_FILE = "matrix.csv";
	private final String FREQUENCY_MATRIX_FILE = "freq.csv";
	private final String FREQUENCY_MATRIX_FILE_ID = "freq_";
	private final String FILE_EXTENSION = ".txt";
	private final String OUTCOME_FILE = "out_";
	
	private final int GROUP_MATRIX_COLUMNS = 20;
	private final int FREQ_MATRIX_COLUMNS = 11;
	private final int FREQ_MATRIX_ROWS = 8;
	private List<String[]> groupMatrix = new ArrayList<String[]>();
	private List<String[]> frequencyMatrix = new ArrayList<String[]>();
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getGeneralExperience() {
		return generalExperience;
	}
	public void setGeneralExperience(int generalExperience) {
		this.generalExperience = generalExperience;
	}
	public int getProfessionalExperience() {
		return professionalExperience;
	}
	public void setProfessionalExperience(int professionalExperience) {
		this.professionalExperience = professionalExperience;
	}
	public int getJavaExperience() {
		return javaExperience;
	}
	public void setJavaExperience(int javaExperience) {
		this.javaExperience = javaExperience;
	}
	public int getIdeExperience() {
		return ideExperience;
	}
	public void setIdeExperience(int ideExperience) {
		this.ideExperience = ideExperience;
	}
	public int getTestExperience() {
		return testExperience;
	}
	public void setTestExperience(int testExperience) {
		this.testExperience = testExperience;
	}
	public String getGroup() {
		return group;
	}
	public void setGroup(String group) {
		this.group = group;
	}
	public int getLastFiveYears() {
		return lastFiveYears;
	}
	public void setLastFiveYears(int lastFiveYears) {
		this.lastFiveYears = lastFiveYears;
	}
	public String getSelectedLogin() {
		return selectedLogin;
	}
	public void setSelectedLogin(String selectedLogin) {
		this.selectedLogin = selectedLogin;
	}
	public String getSelectedPassword() {
		return selectedPassword;
	}
	public void setSelectedPassword(String selectedPassword) {
		this.selectedPassword = selectedPassword;
	}
	public String getSelectedVmLink() {
		return selectedVmLink;
	}
	public void setSelectedVmLink(String selectedVmLink) {
		this.selectedVmLink = selectedVmLink;
	}
	
	public int getSelfEvaluationJava() {
		return selfEvaluationJava;
	}
	public void setSelfEvaluationJava(int selfEvaluationJava) {
		this.selfEvaluationJava = selfEvaluationJava;
	}
	public int getSelfEvaluationIDE() {
		return selfEvaluationIDE;
	}
	public void setSelfEvaluationIDE(int selfEvaluationIDE) {
		this.selfEvaluationIDE = selfEvaluationIDE;
	}
	public int getSelfEvaluationJunit() {
		return selfEvaluationJunit;
	}
	public void setSelfEvaluationJunit(int selfEvaluationJunit) {
		this.selfEvaluationJunit = selfEvaluationJunit;
	}
	
	public String selectVM(){
		if(!idIsGenerated){
			if(this.selfEvaluationJava == 0){
				this.group = "0";
			}else{
				this.generateID();
				this.saveParticipantArray();//creates the new participant array
				this.copyFrequencyMatrixWithID();//copies the main freq matrix as an instance for the ID
				this.callSequencialAllocationMethod();//calls the allocation script
				this.readAllocationOutcome();
				this.selectVMGroup();//chooses the group
				this.addNewParticipantInGroupMatrix();
				this.saveDatabaseGroupMatrix();//saves the new participant
				this.calculateFrequencyMatrix();//updates freq matrix
				
				cleanData();//clean data form to avoid the creation of false participants, to be used with @SessionScoped
			}
		}else{System.out.println("Id already created in this session");}
		System.out.println("group is: "+this.group);
		return "";
	}
	
	private void cleanData(){
		idIsGenerated = true;
		this.generalExperience = 0;
		this.professionalExperience = 0;
		this.javaExperience = 0;
		this.ideExperience = 0;
		this.testExperience = 0;
		this.selfEvaluationJava = 0;
		this.selfEvaluationIDE = 0;
		this.selfEvaluationJunit = 0;
	}
	
	private void selectVMGroup(){
		switch(this.group){
			case "1":{
				this.selectedLogin = login[0];
				this.selectedPassword = password[0];
				this.selectedVmLink = serverLink+vmLink[0];
				break;
			}
			case "2":{
				this.selectedLogin = login[1];
				this.selectedPassword = password[1];
				this.selectedVmLink = serverLink+vmLink[0];
				break;
			}
			case "3":{
				this.selectedLogin = login[2];
				this.selectedPassword = password[2];
				this.selectedVmLink = serverLink+vmLink[0];
				break;
			}
			case "4":{
				this.selectedLogin = login[3];
				this.selectedPassword = password[3];
				this.selectedVmLink = serverLink+vmLink[0];
				break;
			}
			case "5":{
				this.selectedLogin = login[0];
				this.selectedPassword = password[0];
				this.selectedVmLink = serverLink+vmLink[1];
				break;
			}
			case "6":{
				this.selectedLogin = login[1];
				this.selectedPassword = password[1];
				this.selectedVmLink = serverLink+vmLink[1];
				break;
			}
			case "7":{
				this.selectedLogin = login[2];
				this.selectedPassword = password[2];
				this.selectedVmLink = serverLink+vmLink[1];
				break;
			}
			case "8":{
				this.selectedLogin = login[3];
				this.selectedPassword = password[3];
				this.selectedVmLink = serverLink+vmLink[1];
				break;
			}
			default:{
				this.selectedLogin = "";
				this.selectedPassword = "";
				this.selectedVmLink = "";
			}
		}
	}
	
	private void generateID(){
		int idMin = 10000000;
		int idMax = 99999999;
		int newId = 0;
		Random rand = new Random();
		
		if(checkMatrixFile()){//loads the groupMatrix if it exists
			readGroupMatrix();
		}
		
		newId = rand.nextInt((idMax-idMin)+1) + idMin;
		while(idAlreadyExists(newId)){
			newId = rand.nextInt((idMax-idMin)+1) + idMin;
		}
		this.id = newId;
	}
	
	private boolean idAlreadyExists(int newId) {
		for(String line[] : groupMatrix){
			if(Integer.valueOf(line[0]) == newId){
				return true;
			}
		}
		return false;
	}
	
	private void saveParticipantArray(){
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			CsvWriter csv = new CsvWriter(new OutputStreamWriter(baos));
			csv.setFieldDelimiter(';');
			csv.setBlockDelimiter('\n');
		
			fillParticipantArray(csv);
		
			csv.close();
			
			OutputStream output = new FileOutputStream(DIR_PATH+ARRAY_FILE+id+FILE_EXTENSION);
			baos.writeTo(output);
			output.close();
			baos.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void saveDatabaseGroupMatrix(){
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			CsvWriter csv = new CsvWriter(new OutputStreamWriter(baos));
			csv.setFieldDelimiter(';');
			csv.setBlockDelimiter('\n');
		
			fillHeader(csv);
			fillNewParticipantInMatrix(csv);
		
			csv.close();
			
			OutputStream output = new FileOutputStream(DIR_PATH+MATRIX_FILE);
			baos.writeTo(output);
			output.close();
			baos.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	private void fillHeader(CsvWriter csv) {
		try {
			csv.writeField("id");
			csv.writeField("j1");
			csv.writeField("j2");
			csv.writeField("j3");
			csv.writeField("i0");
			csv.writeField("i1");
			csv.writeField("i2");
			csv.writeField("i3");
			csv.writeField("t0");
			csv.writeField("t1");
			csv.writeField("t2");
			csv.writeField("t3");
			csv.writeField("gr");
			csv.writeField("st");
			csv.writeField("ts");
			csv.writeField("q1");
			csv.writeField("q2");
			csv.writeField("q3");
			csv.writeField("q5");
			csv.writeField("q7");
			csv.endBlock();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	private void fillParticipantArray(CsvWriter csv) {
		try {
			csv.writeField((this.selfEvaluationJava==1)?"1":"0");
			csv.writeField((this.selfEvaluationJava==2)?"1":"0");
			csv.writeField((this.selfEvaluationJava==3)?"1":"0");
			csv.writeField((this.selfEvaluationIDE==0)?"1":"0");
			csv.writeField((this.selfEvaluationIDE==1)?"1":"0");
			csv.writeField((this.selfEvaluationIDE==2)?"1":"0");
			csv.writeField((this.selfEvaluationIDE==3)?"1":"0");
			csv.writeField((this.selfEvaluationJunit==0)?"1":"0");
			csv.writeField((this.selfEvaluationJunit==1)?"1":"0");
			csv.writeField((this.selfEvaluationJunit==2)?"1":"0");
			csv.writeField((this.selfEvaluationJunit==3)?"1":"0");
			csv.endBlock();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void fillNewParticipantInMatrix(CsvWriter csv) {
		try {
			for(String strLine[] : groupMatrix){
				for(int i = 0; i < GROUP_MATRIX_COLUMNS; i++){
					csv.writeField(strLine[i]);
				}
				csv.endBlock();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private boolean checkMatrixFile(){
		File matrixFile = new File(DIR_PATH+MATRIX_FILE);
		return matrixFile.exists();
	}
	
	private boolean checkFrequencyMatrixFile(){
		File freqMatrixFile = new File(DIR_PATH+FREQUENCY_MATRIX_FILE);
		return freqMatrixFile.exists();
	}
	
	private void addNewParticipantInGroupMatrix(){
		String line[] = new String[GROUP_MATRIX_COLUMNS];
		line[0] = String.valueOf(this.id);
		line[1] = (this.selfEvaluationJava==1)?"1":"0";
		line[2] = (this.selfEvaluationJava==2)?"1":"0";
		line[3] = (this.selfEvaluationJava==3)?"1":"0";
		line[4] = (this.selfEvaluationIDE==0)?"1":"0";
		line[5] = (this.selfEvaluationIDE==1)?"1":"0";
		line[6] = (this.selfEvaluationIDE==2)?"1":"0";
		line[7] = (this.selfEvaluationIDE==3)?"1":"0";
		line[8] = (this.selfEvaluationJunit==0)?"1":"0";
		line[9] = (this.selfEvaluationJunit==1)?"1":"0";
		line[10] = (this.selfEvaluationJunit==2)?"1":"0";
		line[11] = (this.selfEvaluationJunit==3)?"1":"0";
		line[12] = this.group;
		line[13] = "0";
		line[14] = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
		line[15] = String.valueOf(this.generalExperience);
		line[16] = String.valueOf(this.professionalExperience);
		line[17] = String.valueOf(this.javaExperience);
		line[18] = String.valueOf(this.ideExperience);
		line[19] = String.valueOf(this.testExperience);
		groupMatrix.add(line);
	}
	
	private void readGroupMatrix(){
		try {
			CsvReader reader = new CsvReader(new BufferedReader(new FileReader(new File(DIR_PATH+MATRIX_FILE))));
			reader.setFieldDelimiter(';');
			reader.setBlockDelimiter('\n');
			
			groupMatrix.clear();//if groupMatrix was already loaded
			
			String line[] = reader.readLine();//remove header
			while((line = reader.readLine()) != null){
				groupMatrix.add(line);
			}
			reader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}
	
	public List<String[]> getGroupMatrix(){
		this.readGroupMatrix();
		return groupMatrix;
	}
	
	private void readFrequencyMatrix(){
		try {
			if(!checkFrequencyMatrixFile()){
				initializeFrequencyMatrix();
			}
			CsvReader reader = new CsvReader(new BufferedReader(new FileReader(new File(DIR_PATH+FREQUENCY_MATRIX_FILE))));
			reader.setFieldDelimiter(';');
			reader.setBlockDelimiter('\n');
				
			String line[];// = reader.readLine();
			while((line = reader.readLine()) != null){
				frequencyMatrix.add(line);
			}
			reader.close();
		
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}
	
	public List<String[]> getFrequencyMatrix(){
		this.readFrequencyMatrix();
		return frequencyMatrix;
	}
	
	private void calculateFrequencyMatrix(){
		//readGroupMatrix();//only for test
		final int GROUP_COLUMN = 12;//index of group column in the group matrix
		int frequencyMatrix[][] = new int[FREQ_MATRIX_ROWS][FREQ_MATRIX_COLUMNS];
		
		//initialize Matrix
		for(int i = 0; i < FREQ_MATRIX_ROWS; i++){
			for(int j = 0; j < FREQ_MATRIX_COLUMNS; j++){
				frequencyMatrix[i][j] = 0;
			}
		}
		
		//count frequency
		for(String line[] : groupMatrix){
			int group = Integer.parseInt(line[GROUP_COLUMN]);
			if(group != 0){
				for(int col = 0; col < FREQ_MATRIX_COLUMNS; col++){
					if(!line[col+1].equals("0")){
						frequencyMatrix[group-1][col] += 1; 
					}
				}
			}
		}
		
		saveFrequencyMatrix(frequencyMatrix, FREQ_MATRIX_ROWS, FREQ_MATRIX_COLUMNS);
	}
	
	
	private void saveFrequencyMatrix(int frequencyMatrix[][], int lines, int columns){
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			CsvWriter csv = new CsvWriter(new OutputStreamWriter(baos));
			csv.setFieldDelimiter(';');
			csv.setBlockDelimiter('\n');
		
			fillFrequencyMatrix(csv,frequencyMatrix,lines,columns);
		
			csv.close();
			OutputStream output = new FileOutputStream(DIR_PATH+FREQUENCY_MATRIX_FILE);
			baos.writeTo(output);
			output.close();
			baos.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	private void fillFrequencyMatrix(CsvWriter csv, int frequencyMatrix[][], int lines, int columns) {
		
		try {
			for(int r = 0; r < lines; r++){
				for(int c = 0; c < columns; c++){
					csv.writeField(String.valueOf(frequencyMatrix[r][c]));
				}
				csv.endBlock();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void initializeFrequencyMatrix(){
		int frequencyMatrix[][] = new int[FREQ_MATRIX_ROWS][FREQ_MATRIX_COLUMNS];
		
		//initialize Matrix
		for(int i = 0; i < FREQ_MATRIX_ROWS; i++){
			for(int j = 0; j < FREQ_MATRIX_COLUMNS; j++){
				frequencyMatrix[i][j] = 0;
			}
		}
		
		saveFrequencyMatrix(frequencyMatrix, FREQ_MATRIX_ROWS, FREQ_MATRIX_COLUMNS);
		}
	
	private void copyFrequencyMatrixWithID(){
		readFrequencyMatrix();
		
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			CsvWriter csv = new CsvWriter(new OutputStreamWriter(baos));
			
			csv.setFieldDelimiter(';');
			csv.setBlockDelimiter('\n');
		
			for(String strLine[] : frequencyMatrix){
				for(int i = 0; i < FREQ_MATRIX_COLUMNS; i++){
					csv.writeField(strLine[i]);
				}
				csv.endBlock();
			}
			
			csv.close();
			OutputStream output = new FileOutputStream(DIR_PATH+FREQUENCY_MATRIX_FILE_ID+id+FILE_EXTENSION);
			baos.writeTo(output);
			output.close();
			baos.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	private void callSequencialAllocationMethod(){
		try {
			Process process = Runtime.getRuntime().exec(COMMAND+id+" "+DIR_PATH_ABOVE);//passing two parameters to the allocation.sh file: ID and path to the r script
			//System.out.println("Waiting the process");
			process.waitFor();
			//System.out.println("Process done");
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException ie) {
			ie.printStackTrace();
		}
	}
	
	//reads and assigns the allocator's outcome to the group variable
	private void readAllocationOutcome(){
		String allocatedGroup = "";
		BufferedReader reader;
		try {
			reader = new BufferedReader(new FileReader(new File(DIR_PATH+OUTCOME_FILE+id+FILE_EXTENSION)));
			if(reader != null){
				allocatedGroup = reader.readLine();
				this.group = allocatedGroup;
				reader.close();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
