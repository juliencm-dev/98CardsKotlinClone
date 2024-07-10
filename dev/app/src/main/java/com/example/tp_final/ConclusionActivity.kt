package com.example.tp_final

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.iterator
import org.w3c.dom.Text

class ConclusionActivity : AppCompatActivity() {

    lateinit var btnRetourMenu: Button
    lateinit var db: BaseDeDonnee

    lateinit var txtGagnePerdre: TextView
    lateinit var txtScoreValue: TextView
    lateinit var txtCarteRestante: TextView

    lateinit var leaderboardViewGroup : ViewGroup


    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_conclusion)
        supportActionBar?.hide()

        db = BaseDeDonnee.getInstance(applicationContext)
        db.ouvrirConnectionDb()

        // Doit figure out comment afficher le leaderboard 10 premier score?

        btnRetourMenu = findViewById(R.id.btn_retour_menu)
        btnRetourMenu.setOnClickListener(RetourMenu())

        txtGagnePerdre = findViewById(R.id.txt_gagner_perdre)
        txtScoreValue = findViewById(R.id.conclusion_score_value)
        txtCarteRestante = findViewById(R.id.txt_carte_restante)

        leaderboardViewGroup = findViewById(R.id.conlusion_historique_score)

        val resultatPartie = intent.getBooleanExtra("resultat", false)
        val scoreFinal = intent.getIntExtra("score", 0)
        val nbCarteRestante = intent.getIntExtra("nbCarteRestante", 0)
        val nomJoueur = intent.getStringExtra("nomJoueur")

        if (resultatPartie) {
            txtGagnePerdre.text = "VOUS AVEZ GAGNÉ!"
        } else {
            txtGagnePerdre.text = "VOUS AVEZ PERDUE.."
        }

        txtScoreValue.text = scoreFinal.toString()
        txtCarteRestante.text = "$nbCarteRestante CARTES RESTANTES"

        db.ajouterResultatPartie(nomJoueur!!, scoreFinal)
        val resultat = db.retournerLeaderboard()
        for (i in resultat.indices){
            val layout = leaderboardViewGroup.getChildAt(i + 2) as LinearLayout
            (layout.getChildAt(0) as TextView).text = resultat[i].first
            (layout.getChildAt(1) as TextView).text = resultat[i].second.toString()
        }

    }

    override fun onStop() {
        super.onStop()
        db.fermerConnectionDb()
    }


    inner class RetourMenu: View.OnClickListener{
        override fun onClick(v: View?) {
            val intent = Intent(this@ConclusionActivity, MenuActivity::class.java)

            // On rappel l'instance existante du menu principal à la place d'en créer une nouvelle.
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
            finish()
        }
    }
}