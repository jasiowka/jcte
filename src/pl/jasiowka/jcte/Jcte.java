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
package pl.jasiowka.jcte;

import java.io.IOException;
import java.text.DecimalFormat;

import pl.jasiowka.jcte.struct.CtMatrix;
import pl.jasiowka.jcte.struct.CtMatrixFactory;
import pl.jasiowka.jcte.struct.CtVector;
import pl.jasiowka.jcte.struct.CtVectorFactory;

/**
 * Jcte (Java Computed Tomography Example) is a simple class that shares
 * some methods for manipulating CT images. Them allow you reconstruct CT
 * slices from given projections using back propagation and Radon
 * Transformation.
 * 
 * @author Piotr Jasiowka
 */
public class Jcte {

  /**
   * @param t Angle in radians
   * @return Sinc(t) function value
   */
  private double sinc(double t) { 
    if (t == 0)
      return 1.0;
    else {
      double b = Math.PI * t;
      return Math.sin(b) / b;
    }
  }

  /** 
   * @param matrix Matrix to extend
   * @return Extended input matrix so it can seat all its content when
   *         rotating. The output matrix is zeroed and the input one is placed
   *         in it centrally
   */
  @SuppressWarnings("unused")
  private CtMatrix extend(CtMatrix matrix) {
    int diagonal = (int) Math.ceil(Math.sqrt(matrix.getWidth() * matrix.getWidth() + matrix.getHeight() * matrix.getHeight()));
    int w = diagonal;
    int h = diagonal;
    if (matrix.getWidth()%2 != w%2) w++;
    if (matrix.getHeight()%2 != h%2) h++;
    int startx = (w - matrix.getWidth()) / 2;
    int starty = (h - matrix.getHeight()) / 2;
    CtMatrix nfl = CtMatrixFactory.createArrayCtMatrix(w, h);
    nfl.paste(startx, starty, matrix);
    return nfl;
  }

  /**
   * @param matrix Matrix to be rotated
   * @param angle Rotation angle in radians
   * @return Rotated matrix. By default it is right-hand-rule rotation but it
   *         can be changed by giving the angle value with a minus
   */
  public CtMatrix rotate(CtMatrix matrix, double angle) {
    if (matrix == null) throw new IllegalArgumentException();
    CtMatrix out = CtMatrixFactory.createArrayCtMatrix(matrix.getWidth(), matrix.getHeight());
    int inXCenter = matrix.getWidth() / 2;
    int inYCenter = matrix.getHeight() / 2;
    double alpha = (-angle) * Math.PI / 180;
    double alphaSin = Math.sin(alpha);
    double alphaCos = Math.cos(alpha);
    for (int y = 0; y < matrix.getHeight(); y++) {
      int elY = 2 * (y - inYCenter) + 1;
      for (int x = 0; x < matrix.getWidth(); x++) {
        int elX = 2 * (x - inXCenter) + 1;
        int rotX = (int) Math.round(elX * alphaCos - elY * alphaSin);
        int rotY = (int) Math.round(elX * alphaSin + elY * alphaCos);
        int orgX = (rotX - 1) / 2 + inXCenter;
        int orgY = (rotY - 1) / 2 + inYCenter;
        if (orgX >= 0 && orgX < matrix.getWidth() && orgY >= 0 && orgY < matrix.getHeight())
          out.update(x, y, matrix.get(orgX, orgY));
        else
          out.update(x, y, 0);
      }
    }
    return out;
  }

  /**
   * 
   * @param matrix Matrix with a phantom image (should be extended before)
   * @param range Range of projections to do (a number from range [0..360])
   * @param projections Number of projections within a given range. Normally it
   *        should equals to the range value
   * @return Sinogram made from projections obtained from a phantom by Radon
   *         Transformation
   */
  public CtMatrix makeSinogram(CtMatrix matrix, int range, int projections) {
    if (matrix == null) throw new IllegalArgumentException();
    if (range < 1 || range > 360) throw new IllegalArgumentException();
    if (projections < 0) throw new IllegalArgumentException();
    CtMatrix sinogram = CtMatrixFactory.createArrayCtMatrix(matrix.getWidth(), projections);
    double step = range / projections;
    double angle = 0;
    for (int i = 0; i < projections; i++) {
      sinogram.pasteVector(i, rotate(matrix, angle).sumCols());
      angle += step;
    }
    return sinogram;
  }

