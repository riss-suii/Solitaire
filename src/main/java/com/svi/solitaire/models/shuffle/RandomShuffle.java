package com.svi.solitaire.models.shuffle;

import com.svi.solitaire.models.Card;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/** Fisher-Yates shuffle. The random source is injected so games can be seeded and reproduced. */
public class RandomShuffle implements ShuffleStrategy {

    private final Random random;

    public RandomShuffle(Random random) {
        this.random = random;
    }

    @Override
    public List<Card> shuffle(List<Card> cards) {
        List<Card> result = new ArrayList<>(cards);
        for (int i = result.size() - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            Card tmp = result.get(i);
            result.set(i, result.get(j));
            result.set(j, tmp);
        }
        return result;
    }

    @Override
    public String getName() {
        return "Random";
    }
}
