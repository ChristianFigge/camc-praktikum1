package com.example.camc_praktikum1.ui.screens.components

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
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
import com.example.camc_praktikum1.data.models.SensorEventData
import com.example.camc_praktikum1.viewmodel.DataViewModel
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.sqrt


@Composable
fun DataPlotPanel(
    metaData: RecordingMetaData?,
    viewModel: DataViewModel,
    ctx: Context,
) {
    val data = viewModel.loadRecordingFromFile(metaData, ctx)

    if(data.isNullOrEmpty()) {
        Text("Wähle einen anderen Datensatz!")
        return
    }

    // TODO daten/logik ins viewmodel
    // ++++++++++++++++++++++++++++ data/logic ++++++++++++++++++++++++++++++++++++
    val PAGE_SIZE = min(500, data.size)
    var page_offset by remember{ mutableIntStateOf(0) }
    var limit_excl = page_offset + PAGE_SIZE
    val xPoints = MutableList<Point>(PAGE_SIZE) { _ -> Point(0.0f, 0.0f) }
    val yPoints = MutableList<Point>(PAGE_SIZE) { _ -> Point(0.0f, 0.0f) }
    val zPoints = MutableList<Point>(PAGE_SIZE) { _ -> Point(0.0f, 0.0f) }

    var dataView by remember { mutableStateOf(data.subList(0, PAGE_SIZE))}

    // max/min für valuerange der y-Achse
    var maxVal = Float.MIN_VALUE
    var minVal = Float.MAX_VALUE

    fun setPlotPointsForCurrentPage() {
        limit_excl =
            if (page_offset + PAGE_SIZE > data.size) data.size
            else page_offset + PAGE_SIZE

        var point_idx = 0
        val tNull = data[page_offset].timestampMillis
        for (data_idx in page_offset..< limit_excl) {
            val d = data[data_idx]
            val t = ((d.timestampMillis - tNull)).toFloat()

            xPoints[point_idx] = Point(t, d.values[0])
            yPoints[point_idx] = Point(t, d.values[1])
            zPoints[point_idx] = Point(t, d.values[2])

            val possibleMax = d.values.max()
            if (possibleMax > maxVal)
                maxVal = possibleMax

            val possibleMin = d.values.min()
            if(possibleMin < minVal)
                minVal = possibleMin
            point_idx++
        }

        // for last page, clear arrays from points of previous page if needed
        if(point_idx < xPoints.size) {
            // fill up array with last valid point
            val lastX = xPoints[point_idx-1]
            val lastY = yPoints[point_idx-1]
            val lastZ = zPoints[point_idx-1]

            for (i in point_idx..< xPoints.size) {
                xPoints[i] = lastX
                yPoints[i] = lastY
                zPoints[i] = lastZ
            }
        }

    }
    setPlotPointsForCurrentPage()

    fun getDataViewForCurrentPage(): List<SensorEventData> {
        return data.subList(page_offset, limit_excl)
    }

    // yCharts Axis Data
    val xAxisData = AxisData.Builder()
        .axisStepSize(1.dp) // 1.dp per ms
        .backgroundColor(Color.White)
        .steps(((data.last().timestampMillis - data.first().timestampMillis) / 100).toInt() ) // 100ms steps
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

    // yCharts LineChartData
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
                    LineStyle(color=Color.Red),
                    intersectionPoint,
                    SelectionHighlightPoint(),
                    null, //ShadowUnderLine(),
                    SelectionHighlightPopUp()
                ),
                Line(
                    dataPoints = zPoints,
                    LineStyle(color=Color.Green),
                    intersectionPoint,
                    SelectionHighlightPoint(),
                    null, //ShadowUnderLine(),
                    SelectionHighlightPopUp()
                ),
                 /*
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
                */
            ),
        ),
        xAxisData = xAxisData,
        yAxisData = yAxisData,
        gridLines = GridLines(),
        backgroundColor = Color.White
    )


    // +++++++++++++++++++++++++++++ UI DEF +++++++++++++++++++++++++++++++++
    Column(){

        Spacer(Modifier.height(20.dp))
        Text(metaData!!.sensorName, style= MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(20.dp))

        //Text("\uD83D\uDD35 X|Y|Z  \uD83D\uDD34 Magnitude  \uD83D\uDFE2 Durchschnitt")
        Text("\uD83D\uDD35 X  \uD83D\uDD34 Y  \uD83D\uDFE2 Z")
        Spacer(Modifier.height(10.dp))

        LineChart(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp),
            lineChartData = lineChartData
        )

        Spacer(Modifier.height(20.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Button(
                content = { Text("<") },
                enabled = page_offset - PAGE_SIZE >= 0,
                modifier = Modifier.padding(horizontal = 10.dp),
                onClick = {
                    if(page_offset - PAGE_SIZE >= 0) {
                        page_offset -= PAGE_SIZE // prev page
                        setPlotPointsForCurrentPage()
                        dataView = getDataViewForCurrentPage()
                    }
                }
            )

            Text("${page_offset}-${limit_excl}/${data.size}")

            Button(
                content = { Text(">") },
                enabled = page_offset + PAGE_SIZE < data.size,
                modifier = Modifier.padding(horizontal = 10.dp),
                onClick = {
                    if(page_offset + PAGE_SIZE < data.size) {
                        page_offset += PAGE_SIZE // next page
                        setPlotPointsForCurrentPage()
                        dataView = getDataViewForCurrentPage()
                    }
                }
            )
        }

        Spacer(Modifier.height(20.dp))
        HorizontalDivider()
        Spacer(Modifier.height(30.dp))

        Text("JSON Data:\n\n${dataView}")
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
