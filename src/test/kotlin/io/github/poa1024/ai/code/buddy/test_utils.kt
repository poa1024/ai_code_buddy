package io.github.poa1024.ai.code.buddy

fun String.moreOrLessSimilarTo(s: String) = this.trimmed() == s.trimmed()

fun String.trimmed(): String {

    val lines = trimIndent().lines()
    val minMargin = lines
        .filter { it.isNotBlank() }
        .minOfOrNull { it.indexOfFirst { !it.isWhitespace() } } ?: 0

    return lines
        .dropWhile(String::isBlank)
        .dropLastWhile(String::isBlank)
        .map { it.drop(minMargin) }
        .map { it.trimEnd() }
        .joinToString("\n")
}