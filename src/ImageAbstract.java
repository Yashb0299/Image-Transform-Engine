import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 * Abstract Method for the ImageFormat implementation, applying all the methods for the image
 * manipulation to the corresponding subclass.
 */
public abstract class ImageAbstract implements ImageFormat {

  int[][][] loadedImage;

  /**
   * Constructor method to create class based on the image provided in the path.
   *
   * @param filename path of the image
   * @throws IOException if file does not exist/can not be found
   */
  protected ImageAbstract(String filename) throws IOException {
    this.loadedImage = readImageFile(filename); // Call a method to read the image
  }

  /**
   * Constructor method to create class based on the matrix representation.
   *
   * @param pixels matrix representation of image
   * @throws IllegalArgumentException if matrix is not valid
   */
  protected ImageAbstract(int[][][] pixels) throws IllegalArgumentException {
    if (pixels == null || pixels.length == 0 || pixels[0].length == 0 || pixels[0][0].length == 0) {
      throw new IllegalArgumentException(
          "Invalid Image Matrix representation to initialize the class.");
    }
    this.loadedImage = pixels;
  }

  /**
   * Abstract helper method implemented in each concrete class to create new subClass object,
   * necessary for returning the subclass object on each transformation method.
   *
   * @param imageData Matrix representation of the image
   * @return New subclass object with the matrix representation
   */
  protected abstract ImageFormat createNewFile(int[][][] imageData);

  /**
   * Abstract helper method implemented in each concrete class to load file image into the class.
   *
   * @param filename path to image file
   * @return New subclass object with the matrix representation
   */
  protected abstract int[][][] readImageFile(String filename) throws IOException;

  /**
   * Convert matrix into image and save the image into given path.
   *
   * @param pixels Matrix representation to be saved
   * @param path   where the image is going be saved
   * @throws IOException if the path does not exist
   */
  @Override
  public void saveImg(int[][][] pixels, String path, ImageType type) throws IOException {
    // Get the expected file extension from the ImageType
    String expectedExtension = "." + type.toString().toLowerCase();

    // Check if the path ends with the expected extension
    if (!path.endsWith(expectedExtension)) {
      // Remove any existing extension
      int lastDotIndex = path.lastIndexOf('.');
      if (lastDotIndex != -1) {
        path = path.substring(0, lastDotIndex); // Remove existing extension
      }
      path += expectedExtension; // Append the new extension
    }

    File outputfile = new File(path);
    int height = pixels.length;
    int width = pixels[0].length;
    BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

    for (int row = 0; row < height; row++) {
      for (int col = 0; col < width; col++) {
        int r = pixels[row][col][0];
        int g = pixels[row][col][1];
        int b = pixels[row][col][2];
        int rgb = (r << 16) | (g << 8) | b;

        img.setRGB(col, row, rgb);
      }
    }
    ImageIO.write(img, String.valueOf(type), outputfile);
  }


  /**
   * Overload method for save the image into given path method using matrix array parameter.
   *
   * @param pixels Matrix representation to be saved
   * @param path   where the image is going be saved
   * @throws IOException if the path does not exist
   */

  protected abstract void saveImgMatrix(int[][][] pixels, String path)
      throws IOException;

  /**
   * Method for save the image into given path method using ImageFormat parameter.
   *
   * @param pixels ImageFormat Matrix representation to be saved
   * @param path   where the image is going be saved
   * @throws IOException if the path does not exist
   */
  protected abstract void saveImgLoader(ImageFormat pixels, String path)
      throws IOException;

  /**
   * Method to return the matrix representation of the image.
   *
   * @return matrix representation (RGB) of image
   */
  @Override
  public int[][][] getPixels() {
    return loadedImage;
  }

  /**
   * Helper function to clamp given numbers, to avoid blowing images with values outside the limits
   * of the color representation (0-255),
   *
   * @param numb current calculated value
   * @return value clamped with the range
   */
  private int clipNumber(int numb) {
    return Math.max(0, Math.min(255, numb));
  }

