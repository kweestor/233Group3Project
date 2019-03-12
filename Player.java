import java.util.Scanner;

/**
 * Inherits all properties and methods of the Entity class, with additional methods for user interaction and control. Holds a Deck object to store and use Card objects.
 */
public class Player extends Entity {
	
	private Deck deck;
	
	/**
	 * Creates a Player object with a given name, maximum health, and any number of Cards to put in their deck.
	 */
	public Player(String name, int maxHealth, Card... cards) {
		super(name, maxHealth, maxHealth, 0, 3, 3);
		deck = new Deck(cards);
	}
	
	public Player(Player pCopy) {
		super(pCopy);
	}
	
	/** Private utility method used to number and print all Entities from an array.
	 * @param targets print these Entities.
	 */
	private void printTargets(Entity[] targets) {
		for (int i = 0; i < targets.length; i ++)
			System.out.println((i + 1) + ": " + targets[i].getName());
	}
	
	/** Attempts to use a given card. Prints "You don't have enough energy" when it is unsuccessful. Discards the card if it is successful.
	 * @param card the card to attempt to use.
	 * @param cardIndex the index of the given card(used to discard the card if it is successfully used).
	 * @param targets any number of Entities to target with the card.
	 */
	private void attemptCardUse(Card card, int cardIndex, Entity... targets) {
		if (card.use(this, targets)) {
			deck.drawFromHand(cardIndex);
		} else {
			System.out.println("You don't have enough energy.");
		}
	}
	
	/**
	 * Prompts the player to select a Card from their hand, and attempts to use it on the player(as the user) and the given target. If the user enters
	 * an incorrect index or String value, this will be caught by the try catch block as an IndexOutOfBoundsException or InputMismatchException, and
	 * returns false, ending the player's turn. Otherwise, whether the card use is successful or not, nextCard will return true, continuing the turn.
	 * 
	 * @param target the Entity to attempt to use any cards on.
	 * @return false if the player wishes to end his/her turn, true if they wish to continue.
	 */
	public boolean nextCard(Entity... target) {
		Scanner in = new Scanner(System.in);
		System.out.println("Enter the number of the card you wish to use, or anything else to end your turn." + "\n" + deck.handToString());
		try {
			int whichCard = Integer.parseInt(in.nextLine());
			
			Card card = deck.getHand().get(whichCard);
			
			if (!card.requiresTarget()) {
				System.out.println(card.getDescription() + "\nPress enter to use, type anything else to go back.1");
			
				if (in.nextLine().equals("")) {
					attemptCardUse(card, whichCard, target);
				} else {
					System.out.println("Card cancelled");
				}
			} else {
				System.out.println(card.getDescription() + "\nEnter the number corresponding to your target, or anything else to go back.2");
				printTargets(target);
				try {
					attemptCardUse(card, whichCard, target[Integer.parseInt(in.nextLine()) - 1]);
				} catch(Exception e) {
					System.out.println("Card Cancelled");
				}
				
			}
			
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	/**
	 * Updates the Player's attributes, fills the Player's hand and resets the Player's energy.
	 */
	public void startTurn() {
		super.startTurn();
		deck.startTurn();
		setEnergy(getMaxEnergy());
	}
	
	/**
	 * Updates the Player's attributes, and empties the Player's hand into the discard pile.
	 */
	public void endTurn() {
		super.endTurn();
		deck.endTurn();
	}
	
	/**
	 * Resets the Player's attributes, and fills the Player's draw pile.
	 */
	public void startCombat() {
		super.startCombat();
		deck.startCombat();
	}
	
	/**
	 * Empties the Player's draw pile, hand, and discard pile.
	 */
	public void endCombat() {
		deck.endCombat();
	}
	
	/**
	 * @param c add this card to the player's deck.
	 */
	public void addCard(Card c) {
		deck.addToDeck(c);
	}
	
	public Deck getDeck() {
		return deck;
	}
	
}
