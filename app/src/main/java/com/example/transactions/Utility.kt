package com.example.transactions

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import kotlin.math.abs

class Utility {
    companion object {
        val TransactionCategories = arrayOf(
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
            "Saving",
            "Snacks",
            "Transportation"
        )

        fun time(): Long {
            return System.currentTimeMillis()
        }

        fun formatMoney(
            money: Double
        ): String {
            // Format the double as currency text (exactly 2 decimal places)
            var text = String.format("$%.2f", abs(money))

            // Add the - before the $ if the balance is negative
            if (money < 0.0) {
                text = "-$text"
            }

            return text
        }

        fun readableDate(
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