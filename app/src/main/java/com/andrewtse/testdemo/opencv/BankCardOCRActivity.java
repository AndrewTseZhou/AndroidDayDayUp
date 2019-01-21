package com.andrewtse.testdemo.opencv;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.andrewtse.testdemo.R;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

public class BankCardOCRActivity extends AppCompatActivity {

    private static final String TAG = "BankCardOCRActivity";

    @BindView(R.id.btn_take_photo)
    Button mBtnTakePhoto;
    @BindView(R.id.btn_recognition)
    Button mBtnRecognition;
    @BindView(R.id.btn_to_gray)
    Button mBtnGray;
    @BindView(R.id.btn_clear)
    Button mBtnClear;
    @BindView(R.id.tv_result)
    TextView mTvResult;
    @BindView(R.id.iv_src)
    ImageView mIvSrc;

    private Uri fileUri;
    private int REQUEST_CAPTURE_IMAGE = 1;
    private TextImageProcessor processor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bank_card_ocr);
        ButterKnife.bind(this);

        processor = new TextImageProcessor();
    }

    @OnClick({R.id.btn_take_photo, R.id.btn_to_gray, R.id.btn_recognition, R.id.btn_clear})
    public void onViewClick(View view) {
        switch (view.getId()) {
            case R.id.btn_take_photo:
//                start2Camera();
                pickUpImage();
                break;
            case R.id.btn_to_gray:
                convertToGrayImage();
                break;
            case R.id.btn_recognition:
                processImage();
                break;
            case R.id.btn_clear:
                clearOldData();
                break;
        }
    }

    private void start2Camera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        fileUri = FileProvider.getUriForFile(this, getPackageName() + ".provider", getSaveFilePath());
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
        startActivityForResult(intent, REQUEST_CAPTURE_IMAGE);
    }

    private File getSaveFilePath() {
        String status = Environment.getExternalStorageState();
        if (!status.equals(Environment.MEDIA_MOUNTED)) {
            Log.i(TAG, "SD Card is not suitable...");
            return null;
        }
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd_hhmmss");
        String name = df.format(new Date(System.currentTimeMillis())) + ".jpg";
        File filedir = new File(Environment.getExternalStorageDirectory(), "myOcrImages");
        filedir.mkdirs();
        String fileName = filedir.getAbsolutePath() + File.separator + name;
        return new File(fileName);
    }

    private void pickUpImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "图像选择..."), REQUEST_CAPTURE_IMAGE);
    }

    private String getRealPath(Uri uri) {
        String filePath = null;
        String wholeID = DocumentsContract.getDocumentId(uri);
        String id = wholeID.split(":")[1];
        String[] column = {MediaStore.Images.Media.DATA};
        String sel = MediaStore.Images.Media._ID + "=?";
        Cursor cursor = getApplicationContext().getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, column,
                                                                           sel, new String[]{id}, null);
        int columnIndex = cursor.getColumnIndex(column[0]);
        if (cursor.moveToFirst()) {
            filePath = cursor.getString(columnIndex);
        }
        cursor.close();
        Log.i("CV_TAG", "selected image path : " + filePath);
        return filePath;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CAPTURE_IMAGE && resultCode == RESULT_OK) {
            if (data == null) {
                Log.d(TAG, "onActivityResult: data == null");
//                displaySelectedImage();
                display();
            } else {
                Uri uri = data.getData();
                File f = new File(getRealPath(uri));
                fileUri = FileProvider.getUriForFile(this, getPackageName() + ".provider", f);
                display();
            }
        }
    }

    private void display() {
        try {
            Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(fileUri));
            mIvSrc.setImageBitmap(bitmap);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void displaySelectedImage() {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(fileUri.getPath(), options);
        int w = options.outWidth;
        int h = options.outHeight;
        int inSample = 1;
        if (w > 1000 || h > 1000) {
            while (Math.max(w / inSample, h / inSample) > 1000) {
                inSample *= 2;
            }
        }
        options.inJustDecodeBounds = false;
        options.inSampleSize = inSample;
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Bitmap bm = BitmapFactory.decodeFile(fileUri.getPath(), options);
        mIvSrc.setImageBitmap(bm);
    }

    private void processImage() {
        Bitmap bm = null;
        try {
            bm = BitmapFactory.decodeStream(getContentResolver().openInputStream(fileUri));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        Mat card = processor.findCard(bm);
        if (card == null) {
            Log.d(TAG, "processImage: card == null");
            return;
        }

        Mat numImage = processor.findCardNumBlock(card);
        if (numImage == null) {
            Log.d(TAG, "processImage: numImage == null");
            return;
        }

        List<Mat> textList = processor.splitNumberBlock(numImage);
        if (textList != null && textList.size() > 0) {
            Log.i(TAG, "processImage Number of digits : " + textList.size());
            StringBuilder cardId = new StringBuilder();
            for (Mat oneText : textList) {
                Bitmap bitmap = Bitmap.createBitmap(oneText.cols(), oneText.rows(), Bitmap.Config.ARGB_8888);
                Utils.matToBitmap(oneText, bitmap);
                saveDebugImage(bitmap);

                int digit = processor.recognitionChar(oneText);
                if (digit == 0 || digit == 1) {
                    float w = oneText.cols();
                    float h = oneText.rows();
                    float rate = w / h;
                    if (rate >= 0.6) {
                        digit = 0;
                    } else {
                        digit = 1;
                    }
                }
                cardId.append(digit);
                oneText.release();
            }
            Log.i("OCR-INFO", "Card Number : " + cardId);
            mTvResult.setText("识别结果：" + cardId);
        }
        Log.i(TAG, "processImage Find Card and Card Number Block...");
        Bitmap bitmap = Bitmap.createBitmap(numImage.cols(), numImage.rows(), Bitmap.Config.ARGB_8888);
//        Imgproc.cvtColor(numImage, numImage, Imgproc.COLOR_BGR2RGBA);
        Utils.matToBitmap(numImage, bitmap);
        saveDebugImage(bitmap);
        mIvSrc.setImageBitmap(bitmap);
    }

    private void saveDebugImage(Bitmap bitmap) {
        File filedir = new File(Environment.getExternalStorageDirectory(), "myOcrImages");
        String name = String.valueOf(System.currentTimeMillis()) + "_ocr.jpg";
        File tempFile = new File(filedir.getAbsoluteFile() + File.separator, name);
        FileOutputStream output = null;
        try {
            output = new FileOutputStream(tempFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, output);
        } catch (IOException ioe) {
            Log.e("DEBUG-ERR", ioe.getMessage());
        } finally {
            try {
                output.flush();
                output.close();
            } catch (IOException e) {
                Log.i("DEBUG-INFO", e.getMessage());
            }
        }
    }

    /**
     * 灰度处理
     */
    private void convertToGrayImage() {
        Bitmap bm = ((BitmapDrawable) mIvSrc.getDrawable()).getBitmap();

        Mat src = new Mat();
        Utils.bitmapToMat(bm, src);
        Mat gray = new Mat();
        Imgproc.cvtColor(src, gray, Imgproc.COLOR_BGRA2GRAY);
        int width = gray.cols();
        int height = gray.rows();
        byte[] data = new byte[width * height];
        gray.get(0, 0, data);
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                data[row * width + col] = (byte) (~data[row * width + col]);
            }
        }
        gray.put(0, 0, data);
        Utils.matToBitmap(gray, bm);
        mIvSrc.setImageBitmap(bm);
        gray.release();
        src.release();
    }

    private void clearOldData() {
        File file = new File(Environment.getExternalStorageDirectory(), "myOcrImages");
        File[] files = file.listFiles();
        for (int i = 0; i < files.length; i++) {
            if (files[i].isFile() && files[i].getName().endsWith("ocr.jpg")) {
                files[i].delete();
            }
        }
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file));
        sendBroadcast(mediaScanIntent);
    }
}
