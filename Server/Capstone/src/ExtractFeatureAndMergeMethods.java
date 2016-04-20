import java.util.ArrayList;
import java.util.Collections;

public class ExtractFeatureAndMergeMethods {
	//discrete fourier transform to get fundamental frequencies
		double getFundamentalFrequency(double[] input){
			double fundamentalFrequency = 0;
			ArrayList<Double> output = new ArrayList<Double>();
			for(int i=0;i<input.length;i++){
				double sum = 0;
				for(int j=0;j<input.length;j++){
					double angle = 2*Math.PI*j*i/input.length;
					sum+=input[i]*Math.cos(Math.toRadians(angle));
					//System.out.println("sum "+sum);
				}
				output.add(sum);
			}
			Collections.sort(output);
			fundamentalFrequency = (output.get(0)+output.get(1)+output.get(2)/3);

			return fundamentalFrequency;
		}

		double getAvgAcceleration(double[] input){
			double avgAcceleration = 0;
			for(int i=0;i<input.length;i++){
				avgAcceleration+=input[i];
			}
			avgAcceleration/=input.length;

			return avgAcceleration;
		}

		double getMaxAmplitude(double[] input){
			double maxAmplitude = 0;
			for(int i=0;i<input.length;i++){
				if(i==0)
					maxAmplitude = input[i];

				if(maxAmplitude<=input[i])
					maxAmplitude = input[i];
			}
			return maxAmplitude;
		}
		
		double getMinAmplitude(double[] input){
			double minAmplitude = 0;
			for(int i=0;i<input.length;i++){
				if(i==0)
					minAmplitude = input[i];

				if(minAmplitude<=input[i])
					minAmplitude = input[i];
			}
			return minAmplitude;
		}
		
		//get acceleration magnitude from x,y,z values
		double[] getAccelerationMagnitude(ArrayList<String> values){
			double[] input = new double[values.size()];
			for(int i=0;i<values.size();i++){
				String[] xyz = values.get(i).split(",");
				input[i] = Math.sqrt(Math.pow(Double.parseDouble(xyz[0]), 2)+Math.pow(Double.parseDouble(xyz[1]), 2)+Math.pow(Double.parseDouble(xyz[2]), 2));
				//System.out.println(input[i]);
			}
			return input;
		}
}
