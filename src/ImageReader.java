import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 * A utility class for reading image files and converting them into a 3D matrix representation. The
 * matrix stores the RGB values for each pixel, where the first two dimensions represent the image's
 * height and width, and the third dimension stores the red, green, and blue values of each pixel.
 */
public class ImageReader {

  /**
   * Reads an image from the specified file path and returns its matrix representation. The matrix
   * is a 3D array where the first two dimensions correspond to the image's height and width, and
   * the third dimension holds the RGB values for each pixel.
   *
   * @param path the file path to the image
   * @return a 3D matrix representation of the image, with each pixel's RGB values
   * @throws IOException if the image file cannot be found or read
   */
  public static int[][][] readImage(String path) throws IOException {
    File imageFile = new File(path);
    BufferedImage img = ImageIO.read(imageFile);

    int width = img.getWidth();
    int height = img.getHeight();

    int[][][] pixels = new int[height][width][3];

    for (int row = 0; row < height; row++) {
      for (int col = 0; col < width; col++) {
        int pixel = img.getRGB(col, row);
        pixels[row][col][0] = (pixel & 0xFF0000) >> 16; // Red component
        pixels[row][col][1] = (pixel & 0x00FF00) >> 8;  // Green component
        pixels[row][col][2] = (pixel & 0x0000FF);       // Blue component
      }
    }
    return pixels;
  }
}