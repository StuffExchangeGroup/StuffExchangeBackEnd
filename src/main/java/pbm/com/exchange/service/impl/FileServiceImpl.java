package pbm.com.exchange.service.impl;

import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import pbm.com.exchange.config.ApplicationProperties;
import pbm.com.exchange.domain.File;
import pbm.com.exchange.repository.FileRepository;
import pbm.com.exchange.service.FileService;
import pbm.com.exchange.service.dto.FileDTO;
import pbm.com.exchange.service.mapper.FileMapper;
import pbm.com.exchange.service.utils.FileUtil;

/**
 * Service Implementation for managing {@link File}.
 */
@Service
@Transactional
public class FileServiceImpl implements FileService {

    private final Logger log = LoggerFactory.getLogger(FileServiceImpl.class);

    @Autowired
    private FileRepository fileRepository;

    @Autowired
    private FileMapper fileMapper;

    private ApplicationProperties applicationProperties;

    public FileServiceImpl(ApplicationProperties applicationProperties) {
        this.applicationProperties = applicationProperties;
        FileUtil.getInstance().setUploadFolder(this.applicationProperties.getUploadFolder());
    }

    @Override
    public FileDTO save(FileDTO fileDTO) {
        log.debug("Request to save File : {}", fileDTO);
        File file = fileMapper.toEntity(fileDTO);
        file = fileRepository.save(file);
        return fileMapper.toDto(file);
    }

    @Override
    public Optional<FileDTO> partialUpdate(FileDTO fileDTO) {
        log.debug("Request to partially update File : {}", fileDTO);

        return fileRepository
            .findById(fileDTO.getId())
            .map(existingFile -> {
                fileMapper.partialUpdate(existingFile, fileDTO);

                return existingFile;
            })
            .map(fileRepository::save)
            .map(fileMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<FileDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Files");
        return fileRepository.findAll(pageable).map(fileMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<FileDTO> findOne(Long id) {
        log.debug("Request to get File : {}", id);
        return fileRepository.findById(id).map(fileMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete File : {}", id);
        fileRepository.deleteById(id);
    }

    @Override
    public FileDTO uploadFile(MultipartFile multipartFile) {
        log.debug("Service request to upload file");
        FileDTO fileDTO = null;
        if (multipartFile != null) {
            fileDTO = FileUtil.getInstance().createFile(multipartFile);
            if (fileDTO != null) {
                FileUtil.getInstance().storeFile(fileDTO, multipartFile);
                fileDTO = save(fileDTO);
            }
        }
        return fileDTO;
    }
}
