package com.e.reconbot.ui.dashboard;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import com.e.reconbot.R;
import com.e.reconbot.DbHandler;
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
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import dmax.dialog.SpotsDialog;


public class Camera extends AppCompatActivity {

    AlertDialog waitingDialog;
    DbHandler db;
    Bitmap bitmap;
    String currentPhotoPath;
    ImageView imageView;
    static final int REQUEST_TAKE_PHOTO = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        imageView = findViewById(R.id.cameraImageView);
        db = new DbHandler(getApplicationContext());
        waitingDialog = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Processing...")
                .setCancelable(false).build();

        initiateCam();
    } // end oncreate


    private void initiateCam() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.e.reconbot.provider",
                        photoFile);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(cameraIntent, REQUEST_TAKE_PHOTO);
            } else {
                System.out.println("ERRO: File not created.");
            }
        }
    }

    private File createImageFile() throws IOException {
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile("temp",".jpg", storageDir);
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
           try {
               BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
               bitmap = BitmapFactory.decodeFile(currentPhotoPath, bitmapOptions);
               bitmap = imageResize(bitmap);
               imageView.setImageBitmap(bitmap);

               File file = new File(currentPhotoPath);
               file.delete();
               waitingDialog.show();
               runDetector(bitmap);

            } catch (Exception e) {
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


    private void runDetector(final Bitmap bitmap) {
        final Bitmap bitmap2 = bitmap;
        final FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(bitmap);
        new InternetCheck(new InternetCheck.Consumer() {
            @Override

            public void accept(boolean internet) {
                if(internet){
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
                        final AlertDialog.Builder mBuilder = new AlertDialog.Builder(Camera.this);
                        View mview = getLayoutInflater().inflate(R.layout.imagerecognitiondialog,null);
                        mBuilder.setView(mview);
                        mBuilder.setTitle("Results");
                        mBuilder.setPositiveButton("Go again", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();// Close alert dialog
                                initiateCam();
                            }
                        });
                        mBuilder.setNegativeButton("Back", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish(); // close activity
                            }
                        });

                        // Create results list
                            ArrayList<String> list = new ArrayList<>();
                            for(FirebaseVisionCloudLabel label: firebaseVisionCloudLabels){
                                list.add( label.getLabel() );
                        }

                        if (list.size() < 3) {
                            list.set(2, "");
                            list.set(1, "");
                        }
                        // If there are results, show them and save in History, else notify user.
                        if (list.size() > 0) {
                            mBuilder.setMessage("\n1. " + list.get(0) + "\n"+
                                    "2. " + list.get(1) + "\n"+
                                    "3. " + list.get(2) + "\n");
                            System.out.println("SIZE:" + list.size());
                            insertDB(list, bitmap2);

                        } else {
                            mBuilder.setMessage("Sorry, item not recognized. If possible, go online and try again.");
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


                }else{
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
                                    final AlertDialog.Builder mBuilder = new AlertDialog.Builder(Camera.this);
                                    View mview = getLayoutInflater().inflate(R.layout.imagerecognitiondialog,null);
                                    mBuilder.setView(mview);
                                    mBuilder.setTitle("Results");
                                    mBuilder.setPositiveButton("Go again", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.cancel();// Close alert dialog
                                            initiateCam();
                                        }
                                    });
                                    mBuilder.setNegativeButton("Back", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            finish(); // close activity
                                        }
                                    });

                                    // Create results list
                                    ArrayList<String> listDevice = new ArrayList<>();
                                    for(FirebaseVisionLabel label: firebaseVisionLabels){
                                        listDevice.add( label.getLabel() );
                                    }

                                    // If there are results, show them and save in History, else notify user.
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
        for(FirebaseVisionCloudLabel label: firebaseVisionCloudLabels){
            //Toast.makeText(this,"Cloud result: " +label.getLabel(), Toast.LENGTH_SHORT).show();
            System.out.println("Result: " + label.getLabel());
        }
        if(waitingDialog.isShowing()) {
            waitingDialog.dismiss();
        }
    }


    private void processDataResult(List<FirebaseVisionLabel> firebaseVisionLabels) {
        for(FirebaseVisionLabel label: firebaseVisionLabels){
            //Toast.makeText(this,"Device result: " +label.getLabel(), Toast.LENGTH_SHORT).show();
            System.out.println("Device Result" + label.getLabel());
        }
        if(waitingDialog.isShowing()) {
            waitingDialog.dismiss();
        }
    }


    private void insertDB(ArrayList<String> results, Bitmap image) {
        byte[] imageBytes;
        System.out.println("Ao entrar no insertDB: " + image.getByteCount());
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        imageBytes = stream.toByteArray();
        System.out.println("Qualidade a 50: " + stream.size());

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



