package com.example.tp_final

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper


class BaseDeDonnee(ctx: Context) : SQLiteOpenHelper(ctx, "db", null, 1) {

    private lateinit var database: SQLiteDatabase

    // méthode de base pour un Singleton
    companion object {
        @Volatile
        private var instance: BaseDeDonnee? = null

        fun getInstance(ctx: Context) =
            instance ?: synchronized(this) {
                instance ?: BaseDeDonnee(ctx).also { instance = it }
            }
    }

    override fun onCreate(db: SQLiteDatabase) {
        // Pour créer les tables et, si nécessaire, ajouter les enregistrements/tuples initiaux.
        // Appelée automatiquement une seule fois, lors de l'installation de l'application sur
        // le téléphone. Si on fait une erreure, nous devons désinstaller l'application
        val CREATE_TABLE_RESULTAT = "CREATE TABLE resultat(" +
                "_id integer PRIMARY KEY AUTOINCREMENT, nom_joueur TEXT, score INTEGER)"

        db.execSQL(CREATE_TABLE_RESULTAT)

    }


    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {

        val DROP_TABLE_RESULTAT = "DROP TABLE IF EXISTS resultat;"

        db.execSQL(DROP_TABLE_RESULTAT)
        onCreate(db)
    }

    fun ajouterResultatPartie(nomJoueur: String, score:Int){
        val cv = ContentValues()

        cv.put("nom_joueur", nomJoueur)
        cv.put("score", score)

        database.insert("resultat", null, cv)
    }

    fun ouvrirConnectionDb(){
        database = this.writableDatabase
    }

    fun fermerConnectionDb(){
        database.close()
    }

    fun retournerRecord(): Int{

        var resultat: Int = 0

        val curseur = database.rawQuery("SELECT MAX(score) FROM resultat", null)

        while(curseur.moveToNext()){
            resultat = curseur.getInt(0)
        }
        curseur.close()
        return resultat
    }

    fun retournerLeaderboard(): MutableList<Pair<String, Int>>{
        // Puisque les hashmap ne sont jamais placer en ordre j'ai choisi de faire une liste de "Pair", je peux donc ensuite
        // accéder au valeur first et second de ces Pair
        val resultat: MutableList<Pair<String, Int>> = mutableListOf()

        val curseur = database.rawQuery("SELECT nom_joueur, score FROM resultat ORDER BY score DESC LIMIT 3", null)

        while(curseur.moveToNext()){
            resultat.add(Pair(curseur.getString(0), curseur.getInt(1)))
        }
        curseur.close()
        return resultat
    }

}