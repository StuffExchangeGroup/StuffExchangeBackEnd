package pbm.com.exchange.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pbm.com.exchange.domain.Category;
import pbm.com.exchange.repository.CategoryRepository;
import pbm.com.exchange.service.CategoryService;
import pbm.com.exchange.service.dto.CategoryDTO;
import pbm.com.exchange.service.mapper.CategoryMapper;

/**
 * Service Implementation for managing {@link Category}.
 */
@Service
@Transactional
public class CategoryServiceImpl implements CategoryService {

    private final Logger log = LoggerFactory.getLogger(CategoryServiceImpl.class);

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private CategoryMapper categoryMapper;

    @Override
    public CategoryDTO save(CategoryDTO categoryDTO) {
        log.debug("Request to save Category : {}", categoryDTO);
        Category category = categoryMapper.toEntity(categoryDTO);
        category = categoryRepository.save(category);
        return categoryMapper.toDto(category);
    }

    @Override
    public Optional<CategoryDTO> partialUpdate(CategoryDTO categoryDTO) {
        log.debug("Request to partially update Category : {}", categoryDTO);

        return categoryRepository
            .findById(categoryDTO.getId())
            .map(existingCategory -> {
                categoryMapper.partialUpdate(existingCategory, categoryDTO);

                return existingCategory;
            })
            .map(categoryRepository::save)
            .map(categoryMapper::toDto);
    }


    @Transactional(readOnly = true)
    @Override
	public List<CategoryDTO> findAll() {
    	log.debug("Request to get Category ");
    	List<CategoryDTO> categoryDTOs = new ArrayList<>();
		List<Category> categories = categoryRepository.findAll();
		for(Category category : categories) {
			CategoryDTO categoryDTO =categoryMapper.toDto(category) ;
			categoryDTOs.add(categoryDTO);
		}
		return categoryDTOs;
	}

    @Override
    @Transactional(readOnly = true)
    public Optional<CategoryDTO> findOne(Long id) {
        log.debug("Request to get Category : {}", id);
        return categoryRepository.findById(id).map(categoryMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete Category : {}", id);
        categoryRepository.deleteById(id);
    }

    @Override
    public List<CategoryDTO> findAllByActive() {
        log.debug("Request to get all categories");
        boolean activeCategory = true;
        return categoryMapper.toDto(categoryRepository.findByActive(activeCategory));
    }

	

	}
