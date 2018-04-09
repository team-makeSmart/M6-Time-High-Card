
/**
 * @author Hunter Mason, Jake McGhee, Mac Doussias, Pavlos Papadonikolakis
 * CLASS CST 338
 * Team MakeSmart
 * Assignment 6, Module 6
 * PHASE 2 MVC
 */

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

//--------------------Phase1 creates a MVC pattern----------------------
//
//Phase1 includes classes: Controller, CardView, CardModel
public class Phase2
{

   public static void main(String[] args)
   {
      int numPacksPerDeck = 1;
      int numJokersPerPack = 0;
      int numUnusedCardsPerPack = 0;
      CardModel.Card[] unusedCardsPerPack = null;

      // instantiate the CardGameFramework, used for gameplay
      Controller.highCardGame = new Controller.CardGameFramework(numPacksPerDeck, numJokersPerPack,
            numUnusedCardsPerPack, unusedCardsPerPack, Controller.NUM_PLAYERS, Controller.NUM_CARDS_PER_HAND);

      // establish main GUI frame in which the program will run
      Controller.setFrame();
      Controller.startTheGame();

      //Phase 2, add timer to gui
      MyClock gui = new MyClock();
      gui.setVisible(true);
   }

   // -----------------------------------------------------------------
   // -----------------------------------------------------------------
   // -----------------------------------------------------------------
   // --------------------start of class
   // Controller-----------------------------------------

   // class Controller controls the game
   static class Controller
   {
      public static CardGameFramework highCardGame;
      public static final int NUM_CARDS_PER_HAND = 7;
      public static final int NUM_PLAYERS = 2;
      private static JLabel[] computerLabels = new JLabel[NUM_CARDS_PER_HAND];
      private static JLabel[] humanLabels = new JLabel[NUM_CARDS_PER_HAND];
      private static JLabel[] playedCardLabels = new JLabel[NUM_PLAYERS];
      private static String[] playerNames =
      { "Computer", "You" };
      private static CardModel.Card[] cardPlayed = new CardModel.Card[NUM_PLAYERS];
      private static int[] score =
      { 0, 0 };
      private static boolean cardsClickable = true;
      private static boolean computerPlaysFirst = true;
      private static int gameIndex = 0;
      public static CardView.DisplayWinner winner;
      private static JPanel bottomPanel;
      public static JButton restartButton;
      public static JButton exitButton;
      private static CardView.CardTable myCardTable;
      private static String playedCardText = "";
      public static JPanel buttonsPanel;

      /**
       * Sets the frame for a new game
       */
      private static void setFrame()
      {
         myCardTable = new CardView.CardTable("High Card", NUM_CARDS_PER_HAND, NUM_PLAYERS);
         myCardTable.setSize(800, 850);
         myCardTable.setLayout(new GridLayout(4, 0, 0, 0));
         myCardTable.add(getBottomPanel());
         myCardTable.setLocationRelativeTo(null);
         myCardTable.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
         myCardTable.setVisible(true);
      }

      /**
       * Starts a new game
       */
      private static void startTheGame()
      {

         // MyClock gui = new MyClock();
         // gui.setVisible(true);

         // deal the cards
         highCardGame.deal();

         System.out.println("AI Hand: ");
         System.out.println(highCardGame.getHand(0).toString());

         System.out.println("\nPlayer Hand: ");
         System.out.println(highCardGame.getHand(1).toString());
         for (int i = 0; i < NUM_CARDS_PER_HAND; i++)
         {
            // fill the arrays with cards
            computerLabels[i] = new JLabel(CardView.GUICard.getBackCardIcon());
            // (uncomment to view computer hand)
            // computerLabels[i] = new
            // JLabel(GUICard.getIcon((highCardGame.getHand(0).inspectCard(i))));

            humanLabels[i] = new JLabel(CardView.GUICard.getIcon((highCardGame.getHand(1).inspectCard(i))));

            // add the back cards for the computer and the cards for the hand
            myCardTable.pnlComputerHand.add(computerLabels[i]);
            myCardTable.pnlHumanHand.add(humanLabels[i]);

            // make the human cards clickable for gameplay
            humanLabels[i].addMouseListener(new MouseAdapter()
            {
               @Override
               public void mouseClicked(MouseEvent e)
               {
                  onMouseClicked(e);
               }
            });
         }

         // Add cards and score to the GUI
         for (int i = 0; i < NUM_PLAYERS; i++)
         {
            String playedCardText = "<html><div style='text-align: center;'>" + playerNames[i] + "<br/>Score: "
                  + score[i];

            // add cards for each player
            playedCardLabels[i] = new JLabel(playedCardText);
            playedCardLabels[i].setBorder(new EmptyBorder(0, 0, 20, 0));
            playedCardLabels[i].setHorizontalAlignment(JLabel.CENTER);
            playedCardLabels[i].setVerticalAlignment(JLabel.BOTTOM);
            playedCardLabels[i].setIconTextGap(-1);

            myCardTable.pnlPlayArea.add(playedCardLabels[i]);

         }
         // show everything to the user
         myCardTable.setVisible(true);

         // have the computer play a card
         if (computerPlaysFirst)
            computerPlayCard();
      }

      /**
       * Used for handling clicks on the human cards
       * 
       * @param e
       */
      private static void onMouseClicked(MouseEvent e)
      {

         // check to make sure a card isn't already being played
         if (cardsClickable)
         {
            // temporarily make the other cards unclickable
            cardsClickable = false;

            for (int i = 0; i < NUM_CARDS_PER_HAND; i++)
            {
               if (e.getSource() == humanLabels[i])
               {

                  int handIndex = labelIndexToHandIndex(i);
                  // get the card
                  CardModel.Card card = highCardGame.playCard(1, handIndex);

                  // play the card
                  cardPlayed[1] = card;
                  // add the card to the playing area
                  playedCardLabels[1].setIcon(CardView.GUICard.getIcon(card));
                  playedCardLabels[1].setHorizontalTextPosition(JLabel.CENTER);
                  playedCardLabels[1].setVerticalTextPosition(JLabel.BOTTOM);

                  // remove the card from the hand
                  humanLabels[i].setIcon(null);

                  // if computer has not played a card
                  if (cardPlayed[0] == null)
                  {
                     // creates a delay of one second
                     final int ONE_SECOND = 1000;
                     Timer timer = new Timer(ONE_SECOND, new ActionListener()
                     {
                        public void actionPerformed(ActionEvent e)
                        {
                           computerPlayCard();
                        }
                     });

                     // start the timer
                     timer.setRepeats(false);
                     timer.start();

                  } else // computer has played a cared
                  {
                     checkWinner();
                  }

               }
            }
         }
      }

