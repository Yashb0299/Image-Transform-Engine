import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * Controller Script. Main class to handle image processing via commands. This class provides a
 * command-line interface for loading, manipulating, and saving images of various formats, including
 * JPG, PNG, and PPM.
 */
public class ImageProcessorScript {

  private final Map<String, ImageFormat> images = new HashMap<>();
  private final Map<String, ImageType> imageFormats = new HashMap<>();

  /**
   * Main entry point for the image processing application.
   *
   * @param args command-line arguments (not used)
   */
  public static void main(String[] args) {
    ImageProcessorScript app = new ImageProcessorScript();
    app.run();
  }

  /**
   * Runs the main application loop, prompting the user to choose between entering commands manually
   * or running a script, then proceeding accordingly.
   */
  public void run() {
    Scanner scanner = new Scanner(System.in);

    while (true) {
      System.out.println("Choose an option: (1) Enter commands manually (2) Run a script (3) Exit");
      String option = scanner.nextLine().trim();

      switch (option) {
        case "1":
          promptForImage(
              scanner);  // Only ask to load an image if they want to enter commands manually
          handleManualCommands(scanner);
          break;
        case "2":
          System.out.println("Enter the path to the script file:");
          String scriptPath = scanner.nextLine().trim();
          runScript(scriptPath);
          break;
        case "3":
          System.out.println("Exiting the program.");
          scanner.close();
          return;
        default:
          System.out.println("Invalid option. Please choose 1, 2, or 3.");
      }
    }
  }

  /**
   * Prompts the user for an image path and a name, then loads the image.
   *
   * @param scanner the scanner to use for user input
   */
  private void promptForImage(Scanner scanner) {
    System.out.println("Please enter the path to a sample image file:");
    String imagePath = scanner.nextLine().trim();

    System.out.println("Enter a name for the loaded image:");
    String imageName = scanner.nextLine().trim();
    loadImage(imagePath, imageName);
  }

  /**
   * Loads an image from the specified path and associates it with the given name.
   *
   * @param imagePath the file path to the image
   * @param imageName the name to associate with the loaded image
   */
  private void loadImage(String imagePath, String imageName) {
    String extension = imagePath.substring(imagePath.lastIndexOf('.') + 1).toUpperCase();

    if (extension.equals("PPM")) {
      System.out.println("Extension given PPM: " + imagePath + " and :" + imageName);
      loadPPMImage(imagePath, imageName);
    } else {
      try {
        int[][][] pixels = ImageReader.readImage(imagePath);
        ImageFormat img;

        switch (ImageType.valueOf(extension)) {
          case JPG:
            img = new JPGHandler(pixels);
            imageFormats.put(imageName, ImageType.JPG);
            break;
          case PNG:
            img = new PNGHandler(pixels);
            imageFormats.put(imageName, ImageType.PNG);
            break;
          default:
            throw new IllegalArgumentException("Unsupported image format: " + extension);
        }

        images.put(imageName, img);
        System.out.println("Image loaded successfully as " + imageName);
      } catch (Exception e) {
        System.err.println("Error loading image: " + e.getMessage());
      }
    }
  }

  /**
   * Loads a PPM image from the specified path and associates it with the given name.
   *
   * @param imagePath the file path to the PPM image
   * @param imageName the name to associate with the loaded image
   */
  private void loadPPMImage(String imagePath, String imageName) {
    try {
      ImageFormat img = new PPMHandler(imagePath);
      images.put(imageName, img);
      imageFormats.put(imageName, ImageType.PPM);
    } catch (IOException e) {
      System.err.println("Error loading PPM image: " + e.getMessage());
    }
  }

  /**
   * Handles manual command input from the user.
   *
   * @param scanner the Scanner object for user input
   */
  private void handleManualCommands(Scanner scanner) {
    System.out.println("Enter commands (or type 'exit' to return to the main menu):");
    while (true) {
      String command = scanner.nextLine().trim();
      if (command.equalsIgnoreCase("exit")) {
        break;
      }
      try {
        executeCommand(command);
      } catch (Exception e) {
        System.err.println("Error executing command: " + e.getMessage());
      }
    }
  }

