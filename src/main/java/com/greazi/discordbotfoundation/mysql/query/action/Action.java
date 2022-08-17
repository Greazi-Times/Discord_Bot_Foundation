package com.greazi.discordbotfoundation.mysql.query.action;

import com.greazi.discordbotfoundation.mysql.query.column.Boolean;
import com.greazi.discordbotfoundation.mysql.query.column.Double;
import com.greazi.discordbotfoundation.mysql.query.column.Enum;
import com.greazi.discordbotfoundation.mysql.query.column.Float;
import com.greazi.discordbotfoundation.mysql.query.column.*;

import java.lang.Integer;
import java.lang.String;
import java.util.ArrayList;
import java.util.List;

public abstract class Action implements IAction<IColumn>, IActionColumns {

	protected List<IColumn> columns = new ArrayList<>();

	@Override
    public com.greazi.discordbotfoundation.mysql.query.column.Integer id(final String name) {
		final com.greazi.discordbotfoundation.mysql.query.column.Integer column = this.integer(name);

		column.primary().increment().unsigned();

		return column;
	}

	/**
	 * Create a column with type BIGINT.
	 *
	 * @param name Name of the column.
	 * @return
	 */
	@Override
    public BigInteger bigInteger(final String name) {
		return this.bigInteger(name, 0);
	}

	@Override
    public BigInteger bigInteger(final String name, final Integer length) {
		final BigInteger column = new BigInteger(this, name, length);

		this.columns.add(column);

		return column;
	}

	@Override
    public MediumInteger mediumInteger(final String name) {
		return this.mediumInteger(name, 0);
	}

	@Override
    public MediumInteger mediumInteger(final String name, final Integer length) {
		final MediumInteger column = new MediumInteger(this, name, length);

		this.columns.add(column);

		return column;
	}

	@Override
    public TinyInteger tinyInteger(final String name) {
		return this.tinyInteger(name, 0);
	}

	@Override
    public TinyInteger tinyInteger(final String name, final Integer length) {
		final TinyInteger column = new TinyInteger(this, name, length);

		this.columns.add(column);

		return column;
	}

	@Override
    public SmallInteger smallInteger(final String name) {
		return this.smallInteger(name, 0);
	}

	@Override
    public SmallInteger smallInteger(final String name, final Integer length) {
		final SmallInteger column = new SmallInteger(this, name, length);

		this.columns.add(column);

		return column;
	}

	@Override
    public com.greazi.discordbotfoundation.mysql.query.column.Integer integer(final String name) {
		return this.integer(name, 11);
	}

	@Override
    public com.greazi.discordbotfoundation.mysql.query.column.Integer integer(final String name, final Integer length) {
		final com.greazi.discordbotfoundation.mysql.query.column.Integer column = new com.greazi.discordbotfoundation.mysql.query.column.Integer(this, name, length);

		this.columns.add(column);

		return column;
	}

	@Override
    public Text text(final String name) {
		final Text column = new Text(this, name);

		this.columns.add(column);

		return column;
	}

	@Override
    public MediumText mediumText(final String name) {
		final MediumText column = new MediumText(this, name);

		this.columns.add(column);

		return column;
	}

	@Override
    public Binary binary(final String name) {
		final Binary column = new Binary(this, name);

		this.columns.add(column);

		return column;
	}

	@Override
    public LongBinary longBinary(final String name) {
		final LongBinary column = new LongBinary(this, name);

		this.columns.add(column);

		return column;
	}

	@Override
    public LongText longText(final String name) {
		final LongText column = new LongText(this, name);

		this.columns.add(column);

		return column;
	}

	@Override
    public Boolean booleanColumn(final String name) {
		final Boolean column = new Boolean(this, name);

		this.columns.add(column);

		return column;
	}

	@Override
    public Date date(final String name) {
		final Date column = new Date(this, name);

		this.columns.add(column);

		return column;
	}

	@Override
    public DateTime dateTime(final String name) {
		final DateTime column = new DateTime(this, name);

		this.columns.add(column);

		return column;
	}

	@Override
    public Timestamp timestamp(final String name) {
		final Timestamp column = new Timestamp(this, name);

		this.columns.add(column);

		return column;
	}

	@Override
    public Decimal decimal(final String name, final Integer length, final Integer precision) {
		final Decimal column = new Decimal(this, name, length, precision);

		this.columns.add(column);

		return column;
	}

	@Override
    public Decimal decimal(final String name) {
		return this.decimal(name, 5, 2);
	}

	@Override
    public Float floatColumn(final String name, final Integer length, final Integer precision) {
		final Float column = new Float(this, name, length, precision);

		this.columns.add(column);

		return column;
	}

	@Override
    public Float floatColumn(final String name) {
		return this.floatColumn(name, 11, 6);
	}

	@Override
    public Double doubleColumn(final String name, final Integer length, final Integer precision) {
		final Double column = new Double(this, name, length, precision);

		this.columns.add(column);

		return column;
	}

	@Override
    public Double doubleColumn(final String name) {
		return this.doubleColumn(name, 11, 8);
	}

	@Override
    public com.greazi.discordbotfoundation.mysql.query.column.Integer id() {
		return this.id("id");
	}

	@Override
    public com.greazi.discordbotfoundation.mysql.query.column.String string(final String name) {
		return this.string(name, 255);
	}

	@Override
    public com.greazi.discordbotfoundation.mysql.query.column.String string(final String name, final Integer length) {
		final com.greazi.discordbotfoundation.mysql.query.column.String column = new com.greazi.discordbotfoundation.mysql.query.column.String(this, name, length, false);

		this.columns.add(column);

		return column;
	}

	@Override
    public Enum enumColumn(final String name, final Object[] list) {
		final Enum column = new Enum(this, name, list);

		this.columns.add(column);

		return column;
	}

	@Override
    public Base column(final String name, final String content) {
		final Base column = new Base(this, name, content);

		this.columns.add(column);

		return column;
	}

	@Override
	public List<IColumn> getColumns() {
		return columns;
	}

	@Override
	public String getPrefix() {
		return null;
	}
}
