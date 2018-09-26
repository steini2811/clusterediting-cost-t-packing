package de.unmr.bacluster.util;

import java.util.HashSet;
import java.util.Set;

public class SetOperations {

    /**
     * Returns a new set that equals the difference of {@code M} and {@code N}.
     * This is the set with all members of {@code M} that are not in {@code N}.
     *
     * @param M
     * @param N
     * @return {@code M} \ {@code N}
     */
    public static <T> Set<T> getDifference(final Set<T> M, final Set<T> N) {
        final Set<T> resultSet = new HashSet<>(M);
        resultSet.removeAll(N);
        return resultSet;
    }

    /**
     * Returns a new set that equals the intersection of {@code M} and
     * {@code N}. This is the set with all members that are in both {@code M}
     * and {@code N}.
     *
     * @param M
     * @param N
     * @return M ∩ N
     */
    public static <T> Set<T> getIntersection(final Set<T> M, final Set<T> N) {
        final Set<T> resultSet = new HashSet<>(M);
        resultSet.retainAll(N);
        return resultSet;
    }

    /**
     * Returns whether {@code M} is a subset of {@code N}. This means that every
     * member of {@code M} must also be in {@code N} to let this method return
     * {@code true}.
     *
     * @param M
     * @param N
     * @return true if M ⊆ N
     */
    public static <T> boolean isSubset(final Set<T> M, final Set<T> N) {
        final Set<T> resultSet = new HashSet<>(M);
        resultSet.removeAll(N);
        return resultSet.isEmpty();
    }
}