  /**
   * Executes a script file containing a series of commands.
   *
   * @param scriptPath the path to the script file
   */
  public void runScript(String scriptPath) {
    try (Scanner scanner = new Scanner(new File(scriptPath))) {
      while (scanner.hasNextLine()) {
        String line = scanner.nextLine().trim();
        if (line.startsWith("#") || line.isEmpty()) {
          continue;
        }
        try {
          executeCommand(line);
        } catch (Exception e) {
          System.err.println("Error executing script command: " + e.getMessage());
        }
      }
    } catch (IOException e) {
      System.err.println("Script file not found: " + e.getMessage());
    }
  }

  /**
   * Executes a single command entered by the user.
   *
   * @param command the command to be executed
   */
  private void executeCommand(String command) {
    String[] parts = command.split("\\s+");
    switch (parts[0]) {
      case "load":
        if (parts.length != 3) {
          throw new IllegalArgumentException("Usage: load <image-path> <image-name>");
        }
        loadImage(parts[1], parts[2]);
        break;
      case "save":
        if (parts.length < 3) {
          throw new IllegalArgumentException("Usage: save <image-name> <output-path> [<format>]");
        }
        String imageName = parts[1];
        String outputPath = parts[2];
        String desiredFormat = (parts.length == 4) ? parts[3].toUpperCase() : null;
        saveImage(imageName, outputPath, desiredFormat);
        break;
      case "brighten":
        if (parts.length != 4) {
          throw new IllegalArgumentException(
              "Usage: brighten <increment> <image-name> <dest-image-name>");
        }
        try {
          int increment = Integer.parseInt(parts[1]);
          brightenImage(increment, parts[2], parts[3]);
          autoSave(parts[3]);
        } catch (NumberFormatException e) {
          throw new IllegalArgumentException("Invalid increment value: " + parts[1]);
        }
        break;
      case "horizontal-flip":
        if (parts.length != 3) {
          throw new IllegalArgumentException(
              "Usage: horizontal-flip <image-name> <dest-image-name>");
        }
        horizontalFlip(parts[1], parts[2]);
        autoSave(parts[2]);
        break;
      case "vertical-flip":
        if (parts.length != 3) {
          throw new IllegalArgumentException("Usage: vertical-flip <image-name> <dest-image-name>");
        }
        verticalFlip(parts[1], parts[2]);
        autoSave(parts[2]);
        break;
      case "red-component":
        if (parts.length != 3) {
          throw new IllegalArgumentException("Usage: red-component <image-name> <dest-image-name>");
        }
        redComponent(parts[1], parts[2]);
        autoSave(parts[2]);
        break;
      case "green-component":
        if (parts.length != 3) {
          throw new IllegalArgumentException(
              "Usage: green-component <image-name> <dest-image-name>");
        }
        greenComponent(parts[1], parts[2]);
        autoSave(parts[2]);
        break;
      case "blue-component":
        if (parts.length != 3) {
          throw new IllegalArgumentException(
              "Usage: blue-component <image-name> <dest-image-name>");
        }
        blueComponent(parts[1], parts[2]);
        autoSave(parts[2]);
        break;
      case "luma-component":
        if (parts.length != 3) {
          throw new IllegalArgumentException(
              "Usage: luma-component <image-name> <dest-image-name>");
        }
        lumaComponent(parts[1], parts[2]);
        autoSave(parts[2]);
        break;
      case "value-component":
        if (parts.length != 3) {
          throw new IllegalArgumentException(
              "Usage: value-component <image-name> <dest-image-name>");
        }
        valueComponent(parts[1], parts[2]);
        autoSave(parts[2]);
        break;
      case "intensity-component":
        if (parts.length != 3) {
          throw new IllegalArgumentException(
              "Usage: intensity-component <image-name> <dest-image-name>");
        }
        intensityComponent(parts[1], parts[2]);
        autoSave(parts[2]);
        break;
      case "rgb-split":
        if (parts.length != 5) {
          throw new IllegalArgumentException(
              "Usage: rgb-split <image-name> <red-image> <green-image> <blue-image>");
        }
        rgbSplit(parts[1], parts[2], parts[3], parts[4]);
        autoSave(parts[2]);
        autoSave(parts[3]);
        autoSave(parts[4]);
        break;
      case "rgb-combine":
        if (parts.length != 5) {
          throw new IllegalArgumentException(
              "Usage: rgb-combine <dest-image-name> <red-image> <green-image> <blue-image>");
        }
        rgbCombine(parts[1], parts[2], parts[3], parts[4]);
        autoSave(parts[1]);
        break;
      case "sepia":
        if (parts.length != 3) {
          throw new IllegalArgumentException("Usage: sepia <image-name> <dest-image-name>");
        }
        sepiaImage(parts[1], parts[2]);
        autoSave(parts[2]);
        break;
      case "blur":
        if (parts.length != 3) {
          throw new IllegalArgumentException("Usage: blur <image-name> <dest-image-name>");
        }
        blurImage(parts[1], parts[2]);
        autoSave(parts[2]);
        break;
      case "sharpen":
        if (parts.length != 3) {
          throw new IllegalArgumentException("Usage: sharpen <image-name> <dest-image-name>");
        }
        sharpenImage(parts[1], parts[2]);
        autoSave(parts[2]);
        break;
      default:
        throw new IllegalArgumentException("Unknown command: " + parts[0]);
    }
  }

