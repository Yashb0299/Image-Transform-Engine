import java.io.IOException;

/**
 * Concrete class for handling PNG image transformations. This class extends the abstract class
 * {@code ImageAbstract} and provides specific implementations for handling PNG image files,
 * including loading and saving the image in matrix format.
 */
public class PNGHandler extends ImageAbstract {

  /**
   * Constructs a {@code PNGHandler} instance using a file path to the image.
   *
   * @param filename the file path to the PNG image
   * @throws IOException if the file cannot be found, accessed, or any IO issues occur
   */
  public PNGHandler(String filename) throws IOException {
    super(filename);
  }

  /**
   * Constructs a {@code PNGHandler} instance using a matrix representation of the image.
   *
   * @param matrix the matrix representation of the image
   * @throws IllegalArgumentException if the matrix is invalid
   */
  public PNGHandler(int[][][] matrix) throws IllegalArgumentException {
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
  protected PNGHandler createNewFile(int[][][] imageData) {
    return new PNGHandler(imageData);
  }

  /**
   * Loads file image from computer into matrix representation.
   *
   * @param filename the file path of the image
   * @return Matrix representation of loaded image
   * @throws IOException if image can not find the image provided
   */
  @Override
  protected int[][][] readImageFile(String filename) throws IOException {
    return ImageReader.readImage(filename);
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
    saveImg(pixels, path, ImageType.PNG);
  }

  /**
   * Helper method to save matrix representation of image into the computer system.
   *
   * @param pixels the ImageFormat instance of the image
   * @param path   the file path where the image will be saved
   * @throws IOException if image can not be saved due to some file issues
   */
  @Override
  protected void saveImgLoader(ImageFormat pixels, String path) throws IOException {
    saveImg(pixels.getPixels(), path, ImageType.PNG);
  }
}
