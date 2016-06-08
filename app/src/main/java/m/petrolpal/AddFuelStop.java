package m.petrolpal;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;

import android.widget.Toast;


import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


import m.petrolpal.Models.FuelStop;
import m.petrolpal.Tools.DatabaseHelper;

public class AddFuelStop extends AppCompatActivity {

    EditText inCost;
    EditText inOdom;
    EditText inQuantity;
    EditText inDate;
    EditText inLocation;
    ImageView iconCalendar;
    ImageView iconLocation;
    ImageView iconCamera;

    Button addButton;

    String currentPhotoPath;

    static final int REQUEST_IMAGE_CAPTURE = 1;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_fuel_stop);


        DatabaseHelper dbhelper = new DatabaseHelper(getApplicationContext());
        ArrayList <FuelStop> fuelStops = new
                ArrayList<>(dbhelper.getAllFuelStops().values());


        inCost = (EditText) findViewById(R.id.inputCost);
        inOdom = (EditText) findViewById(R.id.inputOdom);
        inQuantity = (EditText)  findViewById(R.id.inputQuantity);
        inDate = (EditText) findViewById(R.id.inputDate);
        inLocation = (EditText) findViewById(R.id.inputLoc);
        addButton = (Button) findViewById(R.id.addButton);
        iconCalendar = (ImageView) findViewById(R.id.addDateIcon);
        iconLocation = (ImageView) findViewById(R.id.addLocationIcon);
        iconCamera = (ImageView) findViewById(R.id.cameraView);

        //to open dialog on first click
        inDate.setClickable(true);
        inDate.setFocusable(false);
        inLocation.setClickable(true);
        inLocation.setFocusable(false);

        View.OnClickListener dateListener = new View.OnClickListener() {
            Calendar c = Calendar.getInstance();
            @Override
            public void onClick(View v) {
                final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener(){
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {


                        c.set(Calendar.YEAR, year);
                        c.set(Calendar.MONTH, monthOfYear);
                        c.set(Calendar.DAY_OF_MONTH, dayOfMonth);


                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                        String dateFormat = sdf.format(c.getTime());
                        inDate.setText(sdf.format(c.getTime()));

                    }
                };



                new DatePickerDialog(AddFuelStop.this, date,
                        c.get(Calendar.YEAR),
                        c.get(Calendar.MONTH),
                        c.get(Calendar.DAY_OF_MONTH))
                        .show();
            }
        };

        View.OnClickListener addLocationListener = new View.OnClickListener() {

            @Override
            public void onClick(View v) {

            }
        };


        iconCalendar.setOnClickListener(dateListener);
        inDate.setOnClickListener(dateListener);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(inputsEmpty()) {


                    FuelStop f = new FuelStop(
                            inDate.getText().toString(),
                            Double.valueOf(inQuantity.getText().toString()),
                            Double.valueOf(inCost.getText().toString()),
                            Integer.valueOf(inOdom.getText().toString()),

                            12.1,   //TODO get long and lat from input
                            12.3
                    );
                    Intent i = new Intent(AddFuelStop.this, TabsActivity.class);
                    i.putExtra(TabsActivity.ADD_REQUEST, f);
                    setResult(RESULT_OK, i);
                    finish();
                    overridePendingTransition(R.transition.fade_in, R.transition.fade_out);
                }else{
                    Toast.makeText(getApplicationContext(), "Fill in all fields", Toast.LENGTH_SHORT).show();

                }

            }

        });

        iconCamera.setOnClickListener(new View.OnClickListener() {
                          @Override
                          public void onClick(View v) {
                              dispatchTakePictureIntent();
                          }
                      }
        );



//        inDate.getText().toString() != null &&
//                inCost.getText().toString() != null &&
//                inQuantity.getText().toString() != null &&
//                inOdom.getText().toString() != null);




    }



    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("ddMMyyyy_HHmmss").format(new Date());
        String imageFileName = "PPRCPT_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = "file:" + image.getAbsolutePath();

        return image;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            iconCamera.setImageBitmap(imageBitmap);
        }


    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            try{
                photoFile = createImageFile();
            }catch (IOException e){
                e.printStackTrace();
            }
            if(photoFile != null){
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));

            }
        }


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.transition.fade_in, R.transition.fade_out);
    }


    private boolean inputsEmpty(){

        boolean x = (inDate.getText().toString().equals("") ||
                inQuantity.getText().toString().equals("") ||
                inCost.getText().toString().equals("") ||
                inOdom.getText().toString().equals("")
                //|| inLocation.getText().toString().equals("")     //TODO enable add location
        );
        return !x;

        /*
        if(inDate.getText().toString() == "" &&
                inQuantity.getText().toString() == "" &&
                inCost.getText().toString() == "" &&
                inOdom.getText().toString() == "" &&
                inLocation.getText().toString() == ""){
            return false;
        }else{
            return true;
        }
        */





    }





}