      /**
       * Computer considers different tactics to win the game
       * 
       * @return the card chosen by the computer
       */
      private static int computerChooseCard()
      {
         // Going second.
         if (cardPlayed[1] != null)
         {
            int opponentCardRank = CardModel.Card.getSortRanking(cardPlayed[1]);
            System.out.println("Competition: " + cardPlayed[1].toString() + " " + opponentCardRank);

            // Make a temporary hand to try different scenarios.
            CardModel.Card[] ascHand = new CardModel.Card[highCardGame.getHand(0).getNumOfCards()];

            // Add cards to temporary hand.
            for (int i = 0; i < highCardGame.getHand(0).getNumOfCards(); i++)
            {
               ascHand[i] = highCardGame.getHand(0).inspectCard(i);
            }

            // Sort temporary hand in ascending order of sort rank.
            CardModel.Card.arraySort(ascHand, ascHand.length);
            System.out.println("Possible plays: ");
            for (int a = 0; a < ascHand.length; a++)
            {
               System.out.println(ascHand[a].toString() + " -> rank " + CardModel.Card.getSortRanking(ascHand[a]));
            }
            // First we try to play a card higher than the opponent's.
            for (int x = 0; x < ascHand.length; x++)
            {
               if (CardModel.Card.getSortRanking(ascHand[x]) > opponentCardRank)
               {
                  for (int y = 0; y < highCardGame.getHand(0).getNumOfCards(); y++)
                  {
                     if (ascHand[x].toString().equals(highCardGame.getHand(0).inspectCard(y).toString()))
                     {
                        return y;
                     }
                  }
               }
            }

            // Next we try to play a card equal to the opponent's.
            for (int x = 0; x < ascHand.length; x++)
            {
               if (CardModel.Card.getSortRanking(ascHand[x]) == opponentCardRank)
               {
                  for (int y = 0; y < highCardGame.getHand(0).getNumOfCards(); y++)
                  {
                     if (ascHand[x].toString().equals(highCardGame.getHand(0).inspectCard(y).toString()))
                     {
                        return y;
                     }
                  }
               }
            }

            // If we can't beat their card, we play our lowest card instead.
            for (int y = 0; y < highCardGame.getHand(0).getNumOfCards(); y++)
            {
               if (ascHand[0].toString().equals(highCardGame.getHand(0).inspectCard(y).toString()))
               {
                  return y;
               }
            }

         }

         // Going first.
         // Play a random available card.
         return (int) (Math.random() * highCardGame.getHand(0).getNumOfCards());
      }

      /**
       * Plays a card from the computers hand
       */
      private static void computerPlayCard()
      {

         int cardIndex = computerChooseCard();
         CardModel.Card card = highCardGame.getHand(0).inspectCard(cardIndex);

         // set the card
         card = highCardGame.playCard(0, cardIndex);

         // play the card
         cardPlayed[0] = card;

         System.out.println("Optimal card: " + card.toString() + " -> rank " + CardModel.Card.getSortRanking(card));

         // update GUI
         playedCardLabels[0].setIcon(CardView.GUICard.getIcon(card));
         playedCardLabels[0].setHorizontalTextPosition(JLabel.CENTER);
         playedCardLabels[0].setVerticalTextPosition(JLabel.BOTTOM);

         // remove the card from the hand
         removeGUICard(0, card, true);

         // if human has played their card
         if (cardPlayed[1] != null)
            checkWinner();

         System.out
               .println("\nAI Hand: " + highCardGame.getHand(0).getNumOfCards() + highCardGame.getHand(0).toString());
      }

      private static void removeGUICard(int playerID, CardModel.Card card, boolean isHidden)
      {
         Icon removeIcon = CardView.GUICard.getBackCardIcon();

         // Remove a card with the back icon if they are hidden
         if (!isHidden)
         {
            removeIcon = CardView.GUICard.getIcon(card);
         }

         // Remove a card from computer
         if (playerID == 0)
         {
            for (int i = 0; i < computerLabels.length; i++)
            {
               if (computerLabels[i].getIcon() != null)
               {
                  if (computerLabels[i].getIcon().equals(removeIcon))
                  {
                     computerLabels[i].setIcon(null);
                     return;
                  }
               }
            }
         } else if (playerID == 1)
         {
            for (int i = 0; i < humanLabels.length; i++)
            {
               if (humanLabels[i].getIcon() != null)
               {
                  if (humanLabels[i].getIcon().equals(removeIcon))
                  {
                     humanLabels[i].setIcon(null);
                     return;
                  }
               }
            }
         }
      }

      /**
       * Converts the clicked index into an index that can be used with the Hand
       * class.
       * 
       * @param labelIndex
       *           The index of the label that was clicked.
       * @return The index in the hand that matches the label index.
       */
      static int labelIndexToHandIndex(int labelIndex)
      {
         int skippedCards = 0;
         for (int i = 0; i < highCardGame.getHand(1).getNumOfCards(); i++)
         {
            if (humanLabels[labelIndex].getIcon() != null)
            {
               Icon boardIcon = humanLabels[labelIndex].getIcon();
               Icon handIcon = CardView.GUICard.getIcon(highCardGame.getHand(1).inspectCard(i));
               if (boardIcon.toString().equals(handIcon.toString()))
               {
                  return i;
               }
            }
         }

         return 0;
      }

