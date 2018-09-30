package com.dvipersquad.editableprofile.data.source;

import android.support.annotation.NonNull;

import com.dvipersquad.editableprofile.data.Attribute;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class AttributesRepository implements AttributesDataSource {

    private final AttributesDataSource attributesRemoteDataSource;

    private final AttributesDataSource attributesLocalDataSource;

    Map<String, Attribute> cachedAttributes;

    boolean attributeCacheIsDirty = false;


    @Inject
    AttributesRepository(@Remote AttributesDataSource attributesRemoteDataSource, @Local AttributesDataSource attributesLocalDataSource) {
        this.attributesRemoteDataSource = attributesRemoteDataSource;
        this.attributesLocalDataSource = attributesLocalDataSource;
    }

    @Override
    public void getAttributes(@NonNull final GetAttributesCallback callback) {
        if (cachedAttributes != null && !attributeCacheIsDirty) {
            callback.onAttributesLoaded(new ArrayList<>(cachedAttributes.values()));
            return;
        }

        if (attributeCacheIsDirty) {
            getAttributesFromRemoteDataSource(callback);
        } else {
            attributesLocalDataSource.getAttributes(new GetAttributesCallback() {
                @Override
                public void onAttributesLoaded(List<Attribute> attributes) {
                    refreshAttributesCache(attributes);
                    callback.onAttributesLoaded(new ArrayList<>(cachedAttributes.values()));
                }

                @Override
                public void onDataNotAvailable(String message) {
                    getAttributesFromRemoteDataSource(callback);
                }
            });
        }
    }

    private void refreshAttributesCache(List<Attribute> attributes) {
        if (cachedAttributes == null) {
            cachedAttributes = new LinkedHashMap<>();
        }
        cachedAttributes.clear();
        for (Attribute attribute : attributes) {
            cachedAttributes.put(attribute.getId(), attribute);
        }
        attributeCacheIsDirty = false;
    }

    private void getAttributesFromRemoteDataSource(final GetAttributesCallback callback) {
        attributesRemoteDataSource.getAttributes(new GetAttributesCallback() {
            @Override
            public void onAttributesLoaded(List<Attribute> attributes) {
                refreshAttributesCache(attributes);
                refreshAttributesLocalDataSource(attributes);
                callback.onAttributesLoaded(new ArrayList<>(cachedAttributes.values()));
            }

            @Override
            public void onDataNotAvailable(String message) {
                callback.onDataNotAvailable(message);
            }
        });
    }

    private void refreshAttributesLocalDataSource(List<Attribute> attributes) {
        attributesLocalDataSource.deleteAllAttributes();
        for (Attribute attribute : attributes) {
            attributesLocalDataSource.saveAttribute(attribute);
        }
    }

    @Override
    public void getAttributesByType(@NonNull String type, final GetAttributesCallback callback) {
        if (cachedAttributes != null && !attributeCacheIsDirty) {
            List<Attribute> filteredAttributes = new ArrayList<>();
            for (Attribute attribute : cachedAttributes.values()) {
                if (attribute.getType().equals(type)) {
                    filteredAttributes.add(attribute);
                }
            }
            callback.onAttributesLoaded(new ArrayList<>(filteredAttributes));
            return;
        }

        // Do not refresh cached because new ones are filtered
        attributesLocalDataSource.getAttributesByType(type, callback);
    }

    @Override
    public void getAttribute(@NonNull String attributeId, @NonNull final GetAttributeCallback callback) {
        // Try cache first
        if (cachedAttributes != null && !cachedAttributes.isEmpty()) {
            final Attribute cachedAttribute = cachedAttributes.get(attributeId);
            if (cachedAttribute != null) {
                callback.onAttributeLoaded(cachedAttribute);
                return;
            }
        }

        // Try local source
        attributesLocalDataSource.getAttribute(attributeId, new GetAttributeCallback() {
            @Override
            public void onAttributeLoaded(Attribute attribute) {
                if (cachedAttributes == null) {
                    cachedAttributes = new LinkedHashMap<>();
                }
                cachedAttributes.put(attribute.getId(), attribute);
                callback.onAttributeLoaded(attribute);
            }

            @Override
            public void onDataNotAvailable(String message) {
                // Get individual attribute not implemented by remote source
                // so dont try to fetch from server
                callback.onDataNotAvailable(message);
            }
        });
    }

    @Override
    public void saveAttribute(@NonNull Attribute attribute) {
        attributesLocalDataSource.saveAttribute(attribute);
        if (cachedAttributes == null) {
            cachedAttributes = new LinkedHashMap<>();
        }
        cachedAttributes.put(attribute.getId(), attribute);
    }

    @Override
    public void deleteAttribute(@NonNull String attributeId) {
        attributesLocalDataSource.deleteAttribute(attributeId);
        cachedAttributes.remove(attributeId);
    }

    @Override
    public void deleteAllAttributes() {
        attributesLocalDataSource.deleteAllAttributes();
        if (cachedAttributes == null) {
            cachedAttributes = new LinkedHashMap<>();
        }
        cachedAttributes.clear();
    }
}
