package pbm.com.exchange.specifications;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import pbm.com.exchange.domain.City;
import pbm.com.exchange.domain.Product;
import pbm.com.exchange.repository.CityRepository;
import pbm.com.exchange.service.impl.CityServiceImpl;
import pbm.com.exchange.specifications.model.Filter;

public class ProductSpecification{
    public CityRepository cityRepository;
    
    public ProductSpecification(CityRepository cityRepository) {
        this.cityRepository = cityRepository;
    }
    
    public ProductSpecification() {
    }

    private Specification<Product> createSpecification(Filter input) {
        switch (input.getOperator()) {
            case EQUALS:
                return (root, query, criteriaBuilder) ->
                    criteriaBuilder.equal(
                        root.get(input.getField()),
                        castToRequiredType(root.get(input.getField()).getJavaType(), input.getValue())
                    );
            case NOT_EQUALS:
                return (root, query, criteriaBuilder) ->
                    criteriaBuilder.notEqual(
                        root.get(input.getField()),
                        castToRequiredType(root.get(input.getField()).getJavaType(), input.getValue())
                    );
            case LIKE:
                return (root, query, criteriaBuilder) -> criteriaBuilder.like(root.get(input.getField()), "%" + input.getValue() + "%");
            case IN:
                return (root, query, criteriaBuilder) ->
                    criteriaBuilder
                        .in(root.get(input.getField()))
                        .value(castToRequiredType(root.get(input.getField()).getJavaType(), input.getValues()));
            case NOT_IN:
                return (root, query, criteriaBuilder) ->
                    criteriaBuilder
                        .in(root.get(input.getField()))
                        .value(castToRequiredType(root.get(input.getField()).getJavaType(), input.getValues()))
                        .not();
            default:
                throw new RuntimeException("Operation not supported yet");
        }
    }

    private Object castToRequiredType(Class fieldType, String value) {
        if (fieldType.isAssignableFrom(Double.class)) {
            return Double.valueOf(value);
        } else if (fieldType.isAssignableFrom(Integer.class)) {
            return Integer.valueOf(value);
        } else if (fieldType.isAssignableFrom(Long.class)) {
            return Long.valueOf(value);
        } else if (Enum.class.isAssignableFrom(fieldType)) {
            return Enum.valueOf(fieldType, value);
        } else if (fieldType.isAssignableFrom(Boolean.class)) {
            return Boolean.valueOf(value);
        } else if (fieldType.isAssignableFrom(City.class)) {
            return cityRepository.findById(Long.parseLong(value)).get();
        }
        return null;
    }

    private Object castToRequiredType(Class fieldType, List<String> value) {
        List<Object> lists = new ArrayList<>();
        for (String s : value) {
            lists.add(castToRequiredType(fieldType, s));
        }
        return lists;
    }

    // get 'and' specification
    public Specification<Product> getSpecificationFromFilters(List<Filter> filter) {
        Specification<Product> specification = createSpecification(filter.remove(0));
        for (Filter input : filter) {
            specification = specification.and(createSpecification(input));
        }
        return specification;
    }

    //get 'or' specification
    public Specification<Product> getOrSpecificationFromFilters(List<Filter> filters) {
        if (filters.isEmpty()) {
            return null;
        }
        Specification<Product> specification = createSpecification(filters.remove(0));
        for (Filter input : filters) {
            specification = specification.or(createSpecification(input));
        }
        return specification;
    }
}
