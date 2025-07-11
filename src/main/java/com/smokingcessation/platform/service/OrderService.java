package com.smokingcessation.platform.service;

import com.smokingcessation.platform.config.Util;
import com.smokingcessation.platform.dto.CreateOrderRequestDTO;
import com.smokingcessation.platform.dto.PackageOrderItemDTO;
import com.smokingcessation.platform.entity.OrderModel;
import com.smokingcessation.platform.entity.PackageModel;
import com.smokingcessation.platform.entity.User;
import com.smokingcessation.platform.entity.UserPackageModel;
import com.smokingcessation.platform.enums.OrderStatus;
import com.smokingcessation.platform.enums.OrderType;
import com.smokingcessation.platform.repository.IOrderRepository;
import com.smokingcessation.platform.repository.IPackageRepository;
import com.smokingcessation.platform.repository.IUserPackageRepository;
import com.smokingcessation.platform.repository.UserRepository;
import org.apache.coyote.BadRequestException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.payos.PayOS;
import vn.payos.type.CheckoutResponseData;
import vn.payos.type.ItemData;
import vn.payos.type.PaymentData;
import vn.payos.type.WebhookData;

import java.util.Optional;

@Service
public class OrderService {

    @Autowired
    private IOrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private IUserPackageRepository userPackageRepository;

    @Autowired
    private IPackageRepository packageRepository;

    @Autowired
    private PayOS payOS;

    @Autowired
    private ModelMapper mapper;

    @Transactional
    public CheckoutResponseData createOrder(CreateOrderRequestDTO dto) throws Exception {
        Optional<User> userOpt = userRepository.findById(dto.getUserId());
        if (userOpt.isEmpty()) {
            throw new BadRequestException("User not found");
        }

        Optional<PackageModel> pkgOpt = packageRepository.findById(dto.getPackageId());
        if (pkgOpt.isEmpty()) {
            throw new BadRequestException("Package not found");
        }

        User user = userOpt.get();
        PackageModel pkg = pkgOpt.get();
        PackageOrderItemDTO packageResponseDTO = mapper.map(pkg, PackageOrderItemDTO.class);
        String json = Util.toJson(packageResponseDTO);
        long code = Util.generateOrderCode();

        // Tạo OrderModel
        OrderModel orderModel = new OrderModel();
        orderModel.setUser(user);
        orderModel.setCode(code);
        orderModel.setItem(json);
        orderModel.setAmount(pkg.getSalePrice());
        orderModel.setOrderStatus(
                pkg.getSalePrice() == 0 ? OrderStatus.SUCCESS : OrderStatus.PENDING
        );

        int amount = (int) pkg.getSalePrice();
        ItemData itemData = ItemData.builder()
                .name(pkg.getName())
                .quantity(1)
                .price(amount)
                .build();

        PaymentData paymentData = PaymentData.builder()
                .orderCode(code)
                .amount(amount)
                .description("TXN" + code)
                .returnUrl("http://localhost:3000/")
                .cancelUrl("http://localhost:3000/")
                .item(itemData)
                .build();

        CheckoutResponseData result = payOS.createPaymentLink(paymentData);

        orderModel.setPaymentLinkId(result.getPaymentLinkId());
        orderModel.setCheckoutUrl(result.getCheckoutUrl());
        orderModel.setQrCode(result.getQrCode());

        // Ghi thêm thông tin UserPackage
        UserPackageModel userPackageModel = handleAddUserPackage(orderModel, pkg, user);
        orderModel.setUserPackage(userPackageModel);

        orderRepository.save(orderModel);
        return result;
    }

    /**
     * Xử lý webhook từ PayOS
     */
    public boolean handlePayOSWebhook(WebhookData data) throws Exception {
        OrderModel order = orderRepository.findByCode(data.getOrderCode());
        if (order == null
                || order.getOrderStatus() == OrderStatus.SUCCESS
                || order.getAmount() != data.getAmount()) {
            return false;
        }

        if ("00".equals(data.getCode())) {
            order.setOrderStatus(OrderStatus.SUCCESS);
                processOrderPackage(order);
        } else {
            order.setOrderStatus(OrderStatus.FAILED);
        }

        orderRepository.save(order);
        return true;
    }

    public Page<OrderModel> getMyOrders(Long userId, Pageable pageable) {
        return orderRepository.findAllByUserId(userId, pageable);
    }

    public OrderModel getOrderById(Long orderId, Long userId) throws BadRequestException {
        var existingOrder = orderRepository.findById(orderId);
        if (existingOrder.isEmpty()) {
            throw new BadRequestException("Order not found");
        }
        if (!existingOrder.get().getUser().getId().equals(userId)) {
            throw new BadRequestException("You do not have permission to view this order");
        }
        return existingOrder.get();
    }

    public boolean processOrderPackage(OrderModel order) throws Exception {
        var userPkgOpt = userPackageRepository.findById(order.getUserPackage().getId());
        if (userPkgOpt.isEmpty()) {
            throw new BadRequestException("User package not found");
        }
        UserPackageModel userPkg = userPkgOpt.get();
        userPkg.setActive(true);
        userPackageRepository.save(userPkg);
        return true;
    }

    public boolean processOrderRecharge(OrderModel order) {
        // TODO: xử lý nạp tiền
        return true;
    }

    @Transactional
    public UserPackageModel handleAddUserPackage(OrderModel orderModel,
                                                 PackageModel packageModel,
                                                 User user) {
        UserPackageModel userPackage = new UserPackageModel();
        userPackage.setPackageModel(packageModel);
        userPackage.setAuthor(user);
        userPackage.setOrder(orderModel);
        userPackage.setActive(false);
        return userPackageRepository.save(userPackage);
    }

}
