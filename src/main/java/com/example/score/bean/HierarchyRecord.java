package com.example.score.bean;

import com.example.score.service.ScoreTreeLevel;
import org.springframework.util.CollectionUtils;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Deque;
import java.util.List;
import java.util.Queue;
import java.util.stream.Collectors;

/**
 * @Classname HierarchyRecord
 * @Description TODO
 * @Date 2021/3/31
 * @Author hdw
 */
public interface HierarchyRecord<T extends Comparable<T>> {
    int DEFAULT_CHILDREN_SIZE = 8;

    int level();

    T code();

    T getParentCode();

    void parent(HierarchyRecord<T> parent);

    HierarchyRecord<T> parent();

    List<? extends HierarchyRecord<T>> getChildren();

    void justSetParent(HierarchyRecord<T> parent);

    default boolean hasChild() {
        return !CollectionUtils.isEmpty(getChildren());
    }

    default boolean ancestorOf(HierarchyRecord<T> test) {
        HierarchyRecord<?> p = test.parent();
        while (null != p) {
            if (p == this) {
                return true;
            }
            p = p.parent();
        }
        return false;
    }

    default void removeFromTree() {
        if (null != parent()) {
            parent().getChildren().remove(this);
        }
    }

    /**
     * can implement in different ways
     *
     * @param hierarchyRecords must be sorted(order by result)before invoking this method,
     *                         the implementation is fine for ArrayList, for LinkedList, Iterator shall be used.
     * @param <U>
     * @return maxChildLevel
     */
    static <U extends Comparable<U>> int buildTree(List<? extends HierarchyRecord<U>> hierarchyRecords) {
        if (null == hierarchyRecords || hierarchyRecords.isEmpty()) {
            return 0;
        }
        int index = 0;
        HierarchyRecord<U> parent = hierarchyRecords.get(index);
        Queue<HierarchyRecord<U>> parentQueue = new ArrayDeque<>();
        int size = hierarchyRecords.size();
        U parentCode = parent.code();
        int childLevel = parent.level() + 1;
        while (++index < size) {
            HierarchyRecord<U> record = hierarchyRecords.get(index);
            while (record.level() != childLevel || !parentCode.equals(record.getParentCode())) {
                parent = parentQueue.poll();
                if (null == parent) {
                    throw new IllegalStateException("should not be null, maybe configuration data error");
                }
                parentCode = parent.code();
                childLevel = parent.level() + 1;
            }
            record.parent(parent);
            parentQueue.offer(record);
        }
        return childLevel;
    }

    static <U extends HierarchyRecord<X>, X extends Comparable<X>> List<U> findTop(List<U> hierarchyRecords) {
        if (CollectionUtils.isEmpty(hierarchyRecords)) {
            return Collections.emptyList();
        }
        hierarchyRecords.sort(Comparator.comparing(HierarchyRecord::level));
        int topLevel = hierarchyRecords.get(0).level();
        if (topLevel == ScoreTreeLevel.TREE_ROOT) {
            return Collections.singletonList(hierarchyRecords.get(0));
        }
        List<U> result = hierarchyRecords.stream().filter(record -> record.level() == topLevel).collect(Collectors.toList());
        if (result.size() == hierarchyRecords.size()) {
            return result;
        }
        hierarchyRecords.forEach(record -> {
            if (record.level() == topLevel) {
                return;
            }
            if (result.stream().anyMatch(p -> p.ancestorOf(record))) {
                return;
            }
            result.add(record);
        });
        return result;
    }

    static <U extends HierarchyRecord<X>, X extends Comparable<X>> List<U> flat(U root) {
        if (null == root) {
            return Collections.emptyList();
        }
        if (!root.hasChild()) {
            return Collections.singletonList(root);
        }
        Deque<U> queue = new ArrayDeque<>();
        List<U> result = new ArrayList<>();
        U node = root;
        while (null != node) {
            result.add(node);
            if (node.hasChild()) {
                queue.addAll((List<U>) node.getChildren());
            }
            node = queue.poll();
        }
        return result;
    }
}
