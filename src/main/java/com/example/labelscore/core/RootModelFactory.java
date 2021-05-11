package com.example.labelscore.core;

import org.springframework.beans.BeanUtils;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.LazyLoader;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.metamodel.ManagedType;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * @Description TODO
 * @Date 2021/4/4 11:08
 * @Created by hdw
 */
public class RootModelFactory {

    private final Map<String, Class<? extends RootModel>> modelCodeClassMapping = new HashMap<>();

    private final EntityManager entityManager;

    private final Map<Class<? extends RootModel>, RootModelSupplier> rootModelSupplierMap = new HashMap<>();

    private final Map<Class<? extends RootModel>, RootModelPreProcessor> rootModelPreProcessorMap = new HashMap<>();

    public RootModelFactory(EntityManager entityManager, List<RootModelSupplier<? extends RootModel>> rootModelSuppliers,
                            List<RootModelPreProcessor<? extends RootModel>> rootModelPreProcessors) {
        this.entityManager = entityManager;
        if (!CollectionUtils.isEmpty(rootModelSuppliers)) {
            rootModelSuppliers.forEach(rootModelSupplier -> {
                Class<? extends RootModel> rootModelClass;
                if (null != rootModelSupplier && (null != (rootModelClass = rootModelSupplier.rootModelClass()))) {
                    rootModelSupplierMap.put(rootModelClass, rootModelSupplier);
                    RootModel rootModel = BeanUtils.instantiate(rootModelClass);
                    modelCodeClassMapping.put(rootModel.getModelCode(), rootModelClass);
                }
            });
        }
        if (!CollectionUtils.isEmpty(rootModelPreProcessors)) {
            rootModelPreProcessors.forEach(rootModelPreProcessor -> {
                if (null != rootModelPreProcessor && null != rootModelPreProcessor.rootModelClass()) {
                    rootModelPreProcessorMap.put(rootModelPreProcessor.rootModelClass(), rootModelPreProcessor);
                }
            });
        }
    }

    @PostConstruct
    public void init() {
        Objects.requireNonNull(entityManager, "entityManager cannot be null");
        Set<ManagedType<?>> managedTypeSet = entityManager.getMetamodel().getManagedTypes();
        if (!CollectionUtils.isEmpty(managedTypeSet)) {
            managedTypeSet.stream().filter(managedType -> RootModel.class.isAssignableFrom(managedType.getJavaType()))
                    .forEach(managedType -> {
                        @SuppressWarnings({"unchecked", "uncast"})
                        Class<? extends RootModel> rootModelClass = (Class<? extends RootModel>) managedType.getJavaType();
                        RootModel rootModel = BeanUtils.instantiate(rootModelClass);
                        modelCodeClassMapping.put(rootModel.getModelCode(), rootModelClass);
                    });
        }
/*        for (ModelType modelType : ModelType.values()) {
            Class<? extends RootModel> clazz;
            if ((clazz = modelCodeClassMapping.get(modelType.getCode())) != null) {
                if (clazz != modelType.getModelClass()) {
                    throw new IllegalStateException(ModelType.class.getCanonicalName() + " instance [" + modelType +
                            "] incorrect, as the detected managed RootModel is not same");
                }
            } else {
                throw new IllegalStateException(ModelType.class.getCanonicalName() + " instance [" + modelType +
                        "] incorrect, as no managed RootModel is detected");
            }
        }*/
    }

    public RootModel createModel(String modelCode, String id) {
        if (!modelCodeClassMapping.containsKey(modelCode)) {
            throw new IllegalArgumentException("cannot find model class for code " + modelCode);
        }
        return createModel(modelCodeClassMapping.get(modelCode), id);
    }

    public RootModel createModel(String modelCode, Object id) {
        if (!modelCodeClassMapping.containsKey(modelCode)) {
            throw new IllegalArgumentException("cannot find model class for code " + modelCode);
        }
        return createModel(modelCodeClassMapping.get(modelCode), id);
    }

    public RootModel createModel(ModelType modelType, Object id) {
        if (null == modelType) {
            throw new IllegalArgumentException("modelType cannot be null");
        }
        return createModel(modelType.getModelClass(), id);
    }

    @SuppressWarnings("unchecked")
    public <T extends RootModel> RootModel createModel(Class<T> clazz, Object id) {
        if (null == id) {
            throw new NullPointerException("id cannot be null when scoring for model");
        }
        if (null == clazz) {
            throw new NullPointerException("clazz cannot be null when scoring for model");
        }
        return (T) Enhancer.create(clazz, new LazyRootModelLoader(id, clazz));
    }

    class LazyRootModelLoader implements LazyLoader {
        final Object id;
        final Class<? extends RootModel> clazz;

        public LazyRootModelLoader(Object id, Class<? extends RootModel> clazz) {
            this.id = id;
            this.clazz = clazz;
        }

        @Override
        @SuppressWarnings("unchecked")
        public RootModel loadObject() throws Exception {
            RootModel rootModel;
            if (rootModelSupplierMap.containsKey(clazz)) {
                rootModel = rootModelSupplierMap.get(clazz).create(id);
            } else {
                rootModel = entityManager.find(clazz, id);
            }
            if (null == rootModel) {
                throw new IllegalArgumentException("cannot find model of [" + clazz + "] for id [" + id + "]");
            }
            if (rootModelPreProcessorMap.containsKey(clazz)) {
                rootModelPreProcessorMap.get(clazz).process(rootModel);
            }
            rootModel._id = id;
            return rootModel;
        }
    }
}
