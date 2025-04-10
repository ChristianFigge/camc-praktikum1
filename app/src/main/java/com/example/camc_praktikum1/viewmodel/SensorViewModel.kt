package com.example.camc_praktikum1.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class SensorViewModel private constructor(

): ViewModel() {
    companion object { // static member(s) in kotlin
        @Volatile
        private var instance: SensorViewModel? = null

        fun getInstance() =
            instance ?: synchronized(this) {
                SensorViewModel().also { instance = it }
            }
    }

    val test = mutableStateOf("")

    fun getTestString(): String {
        return test.value
    }

    fun setTestString(strInput: String) {
        test.value += strInput
    }
}