  /**
   * Extracts the red component from the specified image and creates a new image.
   *
   * @param sourceImageName the name of the source image
   * @param destImageName   the name to associate with the new red component image
   */
  private void redComponent(String sourceImageName, String destImageName) {
    try {
      ImageFormat img = images.get(sourceImageName);
      if (img == null) {
        throw new IllegalArgumentException("Image not found: " + sourceImageName);
      }
      ImageFormat redImage = img.getRed();
      images.put(destImageName, redImage);
      imageFormats.put(destImageName, imageFormats.get(sourceImageName));
      System.out.println(
          "Red component image available as: " + destImageName + ". Use 'save " + destImageName
              + " <output-path>' to save.");
    } catch (Exception e) {
      System.err.println("Error extracting red component: " + e.getMessage());
    }
  }

  /**
   * Automatically saves the specified image using a default output path and format.
   *
   * @param imageName the name of the image to save
   */
  private void autoSave(String imageName) {
    try {
      String outputPath = "res/images/" + imageName + ".png";
      ImageFormat img = images.get(imageName);

      if (img == null) {
        System.err.println("Error: Image with name " + imageName + " not found.");
        return;
      }

      ImageType format = imageFormats.get(imageName);
      if (format == null) {
        format = ImageType.PNG;
      }

      img.saveImg(img.getPixels(), outputPath, format);
      System.out.println("Image automatically saved as: " + outputPath);
    } catch (IOException e) {
      System.err.println("Error saving the image: " + e.getMessage());
    } catch (Exception e) {
      System.err.println("Unexpected error: " + e.getMessage());
    }
  }

  /**
   * Extracts the green component from the specified image and creates a new image.
   *
   * @param sourceImageName the name of the source image
   * @param destImageName   the name to associate with the new green component image
   */
  private void greenComponent(String sourceImageName, String destImageName) {
    try {
      ImageFormat img = images.get(sourceImageName);
      if (img == null) {
        throw new IllegalArgumentException("Image not found: " + sourceImageName);
      }
      ImageFormat greenImage = img.getGreen();
      images.put(destImageName, greenImage);
      imageFormats.put(destImageName, imageFormats.get(sourceImageName));
      System.out.println(
          "Green component image available as: " + destImageName + ". Use 'save " + destImageName
              + " <output-path>' to save.");
    } catch (Exception e) {
      System.err.println("Error extracting green component: " + e.getMessage());
    }
  }

  /**
   * Extracts the blue component from the specified image and creates a new image.
   *
   * @param sourceImageName the name of the source image
   * @param destImageName   the name to associate with the new blue component image
   */
  private void blueComponent(String sourceImageName, String destImageName) {
    try {
      ImageFormat img = images.get(sourceImageName);
      if (img == null) {
        throw new IllegalArgumentException("Image not found: " + sourceImageName);
      }
      ImageFormat blueImage = img.getBlue();
      images.put(destImageName, blueImage);
      imageFormats.put(destImageName, imageFormats.get(sourceImageName));
      System.out.println(
          "Blue component image available as: " + destImageName + ". Use 'save " + destImageName
              + " <output-path>' to save.");
    } catch (Exception e) {
      System.err.println("Error extracting blue component: " + e.getMessage());
    }
  }

  /**
   * Applies the Luma transformation to the specified image and creates a new image.
   *
   * @param sourceImageName the name of the source image
   * @param destImageName   the name to associate with the new Luma component image
   */
  private void lumaComponent(String sourceImageName, String destImageName) {
    try {
      ImageFormat img = images.get(sourceImageName);
      if (img == null) {
        throw new IllegalArgumentException("Image not found: " + sourceImageName);
      }

      ImageFormat lumaImage = img.getGreyscale(GreyscaleType.LUMA);
      images.put(destImageName, lumaImage);
      imageFormats.put(destImageName, imageFormats.get(sourceImageName));
      System.out.println(
          "Luma component image available as: " + destImageName + ". Use 'save " + destImageName
              + " <output-path>' to save.");
    } catch (Exception e) {
      System.err.println("Error applying luma component: " + e.getMessage());
    }
  }

