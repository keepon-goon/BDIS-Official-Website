package com.bdis.bdis_website.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;

@Service
public class AdminSortService {

    /** Validates and stores a complete ordering in one transaction. */
    @Transactional(rollbackFor = Exception.class)
    public <T> boolean reorder(
            IService<T> service,
            List<String> orderedIds,
            Function<T, String> idGetter,
            BiFunction<String, Integer, T> sortUpdateFactory) {
        if (orderedIds == null) {
            return false;
        }

        List<T> currentItems = service.list();
        if (currentItems.size() != orderedIds.size()) {
            return false;
        }

        Map<String, T> itemById = new HashMap<>();
        for (T item : currentItems) {
            String id = idGetter.apply(item);
            if (id == null || itemById.put(id, item) != null) {
                return false;
            }
        }

        Set<String> requestedIds = new HashSet<>(orderedIds);
        if (requestedIds.size() != orderedIds.size() || !requestedIds.equals(itemById.keySet())) {
            return false;
        }

        List<T> orderedItems = new ArrayList<>(orderedIds.size());
        for (int index = 0; index < orderedIds.size(); index++) {
            orderedItems.add(sortUpdateFactory.apply(orderedIds.get(index), index));
        }

        if (!service.updateBatchById(orderedItems)) {
            throw new IllegalStateException("批量保存排序失败");
        }
        return true;
    }
}
