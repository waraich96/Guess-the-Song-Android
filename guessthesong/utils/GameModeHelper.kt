package com.example.guessthesong.utils

import com.example.guessthesong.model.GameMode

fun getGameMode(mode: String?) =
    when (mode) {
        GameMode.CLASSIC.name -> GameMode.CLASSIC
        GameMode.CURRENT.name -> GameMode.CURRENT
        else -> GameMode.CLASSIC
    }