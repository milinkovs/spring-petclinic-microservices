package org.springframework.samples.petclinic.customers.application;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.samples.petclinic.customers.model.Owner;
import org.springframework.samples.petclinic.customers.model.Pet;
import org.springframework.samples.petclinic.customers.model.PetType;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DuplicatePetRuleTest {

    private final DuplicatePetRule rule = new DuplicatePetRule();

    private Owner owner;
    private Pet leo;

    @BeforeEach
    void setUp() {
        owner = new Owner();
        ReflectionTestUtils.setField(owner, "id", 1);
        leo = pet(1, "Leo", LocalDate.of(2010, 9, 7), 1);
        owner.addPet(leo);
    }

    @Test
    void rejectsNewPetWithSameNameBirthDateAndType() {
        Pet duplicate = pet(null, "leo", LocalDate.of(2010, 9, 7), 1);
        owner.addPet(duplicate);

        assertThatThrownBy(() -> rule.check(owner, duplicate))
            .isInstanceOf(DuplicatePetException.class)
            .hasMessageContaining("Owner 1 already has this pet (id 1)");
    }

    @Test
    void allowsSameNameWithDifferentBirthDate() {
        Pet other = pet(null, "Leo", LocalDate.of(2020, 1, 1), 1);
        owner.addPet(other);

        assertThatCode(() -> rule.check(owner, other)).doesNotThrowAnyException();
    }

    @Test
    void allowsSameNameAndBirthDateWithDifferentType() {
        Pet other = pet(null, "Leo", LocalDate.of(2010, 9, 7), 2);
        owner.addPet(other);

        assertThatCode(() -> rule.check(owner, other)).doesNotThrowAnyException();
    }

    @Test
    void allowsUnchangedUpdateOfExistingPet() {
        assertThat(leo.getId()).isEqualTo(1);

        assertThatCode(() -> rule.check(owner, leo)).doesNotThrowAnyException();
    }

    @Test
    void ignoresPetWithoutBirthDate() {
        Pet other = pet(null, "Leo", null, 1);
        owner.addPet(other);

        assertThatCode(() -> rule.check(owner, other)).doesNotThrowAnyException();
    }

    private static Pet pet(Integer id, String name, LocalDate born, int typeId) {
        PetType type = new PetType();
        type.setId(typeId);
        type.setName(typeId == 1 ? "cat" : "dog");

        Pet pet = new Pet();
        pet.setId(id);
        pet.setName(name);
        pet.setBirthDate(born == null ? null : Date.from(born.atStartOfDay(ZoneId.systemDefault()).toInstant()));
        pet.setType(type);
        return pet;
    }
}
