package com.example.tp_final

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.SystemClock
import android.view.DragEvent
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Chronometer
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.iterator
import kotlin.properties.Delegates

class JouerActivity : AppCompatActivity() {

    private lateinit var partie: Partie
    private lateinit var chrono: Chronometer
    private lateinit var nbCarte: TextView
    private lateinit var txtScore: TextView
    private lateinit var btnRetour: Button

    private lateinit var viewGroupMain: ViewGroup
    private lateinit var viewGroupBoard: ViewGroup
    private lateinit var viewGroupControl: ViewGroup

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_jouer)
        supportActionBar?.hide()

        // Démarrage du chronomêtre:
        chrono = findViewById(R.id.chronometer)
        chrono.base = SystemClock.elapsedRealtime()
        chrono.start()

        // Initialise la partie et récupere le nom du joueur passer par l'intent:
        partie = Partie(intent.getStringExtra("nomJoueur"), applicationContext, chrono)

        nbCarte = findViewById(R.id.card_cnt)
        nbCarte.text = partie.getNombreCarteRestante().toString()

        txtScore = findViewById(R.id.point_cnt)
        txtScore.text = "0"

        // On met le bouton retour en arriere a disabled par défaut, on utilisera la méthode :
        // updateAffichageControle() plus tard pour l'activer ou le désactiver en fonction du nombre
        // de carte en main.

        btnRetour = findViewById(R.id.btn_retour)
        btnRetour.isEnabled = false
        btnRetour.background = ContextCompat.getDrawable(applicationContext, R.drawable.btn_background_disabled)!!

        // Placer les écouteurs sur les cartes en mains, on place la main initiale
        // On set aussi l'élévation histoire de donner un drop shadow à nos cartes.
        viewGroupMain = findViewById(R.id.game_hand)

        // Puisque ma main est une liste de 8 cartes, la manière la plus simple de savoir ou je suis est
        // de garder un compteur de carte pour l'index de la carte courrante, on l'initialise a 0
        var carteCnt = 0
        val main = partie.getCarteEnMain()

        // La structure de layout pour la main et les cartes est relativement complexe
        // J'itère initalement sur chacune des rangées
        for (colonne in viewGroupMain) {
            for (carte in colonne as ViewGroup) {
                if (carte is LinearLayout) {
                    if (carteCnt < main.getList().size) {
                        val carteCourrante = main.getList()[carteCnt]

                        for (valeur in carte) {
                            (valeur as TextView).text = carteCourrante?.getValeur().toString()
                        }

                        carte.background = carteCourrante?.getBackground()
                        carte.elevation = 10f

                        carte.setOnTouchListener(TouchListener())
                        carteCnt++
                    }
                }
            }
        }

        // Placer les écouteurs sur les piles, la structure interne de layout est encore compliqué, mais très similaire à celle
        // expliqué précédement, je ne perdrai donc pas de temps à décrire en autant de détail

        viewGroupBoard = findViewById(R.id.game_board)

        var pileCnt = 0
        val pile = partie.getPiles()

        for (colonne in viewGroupBoard){
            for (box in colonne as ViewGroup){
                for (carte in box as ViewGroup){
                    if (carte is LinearLayout){
                        if (pileCnt < pile.size) {
                            val carteCourrante = pile[pileCnt].getCarteCourrante()

                            for (valeur in carte){
                                (valeur as TextView).text = carteCourrante.getValeur().toString()
                            }

                            carte.background = carteCourrante.getBackground()
                            carte.setOnDragListener(DragAndDropListener())
                            pileCnt++
                        }
                    }
                }
            }
        }

        // Ajoute les écouteurs sur les boutons d'options.

        viewGroupControl = findViewById(R.id.game_controle)

        for (button in viewGroupControl){
            button.setOnClickListener(ClickListener())
        }

    }

    inner class ClickListener : View.OnClickListener{
        override fun onClick(v: View) {

            when(v.id){
                R.id.btn_quitter -> {
                    chrono.stop() // peut etre pas nécessaire?
                    finish()
                }
                R.id.btn_retour -> {
                    partie.actionPrecedente()

                    updateAffichageMain()
                    updateAffichageBoard()
                    updateAffichageCarteRestante()
                    updateAffichageScore()
                    updateAffichageControle()

                }
            }
        }
    }

    inner class TouchListener : View.OnTouchListener {
        @SuppressLint("ClickableViewAccessibility")
        override fun onTouch(v: View, event: MotionEvent): Boolean{
            val dragShadow: View.DragShadowBuilder = View.DragShadowBuilder(v)
            v.startDragAndDrop(null, dragShadow, v, 0)
            v.visibility = View.INVISIBLE
            return true
        }
    }

    inner class DragAndDropListener : View.OnDragListener{
        private lateinit var cartePrise: View
        private lateinit var mainCourrante: Main
        private lateinit var pileCourrante: Pile
        private var indexCarteCourrante by Delegates.notNull<Int>()

        @SuppressLint("UseCompatLoadingForDrawables")
        override fun onDrag(v: View, event: DragEvent): Boolean {
            mainCourrante = partie.getCarteEnMain()
            pileCourrante = partie.getPiles()[getIndexPileCourrante(v.id)]
            cartePrise = event.localState as View
            indexCarteCourrante = getIndexCarteCourrante(cartePrise.id)

            when(event.action){
                DragEvent.ACTION_DRAG_ENDED -> { if (!event.result) { cartePrise.visibility = View.VISIBLE } }
                DragEvent.ACTION_DROP -> {
                    if (pileCourrante.verifJouerCarte(mainCourrante.getList()[indexCarteCourrante])){
                        partie.updateScore(mainCourrante.getList()[indexCarteCourrante]!!, pileCourrante.getCarteCourrante() ,chrono)
                        pileCourrante.setCarteCourrante(mainCourrante.getList()[indexCarteCourrante]!!)
                        partie.setDernierePile(pileCourrante)
                        mainCourrante.jouerCarte(indexCarteCourrante)

                        // Met a jour l'affichage de l'état du jeu
                        updateAffichageBoard()
                        updateAffichageCarteRestante()
                        updateAffichageScore()
                        updateAffichageControle()

                        //Si apres le drop il ne nous reste que 6 carte en main on pige des cartes.
                        // Fonction intéressante de Kotlin qui nous permet de vérifier si une valeur spécifique est présente dans une liste
                        if (mainCourrante.getList().count { it == null } == 2){

                            mainCourrante.pigerCarte(partie.getDeck(), chrono)
                            updateAffichageMain()
                        }

                        // On vérifie ensuite l'état du jeu, si on ne peut plus jouer de carte, ou que le nombre de carte restante est a 0, on passe a l'activité de conclusion
                        if (partie.verifGameState()){

                            chrono.stop() // Peut-être pas nécessaire?

                            val intent = Intent(this@JouerActivity, ConclusionActivity::class.java)
                            intent.putExtra("resultat", partie.getResultatPartie())
                            intent.putExtra("nbCarteRestante", partie.getNombreCarteRestante())
                            intent.putExtra("score", partie.getScore())
                            intent.putExtra("nomJoueur", partie.getNomJoueur())
                            startActivity(intent)
                            finish()
                        }

                    }
                    else{
                        return false
                    }
                }
            }
            return true
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        // Je redéfini la méthode afin d'ignorer les inputs sur ce bouton
    }
    fun updateAffichageCarteRestante() {
        partie.setNombreCarteRestante()
        nbCarte.text = partie.getNombreCarteRestante().toString()
    }
    fun updateAffichageMain() {
        var carteCnt = 0
        val main = partie.getCarteEnMain()

        for (colonne in viewGroupMain) {
            for (carte in colonne as ViewGroup) {
                if (carte is LinearLayout) {
                    if (carteCnt < main.getList().size) {
                        val carteCourrante = main.getList()[carteCnt]

                        for (valeur in carte) {
                            (valeur as TextView).text = carteCourrante?.getValeur().toString()
                            if(valeur.text.equals("null")){
                                valeur.text = ""
                            }
                        }

                        carte.background = carteCourrante?.getBackground()
                        carte.visibility = View.VISIBLE
                        carteCnt++
                    }
                }
            }
        }
    }

    fun updateAffichageBoard(){
        var pileCnt = 0
        val pile = partie.getPiles()

        for (colonne in viewGroupBoard){
            for (box in colonne as ViewGroup){
                for (carte in box as ViewGroup){
                    if (carte is LinearLayout){
                        if (pileCnt < pile.size) {
                            val carteCourrante = pile[pileCnt].getCarteCourrante()

                            for (valeur in carte){
                                (valeur as TextView).text = carteCourrante.getValeur().toString()
                            }

                            carte.background = carteCourrante.getBackground()
                            pileCnt++
                        }
                    }
                }
            }
        }
    }
    fun updateAffichageScore(){
        txtScore.text = partie.getScore().toString()
    }

    fun updateAffichageControle(){
        btnRetour.isEnabled = partie.getCarteEnMain().getList().count { it == null } == 1

        btnRetour.background = if (btnRetour.isEnabled) {
            ContextCompat.getDrawable(applicationContext, R.drawable.btn_background)!!
        } else {
            ContextCompat.getDrawable(applicationContext, R.drawable.btn_background_disabled)!!
        }
    }
    fun getIndexCarteCourrante(idCarteCourrante: Int): Int{
        var index: Int = 0

        when(idCarteCourrante){
            R.id.card_1 -> index = 0
            R.id.card_2 -> index = 1
            R.id.card_3 -> index = 2
            R.id.card_4 -> index = 3
            R.id.card_5 -> index = 4
            R.id.card_6 -> index = 5
            R.id.card_7 -> index = 6
            R.id.card_8 -> index = 7
        }

        return index
    }
    fun getIndexPileCourrante(idPileCourrante: Int): Int{
        var index: Int = 0

        when(idPileCourrante) {
            R.id.stack_desc_1 -> index = 0
            R.id.stack_desc_2 -> index = 1
            R.id.stack_asc_1 -> index = 2
            R.id.stack_asc_2 -> index = 3
        }

        return index
    }

}
