/**
 * 
 */
package edu.uiuc.zenvisage.service.utility;

/**
 *
 */
public class LinearNormalization implements Normalization {

	@Override
	public void normalize(double[] input) {
		// TODO Auto-generated method stub
		double max = input[0];
		double min = input[0];
		for (int i = 0; i < input.length; i++) {
			if (input[i] > max) {
				max = input[i];
			}
			if (input[i] < min) {
				min = input[i];
			}
		}
		
		if (max == min) {
			return;
		}
		
		for (int i = 0; i < input.length; i++) {
			input[i] = (input[i] - min) / (max - min) * 100;
		}
	}

}
