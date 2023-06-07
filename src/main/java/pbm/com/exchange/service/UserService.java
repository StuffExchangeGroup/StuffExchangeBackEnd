package pbm.com.exchange.service;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import com.google.firebase.auth.UserRecord.CreateRequest;
import com.google.firebase.auth.UserRecord.UpdateRequest;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pbm.com.exchange.app.rest.request.ChangePasswordReq;
import pbm.com.exchange.app.rest.request.CheckOTPChangePasswordReq;
import pbm.com.exchange.app.rest.request.CheckOTPReq;
import pbm.com.exchange.app.rest.request.CheckOTPSignUpReq;
import pbm.com.exchange.app.rest.request.ReSendOTPReq;
import pbm.com.exchange.app.rest.request.ResendOTPForUpdateEmail;
import pbm.com.exchange.app.rest.request.ResetPasswordReq;
import pbm.com.exchange.app.rest.request.SignInReq;
import pbm.com.exchange.app.rest.request.SignUpReq;
import pbm.com.exchange.app.rest.request.UpdatePasswordReq;
import pbm.com.exchange.app.rest.respone.ResetPasswordRes;
import pbm.com.exchange.app.rest.respone.SignInRes;
import pbm.com.exchange.app.rest.respone.SignUpRes;
import pbm.com.exchange.app.rest.respone.UserRes;
import pbm.com.exchange.config.Constants;
import pbm.com.exchange.domain.Authority;
import pbm.com.exchange.domain.Level;
import pbm.com.exchange.domain.Nationality;
import pbm.com.exchange.domain.Profile;
import pbm.com.exchange.domain.User;
import pbm.com.exchange.exception.AlreadyUsedException;
import pbm.com.exchange.exception.AuthenticationException;
import pbm.com.exchange.exception.BadRequestException;
import pbm.com.exchange.exception.NotFoundException;
import pbm.com.exchange.message.Message;
import pbm.com.exchange.message.MessageHelper;
import pbm.com.exchange.repository.AuthorityRepository;
import pbm.com.exchange.repository.NationalityRepository;
import pbm.com.exchange.repository.ProfileRepository;
import pbm.com.exchange.repository.UserRepository;
import pbm.com.exchange.security.AuthoritiesConstants;
import pbm.com.exchange.security.SecurityUtils;
import pbm.com.exchange.security.jwt.TokenProvider;
import pbm.com.exchange.security.twilio.TwilioService;
import pbm.com.exchange.service.dto.AdminUserDTO;
import pbm.com.exchange.service.dto.UserDTO;
import tech.jhipster.security.RandomUtil;

/**
 * Service class for managing users.
 */
@Service
@Transactional
public class UserService {

    private Logger log = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthorityRepository authorityRepository;

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private NationalityRepository nationalityRepository;

    @Autowired
    private TwilioService twilioService;

    @Autowired
    private MailService mailService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private AuthenticationManagerBuilder authenticationManagerBuilder;

    @Autowired
    private TokenProvider tokenProvider;

    @Value("${avatar.default.user}")
    private String DEFAULT_AVATAR;

    private static final Double DEFAULT_BALANCE = 0D;

    public Optional<User> activateRegistration(String key) {
        log.debug("Activating user for activation key {}", key);
        return userRepository
            .findOneByActivationKey(key)
            .map(user -> {
                // activate given user for the registration key.
                user.setActivated(true);
                user.setActivationKey(null);
                this.clearUserCaches(user);
                log.debug("Activated user: {}", user);

                // send account creation mail
                mailService.sendCreationEmail(user);
                return user;
            });
    }

    public Optional<User> completePasswordReset(String newPassword, String key) {
        log.debug("Reset user password for reset key {}", key);
        return userRepository
            .findOneByResetKey(key)
            .filter(user -> user.getResetDate().isAfter(Instant.now().minus(1, ChronoUnit.DAYS)))
            .map(user -> {
                user.setPassword(passwordEncoder.encode(newPassword));
                user.setResetKey(null);
                user.setResetDate(null);
                this.clearUserCaches(user);
                return user;
            });
    }

    public Optional<User> requestPasswordReset(String mail) {
        return userRepository
            .findOneByEmailIgnoreCase(mail)
            .filter(User::isActivated)
            .map(user -> {
                user.setResetKey(RandomUtil.generateResetKey());
                user.setResetDate(Instant.now());
                this.clearUserCaches(user);
                return user;
            });
    }

