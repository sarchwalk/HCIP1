package com.cpsc581.colorunlock;

import android.animation.Animator;
import android.animation.ArgbEvaluator;
import android.animation.FloatEvaluator;
import android.animation.IntEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.view.View;
import android.widget.ImageView;

import com.christophesmet.android.views.maskableframelayout.MaskableFrameLayout;
import com.devs.vectorchildfinder.VectorDrawableCompat;

import java.nio.file.Path;

public class FruitableSlice {
    public MaskableFrameLayout parent;
    public ImageView content;
    public ImageView mask;
    public int maskColor;
    public int id;
    private Context current;
    private Bitmap results;
    private Bitmap maskbitmap;

    //public FruitableSlice(int id, MaskableFrameLayout parent, ImageView mask, VectorDrawableCompat.VFullPath slice, int maskColor, Context current)
    public FruitableSlice(int id, MaskableFrameLayout parent, ImageView content, ImageView mask, int maskColor, Context current)
    {
        this.parent = parent;
        this.mask = mask;
        this.content = content;
        this.maskColor = maskColor;
        this.id = id;
    }

    public void animateFill(Fruit toFruit)
    {
        int image = toFruit.toImage();
        content.setImageResource(image);
        parent.setMask(parent.getDrawableMask());

        //content.setAlpha(0f);
        //content.setVisibility(View.VISIBLE);
        //content.animate()
        //        .alpha(0.5f)
         //       .setDuration(400)
          //      .setListener(null);
        /*
        ValueAnimator animator = ValueAnimator.ofObject(new IntEvaluator(), 0x00000000, 0xFF000000);
        animator.setDuration(500);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                int newTransparency = (int) animator.getAnimatedValue() | (0x00FFFFFF & animFruit.toImage());
                parent.setImageResource(animFruit.toImage());
                parent.setImageAlpha(newTransparency);
                parent.setImageBitmap(MaskingProcess(animFruit));
                int newTransparency = (int) animator.getAnimatedValue() | 0x00FF00FF;
                parent.invalidate();
            }

        });
        animator.start();

        return animator;

         */
    }

    /*
    public Animator animateFill(int toColor)
    {
        int from = slice.getFillColor();
        ValueAnimator animator = ValueAnimator.ofObject(new ArgbEvaluator(), from, toColor);
        animator.setDuration(500);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                slice.setFillColor((int) animator.getAnimatedValue());
                parent.invalidate();
            }

        });
        animator.start();

        return animator;
    }
    */

    public void setImage(Fruit fruit){
        //parent.setImageResource(fruit.toImage());
        //mask.setImageResource(R.drawable.ic_complexslices_mask);
        //parent.invalidate();
        //mask.invalidate();
    }

    public void setFill(Fruit fruit)
    {
        //slice.setFillColor(fruit.toInt());
        parent.invalidate();
    }

}
