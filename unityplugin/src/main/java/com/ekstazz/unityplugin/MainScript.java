package com.ekstazz.unityplugin;

import static android.speech.SpeechRecognizer.ERROR_AUDIO;
import static android.speech.SpeechRecognizer.ERROR_CLIENT;
import static android.speech.SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS;
import static android.speech.SpeechRecognizer.ERROR_NETWORK;
import static android.speech.SpeechRecognizer.ERROR_NETWORK_TIMEOUT;
import static android.speech.SpeechRecognizer.ERROR_RECOGNIZER_BUSY;
import static android.speech.SpeechRecognizer.ERROR_SERVER;
import static android.speech.SpeechRecognizer.ERROR_SPEECH_TIMEOUT;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognitionService;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;
import java.util.ArrayList;
import android.app.Service;
import static android.speech.SpeechRecognizer.createSpeechRecognizer;


public class MainScript extends RecognitionService implements RecognitionListener
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
        //System.out.println("13456709876543ewrtyuiopuytre--------------");
        StartListening();
    }

    public static SpeechRecognizer m_EngineSR;
    public static Context mContext;
    public static MainScript mSpeechService;

    static String TAG = "VOICE RECOGNITION";


    public void StartListening()
    {
        System.out.println("-22----33----44----- " + "START THE SERVICE!");
        Intent intent = new Intent(unityActivity, MainScript.class);
        unityActivity.startService(intent);

        onCreate();
    }


    @Override
    public void onCreate()
    {
        System.out.println("-22----33----44----- " + "onCreate()");

        if(mSpeechService == null)
        {
            System.out.println("-22----33----44----- " + "start listening");

            mContext = unityActivity.getApplicationContext(); //getApplicationContext();
            mSpeechService = this;
            m_EngineSR = createSpeechRecognizer(this);
            m_EngineSR.setRecognitionListener(this);
            Intent voiceIntent = RecognizerIntent.getVoiceDetailsIntent(mContext);
            m_EngineSR.startListening(voiceIntent);
        }
        super.onCreate();
    }

    private void checkForCommands(Bundle bundle)
    {
        System.out.println("-22----33----44----- " + "check for commands");

        ArrayList<String> voiceText = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        if (voiceText != null)
        {
            System.out.println("-22----33----44----- " + "voice text not null");

            if (voiceText.size() > 0)
            {
                System.out.println("-22----33----44----- " + voiceText.get(0));
            }
            else
            {
                System.out.println("-22----33----44----- " + "nothing found");

                //SendMessageToUnity("nothing!");
            }

        }
        else
        {
            System.out.println("-22----33----44----- " + "voice text empty");

            //SendMessageToUnity("voiceText empty!");
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        System.out.println("-22----33----44----- " + "onStartCommand");

        //SendMessageToUnity("onStartCommand()!");
        if(mSpeechService != null)
        {
            System.out.println("-22----33----44----- " + "restarting");

            mContext = getApplicationContext();
            restartListening(mContext);
        }

        return Service.START_NOT_STICKY;
    }

    public static void restartListening(Context context_)
    {
        if(mSpeechService != null)
        {
            System.out.println("-22----33----44----- " + "restarting");

            mContext = context_;
            if (m_EngineSR == null)
            {
                m_EngineSR = createSpeechRecognizer(mSpeechService);
                m_EngineSR.setRecognitionListener(mSpeechService);
            }
            else
            {
                m_EngineSR.stopListening();
            }
            Intent voiceIntent = RecognizerIntent.getVoiceDetailsIntent(mContext);
            m_EngineSR.startListening(voiceIntent);
        }
    }

    //public void SendMessageToUnity(String text)
    //{
        /*if(m_EngineSR != null)
        {
            if(text != null && text.isEmpty())Log.i("TESTING: ", "final message! =" + text);
            try
            {
                if(UnityPlayer.currentActivity != null)
                {
                    UnityPlayer.UnitySendMessage("CubeMessage", "GetMessage", text);
                    SendStopWritingUnity();
                }
            }
            catch (Exception e)
            {
                // Log.e(TAG, "UnitySendMessage failed" + e.getMessage());
            }
        }*/
    //}

    public void SendErrorToUnity(String text)
    {
        if(m_EngineSR != null)
        {
            try
            {

            }
            catch (Exception e)
            {

            }
        }
    }

    public void SendStopWritingUnity()
    {
        if(m_EngineSR != null)
        {
            try
            {

            }
            catch (Exception e)
            {

            }
        }
    }

    @Override
    public void onDestroy()
    {
        if(m_EngineSR!= null)
        {
            m_EngineSR.cancel();
        }

        System.out.println("-22----33----44----- " + "service stopped");
        Log.i("SimpleVoiceService", "Service stopped");
        super.onDestroy();
    }

    @Override
    protected void onStartListening(Intent recognizerIntent, Callback listener)
    {
        System.out.println("-22----33----44----- " + "on start listening");
    }

    @Override
    protected void onCancel(Callback listener)
    {
        System.out.println("-22----33----44----- " + "onCancel");
    }

    @Override
    protected void onStopListening(Callback listener)
    {
        System.out.println("-22----33----44----- " + "onStopListening");
        SendStopWritingUnity();
    }


    @Override
    public void onReadyForSpeech(Bundle params)
    {
        System.out.println("-22----33----44----- " + "onReadyForSpeech");
    }

    @Override
    public void onBeginningOfSpeech()
    {
        System.out.println("-22----33----44----- " + "onBeginningOfSpeech");
    }

    @Override
    public void onRmsChanged(float rmsdB)
    {

    }

    @Override
    public void onBufferReceived(byte[] buffer)
    {

    }

    @Override
    public void onEndOfSpeech()
    {
        System.out.println("-22----33----44----- " + "onEndOfSpeech");
    }

    @Override
    public void onError(int error)
    {
        try
        {
            String message;
            switch (error)
            {
                case ERROR_AUDIO:
                    message = "Audio recording error";
                    break;
                case ERROR_CLIENT:
                    message = "Client side error";
                    break;
                case ERROR_INSUFFICIENT_PERMISSIONS:
                    message = "Insufficient permissions";
                    break;
                case ERROR_NETWORK:
                    message = "Network error";
                    break;
                case ERROR_NETWORK_TIMEOUT:
                    message = "Network timeout";
                    break;
                case SpeechRecognizer.ERROR_NO_MATCH:
                    message = "No match";
                    break;
                case ERROR_RECOGNIZER_BUSY:
                    message = "RecognitionService busy";
                    break;
                case ERROR_SERVER:
                    message = "error from server";
                    break;
                case ERROR_SPEECH_TIMEOUT:
                    message = "No speech input";
                    break;
                default:
                    message = "Didn't understand, please try again.";
                    break;
            }

            SendErrorToUnity(message);

            System.out.println("-22----33----44----- " + "error occurred " + message);

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void onResults(Bundle results)
    {
        checkForCommands(results);
    }

    @Override
    public void onPartialResults(Bundle partialResults)
    {
        System.out.println("-22----33----44----- " + "partial results");
    }

    @Override
    public void onEvent(int eventType, Bundle params)
    {
        System.out.println("-22----33----44----- " + "onEvent");
    }
}
