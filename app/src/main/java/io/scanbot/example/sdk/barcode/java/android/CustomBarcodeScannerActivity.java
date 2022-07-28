package io.scanbot.example.sdk.barcode.java.android;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

import io.scanbot.sdk.SdkLicenseError;
import io.scanbot.sdk.barcode.BarcodeDetectorFrameHandler;
import io.scanbot.sdk.barcode.ScanbotBarcodeDetector;
import io.scanbot.sdk.barcode.entity.BarcodeFormat;
import io.scanbot.sdk.barcode.entity.BarcodeScannerConfigBuilder;
import io.scanbot.sdk.barcode.entity.BarcodeScanningResult;
import io.scanbot.sdk.barcode_scanner.ScanbotBarcodeScannerSDK;
import io.scanbot.sdk.camera.CameraModule;
import io.scanbot.sdk.camera.CameraOpenCallback;
import io.scanbot.sdk.camera.FrameHandlerResult;
import io.scanbot.sdk.ui.camera.FinderAspectRatio;
import io.scanbot.sdk.ui.camera.FinderOverlayView;
import io.scanbot.sdk.ui.camera.ScanbotCameraXView;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;

/**
 * An example for a custom scanner Activity that uses the Scanbot SDK Classic Components
 * (ScanbotCameraXView, ScanbotBarcodeDetector, BarcodeDetectorFrameHandler, etc).
 * Classic Components provide the most flexibility and customization options.
 */
public class CustomBarcodeScannerActivity extends AppCompatActivity implements BarcodeDetectorFrameHandler.ResultHandler {

    public static final String EXTRA_KEY_CUSTOM_BARCODE_SCANNER_RESULT = "CUSTOM_BARCODE_SCANNER_RESULT";

    private ScanbotCameraXView cameraView;
    private FinderOverlayView finderOverlayView;
    private CameraModule cameraModule = CameraModule.BACK;
    private boolean flashEnabled = false;
    private BarcodeDetectorFrameHandler barcodeDetectorFrameHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_scanner);

        cameraView = findViewById(R.id.camera);
        cameraView.setCameraModule(cameraModule);

        findViewById(R.id.btn_flash).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                flashEnabled = !flashEnabled;
                cameraView.useFlash(flashEnabled);
            }
        });

        findViewById(R.id.btn_cam_switch).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cameraModule = (cameraModule == CameraModule.BACK ? CameraModule.FRONT_MIRRORED : CameraModule.BACK);
                cameraView.setCameraModule(cameraModule);
                cameraView.restartPreview();
            }
        });

        cameraView.setCameraOpenCallback(new CameraOpenCallback() {
            @Override
            public void onCameraOpened() {
                cameraView.postDelayed(() -> {
                    cameraView.useFlash(flashEnabled);
                    cameraView.continuousFocus();
                }, 300);
            }
        });

        final ScanbotBarcodeDetector barcodeDetector = new ScanbotBarcodeScannerSDK(this).createBarcodeDetector();
        /* how to set a filter for barcode formats:
        final ArrayList<BarcodeFormat> formatsFilter = new ArrayList<>();
        formatsFilter.add(BarcodeFormat.EAN_8);
        formatsFilter.add(BarcodeFormat.EAN_13);
        formatsFilter.add(BarcodeFormat.QR_CODE);
        barcodeDetector.modifyConfig(barcodeScannerConfigBuilder -> {
            barcodeScannerConfigBuilder.setBarcodeFormats(formatsFilter);
            return null;
        });
        */
        barcodeDetectorFrameHandler = BarcodeDetectorFrameHandler.attach(cameraView, barcodeDetector);
        barcodeDetectorFrameHandler.setDetectionInterval(500);
        barcodeDetectorFrameHandler.addResultHandler(this);

        finderOverlayView = findViewById(R.id.my_finder_overlay);
        final ArrayList<FinderAspectRatio> aspectRatios = new ArrayList<>();
        aspectRatios.add(new FinderAspectRatio(300, 150) );
        finderOverlayView.setRequiredAspectRatios(aspectRatios);
    }

    @Override
    protected void onResume() {
        super.onResume();
        cameraView.startPreview();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            // Use onActivityResult to handle permission rejection
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 200);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        cameraView.stopPreview();
    }

    @Override
    public boolean handle(@NonNull FrameHandlerResult<? extends BarcodeScanningResult, ? extends SdkLicenseError> frameHandlerResult) {
        final BarcodeScanningResult result = ((FrameHandlerResult.Success<BarcodeScanningResult>) frameHandlerResult).getValue();
        if (frameHandlerResult instanceof FrameHandlerResult.Success && result != null) {
            // stop detection and return results
            barcodeDetectorFrameHandler.setEnabled(false);
            final Intent data = new Intent();
            data.putExtra(EXTRA_KEY_CUSTOM_BARCODE_SCANNER_RESULT, result);
            setResult(RESULT_OK, data);
            finish();
        }
        return false;
    }
}
