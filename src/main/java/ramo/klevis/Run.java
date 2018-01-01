package ramo.klevis;

import ramo.klevis.ml.ui.ProgressBar;
import ramo.klevis.ml.ui.UI;

import javax.swing.*;
import java.io.IOException;
import java.util.concurrent.Executors;

/**
 * Created by klevis.ramo on 1/1/2018.
 */
public class Run {
    private static JFrame mainFrame = new JFrame();
    public static void main(String[] args) throws Exception {
        ProgressBar progressBar = new ProgressBar(mainFrame, true);
        progressBar.showProgressBar("Collecting data this make take several seconds!");
        UI ui = new UI();
        Executors.newCachedThreadPool().submit(()->{
            try {
                ui.initUI();
            } catch (Exception e) {
                throw new RuntimeException(e);
            } finally {
                progressBar.setVisible(false);
                mainFrame.dispose();
            }
        });

    }
}
