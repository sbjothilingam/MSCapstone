import weka.classifiers.Classifier;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.lazy.IBk;
import weka.classifiers.trees.J48;
import weka.classifiers.trees.RandomForest;
import weka.core.Instances;
import weka.core.SerializationHelper;
import weka.core.converters.ConverterUtils.DataSource;

public class BuildClassificationModels {
	private DataSource trainData;
	private Instances trainInstance;
	private J48 j48;
	private NaiveBayes naiveBayes;
	private RandomForest randomForest;
	private Classifier kNN;
	
	private String rootPathModels = FilePaths.rootPathModels;
	
	public BuildClassificationModels(String trainDataPath) throws Exception{
		trainData = new DataSource(trainDataPath); //load training data source
		trainInstance = trainData.getDataSet(); //get training instance
		
		trainInstance.setClassIndex(trainInstance.numAttributes() - 1); //set the index of class attribute
		
		j48 = new J48();
		naiveBayes = new NaiveBayes();
		randomForest = new RandomForest();
		kNN = new IBk();
	}
	
	public String constructJ48Model() throws Exception{
		String path = rootPathModels+"J48.model";
		j48.buildClassifier(trainInstance); //build model
		
		SerializationHelper.write(path, j48); //write model to file
		
		return path;
	}
	
	public String constructNaiveBayesModel() throws Exception{
		String path = rootPathModels+"NaiveBayes.model";
		naiveBayes.buildClassifier(trainInstance);
		
		SerializationHelper.write(path, naiveBayes);
		
		return path;
	}
	
	public String constructRandomForestModel() throws Exception{
		String path = rootPathModels+"RandomForest.model";
		randomForest.buildClassifier(trainInstance);
		
		SerializationHelper.write(path, randomForest);
		
		return path;
	}
	
	public String constructKNNModel() throws Exception{
		String path = rootPathModels+"KNN.model";
		kNN.buildClassifier(trainInstance);
		
		SerializationHelper.write(path, kNN);
		
		return path;
	}
}
