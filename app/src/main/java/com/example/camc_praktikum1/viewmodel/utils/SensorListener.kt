package com.example.camc_praktikum1.viewmodel.utils

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import kotlin.math.pow
import kotlin.math.sqrt

class SensorListener(
    sensorType: SensorTypeData,
) :
    DataCollector(sensorType.name),
    SensorEventListener
{
    //var runDelayedLoop : Boolean = false

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // kann leer sein, muss aber implementiert werden
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event?.let {
            when (event.sensor?.type) {

                Sensor.TYPE_ACCELEROMETER -> {
                    SensorTypeData.Accelerometer.dataString.value =
                        "X: %.2f m/s²\nY: %.2f m/s²\nZ: %.2f m/s²".format(
                            event.values[0],
                            event.values[1],
                            event.values[2],
                            //getMagnitude(event.values)
                        )
                }

                Sensor.TYPE_LINEAR_ACCELERATION -> {
                    SensorTypeData.LinearAccel.dataString.value =
                        "X: %.2f m/s²\nY: %.2f m/s²\nZ: %.2f m/s²".format(
                            event.values[0],
                            event.values[1],
                            event.values[2],
                            //getMagnitude(event.values)
                        )
                }

                Sensor.TYPE_GYROSCOPE -> {
                    SensorTypeData.Gyroscope.dataString.value =
                        "X: %.2f deg/s\nY: %.2f deg/s\nZ: %.2f deg/s".format(
                            radToDeg(event.values[0]),
                            radToDeg(event.values[1]),
                            radToDeg(event.values[2]),
                            //radToDeg(getMagnitude(event.values))
                        )
                }

                Sensor.TYPE_MAGNETIC_FIELD -> {
                    SensorTypeData.MagneticField.dataString.value =
                        "X: %.2f µT\nY: %.2f µT\nZ: %.2f µT".format(
                            event.values[0],
                            event.values[1],
                            event.values[2]
                        )
                }
            }
            super.collectDatum(event)
        }
    }

    /*** ------------------- UTIL  ----------------------- ***/

    /**
     * Berechnet die Magnitude für gegebene (Sensor-)Werte.
     * @param values array mit den Werten
     * @return Wurzel der Quadratsumme der Werte
     */
    private fun getMagnitude(values: FloatArray): Float {
        var result = 0.0f
        values.forEach { result += it.pow(2) }
        return sqrt(result)
    }

    /**
     * Wandelt Radians in Grad um.
     * @param rad der Radians Wert
     * @return der entsprechende Grad Wert
     */
    private fun radToDeg(rad: Float): Double {
        return rad * 180 / Math.PI
    }
}