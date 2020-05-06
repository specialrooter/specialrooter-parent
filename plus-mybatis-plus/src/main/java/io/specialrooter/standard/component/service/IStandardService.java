package io.specialrooter.standard.component.service;

import java.util.Collection;

public interface IStandardService {
   boolean saveBatchPlus(Collection entityList, Class clazz);
   boolean saveBatchPlus(Collection entityList, Class clazz,String datasource);
}
