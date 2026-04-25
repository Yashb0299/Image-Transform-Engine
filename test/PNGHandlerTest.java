
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit test class for the PNG concrete class' methods.
 */
public class PNGHandlerTest {

  PNGHandler pixels;

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
  public void setUp(){
    try {
      pixels = new PNGHandler("res/PNG/Mountain.png");
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
      pixels = new PNGHandler("res/PNG/Mountain.png");
    } catch (IOException e) {
      fail("Could not load the original image");
    }
    assertNotNull(pixels);

    PNGHandler arrayInitializedPixel = new PNGHandler(pixels.getPixels());
    assertNotNull(arrayInitializedPixel);

    try {
      pixels = new PNGHandler("NonExistingImage.png");
      fail("Image doesn't exist, so this test is suppose to fail");
    } catch (IOException e) {
      // Suppose to fail because file doesn't exist
    }

    try {
      pixels = new PNGHandler(new int[][][]{});
      fail("Image doesn't exist, so this test is suppose to fail");
    } catch (IllegalArgumentException e) {
      // Suppose to fail because file doesn't exist
    }
  }

  /**
   * Method to test brightness manipulation on images.
   */
  @Test
  public void testBrightness(){
    PNGHandler pixelsHorizontalComparison = null;
    try {
      pixelsHorizontalComparison =
          new PNGHandler("res/PNG/Mountain-brighter-by-50.png");
    } catch (IOException e) {
      fail("Could not load the brighter by 50 image");
    }
    assertArrayEquals(pixels.getChangeBrightness(50.0f).getPixels(),
        pixelsHorizontalComparison.getPixels());
    try {
      pixelsHorizontalComparison =
          new PNGHandler("res/PNG/Mountain-darker-by-50.png");
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
  public void testFlipTransformations(){

    PNGHandler flipped = (PNGHandler) pixels.getHorizontalFlip();
    PNGHandler pixelsRotation = null;
    try {
      pixelsRotation = new PNGHandler("res/PNG/Mountain-horizontal.png");
    } catch (IOException e) {
      fail("Horizontal Comparison Image was not loaded correctly");
    }
    assertArrayEquals(flipped.getPixels(), pixelsRotation.getPixels());

    flipped = (PNGHandler) pixels.getVerticalFlip().getHorizontalFlip();


    try {
      pixelsRotation =
          new PNGHandler(
              "res/PNG/Mountain-vertical-then-horizontal.png"
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
  public void testCombinationImage(){
    PNGHandler pixelsComparison = null;
    // Test getting the red channel
    try {
      pixelsComparison = new PNGHandler("res/PNG/Mountain-red.png");
    } catch (IOException e) {
      fail("Red Greyscale Image was not loaded correctly");
    }
    int[][][] limaPixelsRed = pixels.getRed().getPixels();
    assertArrayEquals(limaPixelsRed, pixelsComparison.getPixels());

    //test getting the blue channel
    try {
      pixelsComparison = new PNGHandler("res/PNG/Mountain-blue.png");
    } catch (IOException e) {
      fail("Blue Greyscale Image was not loaded correctly");
    }
    int[][][] limaPixelsBlue = pixels.getBlue().getPixels();
    assertArrayEquals(limaPixelsBlue, pixelsComparison.getPixels());

    //test getting the green channel
    try {
      pixelsComparison = new PNGHandler("res/PNG/Mountain-green.png");
    } catch (IOException e) {
      fail("Blue Greyscale Image was not loaded correctly");
    }
    int[][][] limaPixelsGreen = pixels.getGreen().getPixels();
    assertArrayEquals(limaPixelsGreen, pixelsComparison.getPixels());

    //test combining the channels
    try {
      pixelsComparison = new PNGHandler("res/PNG/Mountain.png");
    } catch (IOException e) {
      fail("Original Image was not loaded correctly");
    }
    int[][][] limaPixelsCombination =
        pixels.combineToGrayscale(limaPixelsGreen, limaPixelsBlue, limaPixelsRed).getPixels();
    assertArrayEquals(limaPixelsCombination, pixelsComparison.getPixels());

    limaPixelsCombination =
        pixels.combineToGrayscale(
            pixels.getGreen(),
            pixels.getBlue(),
            pixels.getRed()
        ).getPixels();
    assertArrayEquals(limaPixelsCombination, pixelsComparison.getPixels());
  }

  /**
   * Unit test to check if sepia transformation effect work properly on matrix.
   */
  @Test
  public void testSepiaEffect() {
    PNGHandler pixelsComparison = null;
    try {
      pixelsComparison = new PNGHandler("res/PNG/Mountain-sepia.png");
    } catch (IOException e) {
      fail("Sepia effect Image was not loaded correctly");
    }
    assertTrue(comparePixelArrays(pixelsComparison.getPixels(), pixels.getSepia().getPixels(), 1));
  }

  /**
   * Unit test to check if Greyscale transformations work properly on matrix.
   */
  @Test
  public void testGrayscale(){
    // Luma
    PNGHandler pixelsComparison = null;
    try {
      pixelsComparison =
          new PNGHandler("res/PNG/Mountain-luma-greyscale.png");
    } catch (IOException e) {
      fail("Luma greyscale effect Image was not loaded correctly");
    }
    int[][][] limaPixels = pixels.getGreyscale(GreyscaleType.LUMA).getPixels();
    assertTrue(comparePixelArrays(pixelsComparison.getPixels(), limaPixels, 1));

    // Value
    try {
      pixelsComparison = new PNGHandler(
          "res/PNG/Mountain-value-greyscale.png");
    } catch (IOException e) {
      fail("Value greyscale effect Image was not loaded correctly");
    }
    limaPixels = pixels.getGreyscale(GreyscaleType.VALUE).getPixels();
    assertTrue(comparePixelArrays(pixelsComparison.getPixels(), limaPixels, 1));

    //Intensity
    try {
      pixelsComparison = new PNGHandler(
          "res/PNG/Mountain-intensity-greyscale.png");
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
  public void testBlurred(){
    PNGHandler pixelsComparison = null;
    try {
      pixelsComparison =
          new PNGHandler("res/PNG/Mountain-blur.png");
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
  public void testSharper(){
    PNGHandler pixelsComparison = null;
    try {
      pixelsComparison =
          new PNGHandler("res/PNG/Mountain-sharper.png");
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