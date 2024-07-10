package com.example.tp_final

import android.content.Context

class Pile (valeur: Int, ctx:Context) {
    private var carteCourrante: Carte
    private lateinit var cartePrecedente: Carte
    private var estPileDesc: Boolean

    init {
        this.carteCourrante = Carte(valeur, ctx)
        this.estPileDesc = valeur == 98
    }

    // Setter
    fun setCarteCourrante(carteArrivante: Carte){
        this.cartePrecedente = this.carteCourrante
        this.carteCourrante = carteArrivante
    }

    // Getter
    fun getCarteCourrante(): Carte {return carteCourrante}
    fun getCartePrecedente(): Carte {return cartePrecedente}

    // Fonction de vérification
    fun verifJouerCarte(carteJouer: Carte?): Boolean{
        // Vérification plus importante pour la fin de la partie, on s'assure que si la "carte" qui est drag
        // n'est pas null, si elle est null on retourne faux directement.
        if(carteJouer == null) {return false}

        // On vérifie d'abord si la carte est +/- 10 que la carte courrante, puisque nous pourrons la placer peut importe la pile
        if (carteJouer.getValeur() == this.carteCourrante.getValeur() + 10 || carteJouer.getValeur() == this.carteCourrante.getValeur() - 10) { return true }

        // On vérifie ensuite le type de pile, si elle est descendante alors on vérifie que la carte est plus petite, sinon on vérifie si elle est plus grande
        if(this.estPileDesc){ if (carteJouer.getValeur() < this.carteCourrante.getValeur()) {return true} }
        else { if (carteJouer.getValeur() > this.carteCourrante.getValeur()) {return true} }

        return false
    }


}