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

/**
 * CtVector represents a single row of the CtMatrix, but also can be used
 * separately. It is a sequence of double type elements with some methods to do
 * on it.
 * 
 * @author Piotr Jasiowka
 * @see pl.jasiowka.jcte.struct.ArrayCtVector
 * @see pl.jasiowka.jcte.struct.CtVectorFactory
 */
public interface CtVector {

  /** @return Number of elements in vector */
  int getSize();

  /**
   * Updates a single position of a vector with a specified value.
   * @param x Position in a vector
   * @param val New value
   * @throws IndexOutOfBoundsException If <b>x</b> is out of bounds
   */
  void update(int x, double val) throws IllegalArgumentException;

  /**
   * @param x Position in a vector
   * @return Value assigned to the <b>x</b> position
   * @throws IndexOutOfBoundsException If <b>x</b> is out of bounds
   */
  double get(int x) throws IndexOutOfBoundsException;

  /**
   * @param filter Filter vector
   * @return A convolution with a given filter. Note that the result vector has
   *         size of [<code>this.size + filter.size - 1</code>]. This method
   *         doesn't affect itself but returns a result vector
   * @throws IllegalArgumentException If <b>vector</b> is null
   */
  CtVector conv(CtVector filter);

  /**
   * Performs an arithmetic sum of corresponding elements in both vectors. Note
   * that both of vectors have to be the same size.
   * 
   * @param vector Vector to sum with
   * @throws IllegalArgumentException If <b>vector</b> is null or is in
   *         a different size
   */
  void sum(CtVector vector) throws IllegalArgumentException;

  /** @return The largest value in vector */
  double max();

  /**
   * Pastes one vector into another starting from a given position.
   * 
   * @param x Start position for pasting
   * @param vector A vector to paste
   * @throws IndexOutOfBoundsException If <b>x</b> is out of bounds
   * @throws IllegalArgumentException If <b>vector</b> is null
   */
  void paste(int x, CtVector vector) throws IndexOutOfBoundsException, IllegalArgumentException;

}
