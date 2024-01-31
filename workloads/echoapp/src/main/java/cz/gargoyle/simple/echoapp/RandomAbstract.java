/* 
 *  Copyright (c) 2024 Cisco Systems, Inc.
 *  
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *  
 *     http://www.apache.org/licenses/LICENSE-2.0
 *  
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *  
 */

package cz.gargoyle.simple.echoapp;

import java.util.Random;

public abstract class RandomAbstract {
    public abstract double PDF(double value);
    public abstract double getDomainMin();
    public abstract double getDomainMax();
    public abstract double getRangeMax();

    protected Random rnd;

    public RandomAbstract() {
        rnd = new Random();
    }

    public double nextRandom() {
        double left = getDomainMin();
        double right = getDomainMax();
        double top = getRangeMax();

        double x, y1, y2;

        while (true) {
            x = left + (right - left) * rnd.nextDouble();
            y1 = top * rnd.nextDouble();
            y2 = PDF(x);
            if (y1 < y2) return x;
        }
    }
}