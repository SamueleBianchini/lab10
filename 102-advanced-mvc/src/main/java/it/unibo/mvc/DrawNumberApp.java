package it.unibo.mvc;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 */
public final class DrawNumberApp implements DrawNumberViewObserver { 

    private final DrawNumber model;
    private final List<DrawNumberView> views;

    /**
     * @param views
     *            the views to attach
     */
    public DrawNumberApp(final DrawNumberView... views) throws IOException{
        /*
         * Side-effect proof
         */
        this.views = Arrays.asList(Arrays.copyOf(views, views.length));
        for (final DrawNumberView view: views) {
            view.setObserver(this);
            view.start();
        }
        Configuration config;
        try {
            File configFile = new File("src/main/resources/config.yml");
            config = new Configuration.Builder().build(configFile);
            this.model = new DrawNumberImpl(config.getMin(), config.getMax(), config.getAttempts());
        } catch (IOException e) {
            throw new IOException("Couldn't find the configuration file");
        }
        
    }

    @Override
    public void newAttempt(final int n) {
        try {
            final DrawResult result = model.attempt(n);
            for (final DrawNumberView view: views) {
                view.result(result);
            }
        } catch (IllegalArgumentException e) {
            for (final DrawNumberView view: views) {
                view.numberIncorrect();
            }
        }
    }

    @Override
    public void resetGame() {
        this.model.reset();
    }

    @Override
    public void quit() {
        /*
         * A bit harsh. A good application should configure the graphics to exit by
         * natural termination when closing is hit. To do things more cleanly, attention
         * should be paid to alive threads, as the application would continue to persist
         * until the last thread terminates.
         */
        System.exit(0);
    }

    /**
     * @param args
     *            ignored
     * @throws FileNotFoundException 
     */
    public static void main(final String... args) throws IOException {
        DrawNumberViewImpl gui1 = new DrawNumberViewImpl();
        DrawNumberViewImpl gui2 = new DrawNumberViewImpl();
        PrintStreamView gui3 = new PrintStreamView(System.out);
        PrintStreamView gui4 = new PrintStreamView("log.txt");
        new DrawNumberApp(gui1, gui2, gui3, gui4);
    }
}
