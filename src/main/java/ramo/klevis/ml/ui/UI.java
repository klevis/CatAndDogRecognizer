package ramo.klevis.ml.ui;

import ramo.klevis.ml.vg16.PetType;
import ramo.klevis.ml.vg16.VG16ForCat;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.plaf.FontUIResource;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by klevis.ramo on 1/1/2018.
 */
public class UI {

    private static final double THRESHOLD_ACCURACY = 0.50;
    private JFrame mainFrame;
    private JPanel mainPanel;
    private static final int FRAME_WIDTH = 800;
    private static final int FRAME_HEIGHT = 600;
    private ImagePanel sourceImagePanel;
    private JLabel predictionResponse;
    private VG16ForCat vg16ForCat;
    private File selectedFile;
    private SpinnerNumberModel modelThresholdSize;
    private JSpinner thresholdField;
    private final Font sansSerifBold = new Font("SansSerif", Font.BOLD, 18);

    public UI() throws Exception {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        UIManager.put("Button.font", new FontUIResource(new Font("Dialog", Font.BOLD, 18)));
        UIManager.put("ProgressBar.font", new FontUIResource(new Font("Dialog", Font.BOLD, 18)));

    }

    public void initUI() throws Exception {

        vg16ForCat = new VG16ForCat();
        vg16ForCat.loadModel();
        // create main frame
        mainFrame = createMainFrame();

        mainPanel = new JPanel();
        mainPanel.setLayout(new GridBagLayout());

        JButton chooseButton = new JButton("Choose Pet Image");
        chooseButton.addActionListener(e -> {
            chooseFileAction();
            predictionResponse.setText("");
        });

        JButton predictButton = new JButton("Is it Cat or a Dog?");
        predictButton.addActionListener(e -> {
            try {
                PetType petType = vg16ForCat.detectCat(selectedFile, (Double) thresholdField.getValue());
                if (petType == PetType.CAT) {
                    predictionResponse.setText("It is a Cat");
                    predictionResponse.setForeground(Color.GREEN);
                } else if (petType == PetType.DOG) {
                    predictionResponse.setText("It is a Dog");
                    predictionResponse.setForeground(Color.GREEN);
                } else {
                    predictionResponse.setText("Not Sure...");
                    predictionResponse.setForeground(Color.RED);
                }
                mainPanel.updateUI();
            } catch (IOException e1) {
                throw new RuntimeException(e1);
            }
        });

        fillMainPanel(chooseButton, predictButton);

        addSignature();

        mainFrame.add(mainPanel, BorderLayout.CENTER);
        mainFrame.setVisible(true);

    }

    private void fillMainPanel(JButton chooseButton, JButton predictButton) throws IOException {
        GridBagConstraints c = new GridBagConstraints();

        c.gridx = 1;
        c.gridy = 1;
        c.weighty = 0;
        c.weightx = 0;
        JPanel buttonsPanel = new JPanel(new FlowLayout());
        modelThresholdSize = new SpinnerNumberModel(THRESHOLD_ACCURACY, 0.5, 1, 0.1);
        thresholdField = new JSpinner(modelThresholdSize);
        JLabel label = new JLabel("Threshold Accuracy %");
        label.setFont(sansSerifBold);
        buttonsPanel.add(label);
        buttonsPanel.add(thresholdField);
        buttonsPanel.add(chooseButton);
        buttonsPanel.add(predictButton);
        mainPanel.add(buttonsPanel, c);

        c.gridx = 1;
        c.gridy = 2;
        c.weighty = 1;
        c.weightx = 1;
        sourceImagePanel = new ImagePanel();
        mainPanel.add(sourceImagePanel, c);

        c.gridx = 1;
        c.gridy = 3;
        c.weighty = 0;
        c.weightx = 0;
        predictionResponse = new JLabel();
        predictionResponse.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 72));
        mainPanel.add(predictionResponse, c);
    }


    public void chooseFileAction() {
        JFileChooser chooser = new JFileChooser();
        chooser.setCurrentDirectory(new File(new File("resources").getAbsolutePath()));
        int action = chooser.showOpenDialog(null);
        if (action == JFileChooser.APPROVE_OPTION) {
            try {
                selectedFile = chooser.getSelectedFile();
                showSelectedImageOnPanel(new FileInputStream(selectedFile), sourceImagePanel);
            } catch (IOException e1) {
                throw new RuntimeException(e1);
            }
        }
    }

    private void showSelectedImageOnPanel(InputStream selectedFile, ImagePanel imagePanel) throws IOException {
        BufferedImage bufferedImage = ImageIO.read(selectedFile);
        imagePanel.setImage(bufferedImage);
    }


    private JFrame createMainFrame() {
        JFrame mainFrame = new JFrame();
        mainFrame.setTitle("Image Recognizer");
        mainFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        mainFrame.setSize(FRAME_WIDTH, FRAME_HEIGHT);
        mainFrame.setLocationRelativeTo(null);
        mainFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                System.exit(0);
            }
        });
        ImageIcon imageIcon = new ImageIcon("icon.png");
        mainFrame.setIconImage(imageIcon.getImage());

        return mainFrame;
    }

    private void addSignature() {
        JLabel signature = new JLabel("ramok.tech", JLabel.HORIZONTAL);
        signature.setFont(new Font(Font.SANS_SERIF, Font.ITALIC, 20));
        signature.setForeground(Color.BLUE);
        mainFrame.add(signature, BorderLayout.SOUTH);
    }
}
