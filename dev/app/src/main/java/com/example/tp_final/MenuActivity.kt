package com.example.tp_final

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView

class MenuActivity : AppCompatActivity() {

    lateinit var btnDemarer:Button
    lateinit var db: BaseDeDonnee
    lateinit var record: TextView
    lateinit var inputNomJoueur: EditText
    lateinit var nomJoueur: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)
        supportActionBar?.hide()

        btnDemarer = findViewById(R.id.btn_demarrer)
        btnDemarer.setOnClickListener(LancerPartie())
    }

    override fun onStart() {
        super.onStart()

        db = BaseDeDonnee.getInstance(applicationContext)
        db.ouvrirConnectionDb()

        record = findViewById(R.id.menu_record_value)
        record.text = db.retournerRecord().toString()

    }

    override fun onStop() {
        super.onStop()
        db.fermerConnectionDb()
    }

    inner class LancerPartie: View.OnClickListener{
        override fun onClick(v: View?) {

            // Ramasser le nom du joueur:
            inputNomJoueur = findViewById(R.id.player_name)

            nomJoueur = if(inputNomJoueur.text.toString() != "") {
                inputNomJoueur.text.toString()
            } else {
                "Invité"
            }

            val intent = Intent(this@MenuActivity, JouerActivity::class.java)

            // On passe la valeur à la prochaine activity:
            intent.putExtra("nomJoueur", nomJoueur)
            inputNomJoueur.setText("")
            startActivity(intent)
        }
    }

}