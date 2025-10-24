package com.example.juego.DB

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.juego.DB.Player
import com.example.juego.Sesion.QRHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "PokemonGame.db"
        private const val DATABASE_VERSION = 1

        // Tabla de jugadores
        const val TABLE_PLAYERS = "players"
        const val COLUMN_ID = "id"
        const val COLUMN_USERNAME = "username"
        const val COLUMN_EMAIL = "email"
        const val COLUMN_PASSWORD = "password"
        const val COLUMN_SCORE = "score"
        const val COLUMN_MAX_SCORE = "max_score"
        const val COLUMN_QR_TOKEN = "qr_token"
        const val COLUMN_CREATED_AT = "created_at"

        // Tabla de Pokémon disponibles en la tienda
        const val TABLE_POKEMON_SHOP = "pokemon_shop"
        const val COLUMN_POKEMON_ID = "pokemon_id"
        const val COLUMN_POKEMON_NAME = "pokemon_name"
        const val COLUMN_POKEMON_IMAGE = "pokemon_image"
        const val COLUMN_POKEMON_PRICE = "price"
        const val COLUMN_POKEMON_DESCRIPTION = "description"

        // Tabla de Pokémon comprados por jugadores
        const val TABLE_PURCHASED_POKEMON = "purchased_pokemon"
        const val COLUMN_PURCHASE_ID = "purchase_id"
        const val COLUMN_PLAYER_ID = "player_id"
        const val COLUMN_PURCHASED_POKEMON_ID = "purchased_pokemon_id"
        const val COLUMN_PURCHASE_DATE = "purchase_date"

        // Tabla de recompensas QR
        const val TABLE_QR_REWARDS = "qr_rewards"
        const val COLUMN_QR_ID = "qr_id"
        const val COLUMN_QR_CODE = "qr_code"
        const val COLUMN_REWARD_TYPE = "reward_type" // 'pokemon' o 'points'
        const val COLUMN_REWARD_VALUE = "reward_value" // id del pokemon o cantidad de puntos
        const val COLUMN_IS_ACTIVE = "is_active"

        // Tabla de QR reclamados por jugadores
        const val TABLE_CLAIMED_REWARDS = "claimed_rewards"
        const val COLUMN_CLAIM_ID = "claim_id"
        const val COLUMN_CLAIM_PLAYER_ID = "claim_player_id"
        const val COLUMN_CLAIM_QR_ID = "claim_qr_id"
        const val COLUMN_CLAIM_DATE = "claim_date"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        // Crear tabla de jugadores
        val createPlayersTable = """
            CREATE TABLE $TABLE_PLAYERS (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_USERNAME TEXT UNIQUE NOT NULL,
                $COLUMN_EMAIL TEXT UNIQUE NOT NULL,
                $COLUMN_PASSWORD TEXT NOT NULL,
                $COLUMN_SCORE INTEGER DEFAULT 0,
                $COLUMN_MAX_SCORE INTEGER DEFAULT 0,
                $COLUMN_QR_TOKEN TEXT UNIQUE,
                $COLUMN_CREATED_AT TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            )
        """.trimIndent()

        // Crear tabla de tienda de Pokémon
        val createPokemonShopTable = """
            CREATE TABLE $TABLE_POKEMON_SHOP (
                $COLUMN_POKEMON_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_POKEMON_NAME TEXT NOT NULL,
                $COLUMN_POKEMON_IMAGE TEXT NOT NULL,
                $COLUMN_POKEMON_PRICE INTEGER NOT NULL,
                $COLUMN_POKEMON_DESCRIPTION TEXT
            )
        """.trimIndent()

        // Crear tabla de Pokémon comprados
        val createPurchasedTable = """
            CREATE TABLE $TABLE_PURCHASED_POKEMON (
                $COLUMN_PURCHASE_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_PLAYER_ID INTEGER NOT NULL,
                $COLUMN_PURCHASED_POKEMON_ID INTEGER NOT NULL,
                $COLUMN_PURCHASE_DATE TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                FOREIGN KEY($COLUMN_PLAYER_ID) REFERENCES $TABLE_PLAYERS($COLUMN_ID),
                FOREIGN KEY($COLUMN_PURCHASED_POKEMON_ID) REFERENCES $TABLE_POKEMON_SHOP($COLUMN_POKEMON_ID),
                UNIQUE($COLUMN_PLAYER_ID, $COLUMN_PURCHASED_POKEMON_ID)
            )
        """.trimIndent()

        // Crear tabla de recompensas QR
        val createQRRewardsTable = """
            CREATE TABLE $TABLE_QR_REWARDS (
                $COLUMN_QR_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_QR_CODE TEXT UNIQUE NOT NULL,
                $COLUMN_REWARD_TYPE TEXT NOT NULL,
                $COLUMN_REWARD_VALUE TEXT NOT NULL,
                $COLUMN_IS_ACTIVE INTEGER DEFAULT 1
            )
        """.trimIndent()

        // Crear tabla de QR reclamados
        val createClaimedRewardsTable = """
            CREATE TABLE $TABLE_CLAIMED_REWARDS (
                $COLUMN_CLAIM_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_CLAIM_PLAYER_ID INTEGER NOT NULL,
                $COLUMN_CLAIM_QR_ID INTEGER NOT NULL,
                $COLUMN_CLAIM_DATE TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                FOREIGN KEY($COLUMN_CLAIM_PLAYER_ID) REFERENCES $TABLE_PLAYERS($COLUMN_ID),
                FOREIGN KEY($COLUMN_CLAIM_QR_ID) REFERENCES $TABLE_QR_REWARDS($COLUMN_QR_ID),
                UNIQUE($COLUMN_CLAIM_PLAYER_ID, $COLUMN_CLAIM_QR_ID)
            )
        """.trimIndent()

        db?.execSQL(createPlayersTable)
        db?.execSQL(createPokemonShopTable)
        db?.execSQL(createPurchasedTable)
        db?.execSQL(createQRRewardsTable)
        db?.execSQL(createClaimedRewardsTable)

        // Insertar datos iniciales de la tienda (9 Pokémon)
        insertInitialPokemonData(db)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_CLAIMED_REWARDS")
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_QR_REWARDS")
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_PURCHASED_POKEMON")
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_POKEMON_SHOP")
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_PLAYERS")
        onCreate(db)
    }

    private fun insertInitialPokemonData(db: SQLiteDatabase?) {
        val pokemonList = listOf(
            PokemonShop(1, "Charizard", "charizard", 50, "Pokémon de tipo fuego/volador"),
            PokemonShop(2, "Blastoise", "blastoise", 45, "Pokémon de tipo agua"),
            PokemonShop(3, "Venusaur", "venusaur", 45, "Pokémon de tipo planta/veneno"),
            PokemonShop(4, "Gengar", "gengar", 40, "Pokémon de tipo fantasma/veneno"),
            PokemonShop(5, "Dragonite", "dragonite", 60, "Pokémon de tipo dragón/volador"),
            PokemonShop(6, "Snorlax", "snorlax", 35, "Pokémon de tipo normal"),
            PokemonShop(7, "Lapras", "lapras", 40, "Pokémon de tipo agua/hielo"),
            PokemonShop(8, "Gyarados", "gyarados", 55, "Pokémon de tipo agua/volador"),
            PokemonShop(9, "Alakazam", "alakazam", 45, "Pokémon de tipo psíquico")
        )

        pokemonList.forEach { pokemon ->
            val values = ContentValues().apply {
                put(COLUMN_POKEMON_NAME, pokemon.name)
                put(COLUMN_POKEMON_IMAGE, pokemon.image)
                put(COLUMN_POKEMON_PRICE, pokemon.price)
                put(COLUMN_POKEMON_DESCRIPTION, pokemon.description)
            }
            db?.insert(TABLE_POKEMON_SHOP, null, values)
        }
    }

    // ========== FUNCIONES DE JUGADORES (YA IMPLEMENTADAS) ==========

    fun registerPlayer(username: String, email: String, password: String): Long {
        val db = this.writableDatabase

        // Primero insertar sin token para obtener el ID
        val values = ContentValues().apply {
            put(COLUMN_USERNAME, username)
            put(COLUMN_EMAIL, email)
            put(COLUMN_PASSWORD, password)
            put(COLUMN_SCORE, 0)
            put(COLUMN_MAX_SCORE, 0)
        }

        val playerId = db.insert(TABLE_PLAYERS, null, values)

        if (playerId != -1L) {
            // Generar token seguro con el ID real
            val qrToken = QRHelper.generateSecureToken(playerId.toInt(), username)

            // Actualizar con el token
            val updateValues = ContentValues().apply {
                put(COLUMN_QR_TOKEN, qrToken)
            }
            db.update(TABLE_PLAYERS, updateValues, "$COLUMN_ID = ?", arrayOf(playerId.toString()))
        }

        db.close()
        return playerId
    }


    fun loginPlayer(usernameOrEmail: String, password: String): Boolean {
        val db = this.readableDatabase
        val query = """
            SELECT * FROM $TABLE_PLAYERS 
            WHERE ($COLUMN_USERNAME = ? OR $COLUMN_EMAIL = ?) 
            AND $COLUMN_PASSWORD = ?
        """.trimIndent()

        val cursor = db.rawQuery(query, arrayOf(usernameOrEmail, usernameOrEmail, password))
        val exists = cursor.count > 0
        cursor.close()
        db.close()
        return exists
    }

    fun checkUserExists(username: String, email: String): Boolean {
        val db = this.readableDatabase
        val query = """
            SELECT * FROM $TABLE_PLAYERS 
            WHERE $COLUMN_USERNAME = ? OR $COLUMN_EMAIL = ?
        """.trimIndent()

        val cursor = db.rawQuery(query, arrayOf(username, email))
        val exists = cursor.count > 0
        cursor.close()
        db.close()
        return exists
    }

    fun getPlayerData(usernameOrEmail: String): Player? {
        val db = this.readableDatabase
        val query = """
            SELECT * FROM $TABLE_PLAYERS 
            WHERE $COLUMN_USERNAME = ? OR $COLUMN_EMAIL = ?
        """.trimIndent()

        val cursor = db.rawQuery(query, arrayOf(usernameOrEmail, usernameOrEmail))
        var player: Player? = null

        if (cursor.moveToFirst()) {
            player = Player(
                id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                username = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USERNAME)),
                email = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL)),
                score = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_SCORE)),
                maxScore = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_MAX_SCORE)),
                qrToken = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_QR_TOKEN))
            )
        }

        cursor.close()
        db.close()
        return player
    }

    fun updateScore(username: String, newScore: Int): Boolean {
        val db = this.writableDatabase

        // Obtener el puntaje máximo actual
        val player = getPlayerData(username)
        val maxScore = if (newScore > (player?.maxScore ?: 0)) newScore else player?.maxScore ?: 0

        val values = ContentValues().apply {
            put(COLUMN_SCORE, newScore)
            put(COLUMN_MAX_SCORE, maxScore)
        }

        val result = db.update(TABLE_PLAYERS, values, "$COLUMN_USERNAME = ?", arrayOf(username))
        db.close()
        return result > 0
    }

    // ========== FUNCIONES PARA LA TIENDA DE POKÉMON ==========

    fun getAllPokemonShop(): List<PokemonShop> {
        val pokemonList = mutableListOf<PokemonShop>()
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_POKEMON_SHOP", null)

        if (cursor.moveToFirst()) {
            do {
                val pokemon = PokemonShop(
                    id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_POKEMON_ID)),
                    name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_POKEMON_NAME)),
                    image = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_POKEMON_IMAGE)),
                    price = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_POKEMON_PRICE)),
                    description = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_POKEMON_DESCRIPTION))
                )
                pokemonList.add(pokemon)
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return pokemonList
    }

    fun purchasePokemon(playerId: Int, pokemonId: Int): Boolean {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_PLAYER_ID, playerId)
            put(COLUMN_PURCHASED_POKEMON_ID, pokemonId)
        }

        val result = db.insert(TABLE_PURCHASED_POKEMON, null, values)
        db.close()
        return result != -1L
    }

    fun isPokemonPurchased(playerId: Int, pokemonId: Int): Boolean {
        val db = this.readableDatabase
        val query = """
            SELECT * FROM $TABLE_PURCHASED_POKEMON 
            WHERE $COLUMN_PLAYER_ID = ? AND $COLUMN_PURCHASED_POKEMON_ID = ?
        """.trimIndent()

        val cursor = db.rawQuery(query, arrayOf(playerId.toString(), pokemonId.toString()))
        val exists = cursor.count > 0
        cursor.close()
        db.close()
        return exists
    }

    fun getPurchasedPokemon(playerId: Int): List<PokemonShop> {
        val pokemonList = mutableListOf<PokemonShop>()
        val db = this.readableDatabase
        val query = """
            SELECT p.* FROM $TABLE_POKEMON_SHOP p
            INNER JOIN $TABLE_PURCHASED_POKEMON pp ON p.$COLUMN_POKEMON_ID = pp.$COLUMN_PURCHASED_POKEMON_ID
            WHERE pp.$COLUMN_PLAYER_ID = ?
        """.trimIndent()

        val cursor = db.rawQuery(query, arrayOf(playerId.toString()))

        if (cursor.moveToFirst()) {
            do {
                val pokemon = PokemonShop(
                    id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_POKEMON_ID)),
                    name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_POKEMON_NAME)),
                    image = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_POKEMON_IMAGE)),
                    price = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_POKEMON_PRICE)),
                    description = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_POKEMON_DESCRIPTION))
                )
                pokemonList.add(pokemon)
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return pokemonList
    }

    fun getMostExpensivePurchasedPokemon(playerId: Int): PokemonShop? {
        val db = this.readableDatabase
        val query = """
            SELECT p.* FROM $TABLE_POKEMON_SHOP p
            INNER JOIN $TABLE_PURCHASED_POKEMON pp ON p.$COLUMN_POKEMON_ID = pp.$COLUMN_PURCHASED_POKEMON_ID
            WHERE pp.$COLUMN_PLAYER_ID = ?
            ORDER BY p.$COLUMN_POKEMON_PRICE DESC
            LIMIT 1
        """.trimIndent()

        val cursor = db.rawQuery(query, arrayOf(playerId.toString()))
        var pokemon: PokemonShop? = null

        if (cursor.moveToFirst()) {
            pokemon = PokemonShop(
                id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_POKEMON_ID)),
                name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_POKEMON_NAME)),
                image = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_POKEMON_IMAGE)),
                price = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_POKEMON_PRICE)),
                description = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_POKEMON_DESCRIPTION))
            )
        }

        cursor.close()
        db.close()
        return pokemon
    }

    // ========== FUNCIONES PARA QR ==========

    fun getPlayerByQRToken(qrToken: String): Player? {
        val db = this.readableDatabase
        val query = "SELECT * FROM $TABLE_PLAYERS WHERE $COLUMN_QR_TOKEN = ?"
        val cursor = db.rawQuery(query, arrayOf(qrToken))
        var player: Player? = null

        if (cursor.moveToFirst()) {
            player = Player(
                id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                username = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USERNAME)),
                email = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL)),
                score = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_SCORE)),
                maxScore = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_MAX_SCORE)),
                qrToken = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_QR_TOKEN))
            )
        }

        cursor.close()
        db.close()
        return player
    }

    fun createQRReward(qrCode: String, rewardType: String, rewardValue: String): Long {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_QR_CODE, qrCode)
            put(COLUMN_REWARD_TYPE, rewardType)
            put(COLUMN_REWARD_VALUE, rewardValue)
            put(COLUMN_IS_ACTIVE, 1)
        }

        val result = db.insert(TABLE_QR_REWARDS, null, values)
        db.close()
        return result
    }

    fun claimQRReward(playerId: Int, qrCode: String): Pair<Boolean, String> {
        val db = this.writableDatabase

        // Verificar si el QR existe y está activo
        val qrQuery = "SELECT * FROM $TABLE_QR_REWARDS WHERE $COLUMN_QR_CODE = ? AND $COLUMN_IS_ACTIVE = 1"
        val qrCursor = db.rawQuery(qrQuery, arrayOf(qrCode))

        if (!qrCursor.moveToFirst()) {
            qrCursor.close()
            db.close()
            return Pair(false, "QR inválido o inactivo")
        }

        val qrId = qrCursor.getInt(qrCursor.getColumnIndexOrThrow(COLUMN_QR_ID))
        val rewardType = qrCursor.getString(qrCursor.getColumnIndexOrThrow(COLUMN_REWARD_TYPE))
        val rewardValue = qrCursor.getString(qrCursor.getColumnIndexOrThrow(COLUMN_REWARD_VALUE))
        qrCursor.close()

        // Verificar si ya fue reclamado por este jugador
        val claimQuery = "SELECT * FROM $TABLE_CLAIMED_REWARDS WHERE $COLUMN_CLAIM_PLAYER_ID = ? AND $COLUMN_CLAIM_QR_ID = ?"
        val claimCursor = db.rawQuery(claimQuery, arrayOf(playerId.toString(), qrId.toString()))

        if (claimCursor.count > 0) {
            claimCursor.close()
            db.close()
            return Pair(false, "Ya reclamaste esta recompensa")
        }
        claimCursor.close()

        // Registrar el reclamo
        val values = ContentValues().apply {
            put(COLUMN_CLAIM_PLAYER_ID, playerId)
            put(COLUMN_CLAIM_QR_ID, qrId)
        }
        db.insert(TABLE_CLAIMED_REWARDS, null, values)
        db.close()

        return Pair(true, "$rewardType:$rewardValue")
    }

    private fun generateQRToken(username: String): String {
        val timestamp = System.currentTimeMillis()
        return "QR_${username}_$timestamp".hashCode().toString()
    }
}

// Data class para Pokémon de la tienda
data class PokemonShop(
    val id: Int,
    val name: String,
    val image: String,
    val price: Int,
    val description: String
)
