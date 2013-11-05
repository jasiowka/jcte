/*
 * Copyright (C) 2013 by Piotr Jasiowka. All rights reserved.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 * 
 */
package pl.jasiowka.jcte.struct;

import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

/**
 * ArrayCtMatrix is an implementation of CtMatrix based on arrays.
 * 
 * @author Piotr Jasiowka
 * @see pl.jasiowka.jcte.struct.CtMatrixFactory
 */
class ArrayCtMatrix implements CtMatrix {

  protected CtVector[] matrix;

  protected int width, height;

  /**
   * Creates one-element, zero valued matrix. Outside this package use
   * {@link pl.jasiowka.jcte.struct.CtMatrixFactory} static methods for
   * creating matrices.
   */
  ArrayCtMatrix() {
    this(1, 1);
  }

  /**
   * Creates a zeroed matrix in a given dimension. Outside this package use
   * {@link pl.jasiowka.jcte.struct.CtMatrixFactory} static methods for
   * creating vectors.
   * 
   * @param width Number of columns
   * @param height Number of rows
   * @throws IllegalArgumentException If <b>width</b> or <b>height</b> is < 0
   */
  ArrayCtMatrix(int width, int height) throws IllegalArgumentException {
    this.width = width;
    this.height = height;
    matrix = new CtVector[height];
    for (int y = 0; y < height; y++)
      matrix[y] = CtVectorFactory.createArrayCtVector(width);
  }

  /**
   * Creates a matrix from a given image file. The image will be converted to
   * the grayscale color space and then it will be mapped into a matrix with
   * pixels value convertion from int range [0..255] to double range [0..1].
   * 
   * @param filename Full path and file name and an extension
   *        (i.e. "/home/wallie/images/input.png")
   * @throws IllegalArgumentException If <b>filename</b> is null
   * @throws IOException If an error occurs during reading a file
   */
  ArrayCtMatrix(String filename) throws IllegalArgumentException, IOException {
    BufferedImage im = ImageIO.read(new File(filename));
    ColorConvertOp op = new ColorConvertOp(ColorSpace.getInstance(ColorSpace.CS_GRAY), null);
    op.filter(im, im);
    width = im.getWidth();
    height = im.getHeight();
    matrix = new CtVector[height];
    for (int y = 0; y < height; y++) {
      double[] line = im.getData().getSamples(0, y, width, 1, 0, (double[])null);
      for (int x = 0; x < width; x++)
        line[x] = line[x] / 255;
      matrix[y] = CtVectorFactory.createArrayCtVector(line);
    }
  }

  @Override
  public int getWidth() {
    return width;
  }

  @Override
  public int getHeight() {
    return height;
  }

  @Override
  public void update(int x, int y, double val) throws IndexOutOfBoundsException {
      matrix[y].update(x, val);
  }

  @Override
  public double get(int x, int y) throws IndexOutOfBoundsException {
    return matrix[y].get(x);
  }

  @Override
  public void save(String filename) throws IllegalArgumentException, IOException {
    BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
    WritableRaster raster = bi.getRaster();
    for (int y = 0; y < height; y++) {
      for (int x = 0; x < width; x++) {
        double weight = 255 / max();
        raster.setSample(x, y, 0, Math.abs(matrix[y].get(x)) * weight);
      }
    }
    ImageIO.write(bi, "png", new File(filename));
  }

  @Override
  public CtVector getVector(int y) throws IndexOutOfBoundsException {
    return matrix[y];
  }

  @Override
  public void sum(CtMatrix matrix) throws IllegalArgumentException {
    if (matrix == null || matrix.getWidth() != width || matrix.getHeight() != height)
      throw new IllegalArgumentException();
    for (int y = 0; y < height; y++)
      this.matrix[y].sum(matrix.getVector(y));
  }

  @Override
  public double max() {
    double m = matrix[0].get(0);
    for (int y = 0; y < height; y++) {
      double lm = matrix[y].max();
      if (lm > m) m = lm;
    }
    return m;
  }

  @Override
  public CtVector sumCols() {
    CtVector line = CtVectorFactory.createArrayCtVector(width);
    for (int y = 0; y < height; y++)
      line.sum(matrix[y]);
    return line;
  }

  @Override
  public void paste(int x, int y, CtMatrix matrix) throws IndexOutOfBoundsException, IllegalArgumentException {
    if (matrix == null) throw new IllegalArgumentException();
    int pasteWidth = matrix.getWidth();
    int pasteHeight = matrix.getHeight();
    if (pasteWidth > width - x) pasteWidth = width - x;
    if (pasteHeight > height - y) pasteHeight = height - y;   
    for (int yy = 0; yy < pasteHeight; yy++)
      this.matrix[yy + y].paste(x, matrix.getVector(yy));
  }

  @Override
  public void pasteVector(int y, CtVector vector) throws IndexOutOfBoundsException, IllegalArgumentException {
    if (vector == null) throw new IllegalArgumentException();
    if (vector.getSize() != width) throw new IllegalArgumentException();
    matrix[y].paste(0, vector);
  }

//  @Override
//  public String toString() {
//    StringBuilder sb = new StringBuilder();
//    for (int y = 0; y < height; y++)
//       sb.append(image[y].toString() + "\n");
//    String out = sb.toString();
//    return out;
//  }

}
