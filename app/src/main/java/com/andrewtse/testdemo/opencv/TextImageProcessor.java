package com.andrewtse.testdemo.opencv;

import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

/**
 * @author xk
 * @date 2019/1/19
 */
public class TextImageProcessor {

    private static final String TAG = "TextImageProcessor";

    public static final String FEATURE_FILE_PATH = Environment.getExternalStorageDirectory() + "/tesseract/traindata";

    public int recognitionChar(Mat charImage) {
        String result = "";
        File file = new File(FEATURE_FILE_PATH);
        File[] featureDataFiles = file.listFiles();
        double minDistance = Double.MAX_VALUE;
        float[] fv = extractFeatureData(charImage);
        for (File f : featureDataFiles) {
            double dist = calculateDistance(fv, readFeatureVector(f));
            if (minDistance > dist) {
                minDistance = dist;
                result = f.getName();
            }
        }
        Log.i("OCR-INFO", result);
        return Integer.parseInt(result.substring(0, 1));
    }

    private double calculateDistance(float[] v1, float[] v2) {
        double x = 0, y = 0, z = 0, zf = 0;
        for (int i = 0; i < 40; i++) {
            x = v1[i];
            y = v2[i];
            z = x - y;
            z *= z;
            zf += z;
        }
        return zf;
    }

    private float[] readFeatureVector(File f) {
        float[] fv = new float[40];
        try {
            if (f.exists()) {
                BufferedReader br = new BufferedReader(new FileReader(f));
                for (int i = 0; i < 40; i++) {
                    float currVal = Float.parseFloat(br.readLine());
                    fv[i] = currVal;
                }
            }
        } catch (IOException ioe) {
            Log.i("IO-ERR", ioe.getMessage());
        }
        return fv;
    }

