package com.mangotea.view

import android.graphics.Typeface
import android.os.Build
import android.widget.TextView

fun TextView.medium() {
    fontWeight = Medium
}

var TextView.fontWeight
    set(value) = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        typeface = Typeface.create(Typeface.DEFAULT, value, false)
    } else {
        setTypeface(Typeface.SERIF, Typeface.BOLD)
    }
    get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        typeface.weight
    } else {
        if (typeface.isBold) Bold else Normal
    }

const val Thin = 100

const val Extra_Light = 200
const val Ultra_Light = 200

const val Light = 300

const val Regular = 400
const val Normal = 400
const val Book = 400
const val Roman = 400

const val Medium = 600
const val Semi_Bold = 600
const val Demi_Bold = 600

const val Bold = 700

const val Extra_Bold = 800
const val Ultra_Bold = 800

const val Black = 900
const val Heavy = 900