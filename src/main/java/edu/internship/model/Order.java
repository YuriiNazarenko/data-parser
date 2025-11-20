package edu.internship.model;

import java.time.LocalDateTime;

class Order {
  private Long id;
  private String orderNumber;
  private LocalDateTime orderDate;
  private String status;
  private Double totalAmount;
  private Address deliveryAddress;
  private String paymentMethod;
  private Long clientId;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
  private String tags;

  public Order(
      Long id,
      String orderNumber,
      LocalDateTime orderDate,
      String status,
      Double totalAmount,
      Address deliveryAddress,
      String paymentMethod,
      Long clientId,
      LocalDateTime createdAt,
      LocalDateTime updatedAt,
      String tags) {
    this.id = id;
    this.orderNumber = orderNumber;
    this.orderDate = orderDate;
    this.status = status;
    this.totalAmount = totalAmount;
    this.deliveryAddress = deliveryAddress;
    this.paymentMethod = paymentMethod;
    this.clientId = clientId;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
    this.tags = tags;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getOrderNumber() {
    return orderNumber;
  }

  public void setOrderNumber(String orderNumber) {
    this.orderNumber = orderNumber;
  }

  public LocalDateTime getOrderDate() {
    return orderDate;
  }

  public void setOrderDate(LocalDateTime orderDate) {
    this.orderDate = orderDate;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public Double getTotalAmount() {
    return totalAmount;
  }

  public void setTotalAmount(Double totalAmount) {
    this.totalAmount = totalAmount;
  }

  public Address getDeliveryAddress() {
    return deliveryAddress;
  }

  public void setDeliveryAddress(Address deliveryAddress) {
    this.deliveryAddress = deliveryAddress;
  }

  public String getPaymentMethod() {
    return paymentMethod;
  }

  public void setPaymentMethod(String paymentMethod) {
    this.paymentMethod = paymentMethod;
  }

  public Long getClientId() {
    return clientId;
  }

  public void setClientId(Long clientId) {
    this.clientId = clientId;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(LocalDateTime createdAt) {
    this.createdAt = createdAt;
  }

  public LocalDateTime getUpdatedAt() {
    return updatedAt;
  }

  public void setUpdatedAt(LocalDateTime updatedAt) {
    this.updatedAt = updatedAt;
  }

  public String getTags() {
    return tags;
  }

  public void setTags(String tags) {
    this.tags = tags;
  }
}
