package forge.ai.simulation;
import java.io.IOException;

import org.deeplearning4j.nn.modelimport.keras.KerasModelImport;
import org.deeplearning4j.nn.modelimport.keras.exceptions.InvalidKerasConfigurationException;
import org.deeplearning4j.nn.modelimport.keras.exceptions.UnsupportedKerasConfigurationException;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;

public class ModelLoader {

	MultiLayerNetwork network;
	
	public void LoadModel() {
		try {
			network = KerasModelImport.importKerasSequentialModelAndWeights("..\\..\\..\\..\\..\\..\\..\\..\\PythonNN\\Tensorflow testing\\pyScripts","PATH TO YOUR H5 FILE");
		} catch (IOException | InvalidKerasConfigurationException | UnsupportedKerasConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void Predict() {
		//INDArray data = new INDArray()
		DataSet set = new DataSet();
		network.predict(set);
		//network.output(input)
	}
}
