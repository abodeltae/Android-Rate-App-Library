package com.nazeer.ratingmanager;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.util.Log;

/**
 * Created by nazeer on 4/6/16.
 */
public class RatingManager {

    SharedPreferences sharedPreferences;
    private static String prefName="RATINGPREFS";

    Runnable ratingRunnable;
    Context context;
    private int defaultHoldBackCount;
    private final String FIRST_USE_PREF="FIRSTUSE"
            ,DEFAULT_HOLD_BACK_PREF="DEFAULTHOLDBACK"
            ,RATED_BEFORE_PREF="RATEDBEFORE"
            ,HOLD_BACK_PREF="HOLDBACK";
    public  enum RatingMode {
        USE_PROVIDED_RUNNABLE,USE_DEFAULT_DIALOG
    }


    public RatingManager (Context context,int defaultHoldBackCount){
        this.context=context;
        sharedPreferences=context.getSharedPreferences(prefName,Context.MODE_PRIVATE);
        sharedPreferences.edit().putInt(DEFAULT_HOLD_BACK_PREF,defaultHoldBackCount).apply();
        this.defaultHoldBackCount=defaultHoldBackCount;
    }



    public boolean isFirstUse(){
        boolean ret=sharedPreferences.getBoolean(FIRST_USE_PREF, true);
        if(ret){
            sharedPreferences.edit().putBoolean(FIRST_USE_PREF,false).apply();
        }
        return ret;
    }

    public boolean ratedBefore(){
        boolean ret=sharedPreferences.getBoolean(RATED_BEFORE_PREF, false);
        return ret;
    }

    public void holdBack(int n){
        sharedPreferences.edit().putInt(HOLD_BACK_PREF,n).apply();
    }

    public void holdBack(){
        holdBack(getHoldBackDefault());
    }

    private int getHoldBackDefault(){
       return sharedPreferences.getInt(DEFAULT_HOLD_BACK_PREF,defaultHoldBackCount);
    }

    public void setRatedBefore(){
        sharedPreferences.edit().putBoolean(RATED_BEFORE_PREF, true).apply();
    }

    private void decrementHoldBack(){
        int currentHoldBack = sharedPreferences.getInt(HOLD_BACK_PREF,getHoldBackDefault());
        holdBack(Math.max(0, currentHoldBack - 1));
    }


    public void triggerRateEvent(RatingMode ratingMode,boolean forceShowRating){
        if (!forceShowRating&&ratedBefore())return ;
        int currentHoldBack=sharedPreferences.getInt(HOLD_BACK_PREF,getHoldBackDefault());
        if(forceShowRating||currentHoldBack==0){
            if(ratingMode== RatingMode.USE_PROVIDED_RUNNABLE)runRatingRunnable();
            else if (ratingMode== RatingMode.USE_DEFAULT_DIALOG)showDefaultRateUsDialog();
            return;
        }
        decrementHoldBack();

    }

    public void triggerRateEvent(RatingMode ratingMode){
        triggerRateEvent(ratingMode,false);
    }
    public void triggerRateEvent(){
         triggerRateEvent(RatingMode.USE_DEFAULT_DIALOG,false);
    }

    public void showDefaultRateUsDialog(String rateUsMessage,String okButtonString,String laterButtonString) {
        AlertDialog.Builder builder=new AlertDialog.Builder(context);
        builder.setMessage(rateUsMessage);
        builder.setPositiveButton(okButtonString, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                launchRateOnStore();
                setRatedBefore();
            }
        });
        builder.setNegativeButton(laterButtonString, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                holdBack();
            }
        });
        builder.show();

    }
    public void showDefaultRateUsDialog(){
        String rateUsMessage=context.getString(R.string.rateUsMessage);
        String okButtonMessage=context.getString(R.string.ok);
        String laterButtonMessage=context.getString(R.string.later);
        showDefaultRateUsDialog(rateUsMessage, okButtonMessage, laterButtonMessage);
    }

    private void runRatingRunnable() {
        if(ratingRunnable!=null){
            ratingRunnable.run();
        }
        else{
            Log.e("RatingManager","Rating runnable not set");
        }
    }


    public void setRatingRunnable(Runnable runnable){
        this.ratingRunnable=runnable;
    }

    public void reset(){
        int defaultHoldBackCount=getHoldBackDefault();
        sharedPreferences.edit().clear().apply();
        sharedPreferences.edit().putInt(DEFAULT_HOLD_BACK_PREF, defaultHoldBackCount).apply();

    }

    public void launchRateOnStore() {
        try {
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + context.getPackageName())));

        }catch (android.content.ActivityNotFoundException anfe) {
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + context.getPackageName())));
        }
    }
}
