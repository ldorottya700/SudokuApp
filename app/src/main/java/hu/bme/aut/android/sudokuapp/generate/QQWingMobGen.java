package hu.bme.aut.android.sudokuapp.generate;

import java.io.IOException;
import java.util.Date;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class QQWingMobGen {

    private static QQWing ret =null;


    public static QQWing gen(String diff) {
        // Start time for the application for timing

        final QQWingOptions opts = new QQWingOptions();

        // Read the arguments and set the options

        opts.action = Action.GENERATE;
        opts.difficulty = Difficulty.get(diff);

        // The number of puzzles solved or generated.
        final AtomicInteger puzzleCount = new AtomicInteger(0);
        final AtomicBoolean done = new AtomicBoolean(false);

        Thread[] threads = new Thread[opts.threads];
        for (int threadCount = 0; threadCount < threads.length; threadCount++) {
            threads[threadCount] = new Thread(
                    new Runnable() {

                        // Create a new puzzle board
                        // and set the options
                        private QQWing ss = createQQWing();

                        private QQWing createQQWing() {
                            QQWing ss = new QQWing();
                            ss.setRecordHistory(opts.printHistory || opts.printInstructions || opts.printStats || opts.difficulty != Difficulty.UNKNOWN);
                            ss.setLogHistory(opts.logHistory);
                            ss.setPrintStyle(opts.printStyle);
                            return ss;
                        }

                        @Override public void run() {
                            try {

                                // Solve puzzle or generate puzzles
                                // until end of input for solving, or
                                // until we have generated the specified number.
                                while (!done.get()) {

                                    // Record whether the puzzle was possible or
                                    // not,
                                    // so that we don't try to solve impossible
                                    // givens.
                                    boolean havePuzzle = false;

                                    if (opts.action == Action.GENERATE) {
                                        // Generate a puzzle
                                        havePuzzle = ss.generatePuzzleSymmetry(opts.symmetry);

                                    }

                                    int solutions = 0;

                                    if (havePuzzle) {

                                        // Solve the puzzle
                                        ss.solve();


                                        // Bail out if it didn't meet the difficulty
                                        // standards for generation
                                        if (opts.action == Action.GENERATE) {
                                            if (opts.difficulty != Difficulty.UNKNOWN && opts.difficulty != ss.getDifficulty()) {
                                                havePuzzle = false;
                                                // check if other threads have
                                                // finished the job
                                                if (puzzleCount.get() >= opts.numberToGenerate) done.set(true);
                                            } else {
                                                int numDone = puzzleCount.incrementAndGet();
                                                if (numDone >= opts.numberToGenerate) done.set(true);
                                                if (numDone > opts.numberToGenerate) havePuzzle = false;
                                            }
                                        }
                                    }

                                    // Check havePuzzle again, it may have changed
                                    // based on difficulty
                                    if (havePuzzle) {
                                        // With a puzzle now in hand and possibly
                                        // solved
                                        // Print the puzzle itself.
                                        ret = ss;

                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace(System.err);
                                System.exit(1);
                            }
                        }

                    }
            );
            threads[threadCount].start();
        }

        while (isAlive(threads)) {
            try {
                Thread.sleep(200);
            } catch (InterruptedException ix) {
                ix.printStackTrace(System.err);
                System.exit(1);
            }
        }
        return ret;
    }

    private static boolean isAlive(Thread[] threads) {
        for (int i = 0; i < threads.length; i++) {
            if (threads[i].isAlive()) return true;
        }
        return false;
    }

    private static void printVersion() {
        System.out.println("qqwing " + QQWing.QQWING_VERSION);
    }


    private static class QQWingOptions {
        // defaults for options
        boolean printPuzzle = false;

        boolean printSolution = false;

        boolean printHistory = false;

        boolean printInstructions = false;

        boolean timer = false;

        boolean countSolutions = false;

        Action action = Action.NONE;

        boolean logHistory = false;

        PrintStyle printStyle = PrintStyle.READABLE;

        int numberToGenerate = 1;

        boolean printStats = false;

        Difficulty difficulty = Difficulty.UNKNOWN;

        Symmetry symmetry = Symmetry.NONE;

        int threads = Runtime.getRuntime().availableProcessors();
    }
}


