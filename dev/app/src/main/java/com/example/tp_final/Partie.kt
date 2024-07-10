package com.example.tp_final

import android.content.Context
import android.widget.Chronometer
import kotlin.math.abs

class Partie(nomJoueur: String?, ctx:Context, chrono: Chronometer) {

    private val NOMBRE_DE_CARTE = 97

    private lateinit var dernierePile: Pile
    private var nomJoueur: String?
    private var nbCarteRestante: Int = NOMBRE_DE_CARTE
    private var score: Int = 0
    private var scorePrecedent: Int = 0
    private var deck: Deck = Deck(ctx, NOMBRE_DE_CARTE)
    private var main: Main = Main(this.deck, chrono)
    private var piles: MutableList<Pile> = mutableListOf(Pile(98, ctx), Pile(98, ctx), Pile(0, ctx), Pile(0, ctx))
    private var resultatPartie: Boolean = false

    init {
        this.nomJoueur = nomJoueur
    }

    // Setter
    private fun setScore(scoreValue: Int) {
        this.scorePrecedent = this.score
        this.score += scoreValue
    }
    fun setDernierePile(indexDernierePile: Pile){
        this.dernierePile = indexDernierePile
    }
    fun setNombreCarteRestante(){this.nbCarteRestante--}
    fun updateScore(carteJouer: Carte, carteSurLaPile:Carte, chrono: Chronometer) {
        val baseScore = 2000
        val modificateurProximite = abs(carteSurLaPile.getValeur() - carteJouer.getValeur() )
        val bonusRapidite = 10000 / (carteJouer.getTempsDepuisPige(chrono).toInt() / 1000)
        val bonusNbCarteRestante = 10000 / this.nbCarteRestante

        val scoreCarteJoue = (baseScore / modificateurProximite) + bonusRapidite + bonusNbCarteRestante

        this.setScore(scoreCarteJoue)
    }

    //Getter
    fun getNomJoueur(): String? { return nomJoueur }
    fun getScore(): Int { return score }
    fun getCarteEnMain(): Main{return main}
    fun getDeck(): Deck {return deck}
    fun getPiles(): MutableList<Pile> {return piles}
    fun getNombreCarteRestante(): Int{return this.nbCarteRestante}
    fun getResultatPartie(): Boolean {return resultatPartie}

    // Fonction de vérification
    fun verifGameState(): Boolean {
        if (nbCarteRestante == 0) {
            this.resultatPartie = true
            return true
        }

        if (this.nePeuPasJouerCarte()){
            this.resultatPartie = false
            return true
        }

        return false
    }
    private fun nePeuPasJouerCarte(): Boolean {

        for (pile in this.piles){
            for (carte in this.main.getList()){
                if (carte != null) {
                    if (pile.verifJouerCarte(carte)){ return false }
                }
            }
        }
        return true
    }
    fun actionPrecedente(){
        this.nbCarteRestante += 2
        this.score = this.scorePrecedent
        this.main.insererCarte(this.dernierePile.getCarteCourrante())
        this.dernierePile.setCarteCourrante(this.dernierePile.getCartePrecedente())
    }
}