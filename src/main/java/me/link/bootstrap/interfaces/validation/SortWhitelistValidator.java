package me.link.bootstrap.interfaces.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import me.link.bootstrap.interfaces.dto.request.SortablePageRequest;
import me.link.bootstrap.shared.kernel.util.SortableFieldUtils;
import me.link.bootstrap.shared.kernel.valueobject.SortingField;

import java.util.List;
import java.util.Set;

import static me.link.bootstrap.shared.kernel.util.SortableFieldUtils.parseSortableFields;

public class SortWhitelistValidator implements ConstraintValidator<SortWhitelist, SortablePageRequest> {

    private Set<String> allowedFields;

    @Override
    public void initialize(SortWhitelist constraintAnnotation) {
        this.allowedFields = (Set<String>) parseSortableFields(constraintAnnotation.value());
    }

    @Override
    public boolean isValid(SortablePageRequest value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        List<SortingField> sortingFields = value.getSortingFields();
        if (sortingFields.isEmpty()) {
            return true;
        }

        return sortingFields.stream()
                .map(SortingField::getField)
                .allMatch(allowedFields::contains);
    }

}
