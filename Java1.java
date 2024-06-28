import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

class ImageClick2 extends JFrame implements MouseListener {
    BufferedImage img;
    private JFrame secondFrame;
    private int clickedX, clickedY; // Store the clicked coordinates

    public ImageClick2() {
        // Use a file chooser to select the image file
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File(System.getProperty("user.home") + "\\Pictures"));
        int result = fileChooser.showOpenDialog(this);

        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            try {
                // Load the selected image
                img = ImageIO.read(selectedFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

            // Set up the JFrame
            this.setSize(500, 500);
            this.setVisible(true);
            this.addMouseListener(this);

            // Add WindowListener to handle window closing event for the main window
            this.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    handleMainWindowClosing();
                }
            });
        } else {
            // User canceled the file chooser, exit the application
            System.exit(0);
        }
    }

    // Make handleMainWindowClosing package-private (default access)
    void handleMainWindowClosing() {
        if (secondFrame != null) {
            secondFrame.dispose(); // Close the second window if it's open
        }
        System.out.println("Terminating application.");
        System.exit(0);
    }

    public void mouseClicked(MouseEvent e) {
        clickedX = e.getX();
        clickedY = e.getY();
        System.out.println("Mouse Clicked at (" + clickedX + ", " + clickedY + " )");

        // Define the size of the subimage
        int subImageSize = 50; // Adjust the size as needed

        // Calculate the bounds for the subimage
        int x1 = (int) (((float) img.getWidth() / (float) 500) * (float) clickedX);
        int y1 = (int) (((float) img.getHeight() / (float) 500) * (float) clickedY);

        if (x1 < 0) x1 = 0;
        if (y1 < 0) y1 = 0;
        if (x1 + subImageSize > img.getWidth()) x1 = img.getWidth() - subImageSize;
        if (y1 + subImageSize > img.getHeight()) y1 = img.getHeight() - subImageSize;

        // Create the subimage
        BufferedImage selectedPixelImage = img.getSubimage(x1-25, y1-25, subImageSize, subImageSize);

        // Show the smaller image in a pop-up dialog
        ImageIcon icon = new ImageIcon(selectedPixelImage);
        int confirm = JOptionPane.showConfirmDialog(this, icon, "Confirm", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            System.out.println("User confirmed. Finding RGB value...");
            // Call a function to find the RGB value of the selected pixel
            RGBFinder.findRGBValue(selectedPixelImage, this);
        }
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }

    public void mousePressed(MouseEvent e) {
    }

    public void mouseReleased(MouseEvent e) {
    }

    public void paint(Graphics g) {
        // Draw the scaled image on the JFrame
        g.drawImage(img, 0, 0, 500, 500, this);
    }

    public void setRGBValuesForSecondFrame(int red, int green, int blue) {
    if (secondFrame instanceof ImageProcessorFrame) {
        ((ImageProcessorFrame) secondFrame).setRGBValues(red, green, blue);
    }
}

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                ImageClick2 mainFrame = new ImageClick2();
                mainFrame.secondFrame = new ImageProcessorFrame(mainFrame);
            }
        });
    }
}

class RGBFinder {
    public static void findRGBValue(BufferedImage image, ImageClick2 mainFrame) {
        int x = image.getWidth() / 2;
        int y = image.getHeight() / 2;
        int rgb = image.getRGB(x, y);

        int red = (rgb >> 16) & 0xFF;
        int green = (rgb >> 8) & 0xFF;
        int blue = rgb & 0xFF;

        System.out.println("RGB Value of the selected pixel: Red=" + red + ", Green=" + green + ", Blue=" + blue);

        // After getting the RGB value, call the function to process the second image
        mainFrame.setRGBValuesForSecondFrame(red, green, blue);

    }
}

class ImageProcessorFrame extends JFrame {
    private BufferedImage secondImg;
    private ImageClick2 mainFrame;
    private int targetRed, targetGreen, targetBlue; // Store RGB values

