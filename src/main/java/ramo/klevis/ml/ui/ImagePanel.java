package ramo.klevis.ml.ui;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;


public class ImagePanel extends JPanel {

    public static final int DEFAULT_WIDTH = 400;
    public static final int DEFAULT_HEIGHT = 400;
    private Image img;
    private BufferedImage bufferedImage;
    private boolean sourceImage;


    public ImagePanel(boolean sourceImage) throws IOException {
        this.sourceImage = sourceImage;
        showDefault();
    }

    public void showDefault() throws IOException {
        String showDefaultImage = getDefaultImage(sourceImage);
        setImage(getClass().getResourceAsStream(showDefaultImage));
    }


    public BufferedImage getCurrentBufferedImage() {
        return bufferedImage;
    }

    public void setBufferedImage(BufferedImage bufferedImage) {
        this.bufferedImage = bufferedImage;
    }

    public void setImage(InputStream imageStream) throws IOException {
        bufferedImage = ImageIO.read(imageStream);
        Image scaledInstance = bufferedImage.getScaledInstance(DEFAULT_WIDTH, DEFAULT_HEIGHT, Image.SCALE_DEFAULT);
        setImage(scaledInstance);
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

    private String getDefaultImage(boolean sourceImage) {
        String showDefaultImage;
        if (!sourceImage) {
            showDefaultImage = "/placeholder.gif";
        } else {
            showDefaultImage = "/cat.jpg";
        }
        return showDefaultImage;
    }

}