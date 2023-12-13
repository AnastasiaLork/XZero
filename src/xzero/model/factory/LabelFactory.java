package xzero.model.factory;

import xzero.model.Label;
import java.util.Random;

/**
 * Фабрика, порождающая метку. Реализует самую простую стратегию
 */
public class LabelFactory {

    private final Random _random;

    public LabelFactory() {
        _random = new Random();
    }
    public Label createLabel() {
        return new Label(needNeutral());
    }

    /** Рандомно решить, будет ли метка нейтральной */
    private boolean needNeutral() {
        int value = _random.nextInt(100);

        return value < 10;
    }
}
