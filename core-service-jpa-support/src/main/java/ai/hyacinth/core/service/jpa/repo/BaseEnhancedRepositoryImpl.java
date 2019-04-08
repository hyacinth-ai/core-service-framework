package ai.hyacinth.core.service.jpa.repo;

import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class BaseEnhancedRepositoryImpl<T, K> implements BaseEnhancedRepository<T, K> {
  @Autowired private EntityManager entityManager;

  private Class<T> entityClass;

  public BaseEnhancedRepositoryImpl(Class<T> entityClass) {
    this.entityClass = entityClass;
  }

  public BaseEnhancedRepositoryImpl() {
    this.entityClass = probeGenericInterfaceEntityType();
  }

  @SuppressWarnings("unchecked")
  private Class<T> probeGenericInterfaceEntityType() {
    Type[] genericInterfaces =
        ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments();
    return (Class<T>) genericInterfaces[0];
  }

  @Override
  public T lockAndRefresh(K id) {
    T pe = entityManager.find(entityClass, id, LockModeType.PESSIMISTIC_WRITE, null);
    entityManager.refresh(pe);
    return pe;
  }
}
