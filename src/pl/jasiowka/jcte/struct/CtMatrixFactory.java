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

import java.io.IOException;

/**
 * Factory for CtMatrix objects.
 * 
 * @author Piotr Jasiowka
 */
public class CtMatrixFactory {

  /**
   * @param width Number of columns
   * @param height Number of rows
   * @return New instance of ArrayCtMatrix initialized with a specified
   *         dimension and with all elements zeroed
   * @throws IllegalArgumentException If <b>width</b> or <b>height</b> is < 0
   */
  public static CtMatrix createArrayCtMatrix(int width, int height) throws IllegalArgumentException {
    return new ArrayCtMatrix(width, height);
  }

  /**
   * @param filename Full path and file name and an extension
   *        (i.e. "/home/wallie/images/input.png")
   * @return New instance of ArrayCtMatrix initialized with a given image. The
   *         image will be converted to the grayscale color space and then it
   *         will be mapped into a matrix with pixels value convertion from int
   *         range [0..255] to double range [0..1]
   * @throws IllegalArgumentException If <b>filename</b> is null
   * @throws IOException If an error occurs during reading a file
   */
  public static CtMatrix createArrayCtMatrix(String filename) throws IllegalArgumentException, IOException {
    return new ArrayCtMatrix(filename);
  }

}