  /**
   * Get the red channel of the matrix representation of the picture, by creating a greyscale
   * version of the red channel. For a normal version of the red channel, set the index 1 and 2 to 0
   * in the loop.
   *
   * @return Matrix representation of the red channel for the image
   */
  @Override
  public ImageFormat getRed() {
    int height = loadedImage.length;
    int width = loadedImage[0].length;
    int[][][] newPixels = new int[height][width][3];

    for (int row = 0; row < height; row++) {
      for (int col = 0; col < width; col++) {
        newPixels[row][col][0] = loadedImage[row][col][0];
        newPixels[row][col][1] = loadedImage[row][col][0];
        newPixels[row][col][2] = loadedImage[row][col][0];
      }
    }
    return createNewFile(newPixels);
  }

  /**
   * Get the green channel of the matrix representation of the picture, by creating a greyscale
   * version of the green channel. For a normal version of the green channel, set the index 0 and 2
   * to 0 in the loop.
   *
   * @return Matrix representation of the green channel for the image
   */
  @Override
  public ImageFormat getGreen() {
    int height = loadedImage.length;
    int width = loadedImage[0].length;
    int[][][] newPixels = new int[height][width][3];

    for (int row = 0; row < height; row++) {
      for (int col = 0; col < width; col++) {
        newPixels[row][col][0] = loadedImage[row][col][1];
        newPixels[row][col][1] = loadedImage[row][col][1];
        newPixels[row][col][2] = loadedImage[row][col][1];
      }
    }
    return createNewFile(newPixels);
  }

  /**
   * Get the blue channel of the matrix representation of the picture, by creating a greyscale
   * version of the blue channel. For a normal version of the blue channel, set the index 0 and 1 to
   * 0 in the loop.
   *
   * @return Matrix representation of the blue channel for the image
   */
  @Override
  public ImageFormat getBlue() {
    int height = loadedImage.length;
    int width = loadedImage[0].length;
    int[][][] newPixels = new int[height][width][3];

    for (int row = 0; row < height; row++) {
      for (int col = 0; col < width; col++) {
        // Set all channels to the blue value
        newPixels[row][col][0] = loadedImage[row][col][2]; // Red
        newPixels[row][col][1] = loadedImage[row][col][2]; // Green
        newPixels[row][col][2] = loadedImage[row][col][2]; // Blue
      }
    }

    return createNewFile(newPixels);
  }

  /**
   * Method to combine color channel images representations into one, creating a complete image.
   *
   * @param green Matrix representation of the green channel of the image
   * @param blue  Matrix representation of the blue channel of the image
   * @param red   Matrix representation of the red channel of the image
   * @return Matrix representation of the entire combination
   */

  private ImageFormat combineToGrayscaleBase(int[][][] green, int[][][] blue, int[][][] red) {
    int height = green.length; // Assuming all channels are the same size
    int width = green[0].length;
    int[][][] newPixels = new int[height][width][3];

    for (int row = 0; row < height; row++) {
      for (int col = 0; col < width; col++) {
        newPixels[row][col][0] = red[row][col][0];
        newPixels[row][col][1] = green[row][col][1];
        newPixels[row][col][2] = blue[row][col][2];
      }
    }

    return createNewFile(newPixels);
  }

  /**
   * Overload Method for the combineToGrayscale.
   *
   * @param green Integer array Matrix representation of the green channel of the image
   * @param blue  Integer array Matrix representation of the blue channel of the image
   * @param red   Integer array Matrix representation of the red channel of the image
   * @return Matrix representation of the entire combination
   */

  @Override
  public ImageFormat combineToGrayscale(int[][][] green, int[][][] blue, int[][][] red) {
    return combineToGrayscaleBase(green, blue, red);
  }

  /**
   * Overload Method for the combineToGrayscale.
   *
   * @param green Integer array Matrix representation of the green channel of the image
   * @param blue  ImageFormat Matrix representation of the blue channel of the image
   * @param red   Integer array Matrix representation of the red channel of the image
   * @return Matrix representation of the entire combination
   */