  /**
   * Applies the Value transformation to the specified image and creates a new image.
   *
   * @param sourceImageName the name of the source image
   * @param destImageName   the name to associate with the new Value component image
   */
  private void valueComponent(String sourceImageName, String destImageName) {
    try {
      ImageFormat img = images.get(sourceImageName);
      if (img == null) {
        throw new IllegalArgumentException("Image not found: " + sourceImageName);
      }

      ImageFormat valueImage = img.getGreyscale(GreyscaleType.VALUE);
      images.put(destImageName, valueImage);
      imageFormats.put(destImageName, imageFormats.get(sourceImageName));
      System.out.println(
          "Value component image available as: " + destImageName + ". Use 'save " + destImageName
              + " <output-path>' to save.");
    } catch (Exception e) {
      System.err.println("Error applying value component: " + e.getMessage());
    }
  }

  /**
   * Applies the Intensity transformation to the specified image and creates a new image.
   *
   * @param sourceImageName the name of the source image
   * @param destImageName   the name to associate with the new Intensity component image
   */
  private void intensityComponent(String sourceImageName, String destImageName) {
    try {
      ImageFormat img = images.get(sourceImageName);
      if (img == null) {
        throw new IllegalArgumentException("Image not found: " + sourceImageName);
      }

      ImageFormat intensityImage = img.getGreyscale(GreyscaleType.INTENSITY);
      images.put(destImageName, intensityImage);
      imageFormats.put(destImageName, imageFormats.get(sourceImageName));
      System.out.println("Intensity component image available as: " + destImageName + ". Use 'save "
          + destImageName + " <output-path>' to save.");
    } catch (Exception e) {
      System.err.println("Error applying intensity component: " + e.getMessage());
    }
  }

  /**
   * Brightens the specified image by a given increment and creates a new image.
   *
   * @param increment       the amount to brighten the image
   * @param sourceImageName the name of the source image
   * @param destImageName   the name to associate with the new brightened image
   */
  private void brightenImage(int increment, String sourceImageName, String destImageName) {
    try {
      ImageFormat img = images.get(sourceImageName);
      if (img == null) {
        throw new IllegalArgumentException("Image not found: " + sourceImageName);
      }
      ImageFormat brightenedImage = img.getChangeBrightness(increment);
      images.put(destImageName, brightenedImage);
      imageFormats.put(destImageName, imageFormats.get(sourceImageName));
      System.out.println(
          "Brightened image available as: " + destImageName + ". Use 'save " + destImageName
              + " <output-path>' to save.");
    } catch (Exception e) {
      System.err.println("Error brightening image: " + e.getMessage());
    }
  }

  /**
   * Flips the specified image horizontally and creates a new image.
   *
   * @param sourceImageName the name of the source image
   * @param destImageName   the name to associate with the new horizontally flipped image
   */
  private void horizontalFlip(String sourceImageName, String destImageName) {
    try {
      ImageFormat img = images.get(sourceImageName);
      if (img == null) {
        throw new IllegalArgumentException("Image not found: " + sourceImageName);
      }
      ImageFormat flippedImage = img.getHorizontalFlip();
      images.put(destImageName, flippedImage);
      imageFormats.put(destImageName, imageFormats.get(sourceImageName));
      System.out.println(
          "Horizontally flipped image available as: " + destImageName + ". Use 'save "
              + destImageName + " <output-path>' to save.");
    } catch (Exception e) {
      System.err.println("Error flipping image horizontally: " + e.getMessage());
    }
  }

  /**
   * Flips the specified image vertically and creates a new image.
   *
   * @param sourceImageName the name of the source image
   * @param destImageName   the name to associate with the new vertically flipped image
   */
  private void verticalFlip(String sourceImageName, String destImageName) {
    try {
      ImageFormat img = images.get(sourceImageName);
      if (img == null) {
        throw new IllegalArgumentException("Image not found: " + sourceImageName);
      }
      ImageFormat flippedImage = img.getVerticalFlip();
      images.put(destImageName, flippedImage);
      imageFormats.put(destImageName, imageFormats.get(sourceImageName));
      System.out.println(
          "Vertically flipped image available as: " + destImageName + ". Use 'save " + destImageName
              + " <output-path>' to save.");
    } catch (Exception e) {
      System.err.println("Error flipping image vertically: " + e.getMessage());
    }
  }

