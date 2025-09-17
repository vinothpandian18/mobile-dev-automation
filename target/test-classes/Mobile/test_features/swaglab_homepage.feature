Feature: swaglabs home Feature

  @regression @functional
  Scenario: add an item and check cart item increased
    When sign in to the application with valid username and password
    Then verify home page displayed
    When tap on a product to add to a cart
    And click on add to item button
    When get cart count
    And click on continue shopping
    When click on add to cart button on home page
    Then verify cart count increased

  @regression @functional
  Scenario: remove an item and check cart item decreased
    When sign in to the application with valid username and password
    Then verify home page displayed
    When tap on a product to add to a cart
    And click on add to item button
    And click on continue shopping
    When click on add to cart button on home page
    When get cart count
    When click on cart icon
    And click on remove button
    Then verify cart count decreased

  @regression @functional
  Scenario: add filter to the item list
    When sign in to the application with valid username and password
    Then verify home page displayed
    When click on filter option
    And select low to high price option

  @regression
  Scenario: toggle item list view
    When sign in to the application with valid username and password
    Then verify home page displayed
    Then toggle the items view

  @regression
  Scenario: verify menu options are displayed
    When sign in to the application with valid username and password
    Then verify home page displayed
    And verify menu tab is displayed

