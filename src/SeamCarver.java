import edu.princeton.cs.algs4.Picture;
import java.util.Stack;

public class SeamCarver {

  private Picture picture;
  private static final int BORDER_ENERGY = 1000;

  public SeamCarver(Picture picture) {
    this.picture = picture;
  }

  public Picture picture() {
    return picture;
  }

  public int width() {
    return picture.width();
  }

  public int height() {
    return picture.height();
  }

  public double energy(int x, int y) {
    if (!validate(x, y)) {
      throw new IllegalArgumentException();
    }
    if (x == 0 || y == 0 || x == width() - 1 || y == height() - 1) {
      return BORDER_ENERGY;
    }
    int redX = picture.get(x + 1, y).getRed() - picture.get(x - 1, y).getRed();
    int greenX = picture.get(x + 1, y).getGreen() - picture.get(x - 1, y).getGreen();
    int blueX = picture.get(x + 1, y).getBlue() - picture.get(x - 1, y).getBlue();
    double yieldingX = redX * redX + greenX * greenX + blueX * blueX;

    int redY = picture.get(x, y + 1).getRed() - picture.get(x, y - 1).getRed();
    int greenY = picture.get(x, y + 1).getGreen() - picture.get(x, y - 1).getGreen();
    int blueY = picture.get(x, y + 1).getBlue() - picture.get(x, y - 1).getBlue();
    double yieldingY = redY * redY + greenY * greenY + blueY * blueY;

    return Math.sqrt(yieldingX + yieldingY);
  }

  public int[] findHorizontalSeam() {
    double[][] energies = new double[width()][height()];
    for (int y = 0; y < height(); y++) {
      for (int x = 0; x < width(); x++) {
        energies[x][y] = energy(x, y);
      }
    }
    return findSeam(energies);
  }

  public int[] findVerticalSeam() {
    double[][] energies = new double[height()][width()];
    for (int y = 0; y < height(); y++) {
      for (int x = 0; x < width(); x++) {
        energies[y][x] = energy(x, y);
      }
    }
    return findSeam(energies);
  }

  public void removeHorizontalSeam(int[] seam) {
    if (seam == null) {
      throw new IllegalArgumentException();
    }
    Picture resizedPicture = new Picture(width(), height() - 1);
    for (int x = 0; x < width(); x++) {
      int currentY = 0;
      for (int y = 0; y < height(); y++) {
        if (seam[x] == y) {
          continue;
        }
        resizedPicture.set(x, currentY++, picture.get(x, y));
      }
    }
    picture = resizedPicture;
  }

  public void removeVerticalSeam(int[] seam) {
    if (seam == null) {
      throw new IllegalArgumentException();
    }
    Picture resizedPicture = new Picture(width() - 1, height());
    for (int y = 0; y < height(); y++) {
      int currentX = 0;
      for (int x = 0; x < width(); x++) {
        if (seam[y] == x) {
          continue;
        }
        resizedPicture.set(currentX++, y, picture.get(x, y));
      }
    }
    picture = resizedPicture;
  }

  private boolean validate(int x, int y) {
    return x >= 0 || x < width() || y >= 0 || y < height();
  }

  private int getVertex(int x, int y, int width) {
    return width * y + x;
  }

  private int getXFromVertex(int vertex, int width) {
    return vertex % width;
  }

  private int[] findSeam(double[][] energies) {
    double[] distTo = new double[height() * width()];
    int[] edgeTo = new int[height() * width()];
    int width = energies[0].length;
    int height = energies.length;
    for (int y = 0; y < height; y++) {
      for (int x = 0; x < width; x++) {
        edgeTo[getVertex(x, y, width)] = -1;
      }
    }
    for (int y = 1; y < height; y++) {
      for (int x = 0; x < width; x++) {
        distTo[getVertex(x, y, width)] = Double.POSITIVE_INFINITY;
      }
    }
    for (int y = 0; y < height - 1; y++) {
      for (int x = 0; x < width; x++) {
        int vertex = getVertex(x, y, width);
        for (int k = x - 1; k <= x + 1; k++) {
          if (k >= 0 && k < width) {
            int currVertex = getVertex(k, y + 1, width);
            double currEnergy = energies[y + 1][k];
            if (distTo[currVertex] > distTo[vertex] + currEnergy) {
              distTo[currVertex] = distTo[vertex] + currEnergy;
              edgeTo[currVertex] = vertex;
            }
          }
        }
      }
    }
    int minVertex = Integer.MAX_VALUE;
    double minDistTo = Double.MAX_VALUE;
    for (int x = 0; x < width; x++) {
      int vertex = getVertex(x, height - 1, width);
      if (distTo[vertex] < minDistTo) {
        minVertex = vertex;
        minDistTo = distTo[vertex];
      }
    }
    Stack<Integer> stack = new Stack<>();
    for (int i = minVertex; i != -1; i = edgeTo[i]) {
      stack.add(i);
    }
    int[] seam = new int[stack.size()];
    int i = 0;
    while (!stack.isEmpty()) {
      seam[i++] = getXFromVertex(stack.pop(), width);
    }
    return seam;
  }
}
