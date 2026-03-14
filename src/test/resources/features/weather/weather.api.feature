@api
Feature: Current Weather Data

	@AC1 @cityids
  Scenario: AC1_Retrieve current weather for multiple international cities
    Given I have a list of major international City IDs:
      | city_id | city_name |
      | 2147714 | Sydney    |
      | 5128581 | New York City |
      | 2643743 | London    |
      | 1850147 | Tokyo     |
    When I request the current weather from Weatherbit
    Then the response should contain weather data for each city
    
    @AC2 @coordinates
  Scenario: AC2_Retrieve current weather  using latitude and longitude
    When I fetch weather data for coordinates Latitude -33.865143 and Longitude 151.209900
    Then the response should contain valid data for these coordinates
    
    @AC3 @warmest
  Scenario: AC3_Identify the warmest Australian capital city from local data
    When I fetch current weather for all cities in "ausCapCities.json"
    Then I should identify and report the warmest city among them

  @AC4 @coldest
  Scenario: AC4_Identify the coldest US State from metadata
    When I identify the coldest US state using the metadata in "us_cities.csv"
    Then the system should report the state with the lowest current temperature