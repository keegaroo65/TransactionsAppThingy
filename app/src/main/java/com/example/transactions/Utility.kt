package com.example.transactions

import android.util.Log
import androidx.annotation.FloatRange
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.colorspace.ColorSpaces
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.example.transactions.data.Recurring
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import kotlin.math.abs

private val TAG = "Utility"

class Utility {
    companion object {
        //// Constants

        val TRANSACTION_CATEGORIES = arrayOf(
            "Emergency",
            "Games/Toys",
            "Giving",
            "Groceries",
            "Housing",
            "Housing Bills",
            "Insurance",
            "Investing",
            "Maintenance",
            "Online",
            "Phone & WiFi",
            "Quick Food",
            "Recreation",
            "Recurring",
            "Saving",
            "Snacks",
            "Transportation"
        )

        //// Various utility functions

        fun time(): Long {
            return System.currentTimeMillis()
        }

        fun formatMoney(
            money: Int
        ): String {
            // Format the double as currency text (exactly 2 decimal places)
            var text = String.format("$%.2f", abs(money / 100.0))

            // Add the - before the $ if the balance is negative
            if (money < 0) {
                text = "-$text"
            }

            return text
        }

        fun lerp(start: Color, stop: Color, @FloatRange(from = 0.0, to = 1.0) fraction: Float): Color {
            val colorSpace = ColorSpaces.Oklab
            val startColor = start.convert(colorSpace)
            val endColor = stop.convert(colorSpace)

            val startAlpha = startColor.alpha
            val startL = startColor.red
            val startA = startColor.green
            val startB = startColor.blue

            val endAlpha = endColor.alpha
            val endL = endColor.red
            val endA = endColor.green
            val endB = endColor.blue

            val interpolated = Color(
                alpha = androidx.compose.ui.util.lerp(startAlpha, endAlpha, fraction),
                red = androidx.compose.ui.util.lerp(startL, endL, fraction),
                green = androidx.compose.ui.util.lerp(startA, endA, fraction),
                blue = androidx.compose.ui.util.lerp(startB, endB, fraction),
                colorSpace = colorSpace
            )
            return interpolated.convert(stop.colorSpace)
        }

        //// @Composable utility functions

        @Composable
        fun LoadingPopup() {
            Box(
                modifier = Modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .width(64.dp)
                )
            }
        }

        @Composable
        fun LoadingPopupCard() {
            Card(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(15.dp)
            ) {
                LoadingPopup()
            }
        }

        //// Date/time utility functions

        fun readablePastDate(
            then: Long
        ): String {
            val now = time()

            // Time from then until now converted to seconds (from milliseconds)
            val delta = ((now - then) / 1000).toDouble()

            //Log.i("hi","delta $delta")

            val text: String = if (delta < 10) {
                "now"
            } else if (delta < 60*60) {
                SimpleDateFormat("h:mm a").format(then)
            } else if (delta < 60*60*24) {
                SimpleDateFormat("EEEE 'at' h:mm a").format(then)
            } else if (delta < 60*60*24*7) {
                SimpleDateFormat("EEEE d 'at' h:mm a").format(then)
            } else {
                SimpleDateFormat("yyyy EEEE MMM d 'at' h:mm a").format(then)
            }

            return text
        }

        fun readableDate(
            then: Long
        ): String {
            return SimpleDateFormat("yyyy EEEE MMM d 'at' h:mm a").format(then)
        }

        fun isLeapYear(year: Int): Boolean {
            return ((year % 4) == 0 && (year % 100) != 0) || (year % 400) == 0
        }

        fun currentDayOfMonth(): Int {
            val now = time()
            val timezone = ZoneId.systemDefault().rules.getOffset(Instant.now())
            val date = LocalDateTime.ofEpochSecond(
                now / 1000L,
                0,
                timezone
            )
            return date.dayOfMonth
        }

        fun daysUntil(timestamp: Long): Int {
            val now = time()
            val timezone = ZoneId.systemDefault().rules.getOffset(Instant.now())
            val nowDate = LocalDateTime.ofEpochSecond(
                now / 1000L,
                0,
                timezone
            ).toLocalDate()
            val thenDate = LocalDateTime.ofEpochSecond(
                timestamp / 1000L,
                0,
                timezone
            ).toLocalDate()

            return ChronoUnit.DAYS.between(nowDate,thenDate).toInt()
        }

//        fun currentDayOfYear(): Int {
//            val now = time()
//            val timezone = ZoneId.systemDefault().rules.getOffset(Instant.now())
//            val date = LocalDateTime.ofEpochSecond(
//                now / 1000L,
//                0,
//                timezone
//            )
//            return date.dayOfMonth
//        }
//
//        fun dayOfYear(timestamp: Long): Int {
//            val timezone = ZoneId.systemDefault().rules.getOffset(Instant.now())
//            val date = LocalDateTime.ofEpochSecond(
//                timestamp / 1000L,
//                0,
//                timezone
//            )
//            return date.dayOfMonth
//        }

