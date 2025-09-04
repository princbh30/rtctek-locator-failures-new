
@rtctek
Feature: RTCtek - Demonstrate failing locators across strategies
  As a tester
  I want to run scenarios that use broken locators
  So that I can observe and practice debugging locator failures

  Background:
    Given I open the RTCtek homepage

  @bad_id
  Scenario: Broken ID locator
    When I try to locate by "id" with value "definitely-not-an-id"
    Then the step should fail due to locator issue

  @bad_name
  Scenario: Broken NAME locator
    When I try to locate by "name" with value "notARealNameField"
    Then the step should fail due to locator issue

  @bad_classname
  Scenario: Broken CLASSNAME locator
    When I try to locate by "className" with value "this-class-does-not-exist-xyz"
    Then the step should fail due to locator issue

  @bad_css_1
  Scenario: Broken CSS locator - attribute
    When I try to locate by "css" with value "a[aria-label='non-existent-label']"
    Then the step should fail due to locator issue

  @bad_css_2
  Scenario: Broken CSS locator - nth-child
    When I try to locate by "css" with value "header nav ul li:nth-child(99) a"
    Then the step should fail due to locator issue

  @bad_xpath_1
  Scenario: Broken XPATH - wrong id
    When I try to locate by "xpath" with value "//*[@id='made-up-id-123']/a"
    Then the step should fail due to locator issue

  @bad_xpath_2
  Scenario: Broken XPATH - brittle index
    When I try to locate by "xpath" with value "(//a[contains(@href,'contact')])[50]"
    Then the step should fail due to locator issue

  @bad_linktext
  Scenario: Broken LINKTEXT locator
    When I try to locate by "linkText" with value "Careers at RTCtek That Probably Doesn't Exist"
    Then the step should fail due to locator issue

  @bad_partiallinktext
  Scenario: Broken PARTIALLINKTEXT locator
    When I try to locate by "partialLinkText" with value "ThisPartialTextIsWrong"
    Then the step should fail due to locator issue

  @bad_tagname
  Scenario: Broken TAGNAME locator
    When I try to locate by "tagName" with value "nonexistenttagname"
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
    When I try to locate by "xpath" with value "//h2[contains(text(),'Our Services')]"
    Then the element should be found successfully

  @good_linktext
  Scenario: Valid LINKTEXT locator
    When I try to locate by "linkText" with value "Contact"
    Then the element should be found successfully