  public ImageFormat combineToGrayscale(int[][][] green, ImageFormat blue, ImageFormat red) {
    return combineToGrayscaleBase(green, blue.getPixels(), red.getPixels());
  }

  /**
   * Overload Method for the combineToGrayscale.
   *
   * @param green ImageFormat Matrix representation of the green channel of the image
   * @param blue  Integer array Matrix representation of the blue channel of the image
   * @param red   ImageFormat Matrix representation of the red channel of the image
   * @return Matrix representation of the entire combination
   */
  public ImageFormat combineToGrayscale(ImageFormat green, int[][][] blue, ImageFormat red) {
    return combineToGrayscaleBase(green.getPixels(), blue, red.getPixels());
  }

  /**
   * Overload Method for the combineToGrayscale.
   *
   * @param green ImageFormat Matrix representation of the green channel of the image
   * @param blue  ImageFormat Matrix representation of the blue channel of the image
   * @param red   Integer array Matrix representation of the red channel of the image
   * @return Matrix representation of the entire combination
   */

  public ImageFormat combineToGrayscale(ImageFormat green, ImageFormat blue, int[][][] red) {
    return combineToGrayscaleBase(green.getPixels(), blue.getPixels(), red);
  }

  /**
   * Overload Method for the combineToGrayscale.
   *
   * @param green ImageFormat Matrix representation of the green channel of the image
   * @param blue  ImageFormat Matrix representation of the blue channel of the image
   * @param red   ImageFormat Matrix representation of the red channel of the image
   * @return Matrix representation of the entire combination
   */

  public ImageFormat combineToGrayscale(ImageFormat green, ImageFormat blue,
      ImageFormat red) {
    return combineToGrayscaleBase(green.getPixels(), blue.getPixels(), red.getPixels());
  }

  /**
   * Method to apply grayscale filter to the matrix representation of the image. To make a pixel
   * gray, the levels of the colors must be the same meaning that they will be the same, and to the
   * human eye, it will become gray out, however, it will still look like the image. This
   * implementation uses Luma, the weighted sum of 0.2126 * red + 0.7152 * green + 0.0722 * blue,to
   * create the effect.
   *
   * @param type type of greyscale calculation being performed
   * @return Matrix representation of image with grayscale applied
   */
  @Override
  public ImageFormat getGreyscale(GreyscaleType type) {
    int height = loadedImage.length;
    int width = loadedImage[0].length;
    int[][][] newPixels = new int[height][width][3];

    for (int y = 0; y < height; y++) {
      for (int x = 0; x < width; x++) {
        int red = loadedImage[y][x][0];
        int green = loadedImage[y][x][1];
        int blue = loadedImage[y][x][2];

        //int impl = procedureImpl(red, green, blue);
        int impl = type.procedureImpl(red, green, blue);

        newPixels[y][x][0] = impl;
        newPixels[y][x][1] = impl;
        newPixels[y][x][2] = impl;
      }
    }
    return createNewFile(newPixels);
  }

  /**
   * Method to apply the Sepia filter to the matrix representation of the image. we take the rgb of
   * the current pixel [r,g,b] and multiplies red by 0.393, green by 0.769, and blue by 0.189. with
   * this, the desired color effect for Sepia will be applied.
   *
   * @return Matrix representation of image after changes
   */
  @Override
  public ImageFormat getSepia() {
    int height = loadedImage.length;
    int width = loadedImage[0].length;
    int[][][] newPixels = new int[height][width][3];

    for (int y = 0; y < height; y++) {
      for (int x = 0; x < width; x++) {
        int red = loadedImage[y][x][0];
        int green = loadedImage[y][x][1];
        int blue = loadedImage[y][x][2];

        // Calculate the sepia values
        int sepiaRed = (int) Math.round(0.393 * red + 0.769 * green + 0.189 * blue);
        int sepiaGreen = (int) Math.round(0.349 * red + 0.686 * green + 0.168 * blue);
        int sepiaBlue = (int) Math.round(0.272 * red + 0.534 * green + 0.131 * blue);

        // Clamp the values
        newPixels[y][x][0] = clipNumber(sepiaRed);
        newPixels[y][x][1] = clipNumber(sepiaGreen);
        newPixels[y][x][2] = clipNumber(sepiaBlue);
      }
    }

    return createNewFile(newPixels);
  }