      /**
       * When both cards (computer and human) have been played, checks for a winner,
       * updates scores, and resets the playing area
       */
      private static void checkWinner()
      {
         // make sure both cards have been played
         if (cardPlayed[0] == null || cardPlayed[1] == null)
            return;
         gameIndex++;
         System.out.println("getNumCardsRemainingInDeck() " + highCardGame.getNumCardsRemainingInDeck());
         // if computer card is higher than players card
         if (CardModel.Card.getSortRanking(cardPlayed[0]) > CardModel.Card.getSortRanking(cardPlayed[1]))
         {
            // increment computers score
            score[0]++;
         } else
         {
            // increment players card
            score[1]++;
         }

         // Creates a delay of two seconds, so that the user can see the result
         // of the
         // current round before scoring
         final int TWO_SECONDS = 2000;
         Timer timer = new Timer(TWO_SECONDS, new ActionListener()
         {
            public void actionPerformed(ActionEvent e)
            {
               // reset both played cards
               for (int i = 0; i < NUM_PLAYERS; i++)
               {
                  playedCardText = "<html><div style='text-align: center;'>" + playerNames[i] + "<br/>Score: "
                        + score[i];
                  cardPlayed[i] = null;
                  playedCardLabels[i].setText(playedCardText);
                  playedCardLabels[i].setIcon(null);
                  playedCardLabels[i].setBorder(new EmptyBorder(0, 0, 20, 0));
                  playedCardLabels[i].setHorizontalAlignment(JLabel.CENTER);
                  playedCardLabels[i].setVerticalAlignment(JLabel.BOTTOM);
               }

               // make the cards clickable again
               cardsClickable = true;

               // alternate between the computer playing first and the
               // human
               // playing first
               if (computerPlaysFirst)
                  computerPlaysFirst = false;
               else
               {
                  computerPlaysFirst = true;

                  // create a small delay between the round ending and
                  // the
                  // computer playing
                  // int QUARTER_SECOND = 250;
                  // Timer computerPlayTimer = new Timer(QUARTER_SECOND,
                  // new ActionListener()
                  // {
                  // public void actionPerformed(ActionEvent e)
                  // {
                  computerPlayCard();
                  // }
                  // });

                  // start the timer
                  // computerPlayTimer.setRepeats(false);
                  // computerPlayTimer.start();
               }
            }
         });

         // start the timer
         timer.setRepeats(false);
         timer.start();
         displayWinner();

      }

      /**
       * Displays the winner by adding a new panel to the bottom panel
       */
      private static void displayWinner()
      {
         int computerScore = score[0];
         int userScore = score[1];
         String stringWinner = "COMPUTER";

         if (computerScore < userScore)
         {
            stringWinner = "USER";
         }
         // set a new panel to display the animated string
         // at the bottom of the frame
         if (gameIndex == NUM_CARDS_PER_HAND)
         {
            // create an new object of CardView.DisplayWinner
            winner = new CardView.DisplayWinner(stringWinner);
            bottomPanel.add(winner, BorderLayout.CENTER);
         }

      }

      /**
       * creates a panel to be placed at the bottom of the frame
       * 
       * @return the panel
       */
      protected static JComponent getBottomPanel()
      {
         bottomPanel = new JPanel();
         bottomPanel.setLayout(new BorderLayout());

         JPanel subBottom = new JPanel();
         JPanel subUpper = new JPanel();
         subUpper.setBackground(Color.BLUE);
         bottomPanel.setBackground(Color.black);
         subBottom.setBackground(Color.BLUE);
         bottomPanel.add(subUpper, BorderLayout.SOUTH);
         bottomPanel.add(subBottom, BorderLayout.NORTH);

         // button to restart the game
         restartButton = new JButton("Restart");
         restartButton.addActionListener(new java.awt.event.ActionListener()
         {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
               restartButtonAction(evt);
            }
         });

         // button to exit the game
         exitButton = new JButton("Exit");
         exitButton.addActionListener(new java.awt.event.ActionListener()
         {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
               exitButtonAction(evt);
            }
         });

         // add components
         buttonsPanel = new JPanel();
         buttonsPanel.setBackground(Color.BLACK);
         buttonsPanel.setLayout(new GridLayout(0, 2, 20, 20));
         buttonsPanel.setBorder(new EmptyBorder(30, 80, 50, 80));
         buttonsPanel.add(restartButton);
         buttonsPanel.add(exitButton);
         restartButton.setVisible(false);
         exitButton.setVisible(false);

         bottomPanel.add(buttonsPanel, BorderLayout.CENTER);