  /**
   * Splits the RGB components from the specified image into separate images.
   *
   * @param sourceImageName the name of the source image
   * @param redImageName    the name to associate with the red component image
   * @param greenImageName  the name to associate with the green component image
   * @param blueImageName   the name to associate with the blue component image
   */
  private void rgbSplit(String sourceImageName, String redImageName, String greenImageName,
      String blueImageName) {
    try {
      ImageFormat img = images.get(sourceImageName);
      if (img == null) {
        throw new IllegalArgumentException("Image not found: " + sourceImageName);
      }
      ImageFormat redImage = img.getRed();
      ImageFormat greenImage = img.getGreen();
      ImageFormat blueImage = img.getBlue();
      images.put(redImageName, redImage);
      images.put(greenImageName, greenImage);
      images.put(blueImageName, blueImage);
      imageFormats.put(redImageName, imageFormats.get(sourceImageName));
      imageFormats.put(greenImageName, imageFormats.get(sourceImageName));
      imageFormats.put(blueImageName, imageFormats.get(sourceImageName));
      System.out.println(
          "RGB components available as: " + redImageName + ", " + greenImageName + ", "
              + blueImageName + ". Use 'save' command to save each.");
    } catch (Exception e) {
      System.err.println("Error splitting RGB: " + e.getMessage());
    }
  }

  /**
   * Combines the specified RGB component images into a single image.
   *
   * @param destImageName  the name to associate with the new combined image
   * @param redImageName   the name of the red component image
   * @param greenImageName the name of the green component image
   * @param blueImageName  the name of the blue component image
   */
  private void rgbCombine(String destImageName, String redImageName, String greenImageName,
      String blueImageName) {
    try {
      ImageFormat redImage = images.get(redImageName);
      ImageFormat greenImage = images.get(greenImageName);
      ImageFormat blueImage = images.get(blueImageName);
      if (redImage == null || greenImage == null || blueImage == null) {
        throw new IllegalArgumentException("One of the RGB images not found.");
      }
      int height = redImage.getPixels().length;
      int width = redImage.getPixels()[0].length;
      int[][][] combinedPixels = new int[height][width][3];
      for (int row = 0; row < height; row++) {
        for (int col = 0; col < width; col++) {
          combinedPixels[row][col][0] = redImage.getPixels()[row][col][0];
          combinedPixels[row][col][1] = greenImage.getPixels()[row][col][1];
          combinedPixels[row][col][2] = blueImage.getPixels()[row][col][2];
        }
      }
      ImageFormat combinedImage = createImageFromPixels(combinedPixels,
          imageFormats.get(redImageName));
      images.put(destImageName, combinedImage);
      imageFormats.put(destImageName, imageFormats.get(redImageName));
      System.out.println(
          "Combined RGB image available as: " + destImageName + ". Use 'save " + destImageName
              + " <output-path>' to save.");
    } catch (Exception e) {
      System.err.println("Error combining RGB: " + e.getMessage());
    }
  }

  /**
   * Creates a new image from the given pixel data and format.
   *
   * @param pixels the pixel data for the new image
   * @param type   the image type of the new image
   * @return a new ImageFormat instance representing the created image
   */
  private ImageFormat createImageFromPixels(int[][][] pixels, ImageType type) {
    switch (type) {
      case JPG:
        return new JPGHandler(pixels);
      case PNG:
        return new PNGHandler(pixels);
      case PPM:
        return new PPMHandler(pixels);
      default:
        throw new IllegalArgumentException("Unsupported image format: " + type);
    }
  }

  /**
   * Applies a blur effect to the specified image and creates a new image.
   *
   * @param sourceImageName the name of the source image
   * @param destImageName   the name to associate with the new blurred image
   */
  private void blurImage(String sourceImageName, String destImageName) {
    try {
      ImageFormat img = images.get(sourceImageName);
      if (img == null) {
        throw new IllegalArgumentException("Image not found: " + sourceImageName);
      }
      ImageFormat blurredImage = img.getBlur();
      images.put(destImageName, blurredImage);
      imageFormats.put(destImageName, imageFormats.get(sourceImageName));
      System.out.println(
          "Blurred image available as: " + destImageName + ". Use 'save " + destImageName
              + " <output-path>' to save.");
    } catch (Exception e) {
      System.err.println("Error applying blur: " + e.getMessage());
    }
  }

