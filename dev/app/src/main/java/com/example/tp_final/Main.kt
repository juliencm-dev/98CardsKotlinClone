package com.example.tp_final

import android.widget.Chronometer

class Main(deck: Deck, chrono: Chronometer) {

    private var main: MutableList<Carte?>
    private var indexDerniereCarte: Int? = null

    init {
        this.main = MutableList(8) {deck.pigeCarte(chrono)}
    }

    //Getter
    fun getList(): MutableList<Carte?> {return main}

    // Fonction d'action
    fun insererCarte(carte: Carte){
        this.main[this.indexDerniereCarte!!] = carte
        this.indexDerniereCarte = null
    }
    fun pigerCarte(deck: Deck, chrono: Chronometer) {
        for (i in main.indices) {
            if (main[i] == null) {
                // Si la valeur dans la liste est null, on la remplace par une nouvelle carte qui provient du deck
                if (deck.getCarte().isNotEmpty()){
                    main[i] = deck.pigeCarte(chrono)
                }
            }
        }
    }
    fun jouerCarte(indexCarteCourrante: Int) {
        this.indexDerniereCarte = indexCarteCourrante
        // Lorsqu'une carte est jouée on remplace la valeur dans la liste par null
        main[indexCarteCourrante] = null

    }


}