    public ImageProcessorFrame(ImageClick2 mainFrame) {
        this.mainFrame = mainFrame;

        // Set up the frame
        this.setSize(500, 500);
        this.setVisible(false);

        // Add WindowListener to handle window closing event for the second window
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                handleWindowClosing();
            }
        });
    }

    void setRGBValues(int red, int green, int blue) {
        targetRed = red;
        targetGreen = green;
        targetBlue = blue;

        // Use a file chooser to select the second image file
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File(System.getProperty("user.home") + "\\Pictures"));
        int result = fileChooser.showOpenDialog(null);

        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();

            try {
                // Load the second image
                secondImg = ImageIO.read(selectedFile);

                // Add a menu bar to the frame after loading the second image
                JMenuBar menuBar = new JMenuBar();
                JMenu menu = new JMenu("Options");
                JMenuItem closestPixelItem = new JMenuItem("Closest Colored Pixel");
                JMenuItem complementaryPixelItem = new JMenuItem("Complementary Colored Pixel");

                closestPixelItem.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        findClosestColoredPixel();
                    }
                });

                complementaryPixelItem.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        findComplementaryColoredPixel();
                    }
                });

                menu.add(closestPixelItem);
                menu.add(complementaryPixelItem);
                menuBar.add(menu);
                this.setJMenuBar(menuBar);

                this.setVisible(true);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            // User canceled the file chooser, exit the application
            System.exit(0);
        }
    }

    private void findClosestColoredPixel() {
        // Implement logic to find the closest colored pixel in the second image
        int closestRGB = -1;
        int minDistance = Integer.MAX_VALUE;
        int subImageSize = 50; // Adjust the size as needed

        int bestX = 0;
        int bestY = 0;

        for (int x = 0; x <= secondImg.getWidth() - subImageSize; x++) {
            for (int y = 0; y <= secondImg.getHeight() - subImageSize; y++) {
                BufferedImage subImage = secondImg.getSubimage(x, y, subImageSize, subImageSize);
                int subRGB = subImage.getRGB(subImage.getWidth() / 2, subImage.getHeight() / 2);
                int distance = calculateRGBDistance(targetRed, targetGreen, targetBlue, subRGB);
                if (distance < minDistance) {
                    minDistance = distance;
                    closestRGB = subRGB;
                    bestX = x;
                    bestY = y;
                }
            }
        }

        // Create a custom JPanel to display the second image with the highlighted pixel
        CustomImagePanel imagePanel = new CustomImagePanel(secondImg, bestX, bestY, subImageSize);
        JFrame frame = new JFrame("Closest Pixel Highlighted");
        frame.add(imagePanel);
        frame.setSize(500, 500);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Close the second image window independently
        frame.setVisible(true);
    }

    private void findComplementaryColoredPixel() {
        // Implement logic to find the complementary colored pixel in the second image
        int complementaryRGB = findComplementaryColor(targetRed, targetGreen, targetBlue);

        // Search for the pixel with the complementary color in the second image
        int subImageSize = 50; // Adjust the size as needed
        int minDistance = Integer.MAX_VALUE;
        int bestX = 0;
        int bestY = 0;

        for (int x = 0; x <= secondImg.getWidth() - subImageSize; x++) {
            for (int y = 0; y <= secondImg.getHeight() - subImageSize; y++) {
                BufferedImage subImage = secondImg.getSubimage(x, y, subImageSize, subImageSize);
                int subRGB = subImage.getRGB(subImage.getWidth() / 2, subImage.getHeight() / 2);

                // Calculate distance based on complementary color
                int distance = calculateRGBDistance(complementaryRGB, subRGB);
                if (distance < minDistance) {
                    minDistance = distance;
                    bestX = x;
                    bestY = y;
                }
            }
        }

        // Create a custom JPanel to display the second image with the highlighted pixel
        CustomImagePanel imagePanel = new CustomImagePanel(secondImg, bestX, bestY, subImageSize);
        JFrame frame = new JFrame("Complementary Pixel Highlighted");
        frame.add(imagePanel);
        frame.setSize(500, 500);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Close the second image window independently
        frame.setVisible(true);
    }

    private int calculateRGBDistance(int rgb1, int rgb2) {
        int red1 = (rgb1 >> 16) & 0xFF;
        int green1 = (rgb1 >> 8) & 0xFF;
        int blue1 = rgb1 & 0xFF;

        int red2 = (rgb2 >> 16) & 0xFF;
        int green2 = (rgb2 >> 8) & 0xFF;
        int blue2 = rgb2 & 0xFF;

        int deltaRed = red1 - red2;
        int deltaGreen = green1 - green2;
        int deltaBlue = blue1 - blue2;

        return deltaRed * deltaRed + deltaGreen * deltaGreen + deltaBlue * deltaBlue;
    }

    private int calculateRGBDistance(int red1, int green1, int blue1, int rgb2) {
        int red2 = (rgb2 >> 16) & 0xFF;
        int green2 = (rgb2 >> 8) & 0xFF;
        int blue2 = rgb2 & 0xFF;

        int deltaRed = red1 - red2;
        int deltaGreen = green1 - green2;
        int deltaBlue = blue1 - blue2;

        return deltaRed * deltaRed + deltaGreen * deltaGreen + deltaBlue * deltaBlue;
    }

    private int findComplementaryColor(int red, int green, int blue) {
        // Calculate complementary color by subtracting each component from 255
        int compRed = 255 - red;
        int compGreen = 255 - green;
        int compBlue = 255 - blue;

        return (compRed << 16) | (compGreen << 8) | compBlue;
    }

    private void handleWindowClosing() {
        // Handle window closing event for the second window
        mainFrame.handleMainWindowClosing();
    }
}

class CustomImagePanel extends JPanel {
    private BufferedImage image;
    private int highlightX;
    private int highlightY;
    private int highlightSize;

    public CustomImagePanel(BufferedImage image, int highlightX, int highlightY, int highlightSize) {
        this.image = image;
        this.highlightX = highlightX;
        this.highlightY = highlightY;
        this.highlightSize = highlightSize;

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int confirm = JOptionPane.showConfirmDialog(null, null, "Confirm", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    System.out.println("User confirmed. Terminating application.");
                    System.exit(0);
                } else {
                    // Close the second image window
                    SwingUtilities.getWindowAncestor(CustomImagePanel.this).dispose();
                }
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Calculate the scaling factor for the second image
        double scale = Math.min(1.0 * getWidth() / image.getWidth(), 1.0 * getHeight() / image.getHeight());
        int scaledWidth = (int) (scale * image.getWidth());
        int scaledHeight = (int) (scale * image.getHeight());

        // Draw the scaled second image
        g.drawImage(image, 0, 0, scaledWidth, scaledHeight, this);

        // Scale the highlight coordinates and size
        int scaledHighlightX = (int) (scale * highlightX);
        int scaledHighlightY = (int) (scale * highlightY);
        int scaledHighlightSize = (int) (scale * highlightSize);

        g.setColor(Color.RED);
        g.drawRect(scaledHighlightX, scaledHighlightY, scaledHighlightSize, scaledHighlightSize);
    }
}
