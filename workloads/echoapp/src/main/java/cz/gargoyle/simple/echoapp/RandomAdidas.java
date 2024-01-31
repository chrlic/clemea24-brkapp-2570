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

public class RandomAdidas extends RandomAbstract {

    private double min, max;

    public RandomAdidas(double min, double max) {
        super();
        this.min = min;
        this.max = max;
    }

    public RandomAdidas() {
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

        // for demo - hardcoded by bucket ranges
        double yy;
        if (x <= 1313) {
            yy = 0;
        } else if ((x > 1313) && (x <= 3188)) {
            yy = 3;
        } else if ((x > 3188) && (x <= 4438)) {
            yy = 0;
        } else {
            yy = 0;
        }
        return yy;
    }
}