package ramo.klevis.ml.ui;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;


public class ImagePanel extends JPanel {

    public static final int DEFAULT_WIDTH = 400;
    public static final int DEFAULT_HEIGHT = 400;
    private Image img;
    private BufferedImage bufferedImage;
    private InputStream imageStream;


    public ImagePanel() throws IOException {
        showDefault();
    }

    public void showDefault() throws IOException {
        String showDefaultImage = getDefaultImage();
        imageStream = new FileInputStream("resources/"+showDefaultImage);
        setImage(imageStream);
    }

    public void setImage(InputStream imageStream) throws IOException {
        bufferedImage = ImageIO.read(imageStream);
        Image scaledInstance = bufferedImage.getScaledInstance(DEFAULT_WIDTH, DEFAULT_HEIGHT, Image.SCALE_DEFAULT);
        setImage(scaledInstance);
        this.imageStream = imageStream;
    }

    public void paintComponent(Graphics g) {
        g.drawImage(img, 0, 0, null);
    }

    public void setImage(Image img) {
        this.img = img;
        Dimension size = new Dimension(img.getWidth(null), img.getHeight(null));
        setPreferredSize(size);
        setMinimumSize(size);
        setMaximumSize(size);
        setSize(size);
        setLayout(null);
        repaint();
        updateUI();
    }

    private String getDefaultImage() {
         return "/placeholder.gif";
    }

}