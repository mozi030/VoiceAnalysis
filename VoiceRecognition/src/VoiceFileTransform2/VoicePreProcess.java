package VoiceFileTransform2;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.security.auth.kerberos.KerberosKey;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioFormat.Encoding;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

import comirva.audio.util.MFCC;

public class VoicePreProcess {
	static private String sourcePath = "/Users/moziliang/Documents/香港留学/2class/1Mutimedia(ROSSITER)/project/voice/all_voice_source";

	static private String outputPath = "/Users/moziliang/Documents/香港留学/2class/1Mutimedia(ROSSITER)/project/voice/all_voice_source_processed2";

	static Map<String, ArrayList<Double>> fractionMap = new HashMap<>();

	public static int getSampleLength = 2048;

	static public void main(String[] args) throws Exception {
		// fractionMap.put("down", new ArrayList<Double>(Arrays.asList(0.3, 0.6,
		// 0.1)));
		// fractionMap.put("jump", new ArrayList<Double>(Arrays.asList(0.6, 0.3,
		// 0.1)));
		// fractionMap.put("left", new ArrayList<Double>(Arrays.asList(0.7,0.15,
		// 0.15)));
		// fractionMap.put("move", new ArrayList<Double>(Arrays.asList(0.2, 0.7,
		// 0.1)));
		// fractionMap.put("right", new ArrayList<Double>(Arrays.asList(0.8,
		// 0.2)));
		// fractionMap.put("shoot", new ArrayList<Double>(Arrays.asList(0.8,
		// 0.2)));
		// fractionMap.put("speedup", new ArrayList<Double>(Arrays.asList(0.1,
		// 0.5, 0.35, 0.05)));
		// fractionMap.put("up", new ArrayList<Double>(Arrays.asList(0.8,
		// 0.2)));

		fractionMap.put("down", new ArrayList<Double>(Arrays.asList(1.0)));
		fractionMap.put("jump", new ArrayList<Double>(Arrays.asList(1.0)));
		fractionMap.put("left", new ArrayList<Double>(Arrays.asList(1.0)));
		fractionMap.put("move", new ArrayList<Double>(Arrays.asList(1.0)));
		fractionMap.put("right", new ArrayList<Double>(Arrays.asList(1.0)));
		fractionMap.put("shoot", new ArrayList<Double>(Arrays.asList(1.0)));
		fractionMap.put("speedup", new ArrayList<Double>(Arrays.asList(1.0)));
		fractionMap.put("up", new ArrayList<Double>(Arrays.asList(1.0)));

		File sourceFolder = new File(sourcePath);

		for (final File file1 : sourceFolder.listFiles()) {
			if (checkIsChar(file1.getName().charAt(0))) {
				for (final File file2 : file1.listFiles()) {
					if (checkIsChar(file2.getName().charAt(0))) {
						WeChatWavFormat wavFormat = WeChatWavFormat.readAndCheckFile(file2);

						myMFCC(wavFormat);
						
						// drawGraph(wavFormat, file1, file2, 1);
						// wavFormat = Boosting(wavFormat);
						// wavFormat = ZeroFilter(wavFormat);
						// wavFormat = sampling(wavFormat);
						// wavFormat = Boosting(wavFormat);
						// drawGraph(wavFormat, file1, file2, 2);

						// ArrayList<Double> fraction =
						// fractionMap.get(file1.getName());
						// partitionTest(wavFormat, fraction, file1, file2);

						// drawGraph(wavFormat2, file1, file2, 3);
						// wavFormat = sampling(wavFormat);
						// wavFormat = Boosting(wavFormat);
						// drawGraph(wavFormat, file1, file2, 4);
						//
						// printSamples(wavFormat, file1, file2);

						// outputWav(wavFormat, file1, file2, 0);

						// System.out.println();
					}
				}
			}
		}
		// System.out.println("begin");
		// Collections.sort(allSampleLength, new Comparator<Integer>() {
		// @Override
		// public int compare(Integer entry1, Integer entry2) {
		// return (int) (entry1 - entry2);
		// }
		// });
		// for (int i : allSampleLength) {
		// System.out.println("sampleLength: " + i);
		// }
	}

