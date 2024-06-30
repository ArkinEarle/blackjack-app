package com.arkinearle.pixelblackjack

import android.os.Bundle
import android.util.DisplayMetrics
import android.util.TypedValue
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import com.arkinearle.pixelblackjack.R
import org.w3c.dom.Text


class Card(val suit: String, val rank: String) {
    var owner: String = ""
}

class Deck(nmbDecks: Int) {
    val suits = listOf("hearts", "diamonds", "clubs", "spades")
    val ranks = listOf("2", "3", "4", "5", "6", "7", "8", "9", "10", "j", "q", "k", "ace")

    // Initialize the cards array
    val cards = mutableListOf<Card>()

    init {
        // Populate the cards array using nested loops
        for (suit in suits) {
            for (rank in ranks) {
                repeat(nmbDecks) {
                    cards.add(Card(suit, rank))
                }
            }
        }
    }
//    fun printCards() {
//        // Example usage: Print the cards
//        cards.forEach { card ->
//            Log.d("Deck", "${card.suit}_${card.rank}")
//        }
//    }

    fun shuffle() {
        cards.shuffle()
    }

    fun popLastCard(): Card? {
        return if (cards.isNotEmpty()) {
            cards.removeAt(cards.size - 1)
        } else {
            null
        }
    }
}


class MainActivity : AppCompatActivity() {
    private lateinit var dShoe: Deck
    private var pTurn: Boolean = true
    private var dTurn: Boolean = false
    private var pTotal: Int = 0
    private var dTotal: Int = 0
    private var pAceCount: Int = 0
    private var dAceCount: Int = 0
    private var drawnCardsP: Int = 0
    private var drawnCardsD: Int = 0
    private lateinit var hiddenCard: Card
    private  var cardBackId: Int = 0
    private  var hiddenValue: Int = 0

    private lateinit var dHandText: TextView
    private lateinit var pHandText: TextView
    private lateinit var logText: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnHit = findViewById<ImageButton>(R.id.btn_hit)
        val btnStand = findViewById<ImageButton>(R.id.btn_stand)
        val btnRestart = findViewById<ImageButton>(R.id.btn_restart)

        pHandText = findViewById(R.id.player_hand)
        dHandText = findViewById(R.id.dealer_hand)
        logText = findViewById(R.id.update_txt)

//        val logText = findViewById<TextView>(R.id.log_txt)
        setupHands()

        btnHit.setOnClickListener {
            disableButtons(btnHit, btnStand)
            // Handle click for btnHit
            drawCard("player")
            if (pTurn){
                enableButtons(btnHit, btnStand)
            }
        }

        btnStand.setOnClickListener {
            disableButtons(btnHit, btnStand)
            // Handle click for btnStand
            pTurn = false
//            revealHidden()
            dealerHit()
        }

