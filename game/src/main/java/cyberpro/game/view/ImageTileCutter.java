/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cyberpro.game.view;

/**
 *
 * @author mikhail
 */
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ImageTileCutter {
    private static final int TILE_SIZE = 64;

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Usage: java ImageTileCutter <image_file_path>");
            return;
        }

        String imagePath = args[0];
        try {
            BufferedImage image = ImageIO.read(new File(imagePath));
            int width = image.getWidth();
            int height = image.getHeight();

            int rows = height / TILE_SIZE;
            int cols = width / TILE_SIZE;

            System.out.printf("Cutting image into %d rows and %d columns%n", rows, cols);

            for (int row = 0; row < rows; row++) {
                for (int col = 0; col < cols; col++) {
                    BufferedImage tile = image.getSubimage(
                            col * TILE_SIZE,
                            row * TILE_SIZE,
                            TILE_SIZE,
                            TILE_SIZE
                    );
                    String fileName = String.format("row%d_col%d.png", row, col);
                    ImageIO.write(tile, "png", new File(fileName));
                }
            }

            System.out.println("Done cutting image!");

        } catch (IOException e) {
            System.err.println("Failed to read or write image: " + e.getMessage());
        }
    }
}