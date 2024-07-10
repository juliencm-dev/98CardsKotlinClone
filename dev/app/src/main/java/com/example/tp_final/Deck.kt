package com.example.tp_final

import android.content.Context
import android.widget.Chronometer

class Deck(ctx:Context, nbCarte:Int) {

    private var cartes : MutableList<Carte> = mutableListOf()

    init {
        for (i in 1..nbCarte){
            cartes.add(Carte(i, ctx))
        }
        cartes.shuffle()
    }

    // Getter
    fun pigeCarte(chrono: Chronometer): Carte {
        cartes[0].setCartePigeA(chrono)
        return cartes.removeAt(0)
    }

    fun getCarte(): MutableList<Carte> {
        return cartes
    }

}