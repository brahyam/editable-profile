package com.dvipersquad.editableprofile.data.source;

import android.support.annotation.NonNull;

import com.dvipersquad.editableprofile.data.Attribute;

import java.util.List;

public interface AttributesDataSource {

    interface LoadAttributesCallback {

        void onAttributesLoaded(List<Attribute> attributes);

        void onDataNotAvailable(String message);

    }

    interface GetAttributeCallback {

        void onAttributeLoaded(Attribute attribute);

        void onDataNotAvailable(String message);
    }

    void getAttributes(@NonNull LoadAttributesCallback callback);

    void getAttributesByType(@NonNull String type, LoadAttributesCallback callback);

    void getAttribute(@NonNull String attributeId, @NonNull GetAttributeCallback callback);

    void saveAttribute(@NonNull Attribute attribute);

    void deleteAttribute(@NonNull String attributeId);

    void deleteAllAttributes();

}
