package io.scanbot.example.sdk.barcode.java.android;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;

import io.scanbot.sdk.barcode.entity.BarcodeFormat;
import io.scanbot.sdk.barcode.entity.BarcodeScanningResult;
import io.scanbot.sdk.ui.barcode_scanner.view.barcode.BarcodeScannerActivity;
import io.scanbot.sdk.ui.view.barcode.configuration.BarcodeScannerConfiguration;
import io.scanbot.sdk.ui.view.base.RtuConstants;

public class MainActivity extends AppCompatActivity {

    private ActivityResultLauncher<BarcodeScannerConfiguration> rtuUiResultLauncher;
    private TextView resultsTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rtuUiResultLauncher = registerForActivityResult(new BarcodeScannerActivity.ResultContract(),
                new ActivityResultCallback<BarcodeScannerActivity.Result>() {
                    @Override
                    public void onActivityResult(BarcodeScannerActivity.Result barcodeResults) {
                        // Handle the returned barcodes
                        if (barcodeResults.getResultOk()) {
                            handleBarcodeResults(barcodeResults.getResult());
                        } else {
                            // user has canceled the RTU UI Activity
                        }
                    }
                });

        findViewById(R.id.btn_rtu_ui_scanner).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // prepare the RTU UI configs
                final BarcodeScannerConfiguration configs = new BarcodeScannerConfiguration();
                configs.setCancelButtonTitle("Abort");
                configs.setFinderLineColor(Color.GREEN);
                configs.setTopBarBackgroundColor(Color.BLUE);
                /* how to set a filter for barcode formats:
                final ArrayList formatsFilter = new ArrayList<BarcodeFormat>();
                formatsFilter.add(BarcodeFormat.EAN_8);
                formatsFilter.add(BarcodeFormat.EAN_13);
                formatsFilter.add(BarcodeFormat.QR_CODE);
                configs.setBarcodeFormatsFilter(formatsFilter);
                configs.setFlashEnabled(true);
                */
                // see further configs...

                // and launch the RTU UI Activity...
                // via new AndroidX Result API:
                rtuUiResultLauncher.launch(configs);
                /* or alternatively via deprecated startActivityForResult(..) API:
                final Intent intent = BarcodeScannerActivity.newIntent(MainActivity.this, configs);
                startActivityForResult(intent, 4711);
                */
            }
        });

        findViewById(R.id.btn_custom_scanner).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // launch the custom Activity build with Scanbot's Classical Components
                final Intent intent = new Intent(MainActivity.this, CustomBarcodeScannerActivity.class);
                startActivityForResult(intent, 4712);
            }
        });

        resultsTextView = findViewById(R.id.txt_view_results);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 4712 && resultCode == RESULT_OK) {
            // handle the scanner result from the CustomBarcodeScannerActivity
            final BarcodeScanningResult result = data.getParcelableExtra(CustomBarcodeScannerActivity.EXTRA_KEY_CUSTOM_BARCODE_SCANNER_RESULT);
            handleBarcodeResults(result);
        }

        /* disabled due to demonstration of the registerForActivityResult API, see rtuUiResultLauncher above
        if (requestCode == 4711 && resultCode == RESULT_OK) {
            // handle the scanner result from the CustomBarcodeScannerActivity
            final BarcodeScanningResult result = data.getParcelableExtra(RtuConstants.EXTRA_KEY_RTU_RESULT);
            handleBarcodeResults(result);
        }
        */
    }

    private void handleBarcodeResults(final BarcodeScanningResult result) {
        // for simplicity we only present the first detected barcode from the list: result.getBarcodeItems().get(0)
        // you can iterate through the list "getBarcodeItems()" and handle all detected barcoded
        final String value = result.getBarcodeItems().get(0).getText();
        final String type = result.getBarcodeItems().get(0).getBarcodeFormat().name();
        resultsTextView.setText("Type: " + type + "\n" + "Value: " + value);
    }
}