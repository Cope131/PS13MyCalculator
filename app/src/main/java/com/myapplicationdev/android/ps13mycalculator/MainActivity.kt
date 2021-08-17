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
    private val allOperands = arrayListOf("0", "1", "2", "3", "4", "5", "6", "7", "8", "9")
    private val operandsAndOperators =
            arrayListOf("0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "/", "*", "-", "+", ".")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        tvOutput.text = "0"
    }

    fun onClickButton(view: View) {
        val button = view as Button
        val buttonValue = button.text
        when (button.text) {
            btnAC.text -> clear()
            btnEquals.text ->  calculate()
            btnSign.text -> appendSign()
            btnRemainder.text -> appendModulo()
            else -> formEquation(buttonValue)
        }
    }

    private fun updateOutputDisplay() {
        tvOutput.text = output
    }

    private fun appendModulo() {
        if (output.isEmpty() || output == "0") {
            return
        }
        // prevent duplicates
        val lastChar = output[output.length-1]
        if (lastChar != '%' && !operators.contains(lastChar.toString())) {
            output += "%"
            updateOutputDisplay()
            return
        }
        // replace last operator
        if (operators.contains(lastChar.toString())) {
            output = output.substring(0, output.length - 1)
            output += "%"
            updateOutputDisplay()
        }
    }

    private fun appendSign() {
        if (output.isEmpty() || output == "0") {
            return
        }
        Log.d(debugTag, output[0] + "")
        output = when (output[0]) {
            '-' -> output.substring(1, output.length)
            else -> "-$output"
        }
        updateOutputDisplay()
    }

    private fun formEquation(value: CharSequence) {
        if (operandsAndOperators.contains(value)) {
            // check last operator - remove if its operator to prevent duplicates
            if (output.isNotEmpty()) {
                val lastChar = output[output.length - 1]
                if (operators.contains(lastChar.toString()) && operators.contains(value)) {
                    output = output.substring(0, output.length - 1)
                }
                // append * after modulo sign (if present)
                if (lastChar == '%' && allOperands.contains(value.toString())) {
                    output += "*"
                }
            }
            output += value
            updateOutputDisplay()
        }
    }

    private fun clear() {
        output = "0"
        tvOutput.text = output
    }

    private fun calculate() {
        if (output.isEmpty()) {
            return
        }

        var result: Double
        var isNegative = false
        // remove last operator (if present)
        when (output.length) {
            1 -> {
                if (operators.contains(output[0].toString())) {
                    output = ""
                }
            }
            else -> {
                val lastCharIdx = output.length-1
                val lastChar = output[lastCharIdx].toString()
                if (operators.contains(lastChar)) {
                    output = output.substring(0, lastCharIdx)
                }
            }
        }

        // check for negative sign for first operand
        if (output[0] == '-') {
            isNegative = true
            output = output.substring(1, output.length)
        }

        // store operands & operators used
        val operands = output.split("-", "+", "/", "*").toMutableList()
        if (operands.isNotEmpty() && isNegative) {
            val firstOp = operands[0]
            operands[0] = "-$firstOp"
        }
        val usedOperators = arrayListOf<String>()
        for (char in output) {
           if (operators.contains(char.toString())) {
               usedOperators.add(char.toString())
           }
        }
        // calculate operands with %
        operands.forEachIndexed { idx, operand ->
            val moduloIdx = operand.indexOf("%")
            if (moduloIdx != -1) {
                val operandWithoutMod = operand.substring(0, operand.length - 1)
                operands[idx] = (operandWithoutMod.toDouble() / 100).toString()
            }
        }

        Log.d(debugTag, operands.toString())
        Log.d(debugTag, usedOperators.toString())
        // initial value of result
        if (operands.isEmpty()) {
            return
        }
        val firstOperand = operands[0].trim()
        if (firstOperand.isEmpty()) {
            return
        }
        result = firstOperand.toDouble()
        // calculation
        operands.forEachIndexed { idx, operand ->
            Log.d(debugTag, "$idx $operand")
            val nextOperandIdx = idx + 1
            if (nextOperandIdx < operands.size) {
                val nextOperandDbl = operands[nextOperandIdx].toDoubleOrNull()
                if (nextOperandDbl != null) {
                    val operator = usedOperators[idx]
                    Log.d(debugTag, "operator: $operator")
                    Log.d(debugTag, "next operand: $nextOperandDbl")
                    when (usedOperators[idx]) {
                        "+" -> result += nextOperandDbl
                        "-" -> result -= nextOperandDbl
                        "/" -> result /= nextOperandDbl
                        else -> result *= nextOperandDbl
                    }
                }
            }
            Log.d(debugTag, "$idx : $result")
        }
        Log.d(debugTag, result.toString())
        tvOutput.text =  when (result.rem(1)) {
            0.0 -> result.toInt().toString()
            else -> result.toString()
        }
        output = result.toString()
    }
}