  /**
   * Method to increase/decrease the brightness of the image representation.
   *
   * @param constant amount of times to increase(positive)/decrease(negative) the brightness
   * @return ImageFormat representation of the image after manipulating the brightness
   */
  @Override
  public ImageFormat getChangeBrightness(float constant) {
    int height = loadedImage.length;
    int width = loadedImage[0].length;
    int[][][] newPixels = new int[height][width][3];

    for (int row = 0; row < height; row++) {
      for (int col = 0; col < width; col++) {
        newPixels[row][col][0] = clipNumber((int) (loadedImage[row][col][0] + constant));
        newPixels[row][col][1] = clipNumber((int) (loadedImage[row][col][1] + constant));
        newPixels[row][col][2] = clipNumber((int) (loadedImage[row][col][2] + constant));
      }
    }

    return createNewFile(newPixels);
  }

  /**
   * Method to flip image by rotating vertically. We achieve this by reversing the order of the rows
   * from top to bottom.
   *
   * @return Rotated ImageFormat Matrix vertically
   */
  @Override
  public ImageFormat getVerticalFlip() {
    int height = loadedImage.length;
    int width = loadedImage[0].length;
    int[][][] newPixels = new int[height][width][3];

    for (int row = 0; row < height; row++) {
      newPixels[row] = loadedImage[height - 1 - row];
    }

    return createNewFile(newPixels);
  }

  /**
   * Method to flip image by rotating horizontally. We achieve this by reversing the order of the
   * rows from left to right.
   *
   * @return Rotated ImageFormat matrix horizontally
   */
  @Override
  public ImageFormat getHorizontalFlip() {
    int height = loadedImage.length;
    int width = loadedImage[0].length;
    int[][][] newPixels = new int[height][width][3];

    for (int row = 0; row < height; row++) {
      for (int col = 0; col < width; col++) {
        newPixels[row][col] = loadedImage[row][width - col - 1];
      }
    }

    return createNewFile(newPixels);
  }

  /**
   * Method to blur image using the matrix representation. This is achieved by applying convolution
   * operation to every pixel. The convolution operation takes the rgb of the current pixel [r,g,b]
   * and with the kernel we will apply this: Output(x, y) = ∑∑ I(x+i, y+j) * K(i, j) for i = -m to
   * m, j = -n to n Output(x, y) : New pixel value at position (x, y) in the output image I(x+i,
   * y+j) : Pixel value of the original image at position (x+i, y+j) K(i, j)        : Weight at
   * position (i, j) in the filter (kernel) ∑∑             : Summation over all positions of the
   * kernel m : Half the height of the kernel (for a 3x3 kernel, m = 1) n : Half the width of the
   * kernel (for a 3x3 kernel, n = 1) Basically, you will multiply each corresponding value with its
   * corresponding weight from the kernel. in the end, you multiply with every possible combination
   * of the value for that matrix
   *
   * @return Matrix representation of the blurred image
   */
  @Override
  public ImageFormat getBlur() {
    int height = loadedImage.length;
    int width = loadedImage[0].length;
    int[][][] newPixels = new int[height][width][3];
    float[] kernel = {0.0625f, 0.125f, 0.0625f, 0.125f, 0.25f, 0.125f, 0.0625f, 0.125f, 0.0625f,};

    // Deep copy pixels array into newPixels
    for (int y = 0; y < height; y++) {
      for (int x = 0; x < width; x++) {
        newPixels[y][x][0] = loadedImage[y][x][0];
        newPixels[y][x][1] = loadedImage[y][x][1];
        newPixels[y][x][2] = loadedImage[y][x][2];
      }
    }

    for (int y = 0; y < height; y++) {
      for (int x = 0; x < width; x++) {
        float[] newColor = {0.0f, 0.0f, 0.0f};
        for (int ky = -1; ky <= 1; ky++) {
          for (int kx = -1; kx <= 1; kx++) {
            int x0 = Math.min(Math.max(x + kx, 0), width - 1);
            int y0 = Math.min(Math.max(y + ky, 0), height - 1);
            float w = kernel[(ky + 1) * 3 + (kx + 1)];

            int r = newPixels[y0][x0][0];
            int g = newPixels[y0][x0][1];
            int b = newPixels[y0][x0][2];

            float r_prime = r * w;
            float g_prime = g * w;
            float b_prime = b * w;

            newColor[0] += r_prime;
            newColor[1] += g_prime;
            newColor[2] += b_prime;
          }
        }

        newPixels[y][x][0] = clipNumber(Math.round(newColor[0]));
        newPixels[y][x][1] = clipNumber(Math.round(newColor[1]));
        newPixels[y][x][2] = clipNumber(Math.round(newColor[2]));
      }
    }

    return createNewFile(newPixels);
  }

