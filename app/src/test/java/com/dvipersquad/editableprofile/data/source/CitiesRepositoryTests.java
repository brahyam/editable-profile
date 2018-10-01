package com.dvipersquad.editableprofile.data.source;

import com.dvipersquad.editableprofile.data.City;
import com.dvipersquad.editableprofile.data.LocationCoordinate;
import com.google.common.collect.Lists;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.internal.verification.VerificationModeFactory.times;

public class CitiesRepositoryTests {

    private final static City TEST_CITY1 = new City(
            "Test Name 1",
            "Test Lat 1",
            "Test Lon 1"
    );

    private final static City TEST_CITY2 = new City(
            "Test Name 2",
            "Test Lat 2",
            "Test Lon 2"
    );

    private final static City TEST_CITY3 = new City(
            "Test Name 3",
            "Test Lat 3",
            "Test Lon 3"
    );

    private static List<City> CITIES = Lists.newArrayList(TEST_CITY1, TEST_CITY2, TEST_CITY3);


    private CitiesRepository citiesRepository;

    @Mock
    private CitiesDataSource citiesRemoteDataSource;

    @Mock
    private CitiesDataSource citiesLocalDataSource;

    @Mock
    private CitiesDataSource.GetCityCallback getCityCallback;

    @Mock
    private CitiesDataSource.LoadCitiesCallback loadCitiesCallback;

    @Captor
    private ArgumentCaptor<CitiesDataSource.LoadCitiesCallback> citiesCallbackCaptor;

    @Captor
    private ArgumentCaptor<CitiesDataSource.GetCityCallback> cityCallbackCaptor;

    @Before
    public void setupCitiesRepository() {
        // Mockito has a very convenient way to inject mocks by using the @Mock annotation. To
        // inject the mocks in the test the initMocks method needs to be called.
        MockitoAnnotations.initMocks(this);

        // Get a reference to the class under test
        citiesRepository = new CitiesRepository(citiesRemoteDataSource, citiesLocalDataSource);
    }

    @Test
    public void getCities_repositoryCachesAfterFirstApiCall() {
        // Given a setup Captor to capture callbacks
        // When two calls are issued to the cities repository
        twoCitiesLoadCallsToRepository(loadCitiesCallback);

        // Then cities were only requested once from Service API
        verify(citiesRemoteDataSource).getCities(any(CitiesDataSource.LoadCitiesCallback.class));
    }

    @Test
    public void getCities_requestsAllCitiesFromLocalDataSource() {
        // When cities are requested from the cities repository
        citiesRepository.getCities(loadCitiesCallback);

        // Then cities are loaded from the local data source
        verify(citiesLocalDataSource).getCities(any(CitiesDataSource.LoadCitiesCallback.class));
    }

    @Test
    public void saveCity_savesCityToLocalDBandCache() {
        // When a city is saved to the cities repository
        citiesRepository.saveCity(TEST_CITY1);

        // Then the local DB is called and the cache is updated
        verify(citiesLocalDataSource).saveCity(TEST_CITY1);
        assertThat(citiesRepository.cachedCities.size(), is(1));
    }

    @Test
    public void getCity_requestsSingleCityFromLocalDataSource() {
        // When a city is requested from the cities repository
        citiesRepository.getCity(TEST_CITY1.getLatitude(), TEST_CITY1.getLongitude(), getCityCallback);

        // Then the city is loaded from the database
        verify(citiesLocalDataSource).getCity(eq(TEST_CITY1.getLatitude()), eq(TEST_CITY1.getLongitude()), any(
                CitiesDataSource.GetCityCallback.class));
    }

    @Test
    public void deleteAllCities_deleteCitiesFromLocalDBAndUpdatesCache() {
        // Given 2 stub completed cities and 1 stub active cities in the repository
        citiesRepository.saveCity(TEST_CITY1);
        citiesRepository.saveCity(TEST_CITY2);
        citiesRepository.saveCity(TEST_CITY3);

        // When all cities are deleted to the cities repository
        citiesRepository.deleteAllCities();

        // Verify the data sources were called
        verify(citiesLocalDataSource).deleteAllCities();

        assertThat(citiesRepository.cachedCities.size(), is(0));
    }

    @Test
    public void deleteCity_deleteCityToServiceAPIRemovedFromCache() {
        // Given a city in the repository
        citiesRepository.saveCity(TEST_CITY1);
        LocationCoordinate locationCoordinate = new LocationCoordinate(TEST_CITY1.getLatitude(), TEST_CITY1.getLongitude());

        // When deleted
        citiesRepository.deleteCity(TEST_CITY1.getLatitude(), TEST_CITY1.getLongitude());

        // Verify the data sources were called
        verify(citiesLocalDataSource).deleteCity(TEST_CITY1.getLatitude(), TEST_CITY1.getLongitude());

        // Verify it's removed from repository
        assertThat(citiesRepository.cachedCities.containsKey(
                new LocationCoordinate(TEST_CITY1.getLatitude(), TEST_CITY1.getLongitude())),
                is(false));
    }

