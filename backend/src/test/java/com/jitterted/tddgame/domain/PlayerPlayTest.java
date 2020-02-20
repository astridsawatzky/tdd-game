package com.jitterted.tddgame.domain;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assumptions.assumeThat;

class PlayerPlayTest {

  @Test
  public void playerPlaysCardThenCardIsInPlay() throws Exception {
    Player player = new Player(PlayerId.of(0));
    PlayingCard playingCard = new CardFactory().playingCard("to play", Usage.SELF);
    player.hand().add(playingCard);

    player.play(null, playingCard.id());

    assertThat(player.hand().contains(playingCard))
      .isFalse();
    assertThat(player.inPlay().isEmpty())
      .isFalse();
    assertThat(player.inPlay().contains(playingCard))
      .isTrue();
  }

  @Test
  public void playerPlaysOpponentUsageCardThenCardIsInOpponentInPlay() throws Exception {
    Player player = new Player(PlayerId.of(0));
    Player opponent = new Player(PlayerId.of(1));
    Game game = new Game(List.of(player, opponent), null, null);

    PlayingCard techDebtCard = new CardFactory().playingCard("opponent", Usage.OPPONENT);
    player.hand().add(techDebtCard);

    player.play(game, techDebtCard.id());

    assertThat(opponent.inPlay().cards())
      .containsOnly(techDebtCard);
    assertThat(player.inPlay().isEmpty())
      .isTrue();
  }

  @Test
  public void playedRefactorCardIsDiscarded() throws Exception {
    Player player = new Player(PlayerId.of(0));
    PlayingCard playingCard = new CardFactory().playingCard("refactor", Usage.DISCARD);
    player.hand().add(playingCard);
    Deck<PlayingCard> deck = new Deck<>(null);
    Game game = new Game(List.of(player), deck, null);

    assumeThat(player.hand().cards())
      .contains(playingCard);

    player.play(game, playingCard.id());

    assertThat(game.deck().discardPile())
      .containsOnly(playingCard);
  }
}
