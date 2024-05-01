package com.buntlit.pictureoftheday.ui.view

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import java.util.*


class DateMask(
    private var editText: EditText,
    private var maxDate: Long,
    private var minDate: Long
) : TextWatcher {

    private var currentString: String
    private val mask: String
    private val dividerCharacter: String
    private var calendar: Calendar

    init {
        this.editText.addTextChangedListener(this)
        currentString = ""
        mask = "DDMMYYYY"
        calendar = Calendar.getInstance()
        dividerCharacter = "."
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        if (s.toString() == currentString) {
            return
        }
        var cleanString = s.toString().replace("[^\\d.]|\\.".toRegex(), "")
        val currentCleanString = currentString.replace("[^\\d.]|\\.".toRegex(), "")

        val cleanStringLength = cleanString.length
        var selectionPos = cleanStringLength

        var i = 2
        while (i <= cleanStringLength && i < 6) {
            selectionPos++
            i += 2
        }

        if (cleanString == currentCleanString) {
            selectionPos--
        }

        if (cleanStringLength < 8) {
            cleanString += mask.substring(cleanString.length)
        } else {
            var day = cleanString.substring(0, 2).toInt()
            var month = cleanString.substring(2, 4).toInt()
            var year = cleanString.substring(4, 8).toInt()

            month = if (month < 1) 1 else if (month > 12) 12 else month
            calendar.set(Calendar.MONTH, month - 1)
            calendar.set(Calendar.YEAR, year)
            day = if (day > calendar.getActualMaximum(Calendar.DATE)) {
                if (month == 2) {
                    if (isLeap(year)) 29 else 28
                } else {
                    calendar.getActualMaximum(Calendar.DATE)
                }
            } else day
            calendar.set(Calendar.DAY_OF_MONTH, day)

            if (calendar.time.time > maxDate || calendar.time.time < minDate) {
                if (calendar.time.time > maxDate) {
                    calendar.time = Date(maxDate)
                } else {
                    calendar.time = Date(minDate)
                }
                println(calendar)
                day = calendar.get(Calendar.DAY_OF_MONTH)
                month = calendar.get(Calendar.MONTH) + 1
                year = calendar.get(Calendar.YEAR)
            }

            cleanString = String.format("%02d%02d%02d", day, month, year)
        }
        cleanString = cleanString.substring(0, 2) + dividerCharacter +
                cleanString.substring(2, 4) + dividerCharacter + cleanString.substring(4, 8)


        selectionPos = if (selectionPos < 0) 0 else selectionPos
        currentString = cleanString
        editText.setText(currentString)
        editText.setSelection(if (selectionPos < currentString.length) selectionPos else currentString.length)
    }

    override fun afterTextChanged(s: Editable?) {}

    private fun isLeap(year: Int): Boolean {
        return if (year % 1000 == 0) {
            true
        } else if (year % 100 == 0) {
            false
        } else year % 4 == 0
    }


}