    @Test
    public void getCitiesWithDirtyCache_citiesAreRetrievedFromRemote() {
        // When calling getCities in the repository with dirty cache
        citiesRepository.refreshCities();
        citiesRepository.getCities(loadCitiesCallback);

        // And the remote data source has data available
        setCitiesAvailable(citiesRemoteDataSource, CITIES);

        // Verify the cities from the remote data source are returned, not the local
        verify(citiesLocalDataSource, never()).getCities(loadCitiesCallback);
        verify(loadCitiesCallback).onCitiesLoaded(CITIES);
    }

    @Test
    public void getCitiesWithLocalDataSourceUnavailable_citiesAreRetrievedFromRemote() {
        // When calling getCities in the repository
        citiesRepository.getCities(loadCitiesCallback);

        // And the local data source has no data available
        setCitiesNotAvailable(citiesLocalDataSource);

        // And the remote data source has data available
        setCitiesAvailable(citiesRemoteDataSource, CITIES);

        // Verify the cities from the local data source are returned
        verify(loadCitiesCallback).onCitiesLoaded(CITIES);
    }

    @Test
    public void getCitiesWithBothDataSourcesUnavailable_firesOnDataUnavailable() {
        // When calling getCities in the repository
        citiesRepository.getCities(loadCitiesCallback);

        // And the local data source has no data available
        setCitiesNotAvailable(citiesLocalDataSource);

        // And the remote data source has no data available
        setCitiesNotAvailable(citiesRemoteDataSource);

        // Verify no data is returned
        verify(loadCitiesCallback).onDataNotAvailable(any(String.class));
    }

    @Test
    public void getCityWithLocalDataSourcesUnavailable_firesOnDataUnavailable() {
        // When calling getCity in the repository
        citiesRepository.getCity(TEST_CITY1.getLatitude(), TEST_CITY1.getLongitude(), getCityCallback);

        // And the local data source has no data available
        setCityNotAvailable(citiesLocalDataSource, TEST_CITY1.getLatitude(), TEST_CITY1.getLongitude());

        // Verify no data is returned
        verify(getCityCallback).onDataNotAvailable(any(String.class));
    }

    @Test
    public void getCities_refreshesLocalDataSource() {
        // Mark cache as dirty to force a reload of data from remote data source.
        citiesRepository.refreshCities();

        // When calling getCities in the repository
        citiesRepository.getCities(loadCitiesCallback);

        // Make the remote data source return data
        setCitiesAvailable(citiesRemoteDataSource, CITIES);

        // Verify that the data fetched from the remote data source was saved in local.
        verify(citiesLocalDataSource, times(CITIES.size())).saveCity(any(City.class));
    }

    /**
     * Convenience method that issues two calls to the cities repository
     */
    private void twoCitiesLoadCallsToRepository(CitiesDataSource.LoadCitiesCallback callback) {
        // When cities are requested from repository
        citiesRepository.getCities(callback); // First call to API

        // Use the Mockito Captor to capture the callback
        verify(citiesLocalDataSource).getCities(citiesCallbackCaptor.capture());

        // Local data source doesn't have data yet
        citiesCallbackCaptor.getValue().onDataNotAvailable(any(String.class));


        // Verify the remote data source is queried
        verify(citiesRemoteDataSource).getCities(citiesCallbackCaptor.capture());

        // Trigger callback so cities are cached
        citiesCallbackCaptor.getValue().onCitiesLoaded(CITIES);

        citiesRepository.getCities(callback); // Second call to API
    }

    private void setCitiesNotAvailable(CitiesDataSource dataSource) {
        verify(dataSource).getCities(citiesCallbackCaptor.capture());
        citiesCallbackCaptor.getValue().onDataNotAvailable(any(String.class));
    }

    private void setCitiesAvailable(CitiesDataSource dataSource, List<City> cities) {
        verify(dataSource).getCities(citiesCallbackCaptor.capture());
        citiesCallbackCaptor.getValue().onCitiesLoaded(cities);
    }

    private void setCityNotAvailable(CitiesDataSource dataSource, String latitude, String longitude) {
        verify(dataSource).getCity(eq(latitude), eq(longitude), cityCallbackCaptor.capture());
        cityCallbackCaptor.getValue().onDataNotAvailable(any(String.class));
    }
}
