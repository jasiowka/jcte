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
 * CtMatrix represents the base structure for all mathematical calculations
 * in computed tomography. Inside it is a sequence of CtVector objects in the
 * same size, so it holds a rectangular, two-dimensional space which has
 * a number of rows indicating a <b>height</b> property and analogously a
 * number of columns indicating the <b>width</b> property. That means every
 * element is addressed by a pair (x, y) and is a double type. This class has
 * also some methods for matrix manipulations and finally has a <b>save()</b>
 * method for mapping content of the matrix into the image object to store it
 * on a local disk. 
 * 
 * @author Piotr Jasiowka
 * @see pl.jasiowka.jcte.struct.ArrayCtMatrix
 * @see pl.jasiowka.jcte.struct.CtMatrixFactory
 */
public interface CtMatrix {

  /** @return Width of the matrix (number of columns) */
  int getWidth();

  /** @return Height of the matrix (number of rows) */
  int getHeight();

  /**
   * Updates an element at (x, y) position with a given value.
   * 
   * @param x X coordinate (column)
   * @param y Y coordinate (row)
   * @param val New value
   * @throws IndexOutOfBoundsException If <b>x</b> or <b>y</b> is out of
   *         bounds
   */
  void update(int x, int y, double val) throws IndexOutOfBoundsException;

  /**
   * @param x X coordinate (column)
   * @param y Y coordinate (row)
   * @return An value at (x, y) position
   * @throws IndexOutOfBoundsException If <b>x</b> or <b>y</b> is out of
   *         bounds
   */
  double get(int x, int y) throws IndexOutOfBoundsException;

  /**
   * Saves the content of the matrix in a specified file.
   * 
   * @param filename Full path with a file name and extension
   *        (i.e. "/home/wallie/images/output.png")
   * @throws IllegalArgumentException If <b>filename</b> is null
   * @throws IOException If an error occurs during writing a file
   */
  void save(String filename) throws IllegalArgumentException, IOException;

  /**
   * @param y Y coordinate (row)
   * @return Vector from the matrix at <b>y</b> row. Note it returns the
   *         reference, not copy
   * @throws IndexOutOfBoundsException If <b>y</b> if out of bounds
   */
  CtVector getVector(int y) throws IndexOutOfBoundsException;

  /**
   * Performs an arithmetical sum of all corresponding elements in both
   * matrices. Note the matrices have to be the same size.
   * 
   * @param matrix Matrix to sum with
   * @throws IllegalArgumentException If <b>matrix</b> is null or is in
   *         a different size
   */
  void sum(CtMatrix matrix) throws IllegalArgumentException;

  /**
   * @return Vector where single element is a sum of all elements in
   * corresponding matrix column, so its size equals the matrix width
   */
  CtVector sumCols();

  /** @return The largest element in matrix */
  double max();

  /**
   * Pastes one matrix into another starting at the specified position.
   * 
   * @param x X coordinate (column)
   * @param y Y coordinate (row)
   * @param Matrix to paste
   * @throws IndexOutOfBoundsException If <b>x</b> or <b>y</b> is out of
   *         bounds
   * @throws IllegalArgumentException If <b>matrix</b> is null
   */
  void paste(int x, int y, CtMatrix matrix) throws IndexOutOfBoundsException, IllegalArgumentException;

  /**
   * Pastes a given vector at specified row number.
   * 
   * @param y Y coordinate (row)
   * @param vector Vector to paste
   * @throws IndexOutOfBoundsException If <b>y</b> is out of bounds
   * @throws IllegalArgumentException If <b>matrix</b> is null
   */
  void pasteVector(int y, CtVector vector) throws IndexOutOfBoundsException, IllegalArgumentException;

}