    public void dumpFeature(float[] fv, int textNum) {
        try {
            File file = new File(FEATURE_FILE_PATH + File.separator + "feature_" + textNum + ".txt");
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            if (file.exists()) {
                file.delete();
            }
            if (file.createNewFile()) {
                PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(file)));
                for (int k = 0; k < fv.length; k++) {
                    pw.println(String.valueOf(fv[k]));
                }
                pw.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public float[] extractFeatureData(Mat txtImage) {
        float[] vectordata = new float[40];
        Arrays.fill(vectordata, 0);
        float width = txtImage.cols();
        float height = txtImage.rows();
        byte[] data = new byte[(int) (width * height)];
        txtImage.get(0, 0, data);
        float bins = 10.0f;
        float xstep = width / 4.0f;
        float ystep = height / 5.0f;
        int index = 0;

        // calculate 20 cells of the pixels
        for (float y = 0; y < height; y += ystep) {
            for (float x = 0; x < width; x += xstep) {
                vectordata[index] = getWeightBlackNumber(data, width, height, x, y, xstep, ystep);
                index++;
            }
        }

        // calculate Y Projection
        xstep = width / bins;
        for (float x = 0; x < width; x += xstep) {
            if ((xstep + x) - width > 1)
                continue;
            vectordata[index] = getWeightBlackNumber(data, width, height, x, 0, xstep, height);
            index++;
        }

        // calculate X Projection
        ystep = height / bins;
        for (float y = 0; y < height; y += ystep) {
            if ((y + ystep) - height > 1) continue;
            vectordata[index] = getWeightBlackNumber(data, width, height, 0, y, width, ystep);
            index++;
        }

        // normalization vector data
        float sum = 0;
        for (int i = 0; i < 20; i++) {
            sum += vectordata[i];
        }
        for (int i = 0; i < 20; i++) {
            vectordata[i] = vectordata[i] / sum;
        }

        // Y
        sum = 0;
        for (int i = 20; i < 30; i++) {
            sum += vectordata[i];
        }
        for (int i = 20; i < 30; i++) {
            vectordata[i] = vectordata[i] / sum;
        }

        // X
        sum = 0;
        for (int i = 30; i < 40; i++) {
            sum += vectordata[i];
        }
        for (int i = 30; i < 40; i++) {
            vectordata[i] = vectordata[i] / sum;
        }
        return vectordata;
    }

    private float getWeightBlackNumber(byte[] data, float width, float height, float x, float y, float xstep, float ystep) {
        float weightNum = 0;

        // 整数部分
        int nx = (int) Math.floor(x);
        int ny = (int) Math.floor(y);

        // 小数部分
        float fx = x - nx;
        float fy = y - ny;

        // 宽度与高度
        float w = x + xstep;
        float h = y + ystep;
        if (w > width) {
            w = width - 1;
        }
        if (h > height) {
            h = height - 1;
        }

        // 宽高整数部分
        int nw = (int) Math.floor(w);
        int nh = (int) Math.floor(h);

        // 小数部分
        float fw = w - nw;
        float fh = h - nh;

        // 统计黑色像素个数
        int c = 0;
        int ww = (int) width;
        float weight = 0;
        int row = 0;
        int col = 0;
        for (row = ny; row < nh; row++) {
            for (col = nx; col < nw; col++) {
                c = data[row * ww + col] & 0xff;
                if (c == 0) {
                    weight++;
                }
            }
        }

        // 计算小数部分黑色像素权重加和
        float w1 = 0, w2 = 0, w3 = 0, w4 = 0;
        // calculate w1
        if (fx > 0) {
            col = nx + 1;
            if (col > width - 1) {
                col = col - 1;
            }
            float count = 0;
            for (row = ny; row < nh; row++) {
                c = data[row * ww + col] & 0xff;
                if (c == 0) {
                    count++;
                }
            }
            w1 = count * fx;
        }

        // calculate w2
        if (fy > 0) {
            row = ny + 1;
            if (row > height - 1) {
                row = row - 1;
            }
            float count = 0;
            for (col = nx; col < nw; col++) {
                c = data[row * ww + col] & 0xff;
                if (c == 0) {
                    count++;
                }
            }
            w2 = count * fy;
        }

        if (fw > 0) {
            col = nw + 1;
            if (col > width - 1) {
                col = col - 1;
            }
            float count = 0;
            for (row = ny; row < nh; row++) {
                c = data[row * ww + col] & 0xff;
                if (c == 0) {
                    count++;
                }
            }
            w3 = count * fw;
        }

        if (fh > 0) {
            row = nh + 1;
            if (row > height - 1) {
                row = row - 1;
            }
            float count = 0;
            for (col = nx; col < nw; col++) {
                c = data[row * ww + col] & 0xff;
                if (c == 0) {
                    count++;
                }
            }
            w4 = count * fh;
        }

        weightNum = (weight - w1 - w2 + w3 + w4);
        if (weightNum < 0) {
            weightNum = 0;
        }
        return weightNum;
    }

    /**
     * 卡号分割
     */
    public List<Mat> splitNumberBlock(Mat textImage) {
        List<Mat> numberImgs = new ArrayList<>();
        Mat gray = new Mat();
        Mat binary = new Mat();

        // gray
        Imgproc.cvtColor(textImage, gray, Imgproc.COLOR_BGR2GRAY);

        // binary and noise remove
        Imgproc.threshold(gray, binary, 0, 255, Imgproc.THRESH_BINARY | Imgproc.THRESH_OTSU);
        Mat kernel = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(1.5, 1.5));
        Imgproc.morphologyEx(binary, binary, Imgproc.MORPH_CLOSE, kernel);

        // noise block fill
        List<MatOfPoint> contours = new ArrayList<>();
        Mat hireachy = new Mat();
        Mat contourBin = binary.clone();
        Core.bitwise_not(contourBin, contourBin);
        Imgproc.findContours(contourBin, contours, hireachy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE, new Point(0, 0));
        Log.d(TAG, "splitNumberBlock: contours.size = " + contours.size());
        int minh = binary.rows() / 3;
        Log.d(TAG, "splitNumberBlock: minh = " + minh);
        for (int i = 0; i < contours.size(); i++) {
            Rect roi = Imgproc.boundingRect(contours.get(i));
            Log.d(TAG, "splitNumberBlock: roi.width = " + roi.width + ", roi.height = " + roi.height);
            double area = Imgproc.contourArea(contours.get(i));
            Log.d(TAG, "splitNumberBlock: area = " + area);
            if (area < 80) {
                Imgproc.drawContours(binary, contours, i, new Scalar(255), -1);
                continue;
            }
            if (roi.height <= minh) {
                Imgproc.drawContours(binary, contours, i, new Scalar(255), -1);
                continue;
            }
        }
        // split digit text
        contours.clear();
        binary.copyTo(contourBin);
        Core.bitwise_not(contourBin, contourBin);
        Imgproc.findContours(contourBin, contours, hireachy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE, new Point(0, 0));
        Log.d(TAG, "splitNumberBlock: contours.size = " + contours.size());
        Rect[] textBoxes = new Rect[contours.size()];
        int index = 0;
        int minWidth = 100000;
        Mat contoursImage = new Mat(contourBin.size(), CvType.CV_8UC1);
        contoursImage.setTo(new Scalar(255));
        for (int i = 0; i < contours.size(); i++) {
            Rect bounding = Imgproc.boundingRect(contours.get(i));
            Log.d(TAG, "splitNumberBlock: bounding.width = " + bounding.width);
            minWidth = Math.min(bounding.width, minWidth);
            textBoxes[index++] = bounding;
            Imgproc.drawContours(contoursImage, contours, i, new Scalar(0), 1);
        }
        minWidth = minWidth * 2;
        Log.d(TAG, "splitNumberBlock: minWidth = " + minWidth);

        // sort text sequence
        for (int i = 0; i < textBoxes.length - 1; i++) {
            for (int j = i + 1; j < textBoxes.length; j++) {
                if (textBoxes[i].x > textBoxes[j].x) {
                    Rect temp = textBoxes[j];
                    textBoxes[j] = textBoxes[i];
                    textBoxes[i] = temp;
                }
            }
        }

        // split digit one by one
        for (int k = 0; k < textBoxes.length; k++) {
            Log.d(TAG, "splitNumberBlock: textBoxes[k].width = " + textBoxes[k].width + ", textBoxes[k].height = " + textBoxes[k].height);
            if (textBoxes[k].height <= minh)
                continue;
            if (textBoxes[k].width > minWidth) {
                Mat twoText = new Mat();
                contoursImage.submat(textBoxes[k]).copyTo(twoText);
                int xpos = getSplitLinePos(binary.submat(textBoxes[k]));
                Log.d(TAG, "splitNumberBlock: xpos = " + xpos);
                numberImgs.add(twoText.submat(new Rect(0, 0, xpos - 1, textBoxes[k].height)));
                numberImgs.add(twoText.submat(new Rect(xpos + 1, 0, textBoxes[k].width - xpos - 1, textBoxes[k].height)));
            } else {
                Mat oneText = new Mat();
                contoursImage.submat(textBoxes[k]).copyTo(oneText);
                numberImgs.add(oneText);
            }
        }

        // release memory
        textBoxes = null;
        binary.release();
        gray.release();
        contourBin.release();
        contoursImage.release();
        return numberImgs;
    }

