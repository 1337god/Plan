/*
 *  This file is part of Player Analytics (Plan).
 *
 *  Plan is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License v3 as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Plan is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with Plan. If not, see <https://www.gnu.org/licenses/>.
 */
package com.djrapitops.plan.identification;

import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import utilities.OptionalAssert;

import java.util.UUID;

/**
 * Tests for {@link UUIDUtility}.
 *
 * @author Rsl1122
 */
@RunWith(JUnitPlatform.class)
class UUIDUtilityTest {

    @Test
    void stringUUIDIsParsed() {
        String test = "f3cc3e96-1bc9-35ad-994f-d894e9764b93";
        OptionalAssert.equals(UUID.fromString(test), UUIDUtility.parseFromString(test));
    }
}