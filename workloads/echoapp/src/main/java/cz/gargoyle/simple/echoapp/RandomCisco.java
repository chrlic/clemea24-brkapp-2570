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

public class RandomCisco extends RandomAbstract {

    private double min, max;

    public RandomCisco(double min, double max) {
        super();
        this.min = min;
        this.max = max;
    }

    public RandomCisco() {
        this(0, 10000);
    }

    public double getDomainMin() {
        return this.min;
    }

    public double getDomainMax() {
        return this.max;
    }

    public double getRangeMax() {
        return 3;
    }

    public double PDF(double x) {

        double xx = (x - getDomainMin()) / (getDomainMax() - getDomainMin()) * 8;

        double yy;
        if (xx <= 2) {
            yy = Math.pow(2, xx) - 1;
        } else if ((xx > 2) && (xx <= 4)) {
            yy = Math.pow(2, 4 - xx) - 1;
        } else if ((xx > 4) && (xx <= 6)) {
            yy = Math.pow(2, xx - 4) - 1;
        } else {
            yy = Math.pow(2, 8 - xx) - 1;
        }
        return yy;
    }
}