import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit test class for the PPM concrete class' methods.
 */

public class PPMHandlerTest {

  PPMHandler pixels;

  /**
   * Helper Test function to assert equal arrays but with a margin of error.
   *
   * @param array1 Array 1 to compare
   * @param array2 Array 2 to compare
   * @param margin Margin of error
   * @return true if both are equal with margin of error, false otherwise
   */
  boolean comparePixelArrays(int[][][] array1, int[][][] array2, int margin) {
    if (array1.length != array2.length || array1[0].length != array2[0].length
        || array1[0][0].length != array2[0][0].length) {
      return false; // Arrays are not the same size
    }

    for (int y = 0; y < array1.length; y++) {
      for (int x = 0; x < array1[y].length; x++) {
        for (int channel = 0; channel < array1[y][x].length; channel++) {
          int value1 = array1[y][x][channel];
          int value2 = array2[y][x][channel];

          // Check if the values are within the margin of error
          if (Math.abs(value1 - value2) > margin) {
            return false;
          }
        }
      }
    }

    return true; // All pixels match within the margin
  }

  @Before
  public void setUp() {
    try {
      pixels = new PPMHandler("res/PPM/P3.ppm");
    } catch (IOException e) {
      fail("Could not load the original image");
    }
  }

  /**
   * Method to check if constructors work correctly.
   */
  @Test
  public void testConstructor() {
    try {
      pixels = new PPMHandler("res/PPM/P3.ppm");
    } catch (IOException e) {
      fail("Could not load the original image");
    }
    assertNotNull(pixels);

    PPMHandler arrayInitializedPixel = new PPMHandler(pixels.getPixels());
    assertNotNull(arrayInitializedPixel);

    try {
      pixels = new PPMHandler("NonExistingImage.ppm");
      fail("Image doesn't exist, so this test is suppose to fail");
    } catch (IOException e) {
      // Suppose to fail because file doesn't exist
    }

    try {
      pixels = new PPMHandler(new int[][][]{});
      fail("Image doesn't exist, so this test is suppose to fail");
    } catch (IllegalArgumentException e) {
      // Suppose to fail because file doesn't exist
    }
  }

  /**
   * Method to test brightness manipulation on images.
   */
  @Test
  public void testBrightness() {
    PPMHandler pixelsHorizontalComparison = null;
    try {
      pixelsHorizontalComparison =
          new PPMHandler("res/PPM/P3-brighter-by-50.ppm");
    } catch (IOException e) {
      fail("Could not load the brighter by 50 image");
    }
    assertArrayEquals(pixels.getChangeBrightness(50.0f).getPixels(),
        pixelsHorizontalComparison.getPixels());
    try {
      pixelsHorizontalComparison =
          new PPMHandler("res/PPM/P3-darker-by-50.ppm");
    } catch (IOException e) {
      fail("Could not load the darker by 50 image");
    }
    assertArrayEquals(pixels.getChangeBrightness(-50.0f).getPixels(),
        pixelsHorizontalComparison.getPixels());
  }

  /**
   * Unit Test to check if flip transformations are applied correctly to the image.
   */
  @Test
  public void testFlipTransformations() {

    PPMHandler flipped = (PPMHandler) pixels.getHorizontalFlip();
    PPMHandler pixelsRotation = null;
    try {
      pixelsRotation = new PPMHandler("res/PPM/P3-horizontal.ppm");
    } catch (IOException e) {
      fail("Horizontal Comparison Image was not loaded correctly");
    }
    assertArrayEquals(flipped.getPixels(), pixelsRotation.getPixels());

    flipped = (PPMHandler) pixels.getVerticalFlip().getHorizontalFlip();

    try {
      pixelsRotation =
          new PPMHandler(
              "res/PPM/P3-vertical-then-horizontal.ppm"
          );
    } catch (IOException e) {
      fail("Horizontal Comparison Image was not loaded correctly");
    }
    assertArrayEquals(flipped.getPixels(), pixelsRotation.getPixels());
  }

