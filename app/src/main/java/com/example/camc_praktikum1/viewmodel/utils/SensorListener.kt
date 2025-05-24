package com.example.camc_praktikum1.viewmodel.utils

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener

class SensorListener(
    sensorType: SensorTypeData,
) :
    DataCollector(sensorType),
    SensorEventListener
{
    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // kann leer sein, muss aber implementiert werden
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event?.let { super.collectDatum(event) }
    }

}