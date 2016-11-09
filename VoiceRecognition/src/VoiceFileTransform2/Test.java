package VoiceFileTransform2;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;

import javax.imageio.ImageIO;

public class Test {

	static public void main(String[] args) throws Exception {
		String filePath = "/Users/moziliang/Documents/香港留学/2class/1Mutimedia(ROSSITER)/project/voice/all_voice_source_processed/down/msg_001519110516c26f03d0772106/selected_samples.txt";
		BufferedReader bufferedReader = new BufferedReader(new FileReader(new File(filePath)));
		String line = null;
		Complex[] values = new Complex[512];
		int index = 0;
		while ((line = bufferedReader.readLine()) != null) {
			int value = Integer.parseInt(line);
			Complex complex = new Complex(value);
			values[index++] = complex;
		}
		bufferedReader.close();
		while (index != 512) {
			values[index++] = new Complex(0);
		}
		System.out.println("length: " + index);
		Complex[] result = fft(values);
		System.out.println("length: " + result.length);
		int sample[] = new int[result.length];
		int maximum = 0;

		for (int i = 0; i < result.length; i++) {
			sample[i] = (int) result[i].re();
			if (Math.abs(sample[i]) > maximum) {
				maximum = Math.abs(sample[i]);
			}
		}

		int width = result.length * 3;
		int height = maximum * 2 / 1000;
		BufferedImage bimg = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		Graphics2D g2d = (Graphics2D) bimg.getGraphics();
		g2d.setBackground(Color.WHITE);
		g2d.clearRect(0, 0, width, height);

		g2d.setColor(Color.BLUE);
		for (int i = -height / 2; i <= height / 2; i += height / 10) {
			g2d.setStroke(new BasicStroke(1));
			int y = -i + height / 2;
			g2d.drawLine(0, y, width, y);
			g2d.setStroke(new BasicStroke(20));
			g2d.drawString("" + i, 0, y);
		}
		for (int i = 0; i <= width; i += width / 20) {
			g2d.setStroke(new BasicStroke(1));
			int x = (int) (1.0 * i);
			g2d.drawLine(x, 0, x, height);
			g2d.setStroke(new BasicStroke(20));
			g2d.drawString("" + i, x, 10);
		}

		for (int i = 0; i < sample.length; i++) {
			g2d.setColor(Color.BLACK);
			g2d.setStroke(new BasicStroke(5));
			int x = (int) (1.0 * (i + 1) / sample.length * width);
			int y = -sample[i] / 1000 + height / 2;
//			g2d.fillOval(x, y, 10, 10);
			if (i != sample.length - 1) {
				int nextX = (int) (1.0 * (i + 1 + 1) / sample.length * width);
				int nextY = -sample[i + 1] / 1000 + height / 2;
				g2d.drawLine(x, y, nextX, nextY);
			}
		}
		String fileName = "/Users/moziliang/Documents/香港留学/2class/1Mutimedia(ROSSITER)/project/voice/all_voice_source_processed/down/msg_001519110516c26f03d0772106/selected_samples.jpg";

		ImageIO.write(bimg, "JPG", new FileOutputStream(fileName));
	}

	static public Complex[] fft(Complex[] x) {
		int N = x.length;

		// base case
		if (N == 1)
			return new Complex[] { x[0] };

		// radix 2 Cooley-Tukey FFT
		if (N % 2 != 0) {
			throw new RuntimeException("N is not a power of 2");
		}

		// fft of even terms
		Complex[] even = new Complex[N / 2];
		for (int k = 0; k < N / 2; k++) {
			even[k] = x[2 * k];
		}
		Complex[] q = fft(even);

		// fft of odd terms
		Complex[] odd = even; // reuse the array
		for (int k = 0; k < N / 2; k++) {
			odd[k] = x[2 * k + 1];
		}
		Complex[] r = fft(odd);

		// combine
		Complex[] y = new Complex[N];
		for (int k = 0; k < N / 2; k++) {
			double kth = -2 * k * Math.PI / N;
			Complex wk = new Complex(Math.cos(kth), Math.sin(kth));
			y[k] = q[k].add(wk.mult(r[k]));
			y[k + N / 2] = q[k].minus(wk.mult(r[k]));
		}
		return y;
	}

	// /**
	// * 一维快速傅里叶变换
	// *
	// * @param values
	// * 一维复数集数组
	// * @return 傅里叶变换后的数组集
	// */
	// static public Complex[] fft(Complex[] values) {
	// int n = values.length;
	// int r = (int) (Math.log10(n) / Math.log10(2)); // 求迭代次数r
	// Complex[][] temp = new Complex[r + 1][n]; // 计算过程的临时矩阵
	// Complex w = new Complex(); // 权系数
	// temp[0] = values;
	// int x1, x2; // 一对对偶结点的下标值
	// int p, t; // p表示加权系数Wpn的p值, t是重新排序后对应的序数值
	// for (int l = 1; l <= r; l++) {
	// if (l != r) {
	// for (int k = 0; k < n; k++) {
	// if (k < n / Math.pow(2, l)) {
	// x1 = k;
	// x2 = x1 + (int) (n / Math.pow(2, l));
	// } else {
	// x2 = k;
	// x1 = x2 - (int) (n / Math.pow(2, l));
	// }
	// p = getWeight(k, l, r);
	// // xi(j) = temp[i-1][x1] + Wpn* temp[i-1][x2];
	// w.setX(Math.cos(-2 * Math.PI * p / n));
	// w.setY(Math.sin(-2 * Math.PI * p / n));
	// temp[l][k] = temp[l - 1][x1].add(w.mult(temp[l - 1][x2]));
	//
	// }
	// } else {
	// for (int k = 0; k < n / 2; k++) {
	// x1 = 2 * k;
	// x2 = 2 * k + 1;
	// // System.out.println("x1:" + x1 + " x2:" + x2);
	// t = reverseRatio(2 * k, r);
	// p = t;
	// w.setX(Math.cos(-2 * Math.PI * p / n));
	// w.setY(Math.sin(-2 * Math.PI * p / n));
	// temp[l][t] = temp[l - 1][x1].add(w.mult(temp[l - 1][x2]));
	// t = reverseRatio(2 * k + 1, r);
	// p = t;
	// w.setX(Math.cos(-2 * Math.PI * p / n));
	// w.setY(Math.sin(-2 * Math.PI * p / n));
	// temp[l][t] = temp[l - 1][x1].add(w.mult(temp[l - 1][x2]));
	// }
	// }
	// }
	// return temp[r];
	// }
	//
	// static private int reverseRatio(int k, int r) {
	// int n = 0;
	// StringBuilder sb = new StringBuilder(Integer.toBinaryString(k));
	// StringBuilder sb2 = new StringBuilder("");
	// if (sb.length() < r) {
	// n = r - sb.length();
	// for (int i = 0; i < n; i++) {
	// sb.insert(0, "0");
	// }
	// }
	//
	// for (int i = 0; i < sb.length(); i++) {
	// sb2.append(sb.charAt(sb.length() - i - 1));
	// }
	// return Integer.parseInt(sb2.toString(), 2);
	// }
	//
	// static private int getWeight(int k, int l, int r) {
	// int d = r - l; // 位移量
	// k = k >> d;
	// return reverseRatio(k, r);
	// }
}