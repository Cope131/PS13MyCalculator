package com.myapplicationdev.android.ps13mycalculator

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val debugTag = "MainActivity"
    private var output = ""
    private val operators = arrayListOf("/", "*", "-", "+")
    private val operandsAndOperators =
            arrayListOf("0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "/", "*", "-", "+")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun onClickButton(view: View) {
        val button = view as Button
        val buttonValue = button.text
        when (button.text) {
            "AC" -> clear()
            "=" ->  calculate()
            else -> formEquation(buttonValue)
        }
    }

    private fun formEquation(value: CharSequence) {
        if (operandsAndOperators.contains(value)) {
            // check last operator - remove if its operator to prevent duplicates
            if (output.isNotEmpty()) {
                val lastChar = output[output.length - 1]
                if (operators.contains(lastChar.toString()) && operators.contains(value)) {
                    output = output.substring(0, output.length - 1);
                }
            }
            output += value
            tvOutput.text = output
        }
    }

    private fun clear() {
        output = "0"
        tvOutput.text = output
    }

    private fun calculate() {
        var result = 0
        // store operands & operators used
        val operands = output.split("-", "+", "/", "*")
        val usedOperators = arrayListOf<String>()
        for (char in output) {
           if (operators.contains(char.toString())) {
               usedOperators.add(char.toString())
           }
        }
        Log.d(debugTag, operands.toString())
        Log.d(debugTag, usedOperators.toString())
        // initial value of result
        if (operands.isNotEmpty()) {
            result = operands[0].toInt()
        }
        // calculation
        operands.forEachIndexed { idx, operand ->
            Log.d(debugTag, "$idx $operand")
            val nextOperandIdx = idx + 1
            if (nextOperandIdx < operands.size) {
                val nextOperandInt = operands[nextOperandIdx].toIntOrNull()
                nextOperandInt.apply {
                    val operator = usedOperators[idx]
                    Log.d(debugTag, "operator: $operator")
                    Log.d(debugTag, "next operand: $nextOperandInt")
                    when (usedOperators[idx]) {
                        "+" -> result += nextOperandInt!!
                        "-" -> result -= nextOperandInt!!
                        "/" -> result /= nextOperandInt!!
                        else -> result *= nextOperandInt!!
                    }
                }
            }
            Log.d(debugTag, "$idx : $result")
        }
        Log.d(debugTag, result.toString())
        tvOutput.text = result.toString()
    }
}