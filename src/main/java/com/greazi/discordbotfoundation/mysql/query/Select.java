package com.greazi.discordbotfoundation.mysql.query;

import com.greazi.discordbotfoundation.mysql.query.join.*;
import com.greazi.discordbotfoundation.mysql.table.ITable;

import java.util.ArrayList;
import java.util.List;

public class Select extends ExecuteQuery<Select> {


	private String[] columns = {"*"};

	private String distinct = null;

	private String order = null;

	private final List<IJoin> joins = new ArrayList<>();

	public Select(final ITable table, final String[] columns) {
		super(table);

		this.columns = columns;
	}

	public Select(final ITable table) {
		this(table, new String[]{"*"});
	}

	/**
	 * Set how the values will be ordered.
	 * Example: .orderBy("id DESC")
	 *
	 * @param order Values order
	 */
	public Select order(final String order) {
		return this.orderBy(order);
	}

	/**
	 * Set how the values will be ordered.
	 * Example: .orderBy("id DESC")
	 *
	 * @param order Values order
	 */
	public Select orderBy(final String order) {
		this.order = order;

		return this;
	}

	/**
	 * Set the columns that you want to fetch.
	 *
	 * @param columns Columns array.
	 * @return Query object.
	 */
	public Select columns(final String[] columns) {
		this.columns = columns;

		return this;
	}

	/**
	 * Set DISTINCT attribute.
	 *
	 * @param column Name of the column that you want to be distinct.
	 */
	public void distinct(final String column) {
		this.distinct = column;
	}

	/**
	 * Join table.
	 *
	 * @param table Table name.
	 * @return IJoin object.
	 */
	public IJoin join(final String table) {
		final IJoin join = new Join(this, table);

		this.joins.add(join);

		return join;
	}

	/**
	 * Inner Join table.
	 *
	 * @param table Table name.
	 * @return IJoin object.
	 */
	public IJoin innerJoin(final String table) {
		final IJoin join = new InnerJoin(this, table);

		this.joins.add(join);

		return join;
	}

	/**
	 * Left Join table.
	 *
	 * @param table Table name.
	 * @return IJoin object.
	 */
	public IJoin leftJoin(final String table) {
		final IJoin join = new LeftJoin(this, table);

		this.joins.add(join);

		return join;
	}

	/**
	 * Right Join table.
	 *
	 * @param table Table name.
	 * @return IJoin object.
	 */
	public IJoin rightJoin(final String table) {
		final IJoin join = new RightJoin(this, table);

		this.joins.add(join);

		return join;
	}

	/**
	 * Full Join table.
	 *
	 * @param table Table name.
	 * @return IJoin object.
	 */
	public IJoin fullJoin(final String table) {
		final IJoin join = new FullJoin(this, table);

		this.joins.add(join);

		return join;
	}

	@Override
	public String toQuery() {
		final StringBuilder query = new StringBuilder("SELECT " + (this.distinct != null ? "DISTINCT " + this.distinct : ""));

		query.append(String.join(", ", this.columns));

		query.append(this.table != null ? " FROM " + this.table : "");

		for (final IJoin join : this.joins) query.append(" ").append(join);

		query.append(this.where != null ? " WHERE " + this.where : "");

		query.append(this.order != null ? " ORDER BY " + this.order : "");

		query.append(this.limit > 0 ? " LIMIT " + this.limit : "");

		return query.toString().trim();
	}
}
