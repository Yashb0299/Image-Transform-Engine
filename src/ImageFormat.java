import java.io.IOException;

/**
 * This interface allows reading, saving, and applying transformations to an image's matrix
 * representation.
 */
public interface ImageFormat {

  /**
   * Saves the matrix representation of the image to a specified path.
   *
   * @param pixels the matrix representation of the image to be saved
   * @param path   the destination path where the image will be saved
   * @param type   the image format (e.g., PNG, JPG, PPM)
   * @throws IOException if there are issues saving the file
   */
  void saveImg(int[][][] pixels, String path, ImageType type) throws IOException;

  /**
   * Returns the matrix representation of the image.
   *
   * @return a 3D matrix representing the RGB values of the image
   */
  int[][][] getPixels();

  /**
   * Retrieves the red channel as a greyscale image.
   *
   * @return a new image object with only the red channel's greyscale values
   */
  ImageFormat getRed();

  /**
   * Retrieves the green channel as a greyscale image.
   *
   * @return a new image object with only the green channel's greyscale values
   */
  ImageFormat getGreen();

  /**
   * Retrieves the blue channel as a greyscale image.
   *
   * @return a new image object with only the blue channel's greyscale values
   */
  ImageFormat getBlue();

  /**
   * Combines separate greyscale channels into a single color image.
   *
   * @param green the matrix representing the green channel
   * @param blue  the matrix representing the blue channel
   * @param red   the matrix representing the red channel
   * @return a new image object with the combined RGB channels
   */
  ImageFormat combineToGrayscale(int[][][] green, int[][][] blue, int[][][] red);

  /**
   * Converts the image to greyscale using a specified method.
   *
   * @param type the greyscale method to use (e.g., LUMA, VALUE)
   * @return a new image object with the greyscale applied
   */
  ImageFormat getGreyscale(GreyscaleType type);

  /**
   * Applies a sepia filter to the image.
   *
   * @return a new image object with the sepia filter applied
   */
  ImageFormat getSepia();

  /**
   * Adjusts the brightness of the image.
   *
   * @param constant amount to adjust brightness (positive to brighten, negative to darken)
   * @return a new image object with the adjusted brightness
   */
  ImageFormat getChangeBrightness(float constant);

  /**
   * Flips the image vertically.
   *
   * @return a new image object with the vertical flip applied
   */
  ImageFormat getVerticalFlip();

  /**
   * Flips the image horizontally.
   *
   * @return a new image object with the horizontal flip applied
   */
  ImageFormat getHorizontalFlip();

  /**
   * Applies a blur filter to the image.
   *
   * @return a new image object with the blur filter applied
   */
  ImageFormat getBlur();

  /**
   * Applies a sharpening filter to the image.
   *
   * @return a new image object with the sharpen filter applied
   */
  ImageFormat getSharpen();
}
