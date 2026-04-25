/**
 * Represents a command that can be executed on an image processor.
 * <p>
 * Classes implementing this interface define specific commands that perform various operations on
 * an image processor.
 * </p>
 */
public interface Command {

  /**
   * Executes the command on the given image processor script.
   * <p>
   * Implementing classes will define the specific behavior of the command when executed, which may
   * involve modifying images, loading/saving images, or performing transformations.
   * </p>
   *
   * @param imageProcessorApp the image processor script to execute the command on
   * @throws Exception if any error occurs during command execution
   */
  void execute(ImageProcessorScript imageProcessorApp) throws Exception;
}
