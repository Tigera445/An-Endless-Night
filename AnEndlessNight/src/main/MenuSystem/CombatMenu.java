package main.MenuSystem;

import java.io.IOException;
import java.util.NoSuchElementException;

import main.Game;
import main.GameInput;
import main.CombatSystem.DamageHandler;
import main.CombatSystem.EscapeController;
import main.CombatSystem.Monster;
import main.CombatSystem.StatusCondition;

/**
 * 
 * @author Aaron
 *
 */
public class CombatMenu extends AbstractMenu 
{

	protected static boolean battleContinuing;

	public CombatMenu(MenuLoader menuLoader) 
	{
		super(menuLoader);
	}

	@Override
	void mainPrompt() 
	{
		
		Monster currentMonster = Game.getHero().getRoom().getMonster();
		
		printMonsterDescription(currentMonster);
		System.out.println();
		
		System.out.println("\tA " + currentMonster.getName() + " stares you down.");
		System.out.println();
		
		battleContinuing = true;

		while(battleContinuing) 
		{

			int userChoice = -1;
			
			try 
			{

				System.out.println("What is your next move?");
				System.out.println();
				System.out.println("1. Attack Monster");
				System.out.println("2. Brace Yourself");
				System.out.println("3. Attempt to Flee");
				System.out.println("4. Use Item");
				System.out.println();

				userChoice = GameInput.getInt();

				if(userChoice == 1) 
				{

					attack(currentMonster);

				}
				else if(userChoice == 2) 
				{

					defend(currentMonster);

				}
				else if(userChoice == 3) 
				{

					flee(currentMonster);

				}
				else if(userChoice == 4) 
				{

					CombatInventoryMenu.openMenu();

				}
				else 
				{
					throw new IOException();
				}
			}

			catch (IOException e) 
			{

				System.out.println("\tYou mumble incoherently. You should be wary of nonsensical babbling, ");
				System.out.println("\telse you be labeled insane.");
				System.out.println();

			}
		}
	}
	

	protected void attack(Monster currentMonster) 
	{
		
		DamageHandler combatManager = new DamageHandler();
		System.out.println("\tYou raise your " + Game.getHero().getEquippedWeapon().getName().toLowerCase() + 
						   " and bring it down on the " + currentMonster.getName());
		System.out.println();
		
		int monsterAttackResult = combatManager.attackMonster();
		
		if(monsterAttackResult == 0) 
		{
			System.out.println("\tYour attack barely grazes the creature, leaving it undamaged.");
			System.out.println();
			this.monsterAttack();
		}
		else if(monsterAttackResult == -1) 
		{
			System.out.println("\tYour attack sinks deep into the " + currentMonster.getName() + 
							   ", mortally wounding it.");
			System.out.println("\tIts body slumps over as the last breath escapes its body.");
			System.out.println();
			
			// TODO: Better score system
			Game.incrementScore(currentMonster.getStrength());
			
			Game.getHero().getRoom().destroyMonster();
			
			battleContinuing = false;
			MenuLoader.loadGameMenu(this);
		}
		else 
		{
			System.out.println("\tYour weapon vibrates as it strikes the creature, wounding it.");
			System.out.println("\tThe creature has " + monsterAttackResult + " health remaining.");
			System.out.println();
			this.monsterAttack();
		}
		
	}

	protected void defend(Monster currentMonster) 
	{
		
		Game.getHero().addStatusCondition(StatusCondition.DEFENSE_BUFF);
		
		System.out.println("\tYou brace yourself for an attack.");
		
		this.monsterAttack();
		
		// this is neccessary because if the hero dies in combat,
		// the game is reset and hero will be null, causing an exception
		try
		{
			Game.getHero().removeStatusCondition(StatusCondition.DEFENSE_BUFF);
		}
		catch(NullPointerException npe) {};
		
		
	}

	protected void flee(Monster battleMonster) {
		
		EscapeController escape = new EscapeController();
		
		// 
		if(escape.attemptEscape()) 
		{
		
			System.out.println("\tYou feign an attack, and dart off while the " + battleMonster.getName() + " attempts to brace itself.");
			System.out.println();
			battleContinuing = false;
			// move hero back to last room.
			Game.getHero().bounceBack();
			MenuLoader.loadGameMenu(this);
			
		}
		else 
		{
			
			System.out.println("\tYou try to escape, but the " + battleMonster.getName() + " is too fast.");
			
			this.monsterAttack();
			
		}
	}