  /**
   * Unit test to check if color channel splitting works correctly.
   */
  @Test
  public void testCombinationImage() throws IOException {
    PPMHandler pixelsComparison = null;
    // Test getting the red channel
    try {
      pixelsComparison = new PPMHandler("res/PPM/P3-red.ppm");
    } catch (IOException e) {
      fail("Red Greyscale Image was not loaded correctly");
    }
    int[][][] limaPixelsRed = pixels.getRed().getPixels();
    assertArrayEquals(limaPixelsRed, pixelsComparison.getPixels());

    //test getting the blue channel
    try {
      pixelsComparison = new PPMHandler("res/PPM/P3-blue.ppm");
    } catch (IOException e) {
      fail("Blue Greyscale Image was not loaded correctly");
    }
    int[][][] limaPixelsBlue = pixels.getBlue().getPixels();
    assertArrayEquals(limaPixelsBlue, pixelsComparison.getPixels());

    //test getting the green channel
    try {
      pixelsComparison = new PPMHandler("res/PPM/P3-green.ppm");
    } catch (IOException e) {
      fail("Blue Greyscale Image was not loaded correctly");
    }
    int[][][] limaPixelsGreen = pixels.getGreen().getPixels();
    assertArrayEquals(limaPixelsGreen, pixelsComparison.getPixels());

    //test combining the channels
    try {
      pixelsComparison = new PPMHandler("res/PPM/P3.ppm");
    } catch (IOException e) {
      fail("Original Image was not loaded correctly");
    }

    int[][][] limaPixelsCombination =
        pixels.combineToGrayscale(
            pixels.getGreen().getPixels(),
            pixels.getBlue().getPixels(),
            pixels.getRed().getPixels()
        ).getPixels();
    pixels.saveImgMatrix(limaPixelsCombination, "res/PPM/P3-combined.ppm");

    assertArrayEquals(limaPixelsCombination, pixelsComparison.getPixels());
  }

  /**
   * Unit test to check if sepia transformation effect work properly on matrix.
   */
  @Test
  public void testSepiaEffect() {
    PPMHandler pixelsComparison = null;
    try {
      pixelsComparison = new PPMHandler("res/PPM/P3-sepia.ppm");
    } catch (IOException e) {
      fail("Sepia effect Image was not loaded correctly");
    }
    assertTrue(comparePixelArrays(pixelsComparison.getPixels(), pixels.getSepia().getPixels(), 1));
  }

  /**
   * Unit test to check if Greyscale transformations work properly on matrix.
   */
  @Test
  public void testGrayscale() {
    // Luma
    PPMHandler pixelsComparison = null;
    try {
      pixelsComparison =
          new PPMHandler("res/PPM/P3-luma-greyscale.ppm");
    } catch (IOException e) {
      fail("Luma greyscale effect Image was not loaded correctly");
    }
    int[][][] limaPixels = pixels.getGreyscale(GreyscaleType.LUMA).getPixels();
    assertTrue(comparePixelArrays(pixelsComparison.getPixels(), limaPixels, 1));

    // Value
    try {
      pixelsComparison = new PPMHandler(
          "res/PPM/P3-value-greyscale.ppm");
    } catch (IOException e) {
      fail("Value greyscale effect Image was not loaded correctly");
    }
    limaPixels = pixels.getGreyscale(GreyscaleType.VALUE).getPixels();
    assertTrue(comparePixelArrays(pixelsComparison.getPixels(), limaPixels, 1));

    //Intensity
    try {
      pixelsComparison = new PPMHandler(
          "res/PPM/P3-intensity-greyscale.ppm");
    } catch (IOException e) {
      fail("Intensity greyscale effect Image was not loaded correctly");
    }
    limaPixels = pixels.getGreyscale(GreyscaleType.INTENSITY).getPixels();
    assertTrue(comparePixelArrays(pixelsComparison.getPixels(), limaPixels, 1));
  }

  /**
   * Unit test method to check if blurring image effect works accordingly.
   */
  @Test
  public void testBlurred() {
    PPMHandler pixelsComparison = null;
    try {
      pixelsComparison =
          new PPMHandler("res/PPM/P3-blur.ppm");
    } catch (IOException e) {
      fail("Blur effect Image was not loaded correctly");
    }
    assertTrue(comparePixelArrays(
        pixelsComparison.getPixels(),
        pixels.getBlur().getPixels(),
        1
    ));
  }

  /**
   * Unit test method to check if sharpening image effect works accordingly.
   */
  @Test
  public void testSharper() {
    PPMHandler pixelsComparison = null;
    try {
      pixelsComparison =
          new PPMHandler("res/PPM/P3-sharpen.ppm");
    } catch (IOException e) {
      fail("Sharpening effect Image was not loaded correctly");
    }
    assertTrue(comparePixelArrays(
        pixelsComparison.getPixels(),
        pixels.getSharpen().getPixels(),
        1
    ));
  }
}