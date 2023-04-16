package pbm.com.exchange.service;

import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import pbm.com.exchange.app.rest.respone.GetProfileUidRes;
import pbm.com.exchange.app.rest.vm.PutProfileDTO;
import pbm.com.exchange.domain.Profile;
import pbm.com.exchange.domain.enumeration.ReferralType;
import pbm.com.exchange.service.dto.FileDTO;
import pbm.com.exchange.service.dto.ProfileDTO;

/**
 * Service Interface for managing {@link pbm.com.exchange.domain.Profile}.
 */
public interface ProfileService {
    /**
     * Save a profile.
     *
     * @param profileDTO the entity to save.
     * @return the persisted entity.
     */
    ProfileDTO save(ProfileDTO profileDTO);

    /**
     * Partially updates a profile.
     *
     * @param profileDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<ProfileDTO> partialUpdate(ProfileDTO profileDTO);

    /**
     * Get all the profiles.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<ProfileDTO> findAll(Pageable pageable);

    /**
     * Get the "id" profile.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<ProfileDTO> findOne(Long id);

    /**
     * Delete the "id" profile.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Partial update profile
     *
     * @param profileDTO
     * @return PutProfileDTO
     */
    PutProfileDTO partialUpdate(PutProfileDTO profileDTO);

    /**
     * Get profile by uid
     *
     * @param uid
     * @return GetProfileUidRes
     */
    GetProfileUidRes getProfileByUid(String uid);

    /**
     * Update user avatar
     *
     * @param fileDTO
     * @param request
     * @return
     */
    String updateAvatar(FileDTO fileDTO, HttpServletRequest request);

    /**
     * get referral code of current user
     * @return Referral Code
     */
    String generateReferralCode(ReferralType referralType, HttpServletRequest request);

    /**
     * get current profile
     *
     * @return Profile
     */
    Profile getCurrentProfile();
}
