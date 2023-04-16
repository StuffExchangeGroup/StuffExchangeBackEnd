package pbm.com.exchange.service.impl;

import java.time.ZonedDateTime;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pbm.com.exchange.app.rest.request.ConfirmSwapReq;
import pbm.com.exchange.app.rest.request.RequestSwapReq;
import pbm.com.exchange.app.rest.request.SendNotificationToMultiDevicesReq;
import pbm.com.exchange.app.rest.vm.ItemSwapDTO;
import pbm.com.exchange.app.rest.vm.MySwapDTO;
import pbm.com.exchange.app.rest.vm.ProductSwapDTO;
import pbm.com.exchange.domain.AppConfiguration;
import pbm.com.exchange.domain.Exchange;
import pbm.com.exchange.domain.Level;
import pbm.com.exchange.domain.Notification;
import pbm.com.exchange.domain.NotificationToken;
import pbm.com.exchange.domain.Product;
import pbm.com.exchange.domain.Profile;
import pbm.com.exchange.domain.User;
import pbm.com.exchange.domain.enumeration.ConfirmStatus;
import pbm.com.exchange.domain.enumeration.ExchangeAction;
import pbm.com.exchange.domain.enumeration.ExchangeStatus;
import pbm.com.exchange.domain.enumeration.ExchangeType;
import pbm.com.exchange.domain.enumeration.NotificationType;
import pbm.com.exchange.exception.BadRequestException;
import pbm.com.exchange.message.Message;
import pbm.com.exchange.message.MessageHelper;
import pbm.com.exchange.repository.AppConfigurationRepository;
import pbm.com.exchange.repository.ExchangeRepository;
import pbm.com.exchange.repository.LevelRepository;
import pbm.com.exchange.repository.NotificationRepository;
import pbm.com.exchange.repository.NotificationTokenRepository;
import pbm.com.exchange.repository.ProductRepository;
import pbm.com.exchange.repository.ProfileRepository;
import pbm.com.exchange.service.ExchangeService;
import pbm.com.exchange.service.NotificationService;
import pbm.com.exchange.service.UserService;
import pbm.com.exchange.service.dto.ExchangeDTO;
import pbm.com.exchange.service.mapper.ExchangeMapper;

/**
 * Service Implementation for managing {@link Exchange}.
 */
@Service
@Transactional
public class ExchangeServiceImpl implements ExchangeService {

    private static final String CHATTING_KEY = "CHAT_FEE";

    private final Logger log = LoggerFactory.getLogger(ExchangeServiceImpl.class);

    @Autowired
    ProfileRepository profileRepository;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    AppConfigurationRepository appConfigurationRepository;

    @Autowired
    LevelRepository levelRepository;

    @Autowired
    NotificationRepository notificationRepository;

    @Autowired
    NotificationTokenRepository notificationTokenRepository;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    UserService userService;

    private final ExchangeRepository exchangeRepository;

    private final ExchangeMapper exchangeMapper;

    public ExchangeServiceImpl(ExchangeRepository exchangeRepository, ExchangeMapper exchangeMapper) {
        this.exchangeRepository = exchangeRepository;
        this.exchangeMapper = exchangeMapper;
    }

    @Override
    public ExchangeDTO save(ExchangeDTO exchangeDTO) {
        log.debug("Request to save Exchange : {}", exchangeDTO);
        Exchange exchange = exchangeMapper.toEntity(exchangeDTO);
        exchange = exchangeRepository.save(exchange);
        return exchangeMapper.toDto(exchange);
    }