         return bottomPanel;
      }

      /**
       * replaces the current frame and 4 rows panel with new ones resets the scores
       * 
       * @param evt
       *           the event
       */
      private static void restartButtonAction(ActionEvent evt)
      {
         String str = evt.getActionCommand();

         if (str.equals("Restart"))
         {
            if (gameIndex == NUM_CARDS_PER_HAND)
            {
               gameIndex = 0;
               Controller.myCardTable.setVisible(false);
               setFrame();
               Controller.myCardTable.setVisible(true);
               Controller.restartButton.setVisible(false);
               Controller.exitButton.setVisible(false);
               Controller.score[1] = 0;
               Controller.score[0] = 0;
               computerPlaysFirst = true;
               Controller.startTheGame();

            }
         }
      }

      /**
       * exits the program
       * 
       * @param evt
       *           the event
       */
      private static void exitButtonAction(ActionEvent evt)
      {
         String str = evt.getActionCommand();

         if (str.equals("Exit"))
         {
            System.exit(0);
         }
      }

      // creates the game framework for the controller
      static class CardGameFramework
      {
         private static final int MAX_PLAYERS = 50;

         private int numPlayers;
         private int numPacks; // # standard 52-card packs per deck
                               // ignoring jokers or unused cards
         private int numJokersPerPack; // if 2 per pack & 3 packs per deck, get
                                       // 6
         private int numUnusedCardsPerPack; // # cards removed from each pack
         private int numCardsPerHand; // # cards to deal each player
         private CardModel.Deck deck; // holds the initial full deck and gets
         // smaller (usually) during play
         private CardModel.Hand[] hand; // one Hand for each player
         private CardModel.Card[] unusedCardsPerPack; // an array holding the
                                                      // cards
                                                      // not used
         // in the game. e.g. pinochle does not
         // use cards 2-8 of any suit

         /**
          * CardGameFramework constructor
          * 
          * @param numPacks
          * @param numJokersPerPack
          * @param numUnusedCardsPerPack
          * @param unusedCardsPerPack
          * @param numPlayers
          * @param numCardsPerHand
          */
         public CardGameFramework(int numPacks, int numJokersPerPack, int numUnusedCardsPerPack,
               CardModel.Card[] unusedCardsPerPack, int numPlayers, int numCardsPerHand)
         {
            int k;

            // filter bad values
            if (numPacks < 1 || numPacks > 6)
               numPacks = 1;
            if (numJokersPerPack < 0 || numJokersPerPack > 4)
               numJokersPerPack = 0;
            if (numUnusedCardsPerPack < 0 || numUnusedCardsPerPack > 50) // > 1
                                                                         // card
               numUnusedCardsPerPack = 0;
            if (numPlayers < 1 || numPlayers > MAX_PLAYERS)
               numPlayers = 4;
            // one of many ways to assure at least one full deal to all players
            if (numCardsPerHand < 1 || numCardsPerHand > numPacks * (52 - numUnusedCardsPerPack) / numPlayers)
               numCardsPerHand = numPacks * (52 - numUnusedCardsPerPack) / numPlayers;

            // allocate
            this.unusedCardsPerPack = new CardModel.Card[numUnusedCardsPerPack];
            this.hand = new CardModel.Hand[numPlayers];
            for (k = 0; k < numPlayers; k++)
               this.hand[k] = new CardModel.Hand();
            deck = new CardModel.Deck(numPacks);

            // assign to members
            this.numPacks = numPacks;
            this.numJokersPerPack = numJokersPerPack;
            this.numUnusedCardsPerPack = numUnusedCardsPerPack;
            this.numPlayers = numPlayers;
            this.numCardsPerHand = numCardsPerHand;
            for (k = 0; k < numUnusedCardsPerPack; k++)
               this.unusedCardsPerPack[k] = unusedCardsPerPack[k];

            // prepare deck and shuffle
            newGame();
         }

         // constructor overload/default for game like bridge
         public CardGameFramework()
         {
            this(1, 0, 0, null, 4, 13);
         }

         /**
          * 
          * @param k
          *           the player index
          * @return the player
          */
         public CardModel.Hand getHand(int k)
         {
            // hands start from 0 like arrays

            // on error return automatic empty hand
            if (k < 0 || k >= numPlayers)
               return new CardModel.Hand();

            return hand[k];
         }

         /**
          * 
          * @return the card to deal
          */
         public CardModel.Card getCardFromDeck()
         {
            return deck.dealCard();
         }

         /**
          * 
          * @return the remaining cards in the deck
          */
         public int getNumCardsRemainingInDeck()
         {
            return deck.getNumCards();
         }

         /**
          * creates a new game
          */
         public void newGame()
         {
            int k, j;

            // clear the hands
            for (k = 0; k < numPlayers; k++)
               hand[k].resetHand();

            // restock the deck
            deck.init(numPacks);

            // remove unused cards
            for (k = 0; k < numUnusedCardsPerPack; k++)
               deck.removeCard(unusedCardsPerPack[k]);

            // add jokers
            for (k = 0; k < numPacks; k++)
               for (j = 0; j < numJokersPerPack; j++)
                  deck.addCard(new CardModel.Card('X', CardModel.Card.Suit.values()[j]));

            // shuffle the cards
            deck.shuffle();
         }

         /**
          * 
          * @return true if the hand has cards
          */
         public boolean deal()
         {
            // returns false if not enough cards, but deals what it can
            int k, j;
            boolean enoughCards;

            // clear all hands
            for (j = 0; j < numPlayers; j++)
               hand[j].resetHand();

            enoughCards = true;
            for (k = 0; k < numCardsPerHand && enoughCards; k++)
            {
               for (j = 0; j < numPlayers; j++)
                  if (deck.getNumCards() > 0)
                     hand[j].takeCard(deck.dealCard());
                  else
                  {
                     enoughCards = false;
                     break;
                  }
            }

            return enoughCards;
         }

         /**
          * sorts the hands
          */
         public void sortHands()
         {
            int k;

            for (k = 0; k < numPlayers; k++)
               hand[k].sort();
         }

         /**
          * plays a specific card for specific player
          * 
          * @param playerIndex
          *           the player index
          * @param cardIndex
          *           the card index
          * @return specific card for specific player
          */
         public CardModel.Card playCard(int playerIndex, int cardIndex)
         {
            // returns bad card if either argument is bad
            if (playerIndex < 0 || playerIndex > numPlayers - 1 || cardIndex < 0 || cardIndex > numCardsPerHand - 1)
            {
               // Creates a card that does not work
               return new CardModel.Card('M', CardModel.Card.Suit.SPADES);
            }

            // return the card played
            return hand[playerIndex].playCard(cardIndex);

         }

         public boolean takeCard(int playerIndex)
         {
            // returns false if either argument is bad
            if (playerIndex < 0 || playerIndex > numPlayers - 1)
               return false;

            // Are there enough Cards?
            if (deck.getNumCards() <= 0)
               return false;

            return hand[playerIndex].takeCard(deck.dealCard());
         }

      }
   }
   // end of controller

   // ______________________________________________________________________________
   // ______________________________________________________________________________
   // ______________________________________________________________________________
   // ____________________start of
   // CardView________________________________________________
   static class CardView
   {
      /**
       * Controls the positioning of the panels and cards of the GUI
       */
      static class CardTable extends JFrame
      {
         public final static int MAX_CARDS_PER_HAND = 56;
         public final static int MAX_PLAYERS = 2; // for now, we only allow 2
                                                  // person
                                                  // games

         private int numCardsPerHand;
         private int numPlayers;

         public JPanel pnlComputerHand, pnlHumanHand, pnlPlayArea;

         /**
          * Instantiates a new card table.
          *
          * @param title
          *           the title
          * @param numCardsPerHand
          *           the num cards per hand
          * @param numPlayers
          *           the num players
          */
         public CardTable(String title, int numCardsPerHand, int numPlayers)
         {
            super(title);// set the title on the JFrame

            if (numCardsPerHand > MAX_CARDS_PER_HAND || numPlayers > MAX_PLAYERS)
            {
               return;
            }

            this.numCardsPerHand = numCardsPerHand;
            this.numPlayers = numPlayers;

            // first load the icons in the 2d array
            CardView.GUICard.loadCardIcons();

            // create a default Font style
            UIManager.getDefaults().put("TitledBorder.font", (new Font("Arial", Font.BOLD, 14)));

            // three rows zero columns layout, 10 pixels space
            setLayout(new GridLayout(3, 0, 10, 10));

            // create the three panels with title borders
            pnlComputerHand = new JPanel();
            pnlComputerHand.setBorder(BorderFactory.createTitledBorder("Computer Hand"));

            pnlPlayArea = new JPanel();
            pnlPlayArea.setBorder(BorderFactory.createTitledBorder("Playing Area"));

            // Zero rows,numPlayers = columns
            pnlPlayArea.setLayout(new GridLayout(0, numPlayers, 10, 10));

            pnlHumanHand = new JPanel();
            pnlHumanHand.setBorder(BorderFactory.createTitledBorder("Your Hand"));

            // add the panels to the JFrame
            add(pnlComputerHand);
            add(pnlPlayArea);
            add(pnlHumanHand);

         }

      }

      /**
       * Manages the reading and building of the card image Icons
       */
      static class GUICard
      {
         public final static int NR_OF_VALUES = 14;
         public final static int NR_OF_SUITS = 4;

         // 14 = A thru K + joker
         private static Icon[][] iconCards = new ImageIcon[NR_OF_VALUES][NR_OF_SUITS];

         private static Icon iconBack;
         static boolean iconsLoaded = false;

         public static void loadCardIcons()
         {
            if (iconCards[0][0] != null)
            {
               iconsLoaded = true;
               return;
            } else
            {
               String inputFileName = "src/images/";
               String fileExtension = ".gif";

               for (int i = 0; i < iconCards.length; i++)
               {
                  for (int j = 0; j < iconCards[i].length; j++)
                  {
                     iconCards[i][j] = new ImageIcon(
                           inputFileName + turnIntIntoCardValue(i) + turnIntIntoCardSuit(j) + fileExtension);
                  }
               }

               // set the card back icon.
               iconBack = new ImageIcon(inputFileName + "BK" + fileExtension);
            }

            // testing
            System.out.println(iconBack);
            System.out.println(getIcon(new CardModel.Card('4', CardModel.Card.Suit.CLUBS)));

         }

         /**
          * This method creates and returns an icon representing the values of the Card
          * object received as an argument
          * 
          * @param card
          *           receives a card object
          * @return object of type Icon representing a card
          */
         public static Icon getIcon(CardModel.Card card)
         {
            return iconCards[CardModel.Card.valueAsInt(card)][CardModel.Card.suitAsInt(card)];
         }

         /**
          * Accessor Method to retrieve a card Icon from the back
          * 
          * @return Icon object representing a card
          */
         public static Icon getBackCardIcon()
         {
            return iconBack;
         }

         /**
          * Turns 0 - 13 into "A", "2", "3", ... "Q", "K", "X"
          * 
          * @param j
          *           the corresponding card value in the array index
          * @return the card value
          */
         public static String turnIntIntoCardValue(int j)
         {
            String values[] =
            { "A", "2", "3", "4", "5", "6", "7", "8", "9", "T", "J", "Q", "K", "X" };
            return values[j];
         }

         /**
          * Turns 0 - 3 into "C", "D", "H", "S"
          * 
          * @param i
          *           the corresponding suit value in the array index
          * @return
          */
         public static String turnIntIntoCardSuit(int i)
         {
            String suits[] =
            { "C", "D", "H", "S" };
            return suits[i];
         }
      }

      // Class DisplayWinner, animates a text on the panel
      static class DisplayWinner extends JPanel
      {
         public DisplayWinner winner;
         private String stringWinner = "";

         /**
          * Default constructor
          * 
          * @param winner
          *           the winner
          * 
          */
         public DisplayWinner(String stringWinner)
         {
            setBackground(Color.BLACK);
            this.stringWinner = "WINNER IS THE " + stringWinner;
         }

         int x = -800;
         int y = 100;

         /**
          * Invoked by Swing to draw components
          */
         public void paint(Graphics g)
         {

            super.paint(g);

            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            Font font = new Font("Arial", Font.BOLD, 30);
            g2.setFont(font);
            g2.setColor(Color.red);

            // if no more cards in deck display who won the game and Game Over
            if (Controller.highCardGame.getNumCardsRemainingInDeck() == 0)
            {
               g2.drawString(stringWinner + " Deck Empty Game Over", x, y);
            } else // display who won
            {
               g2.drawString(stringWinner, x, y);
            }

            try
            {
               Thread.sleep(50);
            } catch (InterruptedException ex)
            {
               Logger.getLogger(DisplayWinner.class.getName()).log(Level.SEVERE, null, ex);
            }
            x += 10;

            if (x > this.getWidth())
            {
               x = -250;

            }
            if (x == 350)
            {
               if (Controller.highCardGame.getNumCardsRemainingInDeck() == 0)
               {
                  System.exit(0);
               }
               Controller.winner.setVisible(false);
               Controller.restartButton.setVisible(true);
               Controller.exitButton.setVisible(true);
               Controller.getBottomPanel().add(Controller.buttonsPanel, BorderLayout.CENTER);
               return;
            }

            repaint();
         }
      }

   }// end of CardView class
    // _____________________________________________________________________________
    // _____________________________________________________________________________
    // _____________________________________________________________________________
    // __________________start of
    // CardModel_____________________________________________

   /**
    * 
    * Class CardModel includes the three classes Card, Hand and Deck
    */
   static class CardModel
   {

      // Card class is for objects that represent an individual playing card
      static class Card
      {

         /** Constant array of valid card values acceptable for program */
         public static final char[] valueRanks =
         { 'A', '2', '3', '4', '5', '6', '7', '8', '9', 'T', 'J', 'Q', 'K', 'X' };

         /**
          * Enumerated Suit values
          */
         public enum Suit
         {
            CLUBS, DIAMONDS, HEARTS, SPADES
         }

         /** Card value (e.g. X,2,3,..., 9, T, J, K Q, A) */
         private char value;

         /** Card suit */
         private Suit suit;

         /**
          * Error flag, keeps track of invalid entries. If true, the card object does not
          * have valid data
          */
         private boolean errorFlag;

         /**
          * Default Constructor Instantiates a new card as Ace of Spades
          */
         public Card()
         {
            this.value = 'A';
            this.suit = Suit.SPADES;
         }

         /**
          * Constructor Instantiates a new card, by calling the set() method
          * 
          * @param value
          *           the value
          * @param suit
          *           the suit
          */
         public Card(char value, Suit suit)
         {
            set(value, suit);
         }

         /**
          * Returns a string based on the value and suit instance variables
          */
         public String toString()
         {
            if (errorFlag == true) // Card does not contain valid data
            {
               return "** illegal **"; // Returns an error message
            } else
            {
               // Get char value, convert to string, and store in variable
               String returnValue = String.valueOf(getValue());

               // Concatenate returnValue with a string relating to suit
               if (suit == Suit.SPADES)
                  returnValue += " of Spades";
               else if (suit == Suit.HEARTS)
                  returnValue += " of Hearts";
               else if (suit == Suit.DIAMONDS)
                  returnValue += " of Diamonds";
               else if (suit == Suit.CLUBS)
                  returnValue += " of Clubs";

               return returnValue;
            }
         }

         /**
          * Sets the value and the suit
          * 
          * @param value
          *           the value
          * @param suit
          *           the suit
          * @return true, if successful
          */
         public boolean set(char value, Suit suit)
         {
            if (!isValid(value, suit))
            {
               errorFlag = true;
               return false;
            }
            this.suit = suit;
            this.value = value;
            errorFlag = false;
            return true;
         }

         /**
          * Gets the errorFlag
          * 
          * @return the error flag
          */
         public boolean getErrorFlag()
         {
            return errorFlag;
         }

         /**
          * Gets the suit
          * 
          * @return the suit
          */
         public Suit getSuit()
         {
            return suit;
         }

         /**
          * Gets the value
          * 
          * @return the value
          */
         public char getValue()
         {
            return value;
         }

         /**
          * Gets the suit as an integer
          * 
          * @return the suit
          */
         public static int suitAsInt(Card card)
         {
            return card.getSuit().ordinal();
         }

         /**
          * Gets the value as an integer
          * 
          * @return the value
          */
         public static int valueAsInt(Card card)
         {
            String values = new String(valueRanks);
            return values.indexOf(card.getValue());
         }

         /**
          * Checks for equal values
          */
         @Override
         public boolean equals(Object obj)
         {
            final Card other = (Card) obj;

            if (this == obj)
            {
               return true;
            }

            if (obj == null || getClass() != obj.getClass() || this.value != other.value
                  || this.errorFlag != other.errorFlag || this.suit != other.suit)
            {
               return false;
            }

            return true;
         }

         /**
          * Checks if is the values entered by the user are valid.
          * 
          * @param value
          *           the value
          * @param suit
          *           the suit
          * @return true, if all values are valid
          */
         private boolean isValid(char value, Suit suit)
         {
            for (char val : valueRanks)
            {
               if (value == val)
               {
                  return true; // The value arg is found in VALID_CARD_VALUES
                               // array
               }
            }
            return false; // The value argument was not found
         }

         /**
          * Sorts an array of Cards using QuickSort.
          * 
          * @param cards
          *           An array of Cards.
          * @param arraySize
          *           The number of Cards to sort from the first index.
          */
         public static void arraySort(Card[] cards, int arraySize)
         {
            // Start timer.
            long startTime = System.nanoTime();

            // Don't sort if there are no cards.
            if (arraySize <= 0)
            {
               return;
            }

            quickSort(cards, 0, arraySize - 1);

            // End timer, display sort time.
            System.out.println("Sort complete. Took " + ((System.nanoTime() - startTime) / 100000) + " ms.");

         }

         /**
          * Sorts an array of Cards from index lowerNdx to index upperNdx.
          * 
          * @param cardArray
          *           The array of Cards to be sorted.
          * @param lowerNdx
          *           The starting index to sort from.
          * @param upperNdx
          *           The ending index to sort to.
          */
         private static void quickSort(Card[] cardArray, int lowerNdx, int upperNdx)
         {
            // Return if this section is already sorted.
            if (lowerNdx >= upperNdx)
            {
               return;
            }

            // Find the mid-point between the two indices lowerNdx and upperNdx.
            int pivotNdx = lowerNdx + ((upperNdx - lowerNdx) / 2);

            // Get the sort ranking of the Card at the index pivotNdx.
            int pivotValue = getSortRanking(cardArray[pivotNdx]);

            // Initialize loop variables to lower and upper index bounds.
            int i = lowerNdx;
            int j = upperNdx;

            // Sort until the loop variables swap places.
            while (i <= j)
            {
               // Get the sort ranking of the cards at indices i and j.
               int lowerValue = getSortRanking(cardArray[i]);
               int upperValue = getSortRanking(cardArray[j]);

               // Find a card with sort ranking higher than the pivotValue in
               // the
               // lower section of the cardArray.
               // This loop ends when an unsorted card is found.
               while (lowerValue < pivotValue)
               {
                  lowerValue = getSortRanking(cardArray[++i]);
               }

               // Find a card with sort ranking lower than the pivotValue in the
               // upper section of the cardArray.
               // This loop ends when an unsorted card is found.
               while (upperValue > pivotValue)
               {
                  upperValue = getSortRanking(cardArray[--j]);
               }

               // Swap the two unsorted cards.
               if (i <= j)
               {
                  Card tempCard = cardArray[j];
                  cardArray[j] = cardArray[i];
                  cardArray[i] = tempCard;
                  i++;
                  j--;
               }

               // Use recursion to repeat the sorting process until all cards
               // are
               // sorted in ascending order.
               if (lowerNdx < j)
               {
                  quickSort(cardArray, lowerNdx, j);
               }

               if (upperNdx > i)
               {
                  quickSort(cardArray, i, upperNdx);
               }
            }
         }

         /**
          * Calculates a card's sort ranking based on value and suit.
          * 
          * @param card
          *           The Card to evaluate.
          * @return
          */
         public static int getSortRanking(Card card)
         {
            return (valueAsInt(card) * Suit.values().length) + suitAsInt(card);
         }

      }

      // Hand class is used for the hand a player (or players) have in a card
      // game
      static class Hand
      {
         /** The max cards in the hand */
         public static final int MAX_CARDS = 180;

         /** holds all the cards */
         private Card[] myCards = new Card[MAX_CARDS];

         /** The number of card in the array. */
         private int numCards;

         /**
          * Default constructor
          */
         public Hand()
         {
            this.numCards = 0;
         }

         /**
          * Removes all cards from the hand
          */
         public void resetHand()
         {
            numCards = 0;
            myCards = new Card[MAX_CARDS];
         }

         /**
          * Adds a card to the next available position in the myCards array
          * 
          * @param card
          *           the card
          * @return true, if successful
          */
         public boolean takeCard(Card card)
         {
            if (numCards < MAX_CARDS)
            {
               // Makes copy of new card and stores in index. & Increments
               // numCards.
               myCards[numCards++] = new Card(card.getValue(), card.getSuit());

               if (numCards == MAX_CARDS) // The hand is full
               {
                  return false;
               }
            }
            return true;
         }

         /**
          * Returns and removes the card in the top occupied position of the array
          * 
          * @parm cardIndex the index of the card that will be played
          * @return the top card
          */
         public Card playCard(int cardIndex)
         {
            Card errorCard = new Card('w', Card.Suit.SPADES);
            if (numCards == 0 || cardIndex < 0 || cardIndex > MAX_CARDS)
               return errorCard;
            Card playCard = myCards[cardIndex];
            // Remove card from hand.
            myCards[cardIndex] = null;

            // Store a temporary hand.
            Card[] tempHand = myCards;
            myCards = new Card[--numCards];
            int ndx = 0;

            // Add non-null cards from the temp hand to the persistent hand.
            for (Card card : tempHand)
            {
               if (card != null)
               {
                  myCards[ndx++] = card;
               }

            }
            return playCard;
         }

         /**
          * Prints value and suit for all the cards in the hand
          */
         public String toString()
         {
            int counter = 0; // Keeps place of cards in the hand
            if (numCards == 0) // There are no cards in the hand
            {
               return "\nHand = (  )";
            } else // There are cards in the hand
            {
               String returnVal = "\nHand = ( ";

               for (int i = 0; i < numCards; i++)
               {
                  returnVal += myCards[i].toString();
                  if ((counter + 1) != numCards)
                     returnVal += ", ";
                  counter++;
                  // Check if reached end of hand
                  if (counter == numCards)
                     returnVal += " )";

                  // If more than sixth card, go to newline
                  if (counter % 6 == 0)
                     returnVal += "\n";
               }
               return returnVal;
            }
         }

         /**
          * Gets the number of cards.
          * 
          * @return the number of cards
          */
         public int getNumOfCards()
         {
            return numCards;
         }

         /**
          * Accessor for an individual card. Returns a card with errorFlag = true if k is
          * bad
          * 
          * @param k
          *           the index of the card in the array
          * @return the card
          */
         public Card inspectCard(int k)
         {
            Card errorCard = new Card('w', Card.Suit.SPADES);
            if (k < 0 || k >= numCards)
               return errorCard;
            return myCards[k];
         }

         /**
          * sorts the array of cards
          */
         public void sort()
         {
            Card.arraySort(myCards, numCards);
         }

      }

      // Allows for processing of a deck of cards
      static class Deck
      {

         // Holds the amount of unique cards in a deck
         public final static int CARDS_PER_DECK = 56;

         // Holds the max cards for all decks. 6 decks permissible
         public final static int MAX_CARDS = CARDS_PER_DECK * 6;

         // Holds a master pack of unique card objects
         private static Card[] masterPack = new Card[CARDS_PER_DECK];

         // Array to hold card objects that equal number of packs
         private Card[] cards = new Card[MAX_CARDS];

         // For the top card in the deck
         private int topCard;

         // For of copies of the masterpack
         private int numPacks;

         /**
          * Constructor Initializes deck of cards * argument
          * 
          * @param numPacks
          */
         public Deck(int numPacks)
         {
            this.numPacks = numPacks; // Initialize numPacks with argument
            allocateMasterPack(); // Initialize the masterpack to copy from
            init(this.numPacks);
         }

         /**
          * Default Constructor Initializes one deck for cards to be used
          */
         public Deck()
         {
            this.numPacks = 1; // Will use only one pack as a default
            allocateMasterPack(); // Initialize the masterpack to copy from
            init(numPacks);
         }

         /**
          * Creates a masterPack of 52 unique cards with all the valid possible unique
          * combinations of the card values and suits Checks to ensure that it has not
          * been called before by checking if masterPack instance variable array was
          * already initialized. It does not execute if masterPack was already
          * initialized. note if masterPack[] contains only null values, it contains no
          * objects and therefore must not have been initialized.
          */
         private static void allocateMasterPack()
         {
            int masterPackIndex = 0;

            // Check if masterPack was already initialized, and return if it was
            if (masterPack[masterPackIndex] != null) // masterPack was
                                                     // initialized
            {
               return;
            } else // masterPack was not initialized
            {
               // Assign cards with all unique combos of suits & values to
               // masterPack
               for (Card.Suit suit : Card.Suit.values())
               {
                  for (char validCardValue : Card.valueRanks)
                  {
                     masterPack[masterPackIndex] = new Card(validCardValue, suit);
                     masterPackIndex++;
                  }
               }
            }
         }

         /**
          * Method initializes the array of cards with amount equal to value in the
          * argument.
          * 
          * @param numPacks
          *           amount of packs to be in the cards array
          */
         public void init(int numPacks)
         {
            // Allocate card array with the total amount of cards
            cards = new Card[numPacks * CARDS_PER_DECK];

            int k = 0, pack = 0;

            // Only allow a valid number of cards in the deck
            if (numPacks < 1 || numPacks > 6)
               numPacks = 1;

            // Add cards to the array by making copies from the master pack
            for (pack = 0; pack < numPacks; pack++)
            {
               for (k = 0; k < CARDS_PER_DECK; k++)
                  cards[(pack * CARDS_PER_DECK + k)] = masterPack[k];
            }
            this.numPacks = numPacks;
            topCard = numPacks * CARDS_PER_DECK;
         }

         /** Method shuffles deck of cards */
         public void shuffle()
         {
            for (int i = 0; i < cards.length; i++)
            {
               // Get a random index in the deck
               int randomIndex = (int) (Math.random() * cards.length);

               // Make a copy of the current card
               Card temp = cards[i];

               // Swap the current and the card at the random index
               cards[i] = cards[randomIndex];
               cards[randomIndex] = temp;
            }
         }

         /**
          * Method removes a card object from top of deck
          * 
          * @returns card object form top of deck
          */
         public Card dealCard()
         {
            Card error = new Card('s', Card.Suit.DIAMONDS);

            if (topCard == 0)
               return error;
            else
               return cards[--topCard];
         }

         /**
          * Method inspects the card at index K
          * 
          * @param k
          *           index of the card to be inspected
          * @Returns an error card if the card is bad Else returns the card as it is
          */
         public Card inspectCard(int k)
         {
            Card errorCard = new Card('s', Card.Suit.DIAMONDS);

            if (k < 0 || k >= topCard) // The card is bad
               return errorCard;
            else // The card is good
               return cards[k];
         }

         /** Method returns number of cards */
         public int getNumCards()
         {
            return topCard;
         }

         /**
          * sorts the array by calling the arraySort() in Card class
          */
         public void sort()
         {
            Card.arraySort(cards, cards.length);
         }

         /**
          * Puts the card on the top of the deck, if there there are not too many
          * instances of the card in the deck
          * 
          * @param card
          *           the card to be added
          * @return true if the card is added
          */
         public boolean addCard(Card card)
         {
            if (getNumCards() >= CARDS_PER_DECK * numPacks)
               return false;

            cards[topCard++] = card;
            return true;
         }

         /**
          * Removes a specific card from the deck. Puts the current top card into its
          * place.
          * 
          * @param card
          *           the card to be removed
          * @return true if the card removed, otherwise false
          */
         public boolean removeCard(Card card)
         {

            for (int i = 0; i < cards.length; i++)
            {
               if (cards[i].equals(card))
               {
                  for (int j = i + 1; i < cards.length - 1; j++)
                  {
                     cards[i] = cards[j];
                     i++;
                  }
                  return true;
               }
            }
            return false;
         }

      }

   }// end of CardModel

}// end of Phase1

