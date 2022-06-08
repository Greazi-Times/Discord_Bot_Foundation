package com.greazi.discordbotfoundation.mysql.query;

import com.greazi.discordbotfoundation.mysql.table.ITable;
import com.greazi.discordbotfoundation.mysql.query.join.*;

import java.util.ArrayList;
import java.util.List;

public class Select extends ExecuteQuery<Select> {


    private String[] columns = {"*"};

    private String distinct = null;

    private String order = null;

    private final List<com.greazi.discordbotfoundation.mysql.query.join.IJoin> joins = new ArrayList<>();

    public Select(ITable table, String[] columns) {
        super(table);

        this.columns = columns;
    }

    public Select(ITable table) {
        this(table, new String[]{"*"});
    }

    /**
     * Set how the values will be ordered.
     * Example: .orderBy("id DESC")
     * @param order Values order
     */
    public Select order(String order) {
        return this.orderBy(order);
    }

    /**
     * Set how the values will be ordered.
     * Example: .orderBy("id DESC")
     * @param order Values order
     */
    public Select orderBy(String order) {
        this.order = order;

        return this;
    }

    /**
     * Set the columns that you want to fetch.
     * @param columns Columns array.
     * @return Query object.
     */
    public Select columns(String[] columns) {
        this.columns = columns;

        return this;
    }

    /**
     * Set DISTINCT attribute.
     * @param column Name of the column that you want to be distinct.
     */
    public void distinct(String column) {
        this.distinct = column;
    }

    /**
     * Join table.
     * @param table Table name.
     * @return IJoin object.
     */
    public com.greazi.discordbotfoundation.mysql.query.join.IJoin join(String table) {
        com.greazi.discordbotfoundation.mysql.query.join.IJoin join = new com.greazi.discordbotfoundation.mysql.query.join.Join(this, table);

        this.joins.add(join);

        return join;
    }

    /**
     * Inner Join table.
     * @param table Table name.
     * @return IJoin object.
     */
    public com.greazi.discordbotfoundation.mysql.query.join.IJoin innerJoin(String table) {
        com.greazi.discordbotfoundation.mysql.query.join.IJoin join = new com.greazi.discordbotfoundation.mysql.query.join.InnerJoin(this, table);

        this.joins.add(join);

        return join;
    }

    /**
     * Left Join table.
     * @param table Table name.
     * @return IJoin object.
     */
    public com.greazi.discordbotfoundation.mysql.query.join.IJoin leftJoin(String table) {
        com.greazi.discordbotfoundation.mysql.query.join.IJoin join = new com.greazi.discordbotfoundation.mysql.query.join.LeftJoin(this, table);

        this.joins.add(join);

        return join;
    }

    /**
     * Right Join table.
     * @param table Table name.
     * @return IJoin object.
     */
    public com.greazi.discordbotfoundation.mysql.query.join.IJoin rightJoin(String table) {
        com.greazi.discordbotfoundation.mysql.query.join.IJoin join = new com.greazi.discordbotfoundation.mysql.query.join.RightJoin(this, table);

        this.joins.add(join);

        return join;
    }

    /**
     * Full Join table.
     * @param table Table name.
     * @return IJoin object.
     */
    public com.greazi.discordbotfoundation.mysql.query.join.IJoin fullJoin(String table) {
        com.greazi.discordbotfoundation.mysql.query.join.IJoin join = new com.greazi.discordbotfoundation.mysql.query.join.FullJoin(this, table);

        this.joins.add(join);

        return join;
    }

    @Override
    public String toQuery() {
        StringBuilder query = new StringBuilder("SELECT " + (this.distinct != null ? "DISTINCT " + this.distinct : ""));

        query.append(String.join(", ", this.columns));

        query.append(this.table != null ? " FROM " + this.table : "");

        for (com.greazi.discordbotfoundation.mysql.query.join.IJoin join: this.joins) query.append(" ").append(join);

        query.append(this.where != null ? " WHERE " + this.where : "");

        query.append(this.order != null ? " ORDER BY " + this.order : "");

        query.append(this.limit > 0 ? " LIMIT " + this.limit : "");

        return query.toString().trim();
    }
}
