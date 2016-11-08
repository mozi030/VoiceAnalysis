package NeuralNetwork;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class Main {
	static public void main(String args[]) throws Exception {
		ArrayList<Data> allTrainData = LoadTrainData();
		
		NeuralNetwork neuralNetwork = new NeuralNetwork();
		neuralNetwork.TrainNetwork(allTrainData);

//		NeuralNetwork.testNeuralNetwork(allTrainData);
		
		// System.out.println();
	}

	static public ArrayList<Data> LoadTrainData() throws Exception {
		ArrayList<Data> allTrainData = new ArrayList<Data>();

		String testPath = "/Users/moziliang/Documents/香港留学/2class/1Mutimedia(ROSSITER)/project/voice/all_voice_source_processed";
		File testFolder = new File(testPath);
		for (File file1 : testFolder.listFiles()) {
			if (checkIsChar(file1.getName().charAt(0))) {
				for (File file2 : file1.listFiles()) {
					if (checkIsChar(file2.getName().charAt(0))) {
						String fileName = testPath + "/" + file1.getName() + "/" + file2.getName()
								+ "/selected_samples.txt";
						File file3 = new File(fileName);
						if (!file3.exists()) {
							throw new Exception("!file3.exists()");
						}

						int[] currentData = new int[NeuralNetwork.inputNum];
						int index = 0;
						BufferedReader bufferedReader = new BufferedReader(new FileReader(file3));
						String line = null;
						while ((line = bufferedReader.readLine()) != null) {
							int currentNum = Integer.parseInt(line.replace("\n", ""));
							currentData[index++] = currentNum;
						}
						if (index != NeuralNetwork.inputNum) {
							throw new Exception("index != NeuralNetwork.inputNum");
						}
						Data data = new Data();
						data.dataList = currentData;
						data.result = NeuralNetwork.outputs.indexOf(file1.getName());
						allTrainData.add(data);
					}
				}
			}
		}
		
//		Data[] dataArray = (Data[]) allTrainData.toArray();
		Object[] objectList = allTrainData.toArray();
		Data[] dataArray =  Arrays.copyOf(objectList,objectList.length,Data[].class);
		
		Random random = new Random();
		for (int i = 0; i < allTrainData.size() - 1; i++) {
			int nextInt = i + random.nextInt(allTrainData.size() - i);
			Data temp = dataArray[i];
			dataArray[i] = dataArray[nextInt];
			dataArray[nextInt] = temp;
		}
		allTrainData = new ArrayList<Data> (Arrays.asList(dataArray));
		
		return allTrainData;
	}

	public static boolean checkIsChar(char a) {
		return (a >= 'a' && a <= 'z') || (a >= 'A' && a <= 'Z');
	}
}
