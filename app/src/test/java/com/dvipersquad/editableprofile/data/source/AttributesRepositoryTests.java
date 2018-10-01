package com.dvipersquad.editableprofile.data.source;

import com.dvipersquad.editableprofile.data.Attribute;
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

public class AttributesRepositoryTests {

    private final static Attribute TEST_ATTRIBUTE1 = new Attribute(
            "1",
            "Test Name 1",
            "Test Type 1"
    );

    private final static Attribute TEST_ATTRIBUTE2 = new Attribute(
            "2",
            "Test Name 2",
            "Test Type 2"
    );

    private final static Attribute TEST_ATTRIBUTE3 = new Attribute(
            "3",
            "Test Name 3",
            "Test Type 3"
    );

    private static List<Attribute> ATTRIBUTES = Lists.newArrayList(TEST_ATTRIBUTE1, TEST_ATTRIBUTE2, TEST_ATTRIBUTE3);


    private AttributesRepository attributesRepository;

    @Mock
    private AttributesDataSource attributesRemoteDataSource;

    @Mock
    private AttributesDataSource attributesLocalDataSource;

    @Mock
    private AttributesDataSource.GetAttributeCallback getAttributeCallback;

    @Mock
    private AttributesDataSource.LoadAttributesCallback loadAttributesCallback;

    @Captor
    private ArgumentCaptor<AttributesDataSource.LoadAttributesCallback> attributesCallbackCaptor;

    @Captor
    private ArgumentCaptor<AttributesDataSource.GetAttributeCallback> attributeCallbackCaptor;

    @Before
    public void setupAttributesRepository() {
        // Mockito has a very convenient way to inject mocks by using the @Mock annotation. To
        // inject the mocks in the test the initMocks method needs to be called.
        MockitoAnnotations.initMocks(this);

        // Get a reference to the class under test
        attributesRepository = new AttributesRepository(attributesRemoteDataSource, attributesLocalDataSource);
    }

    @Test
    public void getAttributes_repositoryCachesAfterFirstApiCall() {
        // Given a setup Captor to capture callbacks
        // When two calls are issued to the attributes repository
        twoAttributesLoadCallsToRepository(loadAttributesCallback);

        // Then attributes were only requested once from Service API
        verify(attributesRemoteDataSource).getAttributes(any(AttributesDataSource.LoadAttributesCallback.class));
    }

    @Test
    public void getAttributes_requestsAllAttributesFromLocalDataSource() {
        // When attributes are requested from the attributes repository
        attributesRepository.getAttributes(loadAttributesCallback);

        // Then attributes are loaded from the local data source
        verify(attributesLocalDataSource).getAttributes(any(AttributesDataSource.LoadAttributesCallback.class));
    }

    @Test
    public void saveAttribute_savesAttributeToLocalDBandCache() {
        // When a attribute is saved to the attributes repository
        attributesRepository.saveAttribute(TEST_ATTRIBUTE1);

        // Then the local DB is called and the cache is updated
        verify(attributesLocalDataSource).saveAttribute(TEST_ATTRIBUTE1);
        assertThat(attributesRepository.cachedAttributes.size(), is(1));
    }

    @Test
    public void getAttribute_requestsSingleAttributeFromLocalDataSource() {
        // When a attribute is requested from the attributes repository
        attributesRepository.getAttribute(TEST_ATTRIBUTE1.getId(), getAttributeCallback);

        // Then the attribute is loaded from the database
        verify(attributesLocalDataSource).getAttribute(eq(TEST_ATTRIBUTE1.getId()), any(
                AttributesDataSource.GetAttributeCallback.class));
    }

    @Test
    public void deleteAllAttributes_deleteAttributesFromLocalDBAndUpdatesCache() {
        // Given 2 stub completed attributes and 1 stub active attributes in the repository
        attributesRepository.saveAttribute(TEST_ATTRIBUTE1);
        attributesRepository.saveAttribute(TEST_ATTRIBUTE2);
        attributesRepository.saveAttribute(TEST_ATTRIBUTE3);

        // When all attributes are deleted to the attributes repository
        attributesRepository.deleteAllAttributes();

        // Verify the data sources were called
        verify(attributesLocalDataSource).deleteAllAttributes();

        assertThat(attributesRepository.cachedAttributes.size(), is(0));
    }

    @Test
    public void deleteAttribute_deleteAttributeToServiceAPIRemovedFromCache() {
        // Given a attribute in the repository
        attributesRepository.saveAttribute(TEST_ATTRIBUTE1);

        // When deleted
        attributesRepository.deleteAttribute(TEST_ATTRIBUTE1.getId());

        // Verify the data sources were called
        verify(attributesLocalDataSource).deleteAttribute(TEST_ATTRIBUTE1.getId());

        // Verify it's removed from repository
        assertThat(attributesRepository.cachedAttributes.containsKey(TEST_ATTRIBUTE1.getId()),
                is(false));
    }

