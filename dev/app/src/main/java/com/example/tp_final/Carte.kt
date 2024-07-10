package com.example.tp_final

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.SystemClock
import android.widget.Chronometer
import androidx.core.content.ContextCompat
import kotlin.math.ceil
import kotlin.properties.Delegates

class Carte(valeur: Int, ctx:Context) {

    private var valeur: Int
    private var background: Drawable
    private var cartePigeA by Delegates.notNull<Long>()

    init {

        this.valeur = valeur
        this.background = getBackgroundDrawable(valeur, ctx)

    }

    private fun getBackgroundDrawable(valeur:Int, ctx:Context): Drawable{
        val carteRange = 14.0 // 97 cartes, 7 backgrounds, 13.86 cartes par background, j'arrondi a 14
        lateinit var background:Drawable

        // Si la valeur de la carte est 0 ou 98 retourne directement la base de carde vide.
        if(valeur == 0 || valeur == 98){ return ContextCompat.getDrawable(ctx, R.drawable.card_base_empty)!! }

        // Calcule le range de 15 carte dans laquel se retrouve la valeur de notre carte
        // Valeur de notre carte divisé par une étendue de 15 le tout arrondi a l'entier le plus élevé (nous donne un index de 1.0 à 7.0)
        val rangeBackground = ceil(valeur / carteRange)

        // On sélectionne le bon Drawable
        when(rangeBackground){
            1.0 -> background = ContextCompat.getDrawable(ctx, R.drawable.card_base_1)!!
            2.0 -> background = ContextCompat.getDrawable(ctx, R.drawable.card_base_2)!!
            3.0 -> background = ContextCompat.getDrawable(ctx, R.drawable.card_base_3)!!
            4.0 -> background = ContextCompat.getDrawable(ctx, R.drawable.card_base_4)!!
            5.0 -> background = ContextCompat.getDrawable(ctx, R.drawable.card_base_5)!!
            6.0 -> background = ContextCompat.getDrawable(ctx, R.drawable.card_base_6)!!
            7.0 -> background = ContextCompat.getDrawable(ctx, R.drawable.card_base_7)!!
        }

        return background
    }

    //Setter
    fun setCartePigeA(chrono: Chronometer) { this.cartePigeA = SystemClock.elapsedRealtime() - chrono.base}

    // Getter
    fun getValeur(): Int {return valeur}
    fun getBackground(): Drawable {return background}
    fun getTempsDepuisPige (chrono: Chronometer): Long {return SystemClock.elapsedRealtime() - chrono.base - this.cartePigeA}

}