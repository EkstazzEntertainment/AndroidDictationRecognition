package com.ekstazz.unityplugin;

import android.Manifest;
import android.app.Activity;
import android.app.Fragment;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.IllformedLocaleException;
import java.util.Locale;


public class MainScript extends Fragment
{
    private static final int REQUEST_SPEECH = 1;

    public int getSomeNum()
    {
        return 234;
    }

    public int Add(int i, int j)
    {
        return i + j;
    }

    private static Activity unityActivity;

    public static void receiveUnityActivity(Activity tActivity)
    {
        unityActivity = tActivity;
    }

    public void StartSpeechRecognition()
    {
        System.out.println("->->->->->->->->->--------------");
        RequestAudioPermission();
    }













    private static final String TAG = "SpeechRecognizer";
    // Singleton instance.
    public static MainScript instance;

    //UNITY CONTEXT
    String gameObjectName;
    String resultCallbackName = "OnResult";
    String errorCallbackName = "OnError";

    private static String finalResult = "";


    public static void SendUnityResults(String results)
    {
        //UnityPlayer.UnitySendMessage(instance.gameObjectName, instance.resultCallbackName, results);
        Log.d(TAG, results);

        finalResult = results;
    }

    public String GetResultString()
    {
        return finalResult;
    }

    public static void SendUnityError(ERROR error)
    {
        String errorString = String.valueOf(error);
        //UnityPlayer.UnitySendMessage(instance.gameObjectName, instance.errorCallbackName, errorString);
        Log.d(TAG, errorString);
    }

    public static void SetUp(String gameObjectName)
    {
        instance = new MainScript();
        instance.gameObjectName = gameObjectName; // Store 'GameObject' reference
        //UnityPlayer.currentActivity.getFragmentManager().beginTransaction().add(instance, SpeechRecognizerFragment.TAG).commit();
    }

    public void RequestAudioPermission()
    {
        RequestRecordAudioPermission();
    }

    //SPEECH RECOGNIZER
    public SpeechRecognizer sr;
    public SpeechRecognitionListener speechListener = new SpeechRecognitionListener();
    private static boolean isLanguageSpecified = false;
    private static String language = "en-US";
    private static int maxResults = 10;

    public enum ERROR
    {
        UNKNOWN, INVALID_LANGUAGE_FORMAT
    }


    private void RequestRecordAudioPermission()
    {
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(unityActivity, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED)
        {
            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(unityActivity, Manifest.permission.RECORD_AUDIO))
            {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            }
            else
            {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(unityActivity, new String[]{Manifest.permission.RECORD_AUDIO},20);
                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
        else
        {
            // Permission has already been granted
            Log.d(TAG, "Permission has already been granted");
        }
    }

    public void StartListening()
    {
        unityActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "StartListeningCalled");
                sr = SpeechRecognizer.createSpeechRecognizer(unityActivity);
                sr.setRecognitionListener(speechListener);

                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                if (isLanguageSpecified)
                {
                    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, language);
                }
                else
                {
                    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
                }
                intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, maxResults);

                try
                {
                    sr.startListening(intent);
                }
                catch (ActivityNotFoundException a)
                {
                    Log.d(TAG, a.toString());
                }
            }
        });
    }

    public void StartListening(boolean isContinuous, String language, int maxResults)
    {
        SetContinuousListening(isContinuous);
        SetLanguage(language);
        SetMaxResults(maxResults);
        StartListening();

        finalResult = "";
    }

    public void StopListening()
    {
        unityActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (sr != null)
                {
                    sr.destroy();
                    sr = null;
                }
            }
        });
    }

    public void RestartListening()
    {
        StopListening();
        StartListening();
    }

    public void SetContinuousListening(boolean isContinuous)
    {
        speechListener.continuousListening = isContinuous;
    }

    public void SetLanguage(String newLanguage)
    {
        try
        {
            Locale locale = new Locale.Builder().setLanguageTag(newLanguage).build();
            language = newLanguage;
            isLanguageSpecified = true;
        }
        catch (IllformedLocaleException exception)
        {
            isLanguageSpecified = false;
            SendUnityError(ERROR.INVALID_LANGUAGE_FORMAT);
        }
    }


    public void SetMaxResults(int newMaxResults)
    {
        maxResults = newMaxResults;
    }


    //SPEECH RECOGNIZER_LISTENER
    class SpeechRecognitionListener implements RecognitionListener
    {
        private ArrayList<String> resultData = new ArrayList<>();
        public boolean continuousListening = false;


        public void onReadyForSpeech(Bundle params)
        {
            Log.d(TAG, "onReadyForSpeech");
        }

        public void onBeginningOfSpeech()
        {
            Log.d(TAG, "onBeginningOfSpeech");
        }

        public void onRmsChanged(float rmsdB)
        {
            /*Log.d(TAG, "onRmsChanged");*/
        }

        public void onBufferReceived(byte[] buffer)
        {
            Log.d(TAG, "onBufferReceived");
        }

        public void onEndOfSpeech()
        {
            Log.d(TAG, "onEndOfSpeech");
        }

        public void onError(int error)
        {
            if(continuousListening)
            {
                RestartListening();
            }
        }

        public void onResults(Bundle results)
        {
            StringBuilder str = new StringBuilder();
            resultData = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            if (resultData != null)
            {
                str.append(resultData.get(0));
                for (int i = 1; i < resultData.size(); i++)
                {
                    str.append("~").append(resultData.get(i));
                }
            }

            System.out.println("->->->->->->->->->--------------" + str.toString());

            SendUnityResults(str.toString());
            if(continuousListening)
            {
                RestartListening();
            }
        }

        public void onPartialResults(Bundle partialResults)
        {
            Log.d(TAG, "onPartialResults");
        }

        public void onEvent(int eventType, Bundle params)
        {
            Log.d(TAG, "onEvent " + eventType);
        }
    }
}
