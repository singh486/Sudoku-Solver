package com.example.jasminesingh.sudokusolver;


import android.app.Dialog;
import android.app.Fragment;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.Text;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import static android.app.Activity.RESULT_OK;

public class Solve extends Fragment {
    private Button btnCamera;
    private ImageView capturedImage;
    private Button btnNext;
    private TextView result;
    Bitmap bp;
    String imageText = "";
    File imgFile;


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getActivity().setTitle("Solve");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.solve, container, false);

        btnCamera = (Button) v.findViewById(R.id.btnCamera);

        capturedImage= (ImageView) v.findViewById(R.id.capturedImage);

        btnNext = (Button) v.findViewById(R.id.btnNext);



        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog alert = new Dialog(getContext());
                alert.setContentView(R.layout.alert);

                result = (TextView) alert.findViewById(R.id.result);

                DisplayMetrics metrics = getResources().getDisplayMetrics();
                int width = metrics.widthPixels;
                int height = metrics.heightPixels;
                alert.getWindow().setLayout((6 * width)/7, (4 * height)/5);

                if(imageText.equals("")){
                    result.setText("No text detected");
                }else{
                    result.setText(imageText);
                }

                Button exit = (Button) alert.findViewById(R.id.btnExit);
                exit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        imgFile.delete();
                        alert.dismiss();
                    }
                });

                Button done = (Button) alert.findViewById(R.id.btnDone);
                done.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        imgFile.delete();

                    }
                });

                alert.show();
            }
        });

        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCamera();
            }
        });
        return v;
    }

    String mCurrentPhotoPath;

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void openCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getContext().getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(getContext(),
                        "com.example.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, 0);
            }
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK) {
            imgFile = new File(mCurrentPhotoPath);
            if(imgFile.exists()){
                bp = BitmapFactory.decodeFile(imgFile.getAbsolutePath());

                TextRecognizer textRecognizer = new TextRecognizer.Builder(getContext()).build();

                Frame imageFrame = new Frame.Builder()
                        .setBitmap(bp)                 // your image bitmap
                        .build();


                SparseArray<TextBlock> textBlocks = textRecognizer.detect(imageFrame);

                for (int i = 0; i < textBlocks.size(); i++) {
                    TextBlock textBlock = textBlocks.get(textBlocks.keyAt(i));
                    imageText = textBlock.getValue();                   // return string
                }
                capturedImage.setImageBitmap(bp);
            }
        }
    }




}