    private int getSplitLinePos(Mat mtexts) {
        int hx = mtexts.cols() / 2;
        int width = mtexts.cols();
        int height = mtexts.rows();
        Log.d(TAG, "getSplitLinePos: mtexts.cols = " + mtexts.cols() + ", mtexts.rows = " + mtexts.rows());
        byte[] data = new byte[width * height];
        mtexts.get(0, 0, data);
        int startx = hx - 3;
        int endx = hx + 3;
        int linepos = hx;
        int min = 1000000;
        int c = 0;
        for (int col = startx; col < endx; col++) {
            int total = 0;
            for (int row = 0; row < height; row++) {
                c = data[row * width + col] & 0xff;
                if (c == 0) {
                    total++;
                }
            }
            if (total < linepos) {
//                linepos = total;
                linepos = col;
            }
        }
        return linepos;
    }

    /**
     * 寻找卡号
     */
    public Mat findCardNumBlock(Mat card) {
        Mat hsv = new Mat();
        Mat binary = new Mat();

        // convert RGB to HSV
        Imgproc.cvtColor(card, hsv, Imgproc.COLOR_BGR2HSV);
        Core.inRange(hsv, new Scalar(30, 40, 45), new Scalar(180, 255, 255), binary);

        // remove noise
        Mat kernel = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(5, 5));
        Imgproc.morphologyEx(binary, binary, Imgproc.MORPH_OPEN, kernel);

