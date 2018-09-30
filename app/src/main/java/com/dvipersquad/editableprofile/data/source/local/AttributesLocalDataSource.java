package com.dvipersquad.editableprofile.data.source.local;

import android.support.annotation.NonNull;

import com.dvipersquad.editableprofile.data.Attribute;
import com.dvipersquad.editableprofile.data.source.AttributesDataSource;
import com.dvipersquad.editableprofile.utils.AppExecutors;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class AttributesLocalDataSource implements AttributesDataSource {

    private final ProfilesDao dao;
    private final AppExecutors appExecutors;

    @Inject
    public AttributesLocalDataSource(@NonNull AppExecutors executors, @NonNull ProfilesDao dao) {
        this.dao = dao;
        this.appExecutors = executors;
    }

    @Override
    public void getAttributes(@NonNull final GetAttributesCallback callback) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                final List<Attribute> attributes = dao.getAttributes();
                appExecutors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        if (attributes.isEmpty()) {
                            // This will be called if the table is new or just empty.
                            callback.onDataNotAvailable("Not found");
                        } else {
                            callback.onAttributesLoaded(attributes);
                        }
                    }
                });
            }
        };
        appExecutors.diskIO().execute(runnable);
    }

    @Override
    public void getAttributesByType(@NonNull final String type, final GetAttributesCallback callback) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                final List<Attribute> attributes = dao.getAttributesByType(type);
                appExecutors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        if (attributes.isEmpty()) {
                            // This will be called if the table is new or just empty.
                            callback.onDataNotAvailable("Not found");
                        } else {
                            callback.onAttributesLoaded(attributes);
                        }
                    }
                });
            }
        };
        appExecutors.diskIO().execute(runnable);
    }

    @Override
    public void getAttribute(@NonNull final String attributeId, @NonNull final GetAttributeCallback callback) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                final Attribute attribute = dao.getAttributeById(attributeId);

                appExecutors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        if (attribute != null) {
                            callback.onAttributeLoaded(attribute);
                        } else {
                            callback.onDataNotAvailable("Not found");
                        }
                    }
                });
            }
        };
        appExecutors.diskIO().execute(runnable);
    }

    @Override
    public void saveAttribute(@NonNull final Attribute attribute) {
        Runnable saveRunnable = new Runnable() {
            @Override
            public void run() {
                dao.insertAttribute(attribute);
            }
        };
        appExecutors.diskIO().execute(saveRunnable);
    }

    @Override
    public void deleteAttribute(@NonNull final String attributeId) {
        Runnable saveRunnable = new Runnable() {
            @Override
            public void run() {
                dao.deleteAttributeById(attributeId);
            }
        };
        appExecutors.diskIO().execute(saveRunnable);
    }

    @Override
    public void deleteAllAttributes() {
        Runnable saveRunnable = new Runnable() {
            @Override
            public void run() {
                dao.deleteAttributes();
            }
        };
        appExecutors.diskIO().execute(saveRunnable);
    }
}
