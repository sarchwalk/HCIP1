package com.cpsc581.colorunlock;

import android.animation.Animator;
import android.animation.FloatEvaluator;
import android.animation.ValueAnimator;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.christophesmet.android.views.maskableframelayout.MaskableFrameLayout;
import com.devs.vectorchildfinder.VectorChildFinder;
import com.devs.vectorchildfinder.VectorDrawableCompat;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;



public class MainActivity extends AppCompatActivity {
    static String TAG = "Acaikii";

    boolean firstLoad = true;
    boolean settingPattern = false;

    FloatingActionButton fab;
    int patternLength;

    MaskableFrameLayout[] frames;
    FruitableSlice[] slices;
    PaletteFruit[] fruitPalette;
    ImageView[] contents;
    ImageView sliceMask;

    Fruit[] availableFruit = {Fruit.DRAGONFRUIT, Fruit.STRAWBERRY, Fruit.BANANA, Fruit.APPLE, Fruit.OATS};
    Fruit selectedFruit = Fruit.EMPTY;

    ImageView pointerImage;
    LockPattern key;
    LockPattern attempt = new LockPattern();

    Vibrator vibrator;

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            hideSystemUI();
        }
    }

    private void hideSystemUI() {
        // Enables regular immersive mode.
        // For "lean back" mode, remove SYSTEM_UI_FLAG_IMMERSIVE.
        // Or for "sticky immersive," replace it with SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE
                        // Set the content to appear under the system bars so that the
                        // content doesn't resize when the system bars hide and show.
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        // Hide the nav bar and status bar
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }

    // Shows the system bars by removing all the flags
    // except for the ones that make the content appear under the system bars.
    private void showSystemUI() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        hideSystemUI();

        //Get View References and set source and mask
        frames = new MaskableFrameLayout[4];
        frames[0] = findViewById(R.id.frm_mask_1);
        frames[1] = findViewById(R.id.frm_mask_2);
        frames[2] = findViewById(R.id.frm_mask_3);
        frames[3] = findViewById(R.id.frm_mask_4);

        //sliceImage = findViewById(R.id.imageView);
        //sliceImage.setImageResource(R.drawable.ic_complexslices);


        //The sliceMask has been repurposed purely as a collision reference.
        sliceMask = findViewById(R.id.imageMask);
        sliceMask.setImageResource(R.drawable.ic_complexslices_mask);
        fab = findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setPattern(view);
            }
        });
        fab.hide();

        contents = new ImageView[4];
        contents[0] = findViewById(R.id.content1); // Left
        contents[1] = findViewById(R.id.content2); // Centre
        contents[2] = findViewById(R.id.content3); // Right
        contents[3] = findViewById(R.id.content4); // Circumference

        //Get paths
        //VectorChildFinder vectorFinder = new VectorChildFinder(this, R.drawable.ic_complexslices, sliceImage);
        slices = new FruitableSlice[3];

        //ImageView bowlImage = findViewById(R.drawable.bowl);

        slices[0] = new FruitableSlice(0, frames[0], contents[0], sliceMask, Color.parseColor("#ff0000"), this);
        slices[1] = new FruitableSlice(1, frames[1], contents[1], sliceMask, Color.parseColor("#00ff00"), this);
        slices[2] = new FruitableSlice(2, frames[2], contents[2], sliceMask, Color.parseColor("#0000ff"), this);


        fruitPalette = new PaletteFruit[5];
        fruitPalette[0] = new PaletteFruit((ImageView)findViewById(R.id.fruit0), availableFruit[0]);
        fruitPalette[1] = new PaletteFruit((ImageView)findViewById(R.id.fruit1), availableFruit[1]);
        fruitPalette[2] = new PaletteFruit((ImageView)findViewById(R.id.fruit2), availableFruit[2]);
        fruitPalette[3] = new PaletteFruit((ImageView)findViewById(R.id.fruit3), availableFruit[3]);
        fruitPalette[4] = new PaletteFruit((ImageView)findViewById(R.id.fruit4), availableFruit[4]);



        //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ THIS "WORKS" BUT CRASHES UPON SAYING OKAY.
        //Load key data if available, otherwise, set key data
        ArrayList<PatternNode> temp =  loadData("pass");
        if(temp == null){
            key = new LockPattern();
            setPatternAlert();
        }
        else{
            key = new LockPattern();
            key.setPattern(temp);
        }


        vibrator = (Vibrator)getSystemService(VIBRATOR_SERVICE);
    }

    public void generatePattern(){
        settingPattern = true;
        fab.show();
    }

    public void setPattern(View view) {
        reset();
        settingPattern = false;
        patternLength = key.getPatternLength();
        saveData(key.getPattern(), "pass");
        fab.hide();
    }

    private void saveData(ArrayList<PatternNode> pn, String key1){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(pn);
        editor.putString(key1, json);
        editor.apply();
    }

    private ArrayList<PatternNode> loadData(String key1){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        Gson gson = new Gson();
        String json = sharedPreferences.getString(key1,null);
        Type type = new TypeToken<ArrayList<PatternNode>>(){}.getType();
        return (ArrayList<PatternNode>) gson.fromJson(json, type);
    }


    @Override
    public void onStart()
    {
        super.onStart();

        if(firstLoad)
        {
            for (FruitableSlice slice : slices) {
                slice.animateFill(Fruit.EMPTY);
            }
            firstLoad = false;
        }

    }

    @Override
    public void onPause(){
        super.onPause();
        reset();
    }

    private void reset() {
        if (slices != null) {
            for (FruitableSlice slice : slices) {
                slice.animateFill(Fruit.EMPTY);
            }
        }
        attempt.clearPattern();
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);

        int action = event.getActionMasked();

        int evX = (int) event.getX();
        int evY = (int) event.getY();


        // ALL THESE ACTIONS ARE CAUSING APP TO CRASH. WHY?
        switch (action) {
            case MotionEvent.ACTION_UP:
                fillSlice(evX, evY);
                unselectAllFruit();
                removePointer();
                break;
            case MotionEvent.ACTION_MOVE:
                moveColorIcon(evX, evY);
                break;
            case MotionEvent.ACTION_DOWN:
                doActionDown(evX, evY);
                break;
        }



        return true;
    }

    private void doActionDown(int evX, int evY)
    {
        unselectAllFruit();
        if(selectFruit(evX, evY))
        {
            createPointer(evX, evY);
        }
    }

    private void removePointer() {
        ((RelativeLayout)findViewById(R.id.mainLayout)).removeView(pointerImage);
    }

    private void createPointer(int x, int y)
    {
        pointerImage = new ImageView(this);
        pointerImage.setImageResource(R.drawable.ic_dragblob);
        pointerImage.setColorFilter(selectedFruit.toInt());
        ((RelativeLayout)findViewById(R.id.mainLayout)).addView(pointerImage);
        pointerImage.setScaleType(ImageView.ScaleType.CENTER);
        pointerImage.setScaleX(1f);
        pointerImage.setScaleY(1f);
        pointerImage.setX(x - (pointerImage.getWidth()/2f));
        pointerImage.setY(y - (pointerImage.getHeight()));
    }

    private boolean selectFruit(int evX, int evY)
    {
        for (PaletteFruit pf: fruitPalette)
        {
            if(pf.getHitRect().contains(evX, evY))
            {
                pf.selectFruit();
                selectedFruit = pf.fruit; //What is invertHex?
                return true;
            }
        }

        return false;
    }

    private void unselectAllFruit()
    {
        selectedFruit = Fruit.EMPTY;

        for (PaletteFruit pf: fruitPalette)
        {
            pf.unselectFruit();
        }
    }

    private void moveColorIcon(int x, int y)
    {
        // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ CRASHING.
        if(pointerImage != null) {
            pointerImage.setX(x - (pointerImage.getWidth() / 2f));
            pointerImage.setY(y - (pointerImage.getHeight()));
        }
    }

    public void fillSlice(int x, int y) {
        if(selectedFruit != Fruit.EMPTY) {
            for (FruitableSlice slice : slices) {
                if (closeMatch(getMaskColor(R.id.imageMask, x, y), slice.maskColor, 25)) {

                    slice.animateFill(selectedFruit);
                    //Animator a =

                    if (!settingPattern) {
                        attempt.addNode(new PatternNode(slice.id, selectedFruit));
                        if (key.getPatternLength() == attempt.getPatternLength()) {
                            if (attempt.validateAgainst(key)) {
                                //UNLOCKED!
                                unlockPhone();
                            } else {
                                invalidPass();
                            }
                        }

                        /*
                        a.addListener(new Animator.AnimatorListener() {
                            @Override
                            public void onAnimationStart(Animator animation) {

                            }
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                if (key.getPatternLength() == attempt.getPatternLength()) {
                                    if (attempt.validateAgainst(key)) {
                                        //UNLOCKED!
                                        unlockPhone();
                                    } else {
                                        invalidPass();
                                    }
                                }
                            }

                            @Override
                            public void onAnimationCancel(Animator animation) {

                            }

                            @Override
                            public void onAnimationRepeat(Animator animation) {

                            }
                        });
                        break;
                        */
                    }
                    else{
                        key.addNode(new PatternNode(slice.id, selectedFruit));
                    }
                }
            }
        }
    }

    private void invalidPass()
    {
        ValueAnimator va1 = ValueAnimator.ofObject(new FloatEvaluator(), 0.1f, 1f);
        final ValueAnimator va2 = ValueAnimator.ofObject(new FloatEvaluator(), 0.1f, 1f);
        va1.setDuration(125);
        va2.setDuration(125);
        va1.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {

            }
        });
        va2.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {

            }
        });
        va1.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                vibrator.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE));
                va2.start();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        va2.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        reset();
                    }
                }, 250);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        va1.start();
    }

    private void unlockPhone() {
        ValueAnimator va = ValueAnimator.ofObject(new FloatEvaluator(), 0.1f, 1f);
        va.setDuration(250);
        va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {

            }
        });
        va.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        finish();
                    }
                }, 250);

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        va.start();
    }

    public int getMaskColor(int hotspot, int x, int y) {
        sliceMask.setDrawingCacheEnabled(true);
        Bitmap hotspots = Bitmap.createBitmap(sliceMask.getDrawingCache());
        sliceMask.setDrawingCacheEnabled(false);

        if (y > hotspots.getHeight() || x > hotspots.getWidth()) {
            return -1;
        }

        return hotspots.getPixel(x, y);
    }

    public boolean closeMatch(int color1, int color2, int tolerance) {
        if ((int) Math.abs(Color.red(color1) - Color.red(color2)) > tolerance)
            return false;
        if ((int) Math.abs(Color.green(color1) - Color.green(color2)) > tolerance)
            return false;
        if ((int) Math.abs(Color.blue(color1) - Color.blue(color2)) > tolerance)
            return false;
        return true;
    } // end match

    public void setPatternAlert(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        String message = "Please set an unlock pattern";
        builder.setMessage(message)
                .setCancelable(false)
                .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        generatePattern();
                        dialog.dismiss();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

}
