package ramo.klevis;

import org.apache.commons.io.FileUtils;
import ramo.klevis.ml.ui.ProgressBar;
import ramo.klevis.ml.ui.UI;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.concurrent.Executors;

/**
 * Created by klevis.ramo on 1/1/2018.
 */
public class Run {
    private static JFrame mainFrame = new JFrame();

    public static void main(String[] args) throws Exception {
        ProgressBar progressBar = new ProgressBar(mainFrame, true);

        File model = new File("resources/model.zip");
        if (!model.exists()) {
            progressBar.showProgressBar("Downloading model for the first time 500MB!");
            URL modelURL = new URL("https://dl.dropboxusercontent.com/s/djmh91tk1bca4hz/RunEpoch_class_2_soft_10_32_1800.zip?dl=0");

            try {
                FileUtils.copyURLToFile(modelURL, model);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null, "Failed to download model");
                throw new RuntimeException(e);
            } finally {
                progressBar.setVisible(false);
            }

        }

        progressBar.showProgressBar("Collecting data this make take several seconds!");
        UI ui = new UI();
        Executors.newCachedThreadPool().submit(() -> {
            try {
                ui.initUI();
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            } finally {
                progressBar.setVisible(false);
                mainFrame.dispose();
            }
        });

    }
}