  /**
   * @param size Size of a vector for which an answer will be calculated
   * @return Calculates an filter's answer for a vector
   */
  private CtVector computeFiler(int size) {
	int outSize = (2 * size) + 1;
	CtVector out = CtVectorFactory.createArrayCtVector(outSize);
    for (int i = -size; i <= size; i++)
      out.update(i + size, i);
    for (int i = 0; i <= outSize - 1; i++) {
      double sinca = sinc(out.get(i));
      double sincb = sinc(out.get(i) / 2);
      double sincc = sincb * sincb;
      out.update(i, (sinca / 2) - (sincc / 4));
    }
    return out;
  }

  /**
   * Makes a convolution of filter's answer vector with every row of a given
   * matrix.
   * 
   * @param matrix Matrix to be filtered (in CT it is normally a sinogram 
   *        matrix)
   */
  private void applyFilter(CtMatrix matrix) {
    CtVector filter = computeFiler(matrix.getWidth());
    for (int y = 0; y < matrix.getHeight(); y++) {
      CtVector tmp = matrix.getVector(y).conv(filter);
      int start = (tmp.getSize() - matrix.getWidth()) / 2;
      int end = tmp.getSize() - start - 1;
      for (int i = start; i <= end; i++) {
        matrix.update(i - start, y, tmp.get(i));}
    }
  }

  /**
   * @param matrix Sinogram matrix
   * @param range Number of projections
   * @return Reconstructed CT slice using a back propagation method
   */
  public CtMatrix reconstruct(CtMatrix matrix, int range) {
    CtMatrix out = CtMatrixFactory.createArrayCtMatrix(matrix.getWidth(), matrix.getWidth());
    CtMatrix smoothedFilter = CtMatrixFactory.createArrayCtMatrix(matrix.getWidth(), matrix.getWidth());
    if (range >= 0 && range < 361) {
    	double step = range / matrix.getHeight();
        double angle = 0;
        for (int y = 0; y < matrix.getHeight(); y++) {
          for (int z = 0; z < matrix.getWidth(); z++)
            smoothedFilter.pasteVector(z, matrix.getVector(y));
          out.sum(rotate(smoothedFilter, -angle));
          angle += step;
        }
    }
    return out;
  }

  /**
   * @param slice Number of a slice
   * @return Sinogram made from projections
   */
  public CtMatrix makeSinogram(int slice) {
    DecimalFormat df = new DecimalFormat("000");
    CtMatrix sin = CtMatrixFactory.createArrayCtMatrix(256, 180);
    for (int p = 0; p < 180; p++) {
      String name = "./data/" + df.format(p) + ".png";
      CtMatrix mx;
      try {
        mx = CtMatrixFactory.createArrayCtMatrix(name);
        sin.pasteVector(p, mx.getVector(slice));
      } catch (IllegalArgumentException e) {
        e.printStackTrace();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    return sin;
  }

  public void test() throws IllegalArgumentException, IOException {
    String path = "./output/";
    int sliceNumber = 137;

    System.out.println("Processing slice No " + sliceNumber + ":");

    System.out.println("--> Building sinogram..");
    CtMatrix sinogram = makeSinogram(sliceNumber);

    System.out.println("--> Saving sinogram..");
    sinogram.save(path + sliceNumber + "sin.png");

    System.out.println("--> Reconstructing a slice from non filtered sinogram..");
    CtMatrix slice = reconstruct(sinogram, 180);

    System.out.println("--> Saving a non filtered slice..");
    slice.save(path + sliceNumber + "slice.png");

    System.out.println("--> Filtering sinogram..");
    applyFilter(sinogram);

    System.out.println("--> Saving filtered sinogram..");
    sinogram.save(path + sliceNumber + "sinf.png");

    System.out.println("--> Reconstructing a slice from filtered sinogram..");
    CtMatrix slicef = reconstruct(sinogram, 180);

    System.out.println("--> Saving a filtered slice..");
    slicef.save(path + sliceNumber + "slicef.png");

    System.out.println("Done!");
  }

}
