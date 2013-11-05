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

import pl.jasiowka.jcte.struct.CtVectorFactory;

/**
 * Simple implementation of CtVector based on arrays.
 * 
 * @author Piotr Jasiowka
 * @see pl.jasiowka.jcte.struct.CtVectorFactory
 */
class ArrayCtVector implements CtVector {

  private double[] vector;

  private int size;

  /**
   * Creates one-element, zero valued vector. Outside this package use
   * {@link pl.jasiowka.jcte.struct.CtVectorFactory} static methods for
   * creating vectors.
   */
  ArrayCtVector() {
	  this(1);
  }

  /**
   * Creates a zeroed vector in a given size. Outside this package use
   * {@link pl.jasiowka.jcte.struct.CtVectorFactory} static methods for
   * creating vectors.
   * 
   * @param size Number of elements in a vector
   * @throws IllegalArgumentException If size is < 0
   */
  ArrayCtVector(int size) throws IllegalArgumentException {
    if (size < 1) throw new IllegalArgumentException();
    this.size = size;
    vector = new double[size];
  }

  /**
   * Creates a vector from a given array. Note that no copy of a given array
   * will be performed. New vector will be initialized with the certain given
   * array. ArrayCtVector will only opaque the array.
   * 
   * @param array Array for new vector
   * @throws IllegalArgumentException If a given array is not initialized
   */
  ArrayCtVector(double[] array) throws IllegalArgumentException {
    if (array == null || array.length == 0) throw new IllegalArgumentException();
    size = array.length;
    vector = array;
  }

  @Override
  public int getSize() {
    return size;
  }

  @Override
  public void update(int x, double val) throws IndexOutOfBoundsException {
    vector[x] = val;
  }

  @Override
  public double get(int x) throws IndexOutOfBoundsException {
    return vector[x];
  }

  @Override
  public CtVector conv(CtVector filter) throws IllegalArgumentException {
    if (filter == null) throw new IllegalArgumentException();
    CtVector result = CtVectorFactory.createArrayCtVector(size + filter.getSize() - 1);
    for (int hostPos = size - 1; hostPos >= 0; hostPos--)
      for (int filterPos = filter.getSize() - 1; filterPos >= 0; filterPos--) {
        double product = get(hostPos) * filter.get(filterPos);
        result.update(hostPos + filterPos, result.get(hostPos + filterPos) + product);
      }
    return result;
  }

  @Override
  public void sum(CtVector vector) throws IllegalArgumentException {
    if (vector == null || vector.getSize() != size) throw new IllegalArgumentException();
    for (int p = 0; p < size; p++)
      this.vector[p] = this.vector[p] + vector.get(p);
  }

  @Override
  public double max() {
    double m = vector[0];
    for (int x = 1; x < size; x++)
      if (vector[x] > m) m = vector[x];
    return m;
  }

  @Override
  public void paste(int x, CtVector vector) throws IndexOutOfBoundsException, IllegalArgumentException {
    if (vector == null) throw new IllegalArgumentException();
    int pasteWidth = vector.getSize();
    if (pasteWidth > size - x) pasteWidth = size - x;
    for (int xx = 0; xx < pasteWidth; xx++)
      this.vector[xx + x] = vector.get(xx);
  }

//  @Override
//  public String toString() {
//    java.text.DecimalFormat df = new java.text.DecimalFormat("000.00");
//    StringBuilder sb = new StringBuilder();
//    for (int x = 0; x < size; x++)
//      sb.append(df.format(line[x]) + "  ");
//    String out = sb.toString();
//    System.out.println(out);
//    return out;
//  }

}
