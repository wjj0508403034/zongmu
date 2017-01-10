package com.zongmu.service.util;

import java.util.ArrayList;
import java.util.List;

import org.dozer.DozerBeanMapper;
import org.dozer.Mapper;
import org.dozer.MappingException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class MapperService {

    private Mapper mapper = new DozerBeanMapper();

    public <T> T map(Object source, Class<T> destinationClass) throws MappingException {
        return this.mapper.map(source, destinationClass);
    }

    public <T, K> List<T> mapList(List<K> source, Class<T> destinationClass) {
        List<T> result = new ArrayList<T>();
        for (K item : source) {
            result.add(this.mapper.map(item, destinationClass));
        }
        return result;
    }

    public <T, K> Page<T> mapPage(Page<K> source, Class<T> destinationClass) {
        List<T> content = this.mapList(source.getContent(), destinationClass);
        Pageable pageable = new PageRequest(source.getNumber(), source.getSize());
        return new PageImpl<T>(content, pageable, source.getTotalPages());
    }

    public <T> List<T> toList(Iterable<T> dataset) {
        List<T> result = new ArrayList<T>();
        for (T it : dataset) {
            result.add(it);
        }
        return result;
    }

}
