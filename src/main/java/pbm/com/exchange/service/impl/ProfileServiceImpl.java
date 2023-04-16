package pbm.com.exchange.service.impl;

import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import liquibase.pro.packaged.iF;
import pbm.com.exchange.app.rest.respone.GetProfileUidRes;
import pbm.com.exchange.app.rest.vm.PutProfileDTO;
import pbm.com.exchange.domain.City;
import pbm.com.exchange.domain.Profile;
import pbm.com.exchange.domain.User;
import pbm.com.exchange.domain.enumeration.ReferralType;
import pbm.com.exchange.exception.BadRequestException;
import pbm.com.exchange.message.Message;
import pbm.com.exchange.message.MessageHelper;
import pbm.com.exchange.repository.CityRepository;
import pbm.com.exchange.repository.ProfileRepository;
import pbm.com.exchange.repository.UserRepository;
import pbm.com.exchange.service.ProfileService;
import pbm.com.exchange.service.UserService;
import pbm.com.exchange.service.dto.FileDTO;
import pbm.com.exchange.service.dto.ProfileDTO;
import pbm.com.exchange.service.mapper.ProfileMapper;
import pbm.com.exchange.service.mapper.UserMapper;

/**
 * Service Implementation for managing {@link Profile}.
 */
@Service
@Transactional
public class ProfileServiceImpl implements ProfileService {

    private static final String PREFIX_CODE = "STFFEX";
    private final Logger log = LoggerFactory.getLogger(ProfileServiceImpl.class);

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private CityRepository cityRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private ProfileMapper profileMapper;

    @Autowired
    private UserMapper userMapper;

    @Override
    public ProfileDTO save(ProfileDTO profileDTO) {
        log.debug("Request to save Profile : {}", profileDTO);
        Profile profile = profileMapper.toEntity(profileDTO);
        profile = profileRepository.save(profile);
        return profileMapper.toDto(profile);
    }

    @Override
    public Optional<ProfileDTO> partialUpdate(ProfileDTO profileDTO) {
        log.debug("Request to partially update Profile : {}", profileDTO);

        return profileRepository
            .findById(profileDTO.getId())
            .map(existingProfile -> {
                profileMapper.partialUpdate(existingProfile, profileDTO);

                return existingProfile;
            })
            .map(profileRepository::save)
            .map(profileMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProfileDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Profiles");
        return profileRepository.findAll(pageable).map(profileMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ProfileDTO> findOne(Long id) {
        log.debug("Request to get Profile : {}", id);
        return profileRepository.findById(id).map(profileMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete Profile : {}", id);
        profileRepository.deleteById(id);
    }

    @Override
    public PutProfileDTO partialUpdate(PutProfileDTO profileDTO) {
        log.debug("Request to partially update Profile : {}", profileDTO);
        
        if(profileDTO.getPhone() != null) {
            if(profileRepository.existsByPhone(profileDTO.getPhone())) {
                throw new BadRequestException(MessageHelper.getMessage(Message.Keys.E0013), new Throwable());
            }
        }
        
        User user = userService.getCurrentUser().get();
        Profile profile = profileRepository.findOneByUser(user).get();

        boolean isUpdatedAvatar = (profileDTO.getAvatar() != null) && (!profileDTO.getAvatar().equals(profile.getAvatar()));
        boolean isUpdatedEmail = (profileDTO.getEmail() != null) && (!profileDTO.getEmail().equals(user.getEmail()));
        boolean isUpdatedPhone = (profileDTO.getPhone() != null) && (!profileDTO.getPhone().equals(profile.getPhone()));
      
        userMapper.partialUpdate(user, profileDTO);
        profileMapper.partialUpdate(profile, profileDTO);
        if (profileDTO.getCityId() != null) {
            City city = cityRepository.findById(profileDTO.getCityId()).get();
            profile.setCity(city);
        }
        
        // update display name
        if(profileDTO.getFirstName() != null || profileDTO.getLastName() != null) {
            profile.setDisplayName(user.getFirstName() + " " + user.getLastName());
        }
        
        user = userRepository.save(user);
        profile = profileRepository.save(profile);

        //update user information on firebase
        if (isUpdatedAvatar) {
            userService.updateUserAvatar(profile);
        }
        if (isUpdatedEmail) {
            userService.updateUserEmail(profile, user);
        }
        if (isUpdatedPhone) {
            userService.updateUserPhone(profile, user);
        }

        //partial update for response profile dto
        profileMapper.partialUpdate(profileDTO, profile);
        profileMapper.partialUpdate(profileDTO, user);

        if (profile.getCity() != null) {
            profileDTO.setCityId(profile.getCity().getId());
        }

        return profileDTO;
    }

    @Override
    public GetProfileUidRes getProfileByUid(String uid) {
        Optional<Profile> profile = profileRepository.findOneByUid(uid);

        if (!profile.isPresent()) {
            throw new BadRequestException(MessageHelper.getMessage(Message.Keys.E0090, uid), new Throwable());
        }

        GetProfileUidRes getProfileUidRes = GetProfileUidRes
            .builder()
            .id(profile.get().getId())
            .userName(profile.get().getDisplayName())
            .avatar(profile.get().getAvatar())
            .build();
        return getProfileUidRes;
    }

    @Override
    public String updateAvatar(FileDTO fileDTO, HttpServletRequest request) {
        log.debug("update avatar profile : {}", fileDTO);
        // link of image
        String urlImage = request.getRequestURL().toString();
        String uriImage = request.getRequestURI();
        urlImage = urlImage.substring(0, urlImage.indexOf(uriImage));
        String linkAvatar = urlImage + "/api/app/image/download/" + fileDTO.getId();

        return linkAvatar;
    }

    @Override
    public String generateReferralCode(ReferralType referralType, HttpServletRequest request) {
        User user = userService.getCurrentUser().get();
        Profile profile = profileRepository.findOneByUser(user).get();
        String referalCode = PREFIX_CODE + String.valueOf(profile.getId());
        if (referralType == ReferralType.CODE) {
            return referalCode;
        }

        String referralLink = request.getRequestURL().toString();
        String urireferral = request.getRequestURI();

        referralLink = referralLink.substring(0, referralLink.indexOf(urireferral));

        referralLink = referralLink + "/api/app/profile/invent/" + referalCode;

        return referralLink;
    }

    @Override
    public Profile getCurrentProfile() {
        Optional<User> currentUser = userService.getCurrentUser();
        if (!currentUser.isPresent()) {
            throw new BadRequestException(MessageHelper.getMessage(Message.Keys.E0003), new Throwable());
        }
        Profile profile = profileRepository.findOneByUser(currentUser.get()).get();
        return profile;
    }
}
