package com.jcdevelopment.hotsdraftadviser

import android.app.Application
import android.util.Log
import org.opencv.android.OpenCVLoader

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        if (OpenCVLoader.initLocal()) {
            Log.i("OpenCV", "OpenCV initialized successfully")
        } else {
            Log.e("OpenCV", "OpenCV initialization failed!")
        }
    }
}