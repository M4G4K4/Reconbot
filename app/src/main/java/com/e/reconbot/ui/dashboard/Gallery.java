package com.e.reconbot.ui.dashboard;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.e.reconbot.DbHandler;
import com.e.reconbot.R;
import com.e.reconbot.ui.InternetCheck;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.cloud.FirebaseVisionCloudDetectorOptions;
import com.google.firebase.ml.vision.cloud.label.FirebaseVisionCloudLabel;
import com.google.firebase.ml.vision.cloud.label.FirebaseVisionCloudLabelDetector;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.label.FirebaseVisionLabel;
import com.google.firebase.ml.vision.label.FirebaseVisionLabelDetector;
import com.google.firebase.ml.vision.label.FirebaseVisionLabelDetectorOptions;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import dmax.dialog.SpotsDialog;

public class Gallery extends AppCompatActivity {

    AlertDialog waitingDialog;
    private static final int PICK_IMAGE = 100;
    ImageView imageView;
    DbHandler db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        waitingDialog = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Processing...")
                .setCancelable(false).build();

        db = new DbHandler(getApplicationContext());
        imageView = (ImageView) findViewById(R.id.galleryImageView);

        openGallery();
    }


    // Criar função onde vai buscar a image poe em bitmap e chama a runDetector();
    private void openGallery() {
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery, PICK_IMAGE);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == PICK_IMAGE) {

            try {
                Uri imageUri = data.getData();
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                bitmap = imageResize(bitmap);
                imageView.setImageURI(imageUri);
                waitingDialog.show();
                runDetector(bitmap);

            } catch (IOException e) {
                e.printStackTrace();
            }

        } else {
            onBackPressed();
        }
    }

    protected Bitmap imageResize (Bitmap image){
        float aspectRatio = image.getWidth() /
                (float) image.getHeight();
        int width = 720;
        int height = Math.round(width / aspectRatio);

        image = Bitmap.createScaledBitmap(
                image, width, height, false);

        return image;
    }

    private void runDetector(Bitmap bitmap) {

        final FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(bitmap);
        final Bitmap bitmap2 = bitmap;
        new InternetCheck(new InternetCheck.Consumer() {
            @Override
            public void accept(boolean internet) {

                if (internet) {
                    // If i have internet
                    FirebaseVisionCloudDetectorOptions options =
                            new FirebaseVisionCloudDetectorOptions.Builder()
                                    .setMaxResults(3)
                                    .build();

                    FirebaseVisionCloudLabelDetector detector =
                            FirebaseVision.getInstance().getVisionCloudLabelDetector(options);

                    detector.detectInImage(image).addOnSuccessListener(new OnSuccessListener<List<FirebaseVisionCloudLabel>>() {
                        @Override
                        public void onSuccess(List<FirebaseVisionCloudLabel> firebaseVisionCloudLabels) {
                            processDataResultCloud(firebaseVisionCloudLabels);

                            // Open Dialog
                            Context context;
                            final AlertDialog.Builder mBuilder = new AlertDialog.Builder(Gallery.this);
                            View mview = getLayoutInflater().inflate(R.layout.imagerecognitiondialog, null);
                            mBuilder.setView(mview);
                            mBuilder.setTitle("Results");
                            mBuilder.setPositiveButton("Go again", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    openGallery();

                                }
                            });
                            mBuilder.setNegativeButton("Back", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    finish(); // close activity
                                }
                            });

                            ArrayList<String> list = new ArrayList<>();
                            for (FirebaseVisionCloudLabel label : firebaseVisionCloudLabels) {
                                list.add(label.getLabel());
                            }

                            if (list.size() > 0) {
                                mBuilder.setMessage("\n1. " + list.get(0) + "\n" +
                                        "2. " + list.get(1) + "\n" +
                                        "3. " + list.get(2) + "\n");
                                insertDB(list, bitmap2);
                            } else {
                                mBuilder.setMessage("\nSorry, item not recognized. If possible, go online and try again.");
                            }

                            AlertDialog dialog = mBuilder.create();
                            dialog.show();

                        }
                    })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d("EDMTError", e.getMessage());
                                }
                            });

                } else {
                    FirebaseVisionLabelDetectorOptions options =
                            new FirebaseVisionLabelDetectorOptions.Builder()
                                    .setConfidenceThreshold(0.8f)
                                    .build();

                    FirebaseVisionLabelDetector detector =
                            FirebaseVision.getInstance().getVisionLabelDetector(options);

                    detector.detectInImage(image)
                            .addOnSuccessListener(new OnSuccessListener<List<FirebaseVisionLabel>>() {
                                @Override
                                public void onSuccess(List<FirebaseVisionLabel> firebaseVisionLabels) {
                                    processDataResult(firebaseVisionLabels);

                                    // Open Dialog
                                    Context context;
                                    final AlertDialog.Builder mBuilder = new AlertDialog.Builder(Gallery.this);
                                    View mview = getLayoutInflater().inflate(R.layout.imagerecognitiondialog, null);
                                    mBuilder.setView(mview);
                                    mBuilder.setTitle("Results");
                                    mBuilder.setPositiveButton("Go again", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            openGallery();
                                            //dialog.cancel();// Close alert dialog

                                        }
                                    });
                                    mBuilder.setNegativeButton("Back", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            finish(); // close activity
                                        }
                                    });

                                    ArrayList<String> listDevice = new ArrayList<>();
                                    for (FirebaseVisionLabel label : firebaseVisionLabels) {
                                        listDevice.add(label.getLabel());
                                    }

                                    if (listDevice.size() > 0) {
                                        mBuilder.setMessage("\n1. " + listDevice.get(0) + "\n");
                                        insertDB(listDevice, bitmap2);

                                    } else {
                                        mBuilder.setMessage("\nSorry, item not recognized. If possible, go online and try again.");
                                    }

                                    AlertDialog dialog = mBuilder.create();
                                    dialog.show();

                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d("EDMTError", e.getMessage());
                                }
                            });
                }
            }
        });
    }


    private void processDataResultCloud(List<FirebaseVisionCloudLabel> firebaseVisionCloudLabels) {
        // 3 resultados
        for (FirebaseVisionCloudLabel label : firebaseVisionCloudLabels) {

        }
        if (waitingDialog.isShowing()) {
            waitingDialog.dismiss();
        }
    }


    private void processDataResult(List<FirebaseVisionLabel> firebaseVisionLabels) {
        for (FirebaseVisionLabel label : firebaseVisionLabels) {

        }
        if (waitingDialog.isShowing()) {
            waitingDialog.dismiss();
        }
    }


    private void insertDB(ArrayList<String> results, Bitmap image) {
        byte[] imageBytes;
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 60, stream);
        imageBytes = stream.toByteArray();

        String results1 = results.get(0);
        String results2 ="";
        String results3 ="";
        if (results.size() > 1) {
             results2 = results.get(1);
             results3 = results.get(2);
        }

        if (db.insertRecord(results1, results2, results3, imageBytes)) {
            Toast.makeText(getApplicationContext(), "Item added to History", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getApplicationContext(), "Error: not added to History!", Toast.LENGTH_LONG).show();
        }
    }

}
