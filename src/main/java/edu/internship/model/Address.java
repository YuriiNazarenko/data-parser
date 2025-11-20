package edu.internship.model;

public class Address {
  private String city;
  private String street;
  private int houseNumber;

  public Address(String city, String street, int houseNumber) {
    this.city = city;
    this.street = street;
    this.houseNumber = houseNumber;
  }

  public String getCity() {
    return city;
  }

  public String getStreet() {
    return street;
  }

  public int getHouseNumber() {
    return houseNumber;
  }

  public void setCity(String city) {
    this.city = city;
  }

  public void setStreet(String street) {
    this.street = street;
  }

  public void setHouseNumber(int houseNumber) {
    this.houseNumber = houseNumber;
  }
}
