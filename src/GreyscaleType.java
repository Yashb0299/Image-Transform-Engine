/**
 * Enum representing different methods to apply a greyscale effect to an image. Each method has its
 * own way of converting RGB values to greyscale.
 */
public enum GreyscaleType {
  LUMA {
    /**
     Applies the Luma greyscale formula to the RGB values.
     The formula is: 0.2126 * red + 0.7152 * green + 0.0722 * blue
     */
    @Override
    protected int procedureImpl(int red, int green, int blue) {
      return (int) (0.2126 * red + 0.7152 * green + 0.0722 * blue);
    }
  },
  INTENSITY {
    /**
     Applies the Intensity greyscale formula to the RGB values.
     The formula is the average of the RGB values: (red + green + blue) / 3
     */
    @Override
    protected int procedureImpl(int red, int green, int blue) {
      double average = (red + blue + green) / 3.0;
      return (int) Math.floor(average);
    }
  },
  VALUE {
    /**
     Applies the Value greyscale formula to the RGB values.
     The formula is the maximum of the RGB values.
     */
    @Override
    protected int procedureImpl(int red, int green, int blue) {
      return Math.max(Math.max(red, blue), green);
    }
  };

  /**
   * Abstract method that applies the specific greyscale formula to RGB values.
   */
  protected abstract int procedureImpl(int red, int green, int blue);
}