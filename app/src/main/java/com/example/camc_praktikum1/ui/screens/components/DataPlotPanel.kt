package com.example.camc_praktikum1.ui.screens.components

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import co.yml.charts.axis.AxisData
import co.yml.charts.common.model.Point
import co.yml.charts.ui.linechart.LineChart
import co.yml.charts.ui.linechart.model.GridLines
import co.yml.charts.ui.linechart.model.IntersectionPoint
import co.yml.charts.ui.linechart.model.Line
import co.yml.charts.ui.linechart.model.LineChartData
import co.yml.charts.ui.linechart.model.LinePlotData
import co.yml.charts.ui.linechart.model.LineStyle
import co.yml.charts.ui.linechart.model.SelectionHighlightPoint
import co.yml.charts.ui.linechart.model.SelectionHighlightPopUp
import com.example.camc_praktikum1.data.models.RecordingMetaData
import com.example.camc_praktikum1.viewmodel.DataViewModel
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.sqrt


@Composable
fun DataPlotPanel(
    metaData: RecordingMetaData?,
    viewModel: DataViewModel,
    ctx: Context,
) {
    var data = viewModel.loadRecordingFromFile(metaData, ctx)

    if(data.isNullOrEmpty()) {
        Text("Wähle einen anderen Datensatz!")
        return
    }

    // calc müsste eigentlich ins viewmodel aber juckt gerade nicht
    var xPoints = MutableList<Point>(data.size) { _ -> Point(0.0f, 0.0f) }
    var yPoints = MutableList<Point>(data.size) { _ -> Point(0.0f, 0.0f) }
    var zPoints = MutableList<Point>(data.size) { _ -> Point(0.0f, 0.0f) }
    var magPoints = MutableList<Point>(data.size) { _ -> Point(0.0f, 0.0f) }
    var avrgPoints = MutableList<Point>(data.size) { _ -> Point(0.0f, 0.0f) }

    // max/min für valuerange der y-Achse
    var maxVal = Float.MIN_VALUE
    var minVal = Float.MAX_VALUE
    val tFirst = data.first().timestampNs
    data.forEachIndexed { i, data ->
        val t = ((data.timestampNs - tFirst) / 1000000 /* as ms */).toFloat()
        magPoints[i] = Point(t, getMagnitude(data.values))
        avrgPoints[i] = Point(t, data.values.average().toFloat())
        xPoints[i] = Point(t, data.values[0])
        yPoints[i] = Point(t, data.values[1])
        zPoints[i] = Point(t, data.values[2])
        avrgPoints

        val possibleMax = max(magPoints[i].y, data.values.max())
        if (possibleMax > maxVal)
            maxVal = possibleMax

        val possibleMin = min(magPoints[i].y, data.values.min())
        if(possibleMin < minVal)
            minVal = possibleMin
    }

    val xAxisData = AxisData.Builder()
        .axisStepSize(1.dp) // 1.dp per ms
        .backgroundColor(Color.White)
        .steps(((data.last().timestampNs - data.first().timestampNs) / 100000000).toInt() ) // 100ms steps
        .labelData { i -> (i).toString() + " ms" }
        .labelAndAxisLinePadding(15.dp)
        .build()

    val steps = 11
    val yStep = (maxVal - minVal) / steps
    val yAxisData = AxisData.Builder()
        .steps(steps)
        .backgroundColor(Color.White)
        .labelAndAxisLinePadding(20.dp)
        .labelData { i ->
            "%.1f".format(minVal + (yStep * i))
        }.build()

    val intersectionPoint = IntersectionPoint(radius=3.dp)
    val lineChartData = LineChartData(
        linePlotData = LinePlotData(
            lines = listOf(
                Line(
                    dataPoints = xPoints,
                    LineStyle(color=Color.Blue),
                    intersectionPoint,
                    SelectionHighlightPoint(),
                    null, //ShadowUnderLine(),
                    SelectionHighlightPopUp()
                ),
                Line(
                    dataPoints = yPoints,
                    LineStyle(color=Color.Blue),
                    intersectionPoint,
                    SelectionHighlightPoint(),
                    null, //ShadowUnderLine(),
                    SelectionHighlightPopUp()
                ),
                Line(
                    dataPoints = zPoints,
                    LineStyle(color=Color.Blue),
                    intersectionPoint,
                    SelectionHighlightPoint(),
                    null, //ShadowUnderLine(),
                    SelectionHighlightPopUp()
                ),
                Line(
                    dataPoints = magPoints,
                    LineStyle(color=Color.Red),
                    intersectionPoint,
                    SelectionHighlightPoint(),
                    null, //ShadowUnderLine(),
                    SelectionHighlightPopUp()
                ),
                Line(
                    dataPoints = avrgPoints,
                    LineStyle(color=Color.Green),
                    intersectionPoint,
                    SelectionHighlightPoint(),
                    null, //ShadowUnderLine(),
                    SelectionHighlightPopUp()
                ),
            ),
        ),
        xAxisData = xAxisData,
        yAxisData = yAxisData,
        gridLines = GridLines(),
        backgroundColor = Color.White
    )
    Column(){

        Spacer(Modifier.height(20.dp))
        Text(metaData!!.sensorName, style= MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(10.dp))
        Text("\uD83D\uDD35 X|Y|Z  \uD83D\uDD34 Magnitude  \uD83D\uDFE2 Durchschnitt")
        Spacer(Modifier.height(10.dp))
        LineChart(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp),
            lineChartData = lineChartData
        )

        Spacer(Modifier.height(20.dp))
        Text("JSON Data: \n\n$data")

    }

}

/**
 * Berechnet die Magnitude für gegebene (Sensor-)Werte.
 * @param values array mit den Werten
 * @return Wurzel der Quadratsumme der Werte
 */
fun getMagnitude(values: FloatArray): Float {
    var result = 0.0f
    values.forEach { result += it.pow(2) }
    return sqrt(result)
}
