package com.greazi.discordbotfoundation.mysql.query.action;

import java.util.List;

public interface IAction<T> {
    /**
     * @return Action columns.
     */
    List<T> getColumns();

    /**
     * @return Action prefix.
     */
    java.lang.String getPrefix();
}
