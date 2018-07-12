package com.anwesh.uiprojects.kotlindecreasingsquarewaveview

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.anwesh.uiprojects.decreasingsquarewaveview.DecreasingSquareWaveView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DecreasingSquareWaveView.create(this)
    }
}
