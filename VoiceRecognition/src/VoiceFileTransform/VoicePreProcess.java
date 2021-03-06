package VoiceFileTransform;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.nio.file.Files;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioFormat.Encoding;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

public class VoicePreProcess {
	static private String sourcePath = "/Users/moziliang/Documents/香港留学/2class/1Mutimedia(ROSSITER)/project/voice/all_voice_source";

	static private String outputPath = "/Users/moziliang/Documents/香港留学/2class/1Mutimedia(ROSSITER)/project/voice/all_voice_source_processed";
	
	static public void main(String[] args) throws Exception {
		File sourceFolder = new File(sourcePath);

		for (final File file1 : sourceFolder.listFiles()) {
			if (checkIsChar(file1.getName().charAt(0))) {
				for (final File file2 : file1.listFiles()) {
					if (checkIsChar(file2.getName().charAt(0))) {
						WeChatWavFormat wavFormat = WeChatWavFormat.readAndCheckFile(file2);

						
						
//						String tempPath = outputPath + "/" + file1.getName() + "/" + file2.getName();
						
						
//						int temp[] = new int[wavFormat.samples.length];
//						System.arraycopy(wavFormat.samples, 0, temp, 0, wavFormat.samples.length);
//						wavFormat.samples = new int [wavFormat.samples.length * 10];
//						int i = 0;
//						for (i = 0;i < temp.length - 1; i++) {
//							int difference = temp[i + 1] - temp[i];
//							for (int j = 0; j < 10; j++) {
//								wavFormat.samples[i * 10 + j] = (int)(temp[i] + 1.0 * j / 10 * difference);
//							}
//						}
//						for (int j = 0; j < 10; j++) {
//							wavFormat.samples[i * 10 + j] = temp[i];
//						}
						
						drawGraph(wavFormat, file1, file2, 1);
						wavFormat = Boosting(wavFormat);
						wavFormat = ZeroFilter(wavFormat);
						drawGraph(wavFormat, file1, file2, 2);
//						WeChatWavFormat wavFormat2 = partitionTest(wavFormat);
//						drawGraph(wavFormat2, file1, file2, 3);
						wavFormat = sampling(wavFormat);
						wavFormat = Boosting(wavFormat);
						drawGraph(wavFormat, file1, file2, 4);
						
						printSamples(wavFormat, file1, file2);
						
//						byte[] currentBytes = WeChatWavFormat.transferWavFormatToBytes(wavFormat);
//    					FileOutputStream fos = new FileOutputStream(tempPath);
//    					fos.write(currentBytes);
//    					fos.close();
    					
//    					System.out.println();
					}
				}
			}
		}
	}

	static public void drawGraph(WeChatWavFormat wavFormat, File file1, File file2, int graphNum) throws Exception{

		String newFolderName = outputPath + "/" + file1.getName() + "/" + file2.getName();
		newFolderName = newFolderName.substring(0, newFolderName.length() - 4);
		File file3 = new File(newFolderName);
		if (!file3.exists() || !file3.isDirectory()) {
			file3.mkdir();
		}
		
		int []lineReadings = new int []{-32768, 32768};
		
		String fileName = newFolderName + "/" + graphNum+ ".jpg";
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
			int x = (int)(1.0 * i / wavFormat.samples.length * width);
			g2d.drawLine(x, 0, x, height);
			g2d.setStroke(new BasicStroke(20));
			g2d.drawString("" + i, x, 10);
		}

		for (int i = 0; i < wavFormat.samples.length;i ++) {
			g2d.setColor(Color.BLACK);
			g2d.setStroke(new BasicStroke(10));
			int x = (int)(1.0 * (i + 1) / wavFormat.samples.length * width);
			int y = -wavFormat.samples[i] / 10 + height / 2;
			g2d.fillOval(x, y, 10, 10);
			if (i != wavFormat.samples.length - 1) {
				int nextX = (int)(1.0 * (i + 1 + 1) / wavFormat.samples.length * width);
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
			wavFormat.samples[i] = (int)(1.0 * wavFormat.samples[i] / scale * 32768);
			if (wavFormat.samples[i] > 32767) {
				wavFormat.samples[i] = 32767;
			} else if (wavFormat.samples[i] < -32768) {
				wavFormat.samples[i] = -32768;
			}
		}
		return wavFormat;
	}
	
	static public WeChatWavFormat ZeroFilter(WeChatWavFormat wavFormat) throws Exception{
		int []temp = new int[wavFormat.samples.length];
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
		
		wavFormat.samples = new int [end - begin + 1];
		System.arraycopy(temp, begin, wavFormat.samples, 0, wavFormat.samples.length);
		return wavFormat;
	}
	
	static public WeChatWavFormat partitionTest(WeChatWavFormat wavFormat) throws Exception {
		int []temp = new int[wavFormat.samples.length];
		System.arraycopy(wavFormat.samples, 0, temp, 0, wavFormat.samples.length);

		wavFormat.samples = new int [300];
		System.arraycopy(temp, 10300, wavFormat.samples, 0, wavFormat.samples.length);
		return wavFormat;
	}
	
	static public WeChatWavFormat sampling(WeChatWavFormat wavFormat) throws Exception {
		int sampleNum = 500;
		int sampleWindow = 5;
		
		int []temp = new int[wavFormat.samples.length];
		System.arraycopy(wavFormat.samples, 0, temp, 0, wavFormat.samples.length);

		wavFormat.samples = new int [sampleNum];
		for (int i = 0; i < sampleNum; i++) {
			int index = (int)(1.0 * i / sampleNum * temp.length);
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

		BufferedWriter bufferedWriter =  new BufferedWriter(new FileWriter(new File(fileName)));
		bufferedWriter.write(outputString);
		bufferedWriter.close();
	}
	
	static public boolean checkIsChar(char a) {
		return (a >= 'a' && a <= 'z') || (a >= 'A' && a <= 'Z');
	}
	
}