    public User registerUser(AdminUserDTO userDTO, String password) {
        userRepository
            .findOneByLogin(userDTO.getLogin().toLowerCase())
            .ifPresent(existingUser -> {
                boolean removed = removeNonActivatedUser(existingUser);
                if (!removed) {
                    throw new UsernameAlreadyUsedException();
                }
            });
        userRepository
            .findOneByEmailIgnoreCase(userDTO.getEmail())
            .ifPresent(existingUser -> {
                boolean removed = removeNonActivatedUser(existingUser);
                if (!removed) {
                    throw new EmailAlreadyUsedException();
                }
            });
        User newUser = new User();
        String encryptedPassword = passwordEncoder.encode(password);
        newUser.setLogin(userDTO.getLogin().toLowerCase());
        // new user gets initially a generated password
        newUser.setPassword(encryptedPassword);
        newUser.setFirstName(userDTO.getFirstName());
        newUser.setLastName(userDTO.getLastName());
        if (userDTO.getEmail() != null) {
            newUser.setEmail(userDTO.getEmail().toLowerCase());
        }
        newUser.setImageUrl(userDTO.getImageUrl());
        newUser.setLangKey(userDTO.getLangKey());
        // new user is not active
        newUser.setActivated(false);
        // new user gets registration key
        newUser.setActivationKey(RandomUtil.generateActivationKey());
        Set<Authority> authorities = new HashSet<>();
        authorityRepository.findById(AuthoritiesConstants.USER).ifPresent(authorities::add);
        newUser.setAuthorities(authorities);
        userRepository.save(newUser);
        this.clearUserCaches(newUser);
        log.debug("Created Information for User: {}", newUser);
        return newUser;
    }

    private boolean removeNonActivatedUser(User existingUser) {
        if (existingUser.isActivated()) {
            return false;
        }
        // delete profile
        profileRepository
            .findOneByUser(existingUser)
            .ifPresent(profile -> {
                profileRepository.delete(profile);
            });

        userRepository.delete(existingUser);
        userRepository.flush();
        this.clearUserCaches(existingUser);

        return true;
    }