    @Override
    public Optional<ExchangeDTO> partialUpdate(ExchangeDTO exchangeDTO) {
        log.debug("Request to partially update Exchange : {}", exchangeDTO);

        return exchangeRepository
                .findById(exchangeDTO.getId())
                .map(existingExchange -> {
                    exchangeMapper.partialUpdate(existingExchange, exchangeDTO);

                    return existingExchange;
                })
                .map(exchangeRepository::save)
                .map(exchangeMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ExchangeDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Exchanges");
        return exchangeRepository.findAll(pageable).map(exchangeMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ExchangeDTO> findOne(Long id) {
        log.debug("Request to get Exchange : {}", id);
        return exchangeRepository.findById(id).map(exchangeMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete Exchange : {}", id);
        exchangeRepository.deleteById(id);
    }

    @Override
    public boolean startChatting(Long exchangeId) {
        User currentUser = userService.getCurrentUser().get();
        Profile currentProfile = profileRepository.findOneByUser(currentUser).get();

        // validation
        Exchange currentExchange = validationExchange(exchangeId);

        Product sendProduct = currentExchange.getSendProduct();
        Product receiveProduct = currentExchange.getReceiveProduct();

        Profile ownerProfile = sendProduct.getProfile();
        Profile exchangeProfile = receiveProduct.getProfile();

        if (currentExchange.getChatting()) {
            currentExchange.setChatting(false);
            exchangeRepository.save(currentExchange);

            // push notification
            if (currentProfile.getId().equals(currentExchange.getOwner().getId())) {
                this.pushNotificationToUser(
                        ownerProfile,
                        exchangeProfile,
                        sendProduct,
                        receiveProduct,
                        NotificationType.CLOSE_CONVERSATION);
            } else {
                this.pushNotificationToUser(
                        exchangeProfile,
                        ownerProfile,
                        receiveProduct,
                        sendProduct,
                        NotificationType.CLOSE_CONVERSATION);
            }

            return true;
        }

        // charge money for each conversation is 5k

        Double ownerBalance = ownerProfile.getBalance() != null ? ownerProfile.getBalance() : 0;
        Double exchangeBalance = exchangeProfile.getBalance() != null ? exchangeProfile.getBalance() : 0;
        String ownerName = ownerProfile.getDisplayName();
        String exchangeName = exchangeProfile.getDisplayName();

        // get chatting fee from db
        Double chattingFee = this.getChattingFee();

        if (ownerBalance < chattingFee || exchangeBalance < chattingFee) {
            throw new BadRequestException(MessageHelper.getMessage(Message.Keys.E0080, ownerName, exchangeName),
                    new Throwable());
        }

        ownerProfile.setBalance(ownerBalance - chattingFee);
        exchangeProfile.setBalance(ownerBalance - chattingFee);

        profileRepository.save(ownerProfile);
        profileRepository.save(exchangeProfile);

        // update chat status
        currentExchange.setChatting(true);
        currentExchange.setStatus(ExchangeStatus.SWAPPING);
        exchangeRepository.save(currentExchange);

        // push notification
        if (currentProfile.getId().equals(currentExchange.getOwner().getId())) {
            this.pushNotificationToUser(ownerProfile, exchangeProfile, sendProduct, receiveProduct,
                    NotificationType.START_CONVERSATION);
        } else {
            this.pushNotificationToUser(exchangeProfile, ownerProfile, receiveProduct, sendProduct,
                    NotificationType.START_CONVERSATION);
        }

        return true;
    }

    /**
     * Check exchange is exist
     *
     * @param exchangeId
     * @return Exchange
     */
    private Exchange validationExchange(Long exchangeId) {
        log.debug("Validate exchange with id: {}", exchangeId);
        var currentExchangeOptional = exchangeRepository.findById(exchangeId);
        if (!currentExchangeOptional.isPresent()) {
            throw new BadRequestException(MessageHelper.getMessage(Message.Keys.E0068, exchangeId), new Throwable());
        }
        return currentExchangeOptional.get();
    }

    public Double getChattingFee() {
        AppConfiguration appConfiguration = appConfigurationRepository.findByKey(CHATTING_KEY);
        String chattingFeeString = appConfiguration.getValue();
        Double chattingFee = null;
        try {
            chattingFee = Double.parseDouble(chattingFeeString);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return chattingFee;
    }

    public int getSwapLimit(Long levelId) {
        Level level = levelRepository.findById(levelId).get();
        return level.getSwapLimit();
    }

    @Override
    public boolean confirmSwap(ConfirmSwapReq confirmSwapReq) {
        log.debug("Request service to check and update exchange {}", confirmSwapReq);
        Exchange currentExchange = validationExchange(confirmSwapReq.getExchangeId());
        Product sentProduct = currentExchange.getSendProduct();
        Product receiveProduct = currentExchange.getReceiveProduct();

        // Get current user
        User currentUser = userService.getCurrentUser().get();
        Profile currentProfile = profileRepository.findOneByUser(currentUser).get();
        final int MAXIMUM_COUNT_REQUEST = currentProfile.getLevel().getSwapLimit() != null
                ? currentProfile.getLevel().getSwapLimit()
                : 0;
        log.debug("Get current profile: {}", currentProfile);

        // flag check delete exchange
        boolean hasDelete = false;
        boolean isCancel = false;

        // check current user is sent or receive
        if (currentProfile.getId().equals(currentExchange.getOwner().getId())) {
            // current user is SENT (owner)
            log.debug("Current user is requester");
            switch (ExchangeAction.valueOf(confirmSwapReq.getAction())) {
                case CANCEL:
                    if (currentExchange.getExchangerConfirm() == ConfirmStatus.CANCEL ||
                            currentExchange.getExchangerConfirm() == ConfirmStatus.WAITING) {
                        // remove this exchange with set flag delete
                        hasDelete = true;

                        // decrease requestCount
                        sentProduct = decreaseCount(sentProduct, true, MAXIMUM_COUNT_REQUEST);
                        
                        // decrease receiveCount
                        receiveProduct = decreaseCount(receiveProduct, false, 0);
                    } else {
                        // handle trick
                        if (currentExchange.getOwnerConfirm() == ConfirmStatus.CANCEL)
                            return true;

                        currentExchange.setOwnerConfirm(ConfirmStatus.CANCEL);
                        currentExchange.setStatus(ExchangeStatus.WAITING);

                        // push notification
                        this.pushNotificationToUser(
                                currentProfile,
                                receiveProduct.getProfile(),
                                sentProduct,
                                receiveProduct,
                                NotificationType.CANCEL_SWAP);
                    }

                    // set flag isCancel = true
                    isCancel = true;
                    break;
                case ACCEPT:
                    // trick
                    if (currentExchange.getOwnerConfirm() == ConfirmStatus.ACCEPT)
                        return true;

                    currentExchange.setOwnerConfirm(ConfirmStatus.ACCEPT);
                    currentExchange.setStatus(ExchangeStatus.SWAPPING);

                    // push notification
                    this.pushNotificationToUser(
                            currentProfile,
                            receiveProduct.getProfile(),
                            sentProduct,
                            receiveProduct,
                            NotificationType.ACCEPT_SWAP);

                    break;
            }
            // save sentProduct when increaseCount and decreaseCount
            sentProduct = productRepository.save(sentProduct);
            receiveProduct = productRepository.save(receiveProduct);
            log.debug("Update send product successfully {}", sentProduct);
            log.debug("Update receive product successfully {}", receiveProduct);
            
        } else if (currentProfile.getId().equals(currentExchange.getExchanger().getId())) {
            // current user is RECEIVE
            log.debug("Current user is receiver");
            switch (ExchangeAction.valueOf(confirmSwapReq.getAction())) {
                case CANCEL:
                    if (currentExchange.getOwnerConfirm() == ConfirmStatus.CANCEL) {
                        // remove this exchange with set flag delete
                        hasDelete = true;
                        
                        // decrease requestCount
                        sentProduct = decreaseCount(sentProduct, true, MAXIMUM_COUNT_REQUEST);
                        
                        // decrease receiveCount
                        receiveProduct = decreaseCount(receiveProduct, false, MAXIMUM_COUNT_REQUEST);
                    } else {
                        // handle trick
                        if (currentExchange.getExchangerConfirm() == ConfirmStatus.WAITING)
                            return true;

                        currentExchange.setExchangerConfirm(ConfirmStatus.CANCEL);
                        currentExchange.setStatus(ExchangeStatus.WAITING);

                        // push notification WILL EDIT
                        this.pushNotificationToUser(
                                currentProfile,
                                sentProduct.getProfile(),
                                receiveProduct,
                                sentProduct,
                                NotificationType.CANCEL_SWAP);
                    }
                    

                    // set flag isCancel = true
                    isCancel = true;
                    break;
                case ACCEPT:
                    // trick
                    if (currentExchange.getExchangerConfirm() == ConfirmStatus.ACCEPT)
                        return true;

                    currentExchange.setExchangerConfirm(ConfirmStatus.ACCEPT);
                    currentExchange.setStatus(ExchangeStatus.SWAPPING);

                    // push notification
                    this.pushNotificationToUser(
                            currentProfile,
                            sentProduct.getProfile(),
                            receiveProduct,
                            sentProduct,
                            NotificationType.ACCEPT_SWAP);

                    break;
            }
            // save receiveProduct when increaseCount and decreaseCount
            sentProduct = productRepository.save(sentProduct);
            
            // save receiveProduct when increaseCount and decreaseCount
            receiveProduct = productRepository.save(receiveProduct);
            log.debug("Update receive product sucessfully {}", receiveProduct);
        }
        if (hasDelete) {
            exchangeRepository.delete(currentExchange);
            log.debug("Delete exchange successfully");
        } else {
            // Case Cancel when started chat
            if (currentExchange.getChatting() && isCancel) {
                currentExchange.setChatting(false);
            }
            exchangeRepository.save(currentExchange);
            log.debug("Update exchange successfully {}", receiveProduct);
        }
        return true;
    }

    @Override
    public boolean requestSwap(RequestSwapReq requestSwapReq) {
        // Validation data
        Optional<Product> sendProductOptional = productRepository.findById(requestSwapReq.getSendProductId());
        Optional<Product> receiveProductReqOptional = productRepository.findById(requestSwapReq.getReceiveProductId());

        if (!sendProductOptional.isPresent()) {
            throw new BadRequestException(
                    MessageHelper.getMessage(Message.Keys.E0071, requestSwapReq.getSendProductId()), new Throwable());
        }
        if (!receiveProductReqOptional.isPresent()) {
            throw new BadRequestException(
                    MessageHelper.getMessage(Message.Keys.E0071, requestSwapReq.getReceiveProductId()),
                    new Throwable());
        }

        Product sendProduct = sendProductOptional.get();
        Product receiveProduct = receiveProductReqOptional.get();

        // check send product and receive product already exist in Exchange table both send or receive product
        boolean hasExchangeBySendAndReceive = exchangeRepository.existsBySendProductAndReceiveProduct(sendProduct, receiveProduct);
        boolean hasExchangeByReceiveAndSend = exchangeRepository.existsByReceiveProductAndSendProduct(sendProduct, receiveProduct);

        if (hasExchangeBySendAndReceive || hasExchangeByReceiveAndSend) {
            throw new BadRequestException(MessageHelper.getMessage(Message.Keys.E0079), new Throwable());
        }

        // Get current user
        User currentUser = userService.getCurrentUser().get();
        Profile currentProfile = profileRepository.findOneByUser(currentUser).get();
        final int MAXIMUM_COUNT_REQUEST = currentProfile.getLevel().getSwapLimit() != null
                ? currentProfile.getLevel().getSwapLimit()
                : 0;

        if (sendProduct.getProfile().getId() != currentProfile.getId()) {
            throw new BadRequestException(
                    MessageHelper.getMessage(Message.Keys.E0070, requestSwapReq.getSendProductId()), new Throwable());
        }
        if (sendProduct.getProfile().getId() == receiveProduct.getProfile().getId()) {
            throw new BadRequestException(MessageHelper.getMessage(Message.Keys.E0072), new Throwable());
        }
        // Check limit when send request by level
        if (sendProduct.getRequestCount() >= MAXIMUM_COUNT_REQUEST) {
            throw new BadRequestException(
                    MessageHelper.getMessage(Message.Keys.E0074, requestSwapReq.getSendProductId()), new Throwable());
        }

        Exchange exchangeSave = Exchange
                .builder()
                .owner(sendProduct.getProfile())
                .ownerConfirm(ConfirmStatus.ACCEPT)
                .status(ExchangeStatus.WAITING)
                .sendProduct(sendProduct)
                .receiveProduct(receiveProduct)
                .active(true)
                .exchangerConfirm(ConfirmStatus.WAITING)
                .exchanger(receiveProduct.getProfile())
                .chatting(false)
                .build();

        // update requestCount of sendProduct
        Integer currentRequestCount = sendProduct.getRequestCount() + 1;
        sendProduct.setRequestCount(currentRequestCount);
        if (currentRequestCount >= MAXIMUM_COUNT_REQUEST) {
            sendProduct.setIsSwapAvailable(false);
        }

        // update receiveCount of receiveProduct
        receiveProduct.setReceiveCount(receiveProduct.getReceiveCount() + 1);

        exchangeRepository.save(exchangeSave);
        productRepository.save(sendProduct);
        productRepository.save(receiveProduct);

        // push notification to exchanger
        this.pushNotificationToUser(
                currentProfile,
                receiveProduct.getProfile(),
                sendProduct,
                receiveProduct,
                NotificationType.REQUEST_SWAP);

        return true;
    }

    @Override
    public boolean pushNotificationToUser(
            Profile senderProfile,
            Profile receiverProfile,
            Product sendProduct,
            Product receiveProduct,
            NotificationType notificationType) {
        log.debug("Send notification to profile: {}" + receiverProfile);

        List<NotificationToken> listOfNotificationToken = notificationTokenRepository.findByProfile(receiverProfile);
        if (listOfNotificationToken != null && !listOfNotificationToken.isEmpty()) {
            String title = "";
            String message = "";
            String productName = receiveProduct.getName().toLowerCase().strip();

            switch (notificationType) {
                case REQUEST_SWAP:
                    title = "Yêu cầu trao đổi";
                    message = "Sản phẩm \"" + productName + "\" đã nhận một yêu cầu trao đổi";
                    break;
                case ACCEPT_SWAP:
                    title = "Chấp nhận trao đổi";
                    message = "Cuộc trao đổi với sản phẩm \"" + productName + "\" đã được chấp nhận";
                    break;
                case CANCEL_SWAP:
                    title = "Hủy bỏ trao đổi";
                    message = "Cuộc trao đổi với sản phẩm \"" + productName + "\" đã bị hủy bỏ";
                    break;
                case START_CONVERSATION:
                    title = "Bắt đầu cuộc trò chuyện";
                    message = "Cuộc trao đổi với sản phẩm \"" + productName + "\" đã bắt đầu cuộc trò chuyện";
                    break;
                case CLOSE_CONVERSATION:
                    title = "Kết thúc cuộc trò chuyện";
                    message = "Cuộc trao đổi với sản phẩm \"" + productName + "\" đã kết thúc cuộc trò chuyện";
                    break;
                default:
                    break;
            }

            List<String> notificationTokenStrings = new ArrayList<>();

            for (NotificationToken notificationToken : listOfNotificationToken) {
                notificationTokenStrings.add(notificationToken.getToken());
            }

            // set data to notification
            Map<String, String> data = new HashMap<>();
            data.put("productId", receiveProduct.getId().toString());
            data.put("type", notificationType.toString());

            SendNotificationToMultiDevicesReq sendNotificationToMultiDevicesReq = SendNotificationToMultiDevicesReq
                    .builder()
                    .tokens(notificationTokenStrings)
                    .title(title)
                    .message(message)
                    .data(data)
                    .build();

            notificationService.sendNotificationToMultipleDevices(sendNotificationToMultiDevicesReq);

            Notification notification = Notification
                    .builder()
                    .subject(title)
                    .content(message)
                    .senderId(senderProfile.getId())
                    .receiverId(receiverProfile.getId())
                    .sendProductId(sendProduct.getId())
                    .receiveProductId(receiveProduct.getId())
                    .createdDate(ZonedDateTime.now())
                    .notificationType(notificationType)
                    .isSeen(false)
                    .build();

            notificationRepository.save(notification);

            return true;
        }

        return false;
    }

    /**
     *
     * @param product
     * @param isRequestCount
     * @param MAXIMUM_REQUEST_COUNT
     * @return Product
     */
    private Product decreaseCount(Product product, boolean isRequestCount, Integer MAXIMUM_REQUEST_COUNT) {
        if (isRequestCount) {
            Integer currentRequestCount = product.getRequestCount() - 1;
            product.setRequestCount(currentRequestCount);
            if (currentRequestCount < MAXIMUM_REQUEST_COUNT) {
                product.setIsSwapAvailable(true);
            }
        } else {
            product.setReceiveCount(product.getReceiveCount() - 1);
        }
        return product;
    }

    /**
     *
     * @param product
     * @param isRequestCount
     * @param MAXIMUM_REQUEST_COUNT
     * @return Product
     */
    private Product increaseCount(Product product, boolean isRequestCount, Integer MAXIMUM_REQUEST_COUNT) {
        if (isRequestCount) {
            Integer currentRequestCount = product.getRequestCount() + 1;
            product.setRequestCount(currentRequestCount);

            if (currentRequestCount >= MAXIMUM_REQUEST_COUNT) {
                product.setIsSwapAvailable(false);
            }
        } else {
            product.setReceiveCount(product.getReceiveCount() + 1);
        }
        return product;
    }

    @Override
    public List<ItemSwapDTO> getExchangesByProductId(Long productId, ExchangeType type, Pageable pageable) {
        log.debug("Get exchange by product id: " + productId + " and exchange type");

        // create new pageable to combine sort by updated date
        Pageable newPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(),
                Sort.by("lastModifiedDate").descending());

        User currentUser = userService.getCurrentUser().get();
        Profile currentProfile = profileRepository.findOneByUser(currentUser).get();

        // validate product
        Optional<Product> productOptional = productRepository.findById(productId);
        if (!productOptional.isPresent()) {
            throw new BadRequestException(MessageHelper.getMessage(Message.Keys.E0071, productId), new Throwable());
        }
        Product myProduct = productOptional.get();
        if (myProduct.getProfile().getId() != currentProfile.getId()) {
            throw new BadRequestException(MessageHelper.getMessage(Message.Keys.E0070, productId), new Throwable());
        }

        List<ItemSwapDTO> itemSwaps = new ArrayList<>();
        List<Exchange> exchanges = null;

        /**
         * get list of exchanges by product and exchangeType. if type is SENT then get
         * exchanges by sendProduct. if type is RECEIVED then get exchanges by
         * receiveProduct. if type is NULL then get exchanges by sendProduct or
         * receiveProduct
         */
        if (type != null) {
            if (type == ExchangeType.SENT) {
                exchanges = exchangeRepository.findBySendProduct(myProduct, newPageable);
            } else {
                exchanges = exchangeRepository.findByReceiveProduct(myProduct, newPageable);
            }
        } else {
            exchanges = exchangeRepository.findBySendProductOrReceiveProduct(myProduct, myProduct, newPageable);
        }

        // myProduct is always the product i found by id above
        ProductSwapDTO myProductDto = ProductSwapDTO
                .builder()
                .productId(myProduct.getId())
                .productName(myProduct.getName())
                .thumbnail(myProduct.getThumbnail())
                .uid(myProduct.getProfile().getUid())
                .build();

        for (Exchange exchange : exchanges) {
            Product exchangeProduct = null;
            ExchangeType swapType = null;
            ConfirmStatus ownerConfirm = null;
            ConfirmStatus exchangerConfirm = null;
            Profile exchangerProfile = null;

            // identify exchangeProduct, ownerConfirm, exchangeConfirm and swapType
            if (exchange.getSendProduct().getId() == productId) {
                exchangeProduct = exchange.getReceiveProduct();
                ownerConfirm = exchange.getOwnerConfirm();
                exchangerConfirm = exchange.getExchangerConfirm();
                swapType = ExchangeType.SENT;
            } else {
                exchangeProduct = exchange.getSendProduct();
                ownerConfirm = exchange.getExchangerConfirm();
                exchangerConfirm = exchange.getOwnerConfirm();
                swapType = ExchangeType.RECEIVED;
            }
            exchangerProfile = exchangeProduct.getProfile();

            ProductSwapDTO exchangeProductDto = ProductSwapDTO
                    .builder()
                    .productId(exchangeProduct.getId())
                    .productName(exchangeProduct.getName())
                    .thumbnail(exchangeProduct.getThumbnail())
                    .username(exchangeProduct.getProfile().getUser().getLogin())
                    .uid(exchangerProfile.getUid())
                    .phone(exchangerProfile.getPhone())
                    .reciverProfileId(exchangerProfile.getId())
                    .avatarUrl(exchangerProfile.getAvatar())
                    .build();

            ItemSwapDTO itemSwapDto = ItemSwapDTO
                    .builder()
                    .exchangeId(exchange.getId())
                    .ownerConfirm(ownerConfirm)
                    .exchangerConfirm(exchangerConfirm)
                    .exchangeStatus(exchange.getStatus())
                    .swapType(swapType)
                    .myProduct(myProductDto)
                    .exchangeProduct(exchangeProductDto)
                    .chatting(exchange.getChatting())
                    .build();

            itemSwaps.add(itemSwapDto);
        }

        return itemSwaps;
    }

    @Override
    public Long getTotalExchangeByProduct(Long productId) {
        Optional<Product> productOptional = productRepository.findById(productId);
        if (!productOptional.isPresent()) {
            throw new BadRequestException(MessageHelper.getMessage(Message.Keys.E0071, productId), new Throwable());
        }
        Product myProduct = productOptional.get();
        Long totalExchangeByProduct = exchangeRepository.countBySendProductOrReceiveProduct(myProduct, myProduct);
        return totalExchangeByProduct;
    }

    @Override
    public List<MySwapDTO> getMyExchanges(ExchangeStatus exchangeStatus, ExchangeType type, Pageable pageable) {
        // get current profile
        User currentUser = userService.getCurrentUser().get();
        Profile currentProfile = profileRepository.findOneByUser(currentUser).get();
        // get my list products
        List<Product> myProducts = productRepository.findByProfile(currentProfile);

        List<MySwapDTO> mySwapDTOs = new ArrayList<MySwapDTO>();
        List<Exchange> myExchanges = new ArrayList<Exchange>();

        if (myProducts.isEmpty()) {
            return mySwapDTOs;
        }

        // check filter conditions
        if (exchangeStatus == null && type == null) {
            for (Product product : myProducts) {
                // find myExchanges by sendProduct or receiveProduct
                myExchanges = exchangeRepository.findBySendProductOrReceiveProduct(product, product);
                // add first prior myExchange
                mySwapDTOs = appendToSwapList(mySwapDTOs, myExchanges, product);
            }
        } else if (exchangeStatus != null && type == null) {
            for (Product product : myProducts) {
                // find myExchanges by sendProduct or receiveProduct and filter by type
                myExchanges = exchangeRepository.findBySendProductAndStatusOrReceiveProductAndStatus(
                        product,
                        exchangeStatus,
                        product,
                        exchangeStatus);
                mySwapDTOs = appendToSwapList(mySwapDTOs, myExchanges, product);
            }
        } else if (exchangeStatus == null && type != null) {
            switch (type) {
                case SENT:
                    for (Product product : myProducts) {
                        myExchanges = exchangeRepository.findBySendProduct(product);
                        mySwapDTOs = appendToSwapList(mySwapDTOs, myExchanges, product);
                    }
                    break;
                case RECEIVED:
                    for (Product product : myProducts) {
                        myExchanges = exchangeRepository.findByReceiveProduct(product);
                        mySwapDTOs = appendToSwapList(mySwapDTOs, myExchanges, product);
                    }
                    break;
                default:
                    break;
            }
        } else if (exchangeStatus != null && type != null) {
            switch (type) {
                case SENT:
                    for (Product product : myProducts) {
                        myExchanges = exchangeRepository.findBySendProductAndStatus(product, exchangeStatus);
                        mySwapDTOs = appendToSwapList(mySwapDTOs, myExchanges, product);
                    }
                    break;
                case RECEIVED:
                    for (Product product : myProducts) {
                        myExchanges = exchangeRepository.findByReceiveProductAndStatus(product, exchangeStatus);
                        mySwapDTOs = appendToSwapList(mySwapDTOs, myExchanges, product);
                    }
                    break;
                default:
                    break;
            }
        }

        // sort and paging final myExchange return mySwapDtos
        mySwapDTOs = this.sortAndPagingSwapList(mySwapDTOs, pageable);
        return mySwapDTOs;
    }

    public List<MySwapDTO> appendToSwapList(List<MySwapDTO> mySwapDTOs, List<Exchange> exchanges, Product product) {
        if (!exchanges.isEmpty()) {
            MySwapDTO mySwapDTO = this.convertExchangeToSwapDTO(exchanges, product);
            if (mySwapDTO != null) {
                mySwapDTOs.add(mySwapDTO);
            }
        }
        return mySwapDTOs;
    }

    // sort and paging for result
    public List<MySwapDTO> sortAndPagingSwapList(List<MySwapDTO> mySwapDTOs, Pageable pageable) {
        mySwapDTOs.sort((mySwap1, mySwap2) -> mySwap2.getUpdatedDate().compareTo(mySwap1.getUpdatedDate()));
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), mySwapDTOs.size());
        Page<MySwapDTO> pageMySwapDTOs = new PageImpl<>(mySwapDTOs.subList(start, end), pageable, mySwapDTOs.size());
        return pageMySwapDTOs.getContent();
    }

    public MySwapDTO convertExchangeToSwapDTO(List<Exchange> myExchanges, Product product) {
        MySwapDTO mySwapDTO = null;
        for (Exchange exchange : myExchanges) {
            if (exchange.getOwnerConfirm() == ConfirmStatus.CANCEL
                    && exchange.getExchangerConfirm() == ConfirmStatus.CANCEL) {
                // ignore exchange was canceled
                continue;
            }
            if (exchange.getStatus() == ExchangeStatus.SWAPPING) {
                mySwapDTO = new MySwapDTO();
                mySwapDTO.setExchangeId(exchange.getId());
                mySwapDTO.setProductId(product.getId());
                mySwapDTO.setAvatarProduct(product.getThumbnail());
                mySwapDTO.setProductName(product.getName());
                mySwapDTO.setExchangeStatus(exchange.getStatus());
                mySwapDTO.setUpdatedDate(product.getLastModifiedDate());
                break;
            }
            if (mySwapDTO == null) {
                mySwapDTO = new MySwapDTO();
                mySwapDTO.setExchangeId(exchange.getId());
                mySwapDTO.setProductId(product.getId());
                mySwapDTO.setAvatarProduct(product.getThumbnail());
                mySwapDTO.setProductName(product.getName());
                mySwapDTO.setExchangeStatus(exchange.getStatus());
                mySwapDTO.setUpdatedDate(product.getLastModifiedDate());
            }
        }
        return mySwapDTO;
    }
}