	protected void monsterAttack() 
	{
		
		System.out.println("\tThe creature attacks!");
		System.out.println();
		
		DamageHandler combat = new DamageHandler();
		int combatResult = combat.attackHero();
		
		if(combatResult == -1) 
		{
			
			battleContinuing = false;
			heroDeath();
			
		}
		else if(combatResult == 0) 
		{
			
			System.out.println("\tThe creature lunges, but you swiftly dodge.");
			System.out.println();
			
		}
		else 
		{
			
			System.out.println("\tIt attacks you, leaving you with " + combatResult + " health left.");
			System.out.println();
			
		}
		
	}

	protected void heroDeath() 
	{
		
		System.out.println("\tYour knees crumple and your vision fades as your wounds become too much to bear.");
		System.out.println("\n\n\n");
		
		try {
			Thread.sleep(2500);
		} catch (InterruptedException e) {
			// do nothing
		}
		
		// TOUCH THIS SHIT I FUCKING DARE YOU :)
		System.out.println("      #############               \n" +
						   "    ##            *##             \n" +
						   "   #               **#            \n" +
						   "  #                ***#            @@@@@@@@   @@@@@@   @@@@@@@@@@   @@@@@@@@      @@@@@@   @@@  @@@  @@@@@@@@  @@@@@@@   \n" +
						   " #                 ****#          @@@@@@@@@  @@@@@@@@  @@@@@@@@@@@  @@@@@@@@     @@@@@@@@  @@@  @@@  @@@@@@@@  @@@@@@@@  \n" +
						   "#                  *****#         !@@        @@!  @@@  @@! @@! @@!  @@!          @@!  @@@  @@!  @@@  @@!       @@!  @@@  \n" +
						   "#   ###           ###***#         !@!        !@!  @!@  !@! !@! !@!  !@!          !@!  @!@  !@!  @!@  !@!       !@!  @!@  \n" +
						   "#  # ####       #### #**#         !@! @!@!@  @!@!@!@!  @!! !!@ @!@  @!!!:!       @!@  !@!  @!@  !@!  @!!!:!    @!@!!@!  \n" +
						   "#  #     #     #     #**#         !!! !!@!!  !!!@!!!!  !@!  !  !@!  !!!!!:       !@!  !!!  !@!  !!!  !!!!!:    !!@!@!    \n" +
						   "#   #####  # #  #####***#         :!!   !!:  !!:  !!!  !!:     !!:  !!:          !!:  !!!  :!:  !!:  !!:       !!: :!!   \n" +
						   "#         #   #  *******#         :!:   !::  :!:  !:!  :!:     :!:  :!:          :!:  !:!   ::!!:!   :!:       :!:  !:!  \n" +
						   " ### #           **# ###           ::: ::::  ::   :::  :::     ::    :: ::::     ::::: ::    ::::     :: ::::  ::   :::  \n" +
						   "     #             #               :: :: :    :   : :   :      :    : :: ::       : :  :      :      : :: ::    :   : :  \n" +
						   "     # - - - - - - #              \n" +
						   "      | | | | | | |               ");
		
		
		System.out.println("\n\n\n");
		System.out.println("Press enter to continue.");
		
		try {
			GameInput.getString();
		} catch (IOException e) {
			// do nothing
		}
		
		
		
		battleContinuing = false;
		Game.reset();
		
		// Reads all excess junk from stdin
		// This is neccessary because if the user just spams enter
		// through the beginning story, any excess "enters" will cause
		// an error in the main loop of this menu and cause a bunch of
		// "not valid input"'s to be printed while it reads all the
		// excess enters that the user spammed.
		try {
			while(System.in.available() > 0) 
			{
				System.in.read();
			}
		} catch (IOException e) {
			// do nothing
		}
		
		MenuLoader.loadStartMenu(this);
		
	}
	
