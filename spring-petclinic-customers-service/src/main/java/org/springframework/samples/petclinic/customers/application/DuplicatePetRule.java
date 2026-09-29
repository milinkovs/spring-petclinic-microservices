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
import java.time.ZoneId;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.samples.petclinic.customers.model.Owner;
import org.springframework.samples.petclinic.customers.model.Pet;
import org.springframework.stereotype.Component;

@Component
public class DuplicatePetRule {

    private static final Logger log = LoggerFactory.getLogger(DuplicatePetRule.class);

    public void check(Owner owner, Pet pet) {
        if (owner == null || pet.getName() == null || pet.getBirthDate() == null || pet.getType() == null) {
            return;
        }
        LocalDate born = toLocalDate(pet.getBirthDate());
        for (Pet other : owner.getPets()) {
            boolean samePet = other == pet || (pet.getId() != null && pet.getId().equals(other.getId()));
            if (!samePet
                    && pet.getName().equalsIgnoreCase(other.getName())
                    && born.equals(toLocalDate(other.getBirthDate()))
                    && other.getType() != null && pet.getType().getId().equals(other.getType().getId())) {
                log.warn("Owner {} already has pet '{}' born {} of type {} (pet id {})",
                    owner.getId(), pet.getName(), born, pet.getType().getName(), other.getId());
                throw new DuplicatePetException("Owner " + owner.getId() + " already has this pet (id " + other.getId() + ")");
            }
        }
    }

    private static LocalDate toLocalDate(Date date) {
        return date == null ? null : Instant.ofEpochMilli(date.getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
    }
}
