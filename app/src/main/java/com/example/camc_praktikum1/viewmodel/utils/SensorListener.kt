package com.example.camc_praktikum1.viewmodel.utils

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import kotlin.math.pow
import kotlin.math.sqrt

class SensorListener(
    sensorType: SensorTypeData,
    private val writeDataStrings: Boolean = true,
) :
    DataCollector(sensorType),
    SensorEventListener
{
    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // kann leer sein, muss aber implementiert werden
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event?.let {
            if (writeDataStrings) {
                when (event.sensor?.type) {
                    Sensor.TYPE_ACCELEROMETER -> {
                        SensorTypeData.Accelerometer.dataString.value =
                            buildDatastringForXYZ(event.values, "m/s²")
                    }

                    Sensor.TYPE_LINEAR_ACCELERATION -> {
                        SensorTypeData.LinearAccel.dataString.value =
                            buildDatastringForXYZ(event.values, "m/s²")
                    }

                    Sensor.TYPE_GYROSCOPE -> {
                        SensorTypeData.Gyroscope.dataString.value =
                            buildDatastringForXYZ(event.values, "deg/s", { f -> radToDeg(f) })
                    }

                    Sensor.TYPE_MAGNETIC_FIELD -> {
                        SensorTypeData.MagneticField.dataString.value =
                            buildDatastringForXYZ(event.values, "µT")
                    }

                    Sensor.TYPE_GRAVITY -> {
                        SensorTypeData.Gravity.dataString.value =
                            buildDatastringForXYZ(event.values, "m/s²")
                    }

                    Sensor.TYPE_ROTATION_VECTOR -> {
                        SensorTypeData.Rotation.dataString.value =
                            buildDatastringForXYZ(event.values, "") // no unit according to docs
                    }

                    Sensor.TYPE_ORIENTATION -> {
                        SensorTypeData.Orientation.dataString.value =
                            buildDatastringForXYZ(event.values, "deg")
                    }
                }
            }
            super.collectDatum(event)
        }
    }

    fun setWriteDataStrings(bool: Boolean) {

    }

    /*** ------------------- UTIL  ----------------------- ***/

    private inline fun buildDatastringForXYZ(
        values: FloatArray,
        unit: String,
        conversionFnc: (Float) -> Float = { f -> f },
        decimalPlaces: Int = 2,
    ): String {
        if(values.size < 3)
            return "SensorEvent.values.size must be >= 3"

        for(i in 0..2) {
            values[i] = conversionFnc(values[i])
        }

        val dp = decimalPlaces.toString()
        return "X: %.${dp}f $unit\nY: %.${dp}f $unit\nZ: %.${dp}f $unit".format(
            values[0], // conversionFnc(values[0]),
            values[1], //conversionFnc(values[1]),
            values[2], //conversionFnc(values[2]),
            //getMagnitude(event.values)
        )
    }

    /**
     * Berechnet die Magnitude für gegebene (Sensor-)Werte.
     * @param values array mit den Werten
     * @return Wurzel der Quadratsumme der Werte
     */
    private inline fun getMagnitude(values: FloatArray): Float {
        var result = 0.0f
        values.forEach { result += it.pow(2) }
        return sqrt(result)
    }

    /**
     * Wandelt Radians in Grad um.
     * @param rad der Radians Wert
     * @return der entsprechende Grad Wert
     */
    private inline fun radToDeg(rad: Float): Float {
        return (rad * 180 / Math.PI).toFloat()
    }
}