  /**
   * Method to sharpen image using the matrix representation. This is achieved by applying
   * convolution operation to every pixel. The convolution operation takes the rgb of the current
   * pixel [r,g,b] and with the kernel we will apply this: Output(x, y) = ∑∑ I(x+i, y+j) * K(i, j)
   * for i = -m to m, j = -n to n Output(x, y)   : New pixel value at position (x, y) in the output
   * image I(x+i, y+j)    : Pixel value of the original image at position (x+i, y+j) K(i, j) :
   * Weight at position (i, j) in the filter (kernel) ∑∑             : Summation over all positions
   * of the kernel m              : Half the height of the kernel (for a 3x3 kernel, m = 1) n : Half
   * the width of the kernel (for a 3x3 kernel, n = 1) Basically, you will multiply each
   * corresponding value with its corresponding weight from the kernel. in the end, you multiply
   * with every possible combination of the value for that matrix
   *
   * @return Matrix representation of the sharpen image
   */
  @Override
  public ImageFormat getSharpen() {
    int height = loadedImage.length;
    int width = loadedImage[0].length;
    int[][][] newPixels = new int[height][width][3];

    // Define the convolution kernel
    double[][] kernel = {
        {-1.0 / 8, -1.0 / 8, -1.0 / 8, -1.0 / 8, -1.0 / 8},
        {-1.0 / 8, 1.0 / 4, 1.0 / 4, 1.0 / 4, -1.0 / 8},
        {-1.0 / 8, 1.0 / 4, 1.0, 1.0 / 4, -1.0 / 8},
        {-1.0 / 8, 1.0 / 4, 1.0 / 4, 1.0 / 4, -1.0 / 8},
        {-1.0 / 8, -1.0 / 8, -1.0 / 8, -1.0 / 8, -1.0 / 8}
    };

    for (int y = 0; y < height; y++) {
      for (int x = 0; x < width; x++) {
        double[] newColor = new double[3]; // For R, G, B

        // Apply the kernel
        for (int ky = -1; ky <= 1; ky++) {
          for (int kx = -1; kx <= 1; kx++) {
            int pixelY = Math.min(Math.max(y + ky, 0), height - 1);
            int pixelX = Math.min(Math.max(x + kx, 0), width - 1);

            // Get the RGB values
            int red = loadedImage[pixelY][pixelX][0];
            int green = loadedImage[pixelY][pixelX][1];
            int blue = loadedImage[pixelY][pixelX][2];

            // Accumulate the color values multiplied by the kernel
            newColor[0] += red * kernel[ky + 1][kx + 1];
            newColor[1] += green * kernel[ky + 1][kx + 1];
            newColor[2] += blue * kernel[ky + 1][kx + 1];
          }
        }

        // Set the new pixel color, clamped to 0-255
        newPixels[y][x][0] = clipNumber((int) newColor[0]);
        newPixels[y][x][1] = clipNumber((int) newColor[1]);
        newPixels[y][x][2] = clipNumber((int) newColor[2]);
      }
    }
    return createNewFile(newPixels);
  }
}