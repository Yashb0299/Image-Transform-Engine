import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

/**
 * Concrete method for PPM file format, handling the specifics for handling png image
 * transformations.
 */
public class PPMHandler extends ImageAbstract {

  /**
   * Constructor for PPM file subclass provided with path.
   *
   * @param filename path to the image file
   * @throws IOException if file can not be found, there are access issues, or there is some space
   *                     issue
   */
  public PPMHandler(String filename) throws IOException {
    super(filename);
  }

  /**
   * Constructor for PPM file subclass provided a matrix representation.
   *
   * @param matrix matrix representation
   */
  public PPMHandler(int[][][] matrix) {
    super(matrix);
  }

  /**
   * Creates a new instance of {@code PNGHandler} using the given matrix representation. This method
   * is used within the abstract class operations to generate new instances of the concrete class.
   *
   * @param imageData the matrix representation of the image
   * @return new PNGHandler instance
   */
  @Override
  protected ImageFormat createNewFile(int[][][] imageData) {
    return new PPMHandler(imageData);
  }

  /**
   * Loads file image from computer into matrix representation.
   *
   * @param filename the file path of the image
   * @return Matrix representation of loaded image
   * @throws FileNotFoundException if image can not find the image provided
   */
  @Override
  protected int[][][] readImageFile(String filename) throws FileNotFoundException {
    Scanner sc;

    // Try to open the file
    try {
      sc = new Scanner(new FileInputStream(filename));
    } catch (FileNotFoundException e) {
      throw new FileNotFoundException("File " + filename + " not found!");
    }

    StringBuilder builder = new StringBuilder();
    // Read the file line by line, ignoring comment lines
    while (sc.hasNextLine()) {
      String s = sc.nextLine();
      if (s.charAt(0) != '#') {
        builder.append(s).append(System.lineSeparator());
      }
    }

    // Set up the scanner to read from the string we just built
    sc = new Scanner(builder.toString());

    // Validate the PPM header
    String token = sc.next();
    if (!token.equals("P3")) {
      throw new IllegalArgumentException("Invalid PPM file: plain RAW file should begin with P3");
    }

    // Read width, height, and max value
    int width = sc.nextInt();
    int height = sc.nextInt();
    int maxValue = sc.nextInt();

    // Initialize the RGB matrix
    int[][][] pixels = new int[height][width][3];

    // Read pixel values into the RGB matrix
    for (int i = 0; i < height; i++) {
      for (int j = 0; j < width; j++) {
        int r = sc.nextInt();
        int g = sc.nextInt();
        int b = sc.nextInt();
        pixels[i][j][0] = r; // Red
        pixels[i][j][1] = g; // Green
        pixels[i][j][2] = b; // Blue
      }
    }
    sc.close();
    return pixels; // Return the populated RGB matrix
  }

  /**
   * Helper method to save matrix representation of image into the computer system.
   *
   * @param pixels the matrix representation of the image
   * @param path   the file path where the image will be saved
   * @throws IOException if image can not be saved due to some file issues
   */
  @Override
  protected void saveImgMatrix(int[][][] pixels, String path) throws IOException {
    if (pixels == null || pixels.length == 0 || pixels[0].length == 0) {
      throw new IllegalArgumentException("Invalid RGB matrix");
    }

    int height = pixels.length;
    int width = pixels[0].length;

    try (BufferedWriter writer = new BufferedWriter(new FileWriter(path))) {
      // Write the P3 header
      writer.write("P3\n");
      writer.write(width + " " + height + "\n");
      writer.write("255\n"); // Max color value

      // Write pixel data
      for (int i = 0; i < height; i++) {
        for (int j = 0; j < width; j++) {
          int r = Math.min(255, Math.max(0, pixels[i][j][0])); // Clamp to [0, 255]
          int g = Math.min(255, Math.max(0, pixels[i][j][1]));
          int b = Math.min(255, Math.max(0, pixels[i][j][2]));
          writer.write(r + " " + g + " " + b + " ");
        }
        writer.newLine();
      }
    }
  }

  /**
   * Helper method to save matrix representation of image into the computer system.
   *
   * @param pixels the ImageFormat instance of the image
   * @param path   the file path where the image will be saved
   * @throws IOException if image can not be saved due to some file issues
   */
  @Override
  protected void saveImgLoader(ImageFormat pixels, String path)
      throws IOException {
    saveImgMatrix(pixels.getPixels(), path);
  }
}