Feature: Login feature

  @start_scenario
Scenario Outline: Login with invalid username
When enter user name "<username>"
And  enter the password as "<password>"
And  login
Then should get error message "<err>"
Examples:
| username      | password        | err                                                                     |
| invalidUserName | secret_sauce | Username and password do not match any user in this service. |


@start_scenario
Scenario Outline: Login with invalid password
When enter user name "<username>"
And  enter the password as "<password>"
And  login
Then should get error message "<err>"
Examples:
| username      | password        | err                                                                     |
| standard_user | invalidPassword | Username and password do not match any user in this service. |



  @start_scenario1
  Scenario Outline: Login with amway valid username and password
    When amway enter user name "<username>"
    And  amway enter the password as "<password>"
    Then  amway login
    Examples:
      | username      | password     |
      | standard_user | secret_sauce |