// BEGIN PHASE2 --------------------------------------------------------------
class MyClock extends JFrame
{
   // Constants
   public static final int WIDTH = 400;
   public static final int HEIGHT = 100;

   // member variables
   private JPanel panel;
   private JLabel timeClockLabel = new JLabel("0");
   private int seconds; // Holds the seconds counted in the clock
   private boolean runClock = true; // Holds True, if clock should keep running

   // clock, else false to not have the clock run
   TimeClock clockThread = new TimeClock(); // Make time thread object

   /**
    * Main function implements program
    */
   public static void main(String[] args)
   {
      MyClock gui = new MyClock();
      gui.setVisible(true);
   }

   // ---------- Listener class designed for TimeClock objects ----------------
   private class runTimerListener implements ActionListener
   {
      public void actionPerformed(ActionEvent e)
      {
         // Stops the clock if started, and starts it if stopped
         clockThread.toggleTimer();
      }
   }// End of runTimeListener inner class

   // Default constructor implements the GUI
   public MyClock()
   {
      super("Time Clock");

      // Starts the thread time and calls run
      clockThread.start();

      setSize(WIDTH, HEIGHT);
      setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      setLayout(new GridLayout(1, 0));

      // Make a panel for the GUI
      panel = new JPanel();

      // Add the panel to the GUI
      add(panel);

      // Make a button that will start / stop the timer
      JButton startStopButton = new JButton("START / STOP CLOCK");

      // Assign special access listener to the button
      startStopButton.addActionListener(new runTimerListener());

      panel.add(startStopButton); // add button to panel
      panel.add(timeClockLabel); // add the label to panel

   }

   // --------------------- TimeClock class --------------------------------
   private class TimeClock extends Thread
   {
      /**
       * Run method for the thread
       */
      public void run()
      {
         while (true)
         {
            if (runClock)
            {
               // Changes the value of the label to represent the seconds
               timeClockLabel.setText(String.format("%d:%02d", seconds/60, seconds%60));
               seconds++; // Increment the seconds value
               doNothing(1000); // Do nothing for one full second
            } else
            {
               doNothing(0); // Do nothing
            }
         }
      }

      /**
       * Helper method will pause thread for amount of milliseconds given in argument
       * 
       * @param :
       *           int milliseconds to pause program for
       */
      public void doNothing(int milliseconds)
      {
         try
         {
            Thread.sleep(milliseconds);
         } catch (InterruptedException e)
         {
            System.out.println("Unexpected interrupt!");
            System.exit(0);
         }
      }

      /**
       * Helper method
       * 
       * Toggles (flips) the value of runClock variable.
       */
      public void toggleTimer()
      {
         if (runClock)
            runClock = false;
         else
            runClock = true;
      }

   } // End of TimeClock thread class

} // END OF MyClock class
  // END PHASE2 --------------------------------------------------------------