    @Test
    public void getAttributesWithDirtyCache_attributesAreRetrievedFromRemote() {
        // When calling getAttributes in the repository with dirty cache
        attributesRepository.refreshAttributes();
        attributesRepository.getAttributes(loadAttributesCallback);

        // And the remote data source has data available
        setAttributesAvailable(attributesRemoteDataSource, ATTRIBUTES);

        // Verify the attributes from the remote data source are returned, not the local
        verify(attributesLocalDataSource, never()).getAttributes(loadAttributesCallback);
        verify(loadAttributesCallback).onAttributesLoaded(ATTRIBUTES);
    }

    @Test
    public void getAttributesWithLocalDataSourceUnavailable_attributesAreRetrievedFromRemote() {
        // When calling getAttributes in the repository
        attributesRepository.getAttributes(loadAttributesCallback);

        // And the local data source has no data available
        setAttributesNotAvailable(attributesLocalDataSource);

        // And the remote data source has data available
        setAttributesAvailable(attributesRemoteDataSource, ATTRIBUTES);

        // Verify the attributes from the local data source are returned
        verify(loadAttributesCallback).onAttributesLoaded(ATTRIBUTES);
    }

    @Test
    public void getAttributesWithBothDataSourcesUnavailable_firesOnDataUnavailable() {
        // When calling getAttributes in the repository
        attributesRepository.getAttributes(loadAttributesCallback);

        // And the local data source has no data available
        setAttributesNotAvailable(attributesLocalDataSource);

        // And the remote data source has no data available
        setAttributesNotAvailable(attributesRemoteDataSource);

        // Verify no data is returned
        verify(loadAttributesCallback).onDataNotAvailable(any(String.class));
    }

    @Test
    public void getAttributeWithLocalDataSourcesUnavailable_firesOnDataUnavailable() {
        // When calling getAttribute in the repository
        attributesRepository.getAttribute(TEST_ATTRIBUTE1.getId(), getAttributeCallback);

        // And the local data source has no data available
        setAttributeNotAvailable(attributesLocalDataSource, TEST_ATTRIBUTE1.getId());

        // Verify no data is returned
        verify(getAttributeCallback).onDataNotAvailable(any(String.class));
    }

    @Test
    public void getAttributes_refreshesLocalDataSource() {
        // Mark cache as dirty to force a reload of data from remote data source.
        attributesRepository.refreshAttributes();

        // When calling getAttributes in the repository
        attributesRepository.getAttributes(loadAttributesCallback);

        // Make the remote data source return data
        setAttributesAvailable(attributesRemoteDataSource, ATTRIBUTES);

        // Verify that the data fetched from the remote data source was saved in local.
        verify(attributesLocalDataSource, times(ATTRIBUTES.size())).saveAttribute(any(Attribute.class));
    }

    /**
     * Convenience method that issues two calls to the attributes repository
     */
    private void twoAttributesLoadCallsToRepository(AttributesDataSource.LoadAttributesCallback callback) {
        // When attributes are requested from repository
        attributesRepository.getAttributes(callback); // First call to API

        // Use the Mockito Captor to capture the callback
        verify(attributesLocalDataSource).getAttributes(attributesCallbackCaptor.capture());

        // Local data source doesn't have data yet
        attributesCallbackCaptor.getValue().onDataNotAvailable(any(String.class));


        // Verify the remote data source is queried
        verify(attributesRemoteDataSource).getAttributes(attributesCallbackCaptor.capture());

        // Trigger callback so attributes are cached
        attributesCallbackCaptor.getValue().onAttributesLoaded(ATTRIBUTES);

        attributesRepository.getAttributes(callback); // Second call to API
    }

    private void setAttributesNotAvailable(AttributesDataSource dataSource) {
        verify(dataSource).getAttributes(attributesCallbackCaptor.capture());
        attributesCallbackCaptor.getValue().onDataNotAvailable(any(String.class));
    }

    private void setAttributesAvailable(AttributesDataSource dataSource, List<Attribute> attributes) {
        verify(dataSource).getAttributes(attributesCallbackCaptor.capture());
        attributesCallbackCaptor.getValue().onAttributesLoaded(attributes);
    }

    private void setAttributeNotAvailable(AttributesDataSource dataSource, String attributeId) {
        verify(dataSource).getAttribute(eq(attributeId), attributeCallbackCaptor.capture());
        attributeCallbackCaptor.getValue().onDataNotAvailable(any(String.class));
    }

}
