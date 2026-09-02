package com.example.rfidreader;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

// 注意：請根據您 SDK 包中 JAR 的實際套件名稱匯入 Reader 類別
import com.uhf.api.cls.Reader;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "RFID_Reader";
    private Reader mReader;
    private boolean isScanning = false;

    private TextView txtRawHex;
    private TextView txtAsciiResult;
    private Button btnScan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtRawHex = findViewById(R.id.txtRawHex);
        txtAsciiResult = findViewById(R.id.txtAsciiResult);
        btnScan = findViewById(R.id.btnScan);

        // 初始化 RFID 硬體模組
        initRfidReader();

        // 綁定按鈕點擊事件
        btnScan.setOnClickListener(v -> {
            if (!isScanning) {
                startScanning();
            } else {
                stopScanning();
            }
        });
    }

    private void initRfidReader() {
        try {
            mReader = new Reader();
            // Wyuan PDA3109 內部 RFID 串口通常為 /dev/ttyMT1，波特率 115200
            int result = mReader.InitReader_Serial("/dev/ttyMT1", 115200);
            if (result == 0) {
                Toast.makeText(this, "RFID 模組連接成功！", Toast.LENGTH_SHORT).show();
            } else {
                Log.e(TAG, "RFID 初始化失敗，代碼: " + result);
            }
        } catch (Exception e) {
            Log.e(TAG, "初始化異常: " + e.getMessage());
        }
    }

    private void startScanning() {
        if (mReader == null) return;
        isScanning = true;
        btnScan.setText("停止盤點");

        new Thread(() -> {
            while (isScanning) {
                // 讀取標籤 EPC
                String rawEpcHex = mReader.readTagEpc(); 
                
                if (rawEpcHex != null && !rawEpcHex.trim().isEmpty()) {
                    // 執行 Hex 轉 ASCII 解碼
                    String asciiDecoded = RfidDecoder.hexToAscii(rawEpcHex);

                    // 主執行緒更新畫面 UI
                    runOnUiThread(() -> {
                        txtRawHex.setText("原始 Hex: " + rawEpcHex);
                        txtAsciiResult.setText("解碼 ASCII: " + asciiDecoded);
                    });
                }
                
                try {
                    Thread.sleep(50); // 控制讀取間隔
                } catch (InterruptedException e) {
                    break;
                }
            }
        }).start();
    }

    private void stopScanning() {
        isScanning = false;
        btnScan.setText("開始讀取 RFID");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopScanning();
        if (mReader != null) {
            mReader.CloseReader(); // 釋放串口資源
        }
    }
}
