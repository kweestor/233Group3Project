import java.util.Scanner;
import java.util.ArrayList;
import java.util.Arrays;


/**
 * Uses a main method to run the primary game loop.
 */
public class Game {
	
	/**
	 * @param monsters
	 * @return if one or more of the Monsters in the given array are alive.
	 */
	private static boolean monstersAlive(Monster[] monsters) {
		for (Monster m : monsters) {
			if (m.alive())
				return true;
		}
		return false;
	}
	
	/**
	 * Prints the intentions of each Monster in the given array.
	 * @param monsters
	 */
	private static void printMonstersIntentions(Monster[] monsters) {
		for (Monster m : monsters)
			System.out.println(m.intentions() + "\n");
	}
	
	/**
	 * Removes dead Monsters from the given array.
	 * @param monsters
	 * @return the original Monster array with any/all dead Monsters removed.
	 */
	private static Monster[] removeDead(Monster[] monsters) {
		for (int i = monsters.length - 1; i >= 0; i --) {
			if (!monsters[i].alive()) {
				Monster[] newMonsters = new Monster[monsters.length - 1];
				for (int j = 0; j < monsters.length; j ++) {
					if (j == i)
						continue;
					newMonsters[(j < i) ? j : j - 1] = monsters[j];
				}
				monsters = newMonsters;
			}
		}
		
		return monsters;
	}

	/**
	 * Main game loop.
	 */
	public static void main(String[] args) {
		CardsUtil.load();
		Monster slime = new Monster("Slime", 19, CardsUtil.get("Monster Attack"), CardsUtil.get("Monster Block")),
				jawWorm = new Monster("Jaw Worm", 44, CardsUtil.get("Monster Attack"), CardsUtil.get("Monster Block"), CardsUtil.get("Monster BlAttack")),
			    louse = new Monster("Louse", 16, CardsUtil.get("Monster WkAttack"), CardsUtil.get("Monster BlAttack"), CardsUtil.get("Monster StBlock"), CardsUtil.get("Monster Block")),
			    gremlinNob = new Monster("Gremlin Nob", 82, CardsUtil.get("Monster Attack"), CardsUtil.get("Monster StAttack"), CardsUtil.get("Monster HvAttack"));

		Monster[] monsters = new Monster[] {slime, jawWorm, louse, gremlinNob};

		Scanner in = new Scanner(System.in);
		Player player = new Player(intro(in), 50, CardsUtil.get("Strike"), CardsUtil.get("Strike"), CardsUtil.get("Strike"), CardsUtil.get("Defend"), CardsUtil.randomP(), CardsUtil.randomP(), CardsUtil.randomP());

		while (player.alive()) {
			Monster[] monster = new Monster[] {new Monster(slime), new Monster(slime), new Monster(slime)};
		//	monster.setStrategy("0.8,Monster Special,2/0.2,Monster Special,2");
		//	monster.setStrategy("1,Monster Special,3/0.9,Monster SpecialTwo,2");
			player.startCombat();

			while (monstersAlive(monster) && player.alive()) {
				playerTurn(player, monster);
				player.endTurn();
				monster = removeDead(monster);
				endTurn(in, player, monster);
				
			}
			player.endCombat();
			endCombat(in, player, monster);
		}

		in.close();
	}

	/**
	 * Prints the game intro and returns the user's name.
	 * @param in this Scanner will be used to get the user's name.
	 * @return the user's name.
	 */
	public static String intro(Scanner in) {
		System.out.println("Please enter your name:");
		String name = in.nextLine();
		pressEnter(in, "Welcome, " + name + ", to Slay the Spire! In this brief demo, you will be challenged by enemies until you are defeated."
				+ " Use your Cards to attack and defend against the monsters!");
		return name;
	}

	/**
	 * Runs the player's turn, and loops until the player ends their turn.
	 * @param player
	 * @param monster
	 */
	public static void playerTurn(Player player, Monster[] monster) {
		System.out.println(player.getName() + "'s turn!\n");
		for (Monster m : monster)
			m.setMove();
		printMonstersIntentions(monster);

		player.startTurn();
		printStats(player, monster);

		while (player.nextCard(monster) && monstersAlive(monster)) {
			monster = removeDead(monster);
			printMonstersIntentions(monster);
			printStats(player, monster);
		}
	}