	/**
	 * Looks up the current monster and prints a description of it.
	 * @param currentMonster
	 */
	private void printMonsterDescription(Monster currentMonster) {
		
		switch(currentMonster.getID()) 
		{
			case 1 :
				// Maneki-neko
				System.out.println("\tIt's porcelain figure glimmers in the light.");
				System.out.println("\tFelines are not your favorite animal, but at least this one is harmless.");
				break;
			case 2 :
				// Karakasa
				System.out.println("\tA slender figure lies before you. It resembles a paper umbrella,");
				System.out.println("\tsave for the large, blinking eye and grotesque tongue.");
				System.out.println("\tThe Old Ones surely must have labeled this an abomination.");
				break;
			case 3 :
				// Futakuchi-onna
				System.out.println("\tBefore you is a beautiful woman. She giggles softly and twirls around,");
				System.out.println("\tbriefly revealing a set on monstrous teeth hidden in the hair on the back of her head.");
				break;
			case 4 :
				// Kappa
				System.out.println("\tA Kappa of old greets you. These turtle-like creatures are distant relatives");
				System.out.println("\tof the goblins. They are known for pranks and their love for cucumbers.");
				System.out.println("\tIt's shell covers most it's body, but atop it's head lies a cavity of water.");
				System.out.println("\tPerhaps this is a weakness?");
				break;
			case 5 :
				// Tanuki
				// TODO: Magic mirror and transform stuff.
				System.out.println("\tA plain creature stands before you. There is nothing remarkable to note.");
				break;
			case 6 :
				// Yatagarasu
				System.out.println("\tA gust of wind hits your face as the creature before you flaps it's large,");
				System.out.println("\tblack wings. It must be a Yatagarasu, the three-legged crows that many");
				System.out.println("\tassociate with divine intervention. I wonder what it is doing in a demon-infested");
				System.out.println("\tlair such as this. Perhaps it has gone mad...");
				break;
			case 7 :
				// Oni
				System.out.println("\tTowering above you, a giant ogre looks down and snorts. A mist of putrid,");
				System.out.println("\trotten air hits your face, causing you to gag. You should be cautious");
				System.out.println("\tof it's iron club, which has been said is strong enough to crush the bones of Old Ones.");
				break;
			case 8 :
				// Shisa
				System.out.println("\tYou rub your eyes. Surely you must be dreaming. A Shisha lies before you, it's mane");
				System.out.println("\tflowing graciously to the floor. You had thought these lion-like creatures were");
				System.out.println("\tmerely fables or stories for children. It is said that they are so powerful,");
				System.out.println("\ttheir very breath can scorch the earth.");
				break;
			case 9 :
				// Tengu
				System.out.println("\tBefore you stands a demon unlike any you have encountered this far. Many heads protrude from");
				System.out.println("\tits bony body, each face having a long, beak-like nose. It carries large, feathered fans");
				System.out.println("\tthat look like they could produce a mighty blast of wind.");
				break;
			case 10 :
				// Kitsune
				System.out.println("\tBefore you stands Kitsune, the twinkle in her eye absolutely entrancing.");
				System.out.println("\tShe pouts and comments on how exhausted you look. Falling back onto");
				System.out.println("\ta plush bed, she motions for you to join her.");
				System.out.println();
				System.out.println("\t\"Surely the stresses of your battle can be....relieved\" she says, smiling.");
				break;
			case 11 :
				// Ryu
				System.out.println("\tThe sky crackles and booms, clouds gathering overhead. As rain begins to plummet");
				System.out.println("\tyour weary face, a movement in the darkened clouds catches your eye. Slowly, you");
				System.out.println("\tbegin to make out a figure...serpentine in form, moving through the air like a snake");
				System.out.println("\tthrough grass. Fear strikes deep in your heart as the realization dawns on you. Surely,");
				System.out.println("\tthis cannot be. An Old God, one whos very name is only whispered in passing nightmares");
				System.out.println("\tand muttered curses. The ground shakes as Ryu, the serpentine water deity, lands.");
				break;
			default :
				// not yet implemented, must be a bug
				System.out.println("\tA bug scurries in front of you. Someone should be alerted of this infestation.");
				break;
		}
		
	}

	@Override
	void onDestroy() 
	{
		// TODO Auto-generated method stub
		
	}

}
