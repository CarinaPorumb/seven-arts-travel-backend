package ro.sevenartstravel.service.util;

import jakarta.persistence.criteria.Path;
import org.springframework.data.jpa.domain.Specification;

import java.util.Collection;

public class SpecificationUtils {

    public static <T> Specification<T> likeIfPresent(String attributeName, String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        String trimValue = value.trim().toLowerCase();
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.like(criteriaBuilder.lower(root.get(attributeName)), "%" + trimValue + "%");
    }

    public static <T> Specification<T> equalsIfPresent(String attributeName, Object value) {
        if (value == null) {
            return null;
        }
        return (root, query, criteriaBuilder)
                -> criteriaBuilder.equal(root.get(attributeName), value);
    }

    public static <T> Specification<T> equalsIgnoreCaseIfPresent(String attributeName, String value) {
        if (value == null || value.isBlank()) return null;
        String trimValue = value.trim().toLowerCase();
        return (root, query, cb) -> cb.equal(cb.lower(root.get(attributeName)), trimValue);
    }

    public static <T> Specification<T> inIfPresent(String attributeName, Collection<?> values) {
        if (values == null || values.isEmpty()) {
            return null;
        }
        return ((root, query, criteriaBuilder) ->
                root.get(attributeName).in(values));
    }

    public static <T, V extends Comparable<? super V>> Specification<T> betweenIfPresent(String attributeName, V start, V end) {
        if (start == null && end == null) {
            return null;
        }

        return (root, query, criteriaBuilder) -> {
            Path<V> path = root.get(attributeName);

            if (start != null && end != null) {
                return criteriaBuilder.between(path, start, end);
            }
            if (start != null) {
                return criteriaBuilder.greaterThanOrEqualTo(path, start);
            }

            return criteriaBuilder.lessThanOrEqualTo(path, end);
        };
    }

    public static <T> Specification<T> isNull(String attributeName) {
        return (root, query, criteriaBuilder)
                -> criteriaBuilder.isNull(root.get(attributeName));
    }

    public static <T> Specification<T> andSpec(Specification<T> base, Specification<T> add) {
        if (add == null) return base;
        return (base == null) ? Specification.where(add) : base.and(add);
    }

    public static <T> Specification<T> orSpec(Specification<T> base, Specification<T> add) {
        if (add == null) return base;
        return (base == null) ? Specification.where(add) : base.or(add);
    }

}