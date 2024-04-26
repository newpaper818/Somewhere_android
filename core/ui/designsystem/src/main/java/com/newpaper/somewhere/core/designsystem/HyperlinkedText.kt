package com.newpaper.somewhere.core.designsystem

import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle


@Composable
fun HyperlinkedText(
    text: String,
    defaultTextStyle: TextStyle,
    onClickText: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val hyperlinkTextStyle = defaultTextStyle.copy(color = MaterialTheme.colorScheme.onSurfaceVariant, textDecoration = TextDecoration.Underline)

    val links = Regex("(https?://\\S+)|(www.\\S+)|(\\S+.com)")

    val annotatedStringList = remember {

        var lastIndex = 0
        val annotatedStringList = mutableStateListOf<AnnotatedString.Range<String>>()

        // Add a text range for link
        for (match in links.findAll(text)) {

            val start = match.range.first
            val end = match.range.last + 1
            val string = text.substring(start, end)

            if (start > lastIndex) {
                annotatedStringList.add(
                    AnnotatedString.Range(
                        text.substring(lastIndex, start),
                        lastIndex,
                        start,
                        "text"
                    )
                )
            }
            annotatedStringList.add(
                AnnotatedString.Range(string, start, end, "link")
            )
            lastIndex = end
        }

        // Add remaining text
        if (lastIndex < text.length) {
            annotatedStringList.add(
                AnnotatedString.Range(
                    text.substring(lastIndex, text.length),
                    lastIndex,
                    text.length,
                    "text"
                )
            )
        }
        annotatedStringList
    }

    // Build an annotated string
    val annotatedString = buildAnnotatedString {
        annotatedStringList.forEach {
            if (it.tag == "link") {
                pushStringAnnotation(tag = it.tag, annotation = it.item)
                withStyle(style = hyperlinkTextStyle.toSpanStyle()) { append(it.item) }
                pop()
            } else {
                withStyle(style = defaultTextStyle.toSpanStyle()) { append(it.item) }
            }
        }
    }

    val uriHandler = LocalUriHandler.current

    ClickableText(
        text = annotatedString,
        style = defaultTextStyle,
        modifier = modifier,
        onClick = { position ->

            val annotatedStringRange =
                annotatedStringList.firstOrNull {
                    it.start < position && position < it.end
                }

            if (annotatedStringRange?.tag == "link") {
                //open internet browser with url
                val url = annotatedStringRange.item
                if ("http" !in url)
                    uriHandler.openUri("https://$url")
                else
                    uriHandler.openUri(url)
            }
            else {
                onClickText()
            }
        },
        overflow = TextOverflow.Ellipsis
    )
}