        // Will always occur at 5am on the set day
        fun nextCharge(recurring: Recurring, _lastCharge: Long? = null): Long {
            Log.d(TAG, "trying recurring date ${recurring.id}")
            val type = recurring.type
            val period = recurring.period
            val lastCharge = _lastCharge ?: recurring.lastCharge // optional parameter to override the current lastCharge field, eg. when in the charging process

            val timezone = ZoneId.systemDefault().rules.getOffset(Instant.now())

            val now = LocalDateTime.now()
            val last = LocalDateTime.ofEpochSecond(
                lastCharge / 1000L,
                0,
                timezone
            )

//            val nowMonth = now.monthValue
//            val nowMonthDay = now.dayOfMonth

            val lastMonth = last.monthValue
            val lastMonthT = last.month
            val lastMonthDay = last.dayOfMonth
            val lastYearDay = last.dayOfYear
            val lastYear = last.year

            var nextChargeDate = LocalDateTime.ofEpochSecond(
                0L,
                0,
                timezone
            )

            // Type 0: Same day every month (eg. June `period`th then July `period`th then August `period`th
            if (type == 0) {
                val nextMonth =
//                    if (now.year == lastYear && now.monthValue == lastMonth)
//                        lastMonth // This is the first transaction and we want it to continue
//                    else
                        (lastMonth % 12) + 1

                nextChargeDate = LocalDateTime.of(
                    if (nextMonth == 1) lastYear + 1 else lastYear,
                    nextMonth,
                    period,
                    3,
                    0
                )
            }

            // Type 1: Every `period` days (eg. `period` = 9 so April 3rd then April 12th then April 21st
            else if (type == 1) {
                val leapYear = isLeapYear(lastYear)
                val newDay = lastYearDay + period
                val daysInYear = if (leapYear) 366 else 365

                nextChargeDate = LocalDateTime.of(
                    if (newDay > daysInYear)
                        lastYear + 1
                    else
                        lastYear,
                    1,
                    1,
                    3,
                    0
                ).withDayOfYear(
                    if (newDay > daysInYear)
                        newDay - daysInYear // year changed, subtract the # of days in the last year
                    else
                        newDay // year did not changed so continue normally
                )
            }

            // Error case
            else {
                Log.e(TAG, "Invalid 'type' for recurring id ${recurring.id} of ${recurring.type}")
            }

            // INCREDIBLY USEFUL DEBUG FOR THIS FUNCTION
            Log.d(TAG,"""
                Calculating next charge date:
                $lastYear $lastMonthT ($lastMonth) $lastMonthDay
                to (type $type; wanting day(s) $period)
                ${nextChargeDate.year} ${nextChargeDate.month} (${nextChargeDate.monthValue}) ${nextChargeDate.dayOfMonth}
            """.trimIndent())

            return nextChargeDate.atZone(timezone).toInstant().toEpochMilli()
        }
    }
}

class MoneyTransformation: VisualTransformation {
    companion object {
        var curText = ""
        var curTran = ""
        var dec = 0
    }

    override fun filter(text: AnnotatedString): TransformedText {
        val ogText = text.toString()

        var transformedText = ogText

        if (ogText.isNotEmpty()) {
            val len = ogText.length

            if (len > 2) {
                transformedText = "$" + ogText.substring(0, len - 2) + "." + ogText.substring(len - 2)
                dec = len - 2
            }
            else if (len == 2) {
                transformedText = "$0.$ogText"
                dec = len - 1
            }
            else {
                transformedText = "$0.0$ogText"
            }
        }

        curText = ogText
        curTran = transformedText

        return TransformedText(
            text = AnnotatedString(text = transformedText),
            offsetMapping = MoneyOffsetMapping()
        )
    }
}

class MoneyOffsetMapping: OffsetMapping {
    // @param: offset -> offset (cursor position) of original text
    override fun originalToTransformed(offset: Int): Int {
        val textLen = MoneyTransformation.curText.length
        val tranLen = MoneyTransformation.curTran.length
        var new = offset

        if (offset <= 0) {
            return if (textLen <= 0) {
                //Log.i("offset", "B$offset $offset $textLen")
                offset
            } else if (textLen > 2)
                1
            else {
                //Log.i("offset", "B$offset ${textLen - 1} $textLen")
                tranLen - textLen
            }
        }

        new = if (textLen > 2) {
            //new = offset + 1
            if (offset < textLen - 1)
                offset + 1
            else
                offset + 2
        } else if (textLen == 2) {
            offset + 3
        } else {
            5
        }

        Log.i("offset", "B$offset $new $textLen")

        return new
    }

    override fun transformedToOriginal(offset: Int): Int {
        val textLen = MoneyTransformation.curText.length
        val tranLen = MoneyTransformation.curTran.length
        var new = offset

        if (offset <= 0) {
            Log.i("offset", "A$offset $offset $textLen")
            return offset
        }

        if (textLen > 2) {
            new = if (offset <= 1)
                0
            else if (offset < tranLen - 3)
                offset - 1
            else if (offset < tranLen - 1)
                textLen - 2
            else
                textLen - (tranLen - offset)

        }
        else if (textLen == 2) {
            new = if (offset < 4) {
                0
            } else {
                offset - 3
            }
        }
        else {
            new =
            if (offset < 5)
                0
            else
                1
        }

        Log.i("offset", "A$offset $new $textLen")

        return new
    }
}

/*@Composable
fun CenterCard(
    modifier: Modifier = Modifier,
    shape: Shape = CardDefaults.shape,
    colors: CardColors = CardDefaults.cardColors(),
    elevation: CardElevation = CardDefaults.cardElevation(),
    border: BorderStroke? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    Box(
        modifier,
        contentAlignment = Alignment.Center,
    ) {
        Card {
            content()
        }
    }
}*/