    public User createUser(AdminUserDTO userDTO) {
        User user = new User();
        user.setLogin(userDTO.getLogin().toLowerCase());
        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());
        if (userDTO.getEmail() != null) {
            user.setEmail(userDTO.getEmail().toLowerCase());
        }
        user.setImageUrl(userDTO.getImageUrl());
        if (userDTO.getLangKey() == null) {
            user.setLangKey(Constants.DEFAULT_LANGUAGE); // default language
        } else {
            user.setLangKey(userDTO.getLangKey());
        }
        String encryptedPassword = passwordEncoder.encode(RandomUtil.generatePassword());
        user.setPassword(encryptedPassword);
        user.setResetKey(RandomUtil.generateResetKey());
        user.setResetDate(Instant.now());
        user.setActivated(true);
        if (userDTO.getAuthorities() != null) {
            Set<Authority> authorities = userDTO
                .getAuthorities()
                .stream()
                .map(authorityRepository::findById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toSet());
            user.setAuthorities(authorities);
        }
        userRepository.save(user);
        this.clearUserCaches(user);
        log.debug("Created Information for User: {}", user);
        return user;
    }

    /**
     * Update all information for a specific user, and return the modified user.
     *
     * @param userDTO user to update.
     * @return updated user.
     */
    public Optional<AdminUserDTO> updateUser(AdminUserDTO userDTO) {
        return Optional
            .of(userRepository.findById(userDTO.getId()))
            .filter(Optional::isPresent)
            .map(Optional::get)
            .map(user -> {
                this.clearUserCaches(user);
                user.setLogin(userDTO.getLogin().toLowerCase());
                user.setFirstName(userDTO.getFirstName());
                user.setLastName(userDTO.getLastName());
                if (userDTO.getEmail() != null) {
                    user.setEmail(userDTO.getEmail().toLowerCase());
                }
                user.setImageUrl(userDTO.getImageUrl());
                user.setActivated(userDTO.isActivated());
                user.setLangKey(userDTO.getLangKey());
                Set<Authority> managedAuthorities = user.getAuthorities();
                managedAuthorities.clear();
                userDTO
                    .getAuthorities()
                    .stream()
                    .map(authorityRepository::findById)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .forEach(managedAuthorities::add);
                this.clearUserCaches(user);
                log.debug("Changed Information for User: {}", user);
                return user;
            })
            .map(AdminUserDTO::new);
    }

    public void deleteUser(String login) {
        userRepository
            .findOneByLogin(login)
            .ifPresent(user -> {
                userRepository.delete(user);
                this.clearUserCaches(user);
                log.debug("Deleted User: {}", user);
            });
    }

    /**
     * Update basic information (first name, last name, email, language) for the
     * current user.
     *
     * @param firstName first name of user.
     * @param lastName  last name of user.
     * @param email     email id of user.
     * @param langKey   language key.
     * @param imageUrl  image URL of user.
     */
    public void updateUser(String firstName, String lastName, String email, String langKey, String imageUrl) {
        SecurityUtils
            .getCurrentUserLogin()
            .flatMap(userRepository::findOneByLogin)
            .ifPresent(user -> {
                user.setFirstName(firstName);
                user.setLastName(lastName);
                if (email != null) {
                    user.setEmail(email.toLowerCase());
                }
                user.setLangKey(langKey);
                user.setImageUrl(imageUrl);
                this.clearUserCaches(user);
                log.debug("Changed Information for User: {}", user);
            });
    }

    @Transactional
    public void changePassword(String currentClearTextPassword, String newPassword) {
        SecurityUtils
            .getCurrentUserLogin()
            .flatMap(userRepository::findOneByLogin)
            .ifPresent(user -> {
                String currentEncryptedPassword = user.getPassword();
                if (!passwordEncoder.matches(currentClearTextPassword, currentEncryptedPassword)) {
                    throw new InvalidPasswordException();
                }
                String encryptedPassword = passwordEncoder.encode(newPassword);
                user.setPassword(encryptedPassword);
                this.clearUserCaches(user);
                log.debug("Changed password for User: {}", user);
            });
    }

    @Transactional(readOnly = true)
    public Page<AdminUserDTO> getAllManagedUsers(Pageable pageable) {
        return userRepository.findAll(pageable).map(AdminUserDTO::new);
    }

    @Transactional(readOnly = true)
    public Page<UserDTO> getAllPublicUsers(Pageable pageable) {
        return userRepository.findAllByIdNotNullAndActivatedIsTrue(pageable).map(UserDTO::new);
    }

    @Transactional(readOnly = true)
    public Optional<User> getUserWithAuthoritiesByLogin(String login) {
        return userRepository.findOneWithAuthoritiesByLogin(login);
    }

    @Transactional(readOnly = true)
    public Optional<User> getUserWithAuthorities() {
        return SecurityUtils.getCurrentUserLogin().flatMap(userRepository::findOneWithAuthoritiesByLogin);
    }

    /**
     * Not activated users should be automatically deleted after 3 days.
     * <p>
     * This is scheduled to get fired everyday, at 01:00 (am).
     */
    @Scheduled(cron = "0 0 1 * * ?")
    public void removeNotActivatedUsers() {
        userRepository
            .findAllByActivatedIsFalseAndActivationKeyIsNotNullAndCreatedDateBefore(Instant.now().minus(3, ChronoUnit.DAYS))
            .forEach(user -> {
                log.debug("Deleting not activated user {}", user.getLogin());
                userRepository.delete(user);
                this.clearUserCaches(user);
            });
    }

    /**
     * Gets a list of all the authorities.
     *
     * @return a list of all the authorities.
     */
    @Transactional(readOnly = true)
    public List<String> getAuthorities() {
        return authorityRepository.findAll().stream().map(Authority::getName).collect(Collectors.toList());
    }

    private void clearUserCaches(User user) {
        Objects.requireNonNull(cacheManager.getCache(UserRepository.USERS_BY_LOGIN_CACHE)).evict(user.getLogin());
        if (user.getEmail() != null) {
            Objects.requireNonNull(cacheManager.getCache(UserRepository.USERS_BY_EMAIL_CACHE)).evict(user.getEmail());
        }
    }

    // more method for application

    public SignUpRes signUp(SignUpReq signUpReq) {
        log.debug("Service register user: ", signUpReq);

        // remove all whitespace of phone number
        if (signUpReq.getPhone() != null) {
            signUpReq.setPhone(signUpReq.getPhone().replaceAll(" ", ""));
            signUpReq.setPhone(signUpReq.getPhone().replaceFirst("0", "+84"));
        }

        // remove all whitespace of email
        signUpReq.setEmail(signUpReq.getEmail().replaceAll(" ", ""));

        // check username is existing
        userRepository
            .findOneByLogin(signUpReq.getLogin().toLowerCase())
            .ifPresent(existUser -> {
                if (!removeNonActivatedUser(existUser)) {
                    throw new AlreadyUsedException(MessageHelper.getMessage(Message.Keys.E0024), new Throwable());
                }
            });

        // check phone is existing
        if (signUpReq.getPhone() != null) {
            profileRepository
                .findOneByPhone(signUpReq.getPhone())
                .ifPresent(existProfile -> {
                    if (!removeNonActivatedUser(existProfile.getUser())) {
                        throw new AlreadyUsedException(MessageHelper.getMessage(Message.Keys.E0013), new Throwable());
                    }
                });
        }

        // check email is existing
        userRepository
            .findOneByEmailIgnoreCase(signUpReq.getEmail())
            .ifPresent(existUser -> {
                if (!removeNonActivatedUser(existUser)) {
                    throw new AlreadyUsedException(MessageHelper.getMessage(Message.Keys.E0063), new Throwable());
                }
            });

        // get country code
        String countryCode = null;
        if (signUpReq.getCountryId() != null) {
            Optional<Nationality> nationalityOptional = nationalityRepository.findById(signUpReq.getCountryId());
            if (!nationalityOptional.isPresent()) {
                throw new BadRequestException(MessageHelper.getMessage(Message.Keys.E0087, signUpReq.getCountryId()), new Throwable());
            }
            countryCode = nationalityOptional.get().getCode();
        }

        // Save data
        String encryptedPassword = passwordEncoder.encode(signUpReq.getPassword());
        Set<Authority> authorities = new HashSet<>();
        authorityRepository.findById(AuthoritiesConstants.USER).ifPresent(authorities::add);

        boolean active = false;
        // if (twilioService.hasOTP()) {
        // active = false;
        // }

        // Save new user
        User newUser = User
            .builder()
            .firstName(signUpReq.getFirstName())
            .lastName(signUpReq.getLastName())
            .login(signUpReq.getLogin().toLowerCase())
            .password(encryptedPassword)
            .langKey("en")
            .authorities(authorities)
            .email(signUpReq.getEmail())
            .otp(generateRandomOTP())
            .activated(active)
            .isBlock(false)
            .build();
        newUser = userRepository.save(newUser);
        this.clearUserCaches(newUser);

        // save profile for new user
        Level level = new Level();
        level.setId(1L);
        String displayName = signUpReq.getFirstName() + " " + signUpReq.getLastName();

        Profile profile = Profile
            .builder()
            .user(newUser)
            .phone(signUpReq.getPhone())
            .level(level)
            .avatar(DEFAULT_AVATAR)
            .displayName(displayName)
            .balance(DEFAULT_BALANCE)
            .countryCode(countryCode)
            .build();
        profileRepository.save(profile);

        // User Response
        UserRes userRes = UserRes
            .builder()
            .id(newUser.getId())
            .email(newUser.getEmail())
            .login(newUser.getLogin())
            .lastName(newUser.getLastName())
            .firstName(newUser.getFirstName())
            .activated(newUser.isActivated())
            .phone(profile.getPhone())
            .displayName(profile.getDisplayName())
            .latitude(profile.getLatitude())
            .longitude(profile.getLongitude())
            .location(profile.getLocation())
            .build();

        // register user to firebase with phoneNumber & UserName
        log.debug("register user to firebase");
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        try {
            // delete existUser
            UserRecord existUserRecord = firebaseAuth.getUserByEmail(signUpReq.getEmail());
            if (existUserRecord != null) {
                firebaseAuth.deleteUser(existUserRecord.getUid());
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        try {
            CreateRequest newUserFirebase = new CreateRequest();
            if (signUpReq.getPhone() != null) {
                newUserFirebase.setPhoneNumber(signUpReq.getPhone());
            }
            newUserFirebase.setDisplayName(displayName);
            newUserFirebase.setPhotoUrl(DEFAULT_AVATAR);
            newUserFirebase.setEmail(signUpReq.getEmail());
            newUserFirebase.setDisabled(false);
            firebaseAuth.createUser(newUserFirebase);
        } catch (FirebaseAuthException e) {
            log.error(e.getMessage());
        }

        // Send OTP
        /*
         * boolean hasOTP = twilioService.sendOTP(signUpReq.getPhone());
         * if (hasOTP) {
         * return SignUpRes.builder().user(userRes).useOTP(true).status(true).build();
         * }
         */

        // send activation link to mail
        mailService.sendActivationEmail(newUser);
        return SignUpRes.builder().user(userRes).useOTP(true).status(true).build();
    }

    public String generateRandomOTP() {
        // random otp
        Random random = new Random();
        return String.format("%04d", (random.nextInt(9999) + 1000));
    }

    public SignInRes checkOTPSignUpAndActiveUser(CheckOTPSignUpReq checkOTPReq) {
        log.debug("Server check sing up OTP: ", checkOTPReq);

        // remove all whitespace of email
        checkOTPReq.setEmail(checkOTPReq.getEmail().replaceAll(" ", ""));

        // check user exist
        Optional<User> userOptional = userRepository.findOneByEmailIgnoreCase(checkOTPReq.getEmail());
        if (!userOptional.isPresent()) {
            throw new NotFoundException(MessageHelper.getMessage(Message.Keys.E0003), new Throwable());
        }
        User user = userOptional.get();
        Profile profile = profileRepository.findOneByUser(user).get();

        // check OTP
        // twilioService.checkOTP(checkOTPReq.getPhone(), checkOTPReq.getCodeOTP());
        if (!user.getOtp().equals(checkOTPReq.getCodeOTP())) {
            throw new BadRequestException(MessageHelper.getMessage(Message.Keys.E0094), new Throwable());
        }

        // Active user
        user.setActivated(true);
        user.setOtp(null);
        userRepository.save(user);

        // save uid to profile
        String uid = this.getUID(user);
        profile.setUid(uid);
        profileRepository.save(profile);

        // send mail creating account successfully
        mailService.sendCreationEmail(user);

        SignInReq signInReq = SignInReq.builder().username(user.getLogin()).password(checkOTPReq.getPassword()).build();
        SignInRes signInRes = this.signInWithoutSendOTP(signInReq);

        return signInRes;
    }

    public String getUID(User user) {
        log.debug("Get uid from user: ", user);
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        String uid = null;
        try {
            UserRecord existUser = firebaseAuth.getUserByEmail(user.getEmail());
            if (existUser != null) {
                uid = existUser.getUid();
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }

        return uid;
    }

    public SignInRes signInWithoutSendOTP(SignInReq signInReq) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
            signInReq.getUsername(),
            signInReq.getPassword()
        );

        Authentication authentication = null;

        try {
            authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        } catch (Exception ex) {
            throw new AuthenticationException(MessageHelper.getMessage(Message.Keys.E0012), new Throwable());
        }

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwtToken = tokenProvider.createToken(authentication, signInReq.isRememberMe());

        // get User Info
        User user = userRepository.findOneWithAuthoritiesByLogin(authentication.getName()).get();
        Profile profile = profileRepository.findOneByUser(user).get();

        // make custom token firebase for curruntUser
        String customUserToken = this.generateCustomTokenFirebase(user);

        UserRes userRes = UserRes
            .builder()
            .id(user.getId())
            .firstName(user.getFirstName())
            .lastName(user.getLastName())
            .activated(user.isActivated())
            .email(user.getEmail())
            .login(user.getLogin())
            .phone(profile.getPhone())
            .displayName(profile.getDisplayName())
            .latitude(profile.getLatitude())
            .longitude(profile.getLongitude())
            .dob(profile.getDob())
            .cityId(profile.getCity() != null ? profile.getCity().getId() : null)
            .avatar(profile.getAvatar())
            .location(profile.getLocation())
            .point(profile.getPoint())
            .customTokenFirebase(customUserToken)
            .uid(profile.getUid())
            .build();

        return SignInRes.builder().authToken(jwtToken).user(userRes).useOTP(true).status(true).build();
    }

    public String generateCustomTokenFirebase(User user) {
        log.debug("Make custom token firebase for curruntUser");
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        String customUserToken = null;
        try {
            UserRecord existUserRecord = firebaseAuth.getUserByEmail(user.getEmail());
            if (existUserRecord != null) {
                customUserToken = FirebaseAuth.getInstance().createCustomToken(existUserRecord.getUid());
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }

        return customUserToken;
    }

    @Transactional
    public SignInRes signIn(SignInReq signInReq) {
        final String ADMIN = "admin";
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
            signInReq.getUsername(),
            signInReq.getPassword()
        );

        Authentication authentication = null;

        try {
            authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        } catch (Exception ex) {
            throw new AuthenticationException(MessageHelper.getMessage(Message.Keys.E0012), new Throwable());
        }

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwtToken = tokenProvider.createToken(authentication, signInReq.isRememberMe());

        // get User Info
        User user = userRepository.findOneWithAuthoritiesByLogin(authentication.getName()).get();
        Profile profile = profileRepository.findOneByUser(user).get();

        if (user.getIsBlock()) {
        	throw new AuthenticationException(MessageHelper.getMessage("Tài khoản của bạn đã bị khóa, vui lòng liên hệ với admin"), new Throwable());
        }
        // make custom token firebase for curruntUser
        String customUserToken = this.generateCustomTokenFirebase(user);

        UserRes userRes = UserRes
            .builder()
            .id(user.getId())
            .firstName(user.getFirstName())
            .lastName(user.getLastName())
            .activated(user.isActivated())
            .email(user.getEmail())
            .login(user.getLogin())
            .phone(profile.getPhone())
            .displayName(profile.getDisplayName())
            .latitude(profile.getLatitude())
            .longitude(profile.getLongitude())
            .dob(profile.getDob())
            .cityId(profile.getCity() != null ? profile.getCity().getId() : null)
            .avatar(profile.getAvatar())
            .location(profile.getLocation())
            .point(profile.getPoint())
            .uid(profile.getUid())
            .customTokenFirebase(customUserToken)
            .build();

        userRes.setAuthorities(user.getAuthorities());
        // Send OPT
        /*
         * boolean hasOTP;
         * if (user.getLogin().compareTo(ADMIN) != 0) {
         * hasOTP = twilioService.sendOTP(profile.getPhone());
         * if (hasOTP) {
         * return
         * SignInRes.builder().authToken(jwtToken).user(userRes).useOTP(true).status(
         * true).build();
         * }
         * }
         */

        return SignInRes.builder().authToken(jwtToken).user(userRes).build();
    }

    public boolean checkOTP(CheckOTPReq checkOTPReq) {
        // check user exist
        Optional<User> userOptional = userRepository.findOneByEmailIgnoreCase(checkOTPReq.getEmail());
        if (!userOptional.isPresent()) {
            throw new NotFoundException(MessageHelper.getMessage(Message.Keys.E0003), new Throwable());
        }
        User user = userOptional.get();

        // check OTP
        if (!user.getOtp().equals(checkOTPReq.getCodeOTP())) {
            throw new BadRequestException(MessageHelper.getMessage(Message.Keys.E0094), new Throwable());
        }

        return true;
    }

    public boolean resendOTP(String email) {
        Optional<User> userOptional = userRepository.findOneByEmailIgnoreCase(email);
        if (!userOptional.isPresent()) {
            throw new BadRequestException(MessageHelper.getMessage(Message.Keys.E0003), new Throwable());
        }
        User user = userOptional.get();

        // set new otp
        String newOtp = generateRandomOTP();
        user.setOtp(newOtp);
        userRepository.saveAndFlush(user);

        mailService.sendActivationEmail(user);

        return true;
    }

    public boolean resendOTP(ReSendOTPReq resendOTPReq) {
        Optional<User> userOptional = userRepository.findOneByTempEmailIgnoreCase(resendOTPReq.getEmail());
        if (!userOptional.isPresent()) {
            throw new BadRequestException(MessageHelper.getMessage(Message.Keys.E0003), new Throwable());
        }
        User user = userOptional.get();

        // set new otp
        String newOtp = generateRandomOTP();
        user.setOtp(newOtp);
        userRepository.saveAndFlush(user);

        mailService.sendUpdateEmail(user);
        return true;
    }

    public ResetPasswordRes forgotPassword(ResetPasswordReq resetPasswordReq) {
        // remove all whitespace of phone number
        resetPasswordReq.setPhone(resetPasswordReq.getPhone().replaceAll(" ", ""));

        // check user exist
        var profileOptional = profileRepository.findOneByPhone(resetPasswordReq.getPhone());
        if (!profileOptional.isPresent()) {
            throw new BadRequestException(MessageHelper.getMessage(Message.Keys.E0044), new Throwable());
        }

        // check user is Activated
        User user = profileOptional.get().getUser();
        if (!user.isActivated()) {
            throw new BadRequestException(MessageHelper.getMessage(Message.Keys.E0004), new Throwable());
        }

        // Send OTP
        boolean hasOTP = twilioService.sendOTP(resetPasswordReq.getPhone());
        return ResetPasswordRes.builder().status(true).useOTP(hasOTP).build();
    }

    @Transactional
    public boolean updatePassword(UpdatePasswordReq updatePasswordReq) {
        Optional<User> userOptional = userRepository.findOneByEmailIgnoreCase(updatePasswordReq.getEmail());
        if (!userOptional.isPresent()) {
            throw new NotFoundException(MessageHelper.getMessage(Message.Keys.E0003), new Throwable());
        }

        User user = userOptional.get();
        String encryptedPassword = passwordEncoder.encode(updatePasswordReq.getPassword());
        user.setPassword(encryptedPassword);
        userRepository.save(user);
        this.clearUserCaches(user);

        return true;
        //        SignInReq signInReq = SignInReq.builder().username(user.getLogin()).password(updatePasswordReq.getPassword())
        //                .build();
        //        SignInRes signInRes = this.signInWithoutSendOTP(signInReq);
        //        return signInRes;
    }

    public ResetPasswordRes requestChangePassword(ChangePasswordReq changePasswordReq) {
        User currentUser = this.getCurrentUser().get();
        String currentEncryptedPassword = currentUser.getPassword();

        // check old password
        if (!passwordEncoder.matches(changePasswordReq.getOldPassword(), currentEncryptedPassword)) {
            throw new BadRequestException(MessageHelper.getMessage(Message.Keys.E0082), new Throwable());
        }
        Profile currentProfile = profileRepository.findOneByUser(currentUser).get();

        // Send OTP
        boolean hasOTP = twilioService.sendOTP(currentProfile.getPhone());
        String encryptedPassword = passwordEncoder.encode(changePasswordReq.getNewPassword());

        if (hasOTP) {
            currentUser.setTempPassword(encryptedPassword);
        } else {
            currentUser.setPassword(encryptedPassword);
        }

        userRepository.save(currentUser);
        this.clearUserCaches(currentUser);
        return ResetPasswordRes.builder().useOTP(hasOTP).status(true).build();
    }

    public boolean checkOTPAndUpdatePassword(CheckOTPChangePasswordReq checkOTPChangePasswordReq) {
        User currentUser = this.getCurrentUser().get();
        Profile currentProfile = profileRepository.findOneByUser(currentUser).get();

        // check OTP
        twilioService.checkOTP(currentProfile.getPhone(), checkOTPChangePasswordReq.getCodeOTP());

        // Update password
        String password = currentUser.getTempPassword();
        currentUser.setPassword(password);
        currentUser.setTempPassword("");
        userRepository.save(currentUser);
        this.clearUserCaches(currentUser);
        return true;
    }

    @Transactional(readOnly = true)
    public Optional<User> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName = authentication.getName();
        return userRepository.findOneWithAuthoritiesByLogin(userName);
    }

    public void updateUserAvatar(Profile profile) {
        log.debug("Update user's avatar in firebase");
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        UpdateRequest request = new UpdateRequest(profile.getUid()).setPhotoUrl(profile.getAvatar());
        try {
            firebaseAuth.updateUser(request);
        } catch (FirebaseAuthException e) {
            e.printStackTrace();
        }
    }

    public void updateUserEmail(Profile profile, User user) {
        log.debug("Update user's email in firebase");
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        UpdateRequest request = new UpdateRequest(profile.getUid()).setEmail(user.getEmail());
        try {
            firebaseAuth.updateUser(request);
        } catch (FirebaseAuthException e) {
            e.printStackTrace();
        }
    }

    public void updateUserPhone(Profile profile, User user) {
        log.debug("Update user's phone in firebase");
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        UpdateRequest request = new UpdateRequest(profile.getUid()).setPhoneNumber(profile.getPhone());
        try {
            firebaseAuth.updateUser(request);
        } catch (FirebaseAuthException e) {
            e.printStackTrace();
        }
    }

    public boolean checkExistEmail(String email) {
        if (userRepository.existsByEmailIgnoreCase(email)) {
            throw new BadRequestException(MessageHelper.getMessage(Message.Keys.E0063), new Throwable());
        }
        // send activation link to email
        User user = getCurrentUser().get();
        user.setOtp(generateRandomOTP());
        user.setTempEmail(email);

        // save user with temp email and new otp
        userRepository.saveAndFlush(user);
        mailService.sendUpdateEmail(user);

        return true;
    }
}
