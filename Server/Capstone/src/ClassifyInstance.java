import java.io.File;
import java.util.ArrayList;

import weka.classifiers.Classifier;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.lazy.IBk;
import weka.classifiers.trees.J48;
import weka.classifiers.trees.RandomForest;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.SerializationHelper;
import weka.core.converters.ConverterUtils.DataSource;

public class ClassifyInstance {
	private Instance testInstance; 
	private Instances testData;
	private J48 j48;
	private NaiveBayes naiveBayes;
	private RandomForest randomForest;
	private Classifier kNN;
	
	private String rootPathModels;
	
	public ClassifyInstance(String rootPathModels) throws Exception{
		this.rootPathModels = rootPathModels;
		
		j48 = (J48) SerializationHelper.read(rootPathModels+"J48.model");
		naiveBayes = (NaiveBayes) SerializationHelper.read(rootPathModels+"NaiveBayes.model");
		randomForest = (RandomForest) SerializationHelper.read(rootPathModels+"RandomForest.model");
		kNN = (IBk) SerializationHelper.read(rootPathModels+"KNN.model");
		
	}
	
	public void setValues(double fund_freq, double avg_acc, double min_amp, double max_amp){
		FastVector attributeList = new FastVector();

        Attribute fundamental_frequency = new Attribute("fundamental_frequency");
        Attribute avg_accl = new Attribute("avg_accl");
        Attribute min_amplitude = new Attribute("min_amplitude");
        Attribute max_amplitude = new Attribute("max_amplitude");

        FastVector classValues = new FastVector();
        classValues.addElement("sitting");
        classValues.addElement("standing");
        classValues.addElement("walking");

        attributeList.addElement(fundamental_frequency);
        attributeList.addElement(avg_accl);
        attributeList.addElement(min_amplitude);
        attributeList.addElement(max_amplitude);
        
        attributeList.addElement(new Attribute("@@class@@",classValues));
        
        this.testData = new Instances("TestData",attributeList,0);
        
        this.testInstance = new Instance(testData.numAttributes());
        
        
        this.testInstance.setValue(fundamental_frequency, fund_freq);
        this.testInstance.setValue(avg_accl, avg_acc);
        this.testInstance.setValue(min_amplitude, min_amp);
        this.testInstance.setValue(max_amplitude, max_amp);
        
        this.testData.add(this.testInstance);
        this.testData.setClassIndex(this.testData.numAttributes() - 1);
	}
	
	public String j48Result() throws Exception{
		String result = "";
        double resultJ48 = this.j48.classifyInstance(this.testData.firstInstance());
        result = this.testData.classAttribute().value((int) resultJ48);
        
		return result;
	}
	
	public String naiveBayesResult() throws Exception{
		String result = "";
        double resultNaiveBayes = this.naiveBayes.classifyInstance(this.testData.firstInstance());
        result = this.testData.classAttribute().value((int) resultNaiveBayes);
        
		return result;
	}
	
	public String randomForestResult() throws Exception{
		String result = "";
        double resultRandomForest = this.randomForest.classifyInstance(this.testData.firstInstance());
        result = this.testData.classAttribute().value((int) resultRandomForest);
        
		return result;
	}
	
	public String kNNResult() throws Exception{
		String result = "";
        double resultKNN = this.kNN.classifyInstance(this.testData.firstInstance());
        result = this.testData.classAttribute().value((int) resultKNN);
        
		return result;
	}
	
}
