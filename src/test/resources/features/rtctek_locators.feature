
@rtctek
Feature: RTCtek - Demonstrate failing locators across strategies
  As a tester
  I want to run scenarios that use broken locators
  So that I can observe and practice debugging locator failures

  Background:
    Given I open the RTCtek homepage

  @bad_id
  Scenario: Broken ID locator
    When I try to locate by "id" with value "site-navigatio"
    Then the step should fail due to locator issue

  @bad_name
  Scenario: Broken NAME locator
    When I try to locate by "name" with value "s"
    Then the step should fail due to locator issue

  @bad_classname
  Scenario: Broken CLASSNAME locator
    When I try to locate by "className" with value "elementor-heading-titl"
    Then the step should fail due to locator issue

  @bad_css_1
  Scenario: Broken CSS locator - attribute
    When I try to locate by "css" with value "a[href*='linkedin']"
    Then the step should fail due to locator issue

  @bad_css_2
  Scenario: Broken CSS locator - nth-child
    When I try to locate by "css" with value "ul#menu-main-menu li:nth-child(2) a"
    Then the step should fail due to locator issue

  @bad_xpath_1
  Scenario: Broken XPATH - wrong id
    When I try to locate by "xpath" with value "//*[@id='menu-item-4711']/a"
    Then the step should fail due to locator issue

  @bad_xpath_2
  Scenario: Broken XPATH - brittle index
    When I try to locate by "xpath" with value "(//a[contains(@href,'linkedin.com')])[1]"
    Then the step should fail due to locator issue

  @bad_linktext
  Scenario: Broken LINKTEXT locator
    When I try to locate by "linkText" with value "Contact U"
    Then the step should fail due to locator issue

  @bad_partiallinktext
  Scenario: Broken PARTIALLINKTEXT locator
    When I try to locate by "partialLinkText" with value "Contact"
    Then the step should fail due to locator issue

  @bad_tagname
  Scenario: Broken TAGNAME locator
    When I try to locate by "tagName" with value "header"
    Then the step should fail due to locator issue

  @good_id
  Scenario: Valid ID locator
    When I try to locate by "id" with value "site-navigation"
    Then the element should be found successfully

  @good_classname
  Scenario: Valid CLASSNAME locator
    When I try to locate by "className" with value "elementor-heading-title"
    Then the element should be found successfully

  @good_css
  Scenario: Valid CSS locator
    When I try to locate by "css" with value "a[href='/contact/']"
    Then the element should be found successfully

  @good_xpath
  Scenario: Valid XPATH locator
    When I try to locate by "xpath" with value "//h2[contains(text(),'Services')]"
    Then the element should be found successfully

  @good_linktext
  Scenario: Valid LINKTEXT locator
    When I try to locate by "linkText" with value "Contact Us"
    Then the element should be found successfully

