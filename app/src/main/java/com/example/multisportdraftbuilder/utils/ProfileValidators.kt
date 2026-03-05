package com.example.multisportdraftbuilder.utils

object ProfileValidators {

    fun sanitizeName(input: String): String {
        return input.filter { it.isLetterOrDigit() || it == ' ' }.take(32)
    }

    fun isValidName(name: String): Boolean = name.isNotBlank() && name.length <= 32

    fun hasValidDisciplines(selected: List<String>): Boolean = selected.size in 2..3

    fun hasValidPoints(pointsLeft: Int): Boolean = pointsLeft >= 0
}
