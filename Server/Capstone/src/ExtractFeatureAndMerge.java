import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
/*
 * @author : Suresh Babu Jothilingam
 */
public class ExtractFeatureAndMerge {
	private File activityDirectory;
	private ExtractFeatureAndMergeMethods methods;
	private String activityName;
	private File[] dataFiles;
	private File output;
	private PrintWriter write;
	
	public ExtractFeatureAndMerge(){
		
	}
	
	public ExtractFeatureAndMerge(String directoryPath) {
		// TODO Auto-generated constructor stub
		this.activityDirectory = new File(directoryPath);
		this.activityName = activityDirectory.getName();
		this.dataFiles = activityDirectory.listFiles();
		this.methods = new ExtractFeatureAndMergeMethods();
	}
	
	//Main method to read files extract features and print to a output file
	void extractFeaturesAndPrint(String outputPath) throws Exception{
		this.output = new File(outputPath);
		if(!this.output.exists())
			this.output.createNewFile();
		this.write = new PrintWriter(new BufferedWriter(new FileWriter(this.output)));
		this.write.println("fundamental_frequency,avg_accl,min_amplitude,max_amplitude,activity");
		for(File file : this.dataFiles){
			BufferedReader read = new BufferedReader(new FileReader(file));
			//output file path
			String line;
			ArrayList<String> values = new ArrayList<String>();
			while((line = read.readLine())!=null){
				if(line.contains("#") && values.size()==150){
					double[] input = this.methods.getAccelerationMagnitude(values);
					this.write.println(this.methods.getFundamentalFrequency(input)+","+this.methods.getAvgAcceleration(input)+","+this.methods.getMinAmplitude(input)+","+this.methods.getMaxAmplitude(input)+","+this.activityName);
					values.clear();
				} else{
					values.add(line);
				}
			}
			//if remaining instances size equal to 150 because window size is 150
			if(values.size()==150){
				double[] input = this.methods.getAccelerationMagnitude(values);
				this.write.println(this.methods.getFundamentalFrequency(input)+","+this.methods.getAvgAcceleration(input)+","+this.methods.getMinAmplitude(input)+","+this.methods.getMaxAmplitude(input)+","+this.activityName);
				values.clear();
			}
			read.close();
		}
		this.write.close();
	}
	
	//Merge the feature extracted files for all activities
	public void mergeFiles(String outputDirectory, String trainDataSetPath) throws Exception{
		File extractedFilesDirectory = new File(outputDirectory);
		File[] extractedFiles = extractedFilesDirectory.listFiles();
		File trainDataSet = new File(trainDataSetPath);
		if(!trainDataSet.exists())
			trainDataSet.createNewFile();
		PrintWriter write = new PrintWriter(new BufferedWriter(new FileWriter(trainDataSet)));
		write.println("fundamental_frequency,avg_accl,min_amplitude,max_amplitude,activity");
		for(File file : extractedFiles){
			BufferedReader read = new BufferedReader(new FileReader(file));
			read.readLine();//leaves the first line which will be the attribute names
			String line;
			while((line = read.readLine())!=null)
				write.println(line);
			read.close();
		}
		write.close();
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try{
			String sittingDirectory = FilePaths.sittingDirectory;
			String sittingOutputPath = FilePaths.sittingOutputPath;
			String standingDirectory = FilePaths.standingDirectory;
			String standingOutputPath = FilePaths.standingOutputPath;
			String walkingDirectory = FilePaths.walkingDirectory;
			String walkingOutputPath = FilePaths.walkingOutputPath;
			String trainDataSetPath = FilePaths.trainDataSetPath;
			String outputDirectory = FilePaths.outputDirectory;
			
			System.out.println("Extracting Features......");
			new ExtractFeatureAndMerge(sittingDirectory).extractFeaturesAndPrint(sittingOutputPath);
			new ExtractFeatureAndMerge(standingDirectory).extractFeaturesAndPrint(standingOutputPath);
			new ExtractFeatureAndMerge(walkingDirectory).extractFeaturesAndPrint(walkingOutputPath);
			System.out.println("Finished Extracting Features");
			
			System.out.println("Merging Files");
			new ExtractFeatureAndMerge().mergeFiles(outputDirectory, trainDataSetPath);
			System.out.println("Finished Merging Files");
			
			BuildClassificationModels model = new BuildClassificationModels(trainDataSetPath);
			
			System.out.println("Building Models.....");
			System.out.println("Finished Building J48 Model "+model.constructJ48Model());
			System.out.println("Finished Building NaiveBayes Model "+model.constructNaiveBayesModel());
			System.out.println("Finished Building K-Nearest Neighbour Model "+model.constructKNNModel());
			System.out.println("Finished Building Random Forest Model "+model.constructRandomForestModel());
			System.out.println("Completed");
			
		} catch(Exception e){
			e.printStackTrace();
		}
	}

}
