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
 * Factory for CtVector objects.
 * 
 * @author Piotr Jasiowka
 */
public class CtVectorFactory {

  /** 
   * @param size Number of elements in a vector
   * @return New instance of an ArrayCtVector initialized with a specified
   *         size. All elements of new vector are initialized with 0.0 value
   * @throws IllegalArgumentException If <b>size</b> is < 0
   */
  public static CtVector createArrayCtVector(int size) throws IllegalArgumentException {
    return new ArrayCtVector(size);
  }

  /**
   * @param array Array to opaque
   * @return New instance of an ArrayCtVector initialized with a given array
   *         Note that new vector will be initialized with the certain given
   *         array, no its copy. ArrayCtVector will only opaque the array
   * @throws IllegalArgumentException If an <b>array</b> is not initialized
   */
  public static CtVector createArrayCtVector(double[] array) throws IllegalArgumentException {
    return new ArrayCtVector(array);
  }

}
