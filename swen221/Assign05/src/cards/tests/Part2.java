package cards.tests;

import static cards.core.Card.Rank.*;
import static cards.core.Card.Suit.*;
import static org.junit.Assert.fail;
import cards.core.*;

import org.junit.Test;

public class Part2 {

	// ===========================================================================
	// Part 2 --- Invalid Plays
	// ===========================================================================
	@Test public void testInvalidPlay_1() throws IllegalMove {
		Card.Suit trumps = HEARTS;
		Card[] played = { };
		Card hand[] = { new Card(HEARTS,TWO),new Card(HEARTS,QUEEN), new Card(SPADES,ACE) };
		// attempt to play card not in hand
		checkInvalidPlay(new Card(CLUBS,TWO),Player.Direction.NORTH,trumps,played,hand);
	}

	@Test public void testInvalidPlay_2() throws IllegalMove {
		Card.Suit trumps = HEARTS;
		Card[] played = { new Card(HEARTS,THREE) };
		Card hand[] = { new Card(HEARTS,TWO),new Card(HEARTS,QUEEN), new Card(SPADES,ACE) };
		// attempt to play card out of turn
		checkInvalidPlay(hand[0],Player.Direction.SOUTH,trumps,played,hand);
	}

	@Test public void testInvalidPlay_3() throws IllegalMove {
		Card.Suit trumps = HEARTS;
		Card[] played = { new Card(SPADES,THREE) };
		Card hand[] = { new Card(HEARTS,TWO),new Card(HEARTS,QUEEN), new Card(SPADES,ACE) };
		// attempt to play card (trumps) which doesn't follow suit
		checkInvalidPlay(hand[0],Player.Direction.EAST,trumps,played,hand);
	}

	@Test public void testInvalidPlay_4() throws IllegalMove {
		Card.Suit trumps = HEARTS;
		Card[] played = { new Card(SPADES,THREE) };
		Card hand[] = { new Card(CLUBS,TWO),new Card(DIAMONDS,QUEEN), new Card(SPADES,ACE) };
		// attempt to play card (not trumps) which doesn't follow suit
		checkInvalidPlay(hand[0],Player.Direction.EAST,trumps,played,hand);
	}

	@Test public void testInvalidPlay_5() throws IllegalMove {
		Card.Suit trumps = null;
		Card[] played = { new Card(SPADES,THREE) };
		Card hand[] = { new Card(CLUBS,TWO),new Card(DIAMONDS,QUEEN), new Card(SPADES,ACE) };
		// attempt to play card (not trumps) which doesn't follow suit
		checkInvalidPlay(hand[0],Player.Direction.EAST,trumps,played,hand);
	}

	// DO NOT MODIFY THE FOLLOWING METHOD
	private void checkInvalidPlay(Card cardToPlay, Player.Direction player,
			Card.Suit trumps, Card[] played, Card[] hand) throws IllegalMove {
		// First, calculate AI's direction and setup Trick. We're assuming that
		// NORTH always leads.
		Trick trick = new Trick(Player.Direction.NORTH,trumps);
		Player.Direction d = Player.Direction.NORTH;
		for(int i=0;i!=played.length;++i) {
			Player ghost = new Player(d);
			ghost.getHand().add(played[i]);
			trick.play(ghost, played[i]);
			d = d.next();
		}
		Player avatar = new Player(player);
		// Second, set up AI's hand
		for(Card c : hand) {
			avatar.getHand().add(c);
		}
		// Third, attempt to play card!
		try {
			trick.play(avatar, cardToPlay);
		} catch(IllegalMove e) {
			return; // OK
		}
		fail("Invalid card was played, but not detetected");
	}
}