	static public double [][] myMFCC(WeChatWavFormat wavFormat) throws Exception {
		int window = 256;
		int sampleLength = wavFormat.samples.length;
		if (sampleLength % (window / 2) != 0) {
			sampleLength = (sampleLength / (window / 2) + 1) * (window / 2);
		}
		
		double[]temp = new double[sampleLength];
		for (int i = 0 ; i < wavFormat.samples.length; i++) {
			temp[i] = (double)wavFormat.samples[i];
		}
		for (int i = wavFormat.samples.length; i < sampleLength; i++) {
			temp[i] = 0.0;
		}
//		MFCC mfcc = new MFCC(24000);
//		double [][]result = mfcc.process(temp);
		
		MFCC mfcc2 = new MFCC(24000, 256, 13, true);
		double [][]result2 = mfcc2.process(temp);
		
		return result2;
		
//		// pre-emphasis
//		double a = 0.9;
//		double[] temp = new double[wavFormat.samples.length];
//		temp[0] = wavFormat.samples[0];
//		for (int i = 1; i < wavFormat.samples.length; i++) {
//			temp[i] = (wavFormat.samples[i] - a * wavFormat.samples[i - 1]);
//		}
//
//		// Frame blocking
//		int frameSize = 512;
//		int overlapFrameSize = frameSize / 3;
//		int frameNum = temp.length / frameSize;
//		double frames[][] = new double[frameNum][frameSize + overlapFrameSize];
//		for (int i = 0; i < frameNum - 1; i++) {
//			System.arraycopy(temp, i * frameSize, frames[i], 0, frames[i].length);
//		}
//		int lastLength = 0;
//		if (frameNum * frameSize + overlapFrameSize < temp.length) {
//			lastLength = frameSize + overlapFrameSize;
//		} else {
//			lastLength = temp.length - (frameNum - 1) * frameSize;
//		}
//		System.arraycopy(temp, (frameNum - 1) * frameSize, frames[frameNum - 1], 0, lastLength);
//
//		// Hamming window
//		a = 0.46;
//		for (int i = 0; i < frames.length; i++) {
//			for (int j = 0; j < frames[i].length; j++) {
//				frames[i][j] *= (1 - a) - a * Math.cos(2 * Math.PI * j / (frames[i].length - 1));
//			}
//		}
//		
//		//fft
//		for (int i = 0; i < frames.length; i++) {
//			frames[i] = FFT.fft(frames[i]);
//		}
//
//		return wavFormat;
	}

	static public void drawGraph(WeChatWavFormat wavFormat, File file1, File file2, int graphNum) throws Exception {

		String newFolderName = outputPath + "/" + file1.getName() + "/" + file2.getName();
		newFolderName = newFolderName.substring(0, newFolderName.length() - 4);
		File file3 = new File(newFolderName);
		if (!file3.exists() || !file3.isDirectory()) {
			file3.mkdir();
		}

		int[] lineReadings = new int[] { -32768, 32768 };

		String fileName = newFolderName + "/" + graphNum + ".jpg";
		int width = 3000;
		int height = 7000;
		BufferedImage bimg = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		Graphics2D g2d = (Graphics2D) bimg.getGraphics();
		g2d.setBackground(Color.WHITE);
		g2d.clearRect(0, 0, width, height);

		g2d.setColor(Color.BLUE);
		for (int i = lineReadings[0]; i <= lineReadings[1]; i += 2048) {
			g2d.setStroke(new BasicStroke(1));
			int y = -i / 10 + height / 2;
			g2d.drawLine(0, y, width, y);
			g2d.setStroke(new BasicStroke(20));
			g2d.drawString("" + i, 0, y);
		}
		for (int i = 0; i <= wavFormat.samples.length; i += wavFormat.samples.length / 20) {
			g2d.setStroke(new BasicStroke(1));
			int x = (int) (1.0 * i / wavFormat.samples.length * width);
			g2d.drawLine(x, 0, x, height);
			g2d.setStroke(new BasicStroke(20));
			g2d.drawString("" + i, x, 10);
		}

		for (int i = 0; i < wavFormat.samples.length; i++) {
			g2d.setColor(Color.BLACK);
			g2d.setStroke(new BasicStroke(10));
			int x = (int) (1.0 * (i + 1) / wavFormat.samples.length * width);
			int y = -wavFormat.samples[i] / 10 + height / 2;
			g2d.fillOval(x, y, 10, 10);
			if (i != wavFormat.samples.length - 1) {
				int nextX = (int) (1.0 * (i + 1 + 1) / wavFormat.samples.length * width);
				int nextY = -wavFormat.samples[i + 1] / 10 + height / 2;
				g2d.drawLine(x, y, nextX, nextY);
			}
		}

		ImageIO.write(bimg, "JPG", new FileOutputStream(fileName));
	}

	static public WeChatWavFormat Boosting(WeChatWavFormat wavFormat) throws Exception {
		int scale = 0;
		for (int i = 0; i < wavFormat.samples.length; i++) {
			if (Math.abs(wavFormat.samples[i]) > scale) {
				scale = Math.abs(wavFormat.samples[i]);
			}
		}

		for (int i = 0; i < wavFormat.samples.length; i++) {
			wavFormat.samples[i] = (int) (1.0 * wavFormat.samples[i] / scale * 32768);
			if (wavFormat.samples[i] > 32767) {
				wavFormat.samples[i] = 32767;
			} else if (wavFormat.samples[i] < -32768) {
				wavFormat.samples[i] = -32768;
			}
		}
		return wavFormat;
	}