  /**
   * Applies a sharpen effect to the specified image and creates a new image.
   *
   * @param sourceImageName the name of the source image
   * @param destImageName   the name to associate with the new sharpened image
   */
  private void sharpenImage(String sourceImageName, String destImageName) {
    try {
      ImageFormat img = images.get(sourceImageName);
      if (img == null) {
        throw new IllegalArgumentException("Image not found: " + sourceImageName);
      }
      ImageFormat sharpenedImage = img.getSharpen();
      images.put(destImageName, sharpenedImage);
      imageFormats.put(destImageName, imageFormats.get(sourceImageName));
      System.out.println(
          "Sharpened image available as: " + destImageName + ". Use 'save " + destImageName
              + " <output-path>' to save.");
    } catch (Exception e) {
      System.err.println("Error applying sharpen: " + e.getMessage());
    }
  }

  /**
   * Applies a sepia effect to the specified image and creates a new image.
   *
   * @param sourceImageName the name of the source image
   * @param destImageName   the name to associate with the new sepia image
   */
  private void sepiaImage(String sourceImageName, String destImageName) {
    try {
      ImageFormat img = images.get(sourceImageName);
      if (img == null) {
        throw new IllegalArgumentException("Image not found: " + sourceImageName);
      }
      ImageFormat sepiaImage = img.getSepia();
      images.put(destImageName, sepiaImage);
      imageFormats.put(destImageName, imageFormats.get(sourceImageName));
      System.out.println(
          "Sepia image available as: " + destImageName + ". Use 'save " + destImageName
              + " <output-path>' to save.");
    } catch (Exception e) {
      System.err.println("Error applying sepia effect: " + e.getMessage());
    }
  }

  /**
   * Saves the specified image to the given output path in the desired format.
   *
   * @param imageName     the name of the image to save
   * @param outputPath    the path to save the image to
   * @param desiredFormat the format to save the image in (optional)
   */
  private void saveImage(String imageName, String outputPath, String desiredFormat) {
    try {
      ImageFormat img = images.get(imageName);

      if (img == null) {
        System.err.println("Error: Image with name " + imageName + " not found.");
        return;
      }

      ImageType format = imageFormats.get(imageName);
      if (format == null) {
        System.err.println("Error: Format not found for image " + imageName);
        return;
      }

      String outputImagePath = outputPath;

      if (desiredFormat != null) {
        switch (desiredFormat.toLowerCase()) {
          case "ppm":
            outputImagePath = outputPath.endsWith(".ppm") ? outputPath : outputPath + ".ppm";
            format = ImageType.PPM;
            break;
          case "jpg":
          case "jpeg":
            outputImagePath =
                outputPath.endsWith(".jpg") || outputPath.endsWith(".jpeg") ? outputPath
                    : outputPath + ".jpg";
            format = ImageType.JPG;
            break;
          case "png":
            outputImagePath = outputPath.endsWith(".png") ? outputPath : outputPath + ".png";
            format = ImageType.PNG;
            break;
          default:
            System.err.println(
                "Error: Unsupported format " + desiredFormat + ". Defaulting to PNG.");
            outputImagePath = outputPath + ".png";
            format = ImageType.PNG;
            break;
        }
      } else {
        if (outputPath.toLowerCase().endsWith(".ppm")) {
          format = ImageType.PPM;
        } else if (outputPath.toLowerCase().endsWith(".jpg") || outputPath.toLowerCase()
            .endsWith(".jpeg")) {
          format = ImageType.JPG;
        } else if (outputPath.toLowerCase().endsWith(".png")) {
          format = ImageType.PNG;
        } else {
          System.out.println("No valid format detected in output path, defaulting to PNG.");
          outputImagePath = outputPath + ".png";
          format = ImageType.PNG;
        }
      }

      if (format == ImageType.PPM && img instanceof PPMHandler) {
        ((PPMHandler) img).saveImgLoader(img, outputImagePath);
      } else {
        img.saveImg(img.getPixels(), outputImagePath, format);
      }

      System.out.println("Image saved as: " + outputImagePath);
    } catch (IOException e) {
      System.err.println("Error saving the image: " + e.getMessage());
    } catch (Exception e) {
      System.err.println("Unexpected error: " + e.getMessage());
    }
  }
}