package com.mail.sendsafe;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.mail.sendsafe.callbacks.IDownloadCallback;
import com.mail.sendsafe.callbacks.IItemHandler;
import com.mail.sendsafe.common.AppPreferences;
import com.mail.sendsafe.common.AppSettings;
import com.mail.sendsafe.imageloader.ImageLoader;
import com.mail.sendsafe.models.City;
import com.mail.sendsafe.models.Country;
import com.mail.sendsafe.models.State;
import com.mail.sendsafe.tasks.FileUploader;
import com.mail.sendsafe.tasks.HTTPTask;
import com.mail.sendsafe.tasks.HTTPostJson;
import com.mail.sendsafe.utils.Utils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

public class Profile_Activity extends AppCompatActivity implements IItemHandler, IDownloadCallback {

    private AppCompatImageView imageview;

    private FileUploader uploader = null;

    private Spinner spin_city, spin_country, spin_state;

    private String imagepath = null, fileName = "";

    private JSONObject jsonobject = null;

    private JSONArray jsonarray = null;

    private ArrayList<Country> counties;
    private ArrayList<State> states;
    private ArrayList<City> cities;

    public String country_id, state_id, city_id;

    private ImageLoader imgLoader = null;

    private Bitmap bitmap = null;

    int rotate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.profile_layout);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.profile_head);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        spin_country = (Spinner) findViewById(R.id.spinner_country);

        spin_state = (Spinner) findViewById(R.id.spinner_state);

        spin_city = (Spinner) findViewById(R.id.spinner_city);

        imageview = (AppCompatImageView) findViewById(R.id.profile_img);

        imageview.setScaleType(ScaleType.FIT_XY);

        findViewById(R.id.save_profile).setOnClickListener(onClick);

        findViewById(R.id.upload_pic).setOnClickListener(onClick);

        try {
            String url = AppSettings.getInstance(Profile_Activity.this).getPropertyValue("profile_ret");
            JSONObject object = new JSONObject();
            object.put("email", AppPreferences.getInstance(this).getFromStore("email"));
            HTTPostJson post = new HTTPostJson(this, this, object.toString(), 1);
            post.setContentType("application/json");
            post.execute(url, "");
            Utils.showProgress(getString(R.string.pwait), this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case android.R.id.home:

                Profile_Activity.this.finish();

                break;

            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    OnClickListener onClick = new OnClickListener() {

        @Override
        public void onClick(View view) {

            ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE))
                    .hideSoftInputFromWindow(view.getWindowToken(), 0);
            switch (view.getId()) {

                case R.id.save_profile:

                    updateProfileData();

                    if (imagepath != null) {
                        uploadFile();
                    }

                    break;

                case R.id.upload_pic:
                    brwoseFile();
                    break;

                default:

                    break;
            }

        }

    };

    private void updateProfileData() {

        try {

            String et_fname = ((EditText) findViewById(R.id.profile_txt)).getText().toString().trim();

            if (et_fname.length() == 0) {
                showToast(R.string.peyfn);
                ((EditText) findViewById(R.id.profile_txt)).requestFocus();
                return;
            }

            String et_lname = ((EditText) findViewById(R.id.profile_txtlname)).getText().toString().trim();

            if (et_lname.length() == 0) {
                showToast(R.string.peyln);
                ((EditText) findViewById(R.id.profile_txtlname)).requestFocus();
                return;
            }

            Country selectedcountry = (Country) spin_country.getSelectedItem();
            String value1 = selectedcountry.getCountryid();
            if (value1.length() == 0) {
                showToast(R.string.pecn1);
                ((Spinner) findViewById(R.id.spinner_country)).requestFocus();
                return;
            }

            State selectedState = (State) spin_state.getSelectedItem();
            String value2 = selectedState.getStateid();
            if (value2.length() == 0) {
                showToast(R.string.pecn2);
                ((Spinner) findViewById(R.id.spinner_state)).requestFocus();
                return;
            }

            City selectedcity = (City) spin_city.getSelectedItem();
            String value3 = selectedcity.getCityid();
            if (value3.length() == 0) {
                showToast(R.string.pecn);
                ((Spinner) findViewById(R.id.spinner_city)).requestFocus();
                return;
            }

            if (imagepath != null && imagepath.length() > 0) {
                fileName = System.currentTimeMillis() + "_" + fileName;
            } else {
                fileName = "Image";
            }

            String url = AppSettings.getInstance(this).getPropertyValue("profile_edit");

            JSONObject object = new JSONObject();
            object.put("email", AppPreferences.getInstance(this).getFromStore("email"));
            object.put("firstname", et_fname);
            object.put("lastname", et_lname);
            object.put("country", value1);
            object.put("state", value2);
            object.put("city", value3);
            object.put("imagename", fileName);

            HTTPostJson post = new HTTPostJson(this, this, object.toString(), 5);
            post.setContentType("application/json");
            post.execute(url, "");
            Utils.showProgress(getString(R.string.pwait), this);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void uploadFile() {

        uploader = new FileUploader(this, this);
        uploader.setFileName("", fileName);
        uploader.userRequest("", 1, AppSettings.getInstance(this).getPropertyValue("upload_file"), imagepath);

    }

    private void brwoseFile() {

        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, 1000);

    }

    private JSONObject processData(Intent intent) {
        try {

            if (intent == null) {
                return null;
            }

            Uri uri = intent.getData();
            if (uri == null) {
                return null;
            }

            Cursor cursor = getContentResolver().query(uri, null, null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                JSONObject object = new JSONObject();
                do {
                    String[] resultsColumns = cursor.getColumnNames();
                    for (int i = 0; i < resultsColumns.length; i++) {
                        String key = resultsColumns[i];

                        if (key.equalsIgnoreCase("com.google.android.apps.photos.api.special_type_id"))
                            key = "_id";

                        String value = cursor.getString(cursor.getColumnIndexOrThrow(
                                key/* resultsColumns[i] */));

                        if (value != null) {
                            if (key.contains("_"))
                                key = key.replace("_", "");
                            object.put(key, value);
                        }
//                        imagepath = object.optString("data");
//                        getMyRotation(imagepath, uri);
                    }

                } while (cursor.moveToNext());

                cursor.close();
                cursor = null;

                return object;
            }

            return null;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;

    }
    private int getMyRotation(String imagepath, Uri uri) {

        try {

            this.getContentResolver().notifyChange(uri, null);
            File imageFile = new File(imagepath);


            ExifInterface exif = new ExifInterface(imageFile.getAbsolutePath());
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotate = 270;
                    imageview.setRotation(rotate);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotate = 180;
                    imageview.setRotation(rotate);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotate = 90;
                    imageview.setRotation(rotate);
                    break;

            }

        } catch (Exception e) {

        }
        return rotate;
    }

    private void showToast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onFinish(Object results, int requestType) {

        try {
            Utils.dismissProgress();
            switch (requestType) {
                case 1:
                    Utils.dismissProgress();
                    parseProfileResponse(results, requestType);

                    HTTPTask task = new HTTPTask(Profile_Activity.this, Profile_Activity.this);
                    task.disableProgress();
                    task.userRequest("", 2, AppSettings.getInstance(this).getPropertyValue("countrylist"));

                    break;

                case 2:

                    if (results == null)
                        return;

                    jsonobject = new JSONObject(results.toString());

                    counties = new ArrayList<Country>();

                    try {

                        jsonarray = jsonobject.getJSONArray("countries");

                        int position = -1;

                        for (int i = 0; i < jsonarray.length(); i++) {
                            jsonobject = jsonarray.getJSONObject(i);

                            Country country = new Country();

                            country.setCountryid(jsonobject.optString("location_id"));
                            country.setCountryname(jsonobject.optString("name"));
                            country.setLoctype(jsonobject.optString("location_type"));
                            country.setIs_visible(jsonobject.optString("is_visible"));
                            country.setP_id(jsonobject.optString("parent_id"));

                            if (jsonobject.optString("location_id").equalsIgnoreCase(country_id)) {
                                position = i;
                            }

                            counties.add(country);
                        }

                        spin_country.setAdapter(new ArrayAdapter<Country>(Profile_Activity.this,
                                R.layout.spinner_row_nothing_selected, counties));

                        spin_country.setSelection(position);

                        spin_country.setPrompt("Select Country");

                        spin_country.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                            @Override
                            public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long arg3) {

                                Country selectedCountry = null;

                                selectedCountry = (Country) spin_country.getSelectedItem();

                                try {
                                    String link = AppSettings.getInstance(Profile_Activity.this)
                                            .getPropertyValue("statelist");
                                    JSONObject object = new JSONObject();
                                    object.put("location_id", selectedCountry.getCountryid());
                                    HTTPostJson post = new HTTPostJson(Profile_Activity.this, Profile_Activity.this,
                                            object.toString(), 3);
                                    post.setContentType("application/json");
                                    post.execute(link, "");
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> arg0) {

                            }
                        });

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    break;

                case 3:

                    if (results == null)
                        return;

                    states = new ArrayList<State>();

                    try {

                        int position = -1;

                        JSONObject jsonobject = new JSONObject(results.toString());

                        jsonarray = jsonobject.getJSONArray("states");
                        for (int i = 0; i < jsonarray.length(); i++) {
                            jsonobject = jsonarray.getJSONObject(i);

                            State state = new State();

                            state.setStateid(jsonobject.optString("location_id"));
                            state.setStatename(jsonobject.optString("name"));
                            state.setLoctype(jsonobject.optString("location_type"));
                            state.setP_id(jsonobject.optString("parent_id"));
                            state.setIs_visible(jsonobject.optString("is_visible"));

                            if (jsonobject.optString("location_id").equalsIgnoreCase(state_id)) {
                                position = i;
                            }

                            states.add(state);

                        }

                        spin_state.setAdapter(new ArrayAdapter<State>(Profile_Activity.this,
                                R.layout.spinner_row_nothing_selected, states));
                        spin_state.setPrompt("Select State");

                        spin_state.setSelection(position);

                        spin_state.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                            @Override
                            public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long arg3) {

                                State selectedState = null;

                                selectedState = (State) spin_state.getSelectedItem();
                                try {
                                    String link = AppSettings.getInstance(Profile_Activity.this)
                                            .getPropertyValue("citylist");
                                    JSONObject object = new JSONObject();
                                    object.put("location_id", selectedState.getStateid());
                                    HTTPostJson post = new HTTPostJson(Profile_Activity.this, Profile_Activity.this,
                                            object.toString(), 4);
                                    post.setContentType("application/json");
                                    post.execute(link, "");
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> arg0) {

                            }
                        });

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    break;

                case 4:

                    if (results == null)
                        return;

                    cities = new ArrayList<City>();

                    try {

                        int position = -1;

                        JSONObject jsonobject = new JSONObject(results.toString());

                        jsonarray = jsonobject.getJSONArray("cities");
                        for (int i = 0; i < jsonarray.length(); i++) {
                            jsonobject = jsonarray.getJSONObject(i);

                            City cityy = new City();

                            cityy.setCityid(jsonobject.optString("location_id"));
                            cityy.setCityname(jsonobject.optString("name"));
                            cityy.setLoctype(jsonobject.optString("location_type"));
                            cityy.setP_id(jsonobject.optString("parent_id"));
                            cityy.setIs_visible(jsonobject.optString("is_visible"));

                            if (jsonobject.optString("location_id").equalsIgnoreCase(city_id)) {
                                position = i;
                            }

                            cities.add(cityy);

                        }

                        spin_city.setAdapter(new ArrayAdapter<City>(Profile_Activity.this,
                                R.layout.spinner_row_nothing_selected, cities));
                        spin_city.setPrompt("Select City");
                        spin_city.setSelection(position);

                    } catch (Exception e) {
                        // Log.e("Error", e.getMessage());
                        e.printStackTrace();
                    }

                    break;

                case 5:
                    Utils.dismissProgress();
                    if (results == null)
                        return;

                    JSONObject obj = new JSONObject(results.toString());
                    showToast(obj.optString("statusdescription"));

                    break;

                default:
                    break;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onError(String errorData, int requestType) {
        showToast(errorData);
    }

    public void showToast(int text) {
        Toast.makeText(this, text, Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        try {

            if (resultCode == RESULT_OK) {
                JSONObject object = processData(data);

                if (object == null) {
                    showToast("Please select any file");
                    return;
                }

                imagepath = object.optString("data");

                fileName = object.optString("displayname");

                bitmap = decodeFile(new File(imagepath), 2);

                if (bitmap != null) {

                    bitmap = getRoundedShape(bitmap);

                    imageview.setScaleType(ScaleType.FIT_XY);

                    imageview.setImageBitmap(bitmap);


                    ((TextView) findViewById(R.id.file_path)).setText(fileName);
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void parseProfileResponse(Object response, int requestId) throws Exception {

        if (response == null)
            return;

        JSONObject result = new JSONObject(response.toString());

        if (result != null) {
            try {

                String fname = result.getString("firstname");
                String lname = result.getString("lastname");
                country_id = result.getString("country_id");
                state_id = result.getString("state_id");
                city_id = result.getString("city_id");
                String imgURL = result.getString("avatar");

                if (imgLoader == null)
                    imgLoader = new ImageLoader(Profile_Activity.this, true);

                    imgLoader.DisplayImage(imgURL, imageview, 5);

                ((EditText) findViewById(R.id.profile_txt)).setText(fname);
                ((EditText) findViewById(R.id.profile_txtlname)).setText(lname);

            } catch (Exception e) {

            }
        }

    }

    private Bitmap decodeFile(File f, int photoToLoad) {
        try {
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(f), null, o);
            int REQUIRED_SIZE = 70;

            if (photoToLoad == 1)
                REQUIRED_SIZE = 120;

            if (photoToLoad == 2 || photoToLoad == 5)
                REQUIRED_SIZE = 200;

            int width_tmp = o.outWidth, height_tmp = o.outHeight;
            int scale = 1;
            while (true) {
                if (width_tmp / 2 < REQUIRED_SIZE || height_tmp / 2 < REQUIRED_SIZE)
                    break;
                width_tmp /= 2;
                height_tmp /= 2;
                scale *= 2;
            }

            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;

            return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);

        } catch (FileNotFoundException e) {
        }
        return null;
    }

    public Bitmap getRoundedShape(Bitmap scaleBitmapImage) {
        int targetWidth = 200;
        int targetHeight = 200;
        Bitmap targetBitmap = Bitmap.createBitmap(targetWidth, targetHeight, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(targetBitmap);
        Path path = new Path();
        path.addCircle(((float) targetWidth - 1) / 2, ((float) targetHeight - 1) / 2,
                (Math.min(((float) targetWidth), ((float) targetHeight)) / 2), Path.Direction.CCW);

        canvas.clipPath(path);
        Bitmap sourceBitmap = scaleBitmapImage;
        canvas.drawBitmap(sourceBitmap, new Rect(0, 0, sourceBitmap.getWidth(), sourceBitmap.getHeight()),
                new Rect(0, 0, targetWidth, targetHeight), null);
        return targetBitmap;
    }

    @Override
    protected void onDestroy() {

        imgLoader.clearCache();

        imgLoader = null;

        imageview.setImageBitmap(null);
        bitmap = null;
        super.onDestroy();
    }

    @Override
    public void onStateChange(int what, int arg1, int arg2, Object obj, int requestId) {

        try {

            switch (what) {

                case -1: // failed

                    showToast(obj + "");
                    uploader = null;

                    break;

                case 1: // progressBar

                    break;

                case 0: // success

                    showToast(R.string.ppus);

                    uploader = null;

                    break;

                default:
                    break;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}