	static public WeChatWavFormat ZeroFilter(WeChatWavFormat wavFormat) throws Exception {
		int[] temp = new int[wavFormat.samples.length];
		System.arraycopy(wavFormat.samples, 0, temp, 0, wavFormat.samples.length);
		int begin = 0;
		int end = wavFormat.samples.length - 1;
		while (Math.abs(temp[begin]) < 4096) {
			begin++;
		}
		while (Math.abs(temp[end]) < 4096) {
			end--;
		}
		if (begin > end) {
			throw new Exception("begin > end");
		}

		wavFormat.samples = new int[end - begin + 1];
		System.arraycopy(temp, begin, wavFormat.samples, 0, wavFormat.samples.length);
		return wavFormat;
	}

	static ArrayList<Integer> allSampleLength = new ArrayList<>();

	static public void partitionTest(WeChatWavFormat wavFormat, ArrayList<Double> fraction, File file1, File file2)
			throws Exception {
		String newFolderName = outputPath + "/" + file1.getName() + "/" + file2.getName();
		newFolderName = newFolderName.substring(0, newFolderName.length() - 4);
		File file3 = new File(newFolderName);
		if (!file3.exists() || !file3.isDirectory()) {
			file3.mkdir();
		}

		allSampleLength.add(wavFormat.samples.length);
		int[] temp = new int[getSampleLength];
		System.arraycopy(wavFormat.samples, 0, temp, 0, getSampleLength);
		temp = FFT.fft(temp);
		// System.out.println(temp.length);

		String fileName = newFolderName + "/all.txt";
		BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(new File(fileName)));
		for (int j = 0; j < temp.length; j++) {
			bufferedWriter.write("" + temp[j] + "\n");
		}
		bufferedWriter.close();

		int usedLength = 0;
		int supplementLength = (int) (0.05 * temp.length);
		for (int i = 0; i < fraction.size(); i++) {
			double currentFraction = fraction.get(i);
			int currentLength = (int) (1.0 * temp.length * currentFraction);
			int finalLength = currentLength + supplementLength;
			if (finalLength + usedLength > temp.length) {
				finalLength = temp.length - usedLength;
			}
			int[] fractionData = new int[finalLength];
			System.arraycopy(temp, usedLength, fractionData, 0, fractionData.length);
			usedLength += currentLength;
			// byte[] result =
			// WeChatWavFormat.transferWavFormatToBytes(wavFormat);
			// String fileName = newFolderName + "/" + i + ".wav";
			// FileOutputStream fos = new FileOutputStream(fileName);
			// fos.write(result);
			// fos.close();

			fileName = newFolderName + "/" + i + ".txt";
			bufferedWriter = new BufferedWriter(new FileWriter(new File(fileName)));
			for (int j = 0; j < fractionData.length; j++) {
				bufferedWriter.write("" + fractionData[j] + "\n");
			}
			bufferedWriter.close();
		}
	}

	static public WeChatWavFormat sampling(WeChatWavFormat wavFormat) throws Exception {
		int sampleNum = 500;
		int sampleWindow = 5;

		int[] temp = new int[wavFormat.samples.length];
		System.arraycopy(wavFormat.samples, 0, temp, 0, wavFormat.samples.length);

		wavFormat.samples = new int[sampleNum];
		for (int i = 0; i < sampleNum; i++) {
			int index = (int) (1.0 * i / sampleNum * temp.length);
			int begin = index - sampleWindow / 2;
			if (begin < 0) {
				begin = 0;
			}
			int end = index + sampleWindow / 2;
			if (end >= temp.length) {
				end = temp.length - 1;
			}

			int sum = 0;
			for (int j = begin; j <= end; j++) {
				sum += temp[j];
			}

			wavFormat.samples[i] = sum / (end - begin + 1);
		}

		return wavFormat;
	}

	static public void printSamples(WeChatWavFormat wavFormat, File file1, File file2) throws Exception {
		String newFolderName = outputPath + "/" + file1.getName() + "/" + file2.getName();
		newFolderName = newFolderName.substring(0, newFolderName.length() - 4);
		File file3 = new File(newFolderName);
		if (!file3.exists() || !file3.isDirectory()) {
			file3.mkdir();
		}

		String fileName = newFolderName + "/selected_samples.txt";

		String outputString = "";
		for (int i = 0; i < wavFormat.samples.length; i++) {
			outputString += "" + wavFormat.samples[i] + "\n";
		}

		BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(new File(fileName)));
		bufferedWriter.write(outputString);
		bufferedWriter.close();
	}

	static public boolean checkIsChar(char a) {
		return (a >= 'a' && a <= 'z') || (a >= 'A' && a <= 'Z');
	}

	static public void outputWav(WeChatWavFormat wavFormat, File file1, File file2, int num) throws Exception {
		String newFolderName = outputPath + "/" + file1.getName() + "/" + file2.getName();
		newFolderName = newFolderName.substring(0, newFolderName.length() - 4);
		File file3 = new File(newFolderName);
		if (!file3.exists() || !file3.isDirectory()) {
			file3.mkdir();
		}

		String fileName = newFolderName + "/" + num + ".wav";

		byte[] currentBytes = WeChatWavFormat.transferWavFormatToBytes(wavFormat);
		FileOutputStream fos = new FileOutputStream(fileName);
		fos.write(currentBytes);
		fos.close();
	}

}