        btnRestart.setOnClickListener {
            // Handle click for btnRestart
            dShoe = Deck(6)
            dShoe.shuffle()
            pTurn = true
            pTotal = 0
            dTotal = 0
            pAceCount = 0
            dAceCount = 0
            drawnCardsD = 0
            drawnCardsP = 0
            clearViews()
            setupHands()
        }
    }

    private fun disableButtons(vararg buttons: ImageButton) {
        buttons.forEach { it.isEnabled = false }
    }
    private fun enableButtons(vararg buttons: ImageButton) {
        buttons.forEach { it.isEnabled = true }
    }

    private fun clearViews() {
        findViewById<LinearLayout>(R.id.p_hand).removeAllViews()
        findViewById<LinearLayout>(R.id.d_hand).removeAllViews()
    }

    private fun setupHands() {
        val btnHit = findViewById<ImageButton>(R.id.btn_hit)
        val btnStand = findViewById<ImageButton>(R.id.btn_stand)
        val btnRestart = findViewById<ImageButton>(R.id.btn_restart)

        enableButtons(btnHit, btnStand)
        disableButtons(btnRestart)
        dShoe = Deck(6)
        dShoe.shuffle()
        pTurn = true
        pTotal = 0
        dTotal = 0
        pAceCount = 0
        dAceCount = 0
        drawnCardsD = 0
        drawnCardsP = 0
        dTurn = true

        drawCard("player")
        drawCard("dealer")
        drawCard("player")
        drawCard("dealer")


//        disableButtons(btnHit, btnStand)
        if (pTurn) {
            logText.text = "New Hand"
        }

        if (pTotal == 21) {
            pTurn = false
            disableButtons(btnHit, btnStand)
            dealerHit()
        }


        dTurn = false
    }

    // Function to convert dp to px
    private fun dpToPx(dp: Int): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp.toFloat(),
            resources.displayMetrics
        ).toInt()
    }


    private fun revealHidden() {
        val hiddenCardString = "${hiddenCard?.suit}_${hiddenCard?.rank}"
        dTotal += hiddenValue

//         remove hidden card back
        findViewById<LinearLayout>(R.id.d_hand).removeView(findViewById(cardBackId))

        // Display hidden card
        val dHandLayout = findViewById<LinearLayout>(R.id.d_hand)
        displayCard(dHandLayout, hiddenCardString, -20)
        updateHand(hiddenCard.rank, "dealer")
    }

    private fun dealerHit() {
        val btnHit = findViewById<ImageButton>(R.id.btn_hit)
        val btnStand = findViewById<ImageButton>(R.id.btn_stand)
        disableButtons(btnHit, btnStand)

        revealHidden()
        checkAces("dealer")
        dTurn = true

        while (dTotal < 17) {
            drawCard("dealer")
        }

        // Determine and display results after dealer finishes drawing cards
        determineWinner()
    }


    private fun determineWinner() {

        val btnRestart = findViewById<ImageButton>(R.id.btn_restart)

        if (pTotal <= 21) {
            updateHandText("dealer")
        }
        updateHandText("player")


        if (pTotal > 21) {
//            dHandText.text = "Dealer wins: $dTotal Ace count: $dAceCount"
//            pHandText.text = "Player busted: $pTotal Ace count: $pAceCount"
            logText.text = "Dealer wins"
        } else if (dTotal > 21) {
//            dHandText.text = "Dealer busted: $dTotal Ace count: $dAceCount"
//            pHandText.text = "Player wins: $pTotal Ace count: $pAceCount"
            logText.text = "Player wins"
        } else if (pTotal == dTotal) {
//            dHandText.text = "Dealer ties: $dTotal Ace count: $dAceCount"
//            pHandText.text = "Player ties: $pTotal Ace count: $pAceCount"
            logText.text = "Tie"
        } else if (pTotal == 21 && dTotal < 21) {
//            dHandText.text = "Dealer loses: $dTotal Ace count: $dAceCount"
//            pHandText.text = "Player wins: $pTotal Ace count: $pAceCount"
            logText.text = "Player wins"
        } else if (dTotal == 21 && pTotal < 21) {
//            dHandText.text = "Dealer has more: $dTotal Ace count: $dAceCount"
//            pHandText.text = "Player loses: $pTotal Ace count: $pAceCount"
            logText.text = "Dealer wins"
        } else if (dTotal < 21 && pTotal < dTotal) {
//            dHandText.text = "Dealer has more: $dTotal Ace count: $dAceCount"
//            pHandText.text = "Player loses: $pTotal Ace count: $pAceCount"
            logText.text = "Dealer wins"
        } else if (pTotal < 21 && pTotal > dTotal) {
//            dHandText.text = "Dealer loses: $dTotal Ace count: $dAceCount"
//            pHandText.text = "Player has more: $pTotal Ace count: $pAceCount"
            logText.text = "Player wins"
        }

        enableButtons(btnRestart)
    }

    private fun drawCard(owner: String) {
        val pHandLayout = findViewById<LinearLayout>(R.id.p_hand)
        val dHandLayout = findViewById<LinearLayout>(R.id.d_hand)
        val lastCard = dShoe.popLastCard()
        val lastCardString = "${lastCard?.suit}_${lastCard?.rank}"
        val cardBack = "back_1"

        val cOffsetP = -20
        if (owner == "player") {
            if (lastCard != null) {
                drawnCardsP += 1
                logText.text = "Drew: ${lastCard.rank}"
                if (drawnCardsP == 1) {
                    displayCard(pHandLayout, lastCardString, 0)
                    updateHand(lastCard.rank, "player")
                } else {
                    displayCard(pHandLayout, lastCardString, cOffsetP)
                    updateHand(lastCard.rank, "player")
                }
            } else {
                println("No cards left in the deck.")
            }
        }

        val cOffsetD = -20
        if (owner == "dealer") {
            if (lastCard != null) {
                drawnCardsD += 1

                if (pTurn && drawnCardsD == 2) {
                    hiddenCard = lastCard
//                    println("hiddenCard: ${hiddenCard.rank}") // print to see hidden
                    displayCard(dHandLayout, cardBack, cOffsetD)
                    updateHand(lastCard.rank, "dealer")
                } else if (drawnCardsD == 1) {
                    logText.text = "Drew: ${lastCard.rank}"
                    displayCard(dHandLayout, lastCardString, 0)
                    updateHand(lastCard.rank, "dealer")
                } else {
                    logText.text = "Drew: ${lastCard.rank}"
                    displayCard(dHandLayout, lastCardString, cOffsetD)
                    updateHand(lastCard.rank, "dealer")
                }
//                } else {
//                    displayCard(dHandLayout, lastCardString, cOffsetD)
//                    updateHand(lastCard.rank, "dealer")
//                }
            } else {
                println("No cards left in the deck.")
            }
        }
    }

    private fun updateHandText(owner: String) {
        if (owner == "player") {
            if (pAceCount < 1) {
                pHandText.text = pTotal.toString()
            } else {
                pHandText.text = "${pTotal - 10}/${pTotal}"
            }
        } else if (owner == "dealer") {
            if (dAceCount < 1 || pTurn) {
                dHandText.text = dTotal.toString()
            } else {
                dHandText.text = "${dTotal - 10}/${dTotal}"
            }
        }
    }

    private fun updateHand(cardRank: String, owner: String) {


        val cardRankInt = when (cardRank) {
            "j", "q", "k" -> 10
            "ace" -> {
                if (owner == "player") {
                    pAceCount += 1
                } else if (owner == "dealer") {
                    dAceCount += 1
                }
                11
            }
            else -> cardRank.toIntOrNull() ?: 0
        }

        val btnHit = findViewById<ImageButton>(R.id.btn_hit)
        val btnStand = findViewById<ImageButton>(R.id.btn_stand)

        if (owner == "player" && pTurn) {
            pTotal += cardRankInt
            checkAces("player")
            if (pTotal > 21) {
                checkAces("player")
            } else if (pTotal == 21) {
                disableButtons(btnHit, btnStand)
                updateHandText("player")
                if (drawnCardsP != 2) {
                    pTurn = false
                    dealerHit()
                }
            } else {
                updateHandText("player")
            }

        }

        if (owner == "dealer" && dTurn) {
            if (drawnCardsD != 2) {
                dTotal += cardRankInt
                checkAces("dealer")
            } else {
                hiddenValue = cardRankInt
            }

            checkAces("dealer")
            if (dTotal == 21) {
                if (pTurn) {
                    dTurn = false
                    updateHandText("dealer")
                }
            }else {
                updateHandText("dealer")
            }
        }
    }

    private fun checkAces(owner: String) {
        val btnHit = findViewById<ImageButton>(R.id.btn_hit)
        val btnStand = findViewById<ImageButton>(R.id.btn_stand)

        if (owner == "player") {
            if (pTotal > 21) {
                if (pAceCount > 0) {
                    pAceCount -= 1
                    pTotal -= 10
                    checkAces("player")
                } else {
                    updateHandText("player")
                    disableButtons(btnHit, btnStand)

//                    revealHidden()
                    if (drawnCardsP != 2) {
                        pTurn = false
//                        revealHidden()
                        determineWinner()
                    }
                }
            } else {
                updateHandText("player")
            }
        }

        if (owner == "dealer") {
            if (dTotal > 21) {
                if (dAceCount > 0) {
                    dAceCount -= 1
                    dTotal -= 10
                    checkAces("dealer")
                } else {
                    updateHandText("dealer")
                    dTurn = false
                }
            } else {
                updateHandText("dealer")
            }
        }
    }

    private fun displayCard(handLayout: LinearLayout, drawable: String, offsetDp: Int) {
        // Create a new ImageView
        val newImageView = ImageView(this)
        val cardBack = "back_1"

        val drawableId = resources.getIdentifier(drawable, "drawable", packageName)
//        if (drawable == cardBack) {
//            cardBackId = drawableId
//        }


        // Set the image resource
        newImageView.setImageDrawable(ContextCompat.getDrawable(this, drawableId))

        if (drawable == cardBack) {
            cardBackId = drawableId
            newImageView.id = cardBackId
        }

        // Convert dp to px
        val widthInPx = dpToPx(37)
        val heightInPx = dpToPx(52)
        val offsetInPx = dpToPx(offsetDp)

        // Set layout parameters
        val layoutParams = LinearLayout.LayoutParams(widthInPx, heightInPx)
        layoutParams.gravity = android.view.Gravity.CENTER_VERTICAL // Center horizontally
        layoutParams.leftMargin = offsetInPx // Apply margin for overlapping
        newImageView.layoutParams = layoutParams



        // Set scale
        newImageView.scaleX = 1.5f
        newImageView.scaleY = 1.5f

        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        val screenWidth = displayMetrics.widthPixels

        val scaledWidth = (widthInPx * 1.5).toInt()

        val halfScreenWidth = screenWidth / 2

        newImageView.translationX = (halfScreenWidth - scaledWidth / 1.5).toFloat()

        // Add the new ImageView to the p_hand LinearLayout
        handLayout.addView(newImageView)
    }
}
