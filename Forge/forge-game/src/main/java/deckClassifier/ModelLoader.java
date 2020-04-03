package deckClassifier;
import java.io.IOException;

import org.deeplearning4j.nn.modelimport.keras.KerasModelImport;
import org.deeplearning4j.nn.modelimport.keras.exceptions.InvalidKerasConfigurationException;
import org.deeplearning4j.nn.modelimport.keras.exceptions.UnsupportedKerasConfigurationException;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;

public class ModelLoader {

	MultiLayerNetwork network;
	String AI_MODEL_PATH = "../../PythonNN/Tensorflow testing/pyScripts/"; // path to where the model data (h5/json) are located
	public ModelLoader() {
		
	}
	
	public void LoadModel() {
		try {
			network = KerasModelImport.importKerasSequentialModelAndWeights(AI_MODEL_PATH+"model.json",AI_MODEL_PATH+"model.h5");
		} catch (IOException | InvalidKerasConfigurationException | UnsupportedKerasConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public INDArray Predict(INDArray data) {
		return network.output(data);
	}
}
