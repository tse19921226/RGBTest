package com.example.peter.rgbtest;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
//import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.charts.LineChart;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    //private String TAG = "RGBTest";
    //UI
    private ImageView imageView;
    private Button btn_selectImage, btn_compute,btn_Ydraw;
    private ProgressBar progressBar;
    private RelativeLayout Y_Layout, R_Layout, G_Layout, B_Layout;
    private TextView R_G,B_G;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        id_init();
        btn_selectImage.setOnClickListener(btn_onClick);
        btn_compute.setOnClickListener(btn_onClick);
        btn_Ydraw.setOnClickListener(btn_onClick);
        thred = new Thread();
    }

    private void id_init() {
        imageView = (ImageView) findViewById(R.id.imageView);
        btn_selectImage = (Button) findViewById(R.id.selectImage);
        btn_compute = (Button) findViewById(R.id.compute);
        btn_Ydraw = (Button) findViewById(R.id.Y_draw);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        Y_Layout = (RelativeLayout) findViewById(R.id.YLayout);
        R_Layout = (RelativeLayout) findViewById(R.id.RLayout);
        G_Layout = (RelativeLayout) findViewById(R.id.GLayout);
        B_Layout = (RelativeLayout) findViewById(R.id.BLayout);
        R_G = (TextView) findViewById(R.id.R_Gain);
        B_G = (TextView) findViewById(R.id.B_Gain);
    }

    private Button.OnClickListener btn_onClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.selectImage:
                    intentData();
                    break;
                case R.id.compute:
                    computeData();
                    break;
                case R.id.Y_draw:
                    R_Layout.removeAllViews();
                    G_Layout.removeAllViews();
                    B_Layout.removeAllViews();
                    Y_Layout.removeAllViews();

                    drawLineChart("R統計圖",R_Layout,"R",r_array);
                    drawLineChart("G統計圖",G_Layout,"G",g_array);
                    drawLineChart("B統計圖",B_Layout,"B",b_array);
                    drawLineChart("Y統計圖",Y_Layout,"Y",y_array);

                    R_G.setText("RG0 : " + String.valueOf(RG0) + " , " + "RG1 : " + String.valueOf(RG1)
                            + " , " + "RG2 : " + String.valueOf(RG2) + " , " + "RG3 : " + String.valueOf(RG3)
                            + " , " + "RG4 : " + String.valueOf(RG4));
                    B_G.setText("BG0 : " + String.valueOf(BG0) + " , " + "BG1 : " + String.valueOf(BG1)
                            + " , " + "BG2 : " + String.valueOf(BG2) + " , " + "BG3 : " + String.valueOf(BG3)
                            + " , " + "BG4 : " + String.valueOf(BG4));
                    break;
            }
        }
    };

    public void drawLineChart(String Description, RelativeLayout layout, String LineName,
                              double[] array){
        LineChart lineChart = new LineChart(MainActivity.this);
        lineChart.setDescription(Description);
        layout.addView(lineChart);
        LineDataSet lineDataSet = new LineDataSet(getChartData(array),LineName);
        LineData data = new LineData(getLabels(),lineDataSet);
        lineChart.setData(data);
        lineChart.invalidate();
    }

    public final int DATA_COUNT = 256;

    public List<Entry> getChartData(double[] arrays){

        List<Entry> chartData = new ArrayList<>();
        for (int i = 0; i < DATA_COUNT; i++){
            chartData.add(new Entry((float) arrays[i], i));
        }
        return chartData;
    }

    private List<String> getLabels(){
        List<String> chartLabels = new ArrayList<>();
        for (int i = 0; i < DATA_COUNT; i++){
            chartLabels.add(String.valueOf(i));
        }
        return chartLabels;
    }

    private int INTENT_IMAGE = 0;
    private Bitmap bitmap;

    private void intentData(){
        if (bitmap != null){
            bitmap.recycle();
        }
        Intent getImage_intent = new Intent();
        getImage_intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
        getImage_intent.setType("image/*");
        startActivityForResult(getImage_intent,INTENT_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == INTENT_IMAGE){
            Uri imageUri = data.getData();
            imageView.setImageURI(imageUri);
        }
    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            progressBar.incrementProgressBy(1);
        }
    };

    private Thread thred;
    private int bitmapWidth, bitmapHeight;
    private int corner_w1,corner_h1,corner_w2,corner_h2;
    private int center_w1,center_h1,center_h2,center_w2;
    private double RG0, RG1, RG2 ,RG3 ,RG4, BG0, BG1, BG2, BG3, BG4;
    private double[]r_array, g_array, b_array, y_array;

    private void computeData(){
        thred.start();
        bitmap = ((BitmapDrawable)imageView.getDrawable()).getBitmap();
        bitmapWidth = bitmap.getWidth();
        bitmapHeight = bitmap.getHeight();
        corner_h1 = bitmapHeight/10;
        corner_w1 = bitmapWidth/10;
        corner_h2 = bitmapHeight*9/10;
        corner_w2 = bitmapWidth*9/10;
        center_h1 = bitmapHeight/2 - corner_h1/2;
        center_w1 = bitmapWidth/2 - corner_w1/2;
        center_h2 = bitmapHeight/2 + corner_h1/2;
        center_w2 = bitmapWidth/2 + corner_w1/2;

        progressBar.setMax(corner_h1*corner_w1*5);

        r_array = new double[256];
        g_array = new double[256];
        b_array = new double[256];
        y_array = new double[256];
        progressBar.setProgress(0);

        thred = new Thread(new Runnable() {
            @Override
            public void run() {
                if (bitmap != null){
                    //中心
                    pixelToRGB(center_w1,center_w2,center_h1,center_h2);
                    RG0 = Gain[0];
                    BG0 = Gain[1];

                    //左上
                    pixelToRGB(0,corner_w1,0,corner_h1);
                    RG1 = Gain[0]/RG0;
                    BG1 = Gain[1]/BG0;

                    //右上
                    pixelToRGB(corner_w2, bitmapWidth, 0, corner_h1);
                    RG2 = Gain[0]/RG0;
                    BG2 = Gain[1]/BG0;

                    //左下
                    pixelToRGB(0, corner_w1, corner_h2, bitmapHeight);
                    RG3 = Gain[0]/RG0;
                    BG3 = Gain[1]/BG0;

                    //右下
                    pixelToRGB(corner_w2, bitmapWidth, corner_h2 , bitmapHeight);
                    RG4 = Gain[0]/RG0;
                    BG4 = Gain[1]/BG0;

                }else {
                    Toast.makeText(MainActivity.this,"no bitmap",Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private float[]Gain;

    public float[] pixelToRGB(int cornerX1, int cornerX2, int cornerY1, int cornerY2){
        float totalR = 0;
        float totalG = 0;
        float totalB = 0;
        Gain = new float[2];
        for (int h = cornerY1;h<cornerY2;h++){
            for (int w = cornerX1;w<cornerX2;w++){
                try {
                    Thread.sleep(0);
                    int bitmapColors = bitmap.getPixel(w, h);
                    int r = Color.red(bitmapColors);
                    int g = Color.green(bitmapColors);
                    int b = Color.blue(bitmapColors);
                    int y = (int) Math.round(r * 0.299 + g * 0.587 + b * 0.114);
                    y_array[y] = y_array[y] + 1;
                    r_array[r] = r_array[r] + 1;
                    g_array[g] = g_array[g] + 1;
                    b_array[b] = b_array[b] + 1;
                    totalR += r;
                    totalB += b;
                    totalG += g;
                    Bundle bundle = new Bundle();
                    Message msg = new Message();
                    msg.setData(bundle);
                    handler.sendMessage(msg);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        float RG = totalR / totalG;
        float BG = totalB / totalG;
        Gain[0] = RG;
        Gain[1] = BG;

        return Gain;
    }
}