	/**
	 * Prints out end turn messages and prompts the user to hit enter.
	 * @param in this Scanner will be used to take the player's input.
	 * @param player
	 * @param monster
	 */
	public static void endTurn(Scanner in, Player player, Monster[] monster) {
		if (monstersAlive(monster) && player.alive()) {
			pressEnter(in, player.getName() + "'s turn is over!");

			System.out.println("Monsters' turn!");
			printStats(player, monster);
			
			for (Monster m : monster ) {
				m.getMove().use(m, player);
				System.out.println(m.actionReport());
			}
			printStats(player, monster);
			pressEnter(in, "");
		}
	}

	/**
	 * Prints if the monster or player won. Adds a random card to the player's deck if they won.
	 * @param in this Scanner will be used to prompt the user to hit enter.
	 * @param player
	 * @param monster
	 */
	public static void endCombat(Scanner in, Player player, Monster[] monster) {
		if (!player.alive())
			System.out.println("DEFEAT!");
		else {
			pressEnter(in, "Victory!");
			Card reward = newCard();
			pressEnter(in, "Your reward: " + reward.getName() + "\n" + reward.getDescription());
			player.addCard(reward);
		}
	}

	/**
	 * @return a random player card, cannot be Strike.
	 */
	private static Card newCard() {
		Card c;
		while((c = CardsUtil.randomP()).getName().equals("Strike"));
		return c;
	}

	/**
	 * Returns a random monster from a given array, with full health.
	 * @param monsters the monsters to choose from.
	 * @return a random monster, with restored health.
	 */
	public static Monster getNextMonster(Monster[] monsters) {
		Monster m = monsters[(int) (Math.random() * monsters.length)];
		m.heal(m.getMaxHealth());
		return m;
	}

	/**
	 * Prints the health, armour and various non-zero attributes of the given player and monster, as well as the energy of the player.
	 */
	public static void printStats(Entity player, Monster[] monster) {
		ArrayList<String> attributeList = new ArrayList<String>(Arrays.asList("Strength", "Dexterity", "Weak", "Vulnerable", "Regeneration", "Poison", "Constricted", "Armour"));

		String playerStatus = ("\n" + player.getName() + ":   health: " + player.getHealth() + "/" + player.getMaxHealth() +
				"      Energy: " + player.getEnergy() + "/"  + player.getMaxEnergy());
		int i = 0;
		for (Attribute a : new Attribute[] {player.getStrength(), player.getDexterity(), player.getWeak(), player.getVulnerable(), player.getRegeneration(),
		player.getPoison(), player.getConstricted(), player.getArmour()}){
			if (a.getCurrentVal()!= 0){
				playerStatus += String.format("      %s: %d", attributeList.get(i), a.getCurrentVal());
			}
			i++;
		}

		String monsterStatus = "";
		
		for (Monster m : monster) {
			if (m.alive()) {
				monsterStatus += ("\n" + m.getName() + ":    health: " + m.getHealth() + "/" + m.getMaxHealth());
				int j = 0;
				for (Attribute a : new Attribute[] {m.getStrength(), m.getDexterity(), m.getWeak(), m.getVulnerable(), m.getRegeneration(),
				m.getPoison(), m.getConstricted(), m.getArmour()}){
					if (a.getCurrentVal()!= 0){
						monsterStatus += String.format("      %s: %d", attributeList.get(j), a.getCurrentVal());
					}
					j++;
				}
			}
		
		}
		System.out.println(playerStatus + "\n");
		System.out.println(monsterStatus);
	}


	/**
	 * Prompts the user to press enter, accompanied by a given message.
	 * @param in Scanner to use to get the user's input.
	 * @param message the message to display before "Press enter to continue."
	 */
	public static void pressEnter(Scanner in, String message) {
		System.out.println(message + "\nPress enter to continue.");
		in.nextLine();
	}

}