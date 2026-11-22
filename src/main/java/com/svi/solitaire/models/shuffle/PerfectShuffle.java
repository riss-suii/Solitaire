package com.svi.solitaire.models.shuffle;

import com.svi.solitaire.models.Card;

import java.util.ArrayList;
import java.util.List;

/**
 * Parent of the deterministic "faro" shuffles. The deck is cut exactly in half and the halves are
 * interleaved one card at a time. Subclasses only decide which half supplies the first card:
 * <ul>
 *   <li>out-shuffle: the top card stays on top</li>
 *   <li>in-shuffle: the top card ends up second</li>
 * </ul>
 * The shuffle can be repeated {@code rounds} times.
 */
public abstract class PerfectShuffle implements ShuffleStrategy {

    private final int rounds;

    protected PerfectShuffle(int rounds) {
        if (rounds < 1) {
            throw new IllegalArgumentException("rounds must be >= 1, got " + rounds);
        }
        this.rounds = rounds;
    }

    public int getRounds() {
        return rounds;
    }

    /** True when the card from the top half is placed first in each pair. */
    protected abstract boolean topHalfFirst();

    protected abstract String baseName();

    @Override
    public List<Card> shuffle(List<Card> cards) {
        if (cards.size() % 2 != 0) {
            throw new IllegalArgumentException("A perfect shuffle needs an even number of cards");
        }
        List<Card> result = new ArrayList<>(cards);
        for (int i = 0; i < rounds; i++) {
            result = riffleOnce(result);
        }
        return result;
    }

    private List<Card> riffleOnce(List<Card> cards) {
        int half = cards.size() / 2;
        List<Card> top = cards.subList(0, half);
        List<Card> bottom = cards.subList(half, cards.size());
        List<Card> first = topHalfFirst() ? top : bottom;
        List<Card> second = topHalfFirst() ? bottom : top;

        List<Card> result = new ArrayList<>(cards.size());
        for (int i = 0; i < half; i++) {
            result.add(first.get(i));
            result.add(second.get(i));
        }
        return result;
    }

    @Override
    public String getName() {
        return baseName() + " (" + rounds + (rounds == 1 ? " round)" : " rounds)");
    }
}
