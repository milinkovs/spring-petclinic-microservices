/*
 * Copyright 2002-2021 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.samples.petclinic.customers.application;

import java.time.Instant;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.samples.petclinic.customers.model.Pet;
import org.springframework.stereotype.Component;

@Component
public class PetLifeStage {

    private static final Logger log = LoggerFactory.getLogger(PetLifeStage.class);

    private static final String[] STAGES = {"baby", "young", "adult", "senior"};
    private static final int YEARS_PER_STAGE = 5;

    public String resolve(Pet pet) {
        Date birthDate = pet.getBirthDate();
        if (birthDate == null) {
            return "unknown";
        }
        LocalDate born = Instant.ofEpochMilli(birthDate.getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
        int years = Period.between(born, LocalDate.now()).getYears();
        try {
            return stageFor(years);
        } catch (ArrayIndexOutOfBoundsException e) {
            log.error("Failed to resolve life stage for pet {} (born {}, {} years old)", pet.getName(), born, years, e);
            throw e;
        }
    }

    private String stageFor(int years) {
        return STAGES[years / YEARS_PER_STAGE];
    }
}