        // contours analysis
        List<MatOfPoint> contours = new ArrayList<>();
        Mat hireachy = new Mat();
        Imgproc.findContours(binary, contours, hireachy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE, new Point(0, 0));
        Log.d(TAG, "findCardNumBlock: contours.size = " + contours.size());
        Log.d(TAG, "findCardNumBlock: binary.cols = " + binary.cols() + ", binary.rows = " + binary.rows());
        int offsetx = binary.cols() / 3;
        int offsety = binary.rows() / 3;
        Log.d(TAG, "findCardNumBlock: offsetx = " + offsetx + ", offsety = " + offsety);
        Rect numberROI = new Rect();
        for (int i = 0; i < contours.size(); i++) {
            Rect roi = Imgproc.boundingRect(contours.get(i));
            Log.d(TAG, "findCardNumBlock: roi = " + roi.x + "-" + roi.y + "-" + roi.width + "-" + roi.height);
            if (Imgproc.contourArea(contours.get(i)) < 200) {
                continue;
            }
            if (roi.x < offsetx && roi.y < offsety) {
                numberROI.x = roi.x - 30;
                numberROI.y = roi.y + roi.height / 2 - 30;
                numberROI.width = binary.cols() - roi.x - 30;
                numberROI.height = (int) (roi.height * 0.2);
                Log.d(TAG, "findCardNumBlock: numberROI = " + numberROI.x + "-" + numberROI.y + '-' + numberROI.width + '-' + numberROI.height);
                break;
            }
        }

        if (numberROI.width == 0 || numberROI.height == 0) {
            return null;
        }

        // get number block
        Mat textImage = new Mat();
        card.submat(numberROI).copyTo(textImage);
        card.release();
        hsv.release();
        binary.release();
        return textImage;
    }

    /**
     * 寻找银行卡区域
     */
    public Mat findCard(Bitmap bitmap) {
        // color image - BGR
//        Mat src = Imgcodecs.imread(fileUri.getPath());
        Mat src = new Mat();
        Utils.bitmapToMat(bitmap, src);

        if (src.empty()) {
            Log.d(TAG, "findCard: src == null");
            return null;
        }

        // calculate gradient 梯度求取
        Mat grad_x = new Mat();
        Mat grad_y = new Mat();
        Mat abs_grad_x = new Mat();
        Mat abs_grad_y = new Mat();

        Imgproc.Scharr(src, grad_x, CvType.CV_32F, 1, 0);
        Imgproc.Scharr(src, grad_y, CvType.CV_32F, 0, 1);
        Core.convertScaleAbs(grad_x, abs_grad_x);
        Core.convertScaleAbs(grad_y, abs_grad_y);
        grad_x.release();
        grad_y.release();

        Mat grad = new Mat();
        Core.addWeighted(abs_grad_x, 0.5, abs_grad_y, 0.5, 0, grad);
        abs_grad_x.release();
        abs_grad_y.release();

        // binary image 二值化
        Mat binary = new Mat();
        Imgproc.cvtColor(grad, binary, Imgproc.COLOR_BGR2GRAY);//灰度图片
        Imgproc.threshold(binary, binary, 40, 255, Imgproc.THRESH_BINARY);
        grad.release();

        // 去除噪声
        Mat kernel = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(3, 3));
        Imgproc.morphologyEx(binary, binary, Imgproc.MORPH_OPEN, kernel);

        // 轮廓发现与面积大小过滤
        List<MatOfPoint> contours = new ArrayList<>();
        Mat hireachy = new Mat();
        Imgproc.findContours(binary, contours, hireachy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE, new Point(0, 0));
        int hw = binary.cols() / 2;
        int hh = binary.rows() / 2;
        Rect roi = new Rect();
        for (int i = 0; i < contours.size(); i++) {
            Rect rect = Imgproc.boundingRect(contours.get(i));
            if (rect.width > hw) {
                roi.x = rect.x;
                roi.y = rect.y;
                roi.width = rect.width;
                roi.height = rect.height;
                break;
            }
        }
        if (roi.width == 0 || roi.height == 0) {
            return null;
        }
        Mat card = new Mat();
        src.submat(roi).copyTo(card);
        src.release();
        binary.release();
        return card;
    }
}
